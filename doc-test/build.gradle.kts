plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}

val generatedDocSrc = layout.projectDirectory.dir("src/main/kotlin")

tasks.register("generateDocSrc") {
    description = "Extracts Kotlin code blocks from README.md and docs into doc-test sources"
    group = "documentation"

    inputs.files(project.rootProject.file("README.md"), project.rootProject.fileTree("docs") { include("**/*.md") })
    outputs.dir(generatedDocSrc)

    doLast {
        generatedDocSrc.asFile.deleteRecursively()
        val kotlinBlockRegex = Regex("""^[ \t]*```kotlin\s*(?:\r?\n)?(.*?)(?:\r?\n)?[ \t]*```""", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
        val projectDirFile = project.rootProject.projectDir
        val sourceFiles = (listOf(project.rootProject.file("README.md")) + project.rootProject.fileTree("docs") { include("**/*.md") }.files)
            .map { sourceFile ->
                val relativePath = sourceFile.relativeTo(projectDirFile).path.replace('\\', '/')
                relativePath to sourceFile
            }

        sourceFiles
            .sortedBy { it.first }
            .forEach { (relativePath, sourceFile) ->
                val codeBlocks = kotlinBlockRegex.findAll(sourceFile.readText()).map { it.groupValues[1].trimEnd() }.toList()
                if (codeBlocks.isNotEmpty()) {
                    val imports = linkedSetOf<String>()
                    val blocksWithBodies = codeBlocks.mapIndexed { index, originalBlock ->
                        val body = originalBlock.lines().filterNot { line ->
                            val trimmed = line.trim()
                            if (trimmed.startsWith("import ")) {
                                imports.add(trimmed)
                                true
                            } else false
                        }.joinToString("\n").trim()
                        index to body
                    }

                    val objectBase = relativePath.replace("/", "_").replace(".", "_").replace(Regex("[^A-Za-z0-9_]"), "_")
                    val fileContent = buildString {
                        appendLine("@file:Suppress(\"unused\")")
                        appendLine("package docsnippets")
                        appendLine()
                        imports.forEach { appendLine(it) }
                        if (imports.isNotEmpty()) appendLine()
                        blocksWithBodies.forEach { (index, body) ->
                            val objectName = "${objectBase}_block$index"
                            appendLine("object $objectName {")
                            if (body.isNotEmpty()) {
                                body.lines().forEach { line ->
                                    if (line.isNotEmpty()) {
                                        appendLine("    $line")
                                    } else {
                                        appendLine()
                                    }
                                }
                            }
                            appendLine("}")
                            appendLine()
                        }
                    }
                    val outputFile = generatedDocSrc.file("${relativePath.replace("/", ".")}.kt").asFile
                    outputFile.parentFile.mkdirs()
                    outputFile.writeText(fileContent.trimEnd() + "\n")
                    println("Generated: ${outputFile.absolutePath}")
                } else {
                    println("Skipped (no Kotlin blocks): ${relativePath}")
                }
            }
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateDocSrc")
}
