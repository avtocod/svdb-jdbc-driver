package codes.spectrum.svdb.jdbc

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Array
import java.sql.Date
import java.util.*

class SvdbJdbcStaticStringResultSet(
    s: Sequence<String>,
    private val tableName: String,
    separator: String = FIELD_SEPARATOR
): ResultSet {
    private val iterator: Iterator<String>
    private val title: MutableMap<String, Int> = mutableMapOf()
    private var record: List<String>? = null

    init {
        iterator = s.iterator()

        val firstRow = if (iterator.hasNext()) {
            iterator.next()
        } else ""

        firstRow.split(separator).mapIndexed { index, string ->
            string to index + 1
        }.toMap().also { title.putAll(it) }
    }

    @Throws(SQLException::class)
    override fun next(): Boolean {
        val retVal = iterator.hasNext()
        record = null
        if (iterator.hasNext()) {
            record = iterator.next().split(FIELD_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
        }
        return retVal
    }

    @Throws(SQLException::class)
    override fun close() {
    }

    @Throws(SQLException::class)
    override fun wasNull(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun getString(i: Int): String {
        return record!![i - 1]
    }

    @Throws(SQLException::class)
    override fun getBoolean(i: Int): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun getByte(i: Int): Byte {
        return 0
    }

    @Throws(SQLException::class)
    override fun getShort(i: Int): Short {
        return 0
    }

    @Throws(SQLException::class)
    override fun getInt(i: Int): Int {
        return record!![i - 1].toInt()
    }

    @Throws(SQLException::class)
    override fun getLong(i: Int): Long {
        return 0
    }

    @Throws(SQLException::class)
    override fun getFloat(i: Int): Float {
        return 0F
    }

    @Throws(SQLException::class)
    override fun getDouble(i: Int): Double {
        return 0.0
    }

    @Deprecated("Deprecated in Java")
    @Throws(SQLException::class)
    override fun getBigDecimal(i: Int, i1: Int): BigDecimal {
        return BigDecimal(0)
    }

    @Throws(SQLException::class)
    override fun getBytes(i: Int): ByteArray {
        return ByteArray(0)
    }

    @Throws(SQLException::class)
    override fun getDate(i: Int): Date {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getTime(i: Int): Time {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getTimestamp(i: Int): Timestamp {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getAsciiStream(i: Int): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Deprecated("Deprecated in Java")
    @Throws(SQLException::class)
    override fun getUnicodeStream(i: Int): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getBinaryStream(i: Int): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getString(s: String): String {
        return getString(title[s] ?: throw SQLException("column $s is not exists!"))
    }

    @Throws(SQLException::class)
    override fun getBoolean(s: String): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun getByte(s: String): Byte {
        return 0
    }

    @Throws(SQLException::class)
    override fun getShort(s: String): Short {
        return 0
    }

    @Throws(SQLException::class)
    override fun getInt(s: String): Int {
        return getInt(title[s] ?: throw SQLException("column $s is not exists!"))
    }

    @Throws(SQLException::class)
    override fun getLong(s: String): Long {
        return 0
    }

    @Throws(SQLException::class)
    override fun getFloat(s: String): Float {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getDouble(s: String): Double {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getBigDecimal(s: String, i: Int): BigDecimal {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getBytes(s: String): ByteArray {
        return ByteArray(0)
    }

    @Throws(SQLException::class)
    override fun getDate(s: String): Date {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getTime(s: String): Time {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getTimestamp(s: String): Timestamp {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getAsciiStream(s: String): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getUnicodeStream(s: String): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getBinaryStream(s: String): InputStream {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getWarnings(): SQLWarning? {
        return null // пока же нет ничего
        /* 
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
        */
    }

    @Throws(SQLException::class)
    override fun clearWarnings() {
    }

    @Throws(SQLException::class)
    override fun getCursorName(): String {
        return "DEFAULT_CURSOR"
    }

    @Throws(SQLException::class)
    override fun getMetaData(): ResultSetMetaData {
        return SvdbJdbcResultSetMetaData(resolveDataWithTypes(title.keys.toList()))
    }

    private fun resolveDataWithTypes(strings: List<String>): List<SvdbJdbcColumnMetadata>  {
        return List(strings.size) { index -> SvdbJdbcColumnMetadata(index.toString(), SvdbJdbcTypes.TEXT) }
    }

    @Throws(SQLException::class)
    override fun getObject(i: Int): Any {
        return getString(i)
    }

    @Throws(SQLException::class)
    override fun getObject(s: String): Any {
        return getString(s)
    }

    @Throws(SQLException::class)
    override fun findColumn(s: String): Int {
        return 0
    }

    @Throws(SQLException::class)
    override fun getCharacterStream(i: Int): Reader  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getCharacterStream(s: String): Reader {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getBigDecimal(i: Int): BigDecimal  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getBigDecimal(s: String): BigDecimal {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun isBeforeFirst(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun isAfterLast(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun isFirst(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun isLast(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun beforeFirst() {
    }

    @Throws(SQLException::class)
    override fun afterLast() {
    }

    @Throws(SQLException::class)
    override fun first(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun last(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun getRow(): Int {
        return 0
    }

    @Throws(SQLException::class)
    override fun absolute(i: Int): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun relative(i: Int): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun previous(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun setFetchDirection(i: Int) {
    }

    @Throws(SQLException::class)
    override fun getFetchDirection(): Int {
        return 0
    }

    @Throws(SQLException::class)
    override fun setFetchSize(i: Int) {
    }

    @Throws(SQLException::class)
    override fun getFetchSize(): Int {
        return 0
    }

    @Throws(SQLException::class)
    override fun getType(): Int {
        return ResultSet.TYPE_FORWARD_ONLY
    }

    @Throws(SQLException::class)
    override fun getConcurrency(): Int {
        return ResultSet.CONCUR_READ_ONLY
    }

    @Throws(SQLException::class)
    override fun rowUpdated(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun rowInserted(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun rowDeleted(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun updateNull(i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateBoolean(i: Int, b: Boolean) {
    }

    @Throws(SQLException::class)
    override fun updateByte(i: Int, b: Byte) {
    }

    @Throws(SQLException::class)
    override fun updateShort(i: Int, i1: Short) {
    }

    @Throws(SQLException::class)
    override fun updateInt(i: Int, i1: Int) {
    }

    @Throws(SQLException::class)
    override fun updateLong(i: Int, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateFloat(i: Int, v: Float) {
    }

    @Throws(SQLException::class)
    override fun updateDouble(i: Int, v: Double) {
    }

    @Throws(SQLException::class)
    override fun updateBigDecimal(i: Int, bigDecimal: BigDecimal) {
    }

    @Throws(SQLException::class)
    override fun updateString(i: Int, s: String) {
    }

    @Throws(SQLException::class)
    override fun updateBytes(i: Int, bytes: ByteArray) {
    }

    @Throws(SQLException::class)
    override fun updateDate(i: Int, date: Date) {
    }

    @Throws(SQLException::class)
    override fun updateTime(i: Int, time: Time) {
    }

    @Throws(SQLException::class)
    override fun updateTimestamp(i: Int, timestamp: Timestamp) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(i: Int, inputStream: InputStream, i1: Int) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(i: Int, inputStream: InputStream, i1: Int) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(i: Int, reader: Reader, i1: Int) {
    }

    @Throws(SQLException::class)
    override fun updateObject(i: Int, o: Any, i1: Int) {
    }

    @Throws(SQLException::class)
    override fun updateObject(i: Int, o: Any) {
    }

    @Throws(SQLException::class)
    override fun updateNull(s: String) {
    }

    @Throws(SQLException::class)
    override fun updateBoolean(s: String, b: Boolean) {
    }

    @Throws(SQLException::class)
    override fun updateByte(s: String, b: Byte) {
    }

    @Throws(SQLException::class)
    override fun updateShort(s: String, i: Short) {
    }

    @Throws(SQLException::class)
    override fun updateInt(s: String, i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateLong(s: String, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateFloat(s: String, v: Float) {
    }

    @Throws(SQLException::class)
    override fun updateDouble(s: String, v: Double) {
    }

    @Throws(SQLException::class)
    override fun updateBigDecimal(s: String, bigDecimal: BigDecimal) {
    }

    @Throws(SQLException::class)
    override fun updateString(s: String, s1: String) {
    }

    @Throws(SQLException::class)
    override fun updateBytes(s: String, bytes: ByteArray) {
    }

    @Throws(SQLException::class)
    override fun updateDate(s: String, date: Date) {
    }

    @Throws(SQLException::class)
    override fun updateTime(s: String, time: Time) {
    }

    @Throws(SQLException::class)
    override fun updateTimestamp(s: String, timestamp: Timestamp) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(s: String, inputStream: InputStream, i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(s: String, inputStream: InputStream, i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(s: String, reader: Reader, i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateObject(s: String, o: Any, i: Int) {
    }

    @Throws(SQLException::class)
    override fun updateObject(s: String, o: Any) {
    }

    @Throws(SQLException::class)
    override fun insertRow() {
    }

    @Throws(SQLException::class)
    override fun updateRow() {
    }

    @Throws(SQLException::class)
    override fun deleteRow() {
    }

    @Throws(SQLException::class)
    override fun refreshRow() {
    }

    @Throws(SQLException::class)
    override fun cancelRowUpdates() {
    }

    @Throws(SQLException::class)
    override fun moveToInsertRow() {
    }

    @Throws(SQLException::class)
    override fun moveToCurrentRow() {
    }

    @Throws(SQLException::class)
    override fun getStatement(): Statement  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getObject(i: Int, map: Map<String?, Class<*>?>?): Any  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getRef(i: Int): Ref  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getBlob(i: Int): Blob  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getClob(i: Int): Clob  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getArray(i: Int): Array  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getObject(s: String, map: Map<String?, Class<*>?>?): Any {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getRef(s: String): Ref {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getBlob(s: String): Blob {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getClob(s: String): Clob {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getArray(s: String): Array {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getDate(i: Int, calendar: Calendar): Date  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getDate(s: String, calendar: Calendar): Date {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getTime(i: Int, calendar: Calendar): Time  = TODO("method name ${retriveFunName()} called")

    @Throws(SQLException::class)
    override fun getTime(s: String, calendar: Calendar): Time {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getTimestamp(i: Int, calendar: Calendar): Timestamp {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i, $calendar")
    }

    @Throws(SQLException::class)
    override fun getTimestamp(s: String, calendar: Calendar): Timestamp {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getURL(i: Int): URL {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getURL(s: String): URL {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun updateRef(i: Int, ref: Ref) {
    }

    @Throws(SQLException::class)
    override fun updateRef(s: String, ref: Ref) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(i: Int, blob: Blob) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(s: String, blob: Blob) {
    }

    @Throws(SQLException::class)
    override fun updateClob(i: Int, clob: Clob) {
    }

    @Throws(SQLException::class)
    override fun updateClob(s: String, clob: Clob) {
    }

    @Throws(SQLException::class)
    override fun updateArray(i: Int, array: Array) {
    }

    @Throws(SQLException::class)
    override fun updateArray(s: String, array: Array) {
    }

    @Throws(SQLException::class)
    override fun getRowId(i: Int): RowId {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getRowId(s: String): RowId {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun updateRowId(i: Int, rowId: RowId) {
    }

    @Throws(SQLException::class)
    override fun updateRowId(s: String, rowId: RowId) {
    }

    @Throws(SQLException::class)
    override fun getHoldability(): Int {
        return 0
    }

    @Throws(SQLException::class)
    override fun isClosed(): Boolean {
        return false
    }

    @Throws(SQLException::class)
    override fun updateNString(i: Int, s: String) {
    }

    @Throws(SQLException::class)
    override fun updateNString(s: String, s1: String) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(i: Int, nClob: NClob) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(s: String, nClob: NClob) {
    }

    @Throws(SQLException::class)
    override fun getNClob(i: Int): NClob {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getNClob(s: String): NClob {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getSQLXML(i: Int): SQLXML {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getSQLXML(s: String): SQLXML {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun updateSQLXML(i: Int, sqlxml: SQLXML) {
    }

    @Throws(SQLException::class)
    override fun updateSQLXML(s: String, sqlxml: SQLXML) {
    }

    @Throws(SQLException::class)
    override fun getNString(i: Int): String {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getNString(s: String): String {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun getNCharacterStream(i: Int): Reader {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i")
    }

    @Throws(SQLException::class)
    override fun getNCharacterStream(s: String): Reader {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s")
    }

    @Throws(SQLException::class)
    override fun updateNCharacterStream(i: Int, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateNCharacterStream(s: String, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(i: Int, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(i: Int, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(i: Int, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(s: String, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(s: String, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(s: String, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(i: Int, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(s: String, inputStream: InputStream, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateClob(i: Int, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateClob(s: String, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(i: Int, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(s: String, reader: Reader, l: Long) {
    }

    @Throws(SQLException::class)
    override fun updateNCharacterStream(i: Int, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateNCharacterStream(s: String, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(i: Int, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(i: Int, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(i: Int, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateAsciiStream(s: String, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateBinaryStream(s: String, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateCharacterStream(s: String, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(i: Int, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateBlob(s: String, inputStream: InputStream) {
    }

    @Throws(SQLException::class)
    override fun updateClob(i: Int, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateClob(s: String, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(i: Int, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun updateNClob(s: String, reader: Reader) {
    }

    @Throws(SQLException::class)
    override fun <T> getObject(i: Int, aClass: Class<T>): T {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $i, $aClass")
    }

    @Throws(SQLException::class)
    override fun <T> getObject(s: String, aClass: Class<T>): T {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $s, $aClass")
    }

    @Throws(SQLException::class)
    override fun <T> unwrap(aClass: Class<T>): T {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called with parameters $aClass")
    }

    @Throws(SQLException::class)
    override fun isWrapperFor(aClass: Class<*>?): Boolean {
        return false
    }

    companion object {
        private const val FIELD_SEPARATOR = ";"
    }
}
