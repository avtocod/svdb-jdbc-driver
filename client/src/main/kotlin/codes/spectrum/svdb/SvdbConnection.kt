package codes.spectrum.svdb

import codes.spectrum.commons.ExceptionGroup
import codes.spectrum.svdb.model.v1.*
import io.grpc.ManagedChannel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.Closeable
import java.sql.SQLTimeoutException
import java.time.Instant
import kotlin.system.measureTimeMillis


class SvdbConnection(
    private val connection: ManagedChannel,
    private val service: SvdbServiceGrpcKt.SvdbServiceCoroutineStub,
    private val sessionUidProvider: IUidsProvider,
    options: NativeDriver.SvdbDriverOptions,
) : Closeable {
    private val timeout = options.timeout

    private fun startNewCursor(startQueryTime: Instant): SvdbCursor {
        return SvdbCursor(
            service = service,
            uidProvider = sessionUidProvider,
            options = SvdbCursor.SvdbCursorOptions(timeout, startQueryTime)
        )
    }

    suspend fun executeQuery(
        queryText: String,
        params: Map<String, Any> = mapOf(),
        timeout: Long = 0,
    ): Result<SvdbCursor> {
        return try {
            val startTime = Instant.now()
            if (timeout <= 0) return executeWithRetries(queryText = queryText, params = params, startTime)
            withTimeout(timeMillis = timeout * 1000) {
                val result: Result<SvdbCursor>
                measureTimeMillis {
                    result = executeWithRetries(queryText = queryText, params = params, startTime)
                }
                result
            }
        } catch (tce: TimeoutCancellationException) {
            throw SQLTimeoutException(tce.message, tce)
        }
    }

    private suspend fun executeWithRetries(
        queryText: String, params: Map<String, Any> = mapOf(), startTime: Instant
    ): Result<SvdbCursor> {
        // если получили 502 Bad Gateway или RST_STREAM closed stream,
        // то уходим на политику ретрая через 100 мс, 300 мс, 1 с, 3 с, 10 с
        var result: Result<SvdbCursor> = Result.failure(
            ExceptionGroup(
                "failed attept to executeQuery. reasons inside group.",
                exceptions = mutableListOf()
            )
        )
        for (it in retryIntervals) {
            delay(it)
            try {
                runBlocking { result = internalExecuteQuery(queryText, params, startTime) }
                if ((result.isSuccess)
                    || (result.isFailure && !(result.exceptionOrNull()!!.message.toString()
                        .contains(RST_STREAM_ERROR_TEXT)
                            || (result.exceptionOrNull()!!.message.toString().contains(CODE_502))))
                ) {
                    break
                }
            } catch (e: Exception) {
                if (e.message.toString().contains(RST_STREAM_ERROR_TEXT) || e.message.toString().contains(CODE_502)) {
                    (result.exceptionOrNull() as ExceptionGroup).addFirst(e)
                } else {
                    throw e
                }
            }
        }
        return result
    }

    private suspend fun internalExecuteQuery(
        queryText: String,
        params: Map<String, Any> = mapOf(),
        startTime: Instant,
    ): Result<SvdbCursor> {
        var normalizedSql = queryText
        if (queryText.isBlank()) {
            return Result.failure(Exception("Empty Query"))
        }
        if (!normalizedSql.contains(DRIVER_TAG_MARKER)) {
            normalizedSql += NativeDriverQuerySuffix
        }
        val result = runCatching {
            val cursor = startNewCursor(startTime)
            cursor.executeQuery(normalizedSql, params)
            cursor
        }
        if (result.isFailure) {
            return result
        }
        val svdbCursor = result.getOrThrow()
        if (svdbCursor.getResult().state.type == "ERROR") {
            return Result.failure(Exception("error during execute query: ${svdbCursor.getResult().state.message}"))
        }
        return result
    }

    suspend fun executeList(
        queryText: String,
        params: Map<String, Any> = mapOf(),
    ): List<RecordOuterClass.Record> {
        return try {
            val startTime = Instant.now()
            if (timeout > 0) {
                withTimeout(timeMillis = timeout * 1000) {
                    executeListWithRetries(queryText = queryText, params = params, startTime)
                }
            } else {
                executeListWithRetries(queryText = queryText, params = params, startTime)
            }
        } catch (tce: TimeoutCancellationException) {
            throw tce
        }
    }

    private suspend fun executeListWithRetries(
        queryText: String,
        params: Map<String, Any> = mapOf(),
        startTime: Instant,
    ): List<RecordOuterClass.Record> {
        // если получили 502 Bad Gateway или RST_STREAM closed stream,
        // то уходим на политику ретрая через 0, 100 мс, 300 мс, 1 с, 3 с, 10 с
        lateinit var lastError: Throwable
        for (it in retryIntervals) {
            delay(it)
            try {
                return internalExecuteList(queryText, params, startTime)
            } catch (e: Throwable) {
                lastError = e
                if (!isRetryAbleError(e)) {
                    throw e
                }
            }
        }
        // к этому времени точно произошел return или есть какая-то ошибка
        throw ExecuteListError(lastError)
    }

    class ExecuteListError(cause: Throwable) : Exception("general error, not covered with retry", cause)

    private fun isRetryAbleError(e: Throwable): Boolean {
        return e.message.toString().contains(RST_STREAM_ERROR_TEXT) || e.message.toString()
            .contains(CODE_502)
    }

    private suspend fun internalExecuteList(
        queryText: String,
        params: Map<String, Any>,
        startTime: Instant,
    ): List<RecordOuterClass.Record> {
        val cursorResult = runCatching {
            val cursor = startNewCursor(startTime)
            cursor.executeQuery(queryText, params)
            cursor
        }
        if (cursorResult.isFailure) {
            throw cursorResult.exceptionOrNull()!!
        }
        val data = mutableListOf<RecordOuterClass.Record>()
        val cursor = cursorResult.getOrThrow()
        while (true) {
            val fetchResult = cursor.fetch()
            if (fetchResult.state.type == "ERROR") {
                error("${fetchResult.state.code} ${fetchResult.state.message}")
            }
            data.addAll(fetchResult.recordsList)
            if (fetchResult.recordsList.size == 0 || fetchResult.state.type == "EOF") {
                break
            }
        }
        return data
    }

    private suspend fun cancel(cancelOptions: CancelOptions): StateOuterClass.State {
        val cancelResult = kotlin.runCatching {
            service.cancel(request = cancelOptions {
                sessionUid = cancelOptions.session_uid
                cursorUid = cancelOptions.cursor_uid
            })
        }
        if (cancelResult.isFailure) {
            return state {
                code = SvdbStateCodes.SERVER_ERROR.intValue
                type = "ERROR"
                message = cancelResult.exceptionOrNull()!!.toString()
            }
        }
        return cancelResult.getOrNull()!!
    }

    override fun close() {
        runCatching {
            runBlocking { cancel(CancelOptions(session_uid = sessionUidProvider.getSessionUid())) }
        }
        runCatching {
            connection.shutdownNow()
        }
    }

    companion object {
        // текст часто содержится в исключении, если произошел разрыв соединения
        val RST_STREAM_ERROR_TEXT = "RST_STREAM closed stream"

        // текст содержащийся в 502 ошибке с nginx
        val CODE_502 = "HTTP status code 502"

        // определяет интервал между попытками подключится
        val retryIntervals = mutableListOf(0, 100L, 300L, 1000L, 3000L, 10000L)

        private val DRIVER_TAG_MARKER = Regex("\\s+@D\\s+")
    }
}
