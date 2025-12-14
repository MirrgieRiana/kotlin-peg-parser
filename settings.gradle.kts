val repoName = providers.gradleProperty("repoName").orElse("xarpeg-kotlin-peg-parser").get()

rootProject.name = repoName
include("doc-test")
