plugins {
    kotlin("jvm")
    application
}

val defaultRepoPath = "MirrgieRiana/xarpeg-kotlin-peg-parser"
val repoPath = providers.gradleProperty("repoPath").orElse(
    providers.provider {
        val propertiesFile = rootDir.resolve("../../gradle.properties")
        if (propertiesFile.isFile) {
            java.util.Properties().apply {
                propertiesFile.inputStream().use(::load)
            }.getProperty("repoPath") ?: defaultRepoPath
        } else {
            defaultRepoPath
        }
    }
).get()

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://raw.githubusercontent.com/$repoPath/maven/maven") }
}

group = "mirrg.xarpite.samples"
version = libs.versions.xarpeg.get()

application {
    mainClass.set("io.github.mirrgieriana.xarpite.xarpeg.samples.java_run.MainKt")
}

dependencies {
    implementation(libs.xarpeg)
    testImplementation(kotlin("test"))
}
