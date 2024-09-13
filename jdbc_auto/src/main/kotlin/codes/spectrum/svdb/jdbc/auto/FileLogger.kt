package codes.spectrum.svdb.jdbc.auto

import java.io.File

/**
 * Класс создан для отладки
 */
class FileLogger(path: String) {
    private val file = File(path)

    fun log(message: String) {
        file.appendText(message)
        file.appendText(System.lineSeparator())
    }

    companion object {
        val DEFAULT = FileLogger("/home/oleg/loggs/log.txt")
    }
}