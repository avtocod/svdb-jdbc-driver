package codes.spectrum.svdb.jdbc

import codes.spectrum.*
import codes.spectrum.commons.Effective
import codes.spectrum.svdb.ISvdbCursor
import codes.spectrum.svdb.SvdbStateCodes
import codes.spectrum.svdb.model.v1.*
import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import com.google.protobuf.ByteString
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Array
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

class SvdbJdbcResultSet(
    val cursor: ISvdbCursor,
) : ResultSet {
    private var currentElem: List<ByteString>? = null
    private val prefetchQueueRecords: Queue<RecordOuterClass.Record> = LinkedList()
    private val columnsData: MutableList<ColumnOuterClass.Column> = mutableListOf()
    private val warningsQueue: Queue<WarningOuterClass.Warning> = LinkedList()
    private var lastResult: Queryresult.QueryResult = queryResult { state = state { type = "OK" } }
    private var wasFirst = false
    private var internalIsClosed = false

    private fun read() {
        // подготовили новую очередь
        lastResult = runBlocking { cursor.fetch() }

        if (!wasFirst) {
            wasFirst = true
            columnsData.addAll(lastResult.columnsList)
        }
        prefetchQueueRecords.addAll(lastResult.recordsList)

        warningsQueue.addAll(lastResult.warningsList)
    }

    override fun next(): Boolean {
        checkIsClosed()

        // если была ошибка, то больше данных нет
        if (lastResult.state.type == "ERROR") {
            if (lastResult.state.code == SvdbStateCodes.TIMEOUT.intValue) {
                throw SQLTimeoutException("(${lastResult.state.code}) ${lastResult.state.message}")
            }
            throw SQLException("(${lastResult.state.code}) ${lastResult.state.message}")
        }

        // если уже есть prefetch очередь - то берем из нее
        val prefetchedByteRecord = prefetchQueueRecords.poll()
        if (prefetchedByteRecord != null && prefetchedByteRecord.fieldsList.isNotEmpty()) {
            currentElem = prefetchedByteRecord.toByteStringList()
            return true
        }

        // если prefetch очереди нет, а при этом последний результат был EOF,
        // то данных больше нет
        if (lastResult.state.type == "EOF") {
            return false
        }

        while (lastResult.state.type == "OK" && prefetchQueueRecords.size == 0
        ) {
            read()
        }

        return next()
    }

    private fun checkIsClosed() {
        if (internalIsClosed) throw SQLException("This ResultSet is closed.", SvdbJdbcState.OBJECT_NOT_IN_STATE.code)
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T  = TODO("method name ${retriveFunName()} called")

    override fun isWrapperFor(iface: Class<*>?): Boolean  = TODO("method name ${retriveFunName()} called")

    override fun close() {
        internalIsClosed = true
    }

    override fun wasNull(): Boolean = false

    override fun getString(columnIndex: Int): String? {
        checkIsClosed()

        val byteItem =
            currentElem?.getOrNull(columnIndex - 1) ?: error("can't get column with index $columnIndex")
        return when (val res = unmarshalByteField(byteItem, DataType.STRING)) {
            null -> null
            else -> res.toString()
        }
    }

    override fun getString(columnLabel: String): String? {
        checkIsClosed()

        if (columnsData.isEmpty()) {
            error("can't get column with label $columnLabel")
        }

        val columnIndex = getIndexByLabel(columnsData, columnLabel)
        if (columnIndex == -1) {
            error("can't get column with label $columnLabel")
        }

        val byteItem = currentElem?.get(columnIndex) ?: error("can't get column with label $columnLabel")
        return when (val res = unmarshalByteField(byteItem, DataType.STRING)) {
            null -> null
            else -> res.toString()
        }

    }

    override fun getBoolean(columnIndex: Int): Boolean {
        checkIsClosed()
        return Effective.boolean(getObject(columnIndex))
    }

    override fun getBoolean(columnLabel: String): Boolean {
        checkIsClosed()
        return Effective.boolean(getObject(columnLabel))
    }

    override fun getByte(columnIndex: Int): Byte {
        checkIsClosed()
        return getString(columnIndex)?.toByte() ?: 0
    }

    override fun getByte(columnLabel: String): Byte {
        checkIsClosed()
        return getString(columnLabel)?.toByte() ?: 0
    }

    override fun getShort(columnIndex: Int): Short {
        checkIsClosed()
        return getString(columnIndex)?.toShort() ?: 0
    }

    override fun getShort(columnLabel: String): Short {
        checkIsClosed()
        return getString(columnLabel)?.toShort() ?: 0
    }

    override fun getInt(columnIndex: Int): Int {
        checkIsClosed()
        return getObject(columnIndex).let { Effective.int(it) }
    }

    override fun getInt(columnLabel: String): Int {
        checkIsClosed()
        return getObject(columnLabel).let { Effective.int(it) }
    }

    override fun getLong(columnIndex: Int): Long {
        checkIsClosed()
        return getObject(columnIndex).let { Effective.long(it) }
    }

    override fun getLong(columnLabel: String): Long {
        checkIsClosed()
        return getObject(columnLabel).let { Effective.long(it) }
    }

    override fun getFloat(columnIndex: Int): Float {
        checkIsClosed()
        return getObject(columnIndex).let { Effective.double(it) }.toFloat()
    }

    override fun getFloat(columnLabel: String): Float {
        checkIsClosed()
        return getObject(columnLabel).let { Effective.double(it) }.toFloat()
    }

    override fun getDouble(columnIndex: Int): Double {
        checkIsClosed()
        return getObject(columnIndex).let { Effective.double(it) }
    }

    override fun getDouble(columnLabel: String): Double {
        checkIsClosed()
        return getString(columnLabel).let { Effective.double(it) }
    }

    @Deprecated("Deprecated in Java")
    override fun getBigDecimal(columnIndex: Int, scale: Int): BigDecimal? {
        checkIsClosed()
        return getString(columnIndex)?.let { BigDecimal(it) }
    }

    @Deprecated("Deprecated in Java")
    override fun getBigDecimal(columnLabel: String, scale: Int): BigDecimal? {
        checkIsClosed()
        return getString(columnLabel)?.let { BigDecimal(it) }
    }

    override fun getBigDecimal(columnIndex: Int): BigDecimal? {
        checkIsClosed()
        return getString(columnIndex)?.let { BigDecimal(it) }
    }

    override fun getBigDecimal(columnLabel: String): BigDecimal? {
        checkIsClosed()
        return getString(columnLabel)?.let { BigDecimal(it) }
    }

    override fun getBytes(columnIndex: Int): ByteArray? {
        checkIsClosed()
        return getString(columnIndex)?.toByteArray()
    }

    override fun getBytes(columnLabel: String): ByteArray? {
        checkIsClosed()
        return getString(columnLabel)?.toByteArray()
    }

    override fun getDate(columnIndex: Int): Date? {
        checkIsClosed()
        return getObject(columnIndex)?.let { Date.valueOf(Effective.localDate(it)) }
    }

    override fun getDate(columnLabel: String): Date? {
        checkIsClosed()
        return getObject(columnLabel)?.let { Date.valueOf(Effective.localDate(it)) }
    }

    override fun getDate(columnIndex: Int, cal: Calendar?): Date  = TODO("method name ${retriveFunName()} called")

    override fun getDate(columnLabel: String?, cal: Calendar?): Date  = TODO("method name ${retriveFunName()} called")

    override fun getTime(columnIndex: Int): Time? {
        checkIsClosed()
        return getObject(columnIndex)?.let { (Time(Effective.date(it).time)) }
    }

    override fun getTime(columnLabel: String): Time? {
        checkIsClosed()
        return getObject(columnLabel)?.let { (Time(Effective.date(it).time)) }
    }

    override fun getTime(columnIndex: Int, cal: Calendar?): Time = TODO("method name ${retriveFunName()} called")

    override fun getTime(columnLabel: String?, cal: Calendar?): Time = TODO("method name ${retriveFunName()} called")

    override fun getTimestamp(columnIndex: Int): Timestamp? {
        checkIsClosed()
        return getObject(columnIndex)?.let { Timestamp(Effective.date(it).time) }
    }

    override fun getTimestamp(columnLabel: String?): Timestamp? {
        checkIsClosed()
        return getObject(columnLabel)?.let { Timestamp(Effective.date(it).time) }
    }

    override fun getTimestamp(columnIndex: Int, cal: Calendar?): Timestamp = TODO("method name ${retriveFunName()} called")

    override fun getTimestamp(columnLabel: String?, cal: Calendar?): Timestamp = TODO("method name ${retriveFunName()} called")

    override fun getAsciiStream(columnIndex: Int): InputStream = TODO("method name ${retriveFunName()} called")

    override fun getAsciiStream(columnLabel: String?): InputStream = TODO("method name ${retriveFunName()} called")

    override fun getUnicodeStream(columnIndex: Int): InputStream = TODO("method name ${retriveFunName()} called")

    override fun getUnicodeStream(columnLabel: String?): InputStream = TODO("method name ${retriveFunName()} called")

    override fun getBinaryStream(columnIndex: Int): InputStream = TODO("method name ${retriveFunName()} called")

    override fun getBinaryStream(columnLabel: String?): InputStream = TODO("method name ${retriveFunName()} called")

    private inner class SvdbSqlWarning(wd: WarningOuterClass.Warning) : SQLWarning(
        wd.reason,
        wd.state,
        wd.code,
    ) {
        override val message: String = "[${wd.state}](${wd.code}) ${wd.reason}"
        override fun getNextWarning(): SQLWarning? {
            val wd: WarningOuterClass.Warning = warningsQueue.poll() ?: return null
            return SvdbSqlWarning(wd)
        }
    }

    override fun getWarnings(): SQLWarning? {
        val wd: WarningOuterClass.Warning = warningsQueue.poll() ?: return null
        return SvdbSqlWarning(wd)
    }

    override fun clearWarnings() {
        warningsQueue.clear()
    }

    override fun getCursorName(): String {
        checkIsClosed()
        return cursor.getSessionUid()
    }

    override fun getMetaData(): ResultSetMetaData {
        checkIsClosed()
        read()
        return SvdbJdbcResultSetMetaData(resolveWithColumns(columnsData))
    }

    private fun resolveWithColumns(columnsData: List<ColumnOuterClass.Column>): List<SvdbJdbcColumnMetadata> {
        return columnsData.map {
            SvdbJdbcColumnMetadata(it.code, SvdbJdbcTypes.getByDataType(it.dataType), resolveScale(it.dataType))
        }
    }

    private fun resolveScale(dataType: DataType): Int =
        if (dataType == DataType.DECIMAL) {
            DECIMAL_DEFAULT_SCALE
        } else {
            SCALE_NOT_SUPPORTED
        }

    override fun getObject(columnIndex: Int): Any? {
        checkIsClosed()
        val column = columnsData.getOrNull(columnIndex - 1) ?: return null
        val byteItem = currentElem?.getOrNull(columnIndex - 1) ?: return null
        return unmarshalByteField(byteItem, column.dataType)
    }

    override fun getObject(columnLabel: String?): Any? {
        checkIsClosed()

        if (columnLabel == null) {
            return null
        }

        val columnIndex = getIndexByLabel(columnsData, columnLabel)
        if (columnIndex == -1) {
            error("can't get column with label $columnLabel")
        }

        val column = columnsData.getOrNull(columnIndex) ?: return null
        val byteItem = currentElem?.getOrNull(columnIndex) ?: return null
        return unmarshalByteField(byteItem, column.dataType)
    }

    override fun getObject(columnIndex: Int, map: MutableMap<String, Class<*>>?): Any = TODO("method name ${retriveFunName()} called")

    override fun getObject(columnLabel: String?, map: MutableMap<String, Class<*>>?): Any = TODO("method name ${retriveFunName()} called")

    override fun <T : Any?> getObject(columnIndex: Int, type: Class<T>?): T = TODO("method name ${retriveFunName()} called")

    override fun <T : Any?> getObject(columnLabel: String?, type: Class<T>?): T = TODO("method name ${retriveFunName()} called")

    override fun findColumn(columnLabel: String?): Int = TODO("method name ${retriveFunName()} called")

    override fun getCharacterStream(columnIndex: Int): Reader = TODO("method name ${retriveFunName()} called")

    override fun getCharacterStream(columnLabel: String?): Reader {
        checkIsClosed()
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun isBeforeFirst(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun isAfterLast(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun isFirst(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun isLast(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun beforeFirst() = TODO("method name ${retriveFunName()} called")

    override fun afterLast() = TODO("method name ${retriveFunName()} called")

    override fun first(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun last(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun getRow(): Int = TODO("method name ${retriveFunName()} called")

    override fun absolute(row: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun relative(rows: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun previous(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun setFetchDirection(direction: Int) = TODO("method name ${retriveFunName()} called")

    override fun getFetchDirection(): Int = TODO("method name ${retriveFunName()} called")

    override fun setFetchSize(rows: Int) = TODO("method name ${retriveFunName()} called")

    override fun getFetchSize(): Int = TODO("method name ${retriveFunName()} called")

    override fun getType(): Int {
        checkIsClosed()
        return ResultSet.TYPE_FORWARD_ONLY
    }

    override fun getConcurrency(): Int = TODO("method name ${retriveFunName()} called")

    override fun rowUpdated(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun rowInserted(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun rowDeleted(): Boolean = TODO("method name ${retriveFunName()} called")

    override fun updateNull(columnIndex: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateNull(columnLabel: String?) = TODO("method name ${retriveFunName()} called")

    override fun updateBoolean(columnIndex: Int, x: Boolean) = TODO("method name ${retriveFunName()} called")

    override fun updateBoolean(columnLabel: String?, x: Boolean) = TODO("method name ${retriveFunName()} called")

    override fun updateByte(columnIndex: Int, x: Byte) = TODO("method name ${retriveFunName()} called")

    override fun updateByte(columnLabel: String?, x: Byte) = TODO("method name ${retriveFunName()} called")

    override fun updateShort(columnIndex: Int, x: Short) = TODO("method name ${retriveFunName()} called")

    override fun updateShort(columnLabel: String?, x: Short) = TODO("method name ${retriveFunName()} called")

    override fun updateInt(columnIndex: Int, x: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateInt(columnLabel: String?, x: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateLong(columnIndex: Int, x: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateLong(columnLabel: String?, x: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateFloat(columnIndex: Int, x: Float) = TODO("method name ${retriveFunName()} called")

    override fun updateFloat(columnLabel: String?, x: Float) = TODO("method name ${retriveFunName()} called")

    override fun updateDouble(columnIndex: Int, x: Double) = TODO("method name ${retriveFunName()} called")

    override fun updateDouble(columnLabel: String?, x: Double) = TODO("method name ${retriveFunName()} called")

    override fun updateBigDecimal(columnIndex: Int, x: BigDecimal?) = TODO("method name ${retriveFunName()} called")

    override fun updateBigDecimal(columnLabel: String?, x: BigDecimal?) = TODO("method name ${retriveFunName()} called")

    override fun updateString(columnIndex: Int, x: String?) = TODO("method name ${retriveFunName()} called")

    override fun updateString(columnLabel: String?, x: String?) = TODO("method name ${retriveFunName()} called")

    override fun updateBytes(columnIndex: Int, x: ByteArray?) = TODO("method name ${retriveFunName()} called")

    override fun updateBytes(columnLabel: String?, x: ByteArray?) = TODO("method name ${retriveFunName()} called")

    override fun updateDate(columnIndex: Int, x: Date?) = TODO("method name ${retriveFunName()} called")

    override fun updateDate(columnLabel: String?, x: Date?) = TODO("method name ${retriveFunName()} called")

    override fun updateTime(columnIndex: Int, x: Time?) = TODO("method name ${retriveFunName()} called")

    override fun updateTime(columnLabel: String?, x: Time?) = TODO("method name ${retriveFunName()} called")

    override fun updateTimestamp(columnIndex: Int, x: Timestamp?) = TODO("method name ${retriveFunName()} called")

    override fun updateTimestamp(columnLabel: String?, x: Timestamp?) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnIndex: Int, x: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateObject(columnIndex: Int, x: Any?, scaleOrLength: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateObject(columnIndex: Int, x: Any?) = TODO("method name ${retriveFunName()} called")

    override fun updateObject(columnLabel: String?, x: Any?, scaleOrLength: Int) = TODO("method name ${retriveFunName()} called")

    override fun updateObject(columnLabel: String?, x: Any?) = TODO("method name ${retriveFunName()} called")

    override fun insertRow() = TODO("method name ${retriveFunName()} called")

    override fun updateRow() = TODO("method name ${retriveFunName()} called")

    override fun deleteRow() = TODO("method name ${retriveFunName()} called")

    override fun refreshRow() = TODO("method name ${retriveFunName()} called")

    override fun cancelRowUpdates() = TODO("method name ${retriveFunName()} called")

    override fun moveToInsertRow() = TODO("method name ${retriveFunName()} called")

    override fun moveToCurrentRow() = TODO("method name ${retriveFunName()} called")

    override fun getStatement(): Statement = TODO("method name ${retriveFunName()} called")

    override fun getRef(columnIndex: Int): Ref = TODO("method name ${retriveFunName()} called")

    override fun getRef(columnLabel: String?): Ref = TODO("method name ${retriveFunName()} called")

    override fun getBlob(columnIndex: Int): Blob = TODO("method name ${retriveFunName()} called")

    override fun getBlob(columnLabel: String?): Blob = TODO("method name ${retriveFunName()} called")

    override fun getClob(columnIndex: Int): Clob = TODO("method name ${retriveFunName()} called")

    override fun getClob(columnLabel: String?): Clob = TODO("method name ${retriveFunName()} called")

    override fun getArray(columnIndex: Int): Array {
        return SvdbJdbcSqlArray((getObject(columnIndex) as ArrayList<*>).toArray())
    }

    override fun getArray(columnLabel: String): Array {
        return SvdbJdbcSqlArray((getObject(columnLabel) as ArrayList<*>).toArray())
    }

    override fun getURL(columnIndex: Int): URL = TODO("method name ${retriveFunName()} called")

    override fun getURL(columnLabel: String?): URL = TODO("method name ${retriveFunName()} called")

    override fun updateRef(columnIndex: Int, x: Ref?) = TODO("method name ${retriveFunName()} called")

    override fun updateRef(columnLabel: String?, x: Ref?) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnIndex: Int, x: Blob?) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnLabel: String?, x: Blob?) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnIndex: Int, x: Clob?) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnLabel: String?, x: Clob?) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnIndex: Int, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnLabel: String?, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnIndex: Int, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateClob(columnLabel: String?, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateArray(columnIndex: Int, x: Array?) = TODO("method name ${retriveFunName()} called")

    override fun updateArray(columnLabel: String?, x: Array?) = TODO("method name ${retriveFunName()} called")

    override fun getRowId(columnIndex: Int): RowId = TODO("method name ${retriveFunName()} called")

    override fun getRowId(columnLabel: String?): RowId = TODO("method name ${retriveFunName()} called")

    override fun updateRowId(columnIndex: Int, x: RowId?) = TODO("method name ${retriveFunName()} called")

    override fun updateRowId(columnLabel: String?, x: RowId?) = TODO("method name ${retriveFunName()} called")

    override fun getHoldability(): Int = TODO("method name ${retriveFunName()} called")

    override fun isClosed(): Boolean {
        return internalIsClosed
    }

    override fun updateNString(columnIndex: Int, nString: String?) = TODO("method name ${retriveFunName()} called")

    override fun updateNString(columnLabel: String?, nString: String?) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnIndex: Int, nClob: NClob?) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnLabel: String?, nClob: NClob?) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnIndex: Int, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnLabel: String?, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnIndex: Int, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateNClob(columnLabel: String?, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun getNClob(columnIndex: Int): NClob = TODO("method name ${retriveFunName()} called")

    override fun getNClob(columnLabel: String?): NClob = TODO("method name ${retriveFunName()} called")

    override fun getSQLXML(columnIndex: Int): SQLXML = TODO("method name ${retriveFunName()} called")

    override fun getSQLXML(columnLabel: String?): SQLXML = TODO("method name ${retriveFunName()} called")

    override fun updateSQLXML(columnIndex: Int, xmlObject: SQLXML?) = TODO("method name ${retriveFunName()} called")

    override fun updateSQLXML(columnLabel: String?, xmlObject: SQLXML?) = TODO("method name ${retriveFunName()} called")

    override fun getNString(columnIndex: Int): String = TODO("method name ${retriveFunName()} called")

    override fun getNString(columnLabel: String?): String = TODO("method name ${retriveFunName()} called")

    override fun getNCharacterStream(columnIndex: Int): Reader = TODO("method name ${retriveFunName()} called")

    override fun getNCharacterStream(columnLabel: String?): Reader = TODO("method name ${retriveFunName()} called")

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?, length: Long) = TODO("method name ${retriveFunName()} called")

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?) = TODO("method name ${retriveFunName()} called")

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?) = TODO("method name ${retriveFunName()} called")

    companion object {
        const val DECIMAL_DEFAULT_SCALE = 6

        const val SCALE_NOT_SUPPORTED = 0
    }
}
