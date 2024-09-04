package codes.spectrum.svdb.jdbc.auto

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class SvdbJdbcAutoDriverTest : FunSpec() {
    init {
        test("Пробуем подключится к stage").config(enabled = System.getenv("IS_CI") == "true") {
            val driver = SvdbJdbcAutoDriver()
            val connection = driver.connect("$SVDB_TEST_HOST:$SVDB_TEST_PORT",
                Properties().apply {
                    this.setProperty("user",SVDB_USERNAME)
                    this.setProperty("password", SVDB_PASSWORD)
                })

            connection.createStatement().executeQuery("select 1").also {
                it.next()
                it.getInt(1) shouldBe 1
            }
        }
    }

    companion object {
        const val SVDB_TEST_HOST = "127.0.0.1"
        const val SVDB_TEST_PORT = 50051
        const val SVDB_USERNAME = "admin"
        const val SVDB_PASSWORD = "123"
    }
}
