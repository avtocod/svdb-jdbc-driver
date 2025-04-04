package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.ISvdbCursor
import codes.spectrum.svdb.SvdbConnection
import codes.spectrum.svdb.SvdbConnection.Companion.DRIVER_TAG_MARKER
import codes.spectrum.svdb.SvdbNull
import codes.spectrum.svdb.model.v1.ColumnOuterClass
import com.google.common.cache.Cache
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Date
import java.time.Instant
import java.util.*

class SvdbJdbcPreparedStatement(
    private val connection: SvdbConnection,
    private val sql: String,
    private val cache: Cache<String, String>,
) : PreparedStatement {
    private var resultSet: ResultSet? = null
    private var cursor: ISvdbCursor? = null
    private val parameters: MutableMap<Int, SvdbJdbcParameter> = TreeMap()
    private val uid = resolveUid()

    private fun resolveUid(): String = runBlocking {
        cache.getIfPresent(sql) ?: executePrepare()
    }

    private suspend fun executePrepare(): String {
        val cursor = connection.executeQuery("$PREPARE_PREFIX $sql").getOrThrow()
        return SvdbJdbcResultSet(cursor).let {
            it.next()
            it.getString(PREPARED_QUERY_UID_FIELD_NAME) ?: error("Cannot get uid for prepare query: $sql")
        }.also {
            cache.put(sql, it)
        }
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T = TODO("method name ${retriveFunName()} called")

    override fun isWrapperFor(iface: Class<*>?): Boolean = TODO("method name ${retriveFunName()} called")

    override fun close() {
        cursor?.close()
        resultSet?.close()
    }

    override fun executeQuery(): ResultSet = runBlocking {
        ensureIsOpen()
        runWrappingSqlException(DATA_EXCEPTION) {
            val params = parameters.mapKeys { it.key.toString() }
            var result = connection.executeQuery(buildExecuteSql(uid), params)
            if (result.exceptionOrNull()?.message?.contains(SVDB_ERROR_CODE_NOT_FOUND_UID) == true) {
                val uid = executePrepare()
                result = connection.executeQuery(buildExecuteSql(uid), params)
            }
            cursor = result.getOrThrow()
            return@runBlocking SvdbJdbcResultSet(cursor!!)
        }
    }

    private fun buildExecuteSql(uid: String): String {
        var normalizedSql = "$EXECUTE_PREFIX '$uid'"
        if (DRIVER_TAG_MARKER !in normalizedSql) {
            normalizedSql += JdbcDriverQuerySuffix
        }
        return normalizedSql
    }

    private fun ensureIsOpen() {
        if (isClosed) throw SQLException("PrepareStatement has been closed", SvdbJdbcState.OBJECT_NOT_IN_STATE.code)
    }

    override fun executeQuery(sql: String?): ResultSet {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun executeUpdate(): Int = TODO("method name ${retriveFunName()} called")

    override fun executeUpdate(sql: String?): Int {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun executeUpdate(sql: String?, autoGeneratedKeys: Int): Int {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun executeUpdate(sql: String?, columnIndexes: IntArray?): Int {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun executeUpdate(sql: String?, columnNames: Array<out String>?): Int {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun getMaxFieldSize(): Int = TODO("method name ${retriveFunName()} called")

    override fun setMaxFieldSize(max: Int) = TODO("method name ${retriveFunName()} called")

    override fun getMaxRows(): Int = TODO("method name ${retriveFunName()} called")

    override fun setMaxRows(max: Int) = TODO("method name ${retriveFunName()} called")

    override fun setEscapeProcessing(enable: Boolean) = TODO("method name ${retriveFunName()} called")

    override fun getQueryTimeout(): Int = TODO("method name ${retriveFunName()} called")

    override fun setQueryTimeout(seconds: Int) = TODO("method name ${retriveFunName()} called")

    override fun cancel() {
        ensureIsOpen()
        cursor?.cancel()
    }

    override fun getWarnings(): SQLWarning = TODO("method name ${retriveFunName()} called")

    override fun clearWarnings() = TODO("method name ${retriveFunName()} called")

    override fun setCursorName(name: String?) = TODO("method name ${retriveFunName()} called")

    override fun execute(): Boolean {
        ensureIsOpen()
        resultSet = executeQuery()
        return true
    }

    override fun execute(sql: String?): Boolean {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun execute(sql: String?, autoGeneratedKeys: Int): Boolean {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun execute(sql: String?, columnIndexes: IntArray?): Boolean {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun execute(sql: String?, columnNames: Array<out String>?): Boolean {
        throw UnsupportedOperationException("by jdbc contract")
    }

    override fun getResultSet(): ResultSet {
        ensureIsOpen()
        return resultSet ?: throw SQLException("result set is not initialized")
    }

    override fun getUpdateCount(): Int = TODO("method name ${retriveFunName()} called")

    override fun getMoreResults(): Boolean  = TODO("method name ${retriveFunName()} called")


    override fun getMoreResults(current: Int): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun setFetchDirection(direction: Int)  = TODO("method name ${retriveFunName()} called")

    override fun getFetchDirection(): Int  = TODO("method name ${retriveFunName()} called")

    override fun setFetchSize(rows: Int)  = TODO("method name ${retriveFunName()} called")

    override fun getFetchSize(): Int = TODO("method name ${retriveFunName()} called")

    override fun getResultSetConcurrency(): Int  = TODO("method name ${retriveFunName()} called")

    override fun getResultSetType(): Int  = TODO("method name ${retriveFunName()} called")

    override fun addBatch()  = TODO("method name ${retriveFunName()} called")

    override fun addBatch(sql: String?)  = TODO("method name ${retriveFunName()} called")

    override fun clearBatch()  = TODO("method name ${retriveFunName()} called")

    override fun executeBatch(): IntArray  = TODO("method name ${retriveFunName()} called")

    override fun getConnection(): Connection  = TODO("method name ${retriveFunName()} called")

    override fun getGeneratedKeys(): ResultSet  = TODO("method name ${retriveFunName()} called")

    override fun getResultSetHoldability(): Int  = TODO("method name ${retriveFunName()} called")

    override fun isClosed(): Boolean {
        return resultSet?.isClosed ?: false
    }

    override fun setPoolable(poolable: Boolean)  = TODO("method name ${retriveFunName()} called")

    override fun isPoolable(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun closeOnCompletion()  = TODO("method name ${retriveFunName()} called")

    override fun isCloseOnCompletion(): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun setNull(parameterIndex: Int, sqlType: Int) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, SvdbNull)
    }

    override fun setNull(parameterIndex: Int, sqlType: Int, typeName: String?) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, SvdbNull)
    }

    override fun setBoolean(parameterIndex: Int, x: Boolean) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.BOOL, x)
    }

    override fun setByte(parameterIndex: Int, x: Byte) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
    }

    override fun setShort(parameterIndex: Int, x: Short) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
    }

    override fun setInt(parameterIndex: Int, x: Int) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
    }

    override fun setLong(parameterIndex: Int, x: Long) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
    }

    override fun setFloat(parameterIndex: Int, x: Float) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, x)
    }

    override fun setDouble(parameterIndex: Int, x: Double) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, x)
    }

    override fun setBigDecimal(parameterIndex: Int, x: BigDecimal) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.DECIMAL, x)
    }

    override fun setString(parameterIndex: Int, x: String) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, x)
    }

    override fun setBytes(parameterIndex: Int, x: ByteArray)  = TODO("method name ${retriveFunName()} called")

    override fun setDate(parameterIndex: Int, x: Date) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.toLocalDate())
    }

    override fun setDate(parameterIndex: Int, x: Date?, cal: Calendar)  = TODO("method name ${retriveFunName()} called")

    override fun setTime(parameterIndex: Int, x: Time) {
        ensureIsOpen()
        // Внутри используем long
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.time)
    }

    override fun setTime(parameterIndex: Int, x: Time?, cal: Calendar?)  = TODO("method name ${retriveFunName()} called")

    override fun setTimestamp(parameterIndex: Int, x: Timestamp) {
        ensureIsOpen()
        // Внутри используем long
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.time)
    }

    override fun setTimestamp(parameterIndex: Int, x: Timestamp?, cal: Calendar?)  = TODO("method name ${retriveFunName()} called")

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?)  = TODO("method name ${retriveFunName()} called")

    override fun setUnicodeStream(parameterIndex: Int, x: InputStream?, length: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?)  = TODO("method name ${retriveFunName()} called")

    override fun clearParameters() {
        ensureIsOpen()
        parameters.clear()
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setObject(paramIdx: Int, x: Any) {
        ensureIsOpen()
        when (x) {
            is Boolean -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.BOOL, x)

            is Int -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
            is Long -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
            is Byte -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)
            is Short -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.INT, x)

            is Instant -> parameters[paramIdx] =
                SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.toEpochMilli())
            is Time -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.time)
            is Timestamp -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.time)
            is Date -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.TIMESTAMP, x.time)

            is Float -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, x)
            is Double -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, x)

            is BigDecimal -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.DECIMAL, x)

            is java.sql.Array -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.ARRAY, x.array)
            is Array<*> -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.ARRAY, x)
            is List<*> -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.ARRAY, x.toTypedArray())

            is Map<*, *> -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.OBJECT, x)

            is String -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, x)
            else -> parameters[paramIdx] = SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, x.toString())
        }
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int, scaleOrLength: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Int)  = TODO("method name ${retriveFunName()} called")

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?)  = TODO("method name ${retriveFunName()} called")

    override fun setRef(parameterIndex: Int, x: Ref?)  = TODO("method name ${retriveFunName()} called")

    override fun setBlob(parameterIndex: Int, x: Blob?)  = TODO("method name ${retriveFunName()} called")

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?)  = TODO("method name ${retriveFunName()} called")

    override fun setClob(parameterIndex: Int, x: Clob?)  = TODO("method name ${retriveFunName()} called")

    override fun setClob(parameterIndex: Int, reader: Reader?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setClob(parameterIndex: Int, reader: Reader?)  = TODO("method name ${retriveFunName()} called")

    override fun setArray(parameterIndex: Int, x: java.sql.Array) {
        ensureIsOpen()
        parameters[parameterIndex] = SvdbJdbcParameter(ColumnOuterClass.DataType.ARRAY, x.array)
    }

    override fun getMetaData(): ResultSetMetaData  = TODO("method name ${retriveFunName()} called")

    override fun setURL(parameterIndex: Int, x: URL?)  = TODO("method name ${retriveFunName()} called")

    override fun getParameterMetaData(): ParameterMetaData  = TODO("method name ${retriveFunName()} called")

    override fun setRowId(parameterIndex: Int, x: RowId?)  = TODO("method name ${retriveFunName()} called")

    override fun setNString(parameterIndex: Int, value: String?)  = TODO("method name ${retriveFunName()} called")

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?)  = TODO("method name ${retriveFunName()} called")

    override fun setNClob(parameterIndex: Int, value: NClob?)  = TODO("method name ${retriveFunName()} called")

    override fun setNClob(parameterIndex: Int, reader: Reader?, length: Long)  = TODO("method name ${retriveFunName()} called")

    override fun setNClob(parameterIndex: Int, reader: Reader?)  = TODO("method name ${retriveFunName()} called")

    override fun setSQLXML(parameterIndex: Int, xmlObject: SQLXML?)  = TODO("method name ${retriveFunName()} called")

    companion object {
        const val PREPARED_QUERY_UID_FIELD_NAME = "uid"

        const val EXECUTE_PREFIX = "EXECUTE"

        const val PREPARE_PREFIX = "PREPARE"

        const val SVDB_ERROR_CODE_NOT_FOUND_UID = "SVDB0260"
    }
}
