package codes.spectrum.commons.transformers

import codes.spectrum.commons.TransformationDescription
import codes.spectrum.commons.booleanOrDefault
import java.math.BigDecimal
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.reflect.KClass

internal object EffectiveNumberTransformer : EffectiveTransformerBase() {
    override fun getSupportedTransformations(): List<TransformationDescription> {
        return listOf(
            TransformationDescription(
                supportsNullOnSource = true,
                sourceClasses = listOf(Any::class),
                sourceIsHierarchyRoot = true,
                targetClasses = listOf(
                    Int::class,
                    Long::class,
                    Double::class,
                    Number::class,
                    BigDecimal::class
                ),
                targetsAreHierarchyRoots = false,
                priorityBase = TransformationDescription.LOW_PRIORITY,
                handler = this
            )
        )
    }

    override fun resolveFinal(
        source: Any?,
        targetClazz: KClass<*>,
        description: TransformationDescription?,
        strict: Boolean
    ): Any {
        return when (targetClazz) {
            Int::class -> int(source, strict)
            Long::class -> long(source, strict)
            Double::class -> double(source, strict)
            BigDecimal::class -> double(source, strict)?.toBigDecimal()
            else -> number(source, strict)
        } ?: NotResolved
    }

    @Suppress("ALL")
    private fun int(obj: Any?, strict: Boolean): Int? = when (obj) {
        null -> 0
        is Int -> obj
        is Double -> obj.roundToInt()
        is Number -> obj.toInt()
        false -> 0
        true -> 1
        (obj == "") -> 0
        (obj is String && obj.all { it.isDigit() }) -> obj.toString().toInt()
        // need because if we cast to Int directly - values as "42.38" will ignore
        is String -> when (val sys_parse_result = double(obj, strict)) {
            null -> if (strict) null else (if (host.booleanOrDefault(obj)) 1 else 0)
            else -> sys_parse_result.roundToInt()
        }
        else -> if (strict) null else (if (host.booleanOrDefault(obj)) 1 else 0)
    }

    @Suppress("ComplexMethod", "NestedBlockDepth")
    private fun long(obj: Any?, strict: Boolean): Long? = when {
        obj == null -> 0
        obj is Long -> obj
        obj is Double -> obj.roundToLong()
        obj is Number -> obj.toLong()
        obj == false -> 0
        obj == true -> 1
        obj == "" -> 0
        obj is String && obj.all { it.isDigit() } -> obj.toLong()
        obj is String && obj.matches(LONGREGEX) -> obj.dropLast(1).toLong()
        obj is String -> when (val sys_parse_result = double(obj, strict)) {
            null -> if (strict) null else (if (host.booleanOrDefault(obj)) 1L else 0L)
            else -> sys_parse_result.roundToLong()
        }
        else -> if (strict) null else (if (host.booleanOrDefault(obj)) 1L else 0L)
    }

    @Suppress("ComplexMethod", "NestedBlockDepth")
    private fun double(obj: Any?, strict: Boolean): Double? = when (obj) {
        null -> 0.0
        is Double -> obj
        is Number -> obj.toDouble()
        false -> 0.0
        true -> 1.0
        (obj == "") -> 0.0
        is String -> when (obj) {
            "NaN" -> Double.NaN
            "Infinity" -> Double.POSITIVE_INFINITY
            "+Infinity" -> Double.POSITIVE_INFINITY
            "-Infinity" -> Double.NEGATIVE_INFINITY
            else -> when (val sys_parse_result = obj.replace(",", ".").toDoubleOrNull()) {
                null -> if (strict) null else (if (host.booleanOrDefault(obj)) 1.0 else 0.0)
                else -> sys_parse_result
            }
        }
        else -> if (host.booleanOrDefault(obj)) 1.0 else 0.0
    }

    private fun getBestVariant(double: Double): Number {
        return if (double.isFinite()) {
            val long = double.toLong()
            if (long.toDouble() != double) {
                double
            } else {
                if (long > Int.MAX_VALUE || long < Int.MIN_VALUE) {
                    long
                } else {
                    long.toInt()
                }
            }
        } else double
    }

    private fun number(obj: Any?, strict: Boolean): Number? {
        return when {
            /* "123L" ->  123L */
            obj is String && obj.matches(LONGREGEX) -> long(obj.dropLast(1), strict)
            /* "1.2M" -> BigDecimal(1.2) */
            obj is String && obj.matches(DECIMALREGEX) -> double(obj.dropLast(1), strict)?.toBigDecimal()
            else -> double(obj, strict)?.let { getBestVariant(it) }
        }
    }

    private val LONGREGEX = """^-?\d+[Ll]$""".toRegex()
    private val DECIMALREGEX = """^-?\d+(\.\d+)?[Mm]$""".toRegex()
}