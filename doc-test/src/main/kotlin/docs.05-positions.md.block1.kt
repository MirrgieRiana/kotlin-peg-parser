@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.text
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_05_positions_md_1 {
    init {
        val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*")

        // Access position information without changing the parser's type
        val identifierWithPosition = identifier mapEx { ctx, result ->
            "${result.value.value}@${result.start}-${result.end}"
        }

        identifierWithPosition.parseAllOrThrow("hello") // => "hello@0-5"
    }
}
