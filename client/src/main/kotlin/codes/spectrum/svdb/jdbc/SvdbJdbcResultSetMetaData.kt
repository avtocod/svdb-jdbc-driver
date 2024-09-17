package codes.spectrum.svdb.jdbc

import java.sql.ResultSetMetaData

class SvdbJdbcResultSetMetaData(
    val fields: List<SvdbJdbcColumnMetadata>
) : ResultSetMetaData {
    override fun <T : Any?> unwrap(iface: Class<T>?): T = TODO("method name ${retriveFunName()} called")

    override fun isWrapperFor(iface: Class<*>?): Boolean = TODO("method name ${retriveFunName()} called")

    override fun getColumnCount(): Int {
        return fields.size
    }

    override fun isAutoIncrement(column: Int): Boolean = false

    override fun isCaseSensitive(column: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun isSearchable(column: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun isCurrency(column: Int): Boolean = false

    override fun isNullable(column: Int): Int = TODO("method name ${retriveFunName()} called")

    override fun isSigned(column: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun getColumnDisplaySize(column: Int): Int = TODO("method name ${retriveFunName()} called")

    override fun getColumnLabel(column: Int): String {
        return fields[column - 1].columnName
    }

    override fun getColumnName(column: Int): String {
        return fields[column - 1].columnName
    }

    override fun getSchemaName(column: Int): String = TODO("method name ${retriveFunName()} called")

    override fun getPrecision(column: Int): Int = TODO("method name ${retriveFunName()} called")

    override fun getScale(column: Int): Int {
        return fields[column - 1].scale
    }

    override fun getTableName(column: Int): String = TODO("method name ${retriveFunName()} called")

    override fun getCatalogName(column: Int): String = TODO("method name ${retriveFunName()} called")

    override fun getColumnType(column: Int): Int {
        return fields[column - 1].type.sqlType
    }

    override fun getColumnTypeName(column: Int): String {
        return fields[column - 1].type.typeName
    }

    override fun isReadOnly(column: Int): Boolean = true

    override fun isWritable(column: Int): Boolean = false

    override fun isDefinitelyWritable(column: Int): Boolean = TODO("method name ${retriveFunName()} called")

    override fun getColumnClassName(column: Int): String {
        return fields[column - 1].type.javaName
    }
}