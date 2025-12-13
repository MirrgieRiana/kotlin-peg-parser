@file:Suppress("unused", "UNCHECKED_CAST", "CANNOT_INFER_PARAMETER_TYPE")
package docsnippets

import mirrg.xarpite.parser.parsers.*
import mirrg.xarpite.parser.Tuple1

private object Block_docs_06_template_strings_md_1 {
    init {
        val stringPartRegexParser = +Regex("""[^"$]+|\$(?!\()""")
    }
}
