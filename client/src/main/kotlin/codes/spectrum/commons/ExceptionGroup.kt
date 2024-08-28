package codes.spectrum.commons


/**
 * Исключение для обертывания группы событий
 */
class ExceptionGroup(
    message: String,
    /**
     * Вложенные исключения
     */
    val exceptions: List<Throwable>,
) : Exception(message) {

    /**
     * Слить две группы исключений в одну
     */
    infix fun combineWithGroup(second: ExceptionGroup) = ExceptionGroup(
        message = SEVERAL_ERRORS, // TODO: или строить комбинацию из двух сообщений?
        exceptions = exceptions + second.exceptions
    )

    /**
     * Добавить одно исключение в конец группы
     */
    infix fun addLast(error: Throwable) =
        addToGroup(exceptions + error)

    /**
     * Добавить одно исключение в начало группы
     */
    infix fun addFirst(error: Throwable) =
        addToGroup(exceptions.toMutableList().apply { add(0, error) })

    private fun addToGroup(errors: List<Throwable>) = ExceptionGroup(
        message = message.orEmpty(),
        exceptions = errors.distinct()
    )

    companion object {

        /**
         * Обрабатывает коллекции ошибок и формирует эффективное исклюение
         * 1. пустая коллекция - null
         * 2. коллекция из одного - это исключение
         * 3. больше - редукция через [ExceptionGroup.resolve(one, two)]
         */
        fun resolve(errors: Collection<Throwable>): Throwable? {
            return when {
                errors.isEmpty() -> null
                errors.size == 1 -> errors.elementAt(0)
                else -> errors.reduce { acc, c -> resolve(acc, c)!! }
            }
        }

        /**
         * Компоновка исключений или групп исключений.
         * Реализует правила:
         * - null + null = null
         * - ex + null = ex
         * - null + ex = ex
         * - ex1 + ex2 = group(ex1, ex2)
         * - group(ex1, ex2) + ex3 = group(ex1, ex2, ex3)
         * - group(ex1, ex2) + ex1 = group(ex1, ex2)
         * - ex3 + group(ex1, ex2) = group(ex3, ex1, ex2)
         * - group(ex1, ex2) + group(ex3, ex4) = group(ex1, ex2, ex3, ex4)
         * @see Throwable.combineWith
         */
        fun resolve(current: Throwable?, newError: Throwable?): Throwable? {
            return when {
                current != null && newError != null -> {
                    when {
                        current is ExceptionGroup && newError is ExceptionGroup -> current combineWithGroup newError
                        current is ExceptionGroup -> current addLast newError
                        newError is ExceptionGroup -> newError addFirst current
                        else -> ExceptionGroup(
                            message = SEVERAL_ERRORS,
                            exceptions = listOf(current, newError)
                        )
                    }
                }

                current != null -> current
                newError != null -> newError
                else -> null
            }
        }

        /**
         * Формирует сводную групповую ошибку для группы ошибок,
         * с плоским разбором вложенных групп.
         * Если ошибок нет или она одна, запаковка не производится
         */
        fun resolve(message: String = "", errors: List<Throwable>): Throwable? {
            return when {
                errors.isEmpty() -> null
                errors.size == 1 -> errors[0]
                else -> {
                    val resolved = errors.flatMap { if (it is ExceptionGroup) it.exceptions else listOf(it) }
                    val digest = resolved.map { it::class.simpleName + ": " + it.message }
                        .joinToString("; ", prefix = " [", postfix = "]")
                    ExceptionGroup(message + digest, resolved)
                }
            }
        }

        private const val SEVERAL_ERRORS = "Несколько ошибок"
    }
}

/**
 * Компоновка исключений или групп исключений.
 * @see ExceptionGroup.resolve
 */
infix fun Throwable?.combineWith(other: Throwable?): Throwable? = ExceptionGroup.resolve(this, other)
