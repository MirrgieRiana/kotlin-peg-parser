@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

val number = +Regex("[0-9]+") map { it.value.toInt() }

fun main() {
    number.parseAllOrThrow("42") // => 42 (just the value)
}
