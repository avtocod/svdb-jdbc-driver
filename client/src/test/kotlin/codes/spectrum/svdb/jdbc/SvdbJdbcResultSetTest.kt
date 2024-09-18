package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.ISvdbCursor
import codes.spectrum.svdb.model.v1.ColumnOuterClass
import codes.spectrum.svdb.model.v1.Queryresult
import codes.spectrum.svdb.model.v1.RecordOuterClass
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SvdbJdbcResultSetTest : FunSpec() {
    private fun getCursor(map: Map<String, SvdbJdbcParameter>) = object : ISvdbCursor {
        override suspend fun fetch(): Queryresult.QueryResult {
            return mapToQueryResult(map)
        }

        override fun getSessionUid(): String = "stub"
        override fun getCursorUid(): String = "stub"
        override fun cancel() = Unit
        override fun close() = Unit
    }

    private fun mapToQueryResult(map: Map<String, SvdbJdbcParameter>): Queryresult.QueryResult {
        val query = Queryresult.QueryResult.newBuilder()
        map.forEach { (k, p) ->
            val column =  ColumnOuterClass.Column.newBuilder()
            column.setCode(k)
            column.setDataType(p.dataType)
            query.addColumns(column)
        }
        val record = RecordOuterClass.Record.newBuilder()
        map.forEach { (_, p) ->
            record.addFields(tryNewByteValue(p.value, p.dataType))
        }
        query.addRecords(record)
        return query.build()
    }

    init {
        context("getSting") {
            test("Проверяем получение строк как строк") {
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "Hello"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "Hello2"))
                )
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getString(1) shouldBe "Hello"
                    it.getString("first") shouldBe "Hello"
                    it.getString(2) shouldBe "Hello2"
                    it.getString("second") shouldBe "Hello2"
                }
            }

            test("Проверяем получение инта из строки") {
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, Long.MAX_VALUE.toString()))
                )
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getInt(1) shouldBe 2
                    it.getInt("first") shouldBe 2
                    it.getInt(2) shouldBe 2147483647
                }
            }
        }

        context("Приведения числовых типов") {
            test("целочисленные типы из double и float как отрезание дробной части") {
                val cursor =
                    getCursor(mapOf(
                        "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, 22.043F),
                        "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, 233.001),
                        "long" to SvdbJdbcParameter(ColumnOuterClass.DataType.FLOAT, Long.MAX_VALUE + 0.5542))
                    )
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
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "True"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "1"),
                    "third" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "false"),
                    "fourth" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, " "))
                )
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
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.INT, 1),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.INT, 0))
                )
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
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2022-10-01"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2023-03-31T12:45:14"))
                )
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getDate(1)?.toLocalDate() shouldBe LocalDate.of(2022, 10, 1)
                    it.getDate("first")?.toLocalDate() shouldBe LocalDate.of(2022, 10, 1)
                    it.getDate(2)?.toLocalDate() shouldBe LocalDate.of(2023, 3, 31)
                    it.getDate("second")?.toLocalDate() shouldBe LocalDate.of(2023, 3, 31)
                }
            }

            test("Получение time из строки с датой и с датой и временем") {
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2022-10-01"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2023-03-31T12:45:14"))
                )
                SvdbJdbcResultSet(cursor).also {
                    it.next()
                    it.getTime(1)?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTime("first")?.time shouldBe LocalDateTime.of(2022, 10, 1, 0, 0, 0).getEpochMillis()
                    it.getTime(2)?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                    it.getTime("second")?.time shouldBe LocalDateTime.of(2023, 3, 31, 12, 45, 14).getEpochMillis()
                }
            }

            test("Получение timestamp из строки с датой и временем") {
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2022-10-01"),
                    "second" to SvdbJdbcParameter(ColumnOuterClass.DataType.STRING, "2023-03-31T12:45:14"))
                )
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
                val cursor = getCursor(mapOf(
                    "first" to SvdbJdbcParameter(ColumnOuterClass.DataType.INT, millis))
                )
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
