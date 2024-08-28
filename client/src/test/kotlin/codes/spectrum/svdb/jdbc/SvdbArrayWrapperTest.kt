package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ValueKt.arr
import codes.spectrum.svdb.model.v1.ValueKt.obj
import codes.spectrum.svdb.model.v1.value
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SvdbArrayWrapperTest : FunSpec() {
    init {
        test("Проверяем конструирование toString со строками") {
            SvdbJdbcArrayWrapper(
                array = arr {
                    arr.add(value { str = "sdfs" })
                    arr.add(value { str = "sdf12332s" })
                    arr.add(value { str = "sdfs231" })
                }).toString() shouldBe """["sdfs","sdf12332s","sdfs231"]"""
        }

        test("Проверяем конструирование toString с примитивами") {
            SvdbJdbcArrayWrapper(
                array = arr {
                    arr.add(value { i32 = 1000 })
                    arr.add(value { str = "sdf12332s" })
                    arr.add(value { bit = false })
                }).toString() shouldBe """[1000,"sdf12332s",false]"""
        }

        test("Проверяем конструирование toString с вложенными массивами") {
            SvdbJdbcArrayWrapper(
                array = arr {
                    arr.add(value { i32 = 1000 })
                    arr.add(value {
                        arr = arr {
                            arr.add(value { str = "333" })
                            arr.add(value { bit = true })
                        }
                    })
                }
            ).toString() shouldBe """[1000,["333",true]]"""
        }

        test("Проверяем конструирование toString с объектами") {
            SvdbJdbcArrayWrapper(
                array = arr {
                    arr.add(value { i32 = 1000 })
                    arr.add(value {
                        obj = obj {
                            obj.put("key", value { str = "22" })
                        }
                    })
                }
            ).toString() shouldBe """[1000,{"key":"22"}]"""
        }
    }
}
