package codes.spectrum.svdb.jdbc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.sql.Driver
import java.sql.SQLException
import java.util.*

class SvdbJdbcConnectionTest : FunSpec() {
    init {
        val driver: Driver = SvdbJdbcDriver()
        // отключил тест, потому что он постоянно валится на живых данных из за того что данные меняются,
        // надо будет уточнить
        test("!Проверяем конструирование мета-данных") {
            val connect: SvdbJdbcConnection = driver.connect(
                "${SvdbDriverTest.SVDB_TEST_HOST}:${SvdbDriverTest.SVDB_TEST_PORT}",
                Properties().apply {
                    put("user", SvdbDriverTest.SVDB_USERNAME)
                    put("password", SvdbDriverTest.SVDB_PASSWORD)
                }) as SvdbJdbcConnection

            connect.svdbJdbcSysData.also { sysData ->
                sysData.catalogs.map { it.name } shouldContain listOf("system", "data")
                sysData.catalogs.firstOrNull { it.name == "system" }?.also { catalog ->
                    catalog.schemas.map { it.name } shouldContain listOf("sys")
                    catalog.schemas.flatMap { schema -> schema.tables.map { it.name } } shouldContain listOf(
                        "tables",
                        "fields",
                        "nodes",
                        "sessions",
                        "errors",
                        "logs"
                    )
                }.shouldNotBeNull()

                sysData.fields.shouldNotBeEmpty()
                sysData.fields shouldContain SvdbJdbcField(
                    catalog = "data",
                    schema = "fssp",
                    table = "source",
                    name = "first_name",
                    type_name = "STRING",
                    description = "имя",
                    position = 5,
                    isNull = true
                )
            }
        }

        test("Проверяем возврат последнего исключения, в случае когда их несколько") {
            val wrongHost = "s-svdb-svdbgo-4.spectrumdata.tech1"
            val wrongLogin = "wrongLogin"
            val wrongPass = "123"

            val err = shouldThrow<SQLException> {
                driver.connect(
                    "${wrongHost}:${SvdbDriverTest.SVDB_TEST_PORT}",
                    Properties().apply {
                        put("user", wrongLogin)
                        put("password", wrongPass)
                    }) as SvdbJdbcConnection
            }
            err.message shouldBe """
                io.grpc.StatusException: UNAVAILABLE: Unable to resolve host s-svdb-svdbgo-4.spectrumdata.tech1

                io.grpc.StatusException: UNAVAILABLE: Unable to resolve host s-svdb-svdbgo-4.spectrumdata.tech1


            """.trimIndent()

        }

        test("!Проверяем что сессии не плодятся").config(invocations = 1) {
            driver.connect(
                "${SvdbDriverTest.SVDB_TEST_HOST}:${SvdbDriverTest.SVDB_TEST_PORT}",
                Properties().apply {
                    put("user", SvdbDriverTest.SVDB_USERNAME)
                    put("password", SvdbDriverTest.SVDB_PASSWORD)
                }).use { connect ->
                val statement = { connect.createStatement() }
                var count = 0

                // выполняем несколько запросов
                repeat((0..5).count()) {
                    statement().also {
                        it.executeQuery("select  session_uid from sys.sessions")
                        it.close()
                    }
                }

                statement().executeQuery("select  session_uid from sys.sessions").also {
                    while (it.next()) {
                        count++
                    }
                }
                // Проверяем, что есть только одна сессия
                count shouldBe 1
            }
        }
    }
}
