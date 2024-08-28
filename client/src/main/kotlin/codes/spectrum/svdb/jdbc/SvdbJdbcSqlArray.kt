package codes.spectrum.svdb.jdbc

import java.sql.ResultSet
import java.util.*

class SvdbJdbcSqlArray(
    private val internalArray: Array<out Any?>
): java.sql.Array {
    override fun getBaseTypeName(): String {
        TODO("Not yet implemented")
    }

    override fun getBaseType(): Int {
        TODO("Not yet implemented")
    }

    override fun getArray(): Any {
        return internalArray
    }

    override fun getArray(map: MutableMap<String, Class<*>>?): Any {
        TODO("Not yet implemented")
    }

    override fun getArray(index: Long, count: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getArray(index: Long, count: Int, map: MutableMap<String, Class<*>>?): Any {
        TODO("Not yet implemented")
    }

    override fun getResultSet(): ResultSet {
        TODO("Not yet implemented")
    }

    override fun getResultSet(map: MutableMap<String, Class<*>>?): ResultSet {
        TODO("Not yet implemented")
    }

    override fun getResultSet(index: Long, count: Int): ResultSet {
        TODO("Not yet implemented")
    }

    override fun getResultSet(index: Long, count: Int, map: MutableMap<String, Class<*>>?): ResultSet {
        TODO("Not yet implemented")
    }

    override fun free() {
        Arrays.fill(internalArray, null)
    }
}
