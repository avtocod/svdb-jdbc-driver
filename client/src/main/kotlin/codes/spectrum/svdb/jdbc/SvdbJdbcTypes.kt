package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import codes.spectrum.svdb.model.v1.ValueOuterClass
import java.sql.Types

enum class SvdbJdbcTypes(
    val valuesCase: ValueOuterClass.Value.ValueCase,
    val typeName: String,
    val sqlType: Int,
    val javaName: String,
) {
    TEXT(ValueOuterClass.Value.ValueCase.STR, "text", Types.VARCHAR, "java.lang.String"),
    INT(ValueOuterClass.Value.ValueCase.I32, "int", Types.INTEGER, "java.lang.Integer"),
    BIGINT(ValueOuterClass.Value.ValueCase.I64, "bigint", Types.BIGINT, "java.lang.Long"),
    DOUBLE(ValueOuterClass.Value.ValueCase.F64, "double", Types.DOUBLE, "java.lang.Double"),
    DECIMAL(ValueOuterClass.Value.ValueCase.DEC, "decimal", Types.DECIMAL, "java.math.BigDecimal"),
    BOOLEAN(ValueOuterClass.Value.ValueCase.BIT, "bool", Types.BOOLEAN, "java.lang.Boolean"),
    DURATION(ValueOuterClass.Value.ValueCase.DUR, "duration", Types.VARCHAR, "java.lang.String"),
    DATETIME(ValueOuterClass.Value.ValueCase.TIM, "datetime", Types.VARCHAR, "java.lang.String"),
    ARRAY(ValueOuterClass.Value.ValueCase.ARR, "array", Types.OTHER, "codes.spectrum.svdb.jdbc.SvdJdbcArrayWrapper"),
    MAP(ValueOuterClass.Value.ValueCase.OBJ, "object", Types.OTHER, "codes.spectrum.svdb.jdbc.SvdJdbcObjWrapper"),
    UNDEFINED(ValueOuterClass.Value.ValueCase.VALUE_NOT_SET, "undefined", Types.OTHER, "");

    companion object {
        fun getByValueCase(valuesCase: ValueOuterClass.Value.ValueCase): SvdbJdbcTypes {
            return entries.first { it.valuesCase == valuesCase }
        }

        fun getByDataType(dataType: DataType): SvdbJdbcTypes {
            return when (dataType) {
                DataType.AUTO -> UNDEFINED
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
