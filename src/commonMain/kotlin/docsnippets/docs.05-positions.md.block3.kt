@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.text
import mirrg.xarpite.parser.parsers.*

val number = +Regex("[0-9]+")

val numberWithText = number mapEx { ctx, result ->
    val matched = result.text(ctx)
    val value = matched.toInt()
    "Parsed '$matched' as $value"
}

fun main() {
    numberWithText.parseAllOrThrow("123") // => "Parsed '123' as 123"
}
