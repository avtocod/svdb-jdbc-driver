package codes.spectrum.svdb.jdbc

import codes.spectrum.commons.*
import codes.spectrum.svdb.model.v1.ValueOuterClass

class SvdJdbcObjWrapper(
    obj: ValueOuterClass.Value.Obj
) : LinkedHashMap<String, Any?>() {
    init {
        this.putAll(
            obj.objMap.map {
                it.key to when (it.value.valueCase) {
                    ValueOuterClass.Value.ValueCase.ARR -> SvdbJdbcArrayWrapper(it.value.arr)
                    ValueOuterClass.Value.ValueCase.OBJ -> SvdJdbcObjWrapper(it.value.obj)
                    ValueOuterClass.Value.ValueCase.STR -> it.value.str
                    ValueOuterClass.Value.ValueCase.I32 -> it.value.i32
                    ValueOuterClass.Value.ValueCase.I64 -> it.value.i64
                    ValueOuterClass.Value.ValueCase.F64 -> it.value.f64
                    ValueOuterClass.Value.ValueCase.DEC -> it.value.dec
                    ValueOuterClass.Value.ValueCase.BIT -> it.value.bit
                    ValueOuterClass.Value.ValueCase.DUR -> it.value.dur
                    ValueOuterClass.Value.ValueCase.TIM -> it.value.tim
                    ValueOuterClass.Value.ValueCase.VALUE_NOT_SET, null -> null
                }
            }.toMap()
        )
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}
