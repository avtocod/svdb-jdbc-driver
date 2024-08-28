package codes.spectrum.svdb.jdbc

import codes.spectrum.svdb.model.v1.ValueOuterClass

data class SvdbJdbcWrappedValue(
    val value: Any,
    val type: ValueOuterClass.Value.ValueCase
) {
    private val _unwrapped by lazy {
        when (type) {
            ValueOuterClass.Value.ValueCase.ARR -> SvdbJdbcArrayWrapper(value as ValueOuterClass.Value.Arr)
            ValueOuterClass.Value.ValueCase.OBJ -> SvdJdbcObjWrapper(value as ValueOuterClass.Value.Obj)
            else -> value
        }
    }
    fun unwrap(): Any { return _unwrapped }
}
