@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

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
