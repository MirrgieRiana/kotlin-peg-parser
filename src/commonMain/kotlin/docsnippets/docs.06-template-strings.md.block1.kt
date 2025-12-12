@file:Suppress("unused")
package docsnippets

import mirrg.xarpite.parser.parsers.*

val stringPartRegexParser = +Regex("""[^"$]+|\$(?!\()""")
