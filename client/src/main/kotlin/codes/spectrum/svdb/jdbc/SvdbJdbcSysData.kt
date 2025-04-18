package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import java.sql.DatabaseMetaData.columnNoNulls
import java.sql.DatabaseMetaData.columnNullable
import java.sql.Types
import javax.xml.crypto.Data


data class SvdbJdbcSysData(
    val catalogs: List<SvdbJdbcCatalog> = emptyList(),
    val fields: List<SvdbJdbcField> = emptyList(),
    val version: String = "",
) {
    fun toSchemasTablesScv(catalog: String? = null, schema: String? = null): Sequence<String> = sequence {
        yield("TABLE_CAT;TABLE_SCHEM;TABLE_NAME;REMARKS;TABLE_TYPE")
        catalogs.filter { catalog == null || it.name == catalog }.forEach { catalog ->
            catalog.schemas.filter { schema == null || it.name == schema }.forEach { schema ->
                schema.tables.forEach { table ->
                    yield("${catalog.name};${schema.name};${table.name};${table.description};TABLE")
                }
            }
        }
    }

    fun toSchemasScv(catalog: String? = null): Sequence<String> = sequence {
        yield("TABLE_SCHEM;TABLE_CATALOG")
        catalogs.filter { catalog == null || it.name == catalog }.forEach { catalog ->
            catalog.schemas.forEach { schema ->
                yield("${schema.name};${catalog.name}")
            }
        }
    }

    fun toCatalogsScv(): Sequence<String> = sequence {
        yield("TABLE_CAT")
        catalogs.forEach {
            yield(it.name)
        }
    }

    fun toFieldsScv(catalog: String, schema: String, table: String): Sequence<String> = sequence {
        yield("TABLE_CAT;TABLE_SCHEM;TABLE_NAME;COLUMN_NAME;DATA_TYPE;TYPE_NAME;ORDINAL_POSITION;REMARKS;NULLABLE")
        yieldAll(
            fields
                .asSequence()
                .filter {
                    it.catalog == catalog && it.schema == schema && it.table == table
                }
                .map {
                    "${it.catalog};${it.schema};${it.table};${it.name};${it.sqlType};${it.type};${it.position};" +
                            "${it.description};${it.isNull.toInt()}"
                }
        )
    }

    private fun Boolean.toInt(): Int = if (this) columnNullable else columnNoNulls
}

data class SvdbJdbcCatalog(
        val name: String = "",
        val schemas: List<Schema> = listOf()
)

data class SvdbJdbcField(
    val catalog: String = "",
    val schema: String = "",
    val table: String = "",
    val name: String = "",
    val type: DataType = DataType.UNDEFINED,
    val description: String = "",
    val position: Int = -1,
    val isNull: Boolean = false,
) {
    val sqlType: Int = type.toSqlTypeInt()
}

internal fun DataType.toSqlTypeInt() = when (this) {
    DataType.STRING -> Types.VARCHAR
    DataType.INT -> Types.INTEGER
    DataType.DECIMAL -> Types.BIGINT
    DataType.FLOAT -> Types.DOUBLE
    DataType.DATE -> Types.DATE
    DataType.TIMESTAMP -> Types.TIMESTAMP_WITH_TIMEZONE
    DataType.BOOL -> Types.BOOLEAN
    else -> Types.OTHER
}

data class Schema(
        val name: String = "",
        val tables: List<Table> = listOf()
)

data class Table(
        val name: String,
        val description: String,
)
