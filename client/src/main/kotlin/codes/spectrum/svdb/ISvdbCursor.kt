package codes.spectrum.svdb

import codes.spectrum.svdb.model.v1.Queryresult
import java.io.Closeable

interface ISvdbCursor: Closeable {
    suspend fun fetch(): Queryresult.QueryResult
    fun getSessionUid(): String

    fun getCursorUid(): String

    fun cancel()

}

