package codes.spectrum.svdb


import codes.spectrum.svdb.model.v1.ColumnOuterClass
import codes.spectrum.svdb.model.v1.Queryresult.QueryResult
import codes.spectrum.withSvdbServer
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.fail
import codes.spectrum.commons.*
import codes.spectrum.svdb.jdbc.*

class ConnectionSDKTest : FunSpec() {
    init {
        context("Тесты для проверки корректности работы драйвера svdb") {
            withSvdbServer {
                test("Проверка что connection корректно создался через дескриптор") {
                    val connectorDescriptor = ConnectionDescriptor.fromUrlString(SVDB_CONNECTION_STRING)
                    NativeDriver().connect(connectorDescriptor)
                        .use { connection ->
                            val result = connection.executeQuery("select * from demo.inns @F S 1 M 1")
                            result.isSuccess shouldBe true

                            result.getOrNull()?.getResult()?.state?.also {
                                it.type shouldBe "OK"
                                it.code shouldBe 200
                            }
                        }
                }

                test("Проверка что connection корректно создался") {
                    NativeDriver().connect(
                        host = SVDB_TEST_HOST,
                        port = SVDB_TEST_PROCESS_PORT,
                        creds = SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    )
                        .use { connection ->
                            val result = connection.executeQuery("select * from demo.inns @F S 1 M 1")
                            result.isSuccess shouldBe true

                            result.getOrNull()?.getResult()?.state?.also {
                                it.type shouldBe "OK"
                                it.code shouldBe 200
                            }
                        }
                }

                test("Проверка что при некорректном запросе результат не успех") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    )
                        .use { connection ->
                            val result = connection.executeQuery("select * from bad.table")
                            result.isSuccess.shouldBeFalse()
                            result.isFailure.shouldBeTrue()
                        }
                }

                test("Тестируем получение записей по одной") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    )
                        .use { connection ->
                            val cursorResult = connection.executeQuery("select * from demo.inns @F S 1 M 1")
                            cursorResult.isSuccess shouldBe true

                            val agregatedResult = mutableListOf<QueryResult>()

                            val cursor = cursorResult.getOrNull()
                            println("Before first fetch")
                            var recResult = cursor?.fetch()?.also {
                                agregatedResult.add(it)
                                println("First fetch")
                            }

                            while (recResult?.state?.type == "OK") {
                                print("fetch... ")
                                recResult = cursor?.fetch()?.also {
                                    agregatedResult.add(it)
                                }
                                println(" complete!")
                            }

                            if (recResult?.state?.type == "ERROR") {
                                println(recResult.state?.message)
                                fail { "error with: ${recResult.state?.message}" }
                            }
                            println(recResult?.state?.type)
                            println("EOF reached")

                            agregatedResult.size shouldBe 4
                            agregatedResult.last().state.type shouldBe "EOF"
                        }
                }

                test("Тестируем метод сразу с обьявлением запроса и выгрузкой агрегированного результата") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    ).use {
                        val agregatedResult = it.executeList("select * from demo.inns @F S 1 M 1")
                        agregatedResult.size shouldBe 3
                    }
                }

                test("Ошибка некорректного SDQL") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    ).use {
                        shouldThrowAny { it.executeList("select * from") }
                    }
                }

                test("Ошибка некорректного SDQL (просто execute)") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    ).use {
                        val result = it.executeQuery("select * from")
                        result.shouldBeFailure()
                    }
                }

                test("Ошибка отсутствия таблицы") {
                    NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    ).use {
                        shouldThrowAny { it.executeList("select * from no.such") }
                    }
                }

                context("Параметризованные запросы") {
                    test("Подготавливаем и выполняем запрос с параметром в режиме V1") {
                        NativeDriver().connect(
                            SVDB_TEST_HOST,
                            SVDB_TEST_PROCESS_PORT,
                            SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                        )
                            .use { connection ->
                                val literal = "kkk"

                                val uid =
                                    connection.executeQuery("prepare select \$1 $SDQL_DRIVER_PROTOCOL_V1").getOrNull()
                                        ?.getResult()?.recordsList?.get(0)?.fieldsList?.get(0)?.value?.str
                                        ?: error("can't get uid")

                                val svdbCursorResult = connection.executeQuery(
                                    "execute '$uid' $SDQL_DRIVER_PROTOCOL_V1",
                                    mapOf(
                                        "1" to SvdbJdbcParameter(
                                            dataType = ColumnOuterClass.DataType.STRING,
                                            value = literal
                                        )
                                    )
                                )

                                val cursor = svdbCursorResult.getOrNull() ?: error("can't get cursor")

                                val result = cursor.getResult().recordsList?.get(0)?.fieldsList?.get(0)?.value?.str
                                    ?: error("can't get literal")

                                result shouldBe literal
                            }
                    }

                    test("Подготавливаем и выполняем запрос с параметром в режиме V2") {
                        NativeDriver().connect(
                            SVDB_TEST_HOST,
                            SVDB_TEST_PROCESS_PORT,
                            SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                        )
                            .use { connection ->
                                val literal = "kkk"

                                val byteItemWithUid =
                                    connection.executeQuery("prepare select \$1").getOrNull()
                                        ?.getResult()?.byteRecordsList?.get(0)?.fieldsList?.get(0)?.item
                                        ?: error("can't get byte item with uid")

                                val uid = unmarshalByteField(byteItemWithUid, ColumnOuterClass.DataType.STRING)

                                val svdbCursorResult = connection.executeQuery(
                                    "execute '$uid' $SDQL_DRIVER_PROTOCOL_V1",
                                    mapOf(
                                        "1" to SvdbJdbcParameter(
                                            dataType = ColumnOuterClass.DataType.STRING,
                                            value = literal
                                        )
                                    )
                                )

                                val cursor = svdbCursorResult.getOrNull() ?: error("can't get cursor")

                                val byteItemWithRes = cursor.getResult().byteRecordsList?.get(0)?.fieldsList?.get(0)?.item
                                    ?: error("can't get byte item with res")

                                val result = unmarshalByteField(byteItemWithRes, ColumnOuterClass.DataType.STRING)

                                result shouldBe literal
                            }
                    }
                }

                test("Проверка парсинга строки для замены на @") {
                    val connectionString =
                        "https://user~system:passwd@s-svdb-svdbgo-SVDBCLSTR-4.spectrumdata.tech:443?code=svdb"
                    shouldNotThrowAny {
                        ConnectionDescriptor.fromUrlString(connectionString)
                    }
                }
            }
        }

        test("Проверяем поведение при реконекте") {
            var connection: SvdbConnection? = null
            try {
                // поднимаем svdb-server и успешно выполняем запрос
                withSvdbServer {
                    connection = NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    )
                    runCatching { connection!!.executeList("select * from demo.inns @F S 1 M 1") }.isSuccess.shouldBeTrue()
                }

                Thread.sleep(100)

                // отключили svdb-server
                // конечно, же получаем ошибку, так как svdb-server не запущен
                shouldThrowAny { connection!!.executeList("select * from demo.inns @F S 1 M 1") }

                // запускаем другой svdb-server
                withSvdbServer {
                    Thread.sleep(2000)
                    runCatching { connection!!.executeList("select * from demo.inns @F S 1 M 1") }.isSuccess.shouldBeTrue()
                }
            } finally {
                connection?.close()
            }
        }

        test("Проверяем поведение при реконекте, если разрыв произошел между fetch") {
            var connection: SvdbConnection? = null
            var cursor: ISvdbCursor? = null
            try {
                // поднимаем svdb-server и успешно выполняем запрос
                withSvdbServer {
                    connection = NativeDriver().connect(
                        SVDB_TEST_HOST,
                        SVDB_TEST_PROCESS_PORT,
                        SvdbCreds(SVDB_USERNAME, SVDB_PASSWORD)
                    )

                    // @F S 1 M 1 для явного режима "по одной" который был до этого
                    cursor = connection!!.executeQuery("select * from demo.inns @F S 1 M 1").getOrThrow()
                    cursor!!.fetch()
                }

                Thread.sleep(100)

                // отключили svdb-server
                // конечно, же получаем ошибку, так как svdb-server не запущен
                shouldThrowAny { cursor!!.fetch() }

                // запускаем другой svdb-server
                withSvdbServer {
                    Thread.sleep(2000)
                    shouldThrowAny { cursor!!.fetch() }
                }
            } finally {
                connection?.close()
            }
        }
    }

    companion object {
        const val SVDB_TEST_HOST = "127.0.0.1"
        const val SVDB_TEST_PROCESS_PORT = 50053
        const val SVDB_USERNAME = "admin"
        const val SVDB_PASSWORD = "123"
        const val SVDB_CONNECTION_STRING =
            "https://$SVDB_USERNAME:$SVDB_PASSWORD@$SVDB_TEST_HOST:$SVDB_TEST_PROCESS_PORT?code=svdb"
    }
}
