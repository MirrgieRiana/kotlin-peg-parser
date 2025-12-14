val defaultRepoPath = "MirrgieRiana/xarpeg-kotlin-peg-parser"
val repoRoot = rootDir.parentFile?.parentFile?.canonicalFile

val repoPathFromFile = generateSequence(rootDir.canonicalFile) { it.parentFile }
    .takeWhile { current -> repoRoot == null || current.toPath().startsWith(repoRoot.toPath()) }
    .mapNotNull { current ->
        val propertiesFile = current.resolve("gradle.properties")
        if (!propertiesFile.isFile) return@mapNotNull null
        java.util.Properties().apply {
            propertiesFile.inputStream().use(::load)
        }.getProperty("repoPath")
    }
    .firstOrNull()

val repoPath = providers.gradleProperty("repoPath").orElse(
    providers.provider { repoPathFromFile ?: defaultRepoPath }
).get()

extra["repoPath"] = repoPath
