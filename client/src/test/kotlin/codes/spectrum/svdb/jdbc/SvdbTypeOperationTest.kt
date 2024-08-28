package codes.spectrum.svdb.jdbc

import codes.spectrum.withSvdbServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.sql.Driver
import java.util.*

class SvdbTypeOperationTest : FunSpec() {
    init {
        context("Сложение, где должно быть преобразование к duration") {
            withSvdbServer {
                val driver: Driver = SvdbJdbcDriver()

                test("Операции с двумя duration") {
                    driver.connect("${SvdbDriverTest.SVDB_TEST_HOST}:${SvdbDriverTest.SVDB_TEST_PORT}",
                        Properties().apply {
                            put("user", SvdbDriverTest.SVDB_USERNAME)
                            put("password", SvdbDriverTest.SVDB_PASSWORD)
                        }).use { connection ->
                        connection.prepareStatement("select duration(\$1 + \$2 + \$3)").use { preparedStatement ->
                            preparedStatement.setString(1, "1h")
                            preparedStatement.setInt(2, 1000 * 60 * 60 * 2)
                            preparedStatement.setString(3, "3h")

                            val resultSet = preparedStatement.executeQuery()
                            resultSet.next()
                            resultSet.getString(1) shouldBe ("6h0m0s")
                        }
                    }
                }

                test("Сложение, где должно быть преобразование к date") {
                    driver.connect("${SvdbDriverTest.SVDB_TEST_HOST}:${SvdbDriverTest.SVDB_TEST_PORT}",
                        Properties().apply {
                            put("user", SvdbDriverTest.SVDB_USERNAME)
                            put("password", SvdbDriverTest.SVDB_PASSWORD)
                        }).use { connection ->
                        connection.prepareStatement("select datetime(\$1 + \$2 + \$3 + \$4)").use { preparedStatement ->
                            preparedStatement.setString(1, "1h")
                            preparedStatement.setString(2, "2020-03-04T14:15:16.123322178+03:00")
                            preparedStatement.setInt(3, 1000 * 60 * 60 * 2)
                            preparedStatement.setString(4, "3h")
                            val resultSet = preparedStatement.executeQuery()
                            resultSet.next()
                            resultSet.getString(1) shouldBe ("2020-03-04T20:15:16.123322178+03:00")
                        }
                    }
                }
            }
        }
    }
}
