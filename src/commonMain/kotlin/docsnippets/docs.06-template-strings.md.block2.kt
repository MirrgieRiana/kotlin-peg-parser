@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parsers.*

object docs_06_template_strings_md_block2 {
    object TemplateWithNestedStrings {
        val number = +Regex("[0-9]+") map { it.value.toInt() }
        val grouped: Parser<Int> by lazy { (-'(' * parser { sum } * -')') map { it.b } }

        val stringPart: Parser<TemplateElement> =
            +Regex("""[^"$]+|\$(?!\()""") map { match -> StringPart(match.value) }

        val expressionPart: Parser<TemplateElement> =
            -Regex("""\$\(""") * parser { sum } * -')' map { tuple ->
                ExpressionPart(tuple.b)
            }

        val templateElement = expressionPart + stringPart

        val templateString: Parser<String> by lazy {
            -'"' * templateElement.zeroOrMore * -'"' map { tuple ->
                val elements = tuple.b
                elements.joinToString("") { element ->
                    when (element) {
                        is StringPart -> element.text
                        is ExpressionPart -> element.value.toString()
                    }
                }
            }
        }

        // Now expressions can contain template strings
        val factor: Parser<Int> = number + grouped + (templateString map { it.length })
        val sum: Parser<Int> = leftAssociative(factor, -'+') { a, _, b -> a + b }
    }
}
