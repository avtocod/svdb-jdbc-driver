package codes.spectrum.commons.transformers

import codes.spectrum.commons.*
import kotlin.reflect.KClass

interface IHosted<T> {
    /**
     * Установить хост для данного объекта
     * по умолчанию пустой для простой реализации
     */
    fun applyHost(host: T) {}
}


abstract class EffectiveTransformerBase : IEffectiveTransformerExtension {
    private var specialHost: IEffectiveTransformerService? = null
    protected val host = specialHost ?: Effective

    /**
     * Устанавливает хост-сервис для данного расширения
     *
     * @param host
     */
    override fun applyHost(host: IEffectiveTransformerService) {
        this.specialHost = host
    }

    protected object NotResolved

    protected open fun resolveFinal(
        source: Any?,
        targetClazz: KClass<*>,
        description: TransformationDescription?,
        strict: Boolean
    ): Any {
        return NotResolved
    }

    protected open fun resolveSubproduct(
        source: Any?,
        targetClazz: KClass<*>,
        description: TransformationDescription?,
        strict: Boolean
    ): Any? {
        return NotResolved
    }

    /**
     * Преобразует переданный [source] в объект типа [targetClazz]
     * или формирует одно из исключений [EffectiveException]
     */
    @Suppress("TooGenericExceptionCaught")
    override fun <R : Any> transfomTo(
        source: Any?,
        targetClazz: KClass<R>,
        description: TransformationDescription?,
        strict: Boolean
    ): EffectiveTransformResult {
        return try {
            val finalValue = resolveFinal(source, targetClazz, description, strict)
            if (finalValue != NotResolved) {
                EffectiveTransformResult(
                    result = finalValue,
                )
            } else {
                val subProduct = resolveSubproduct(source, targetClazz, description, strict)
                if (subProduct != NotResolved) {
                    EffectiveTransformResult(
                        isSubproduct = true,
                        subProduct = subProduct
                    )
                } else {
                    EffectiveTransformResult()
                }
            }
        } catch (e: Throwable) {
            EffectiveTransformResult(error = e)
        }.apply {
            this.source = source
            this.description = description
            this.targetClazz = targetClazz
            this.strict = strict
            this.finalize()
        }
    }
}
