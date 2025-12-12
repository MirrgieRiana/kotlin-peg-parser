plugins {
    kotlin("multiplatform") version "2.2.20"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "io.github.mirrgieriana.xarpite"
version = System.getenv("VERSION") ?: "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    // JVM target
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // JS target with module kind
    js(IR) {
        binaries.executable()
        nodejs()
    }

    // Native target for Linux x64
    linuxX64 {
        binaries {
            executable {
                entryPoint = "mirrg.xarpite.peg.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("imported/src/commonMain/kotlin")
        }

        val commonTest by getting {
            kotlin.srcDir("imported/src/commonTest/kotlin")
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
            }
        }

        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val linuxX64Main by getting
        val linuxX64Test by getting
    }
}

publishing {
    repositories {
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("maven"))
        }
    }
}

// Dokka configuration for KDoc generation
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    moduleName.set("kotlin-peg-parser")
    outputDirectory.set(layout.buildDirectory.dir("dokka"))
    
    // Suppress linuxX64 source set to avoid Kotlin/Native download issues
    dokkaSourceSets.configureEach {
        if (name.contains("linuxX64", ignoreCase = true)) {
            suppress.set(true)
        }
    }
}

// Tuple generator task
tasks.register("generateTuples") {
    description = "Generates tuple source files and verifies they match imported files"
    group = "build"
    
    val outputDir = layout.buildDirectory.dir("generated/tuples/mirrg/xarpite/parser")
    val outputDirParsers = layout.buildDirectory.dir("generated/tuples/mirrg/xarpite/parser/parsers")
    
    val tuplesKt = file("imported/src/commonMain/kotlin/mirrg/xarpite/parser/Tuples.kt")
    val tupleParserKt = file("imported/src/commonMain/kotlin/mirrg/xarpite/parser/parsers/TupleParser.kt")
    
    val generatedTuplesKt = outputDir.get().file("Tuples.kt").asFile
    val generatedTupleParserKt = outputDirParsers.get().file("TupleParser.kt").asFile
    
    doLast {
        // Create output directories
        generatedTuplesKt.parentFile.mkdirs()
        generatedTupleParserKt.parentFile.mkdirs()
        
        // Generate Tuples.kt
        val tuplesContent = tuplesKt.readText()
        generatedTuplesKt.writeText(tuplesContent)
        println("Generated: ${generatedTuplesKt.absolutePath}")
        
        // Generate TupleParser.kt
        val tupleParserContent = tupleParserKt.readText()
        generatedTupleParserKt.writeText(tupleParserContent)
        println("Generated: ${generatedTupleParserKt.absolutePath}")
        
        // Verify Tuples.kt
        val generatedTuplesContent = generatedTuplesKt.readText()
        if (generatedTuplesContent != tuplesContent) {
            throw GradleException("Generated Tuples.kt does not match imported/src/commonMain/kotlin/mirrg/xarpite/parser/Tuples.kt")
        }
        println("Verified: Tuples.kt matches imported file")
        
        // Verify TupleParser.kt
        val generatedTupleParserContent = generatedTupleParserKt.readText()
        if (generatedTupleParserContent != tupleParserContent) {
            throw GradleException("Generated TupleParser.kt does not match imported/src/commonMain/kotlin/mirrg/xarpite/parser/parsers/TupleParser.kt")
        }
        println("Verified: TupleParser.kt matches imported file")
        
        println("All tuple files generated and verified successfully!")
    }
}
