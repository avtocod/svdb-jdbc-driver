package codes.spectrum.svdb.jdbc

import java.io.File

class SvdbJdbcFileLogger(path: String) {
    private val file = File(path)

    fun log(message: String) {
        file.bufferedWriter().use {
            it.write(message)
            it.newLine()
        }
    }
}
