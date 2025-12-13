@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_05_positions_md_0 {
    init {
        val number = +Regex("[0-9]+") map { it.value.toInt() }

        fun main() {
            number.parseAllOrThrow("42") // => 42 (just the value)
        }
    }
}
