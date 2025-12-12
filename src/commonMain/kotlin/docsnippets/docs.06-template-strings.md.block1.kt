@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parsers.*

object docs_06_template_strings_md_block1 {
    val stringPartRegexParser = +Regex("""[^"$]+|\$(?!\()""")
}
