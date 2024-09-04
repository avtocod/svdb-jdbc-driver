package codes.spectrum.svdb.jdbc.auto

import java.sql.SQLException

inline fun <R> runWrappingSqlException(sqlStatus: String, block: () -> R): R {
    return try {
        block()
    } catch (e: Exception) {
        throw SQLException(e.message, sqlStatus, e)
    }
}
