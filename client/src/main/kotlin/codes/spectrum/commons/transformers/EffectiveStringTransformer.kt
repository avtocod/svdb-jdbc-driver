package codes.spectrum.commons.transformers

import codes.spectrum.commons.TransformationDescription
import kotlin.reflect.KClass

internal object EffectiveStringTransformer : EffectiveTransformerBase() {
    override fun getSupportedTransformations(): List<TransformationDescription> {
        return listOf(
            TransformationDescription(
                supportsNullOnSource = true,
                sourceClasses = listOf(Any::class),
                sourceIsHierarchyRoot = true,
                targetClasses = listOf(String::class),
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
        return when {
            source == null -> ""
            else -> source.toString()
        }
    }
}
