package codes.spectrum.svdb.jdbc

import codes.spectrum.commons.gson


class SvdbJdbcArrayWrapperV2(array: List<Any?>) : ArrayList<Any?>() {
    init {
        addAll(array)
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}
