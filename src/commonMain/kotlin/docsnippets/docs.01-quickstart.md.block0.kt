@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

object docs_01_quickstart_md_block0 {
    val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*") map { it.value }
    val number = +Regex("[0-9]+") map { it.value.toInt() }
    val kv: Parser<Pair<String, Int>> =
        identifier * -'=' * number map { (key, value) -> key to value }

    fun main() {
        println(kv.parseAllOrThrow("count=42")) // => (count, 42)
    }
}
