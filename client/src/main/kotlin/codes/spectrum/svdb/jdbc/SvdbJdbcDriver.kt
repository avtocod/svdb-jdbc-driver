package codes.spectrum.svdb.jdbc

import codes.spectrum.commons.ExceptionGroup
import codes.spectrum.svdb.NativeDriver
import codes.spectrum.svdb.ProjectInfo
import codes.spectrum.svdb.SvdbConnection
import codes.spectrum.svdb.SvdbCreds
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger


val JdbcDriverQuerySuffix by lazy { " @D T 'jdbc' V '${ProjectInfo.version}'" }

class SvdbJdbcDriver(
    private val nativeDriver: NativeDriver = NativeDriver()
) : Driver {
    companion object {
        private val svdbUrlPrefix = """^(jdbc-svdb://)|(svdb://)""".toRegex()

        init {
            register()
        }

        private var registeredDriver: SvdbJdbcDriver? = null

        private fun isRegistered() = registeredDriver != null

        private fun register() {
            check(!isRegistered()) { "Driver is already registered. It can only be registered once." }
            SvdbJdbcDriver().also {
                DriverManager.registerDriver(it)
                registeredDriver = it
            }
        }
    }

    var connectionsCount: Int = 0
        private set

    internal fun parseUrl(url: String): SvdbJdbcUrl {
        val cleanedUrl = url.replace(svdbUrlPrefix, "")
        val (host, portStr) = cleanedUrl.split(":")
        val port = try {
            portStr.toInt()
        } catch (nfe: NumberFormatException) {
            throw IllegalArgumentException("can't parse port $portStr", nfe)
        }
        return SvdbJdbcUrl(
            host = host,
            port = port
        )
    }

    data class SvdbJdbcUrl(
        val host: String,
        val port: Int
    )

    override fun connect(url: String, info: Properties): Connection = runBlocking {
        runWrappingSqlException(CONNECTION_EXCEPTION_CODE) {
            val parsedUrl = parseUrl(url)
            val props = SvdbJdbcProperties.create(info)

            val connection: SvdbJdbcConnection
            try {
                connection = SvdbJdbcConnection(
                    nativeDriver.connect(
                        host = parsedUrl.host,
                        port = parsedUrl.port,
                        SvdbCreds(
                            login = info.getProperty("user"),
                            password = props.password,
                            impersonateAs = props.impersonate_as,
                            impersonateRoles = props.impersonate_roles
                        ),
                        options = NativeDriver.SvdbDriverOptions(
                            props.queryTimeout,
                            props.useMTLS,
                            props.caCertPath,
                            props.clientCertPath,
                            props.clientKeyPath
                        )
                    )
                )
                connectionsCount++
            } catch (e: ExceptionGroup) {
                var message = ""
                e.exceptions.forEach {
                    if (it.cause != null) {
                        message = message.plus(it.cause.toString() + "\n")
                    } else if (!it.message.isNullOrBlank()){
                        message = message.plus(it.message + "\n")
                    }
                }
                throw ExceptionGroup(message, e.exceptions)
            }
            connection
        }
    }

    override fun acceptsURL(url: String): Boolean {
        return url.contains(svdbUrlPrefix)
    }

    override fun getPropertyInfo(url: String?, info: Properties?): Array<DriverPropertyInfo> {
        return arrayOf()
    }

    override fun getMajorVersion(): Int {
        return ProjectInfo.version.substringBefore(".").toInt()
    }

    override fun getMinorVersion(): Int {
        return ProjectInfo.version.split(".")[1].toInt()
    }

    override fun jdbcCompliant(): Boolean {
        return false
    }

    override fun getParentLogger(): Logger? {
        return Logger.getGlobal()
    }
}
