package mirrg.xarpite.parser

import io.github.mirrgieriana.xarpite.xarpeg.ParseContext
import io.github.mirrgieriana.xarpite.xarpeg.ParseResult
import io.github.mirrgieriana.xarpite.xarpeg.Parser

object NumberParser : Parser<Int> {
    override fun parseOrNull(context: ParseContext, start: Int): ParseResult<Int>? {
        val sb = StringBuilder()
        var index = start
        while (index < context.src.length) {
            val char = context.src[index]
            if (char in '0'..'9') {
                sb.append(char)
                index++
            } else {
                break
            }
        }
        if (sb.isEmpty()) return null
        val number = sb.toString().toInt()
        return ParseResult(number, start, index)
    }
}
