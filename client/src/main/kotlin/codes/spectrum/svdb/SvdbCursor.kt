package codes.spectrum.svdb

import codes.spectrum.svdb.model.v1.*
import codes.spectrum.svdb.model.v1.QueryOptionsKt.arg
import codes.spectrum.svdb.model.v1.ValueKt.arr
import codes.spectrum.svdb.model.v1.ValueOuterClass.Value
import io.grpc.Metadata
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import codes.spectrum.svdb.jdbc.SvdbJdbcParameter
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class SvdbCursor(
    private val service: SvdbServiceGrpcKt.SvdbServiceCoroutineStub,
    private val uidProvider: IUidsProvider,
    private val preparedDataMode: Queryresult.DataMode? = null,
    private val preparedData: List<RecordOuterClass.Record>? = null,
    private val preparedByteData: List<ByteRecordOuterClass.ByteRecord>? = null,
    private val preparedColumns: List<ColumnOuterClass.Column>? = null,
    private val options: SvdbCursorOptions,
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

        // обвязка для selectList
        if (preparedData != null) {
            return queryResult {
                dataMode = preparedDataMode ?: Queryresult.DataMode.V1
                records.addAll(preparedData)
                state = state { type = "EOF" }
            }
        }

        // режим работы v2
        if (preparedByteData != null) {
            return queryResult {
                dataMode = preparedDataMode ?: Queryresult.DataMode.V1
                if (preparedColumns != null) {
                    columns.addAll(preparedColumns)
                }
                byteRecords.addAll(preparedByteData)
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

                dataMode = res.dataMode

                if (res.dataMode == Queryresult.DataMode.V2) {

                    //TODO: это костыль, чтобы корректно обработать ошибку неопределенного типа на стороне клиента S-22761
                    val invalidColumns = res.columnsList.filter {
                        it.dataType== ColumnOuterClass.DataType.AUTO || it.dataType == ColumnOuterClass.DataType.UNRECOGNIZED
                    }
                    if (invalidColumns.isNotEmpty()){
                        return queryResult {
                            state = state { type = "ERROR"; this.message = "undefined types for fields: [${invalidColumns.joinToString()}]" }
                        }
                    }


                    if (res.columnsList.isNotEmpty()) {
                        columns.addAll(res.columnsList)
                    }

                    for (r in res.byteRecordsList) {
                        if (r.fieldsList.isNotEmpty()) {
                            byteRecords.add(r)
                        }
                    }
                } else {
                    // обходим проблему вывода пустых записей с 0 или только с варнингами
                    for (r in res.recordsList) {
                        if (r.fieldsList.isNotEmpty()) {
                            records.add(r)
                        }
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
                    dataMode = Queryresult.DataMode.V2
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
                args.addAll(
                    params.map {
                        val param = it.value as SvdbJdbcParameter
                        arg {
                            name = it.key
                            dataType = param.dataType
                            value = param.value.toProtobufValue()
                        }
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

        if (queryResult.recordsCount > 0 || queryResult.byteRecordsCount > 0 || queryResult.columnsCount > 0) {
            shouldReturnFromQueryResult = true
        }

        if (queryResult.state.type == "EOF") {
            hasEof = true
        }
    }

    private fun isTimeoutExpired(): Boolean = Instant.now().isAfter(deadline)

    private fun Any.toProtobufValue(): Value {
        val value = this
        return when (this) {
            is String -> value { str = value as String }
            is Int -> value { i32 = value as Int }
            is Long -> value { i64 = value as Long }
            is Float -> value { f64 = (value as Float).toDouble() }
            is Double -> value { f64 = value as Double }
            is Boolean -> value { bit = value as Boolean }
            is BigDecimal -> value { str = (value as BigDecimal).toPlainString() }
            is LocalDate -> value { str = (value as LocalDate).toString() }
            is Instant -> value { i64 = (value as Instant).toEpochMilli() }
            is SvdbNull -> value { /* пустое значение это null */ }
            is Array<*> -> value {
                arr = arr { arr.addAll((value as Array<*>).map { it?.toProtobufValue() ?: value {} }) }
            }

            else -> throw UnsupportedOperationException("param type ${this::class.java} is not supported")
        }
    }

    data class SvdbCursorOptions(
        val timeout: Long,
        val startQueryTime: Instant,
    ) {
        val deadline: Instant = if (timeout <= 0) Instant.MAX else startQueryTime.plus(timeout, ChronoUnit.SECONDS)
    }
}
