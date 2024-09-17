package codes.spectrum.svdb

import codes.spectrum.svdb.jdbc.SvdbJdbcParameter
import codes.spectrum.svdb.jdbc.tryNewByteValue
import codes.spectrum.svdb.model.v1.*
import io.grpc.Metadata
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.temporal.ChronoUnit

class SvdbCursor(
    private val service: SvdbServiceGrpcKt.SvdbServiceCoroutineStub,
    private val uidProvider: IUidsProvider,
    private val preparedData: List<RecordOuterClass.Record>? = null,
    private val preparedColumns: List<ColumnOuterClass.Column>? = null,
    options: SvdbCursorOptions,
) : ISvdbCursor {

    private val deadline = options.deadline
    private var sessionUid: String = ""
    private lateinit var queryResult: Queryresult.QueryResult
    override fun getSessionUid(): String {
        return sessionUid
    }

    init {
        sessionUid = uidProvider.getSessionUid()
    }

    private var cursorUid: String = ""
    override fun getCursorUid(): String {
        return cursorUid
    }

    override fun close() {
        runCatching {
            runBlocking {
                service.cancel(
                    cancelOptions {
                        sessionUid = getSessionUid()
                        cursorUid = getCursorUid()
                    },
                )
            }
        }
    }

    override fun cancel() {
        runBlocking {
            runCatching {
                service.cancel(
                    request = cancelOptions {
                        sessionUid = getSessionUid()
                        cursorUid = getCursorUid()
                    },
                    Metadata(),
                )
            }
        }
    }

    fun getResult(): Queryresult.QueryResult {
        return queryResult
    }

    var shouldReturnFromQueryResult = false
    var hasEof = false

    // TODO - доделать тему со сбором Warnings в очередь и ее отдачу JDBC под SqlWarning
    // val warningQueue = LinkedQueue<WarningOuterClass.Warning>()

    override suspend fun fetch(): Queryresult.QueryResult {
        if (isTimeoutExpired()) {
            cancel()
            return timeoutResult
        }

        if (preparedData != null) {
            return queryResult {
                if (preparedColumns != null) {
                    columns.addAll(preparedColumns)
                }
                records.addAll(preparedData)
                state = state { type = "EOF" }
            }
        }

        // обвязка на случай если прямо в запросе были возвращены данные синхронно
        // по новой схеме
        if (shouldReturnFromQueryResult) {
            shouldReturnFromQueryResult = false
            val stateType = if (getResult().state.type == "EOF") "EOF" else "OK"
            return queryResult {
                val res = getResult()


                if (res.columnsList.isNotEmpty()) {
                    columns.addAll(res.columnsList)
                }

                for (r in res.recordsList) {
                    if (r.fieldsList.isNotEmpty()) {
                        records.add(r)
                    }
                }

                warnings.addAll(res.warningsList)
                state = state { type = stateType }
            }
        }

        // если при некорректном использовании будет все же вызван fetch при EOF уже в запросе
        if (hasEof) {
            return queryResult {
                val res = getResult()
                if (res.columnsList.isNotEmpty()) {
                    columns.addAll(res.columnsList)
                }
                state = state { type = "EOF" }
            }
        }

        val metadata = Metadata()

        try {
            val result = service.fetch(
                fetchOptions {
                    cursorUid = getCursorUid()
                },
                metadata,
            )
            if (result.state.type == "EOF") {
                hasEof = true
            }
            return result
        } catch (e: StatusException) {
            error(e)
        }
    }

    private val timeoutResult by lazy {
        queryResult {
            state = state {
                code = SvdbStateCodes.TIMEOUT.intValue
                message = "Timeout expired"
                type = "ERROR"
            }
        }
    }

    suspend fun executeQuery(queryText: String, params: Map<String, Any>) {
        if (isTimeoutExpired()) {
            cancel()
            queryResult = timeoutResult
            return
        }

        queryResult = service.query(
            queryOptions {
                text = queryText
                argHeads.addAll(
                    params.map {
                        val param = it.value as SvdbJdbcParameter
                        column {
                            code = it.key
                            dataType = param.dataType
                        }
                    },
                )
                argValues.addAll(
                    params.map {
                        val param = it.value as SvdbJdbcParameter
                        tryNewByteValue(param.value, param.dataType)
                    },
                )
            },
            Metadata(),
        )

        val sessionUid = uidProvider.getSessionUid()
        check(sessionUid.isNotBlank()) { "not found sessionuid in response metadata" }
        this.sessionUid = sessionUid

        val cursorUid = uidProvider.getLastQueryCursorUid()
        check(cursorUid.isNotBlank()) { "not found cursoruid in response metadata" }
        this.cursorUid = cursorUid

        check(queryResult.state.type != "ERROR") {
            "error during executeQuery:\n${queryResult.state.type} ${queryResult.state.message}"
        }

        if (queryResult.recordsCount > 0 || queryResult.recordsCount > 0 || queryResult.columnsCount > 0) {
            shouldReturnFromQueryResult = true
        }

        if (queryResult.state.type == "EOF") {
            hasEof = true
        }
    }

    private fun isTimeoutExpired(): Boolean = Instant.now().isAfter(deadline)

    data class SvdbCursorOptions(
        val timeout: Long,
        val startQueryTime: Instant,
    ) {
        val deadline: Instant = if (timeout <= 0) Instant.MAX else startQueryTime.plus(timeout, ChronoUnit.SECONDS)
    }
}
