@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

object README_md_block0 {
    // Simple arithmetic expression parser.
    val expr: Parser<Int> = object {
        val number = +Regex("[0-9]+") map { match -> match.value.toInt() }
        val brackets: Parser<Int> by lazy { (-'(' * parser { root } * -')') map { (_, value, _) -> value } }
        val factor = number + brackets
        val mul = leftAssociative(factor, -'*') { a, _, b -> a * b }
        val add = leftAssociative(mul, -'+') { a, _, b -> a + b }
        val root = add
    }.root

    fun main() {
        check(expr.parseAllOrThrow("2*(3+4)") == 14)
    }
}

object README_md_block1 {
    repositories {
        maven { url = uri("https://raw.githubusercontent.com/MirrgieRiana/xarpeg-kotlin-peg-parser/maven/maven") }
    }

    dependencies {
        implementation("io.github.mirrgieriana.xarpite:xarpeg-kotlin-peg-parser:<latest-version>")
    }
}
