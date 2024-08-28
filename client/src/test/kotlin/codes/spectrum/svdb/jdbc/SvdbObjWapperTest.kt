package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ValueKt.arr
import codes.spectrum.svdb.model.v1.ValueKt.obj
import codes.spectrum.svdb.model.v1.value
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SvdbObjWapperTest : FunSpec() {
    init {
        test("Проверяем toString с примитивами") {
            SvdJdbcObjWrapper(
                obj = obj {
                    obj.put("string", value { str = "str" })
                    obj.put("int", value { i32 = 20 })
                    obj.put("bool", value { bit = true })
                }).toString() shouldBe """{"string":"str","int":20,"bool":true}"""
        }

        test("Проверяем toString с массивом") {
            SvdJdbcObjWrapper(
                obj = obj {
                    obj.put("string", value { str = "str" })
                    obj.put("arr", value { arr = arr { arr.add(value { str = "arr_value" }) } })
                }).toString() shouldBe """{"string":"str","arr":["arr_value"]}"""
        }

        test("Проверяем toString с вложенным объектом") {
            SvdJdbcObjWrapper(
                obj = obj {
                    obj.put("string", value { str = "str" })
                    obj.put("obj", value { obj = obj { obj.put("internal_key", value { str = "internal_value" }) } })
                }).toString() shouldBe """{"string":"str","obj":{"internal_key":"internal_value"}}"""
        }
    }
}
