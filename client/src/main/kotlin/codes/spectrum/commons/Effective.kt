package codes.spectrum.commons

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

object Effective {
    fun boolean(obj: Any?) : Boolean {
        return when (obj) {
            null -> false
            is Boolean -> obj
            is Int -> obj > 0
            is Short -> obj > 0
            is Long -> obj > 0
            is Double -> obj > 0
            is Byte -> obj > 0
            is Float -> obj > 0
            is BigDecimal -> obj.longValueExact() > 0
            is String -> obj.isNotBlank() && obj != "false"
            is Date -> obj > MIN_NULL_DATE && obj < MAX_NULL_DATE
            is LocalDate -> obj > MIN_NULL_LOCAL_DATE && obj < MAX_NULL_LOCAL_DATE
            is LocalDateTime -> obj > MIN_NULL_LOCAL_DATE_TIME && obj < MAX_NULL_LOCAL_DATE_TIME
            is Instant -> obj > MIN_NULL_INSTANT && obj < MAX_NULL_INSTANT
            is List<*> -> obj.isNotEmpty()
            is Map<*,*> -> obj.isNotEmpty()
            else -> true
        }
    }

    fun int(obj: Any?) : Int {
        return long(obj).toInt()
    }
    fun long(obj: Any?) : Long {
        return when (obj) {
            null -> 0
            is Boolean -> if(obj) 1 else 0
            is Int -> obj.toLong()
            is Short -> obj.toLong()
            is Long -> obj.toLong()
            is Double -> obj.toLong()
            is Byte -> obj.toLong()
            is Float -> obj.toLong()
            is BigDecimal -> obj.toLong()
            is String -> obj.toLong()
            is Date -> obj.toInstant().toEpochMilli()
            is LocalDate -> (obj.toEpochSecond(LocalTime.MIN, DEFAULT_ZONE_OFFSET)*1000)
            is Instant -> obj.toEpochMilli()
            is List<*> -> obj.size.toLong()
            is Map<*,*> -> obj.size.toLong()
            else -> throw RuntimeException("cannot cast type [${obj::class}] to long")
        }
    }

    fun double(obj: Any?) : Double {
        return when (obj) {
            null -> 0.0
            is Double -> obj
            is String -> obj.toDouble()
            else -> long(obj).toDouble()
        }
    }

    fun instant(obj: Any?) : Instant {
        return when(obj) {
            null -> MIN_NULL_INSTANT
            is Instant -> obj
            is Date -> obj.toInstant()
            is LocalDate -> obj.toInstant()
            is LocalDateTime -> obj.toInstant()
            is String -> Dates.parse(obj)
            is Int -> Instant.ofEpochMilli(obj.toLong())
            is Long -> Instant.ofEpochMilli(obj)
            is Double -> Instant.ofEpochMilli(obj.toLong()).plusNanos((obj - obj.toLong()).toLong() * 1000000)
            is Byte -> Instant.ofEpochMilli(obj.toLong())
            is Float -> Instant.ofEpochMilli(obj.toLong()).plusNanos((obj - obj.toLong()).toLong() * 1000000)
            is BigDecimal ->Instant.ofEpochMilli(obj.toLong())
            else -> throw RuntimeException("cannot cast type [${obj::class}] to Instant")
        }
    }

    fun date(obj: Any?) : Date {
        if (obj == null){
            return MIN_NULL_DATE
        }
        return Date.from(instant(obj))
    }

    fun localDate(obj: Any?) : LocalDate {
        if (obj == null){
            return MIN_NULL_LOCAL_DATE
        }
        return LocalDate.from(instant(obj).atZone(DEFAULT_ZONE_ID))
    }
}
