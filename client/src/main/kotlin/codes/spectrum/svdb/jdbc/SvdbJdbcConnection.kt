package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.*
import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.sql.*
import java.util.*
import java.util.concurrent.Executor
import kotlin.time.Duration.Companion.seconds


class SvdbJdbcConnection(
    private val svdbConnection: SvdbConnection,
    private var database: String = DEFAULT_DATABASE,
    private val username: String = DEFAULT_USERNAME,
) : Connection {
    private var internalSchema: String = "sys"
    private val statements: MutableList<Statement> = mutableListOf()

    /**
     * Кеш prepare запросов key - sql запрос, value - uid
     */
    private val cache: Cache<String, String> by lazy {
        CacheBuilder.newBuilder().maximumSize(CACHE_MAXIMUM_SIZE).build()
    }

    internal val svdbJdbcSysData: SvdbJdbcSysData by UpdatableLazyDelegate(REFRESH_TIME_MS) {
        runBlocking {
            val tablesMap: List<Map<String, String>> = svdbConnection.executeList("select * from sys.tables")
                .map { row -> row.toStringMap() }

            val catalogs: Map<String, List<String>> = tablesMap.map {
                (it["catalog"] ?: "") to (it["schema"] ?: "")
            }.groupBy({ it.first }) { it.second }
                .mapValues { it.value.distinct() }

            val catalogsNames = tablesMap.map { it["catalog"] ?: "" }.distinct()

            val catalogList: List<SvdbJdbcCatalog> =
                catalogsNames.map { name ->
                    SvdbJdbcCatalog(
                        name = name,
                        schemas = catalogs.getOrDefault(name, listOf())
                            .map { schema ->
                                Schema(schema, tablesMap
                                    .filter { it["schema"] == schema }
                                    .map { Table(it["table_name"] ?: "", it["comment"] ?: "") }
                                )
                            }
                    )
                }

            val fieldsMap = svdbConnection.executeList("select * from sys.fields").map { it.toStringMap() }
            val fields = fieldsMap.map {
                SvdbJdbcField(
                    catalog = it["catalog"] ?: "",
                    schema = it["schema"] ?: "",
                    table = it["table_name"] ?: "",
                    name = it["field_name"] ?: "",
                    type = DataType.valueOf(it["type"] ?: "UNDEFINED"),
                    description = it["comment"] ?: "",
                    position = it["idx"]?.toInt()?.plus(1) ?: -1,
                    isNull = it["is_null"]?.toBoolean() ?: false
                )
            }

            val version =
                svdbConnection.executeList("select app_version from sys.nodes limit 1 with local_node")
                    .first().atOf<String>(0)

            SvdbJdbcSysData(catalogList, fields, version)
        }
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        TODO("method name ${retriveFunName()} called with parameters $iface")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        TODO("method name ${retriveFunName()} called with parameters $iface")
    }

    var _isClosed = false
        private set

    override fun close() {
        if (!_isClosed) {
            statements.forEach { it.close() }
            statements.clear()
            runCatching { svdbConnection.close() }
            _isClosed = true
        }
    }


    override fun createStatement(): Statement {
        checkClosed()
        return SvdbJdbcStatement(svdbConnection).also { statements.add(it) }
    }

    private fun checkClosed() {
        if (_isClosed) throw SQLException("Connection has been closed", SvdbJdbcState.CONNECTION_DOES_NOT_EXIST.code)
    }

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement {
        TODO("method name ${retriveFunName()} called with parameters $resultSetType $resultSetConcurrency")
    }

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement {
        TODO("method name ${retriveFunName()} called with parameters $resultSetType $resultSetConcurrency $resultSetHoldability")
    }

    override fun prepareStatement(sql: String): PreparedStatement = runBlocking {
        checkClosed()
        SvdbJdbcPreparedStatement(connection = svdbConnection, sql = sql, cache)
    }

    override fun prepareStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $resultSetType $resultSetConcurrency")
    }

    override fun prepareStatement(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): PreparedStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $resultSetType $resultSetConcurrency $resultSetHoldability")
    }

    override fun prepareStatement(sql: String?, autoGeneratedKeys: Int): PreparedStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $autoGeneratedKeys")
    }

    override fun prepareStatement(sql: String?, columnIndexes: IntArray?): PreparedStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $columnIndexes")
    }

    override fun prepareStatement(sql: String?, columnNames: Array<out String>?): PreparedStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $columnNames")
    }

    override fun prepareCall(sql: String?): CallableStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql")
    }

    override fun prepareCall(sql: String?, resultSetType: Int, resultSetConcurrency: Int): CallableStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $resultSetType $resultSetConcurrency")
    }

    override fun prepareCall(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): CallableStatement {
        checkClosed()
        TODO("method name ${retriveFunName()} called with parameters $sql $resultSetType $resultSetConcurrency $resultSetHoldability")
    }

    override fun nativeSQL(sql: String?): String {
        return sql ?: ""
    }

    override fun setAutoCommit(autoCommit: Boolean) = Unit

    override fun getAutoCommit(): Boolean = true

    override fun commit() = Unit

    override fun rollback() = Unit

    override fun rollback(savepoint: Savepoint?) = Unit

    override fun isClosed(): Boolean {
        return _isClosed
    }

    override fun getMetaData(): DatabaseMetaData {
        checkClosed()
        return SvdbDatabaseMetaData(svdbJdbcSysData, username)
    }

    override fun setReadOnly(readOnly: Boolean) = Unit

    override fun isReadOnly(): Boolean = true

    override fun setCatalog(catalog: String) {
        checkClosed()
        database = catalog
    }

    override fun getCatalog(): String {
        checkClosed()
        return database
    }

    override fun setTransactionIsolation(level: Int) = Unit

    override fun getTransactionIsolation(): Int = Connection.TRANSACTION_NONE

    override fun getWarnings(): SQLWarning? {
        checkClosed()
        return null
    }

    override fun clearWarnings() = Unit

    override fun getTypeMap(): MutableMap<String, Class<*>>  = TODO("method name ${retriveFunName()} called")

    override fun setTypeMap(map: MutableMap<String, Class<*>>?) {
        TODO("method name ${retriveFunName()} called with parameters $map")
    }

    override fun setHoldability(holdability: Int) {
        TODO("method name ${retriveFunName()} called with parameters $holdability")
    }

    override fun getHoldability(): Int  = TODO("method name ${retriveFunName()} called")

    override fun setSavepoint(): Savepoint  = TODO("method name ${retriveFunName()} called")

    override fun setSavepoint(name: String?): Savepoint {
        TODO("method name ${retriveFunName()} called with parameters $name")
    }

    override fun releaseSavepoint(savepoint: Savepoint?) {
        TODO("method name ${retriveFunName()} called with parameters $savepoint")
    }

    override fun createClob(): Clob  = TODO("method name ${retriveFunName()} called")

    override fun createBlob(): Blob  = TODO("method name ${retriveFunName()} called")

    override fun createNClob(): NClob  = TODO("method name ${retriveFunName()} called")

    override fun createSQLXML(): SQLXML  = TODO("method name ${retriveFunName()} called")

    override fun isValid(timeout: Int): Boolean {
        if (timeout < 0) {
            throw SQLException("timeout should be greater or equal to 0")
        }

        if (_isClosed) {
            return true
        }

        val isValidFunc = suspend { svdbConnection.executeQuery("select 1").isSuccess }
        return runBlocking {
            if (timeout == 0) isValidFunc()
            else withTimeoutOrNull(timeout.seconds) { isValidFunc() } ?: false
        }
    }

    override fun setClientInfo(name: String?, value: String?) {
        TODO("method name ${retriveFunName()} called with parameters $name $value")
    }

    override fun setClientInfo(properties: Properties?) {
        TODO("method name ${retriveFunName()} called with parameters $properties")
    }

    override fun getClientInfo(name: String?): String {
        TODO("method name ${retriveFunName()} called with parameters $name")
    }

    override fun getClientInfo(): Properties  = TODO("method name ${retriveFunName()} called")

    override fun createArrayOf(typeName: String?, elements: Array<Any?>): java.sql.Array {
        return SvdbJdbcSqlArray(elements)
    }

    override fun createStruct(typeName: String?, attributes: Array<out Any>?): Struct {
        TODO("method name ${retriveFunName()} called with parameters $typeName $attributes")
    }

    override fun setSchema(schema: String) {
        internalSchema = schema
    }

    override fun getSchema(): String {
        return internalSchema
    }

    override fun abort(executor: Executor?) {
        executor?.runCatching { close() }
    }

    private var internalNetworkTimeOut: Int = 10000

    override fun setNetworkTimeout(executor: Executor?, milliseconds: Int) {
        checkClosed()
        internalNetworkTimeOut = milliseconds
    }

    override fun getNetworkTimeout(): Int {
        return internalNetworkTimeOut
    }

    companion object {
        private const val DEFAULT_DATABASE = "data"

        private const val DEFAULT_USERNAME = "default"

        private const val REFRESH_TIME_MS = 5 * 1000L

        private const val CACHE_MAXIMUM_SIZE = 1000L
    }
}
