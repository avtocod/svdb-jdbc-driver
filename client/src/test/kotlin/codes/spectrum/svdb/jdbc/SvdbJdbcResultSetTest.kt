package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.FieldOuterClass
import codes.spectrum.svdb.model.v1.Queryresult
import codes.spectrum.svdb.model.v1.RecordOuterClass
import codes.spectrum.svdb.model.v1.ValueOuterClass
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import codes.spectrum.svdb.ISvdbCursor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SvdbJdbcResultSetTest : FunSpec() {
    private fun getCursor(map: Map<String, Any?>) = object : ISvdbCursor {
        override suspend fun fetch(): Queryresult.QueryResult {
            return mapToQueryResult(map)
        }

        override fun getSessionUid(): String = "stub"
        override fun getCursorUid(): String = "stub"
        override fun cancel() = Unit
        override fun close() = Unit
    }

    private fun mapToQueryResult(map: Map<String, Any?>): Queryresult.QueryResult {
        return Queryresult.QueryResult.newBuilder()
            .addRecords(
                RecordOuterClass.Record.newBuilder()
                    .also { record ->
                        map.entries.forEachIndexed { index, entry ->
                            record.addFields(
                                index,
                                FieldOuterClass.Field.newBuilder()
                                    .setCode(entry.key)
                                    .setValue(
                                        run {
                                            val valueBuilder = ValueOuterClass.Value.newBuilder()
                                            when (entry.value) {
                                                is String -> valueBuilder.setStr(entry.value as String)
                                                is Int -> valueBuilder.setI32(entry.value as Int)
                                                is Long -> valueBuilder.setI64(entry.value as Long)
                                                is Float -> valueBuilder.setF64((entry.value as Float).toDouble())
                                                is Double -> valueBuilder.setF64(entry.value as Double)
                                                is Boolean -> valueBuilder.setBit(entry.value as Boolean)
                                                else -> valueBuilder
                                            }
                                        }
                                    )
                            )
                        }
                    }
            ).build()
    }

    init {
        context("getSting") {
            test("Проверяем получение строк как строк") {
                val cursor = getCursor(mapOf("first" to "Hello", "second" to "Hello2"))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getString(1) shouldBe "Hello"
                    it.getString("first") shouldBe "Hello"
                    it.getString(2) shouldBe "Hello2"
                    it.getString("second") shouldBe "Hello2"
                }
            }

            test("Проверяем получение инта из строки") {
                val cursor = getCursor(mapOf("first" to "2", "second" to Int.MAX_VALUE.toString()))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getInt(1) shouldBe 2
                    it.getInt("first") shouldBe 2
                    it.getInt(2) shouldBe 2147483647
                }
            }

            test("Проверяем получение строки из инта, лонга") {
                val cursor = getCursor(mapOf("first" to 22, "second" to 23L))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getString(1) shouldBe "22"
                    it.getString("first") shouldBe "22"
                    it.getString(2) shouldBe "23"
                    it.getString("second") shouldBe "23"
                }
            }

            test("Проверяем получение строки из float, double") {
                val cursor = getCursor(mapOf("first" to 22.0F, "second" to 233.001))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getString(1) shouldBe "22.0"
                    it.getString("first") shouldBe "22.0"
                    it.getString(2) shouldBe "233.001"
                    it.getString("second") shouldBe "233.001"
                }
            }
        }

        context("Приведения числовых типов") {
            test("целочисленные типы из double и float как отрезание дробной части") {
                val cursor =
                    getCursor(mapOf("first" to 22.043F, "second" to 233.001, "long" to Long.MAX_VALUE + 0.5542))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getInt(1) shouldBe 22
                    it.getInt("first") shouldBe 22
                    it.getInt(2) shouldBe 233
                    it.getInt("second") shouldBe 233
                    it.getLong(3) shouldBe Long.MAX_VALUE
                    it.getLong("long") shouldBe Long.MAX_VALUE
                }
            }
        }

        context("getBoolean") {
            test("из строки") {
                val cursor = getCursor(mapOf("first" to "True", "second" to "1", "third" to "false", "fourth" to " "))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getBoolean(1).shouldBeTrue()
                    it.getBoolean("first").shouldBeTrue()
                    it.getBoolean(2).shouldBeTrue()
                    it.getBoolean("second").shouldBeTrue()
                    it.getBoolean(3).shouldBeFalse()
                    it.getBoolean(4).shouldBeFalse()
                }
            }

            test("из number") {
                val cursor = getCursor(mapOf("first" to 1, "second" to 0))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getBoolean(1) shouldBe true
                    it.getBoolean("first") shouldBe true
                    it.getBoolean(2) shouldBe false
                    it.getBoolean("second") shouldBe false
                }
            }
        }

        context("date") {
            test("Получение date из строки с датой и с датой и временем") {
                val cursor = getCursor(mapOf("first" to "2022-10-01", "second" to "2023-03-31T12:45:14"))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getDate(1)?.toLocalDate() shouldBe LocalDate.of(2022, 10, 1)
                    it.getDate("first")?.toLocalDate() shouldBe LocalDate.of(2022, 10, 1)
                    it.getDate(2)?.toLocalDate() shouldBe LocalDate.of(2023, 3, 31)
                    it.getDate("second")?.toLocalDate() shouldBe LocalDate.of(2023, 3, 31)
                }
            }

            test("Получение time из строки с датой и с датой и временем") {
                val cursor = getCursor(mapOf("first" to "2022-10-01", "second" to "2023-03-31T12:45:14"))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getTime(1)?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTime("first")?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTime(2)?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                    it.getTime("second")?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                }
            }

            test("Получение timestamp из строки с датой и временем") {
                val cursor = getCursor(mapOf("first" to "2022-10-01", "second" to "2023-03-31T12:45:14"))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getTimestamp(1)?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTimestamp("first")?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTimestamp(2)?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                    it.getTimestamp("second")?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                }
            }

            test("Получение даты из long") {
                val localDateTime = LocalDateTime.of(2022, 10, 1, 0, 0, 0)
                val millis = localDateTime.getEpochMillis()
                val cursor = getCursor(mapOf("first" to millis))
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    assertSoftly {
                        it.getTimestamp(1)?.time shouldBe millis
                        it.getTimestamp("first")?.time shouldBe millis
                        it.getTime(1)?.time shouldBe millis
                        it.getTime("first")?.time shouldBe millis
                        it.getDate(1)?.toLocalDate() shouldBe localDateTime.toLocalDate()
                        it.getDate("first")?.toLocalDate() shouldBe localDateTime.toLocalDate()
                    }
                }
            }
        }
    }

    private fun LocalDateTime.getEpochMillis() = ZonedDateTime.of(this, ZoneId.of("Europe/Moscow"))
        .toInstant().toEpochMilli()
}
