package codes.spectrum.svdb

import codes.spectrum.commons.*
import codes.spectrum.svdb.model.v1.SvdbServiceGrpcKt
import com.google.protobuf.Empty
import io.grpc.ClientInterceptors
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NegotiationType
import io.grpc.netty.NettyChannelBuilder
import io.netty.handler.ssl.SslContext
import kotlinx.coroutines.runBlocking
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlin.random.Random
import kotlin.random.nextInt

val NativeDriverQuerySuffix by lazy { " @D T 'kt' V '${ProjectInfo.version}'" }

private val isTls = mutableMapOf<String, Boolean>()


const val authErrorMarker: String = "[AUTH0040]"
private fun Throwable?.isAuthError(): Boolean {
    if (this == null) {
        return false
    }
    return this.toString().contains(authErrorMarker) || this.cause.isAuthError()

}

private fun Result<*>.isAuthError(): Boolean = this.exceptionOrNull().isAuthError()

class NativeDriver {

    fun connect(
        host: String,
        port: Int,
        creds: SvdbCreds,
        options: SvdbDriverOptions = SvdbDriverOptions.DEFAULT
    ): SvdbConnection {
        val key = "$host:$port"
        val tls = isTls[key]
        if (tls == null) {
            val trySecureConnection = kotlin.runCatching { internalConnect(host, port, creds, true, options) }
            if (trySecureConnection.isSuccess) {
                isTls[key] = true
                return trySecureConnection.getOrThrow()
            }
            if (trySecureConnection.isAuthError()) {
                throw trySecureConnection.exceptionOrNull()!!
            }

            val tryInsecureConnection = kotlin.runCatching { internalConnect(host, port, creds, false, options) }
            if (tryInsecureConnection.isSuccess) {
                isTls[key] = false
                return tryInsecureConnection.getOrThrow()
            }
            if (tryInsecureConnection.isAuthError()) {
                throw tryInsecureConnection.exceptionOrNull()!!
            }

            var err = tryInsecureConnection.exceptionOrNull()
            err = ExceptionGroup.resolve(err, tryInsecureConnection.exceptionOrNull())
            throw err!!
        }
        return internalConnect(host, port, creds, tls, options)
    }

    /**
     * Коннект, принимающий на вход ConnectorDescriptor
     */
    fun connect(
        connection: ConnectionDescriptor,
        creds: SvdbCreds? = null,
        options: SvdbDriverOptions = SvdbDriverOptions.DEFAULT
    ): SvdbConnection {
        val host = connection.host.replace(CLUSTER_PREFIX, "@", ignoreCase = true)
        val port = connection.port
        val login = creds?.login ?: connection.user.replace("~", "@")
        val pass = creds?.password ?: connection.password
        val token = if (login == "token") pass else ""
        val svdbCredentials = SvdbCreds(
            login = login,
            password = pass,
            token = token,
            impersonateAs = creds?.impersonateAs ?: connection.impersonateAs,
            impersonateRoles = creds?.impersonateRoles ?: emptyList()
        )
        return connect(host, port, svdbCredentials, options)
    }

    private fun internalConnect(
        host: String,
        port: Int,
        creds: SvdbCreds,
        secure: Boolean,
        options: SvdbDriverOptions
    ): SvdbConnection {
        var resolvedHost = host
        val splittedHost = host.split(".")
        val firstPart = splittedHost[0]
        if (firstPart.contains("@")) {
            val clusterSize = firstPart.split("@")[1].toInt()
            val nodeNumber = Random.nextInt(1..clusterSize)
            resolvedHost = firstPart.split("@")[0] + "${nodeNumber}.${splittedHost.drop(1).joinToString(".")}"
        }

        val channel =
            if (secure) NettyChannelBuilder.forAddress(resolvedHost, port).negotiationType(NegotiationType.TLS)
                .sslContext(getSslContext()).build() else
                NettyChannelBuilder.forAddress(resolvedHost, port).usePlaintext().build()


        val sessionInterceptor = SessionInterceptor(creds)

        val service = SvdbServiceGrpcKt.SvdbServiceCoroutineStub(
            ClientInterceptors.intercept(
                channel,
                sessionInterceptor
            )
        )

        runCatching {
            runBlocking { service.ping(Empty.getDefaultInstance()) }
        }.onSuccess {
            if (it.code != 200) {
                channel.shutdownNow()
                error("ping returns unexpected status ${it.code} with message: ${it.message}")
            }
        }.onFailure {
            channel.shutdownNow()
            throw Exception("error when making init ping", it)
        }

        return SvdbConnection(
            connection = channel,
            service = service,
            sessionUidProvider = sessionInterceptor,
            options = options
        )
    }

    private fun getSslContext(): SslContext {
        return GrpcSslContexts.forClient().trustManager(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }).build()
    }

    data class SvdbDriverOptions(
        val timeout: Long
    ) {
        companion object {
            val DEFAULT = SvdbDriverOptions(0)
        }
    }

    companion object {
        private const val CLUSTER_PREFIX = "SVDBCLSTR-"
    }
}
