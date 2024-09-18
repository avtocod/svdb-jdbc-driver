package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import java.sql.Types

enum class SvdbJdbcTypes(
    val typeName: String,
    val sqlType: Int,
    val javaName: String,
) {
    TEXT("text", Types.VARCHAR, "java.lang.String"),
    INT("int", Types.INTEGER, "java.lang.Integer"),
    BIGINT("bigint", Types.BIGINT, "java.lang.Long"),
    DOUBLE("double", Types.DOUBLE, "java.lang.Double"),
    DECIMAL("decimal", Types.DECIMAL, "java.math.BigDecimal"),
    BOOLEAN("bool", Types.BOOLEAN, "java.lang.Boolean"),
    DURATION("duration", Types.VARCHAR, "java.lang.String"),
    DATETIME("datetime", Types.VARCHAR, "java.lang.String"),
    ARRAY("array", Types.OTHER, "codes.spectrum.svdb.jdbc.SvdJdbcArrayWrapper"),
    MAP("object", Types.OTHER, "codes.spectrum.svdb.jdbc.SvdJdbcObjWrapper"),
    UNDEFINED("undefined", Types.OTHER, "");

    companion object {
        fun getByDataType(dataType: DataType): SvdbJdbcTypes {
            return when (dataType) {
                DataType.UNDEFINED -> UNDEFINED
                DataType.STRING -> TEXT
                DataType.BOOL -> BOOLEAN
                DataType.INT -> BIGINT
                DataType.FLOAT -> DOUBLE
                DataType.DECIMAL -> DECIMAL
                DataType.TIMESTAMP -> DATETIME
                DataType.DATE -> DATETIME
                DataType.DURATION -> DURATION
                DataType.ARRAY -> ARRAY
                DataType.OBJECT -> MAP
                else -> UNDEFINED
            }
        }
    }
}
