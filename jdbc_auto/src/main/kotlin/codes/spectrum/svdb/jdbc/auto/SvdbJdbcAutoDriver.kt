package codes.spectrum.svdb.jdbc.auto

import codes.spectrum.svdb.jdbc.auto.SvdbJdbcAutoDriverLoader.Companion.DEFAULT_DRIVER_CLASS_NAME
import codes.spectrum.svdb.jdbc.auto.SvdbJdbcAutoDriverLoader.Companion.DEFAULT_JAR_URL
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger


class SvdbJdbcAutoDriver : Driver {
    private val driverLoader = SvdbJdbcAutoDriverLoader.DEFAULT
    private lateinit var internalDriver: Driver

    private fun updateDriver(
        url: String = DEFAULT_JAR_URL,
        className: String = DEFAULT_DRIVER_CLASS_NAME
    ) {
        internalDriver = driverLoader.loadAndGetDriver(url, className)
    }

    override fun connect(url: String?, info: Properties?): Connection {
        val jarUrl: String = info?.getProperty("jar_url") ?: DEFAULT_JAR_URL
        val driverClassName = info?.getProperty("driver_class_name") ?: DEFAULT_DRIVER_CLASS_NAME

        return runWrappingSqlException("22000") {
            updateDriver(jarUrl, driverClassName)
            internalDriver.connect(url, info)
        }
    }

    override fun acceptsURL(url: String): Boolean {
        return internalDriver.acceptsURL(url)
    }

    override fun getPropertyInfo(url: String?, info: Properties?): Array<DriverPropertyInfo> {
        return internalDriver.getPropertyInfo(url, info)
    }

    override fun getMajorVersion(): Int {
        return internalDriver.majorVersion
    }

    override fun getMinorVersion(): Int {
        return internalDriver.minorVersion
    }

    override fun jdbcCompliant(): Boolean = internalDriver.jdbcCompliant()

    override fun getParentLogger(): Logger {
        return internalDriver.parentLogger
    }
}
