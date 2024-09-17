package codes.spectrum.svdb.jdbc

import java.sql.ResultSetMetaData

class SvdbJdbcResultSetMetaData(
    val fields: List<SvdbJdbcColumnMetadata>
) : ResultSetMetaData {
    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getColumnCount(): Int {
        return fields.size
    }

    override fun isAutoIncrement(column: Int): Boolean = false

    override fun isCaseSensitive(column: Int): Boolean {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun isSearchable(column: Int): Boolean {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun isCurrency(column: Int): Boolean = false

    override fun isNullable(column: Int): Int {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun isSigned(column: Int): Boolean {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getColumnDisplaySize(column: Int): Int {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getColumnLabel(column: Int): String {
        return fields[column - 1].columnName
    }

    override fun getColumnName(column: Int): String {
        return fields[column - 1].columnName
    }

    override fun getSchemaName(column: Int): String {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getPrecision(column: Int): Int {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getScale(column: Int): Int {
        return fields[column - 1].scale
    }

    override fun getTableName(column: Int): String {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getCatalogName(column: Int): String {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getColumnType(column: Int): Int {
        return fields[column - 1].type.sqlType
    }

    override fun getColumnTypeName(column: Int): String {
        return fields[column - 1].type.typeName
    }

    override fun isReadOnly(column: Int): Boolean = true

    override fun isWritable(column: Int): Boolean = false

    override fun isDefinitelyWritable(column: Int): Boolean {
        val methodName = object : Any() {}
            .javaClass
            .enclosingMethod
            .name
        TODO("method name $methodName called")
    }

    override fun getColumnClassName(column: Int): String {
        return fields[column - 1].type.javaName
    }
}