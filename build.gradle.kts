import com.google.protobuf.gradle.ProtobufExtension
import com.google.protobuf.gradle.id
import org.gradle.jvm.tasks.Jar
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.google.protobuf") version "0.9.5" apply false
}

group = "codes.spectrum.svdb"
version = findProperty("project.version") ?: "0.0.0-UNDEFINED"


allprojects {
    repositories {
        mavenCentral()
    }
}

// версии различных используемых зависимостей
val kotestVersion = "5.9.1"
val protobufVersion = "4.31.1"
val grpcVersion = "1.74.0"
val grpcKotlinStubVersion = "1.4.1"
val grpcPluginJavaVersion = "1.74.0"
val grpcPluginKotlinVersion = "1.4.1:jdk8@jar"
val coroutinesVersion = "1.9.0"
val gsonVersion = "2.11.0"


val Project.sdevtool: Sdevtool
    get() {
        if (rootProject.extra.has("sdevtool")) {
            return rootProject.extra.get("sdevtool") as Sdevtool
        }
        val result = Sdevtool(this)
        rootProject.extra.set("sdevtool", result)
        return result
    }



subprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    layout.buildDirectory = rootProject.layout.buildDirectory.dir(name)
    group = rootProject.group
    version = rootProject.version

    dependencies {
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(17)
    }
}

project("model") {
    apply {
        plugin("com.google.protobuf")
    }

    sourceSets {
        main {
            (this.extensions.getByName("proto") as SourceDirectorySet).apply {
                srcDir("../proto/")
            }
        }
    }
    // там свой build.gradle.kts для PROTOBUF иначе не работает DSL для protobuf
    dependencies {
        //protobuf
        api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
        api("com.google.protobuf:protobuf-java-util:$protobufVersion")
        //grpc
        api("io.grpc:grpc-protobuf:$grpcVersion")
        api("io.grpc:grpc-stub:$grpcVersion")
        api("io.grpc:grpc-netty:$grpcVersion")
        api("io.grpc:grpc-kotlin-stub:$grpcKotlinStubVersion")
        api("io.grpc:grpc-okhttp:$grpcVersion")
        api("com.squareup.okhttp3:okhttp-tls:4.12.0")
    }

    extensions.getByType(ProtobufExtension::class).apply {
        protoc {
            artifact = "com.google.protobuf:protoc:$protobufVersion"
        }
        generatedFilesBaseDir = "$buildDir/generated/sources/proto"
        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:$grpcPluginJavaVersion"
            }
            id("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcPluginKotlinVersion"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    id("grpc") { outputSubDir = "java" }
                    id("grpckt") { outputSubDir = "kotlin" }
                }
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }
}

project("client") {
    dependencies {
        api(project(":model"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutinesVersion")
        implementation("com.google.code.gson:gson:$gsonVersion")
    }

    tasks.getByName("test") {
        dependencies {
            api(project(":test"))
        }
    }

    // немного кодогенерации, чтобы гарантировать версию внутри самой сборки
    tasks.getByName("compileKotlin").apply {
        // нужно обновить значение версии в классе ProjectInfo
        doFirst {
            val file = File("$projectDir/src/main/kotlin/codes/spectrum/svdb/ProjectInfo.kt")
            val currentContent = file.readText()
            val updatedContent = currentContent.replace(Regex("version\\s*=\\s*\"[^\"]*\""), "version = \"$version\"")
            if (updatedContent != currentContent) {
                file.writeText(updatedContent)
            }
        }
    }
}

project("jdbc") {
    dependencies {
        api(project(":client"))
    }
    buildFatJar(
        configs = listOf(
            GradleConfigurations.COMPILE,
            GradleConfigurations.RUNTIME
        ), strategy = DuplicatesStrategy.INCLUDE,
        filter = { !it.name.contains("pureMain") }) {
        from("./src/main/resources") {
            include("META-INF/services/java.sql.Driver")
        }
    }
}

// драйвер с автообновлением. сборка требует указание места, откуда нужно выкачать драйвер
// указать надо в переменную окружения SVDB_DRIVER_JAR_URL
project("jdbc_auto") {
    // немного кодогенерации, чтобы динамически из енва выставлять местоположение собранного jar драйвера
    tasks.getByName("compileKotlin").apply {
        val exceptionText = "Переменная окружения SVDB_DRIVER_JAR_URL не установлена или пуста. " +
                "Необходимо выставить перед сборкой автообновляющегося драйвера."
        val jarUrlPlaceholder = "<<JAR_URL_PLACEHOLDER>>"


        doFirst {
            val isInhouseCI = System.getenv().getOrDefault("IS_CI", "false").toBoolean()
            val jarUrl = System.getenv().getOrDefault("SVDB_DRIVER_JAR_URL", jarUrlPlaceholder)

            if ( isInhouseCI && jarUrl.equals(jarUrlPlaceholder)) {
                throw IllegalStateException(exceptionText)
            }

            // заменить <<JAR_URL_PLACEHOLDER>> на реальный URL местоположения jar в сети
            val file = File("$projectDir/src/main/kotlin/codes/spectrum/svdb/jdbc/auto/SvdbJdbcAutoJarProvider.kt")
            val currentContent = file.readText()
            val updatedContent = currentContent.replace(Regex("<<\\s*JAR_URL_PLACEHOLDER\\s*>>"), jarUrl)
            if (updatedContent != currentContent) {
                file.writeText(updatedContent)
            }
        }
    }
}


project("jdbc_auto_pub") {
    dependencies {
        api(project(":jdbc_auto"))
    }

    buildFatJar(
        configs = listOf(
            GradleConfigurations.COMPILE,
            GradleConfigurations.RUNTIME,
        ),
        strategy = DuplicatesStrategy.INCLUDE,
        filter = {
            !it.name.contains("pureMain")
        },
    ) {
        from("./src/main/resources") {
            include("META-INF/services/java.sql.Driver")
        }
    }
}


enum class GradleConfigurations(
    val gradleValue: String
) {
    COMPILE("compileClasspath"),

    RUNTIME("runtimeClasspath"),

    TEST_COMPILE("testCompileClasspath"),

    TEST_RUNTIME("testRuntimeClasspath")
}


fun Project.buildFatJar(
    configs: List<GradleConfigurations> = listOf(GradleConfigurations.COMPILE),
    strategy: DuplicatesStrategy = DuplicatesStrategy.INCLUDE,
    filter: (File) -> Boolean = { true },
    extraBuild: Jar.() -> Unit = {}
) {
    val FAT_JAR_TASK_NAME = "fatJar"
    if (this.tasks.findByName(FAT_JAR_TASK_NAME) == null) {
        this.tasks.create(FAT_JAR_TASK_NAME, Jar::class.java) {
            dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
            group = "build"
            configs.flatMap { configurations.getByName(it.gradleValue) }.filter(filter).distinct()
                // вот этого категорически нельзя - сортировать, потом не рабочие fatJar
                // видимо влияет на резолюцию класов
                //.sortedBy { it.name }
                .forEach { file ->
                    // оставил для контроля того, что именно попадает в fatJar
                    println("FATJAR ${file.name}")
                    from(zipTree(file.canonicalPath))
                }
            duplicatesStrategy = strategy
            extraBuild()
        }
    }

    val PUBLISH_FAT_JAR_TASK_NAME = "publishFatJar"
    if (this.tasks.findByName(PUBLISH_FAT_JAR_TASK_NAME) == null) {
        this.tasks.register(PUBLISH_FAT_JAR_TASK_NAME) {
            group = "publishing"
            dependsOn(FAT_JAR_TASK_NAME)
            doFirst {
                val project = this@buildFatJar
                val jarToPublish = "${project.name}-${project.version}.jar"
                val outJarName = "${rootProject.name}-${project.name}.jar"
                synchronized(this.project) {
                    println("task $jarToPublish is started")
                    project.sdevtool.publishToNexus(
                        sourceFile = "build/${project.name}/libs/${jarToPublish}", targetFile = outJarName
                    ).also {
                        println("nexus-publish state: ${it.state}")
                        println(it.output)
                        if (it.state != 0) {
                            error("could not execute task $PUBLISH_FAT_JAR_TASK_NAME ext code: ${it.state} msg: ${it.output}")
                        }
                    }
                }
            }
        }
    }
}

val is_windows = System.getProperty("os.name").startsWith("Windows")

class Sdevtool internal constructor(val project: Project) {
    private val execPath by lazy {
        val nexusHost: String = System.getenv("NEXUS_HOST") ?: ""
        val exename = if (is_windows) "sdevtool.exe" else "sdevtool"
        val path = File(project.rootDir, "build/tools/$exename")
        path.parentFile.mkdirs()


        val cli = HttpClient.newHttpClient()
        val verRequest = HttpRequest.newBuilder(URI("${nexusHost}/repository/bin/sdevtool/versions.txt")).build()
        val verResponse = cli.send(verRequest, HttpResponse.BodyHandlers.ofString())
        val lastVer = verResponse.body().split("\n")[0]


        val uri =
            if (is_windows) URI("${nexusHost}/repository/bin/sdevtool/$lastVer/windows/sdevtool.exe") else URI(
                "${nexusHost}/repository/bin/sdevtool/$lastVer/linux/sdevtool"
            )
        val binRequest = HttpRequest.newBuilder(uri).build()
        val binResponse = cli.send(binRequest, HttpResponse.BodyHandlers.ofInputStream())
        path.outputStream().use {
            binResponse.body().copyTo(it)
            it.flush()
        }
        if (!is_windows) {
            executeProcess(project.rootDir, "chmod", "755", path.path)
        }
        project.logger.quiet("Successfully download...")
        project.logger.quiet(
            "Using ${path.path} with version ${
                executeProcess(
                    project.rootDir, path.path, "--version"
                ).output.split(" ").last()
            }"
        )
        path.path
    }

    fun publishToNexus(sourceFile: String, targetFile: String): ProcessExecResult {
        println("source file: $sourceFile")
        println("target file: $targetFile")

        return project.sdevtool.execute(
            "build", "nexus-publish",
            "--file", sourceFile, "--out-path", targetFile,
        )
    }


    fun execute(command: String, vararg args: String): ProcessExecResult {
        return executeProcess(project.projectDir, execPath, command, *args)
    }

}


/**
 * Результат выполнения консольной команды
 */
data class ProcessExecResult(val state: Int, val output: String)

/**
 * Вспомогательная функция для обертки вызовов внешних процессов (например GIT)
 */
fun executeProcess(workingDir: File, vararg cmd: String): ProcessExecResult {
    val processStarted = ProcessBuilder(*cmd).redirectErrorStream(true).directory(workingDir).start()
    val processOutput = processStarted.inputStream.reader().use {
        it.readText()
    }.trim()
    processStarted.waitFor(5, TimeUnit.SECONDS)
    val state = processStarted.exitValue()
    return ProcessExecResult(state, processOutput)
}





