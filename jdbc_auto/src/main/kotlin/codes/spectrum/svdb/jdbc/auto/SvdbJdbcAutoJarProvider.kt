package codes.spectrum.svdb.jdbc.auto

import java.io.Closeable
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Driver
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class SvdbJdbcAutoDriverLoader: Closeable {
    @Volatile
    private var driver: Driver? = null
    private val driverSavePath = "${System.getProperty("user.home")}/svdb"
    private val jarSaveDir: File = File(driverSavePath).also { it.mkdirs() }
    private val executorService = Executors.newSingleThreadScheduledExecutor()
    private var future: ScheduledFuture<*>? = null

    fun loadAndGetDriver(url: String, className: String): Driver {
        if (driver == null) {
            updateDriver(url, className)
        }

        if (future == null) {
            future = executorService.scheduleAtFixedRate({  updateDriver(url, className) },
                DEFAULT_POLLING_INTERVAL,
                DEFAULT_POLLING_INTERVAL,
                TimeUnit.SECONDS)
        }
        return driver ?: error("Could not load driver from url $url")
    }

    private fun updateDriver(jarUrl: String, className: String) {
        val driverPath = jarSaveDir.toPath().resolve("svdb_driver.jar")
        Files.copy(
            URL(jarUrl).openStream(),
            driverPath,
            StandardCopyOption.REPLACE_EXISTING
        )

        val url = URL("jar:file:${driverPath.toRealPath()}!/")
        val ucl = URLClassLoader(arrayOf(url))
        val clazz = ucl.loadClass(className)
        driver = clazz?.getDeclaredConstructor()?.newInstance() as Driver
    }

    companion object {
        val DEFAULT = SvdbJdbcAutoDriverLoader()

        // должен быть заменен на URL, где хостится jar драйвера,
        // обычно заменяет build.gradle.kts из env SVDB_DRIVER_JAR_URL
        const val DEFAULT_JAR_URL = "<<JAR_URL_PLACEHOLDER>>"

        const val DEFAULT_DRIVER_CLASS_NAME = "codes.spectrum.svdb.jdbc.SvdbJdbcDriver"

        /**
         * Интервал опроса в sec
         */
        const val DEFAULT_POLLING_INTERVAL: Long = 30 * 60 // 30m
    }

    override fun close() {
        future?.cancel(false)
        executorService.shutdown()
    }
}
