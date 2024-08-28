package codes.spectrum.svdb.jdbc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.sequences.shouldContainInOrder

class SysDataTest : FunSpec() {
    init {
        val svdbJdbcSysData = SvdbJdbcSysData(
            listOf(
                SvdbJdbcCatalog(
                    "demo_base", listOf(
                        Schema("demo", listOf(Table("inns",""), Table("passports",""))),
                        Schema("fssp", listOf(Table("direct","")))
                    )
                ),
                SvdbJdbcCatalog(
                    "system", listOf(
                        Schema("sys", listOf(Table("tables",""), Table("fields","")))
                    )
                ),
            ),
            listOf()
        )

        context("схемы и таблицы") {
            test("Проверяем фильтрацию по схеме") {
                svdbJdbcSysData.toSchemasTablesScv(schema = "fssp").toList() shouldContainInOrder listOf(
                    "TABLE_CAT;TABLE_SCHEM;TABLE_NAME;REMARKS;TABLE_TYPE",
                    "demo_base;fssp;direct;;TABLE",
                )
            }

            test("Если ничего не предано в фильтр, то должен отдавать все таблицы всех схем") {
                svdbJdbcSysData.toSchemasTablesScv().toList() shouldContainInOrder listOf(
                    "TABLE_CAT;TABLE_SCHEM;TABLE_NAME;REMARKS;TABLE_TYPE",
                    "demo_base;demo;inns;;TABLE",
                    "demo_base;demo;passports;;TABLE",
                    "demo_base;fssp;direct;;TABLE",
                    "system;sys;tables;;TABLE",
                    "system;sys;fields;;TABLE",
                )
            }

            test("Проверяем фильтрацию по каталогу и по схеме") {
                svdbJdbcSysData.toSchemasTablesScv("demo_base", "fssp").toList() shouldContainInOrder listOf(
                    "TABLE_CAT;TABLE_SCHEM;TABLE_NAME;REMARKS;TABLE_TYPE",
                    "demo_base;fssp;direct;;TABLE",
                )
            }
        }

        context("cхемы") {
            test("Проверяем фильтрацию по каталогу") {
                svdbJdbcSysData.toSchemasScv("system") shouldContainInOrder listOf(
                    "TABLE_SCHEM;TABLE_CATALOG",
                    "sys;system"
                ).asSequence()
            }

            test("Если не передать каталог, то должен возвращать все схемы") {
                svdbJdbcSysData.toSchemasScv() shouldContainInOrder listOf(
                    "TABLE_SCHEM;TABLE_CATALOG",
                    "demo;demo_base",
                    "sys;system"
                ).asSequence()
            }
        }
    }
}
