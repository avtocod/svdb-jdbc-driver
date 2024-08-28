package codes.spectrum.svdb.jdbc

import java.time.Instant
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class UpdatableLazyDelegate<T>(
        /**
         * Время обновления 30 сек по умолчанию
         */
        private val refreshTime: Long = 30L * 1000L,
        locker: Any? = null,
        private var initializer: () -> T,
) : ReadOnlyProperty<Any, T> {
    @Volatile
    private var _value: T? = null

    @Volatile
    private var lastUpdate: Long = Instant.now().toEpochMilli()

    private val lock = locker ?: this

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return synchronized(lock) {
            val currentTime = Instant.now().toEpochMilli()
            if (_value == null || currentTime > lastUpdate + refreshTime) {
                _value = initializer()
                lastUpdate = currentTime
            }
            @Suppress("UNCHECKED_CAST")
            _value as T
        }
    }
}
