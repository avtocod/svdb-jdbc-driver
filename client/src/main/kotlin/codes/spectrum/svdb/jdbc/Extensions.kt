package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ColumnOuterClass
import codes.spectrum.svdb.model.v1.Queryresult.QueryResult
import codes.spectrum.svdb.model.v1.RecordOuterClass
import com.google.protobuf.ByteString
import com.google.protobuf.ListValue
import com.google.protobuf.Value
import java.sql.SQLException
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

inline fun <R> runWrappingSqlException(
    sqlStatus: String,
    block: () -> R,
): R {
    return try {
        block()
    } catch (e: Exception) {
        if (e is SQLException) throw e
        throw SQLException(e.message, sqlStatus, e)
    }
}

const val CONNECTION_EXCEPTION_CODE = "08000"

const val DATA_EXCEPTION = "22000"

fun RecordOuterClass.Record.toByteStringList(): List<ByteString> {
    return fieldsList.map { it }
}

fun getIndexByLabel(
    columns: List<ColumnOuterClass.Column>,
    columnLabel: String,
): Int {
    return columns.indexOfFirst { it.code == columnLabel }
}

fun QueryResult.at(rowIndex : Int, colIndex: Int): Any? {
    val row = this.getRecords(rowIndex)
    val value = row.getFields(colIndex)
    val type = this.columnsList[colIndex].dataType
    return unmarshalByteField(value, type)
}


fun unmarshalByteField(
    byteItem: ByteString,
    dataType: ColumnOuterClass.DataType,
): Any? {
    if (byteItem.isEmpty) {
        return null
    }

    return when (dataType) {
        ColumnOuterClass.DataType.STRING -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.STRING_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.stringValue
        }

        ColumnOuterClass.DataType.BOOL -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.BOOL_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.boolValue
        }

        ColumnOuterClass.DataType.INT -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.NUMBER_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.numberValue.toLong()
        }

        ColumnOuterClass.DataType.FLOAT -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.NUMBER_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.numberValue
        }

        ColumnOuterClass.DataType.DECIMAL -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.STRING_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.stringValue
        }

        ColumnOuterClass.DataType.TIMESTAMP -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.STRING_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.stringValue
        }

        ColumnOuterClass.DataType.DURATION -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.STRING_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.stringValue
        }

        ColumnOuterClass.DataType.ARRAY -> {
            val listValue = ListValue.parseFrom(byteItem)

            if (listValue.unknownFields.serializedSize != 0) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return unwrapValues(listValue.valuesList)
        }

        ColumnOuterClass.DataType.OBJECT -> {
            val value = Value.parseFrom(byteItem)

            if (value.kindCase != Value.KindCase.STRING_VALUE) {
                error("can't unmarshal byte value with datatype: $dataType")
            }

            return value.stringValue
        }

        else -> null
    }
}

fun unwrapValues(valuesList: List<Value>): SvdbJdbcArrayWrapperV2 {
    return SvdbJdbcArrayWrapperV2(
        valuesList.map {
            when (it.kindCase) {
                Value.KindCase.BOOL_VALUE -> it.boolValue
                Value.KindCase.LIST_VALUE -> unwrapValues(it.listValue.valuesList)
                Value.KindCase.NUMBER_VALUE -> unwrapNumberValue(it.numberValue)
                Value.KindCase.STRING_VALUE -> it.stringValue
                Value.KindCase.STRUCT_VALUE -> unwrapValuesMap(it.structValue.fieldsMap)
                Value.KindCase.KIND_NOT_SET -> null
                Value.KindCase.NULL_VALUE -> null
                else -> null
            }
        },
    )
}

fun unwrapValuesMap(valuesMap: Map<String, Value>): SvdbJdbcObjWrapperV2 {
    return SvdbJdbcObjWrapperV2(
        valuesMap.map {
            it.key to
                when (it.value.kindCase) {
                    Value.KindCase.BOOL_VALUE -> it.value.boolValue
                    Value.KindCase.LIST_VALUE -> unwrapValues(it.value.listValue.valuesList)
                    Value.KindCase.NUMBER_VALUE -> it.value.numberValue
                    Value.KindCase.STRING_VALUE -> it.value.stringValue
                    Value.KindCase.STRUCT_VALUE -> unwrapValuesMap(it.value.structValue.fieldsMap)
                    Value.KindCase.KIND_NOT_SET -> null
                    Value.KindCase.NULL_VALUE -> null
                    else -> null
                }
        }.toMap(),
    )
}

fun unwrapNumberValue(value: Double): Any {
    if (value.toString().endsWith(".0")) {
        return value.absoluteValue.roundToLong()
    }
    return value
}
