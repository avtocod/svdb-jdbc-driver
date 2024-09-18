package codes.spectrum.svdb

enum class SvdbStateTypes(val stringValue: String) {
    EOF("EOF"),
    ERROR("ERROR"),
}

enum class SvdbStateCodes(val intValue: Int) {
    TIMEOUT(408),
    SERVER_ERROR(500)
}

