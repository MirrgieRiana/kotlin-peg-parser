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
                    codeBlocks.forEachIndexed { index, originalBlock ->
                        // Skip blocks that contain Gradle DSL keywords (documentation examples)
                        if (originalBlock.contains("repositories {") || originalBlock.contains("dependencies {")) {
                            println("Skipped (Gradle DSL): ${relativePath} block$index")
                            return@forEachIndexed
                        }
                        
                        val imports = linkedSetOf<String>()
                        val blockBody = originalBlock.lines().filterNot { line ->
                            val trimmed = line.trim()
                            if (trimmed.startsWith("import ")) {
                                imports.add(trimmed)
                                true
                            } else false
                        }.joinToString("\n").trim()

                        // Add default imports if no imports were found
                        val effectiveImports = if (imports.isEmpty()) {
                            linkedSetOf(
                                "import mirrg.xarpite.parser.Parser",
                                "import mirrg.xarpite.parser.parseAllOrThrow",
                                "import mirrg.xarpite.parser.parsers.*"
                            )
                        } else {
                            LinkedHashSet(imports)
                        }.apply {
                            if (blockBody.contains("Tuple1")) add("import mirrg.xarpite.parser.Tuple1")
                        }

                        val fileContent = buildString {
                            appendLine("@file:Suppress(\"unused\", \"UNCHECKED_CAST\", \"CANNOT_INFER_PARAMETER_TYPE\")")
                            appendLine("package docsnippets")
                            appendLine()
                            effectiveImports.forEach { appendLine(it) }
                            if (effectiveImports.isNotEmpty()) appendLine()
                            
                            // Parse to separate top-level declarations from function body
                            val lines = blockBody.lines()
                            val topLevelDeclarations = mutableListOf<String>()
                            val functionBody = mutableListOf<String>()
                            
                            var i = 0
                            while (i < lines.size) {
                                val line = lines[i]
                                val trimmed = line.trim()
                                
                                // Check if this is a top-level declaration that can't be in a function
                                // Note: anonymous objects (object { }) and object expressions (val x = object { }) are allowed in functions
                                val isNamedObject = trimmed.startsWith("object ") && 
                                    !trimmed.startsWith("object {") && 
                                    !trimmed.startsWith("object:") &&
                                    !line.contains(" = object ")  // Not an object expression assignment
                                if (trimmed.startsWith("sealed ") || 
                                    trimmed.startsWith("data class ") ||
                                    isNamedObject ||
                                    trimmed.startsWith("enum ")) {
                                    // Find the end of this declaration by tracking braces
                                    var braceCount = 0
                                    val declarationLines = mutableListOf<String>()
                                    var j = i
                                    while (j < lines.size) {
                                        val declLine = lines[j]
                                        declarationLines.add(declLine)
                                        braceCount += declLine.count { it == '{' }
                                        braceCount -= declLine.count { it == '}' }
                                        j++
                                        if (braceCount == 0 && declLine.trim().isNotEmpty()) {
                                            break
                                        }
                                    }
                                    topLevelDeclarations.addAll(declarationLines)
                                    i = j
                                } else {
                                    functionBody.add(line)
                                    i++
                                }
                            }
                            
                            // Output top-level declarations first
                            if (topLevelDeclarations.isNotEmpty()) {
                                topLevelDeclarations.forEach { line ->
                                    appendLine(line)
                                }
                                appendLine()
                            }
                            
                            // Wrap everything in an object to allow both declarations and executable statements
                            if (topLevelDeclarations.isNotEmpty() || functionBody.isNotEmpty()) {
                                val sanitizedName = relativePath.replace("/", "_").replace(".", "_").replace(Regex("[^A-Za-z0-9_]"), "_")
                                if (topLevelDeclarations.isEmpty()) {
                                    // No extracted declarations, so wrap in a dummy object with init
                                    appendLine("private object Block_${sanitizedName}_$index {")
                                    appendLine("    init {")
                                    functionBody.forEach { line ->
                                        if (line.isNotEmpty()) {
                                            appendLine("        $line")
                                        } else {
                                            appendLine()
                                        }
                                    }
                                    appendLine("    }")
                                    appendLine("}")
                                } else {
                                    // Has top-level declarations already extracted, wrap remaining in function
                                    appendLine("private fun block_${sanitizedName}_$index() {")
                                    functionBody.forEach { line ->
                                        if (line.isNotEmpty()) {
                                            appendLine("    $line")
                                        } else {
                                            appendLine()
                                        }
                                    }
                                    appendLine("}")
                                }
                            }
                        }
                        val blockFile = generatedDocSrc.file("${relativePath.replace("/", ".")}.block$index.kt").asFile
                        blockFile.parentFile.mkdirs()
                        blockFile.writeText(fileContent)
                        println("Generated: ${blockFile.absolutePath}")
                    }
                } else {
                    println("Skipped (no Kotlin blocks): ${relativePath}")
                }
            }
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateDocSrc")
}
