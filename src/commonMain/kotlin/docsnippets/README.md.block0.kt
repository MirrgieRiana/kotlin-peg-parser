@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

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
    println(expr.parseAllOrThrow("2*(3+4)")) // => 14
}
