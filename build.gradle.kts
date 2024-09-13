import com.google.protobuf.gradle.ProtobufExtension
import com.google.protobuf.gradle.id
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "2.0.0"
    id("com.google.protobuf") version "0.9.4" apply false
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
val protobufVersion = "4.27.3"
val grpcVersion = "1.65.1"
val grpcKotlinStubVersion = "1.4.0"
val grpcPluginJavaVersion = "1.65.1"
val grpcPluginKotlinVersion = "1.4.1:jdk8@jar"
val coroutinesVersion = "1.9.0-RC"
val gsonVersion = "2.11.0"


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
        generatedFilesBaseDir = "./src"
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

    tasks.getByName("generateProto") {
        doLast {
            File("$rootDir/build/model/generated/source/proto/main/java/codes/spectrum/svdb/model/")
                .listFiles()?.forEach { dir ->
                    dir.listFiles()?.forEach {
                        it.writeText("")
                    }
                }
            File("$rootDir/build/model/generated/source/proto/main/kotlin/codes/spectrum/svdb/model/")
                .listFiles()?.forEach { dir ->
                    dir.listFiles()?.forEach {
                        it.writeText("")
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
            configs.flatMap { configurations.getByName(it.gradleValue) }
                .filter(filter)
                .distinct()
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
}


