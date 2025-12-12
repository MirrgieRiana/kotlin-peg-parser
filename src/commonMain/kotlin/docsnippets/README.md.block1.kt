@file:Suppress("unused")
package docsnippets

object README_md_block1 {
    repositories {
        maven { url = uri("https://raw.githubusercontent.com/MirrgieRiana/kotlin-peg-parser/maven/maven") }
    }

    dependencies {
        implementation("io.github.mirrgieriana.xarpite:kotlin-peg-parser:1.0.3")
    }
}
