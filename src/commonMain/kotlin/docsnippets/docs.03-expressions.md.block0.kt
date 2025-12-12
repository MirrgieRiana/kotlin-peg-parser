@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

val expr: Parser<Int> = object {
    val number = +Regex("[0-9]+") map { it.value.toInt() }
    val paren: Parser<Int> by lazy { (-'(' * parser { root } * -')') map { (_, value, _) -> value } }
    val factor = number + paren
    val mul = leftAssociative(factor, -'*') { a, _, b -> a * b }
    val add = leftAssociative(mul, -'+') { a, _, b -> a + b }
    val root = add
}.root

expr.parseAllOrThrow("2*(3+4)") // => 14
