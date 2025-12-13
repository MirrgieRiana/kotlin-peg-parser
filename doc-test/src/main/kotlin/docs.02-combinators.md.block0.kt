@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_02_combinators_md_0 {
    init {
        val sign = (+'+' + +'-').optional map { it.a ?: '+' }
        val unsigned = +Regex("[0-9]+") map { it.value.toInt() }
        val signedInt = sign * unsigned map { (s, value) ->
            if (s == '-') -value else value
        }

        val repeatedA = (+'a').oneOrMore map { it.joinToString("") }

        signedInt.parseAllOrThrow("-42") // => -42
        signedInt.parseAllOrThrow("99")  // => 99
        repeatedA.parseAllOrThrow("aaaa") // => "aaaa"
    }
}
