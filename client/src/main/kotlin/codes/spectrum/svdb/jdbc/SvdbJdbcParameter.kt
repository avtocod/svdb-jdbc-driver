package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ColumnOuterClass.DataType

data class SvdbJdbcParameter(
    val dataType: DataType,
    val value: Any,
)
