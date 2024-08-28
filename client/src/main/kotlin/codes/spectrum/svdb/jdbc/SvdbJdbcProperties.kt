package codes.spectrum.svdb.jdbc

import java.util.*

class SvdbJdbcProperties(
    val password: String = "",
    val impersonate_as: String = "",
    val impersonate_roles: List<String> = listOf(),
    /**
     * Таймаут запроса, включая все операции по вычитыванию resultSet, дефолт 0 - бесконечный таймаут
     */
    val queryTimeout: Long
) {

    enum class SvdbJdbcPropsElems(val paramName: String) {
        PASSWORD("password"),
        IMPERSONATE_AS("impersonate_as"),
        IMPERSONATE_ROLES("impersonate_roles"),
        QUERY_TIMEOUT("queryTimeout");
    }

    companion object {
        fun create(prop: Properties): SvdbJdbcProperties = SvdbJdbcProperties(
            password = prop.getProperty(SvdbJdbcPropsElems.PASSWORD.paramName, ""),
            impersonate_as = prop.getProperty(SvdbJdbcPropsElems.IMPERSONATE_AS.paramName, ""),
            impersonate_roles = prop.getProperty(SvdbJdbcPropsElems.IMPERSONATE_ROLES.paramName, null)
                ?.split(",") ?: emptyList(),
            queryTimeout = prop.getProperty(SvdbJdbcPropsElems.QUERY_TIMEOUT.paramName, null)
                ?.toLongOrNull() ?: 0
        )
    }
}
