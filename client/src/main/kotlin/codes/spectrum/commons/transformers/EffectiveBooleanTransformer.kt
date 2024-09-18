package codes.spectrum.commons.transformers

import codes.spectrum.commons.*
import java.util.*
import kotlin.reflect.KClass

internal object EffectiveBooleanTransformer : EffectiveTransformerBase() {
    override fun getSupportedTransformations(): List<TransformationDescription> {
        return listOf(
            TransformationDescription(
                supportsNullOnSource = true,
                sourceClasses = listOf(Any::class),
                sourceIsHierarchyRoot = true,
                targetClasses = listOf(Boolean::class),
                targetsAreHierarchyRoots = false,
                priorityBase = TransformationDescription.LOW_PRIORITY,
                handler = this
            )
        )
    }

    @Suppress("ComplexMethod")
    override fun resolveFinal(
        source: Any?,
        targetClazz: KClass<*>,
        description: TransformationDescription?,
        strict: Boolean
    ): Any {
        return when (source) {
            null -> false
            true -> true
            false -> false
            is String -> when (source.lowercase(Locale.getDefault())) {
                "true" -> true
                "false" -> false
                "t" -> true
                "f" -> false
                "yes" -> true
                "no" -> false
                "y" -> true
                "n" -> false
                "1" -> true
                "0" -> false
                "{}" -> false
                "[]" -> false
                "0.0" -> false
                else -> {
                    if (source.toDoubleOrNull() != null) source.toDouble() != 0.0
                    source.isNotBlank()
                }
            }
            0 -> false
            0L -> false
            0.0 -> false
            Double.NaN -> false
            Double.NEGATIVE_INFINITY -> false
            Double.POSITIVE_INFINITY -> true
            MIN_NULL_DATE -> false
            MAX_NULL_DATE -> false
            MIN_NULL_LOCAL_DATE -> false
            MAX_NULL_LOCAL_DATE -> false
            MIN_NULL_LOCAL_DATE_TIME -> false
            MAX_NULL_LOCAL_DATE_TIME -> false
            MIN_NULL_INSTANT -> false
            MAX_NULL_INSTANT -> false
            else -> true
        }
    }
}
