package codes.spectrum

import java.io.File
import java.nio.file.Paths
import java.time.Instant


/**
 * Выполнить некоторую работу с поднятым инстансом `SVDB`
 */
suspend fun <T> withSvdbServer(
    grpcPort: Int = 50053,
    adminPort: Int = 8083,
    kubePort: Int = 9093,
    body: suspend () -> T,
): T {
    val gitToken = System.getenv().getOrDefault("SVDB_REGISTRY_GIT_TOKEN", "")
    val gitBranch = System.getenv().getOrDefault("SVDB_REGISTRY_GIT_BRANCH", "dev")
    val isLocalRun = System.getenv().getOrDefault("LOCAL_RUN", "false").toBoolean()


    val tempFile = File.createTempFile("svdb-error", Instant.now().toString())
    tempFile.deleteOnExit()

    val execFile = "svdb-srv"


    val srvPath = if (isLocalRun) "./test_instance" else "/opt"
    val process = ProcessBuilder()
        .command(
            "$srvPath/$execFile",
            "--demo-mode",
            "--registry-git",
            "offline",
            "--registry-git-token",
            gitToken,
            "--registry-git-branch",
            gitBranch,
            "--server-port",
            grpcPort.toString(),
            "--admin-port",
            adminPort.toString(),
            "--k8s-port",
            kubePort.toString()
        )
        .directory(Paths.get("").toAbsolutePath().parent.toFile())
        .redirectErrorStream(true)
        .redirectOutput(tempFile)
        .start()

    while (true) {
        Thread.sleep(500)
        if (!process.isAlive) {
            break
        }
        val data = tempFile.readText()
        if (data.contains("SVDB ready to process queries")) {
            break
        }
    }
    if (!process.isAlive) {
        throw Exception("cannot start svdb, exit code ${process.exitValue()} (${tempFile.readText()})")
    }
    val result = runCatching {
        body()
    }
    process.destroyForcibly()
    return result.getOrThrow()
}
