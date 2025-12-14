val defaultRepoPath = "MirrgieRiana/xarpeg-kotlin-peg-parser"
val repoRoot = rootDir.parentFile?.parentFile?.canonicalFile

val repoPath = providers.gradleProperty("repoPath").orElse(
    providers.provider {
        val propertiesFile = rootDir.resolve("../../gradle.properties").normalize().absoluteFile
        val isInsideRoot = repoRoot?.let { propertiesFile.toPath().startsWith(it.toPath()) } ?: true
        if (isInsideRoot && propertiesFile.isFile) {
            java.util.Properties().apply {
                propertiesFile.inputStream().use(::load)
            }.getProperty("repoPath") ?: defaultRepoPath
        } else {
            defaultRepoPath
        }
    }
).get()

extra["repoPath"] = repoPath
