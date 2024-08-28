package codes.spectrum.svdb.jdbc

import codes.spectrum.withSvdbServer
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.*
import java.time.Instant
import java.time.LocalDate
import java.util.*
import java.util.Date


class SvdbDriverDatamodeV2Test : FunSpec() {
    init {
        context("Внешний контекст для поднятия инстанса svdb") {
            withSvdbServer {
                val driver: Driver = SvdbJdbcDriver()

                context("query with statement") {
                    test("Проверяем количество") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery("select * from demo.inns").use {
                                    var cnt = 0
                                    while (it.next()) {
                                        cnt++
                                    }
                                    cnt shouldBe 3
                                }
                            }
                        }
                    }

                    test("Проверяем получение данных первую колонку по индексу") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery("select * from demo.inns").use {
                                    sequence {
                                        while (it.next()) {
                                            yield(it.getString(1) to it.getString(2))
                                        }
                                    }.toList().also {
                                        it[0].first shouldBe "859556348020"
                                        it[0].second shouldBe "4126289158"

                                        it[1].first shouldBe "194032337484"
                                        it[1].second shouldBe "4487437026"

                                        it[2].first shouldBe "033493024890"
                                        it[2].second shouldBe "4714657736"
                                    }
                                }
                            }
                        }
                    }

                    test("Проверяем получение данных первую колонку по лейблу") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery("select * from demo.inns").use {
                                    sequence {
                                        while (it.next()) {
                                            yield(it.getString("inn") to it.getString("pass"))
                                        }
                                    }.toList().also {
                                        it[0].first shouldBe "859556348020"
                                        it[0].second shouldBe "4126289158"

                                        it[1].first shouldBe "194032337484"
                                        it[1].second shouldBe "4487437026"

                                        it[2].first shouldBe "033493024890"
                                        it[2].second shouldBe "4714657736"
                                    }
                                }
                            }
                        }
                    }

                    test("Проверяем метаданные для resultSet") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery("select * from demo.inns").use { resultSet ->
                                    resultSet.metaData.also {
                                        it.columnCount shouldBe 2
                                        it.getColumnLabel(1) shouldBe "inn"
                                        it.getColumnLabel(2) shouldBe "pass"
                                    }

                                    var counter = 0
                                    while (resultSet.next()) {
                                        counter++
                                    }
                                    counter shouldBe 3
                                }
                            }
                        }
                    }

                    test("Проверяем булевы колонки") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery("SELECT is_null FROM sys.fields WHERE field_name = '_raw';")
                                resultSet.next().shouldBeTrue()
                                resultSet.getBoolean(1).shouldBeTrue()
                                resultSet.getBoolean("is_null").shouldBeTrue()
                            }
                        }
                    }

                    test("Проверяем получение массива") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery("select features from sys.plugins where name = 'demo'")
                                resultSet.next().shouldBeTrue()
                                resultSet.getObject(1).toString().shouldBe("[\"dataset-registry\",\"config-provider\"]")
                            }
                        }
                    }

                    test("Проверяем получение объекта") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery("select options from sys.tables where schema = 'sys' and table_name = 'fields'")
                                resultSet.next().shouldBeTrue()
                                resultSet.getObject(1).toString().shouldBe("{\"force-sync-all\":\"true\"}")
                            }
                        }
                    }

                    test("Проверяем преобразование типов с decimal") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery("select decimal(2.14), decimal(-2.14), 2.14;")
                                resultSet.next().shouldBeTrue()
                                resultSet.getDouble(1) shouldBe 2.14
                                resultSet.getDouble(2) shouldBe -2.14
                                resultSet.getBigDecimal(1) shouldBeWithScaleFive BigDecimal(2.14)
                                resultSet.getBigDecimal(2) shouldBeWithScaleFive BigDecimal(-2.14)
                                resultSet.getString(1) shouldBe "2.14"
                                resultSet.getString(2) shouldBe "-2.14"
                            }
                        }
                    }

                    test("Проверяем математическое выражение") {
                        // запрос с математическим выражением обрабатывается через datamode V1
                        val query = "select 1 + 1;"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery(query)
                                resultSet.next().shouldBeTrue()
                                resultSet.getInt(1) shouldBe 2
                            }
                        }
                    }

                    test("Проверяем математическое выражение с int и double") {
                        // запрос с математическим выражением обрабатывается через datamode V1
                        val query = "select 4 + 3.5;"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery(query)
                                resultSet.next().shouldBeTrue()
                                resultSet.getDouble(1) shouldBe 7.5
                            }
                        }
                    }

                    test("Проверяем запрос без результирующих записей") {
                        val query = "SELECT * FROM sys.fields WHERE field_name = 'полеполеполе'"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery(query)
                                // несмотря на то, что записей нет, метадата должна быть
                                resultSet.metaData.columnCount.shouldBe(22)
                                resultSet.next().shouldBeFalse()
                            }
                        }
                    }
                }

                context("Prepared Query") {
                    test("Проверяем запрос без параметров") {
                        getConnection(driver).use { connection ->
                            connection.prepareStatement("select * from demo.inns").use { preparedStatement ->
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next().shouldBeTrue()
                                resultSet.getString(1) shouldBe "859556348020"
                                resultSet.getString(2) shouldBe "4126289158"
                            }
                        }
                    }

                    test("Проверяем запрос c параметром") {
                        getConnection(driver).use { connection ->
                            val literal = "kkk"
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
                                    preparedStatement.setString(1, literal)
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()
                                    resultSet.getString(1) shouldBe literal
                                }
                        }
                    }

                    test("Проверяем запрос c 2 параметрами") {
                        getConnection(driver).use { connection ->
                            val first = 2
                            val second = 8

                            connection.prepareStatement("select \$1 + \$2").use { preparedStatement ->
                                // запрос с математическим выражением обрабатывается через datamode V1
                                preparedStatement.setInt(1, first)
                                preparedStatement.setInt(2, second)
                                var resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getInt(1) shouldBe (first + second)

                                // пробуем ещё раз
                                preparedStatement.setInt(1, 10)
                                preparedStatement.setInt(2, 20)
                                resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getInt(1) shouldBe (30)
                            }
                        }
                    }

                    test("Проверяем запрос c 2 параметрами и математической операцией") {
                        // запрос с математическим выражением обрабатывается через datamode V1
                        getConnection(driver).use { connection ->
                            val dec1 = "6.0"
                            val dec2 = "2.0"
                            connection.prepareStatement("select \$1 / \$2").use { preparedStatement ->
                                preparedStatement.setString(1, dec1)
                                preparedStatement.setString(2, dec2)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getInt(1) shouldBe 3
                            }
                        }
                    }

                    test("Проверяем запрос c 2 параметрами с передачей не по порядку") {
                        getConnection(driver).use { connection ->
                            val first = 2
                            val second = 8
                            connection.prepareStatement("select \$1, \$2").use { preparedStatement ->
                                preparedStatement.setInt(2, second)
                                preparedStatement.setInt(1, first)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getInt(1) shouldBe first
                                resultSet.getInt(2) shouldBe second
                            }
                        }
                    }

                    test("Проверяем запрос используя метод execute") {
                        // запрос с математическим выражением обрабатывается через datamode V1
                        getConnection(driver).use { connection ->
                            val first = 2
                            val second = 8
                            connection.prepareStatement("select \$1 + \$2").use { preparedStatement ->
                                preparedStatement.setInt(1, first)
                                preparedStatement.setInt(2, second)
                                shouldThrow<SQLException> { preparedStatement.resultSet }
                                preparedStatement.execute().shouldBeTrue()
                                val resultSet = preparedStatement.resultSet
                                resultSet.next()
                                resultSet.getInt(1) shouldBe (first + second)
                            }
                        }
                    }

                    test("Проверяем запрос c параметром int") {
                        getConnection(driver).use { connection ->
                            val literal = 8
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setInt(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getInt(1) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем запрос c параметром bool") {
                        getConnection(driver).use { connection ->
                            val literal = true
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setBoolean(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getBoolean(1) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем запрос c параметром BigDecimal") {
                        getConnection(driver).use { connection ->
                            val literal = BigDecimal(12.001)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setBigDecimal(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getBigDecimal(1) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем запрос c параметром LocalDate") {
                        getConnection(driver).use { connection ->
                            val localDate = LocalDate.of(2022, 4, 1)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setDate(1, java.sql.Date.valueOf(localDate))
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getDate(1).toLocalDate() shouldBe localDate
                            }
                        }
                    }

                    test("Проверяем запрос c параметром LocalDate с передачей как object") {
                        getConnection(driver).use { connection ->
                            val localDate = LocalDate.of(2022, 4, 1)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setObject(1, localDate)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getDate(1).toLocalDate() shouldBe localDate
                            }
                        }
                    }

                    test("Проверяем запрос c параметром Time") {
                        getConnection(driver).use { connection ->
                            val literal = Time(System.currentTimeMillis())
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setTime(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getTime(1) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем запрос c параметром Timestamp") {
                        getConnection(driver).use { connection ->
                            val literal = Timestamp(System.currentTimeMillis())
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setTimestamp(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getTimestamp(1) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем запрос c параметром Instant передача как object") {
                        getConnection(driver).use { connection ->
                            val literal = Instant.ofEpochMilli(Date().time)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setObject(1, literal)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                Instant.ofEpochMilli(resultSet.getTime(1).time) shouldBe literal
                            }
                        }
                    }

                    test("Проверяем передачу массива как параметра") {
                        getConnection(driver).use { connection ->
                            val literal = arrayOf("frist", "second")
                            val sqlArray = connection.createArrayOf("text", literal)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setArray(1, sqlArray)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getArray(1).array shouldBe literal
                            }
                        }
                    }

                    test("Проверяем передачу двумерного массива как параметра") {
                        getConnection(driver).use { connection ->
                            val literal = arrayOf(arrayOf("frist", "second"), arrayOf("third"))
                            val sqlArray = connection.createArrayOf("text", literal)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setArray(1, sqlArray)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                resultSet.getArray(1).array shouldBe literal
                            }
                        }
                    }

                    test("Проверяем, что целочисленные значения в массиве не содержат дробных частей") {
                        getConnection(driver).use { connection ->
                            val literal = arrayOf(1, 2, 3)
                            val sqlArray = connection.createArrayOf("test", literal)
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setArray(1, sqlArray)
                                val resultSet = preparedStatement.executeQuery()
                                resultSet.next()
                                // При сравнении c arrayOf(1, 2, 3) приводит double к int, что не показательно
                                resultSet.getArray(1).array shouldNotBe arrayOf(1.0, 2.0, 3.0)
                            }
                        }
                    }

                    test("Проверяем запрос c параметром который не поддерживается") {
                        getConnection(driver).use { connection ->
                            val literal = arrayListOf("param")
                            shouldThrow<SQLException> {
                                connection.prepareStatement("select \$1").use { preparedStatement ->
                                    preparedStatement.setObject(1, literal)
                                    preparedStatement.executeQuery()
                                }
                            }
                        }
                    }

                    test("Проверяем, что есть восстановление если не будет uid для prepare query") {
                        getConnection(driver).use { connection ->
                            val literal = "kkk"
                            connection.prepareStatement("select \$1").use { preparedStatement ->
                                preparedStatement.setString(1, literal)
                                preparedStatement.executeQuery().also {
                                    it.next()
                                    it.getString(1) shouldBe literal
                                }
                                // Подменяем uid, имитируем разрыв соединения
                                (preparedStatement as SvdbJdbcPreparedStatement)::class.java.getDeclaredField("uid")
                                    .also {
                                        it.isAccessible = true
                                        it.set(preparedStatement, "anotherUid")
                                    }
                                // выполнение все еще возможно
                                preparedStatement.executeQuery().also {
                                    it.next()
                                    it.getString(1) shouldBe literal
                                }
                            }
                        }
                    }
                }

                context("Проверяем таймаут запросов соединения") {
                    test("Таймаут при долгом запросе") {
                        shouldThrow<SQLTimeoutException> {
                            getConnection(driver, timeout = 1).createStatement()
                                .executeQuery("select * from debug.long")
                                .also {
                                    while (it.next()) {
                                    }
                                }
                        }
                    }

                    test("Таймаут при долгом запросе c prepare statement") {
                        shouldThrow<SQLTimeoutException> {
                            getConnection(driver, timeout = 1).prepareStatement("select * from debug.long")
                                .use { preparedStatement ->
                                    val resultSet = preparedStatement.executeQuery()
                                    while (resultSet.next()) {
                                    }
                                }
                        }
                    }

                    test("Быстрый запрос, укладываемся в таймаут") {
                        shouldNotThrowAny {
                            getConnection(driver, timeout = 1).use { connection ->
                                connection.createStatement().use {
                                    it.executeQuery("select * from demo.inns").use { resultSet ->
                                        while (resultSet.next()) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getConnection(
        driver: Driver,
        url: String = "$SVDB_TEST_HOST:$SVDB_TEST_PORT",
        timeout: Long = 0,
    ): Connection =
        driver.connect(
            url,
            Properties().apply {
                put("user", SVDB_USERNAME)
                put("password", SVDB_PASSWORD)
                put("queryTimeout", timeout.toString())
            },
        )

    private infix fun BigDecimal.shouldBeWithScaleFive(other: BigDecimal) {
        this.setScale(5, RoundingMode.HALF_UP) shouldBe other.setScale(5, RoundingMode.HALF_UP)
    }

    companion object {
        const val SVDB_TEST_HOST = "127.0.0.1"
        const val SVDB_TEST_PORT = 50053
        const val SVDB_USERNAME = "admin"
        const val SVDB_PASSWORD = "123"
    }
}
