@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class Equals(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : EqualityOperator(left, right, position) {
    override val operatorSymbol = "=="
    override fun compareValues(result: Boolean) = result
}
