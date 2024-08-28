package codes.spectrum.svdb.jdbc

import codes.spectrum.commons.*

class SvdbJdbcObjWrapperV2(obj: Map<String, Any?>) : LinkedHashMap<String, Any?>() {
    init {
        putAll(obj)
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}
