package codes.spectrum.svdb

enum class SvdbStateTypes(val stringValue: String) {
    EOF("EOF")
}

enum class SvdbStateCodes(val stringValue: String, val intValue: Int) {
    TIMEOUT("408", 408),

    SERVER_ERROR("500", 500)
}

// используется в тестах в sdql запросах для проверки 1 версии режима работы
const val SDQL_DRIVER_PROTOCOL_V1 = "@D V '1.6.0'"
