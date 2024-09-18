package codes.spectrum.commons

abstract class SpectrumExceptionBase(message: String, cause: Throwable?) : Exception(message, cause)


sealed class EffectiveException(message: String, cause: Throwable?) :
    SpectrumExceptionBase(message, cause) {

    /**
     * Возникает при полном отсутствии расширения [IEffectiveTransformerExtension],
     * которое поддерживало бы запрошенное преобразование
     */
    class NoHandler internal constructor(
        /**
         * Внутренний результат преобразования
         */
        val result: EffectiveTransformResult
    ) :
        EffectiveException(
            """
                Не найдена реализация для преобразования из 
                `${result.source?.javaClass?.name}` в `${result.targetClazz.java.name}`
            """.trimIndent(),
            null
        )

    /**
     * Ни один из обработчиков не смог трансформировать переданный класс в заданный
     */
    class CannotTransform internal constructor(
        /**
         * Внутренний результат преобразования
         */
        val result: EffectiveTransformResult
    ) :
        EffectiveException(
            """
                При наличии обработчика, не удалось преобразовать переданный 
                `${result.source?.javaClass?.name}` в `${result.targetClazz.java.name}`
            """.trimIndent(),
            null
        )

    /**
     * Обработка завершилась с ошибкой обработчика
     */
    class TransformerFail(
        /**
         * Внутренний результат преобразования
         */
        val result: EffectiveTransformResult
    ) :
        EffectiveException(
            """Последний в цепи обработчик `${result.description!!.handler}` завершил свою работу с ошибкой""",
            result.error
        )
}