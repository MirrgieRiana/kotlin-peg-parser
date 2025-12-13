@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_01_quickstart_md_0 {
    init {
        val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*") map { it.value }
        val number = +Regex("[0-9]+") map { it.value.toInt() }
        val kv: Parser<Pair<String, Int>> =
            identifier * -'=' * number map { (key, value) -> key to value }

        fun main() {
            check(kv.parseAllOrThrow("count=42") == ("count" to 42))
        }
    }
}
