package codes.spectrum.svdb.jdbc

import codes.spectrum.withSvdbServer
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.*
import java.time.LocalDate
import java.util.*

class SvdbDriverTest : FunSpec() {
    init {
        context("Внешний контекст для поднятия инстанса svdb") {
            withSvdbServer {
                val driver: Driver = SvdbJdbcDriver()

                context("query with statement") {
                    test("Проверяем количество") {
                        val query = "select * from demo.inns"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery(query).use {
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
                        val query = "select * from demo.inns"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery(query).use {
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
                        val query = "select * from demo.inns"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery(query).use {
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
                        val query = "select * from demo.inns"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                statement.executeQuery(query).use { resultSet ->
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
                        val query = "SELECT is_null FROM sys.fields WHERE field_name = '_raw';"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery(query)
                                resultSet.next().shouldBeTrue()
                                resultSet.getBoolean(1).shouldBeTrue()
                                resultSet.getBoolean("is_null").shouldBeTrue()
                            }
                        }
                    }

                    test("Проверяем получение массива") {
                        val query = "SELECT * from sys.sessions with explicit_fields;"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery(query)
                                resultSet.next().shouldBeTrue()
                                resultSet.getObject(5)
                                resultSet.getObject("user_roles")
                            }
                        }
                    }

                    test("Проверяем преобразование типов с decimal") {
                        val query = "select decimal(2.14), decimal(-2.14), 2.14;"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery(query)
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

                    // больше на этом запросе данная ошибка не происходит поэтому актуальности нет у теста
                    test("!Проверяем, что ошибки в фетче являются ошибками и на драйвере") {
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet =
                                    statement.executeQuery("select * from fssp.direct where creditor_inn = '4909047148' and last_name  = 'ЛЕБЕДЕВ'")
                                resultSet.next().shouldBeTrue()
                                shouldThrowAny { resultSet.next() }
                            }
                        }
                    }

                    test("Проверяем математическое выражение") {
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
                        val query = "select 4 + 3.5;"
                        getConnection(driver).use { connection ->
                            connection.createStatement().use { statement ->
                                val resultSet = statement.executeQuery(query)
                                resultSet.next().shouldBeTrue()
                                resultSet.getDouble(1) shouldBe 7.5
                            }
                        }
                    }
                }

                context("Prepared Query") {
                    test("Проверяем запрос без параметров") {
                        val query = "select * from demo.inns;"
                        getConnection(driver).use { connection ->
                            connection.prepareStatement(query).use { preparedStatement ->
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
                            connection.prepareStatement("select \$1 + \$2")
                                .use { preparedStatement ->
                                    preparedStatement.setInt(1, first)
                                    preparedStatement.setInt(2, second)
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()
                                    resultSet.getInt(1) shouldBe (first + second)
                                }
                        }
                    }

                    test("Проверяем запрос c 2 параметрами с передачей не по порядку") {
                        getConnection(driver).use { connection ->
                            val first = 2
                            val second = 8
                            connection.prepareStatement("select \$1, \$2")
                                .use { preparedStatement ->
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
                        getConnection(driver).use { connection ->
                            val first = 2
                            val second = 8
                            connection.prepareStatement("select \$1 + \$2")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
                                    preparedStatement.setDate(1, java.sql.Date.valueOf(localDate))
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()
                                    resultSet.getDate(1).toLocalDate() shouldBe localDate
                                }
                        }
                    }

                    test("Проверяем запрос c параметром Time") {
                        getConnection(driver).use { connection ->
                            val literal = Time(System.currentTimeMillis())
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
                                    preparedStatement.setTimestamp(1, literal)
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()
                                    resultSet.getTimestamp(1) shouldBe literal
                                }
                        }
                    }

                    test("Проверяем запрос с null") {
                        getConnection(driver).use { connection ->
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
                                    preparedStatement.setNull(1, Types.VARCHAR)
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()

                                    assertSoftly {
                                        resultSet.getString(1).shouldBeNull()
                                        shouldThrowAny { resultSet.getString(2) }

                                        resultSet.getBoolean(1).shouldBeFalse()
                                        resultSet.getTime(1).shouldBeNull()
                                        resultSet.getTimestamp(1).shouldBeNull()
                                        resultSet.getDate(1).shouldBeNull()
                                        resultSet.getByte(1) shouldBe 0
                                        resultSet.getInt(1) shouldBe 0
                                        resultSet.getLong(1) shouldBe 0
                                        resultSet.getFloat(1) shouldBe 0F
                                        resultSet.getDouble(1) shouldBe 0.0
                                        resultSet.getBigDecimal(1).shouldBeNull()
                                        resultSet.getObject(1).shouldBeNull()
                                    }
                                }
                        }
                    }

                    test("Проверяем передачу массива как параметра") {
                        getConnection(driver).use { connection ->
                            val literal = arrayOf("frist", "second")
                            val sqlArray = connection.createArrayOf("text", literal)
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
                                    preparedStatement.setArray(1, sqlArray)
                                    val resultSet = preparedStatement.executeQuery()
                                    resultSet.next()
                                    resultSet.getArray(1).array shouldBe literal
                                }
                        }
                    }

                    test("Проверяем, что есть восстановление если не будет uid для prepare query") {
                        getConnection(driver).use { connection ->
                            val literal = "kkk"
                            connection.prepareStatement("select \$1")
                                .use { preparedStatement ->
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

                context("проверяем возможность соединения с различными вариантами строки подключения") {
                    test("c префиксом svdb://") {
                        SvdbJdbcDriver().also { driver ->
                            val url = "svdb://$SVDB_TEST_HOST:$SVDB_TEST_PORT"
                            driver.acceptsURL(url).shouldBeTrue()
                            shouldNotThrowAny {
                                getConnection(driver, url).use { }
                            }
                        }
                    }

                    test("c префиксом jdbc-svdb://") {
                        SvdbJdbcDriver().also { driver ->
                            val url = "jdbc-svdb://$SVDB_TEST_HOST:$SVDB_TEST_PORT"
                            driver.acceptsURL(url).shouldBeTrue()
                            shouldNotThrowAny {
                                getConnection(driver, url).use { }
                            }
                        }
                    }

                    test("без префикса") {
                        SvdbJdbcDriver().also { driver ->
                            val url = "$SVDB_TEST_HOST:$SVDB_TEST_PORT"
                            // метод возвращает false для того, что через пулы подключались с корректным префиксом
                            driver.acceptsURL(url).shouldBeFalse()
                            // но вручную создав драйвер мы можем подключится
                            shouldNotThrowAny {
                                getConnection(driver, url).use { }
                            }
                        }
                    }

                    test("c префиксом другой схемы не должен подключатся") {
                        SvdbJdbcDriver().also { driver ->
                            val url = "jdbc:postgresql://$SVDB_TEST_HOST:$SVDB_TEST_PORT"
                            driver.acceptsURL(url).shouldBeFalse()
                            shouldThrow<SQLException> {
                                getConnection(driver, url).use { }
                            }
                        }
                    }
                }

                context("проверяем парсинг URL") {
                    val svdbJdbcDriver = driver as SvdbJdbcDriver

                    test("хосты без схемы") {
                        svdbJdbcDriver.parseUrl("s-svdb-@4.spectrumdata.tech:443").also {
                            it.host shouldBe "s-svdb-@4.spectrumdata.tech"
                            it.port shouldBe 443
                        }

                        svdbJdbcDriver.parseUrl("127.0.0.1:443").also {
                            it.host shouldBe "127.0.0.1"
                            it.port shouldBe 443
                        }
                    }

                    test("хосты со схемой svdb") {
                        svdbJdbcDriver.parseUrl("svdb://s-svdb-@4.spectrumdata.tech:443").also {
                            it.host shouldBe "s-svdb-@4.spectrumdata.tech"
                            it.port shouldBe 443
                        }

                        svdbJdbcDriver.parseUrl("svdb://127.0.0.1:443").also {
                            it.host shouldBe "127.0.0.1"
                            it.port shouldBe 443
                        }
                    }

                    test("хосты со схемой jdbc-svdb") {
                        svdbJdbcDriver.parseUrl("jdbc-svdb://s-svdb-@4.spectrumdata.tech:443").also {
                            it.host shouldBe "s-svdb-@4.spectrumdata.tech"
                            it.port shouldBe 443
                        }

                        svdbJdbcDriver.parseUrl("jdbc-svdb://127.0.0.1:443").also {
                            it.host shouldBe "127.0.0.1"
                            it.port shouldBe 443
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
                            getConnection(
                                driver,
                                timeout = 1
                            ).prepareStatement("select * from debug.long")
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
                                    it.executeQuery("select * from demo.inns")
                                        .use { resultSet ->
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
