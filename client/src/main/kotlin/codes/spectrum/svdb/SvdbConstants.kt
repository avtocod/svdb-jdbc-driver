package codes.spectrum.svdb

enum class SvdbStateTypes(val stringValue: String) {
    OK("OK"),
    EOF("EOF"),
    ERROR("ERROR"),
}

enum class SvdbStateCodes(val intValue: Int) {
    TIMEOUT(408),
    SERVER_ERROR(500)
}

