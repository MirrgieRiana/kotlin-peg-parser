@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.text
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_05_positions_md_3 {
    init {
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
}
