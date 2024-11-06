package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.SvdbNull
import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType
import com.google.protobuf.*
import java.time.Instant
import java.time.LocalDate

fun tryNewByteValue(value: Any, dataType: DataType): ByteString {
    if (value is SvdbNull) {
        return ByteString.empty()
    }
    return when (dataType) {
        DataType.BOOL -> {
            val bool = value as Boolean
            Value.newBuilder().setBoolValue(bool).build().toByteString()
        }

        DataType.INT -> {
            when (value) {
                is Int -> Value.newBuilder().setNumberValue(value.toDouble()).build().toByteString()
                is Long -> Value.newBuilder().setNumberValue(value.toDouble()).build().toByteString()
                else -> error("can't marshal byte value with datatype: $dataType")
            }
        }

        DataType.FLOAT -> {
            when (value) {
                is Int -> Value.newBuilder().setNumberValue(value.toDouble()).build().toByteString()
                is Long -> Value.newBuilder().setNumberValue(value.toDouble()).build().toByteString()
                is Float -> Value.newBuilder().setNumberValue(value.toDouble()).build().toByteString()
                is Double -> Value.newBuilder().setNumberValue(value).build().toByteString()
                else -> error("can't marshal byte value with datatype: $dataType")
            }
        }

        DataType.STRING -> {
            Value.newBuilder().setStringValue(value.toString()).build().toByteString()
        }
        DataType.DECIMAL,
        DataType.TIMESTAMP,
        DataType.DATE,
        DataType.DURATION -> {
            Value.newBuilder().setStringValue(value.toString()).build().toByteString()
        }

        DataType.ARRAY -> {
            val arr = value as Array<*>
            encodeList(arr).toByteString()
        }

        DataType.OBJECT -> {
            val struct = value as Map<*, *>
            encodeObject(struct).toByteString()
        }

        DataType.UNDEFINED -> error("can't marshal byte value with datatype: $dataType")
        DataType.ANY -> error("can't marshal byte value with datatype: $dataType")
        else -> error("can't marshal byte value with datatype: $dataType")
    }
}

fun encodeList(value: Array<*>): ListValue {
    return ListValue.newBuilder().addAllValues(
        value.map {
            when (it) {
                is Int -> Value.newBuilder().setNumberValue(it.toDouble()).build()
                is Long -> Value.newBuilder().setNumberValue(it.toDouble()).build()
                is Float -> Value.newBuilder().setNumberValue(it.toDouble()).build()
                is Double -> Value.newBuilder().setNumberValue(it).build()
                is String -> Value.newBuilder().setStringValue(it).build()
                is LocalDate -> Value.newBuilder().setStringValue(it.toString()).build()
                is Instant -> Value.newBuilder().setStringValue(it.toString()).build()
                is Array<*> -> Value.newBuilder().setListValue(encodeList(it)).build()
                is Map<*, *> -> Value.newBuilder().setStructValue(encodeObject(it)).build()
                else -> throw Exception()
            }
        }
    ).build()
}

fun encodeObject(value: Map<*, *>): Struct {
    return Struct.newBuilder().putAllFields(
        value
            .mapKeys { it.toString() }
            .mapValues {
                when (it.value) {
                    is Int -> Value.newBuilder().setNumberValue((it.value as Int).toDouble()).build()
                    is Long -> Value.newBuilder().setNumberValue((it.value as Long).toDouble()).build()
                    is Float -> Value.newBuilder().setNumberValue((it.value as Float).toDouble()).build()
                    is Double -> Value.newBuilder().setNumberValue((it.value as Double)).build()
                    is String -> Value.newBuilder().setStringValue((it.value as String)).build()
                    is LocalDate -> Value.newBuilder().setStringValue(it.toString()).build()
                    is Instant -> Value.newBuilder().setStringValue(it.toString()).build()
                    is Array<*> -> Value.newBuilder().setListValue(encodeList(it.value as Array<*>)).build()
                    is Map<*, *> -> Value.newBuilder().setStructValue(encodeObject(it.value as Map<*, *>)).build()
                    else -> throw Exception()
                }
            }
    ).build()
}
