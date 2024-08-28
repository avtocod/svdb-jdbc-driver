package codes.spectrum.commons

import com.google.gson.annotations.JsonAdapter
import java.net.URI
import java.net.URLDecoder


const val DEFAULT_PORT = 50051
data class ConnectionDescriptor (
    val host: String,
    val port: Int = DEFAULT_PORT,
    val user: String = "",
    @JsonAdapter(SecureJsonAdapter::class)
    val password: String = "",
    val impersonateAs: String = "",
    val impersonateAsRoles: List<String> = emptyList(),
){
    companion object {
        fun fromUrlString(url: String) : ConnectionDescriptor {
            return fromURI(URI.create(url))
        }

        fun fromURI(uri : URI) : ConnectionDescriptor {
            var user = ""
            var pass = ""
            if (!uri.userInfo.isNullOrBlank()){
                if (uri.userInfo.contains(":")){
                    user=uri.userInfo.substringBefore(":")
                    pass=uri.userInfo.substringAfter(":")
                }else{
                    user=uri.userInfo
                }
            }

            var impersonateAs = ""
            val impersonateAsRoles = mutableListOf<String>()
            val params =  uri.rawQuery.trim('?').split("&")
            for (p in params){
                if (p.startsWith("impersonate_as=")){
                    impersonateAs = URLDecoder.decode(p.substringAfter("="),Charsets.UTF_8)
                }
                if(p.startsWith("impersonate_as_roles=")){
                    for(_r in p.substringAfter("=").split(",")){
                        val r = _r.trim()
                        if (r.isNotBlank()){
                            impersonateAsRoles.add(r)
                        }
                    }
                }
            }

            return ConnectionDescriptor(
                host = uri.host,
                port = if (uri.port == 0) DEFAULT_PORT else uri.port,
                user = user,
                password = pass,
                impersonateAs = impersonateAs,
                impersonateAsRoles = impersonateAsRoles
            )
        }
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
}
