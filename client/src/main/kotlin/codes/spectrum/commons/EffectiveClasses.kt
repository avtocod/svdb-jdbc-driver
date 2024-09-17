package codes.spectrum.commons

import kotlin.reflect.KClass

interface IEffectiveNullable {
    /**
     * Преобразует [source] в объект типа [targetClazz]
     * @return преобразованный объект или null
     */
    fun <R : Any> transformToOrNull(source: Any?, targetClazz: KClass<R>, strict: Boolean): R?
}

interface IEffectiveTransformer : IEffectiveNullable {
    /** Выполняет преобразование и возвращает аннотированный результат */
    fun <R : Any> transfomTo(
        source: Any?,
        targetClazz: KClass<R>,
        description: TransformationDescription?,
        strict: Boolean
    ): EffectiveTransformResult

    /** Выполняет преобразование и возвращает или результат или null если результат не успешен */
    override fun <R : Any> transformToOrNull(source: Any?, targetClazz: KClass<R>, strict: Boolean): R? =
        transfomTo(source, targetClazz, null, strict).takeIf { it.isSuccess }?.result.uncheckedOrNull()
}

data class EffectiveTransformResult(
    /** Значение на входе */
    var source: Any? = null,
    /** Итоговое значение */
    var result: Any? = null,
    /** Признак того, что приведение успешное */
    var isSuccess: Boolean = false,
    /** Целевой класс */
    var targetClazz: KClass<*> = Any::class,
    /** Признак того, что обработка завершена лишь частично */
    var isSubproduct: Boolean = false,
    /** Результат частичной обработки */
    var subProduct: Any? = null,
    /** Ошибка при трансформации */
    var error: Throwable? = null,
    /** Ссылка на описатель преобразования для отладки */
    var description: TransformationDescription? = null,
    /** Признак ограниченного преобразования */
    var strict: Boolean = true
) {
    /** Ссылка на предыдущий элемент для отладки */
    var previous: EffectiveTransformResult? = null
    /** Ссылка на цепь трансформаторов */
    var chain: List<TransformationDescription>? = null
    /** Завершает подготовку результата - вычисляет финальный статус [isSuccess] */
    fun finalize() { isSuccess = (isSuccess || (isSubproduct || result != null)) && (error == null) }
}

data class TransformationDescription(
    /** Признак поддержки null на входе */
    val supportsNullOnSource: Boolean = false,
    /** Класс для обработки на входе */
    val sourceClasses: List<KClass<*>> = emptyList(),
    /** Класс на входе - основание иерархии */
    val sourceIsHierarchyRoot: Boolean = true,
    /** Какие классы могут создаваться на выходе */
    val targetClasses: List<KClass<*>> = emptyList(),
    /** Использовать и для субклассов */
    val targetsAreHierarchyRoots: Boolean = true,
    /** Приоритет трансформации при конфликтующих вариантах */
    val priorityBase: Int = BASE_PRIORITY,
    /** Ссылка на реальный обработчик */
    val handler: IEffectiveTransformer
) : IEffectiveTransformer by handler {
    private val isAnySource: Boolean by lazy {
        sourceClasses.any { it == Any::class && sourceIsHierarchyRoot }
    }

    private val isAnyTarget: Boolean by lazy {
        targetClasses.any { it == Any::class && targetsAreHierarchyRoots }
    }

    /**
     * Вычисляет коэфициент "точности" правила
     * самое "выпуклое правило" имеет значение 1.0 и имеет такие характеристики:
     * 1. не поддерживает null
     * 2. на вход только 1 тип без дочерних
     * 3. на выход только 1 тип без дочерних
     * то есть это точечное преобразование одного типа в другой
     *
     * и самый низший коэфициент "точности" получит
     * 1. то что поддерживает null
     * 2. Any с дочерними на входе
     * 3. Any с дочерними на выходе
     */
    @Suppress("MagicNumber")
    val precession: Double by lazy {
        var result = 1.0
        if (supportsNullOnSource) {
            result -= 0.05
        }
        if (isAnySource) {
            result -= 0.2
        } else if (sourceClasses.size > 1 || sourceIsHierarchyRoot) {
            result -= 0.1
        }

        if (isAnyTarget) {
            result -= 0.2
        } else if (targetClasses.size > 1 || targetsAreHierarchyRoots) {
            result -= 0.1
        }
        result
    }

    /**
     * Определяет - соответствует ли данному дескриптору
     * трансформация класса [source] или null в [target]
     */
    fun matches(source: KClass<*>?, target: KClass<*>): Boolean {
        val sourceMatches by lazy {
            when {
                source == null -> supportsNullOnSource
                isAnySource -> true
                sourceIsHierarchyRoot -> sourceClasses.any { it.java.isAssignableFrom(source.java) }
                else -> source in sourceClasses
            }
        }
        val targetMatches by lazy {
            when {
                isAnyTarget -> true
                targetsAreHierarchyRoots -> targetClasses.any { it.java.isAssignableFrom(target.java) }
                else -> target in targetClasses
            }
        }
        return sourceMatches && targetMatches
    }

    /**
     * Общая приоритетность трансформации,
     * рассчитывается из явно переданной оценки [priorityBase]
     * и рассчетной [precession] с нормализацией [PRECESSION_OFFSET_LIMIT]
     */
    val priority by lazy { priorityBase + precession * PRECESSION_OFFSET_LIMIT }

    companion object {
        /** База расчета приоритета */
        const val BASE_PRIORITY = 10000

        /** Константа низкого приоритета для трансформации */
        const val LOW_PRIORITY = BASE_PRIORITY - 5000

        /** Константа высокого приоритета при трансформации */
        const val HIGH_PRIORITY = BASE_PRIORITY + 5000

        /** Корректор на основе выпуклости для приоритета */
        private const val PRECESSION_OFFSET_LIMIT = 500
    }
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Any?.uncheckedOrNull(): T? = this as? T

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "ALL")
inline fun <T> Any?.unchecked(): T = this as T

data class TransformKey(val source: KClass<*>?, val target: KClass<*>)

interface IEffectiveTransformerExtension :
    IEffectiveTransformer{
    /**
     * Возвращает набор поддерживаемых трансформаций
     */
    fun getSupportedTransformations(): List<TransformationDescription>
}

interface IEffectiveTransformerService : IEffective {
    /**
     * Перечень преобразователей
     */
    val extensions: List<IEffectiveTransformerExtension>

    /**
     * Метод для получения пояснения о логике трансформации
     */
    fun <R : Any> describe(
        source: Any?,
        targetClazz: KClass<R>,
        excludeHandlers: MutableList<IEffectiveTransformer> = mutableListOf(),
        strict: Boolean = true
    ): EffectiveTransformResult
}

interface IEffective : IEffectiveNullable {
    /**
     * Преобразует переданный [source] в объект типа [targetClazz]
     * или формирует одно из исключений [EffectiveException]
     */
    fun <R : Any> transformTo(source: Any?, targetClazz: KClass<R>, strict: Boolean): R
}
