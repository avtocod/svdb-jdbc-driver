package codes.spectrum.commons

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


/**
 * Временная зона по умолчанию, унифицирует все форматирование дат,
 * а также приведения между объектами с указанием таймзоны и локальными объектами
 * дат, по умолчанию `Europe/Moscow`, может быть перекрыто из ENV SYSTEM_TIMEZONE
 * */
val DEFAULT_ZONE_ID = ZoneId.of(
    System.getenv().getOrDefault("SYSTEM_TIMEZONE", "Europe/Moscow")
)
/**
 * Временное смещение по умолчанию, унифицирует все форматирование дат,
 * а также приведения между объектами с указанием таймзоны и локальными объектами
 * дат, по умолчанию `Europe/Moscow`, может быть перекрыто из ENV SYSTEM_TIMEZONE
 * */
val DEFAULT_ZONE_OFFSET = DEFAULT_ZONE_ID.rules.getOffset(Instant.now())

/**
 * Формат даты времени для сериализации и десериализации по умолчанию
 */
const val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

/**
 * Формат даты времени для сериализации Instant (высокого разрешения)
 */
const val DEFAULT_INSTANT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX"



/**
 * Минимальный год, используемый в реальных сведениях
 */
const val MIN_YEAR = 1800

/**
 * Максимальный год, используемый в реальных сведениях
 */
const val MAX_YEAR = 3000

/**
 * Стандартный формат даты по ISO
 */
val ISO_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

/**
 * Логический null для [LocalDate] в позиции "от"
 */
val MIN_NULL_LOCAL_DATE = LocalDate.of(MIN_YEAR, 1, 1)

/**
 * Логический null для [LocalDate] в позиции "до"
 */
val MAX_NULL_LOCAL_DATE = LocalDate.of(MAX_YEAR, 1, 1)

/**
 * Логический null для [LocalDateTime] в позиции "от"
 */
val MIN_NULL_LOCAL_DATE_TIME = LocalDateTime.of(MIN_YEAR, 1, 1, 0, 0, 0)

/**
 * Логический null для [LocalDateTime] в позиции "до"
 */
val MAX_NULL_LOCAL_DATE_TIME = LocalDateTime.of(MAX_YEAR, 1, 1, 0, 0, 0)

/**
 * Логический null для [Date] в позиции "от"
 */
val MIN_NULL_DATE = ISO_DATE_FORMATTER.parse("1800-01-01T00:00:00.000+0000")

/**
 * Логический null для [Date] в позиции "до"
 */
val MAX_NULL_DATE = ISO_DATE_FORMATTER.parse("3000-01-01T00:00:00.000+0000")

/**
 * Логический null для [Instant] в позиции "от"
 */
val MIN_NULL_INSTANT = Instant.parse("1800-01-01T00:00:00.000Z")

/**
 * Логический null для [Instant] в позиции "до"
 */
val MAX_NULL_INSTANT = Instant.parse("3000-01-01T00:00:00.00Z")

/**
 * Реализация старого сериализатора за исключением части,
 * связанной непосредственно с GSON
 * и с максимальным переключением на московское время
 */
@Suppress("UnusedPrivateProperty")
class Dates(
    private val input_formats: List<DateTimeFormatter> = listOf(
        DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.MICRO_OF_SECOND, 1, POSTGRES_MICRO_SIGNES, false)
            .optionalEnd()
            .appendPattern("X[':00']")
            .toFormatter().withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[X]").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendPattern("[-]MM[-]dd[' ']['T'][HH][:][mm][:][ss][.][SSS][X]")
            .toFormatter().withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("d.MM.yyyy").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("d.M.yyyy").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("dd.M.yyyy").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm:ss").withZone(DEFAULT_ZONE_ID),
        DateTimeFormatter.ofPattern("MM/dd/yy").withZone(DEFAULT_ZONE_ID)
    ),
    private val pg_input_formats: List<DateTimeFormatter> = listOf(
        DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.MICRO_OF_SECOND, 1, POSTGRES_MICRO_SIGNES, false)
            .optionalEnd()
            .optionalStart()
            .appendPattern("X[':00']")
            .optionalEnd()
            .toFormatter().withZone(DEFAULT_ZONE_ID),
        DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd' 'HH:mm:ss")
            .optionalStart()
            .appendPattern(".")
            .appendFraction(ChronoField.MICRO_OF_SECOND, 1, POSTGRES_MICRO_SIGNES, false)
            .optionalEnd()
            .toFormatter(),
        DateTimeFormatter.ofPattern("dd.MM.yyyy' 'HH:mm[:][ss]").withZone(DEFAULT_ZONE_ID)
    )
)  {


    @Suppress("ALL")
    private fun DateTimeFormatter.parseOrNull(str: String): Instant? {
        return try {
            val temporal = this.parse(str)
            temporal.toInstant()
        } catch (e: Throwable) {
            null
        }
    }

    private fun inputFormatters() = sequence<DateTimeFormatter> {
        yield(STANDARD_INSTANT_FORMAT)
        yieldAll(input_formats)
        yieldAll(pg_input_formats)
    }

    @Suppress("ALL")
    fun parse(string: String): Instant {
        return inputFormatters().map {
            it.parseOrNull(string)
        }.firstOrNull { it != null }
            ?: throw RuntimeException("не поддерживаемый формат даты-времени [$string]")
    }

    companion object  {
        /**
         * Количество цифр с фракциях секунд постгреса
         */
        private const val POSTGRES_MICRO_SIGNES = 6
        private val Default by lazy { Dates() }

        /**
         * Парсит [Instant] из переданной строки
         * @param string строка, содержащая дату в любом из поддерживаемх форматов
         */
        fun parse(string: String): Instant = Default.parse(string)

        /**
         * Вычитаение Instant
         */
        operator fun Instant.minus(other: Instant) : kotlin.time.Duration =
            (this.toEpochMilli() - other.toEpochMilli()).milliseconds

        /**
         * Операция сложения [Instant] и [kotlin.time.Duration]
         */
        operator fun Instant.plus(d: kotlin.time.Duration) : Instant = this.plus(d.inWholeNanoseconds, ChronoUnit.NANOS)

        /**
         * Операция вычитания [kotlin.time.Duration] из [Instant]
         */
        operator fun Instant.minus(d: kotlin.time.Duration) : Instant =      this.minus(d.inWholeNanoseconds, ChronoUnit.NANOS)
    }
}


val STANDARD_INSTANT_FORMAT =  DateTimeFormatter.ofPattern(DEFAULT_INSTANT_FORMAT)
    .withZone(DEFAULT_ZONE_ID)

fun TemporalAccessor.toInstant(): Instant {
    return if (isSupported(ChronoField.YEAR_OF_ERA) && isSupported(ChronoField.INSTANT_SECONDS)) {
        Instant.from(this)
    } else {
        var instant = Duration.ofSeconds(
            if (isSupported(ChronoField.INSTANT_SECONDS)) {
                getLong(ChronoField.INSTANT_SECONDS)
            } else {
                val date = LocalDate.from(this).atStartOfDay()
                date.toEpochSecond(DEFAULT_ZONE_OFFSET)
            }
        ).toMillis()
        if (isSupported(ChronoField.MILLI_OF_SECOND)) {
            instant += get(ChronoField.MILLI_OF_SECOND)
        }
        return Instant.ofEpochMilli(instant)
    }
}
