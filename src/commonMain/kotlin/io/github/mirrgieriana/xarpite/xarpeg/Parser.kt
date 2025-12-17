package io.github.mirrgieriana.xarpite.xarpeg

import io.github.mirrgieriana.xarpite.xarpeg.internal.escapeDoubleQuote
import io.github.mirrgieriana.xarpite.xarpeg.internal.truncate
import io.github.mirrgieriana.xarpite.xarpeg.parsers.normalize

fun interface Parser<out T : Any> {
    fun parseOrNull(context: ParseContext, start: Int): ParseResult<T>?
    val name: String? get() = null
}

val Parser<*>.nameOrString get() = this.name ?: this.toString()

data class ParseResult<out T : Any>(val value: T, val start: Int, val end: Int)

fun ParseResult<*>.text(context: ParseContext) = context.src.substring(this.start, this.end).normalize()

open class ParseException(message: String, val context: ParseContext, val position: Int) : Exception(message)


class UnmatchedInputParseException(message: String, context: ParseContext, position: Int) : ParseException(message, context, position)

class ExtraCharactersParseException(message: String, context: ParseContext, position: Int) : ParseException(message, context, position)

fun <T : Any> Parser<T>.parseAllOrThrow(src: String, useMemoization: Boolean = true) = this.parseAll(src, useMemoization).getOrThrow()

fun <T : Any> Parser<T>.parseAllOrNull(src: String, useMemoization: Boolean = true) = this.parseAll(src, useMemoization).getOrNull()

fun <T : Any> Parser<T>.parseAll(src: String, useMemoization: Boolean = true): Result<T> {
    val context = ParseContext(src, useMemoization)
    val result = context.parseOrNull(this, 0) ?: return Result.failure(UnmatchedInputParseException("Failed to parse.", context, 0))
    if (result.end != src.length) {
        val string = src.drop(result.end).truncate(10, "...").escapeDoubleQuote()
        return Result.failure(ExtraCharactersParseException("""Extra characters found after position ${result.end}: "$string"""", context, result.end))
    }
    return Result.success(result.value)
}
