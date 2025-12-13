@file:Suppress("unused")
package docsnippets

object docs_02_combinators_md_block0 {
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
