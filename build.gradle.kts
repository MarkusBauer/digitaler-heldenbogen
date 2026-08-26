import com.github.gradle.node.npm.task.NpmTask
import io.freefair.gradle.plugins.aspectj.AjcAction
import java.net.URI
import java.util.zip.ZipInputStream

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.aspectj.post-compile-weaving") version "9.1.0"
    id("com.github.node-gradle.node") version "7.1.0"
}

// true = OSS repository, false = my internal multi-plugin build chain
val isStandalone = true

group = "de.mb"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

if (isStandalone) {
    node {
        nodeProjectDir.set(File("heldenbogen"))
    }
}

sourceSets.main {
    java {
        srcDirs("${project.rootDir}/src/main/java")
        include("de/mb/heldenbogen/**")
        include("de/mb/reflection/**")
        include("de/mb/autoexport/**")
        include("de/mb/config/AutoBogenConfig.java")
        include("de/mb/config/CustomGlobalConfig.java")
        include("de/mb/fork/JvmFinder.java")
        include("de/mb/heldensoftware/HeldChangeListeners.java")
        include("de/mb/*.java")
        include("de/mb/standalone/**")
        include("patches/deps/**")
        exclude("de/mb/heldenbogen/HeldenbogenManualExporter.java")  // this is just a test class
        exclude("de/mb/heldenbogen/HeldenbogenPluginIntegrated.java")  // this is just a test class
        exclude("de/mb/reflection/HeldPortraitConnectorImpl.java")  // this is just a test class
    }

    resources {
        include("templates/**")
        include("heldenbogen/**")
        if (!isStandalone) {
            srcDirs(
                "${project.rootDir}/build/resources/main",
                "${project.rootDir}/build/classes/java/main",
            )
            include("patches/deps/*.class")
        }
    }
}

tasks.compileJava {
    configure<AjcAction> {
        options {
            if (isStandalone) {
                aspectpath.setFrom(configurations.aspect)
                compilerArgs = listOf("-sourceroots", sourceSets.main.get().java.sourceDirectories.asPath)
            } else {
                // no idea how to build these aspects in the subproject.
                // there's almost zero documentation of this shitty plugin.
                // for reasons it's built in the main project, so we'll just use that class file.
                aspectpath.setFrom(files("${project.rootDir}/build/classes/java/main/patches/deps"))
            }
        }
    }

    doFirst {
        val f = File("${project.rootDir}/build/classes/java/main/patches/deps/WindowsFirewallPatch.class")
        if (!isStandalone && !f.exists()) {
            throw RuntimeException("WindowsFirewallPatch.class not found, compile root project first!")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Class-Path" to ".",
            "HeldenPluginClass" to "de.mb.standalone.HeldenbogenPluginStandalone.class"
        )
    }
}

tasks.shadowJar {
    archiveFileName = "digitaler-heldenbogen.jar"
    manifest {
        attributes(
            "HeldenPluginClass" to "de.mb.standalone.HeldenbogenPluginStandalone.class"
        )
    }
}

if (isStandalone) {
    tasks.register<NpmTask>("buildWebpack") {
        inputs.files(fileTree("heldenbogen").matching { include("*.js", "*.scss") })
        outputs.files(
            "${project.layout.buildDirectory.get()}/resources/main/heldenbogen/main.css",
            "${project.layout.buildDirectory.get()}/resources/main/heldenbogen/main.js"
        )
        args.set(listOf("run", "build"))
        dependsOn("npmInstall")
    }

    tasks.processResources {
        dependsOn("buildWebpack")
    }
}


val downloadedJar: (String) -> FileCollection = { name ->
    files(File("${rootDir.absoluteFile}/build/download/${name}.jar").absolutePath)
}

val urlFile: (String, String) -> FileCollection = { url, name ->
    val file = File("${layout.buildDirectory.get()}/download/${name}.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        URI.create(url).toURL().openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
            println("downloaded: ${file.name}")
        }
    }
    files(file.absolutePath)
}

val urlZip: (String, String) -> FileCollection = { url, name ->
    val file = File("${layout.buildDirectory.get()}/download/${name}.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        ZipInputStream(URI.create(url).toURL().openStream()).use { zipStream ->
            generateSequence { zipStream.nextEntry }.filter { !it.isDirectory }.forEach { ze ->
                var fileName = ze.name
                fileName = fileName.substring(fileName.indexOf("/") + 1)

                if (fileName == "$name.jar") {
                    file.outputStream().use { fileOut ->
                        zipStream.copyTo(fileOut)
                    }
                    println("extracted: $fileName")
                }
            }
        }
    }
    files(file.absolutePath)
}


repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.freemarker/freemarker
    implementation("org.freemarker:freemarker:2.3.34")
    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java
    implementation("org.seleniumhq.selenium:selenium-java:4.46.0")
    inpath("org.seleniumhq.selenium:selenium-api:4.46.0")
    // inpath("org.seleniumhq.selenium:selenium-java:4.46.0")

    // https://mvnrepository.com/artifact/org.apache.xmlgraphics/fop
    implementation("org.apache.xmlgraphics:fop-core:2.11")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.22.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")

    // Helden-Software and plugins
    if (isStandalone) {
        compileOnly(urlFile("https://www.helden-software.de/down/hs5/050600/helden.jar", "helden5"))
        implementation(urlFile("https://mk-bauer.de/helden-software/dl.php?pw=autoUpdater", "autoupdater"))
        compileOnly(
            urlZip(
                "https://github.com/MarkusBauer/helden-software-loader/releases/download/v1.9.2/helden-software-loader-1.9.2-release.zip",
                "CustomEntryLoader"
            )
        )
    } else {
        compileOnly(downloadedJar("helden5"))
        implementation(project(":autoupdater"))
        implementation(files("${project.rootDir}/lib/autoUpdater.jar"))
    }

    // https://mvnrepository.com/artifact/org.aspectj/aspectjrt
    implementation("org.aspectj:aspectjrt:1.9.25.1")
    // https://mvnrepository.com/artifact/org.aspectj/aspectjweaver
    implementation("org.aspectj:aspectjweaver:1.9.25.1")
}

