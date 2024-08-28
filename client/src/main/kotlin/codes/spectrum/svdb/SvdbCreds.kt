package codes.spectrum.svdb

import codes.spectrum.commons.SecureJsonAdapter
import com.google.gson.annotations.JsonAdapter
import java.util.*

/**
 * Описатель креденций для gRPC
 */
data class SvdbCreds(
    val login: String = "",
    @field:JsonAdapter(SecureJsonAdapter::class)
    val password: String = "",
    val token: String = "",
    val impersonateAs: String = "",
    val impersonateRoles: List<String> = emptyList(),
) {
    fun isImpersonation() = impersonateAs.isNotBlank()

    fun toImpersonationHeader() : String {
        if (!isImpersonation()) {
            return ""
        }
        var result = impersonateAs
        if (impersonateRoles.isNotEmpty()) {
            result = "$result:${impersonateRoles.joinToString(",")}"
        }
        return result
    }

    fun toAuthorizationHeader(): String {
        var schema = "Basic"
        var content = "$login:$password"
        if (token.isNotBlank()) {
            schema = "Bearer"
            content = token
        } else {
            content = Base64.getEncoder().encodeToString(content.toByteArray())
        }
        return schema + " " + content
    }
}

