package codes.spectrum.svdb.jdbc


import codes.spectrum.svdb.model.v1.ValueOuterClass
import codes.spectrum.commons.gson

class SvdbJdbcArrayWrapper(array: ValueOuterClass.Value.Arr) : ArrayList<Any?>() {
    init {
        addAll(
            array.arrList.map {
                when (it.valueCase) {
                    ValueOuterClass.Value.ValueCase.ARR -> SvdbJdbcArrayWrapper(it.arr)
                    ValueOuterClass.Value.ValueCase.OBJ -> SvdJdbcObjWrapper(it.obj)
                    ValueOuterClass.Value.ValueCase.STR -> it.str
                    ValueOuterClass.Value.ValueCase.I32 -> it.i32
                    ValueOuterClass.Value.ValueCase.I64 -> it.i64
                    ValueOuterClass.Value.ValueCase.F64 -> it.f64
                    ValueOuterClass.Value.ValueCase.DEC -> it.dec
                    ValueOuterClass.Value.ValueCase.BIT -> it.bit
                    ValueOuterClass.Value.ValueCase.DUR -> it.dur
                    ValueOuterClass.Value.ValueCase.TIM -> it.tim
                    ValueOuterClass.Value.ValueCase.VALUE_NOT_SET, null -> null
                }
            }
        )
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}
