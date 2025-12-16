@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class GreaterThan(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ComparisonOperator(left, right, position) {
    override val operatorSymbol = ">"
    override fun compare(leftValue: Double, rightValue: Double) = leftValue > rightValue
}
