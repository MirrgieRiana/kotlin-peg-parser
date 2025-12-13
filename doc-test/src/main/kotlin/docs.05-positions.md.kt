@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.text
import mirrg.xarpite.parser.Parser

object docs_05_positions_md_block0 {
    val number = +Regex("[0-9]+") map { it.value.toInt() }

    fun main() {
        number.parseAllOrThrow("42") // => 42 (just the value)
    }
}

object docs_05_positions_md_block1 {
    val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*")

    // Access position information without changing the parser's type
    val identifierWithPosition = identifier mapEx { ctx, result ->
        "${result.value.value}@${result.start}-${result.end}"
    }

    identifierWithPosition.parseAllOrThrow("hello") // => "hello@0-5"
}

object docs_05_positions_md_block2 {
    data class Located<T>(val value: T, val line: Int, val column: Int)

    fun <T : Any> Parser<T>.withLocation(): Parser<Located<T>> = this mapEx { ctx, result ->
        // Calculate line and column from position
        val text = ctx.src.substring(0, result.start)
        val line = text.count { it == '\n' } + 1
        val column = text.length - (text.lastIndexOf('\n') + 1) + 1
        Located(result.value, line, column)
    }

    val keyword = +Regex("[a-z]+") map { it.value }
    val keywordWithLocation = keyword.withLocation()

    val result = keywordWithLocation.parseAllOrThrow("hello")
    // => Located(value=hello, line=1, column=1)
}

object docs_05_positions_md_block3 {
    val number = +Regex("[0-9]+")

    val numberWithText = number mapEx { ctx, result ->
        val matched = result.text(ctx)
        val value = matched.toInt()
        "Parsed '$matched' as $value"
    }

    fun main() {
        numberWithText.parseAllOrThrow("123") // => "Parsed '123' as 123"
    }
}
