@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.text
import mirrg.xarpite.parser.parsers.*

object docs_05_positions_md_block1 {
    val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*")

    // Access position information without changing the parser's type
    val identifierWithPosition = identifier mapEx { ctx, result ->
        "${result.value.value}@${result.start}-${result.end}"
    }

    identifierWithPosition.parseAllOrThrow("hello") // => "hello@0-5"
}
