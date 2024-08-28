package codes.spectrum.svdb.jdbc

enum class SvdbJdbcState(val code: String) {
    CONNECTION_DOES_NOT_EXIST("08003"),
    OBJECT_NOT_IN_STATE("55000")
}
