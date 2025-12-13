@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_03_expressions_md_0 {
    init {
        val expr: Parser<Int> = object {
            val number = +Regex("[0-9]+") map { it.value.toInt() }
            val paren: Parser<Int> by lazy { (-'(' * parser { root } * -')') map { value -> value } }
            val factor = number + paren
            val mul = leftAssociative(factor, -'*') { a, _, b -> a * b }
            val add = leftAssociative(mul, -'+') { a, _, b -> a + b }
            val root = add
        }.root

        expr.parseAllOrThrow("2*(3+4)") // => 14
    }
}
