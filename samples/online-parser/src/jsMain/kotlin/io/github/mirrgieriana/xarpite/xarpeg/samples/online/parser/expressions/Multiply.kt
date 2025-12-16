@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class Multiply(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ArithmeticOperator(left, right, position) {
    override val operatorSymbol = "*"
    override fun compute(leftValue: Double, rightValue: Double) = leftValue * rightValue
}
