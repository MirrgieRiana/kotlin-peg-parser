@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

object docs_06_template_strings_md_block0 {
    // Define the result types
    sealed class TemplateElement
    data class StringPart(val text: String) : TemplateElement()
    data class ExpressionPart(val value: Int) : TemplateElement()

    val templateStringParser: Parser<String> = object {
        // Expression parser (reusing from earlier tutorials)
        val number = +Regex("[0-9]+") map { it.value.toInt() }
        val grouped: Parser<Int> by lazy { (-'(' * parser { sum } * -')') map { it.b } }
        val factor: Parser<Int> = number + grouped
        val product = leftAssociative(factor, -'*') { a, _, b -> a * b }
        val sum: Parser<Int> = leftAssociative(product, -'+') { a, _, b -> a + b }
        val expression = sum

        // String parts: match everything except $( and closing "
        // The key insight: use a regex that stops before template markers
        val stringPart: Parser<TemplateElement> =
            +Regex("""[^"$]+|\$(?!\()""") map { match ->
                StringPart(match.value)
            }

        // Expression part: $(...)
        val expressionPart: Parser<TemplateElement> =
            -Regex("""\$\(""") * expression * -')' map { tuple ->
                ExpressionPart(tuple.b)
            }

        // Template elements can be string parts or expression parts
        val templateElement = expressionPart + stringPart

        // A complete template string: "..." with any number of elements
        val templateString: Parser<String> =
            -'"' * templateElement.zeroOrMore * -'"' map { tuple ->
                val elements = tuple.b
                elements.joinToString("") { element ->
                    when (element) {
                        is StringPart -> element.text
                        is ExpressionPart -> element.value.toString()
                    }
                }
            }
        
        val root = templateString
    }.root

    fun main() {
        check(templateStringParser.parseAllOrThrow(""""hello"""") == "hello")
        
        check(templateStringParser.parseAllOrThrow(""""result: $(1+2)"""") == "result: 3")
        
        check(templateStringParser.parseAllOrThrow(""""$(2*(3+4)) = answer"""") == "14 = answer")
        
        check(templateStringParser.parseAllOrThrow(""""a$(1)b$(2)c$(3)d"""") == "a1b2c3d")
    }
}

object docs_06_template_strings_md_block1 {
    val stringPartRegexParser = +Regex("""[^"$]+|\$(?!\()""")
}

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
