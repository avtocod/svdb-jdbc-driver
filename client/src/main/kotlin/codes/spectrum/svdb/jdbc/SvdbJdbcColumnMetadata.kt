package codes.spectrum.svdb.jdbc

data class SvdbJdbcColumnMetadata(
        val columnName: String,
        val type: SvdbJdbcTypes,
        val scale: Int = 0
)
