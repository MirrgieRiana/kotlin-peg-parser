@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.CallFrame
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationException
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
abstract class ComparisonOperatorExpression(
    protected val left: Expression,
    protected val right: Expression,
    protected val position: SourcePosition
) : Expression {
    abstract val operatorSymbol: String
    abstract fun compare(leftValue: Double, rightValue: Double): Boolean

    override fun evaluate(ctx: EvaluationContext): Value {
        val leftVal = left.evaluate(ctx)
        val rightVal = right.evaluate(ctx)

        if (leftVal !is Value.NumberValue) {
            val newCtx = ctx.copy(callStack = ctx.callStack + CallFrame("$operatorSymbol operator", position))
            throw EvaluationException("Left operand of $operatorSymbol must be a number", newCtx, ctx.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = ctx.copy(callStack = ctx.callStack + CallFrame("$operatorSymbol operator", position))
            throw EvaluationException("Right operand of $operatorSymbol must be a number", newCtx, ctx.sourceCode)
        }

        return Value.BooleanValue(compare(leftVal.value, rightVal.value))
    }
}

@JsExport
class LessThanExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ComparisonOperatorExpression(left, right, position) {
    override val operatorSymbol = "<"
    override fun compare(leftValue: Double, rightValue: Double) = leftValue < rightValue
}

@JsExport
class LessThanOrEqualExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ComparisonOperatorExpression(left, right, position) {
    override val operatorSymbol = "<="
    override fun compare(leftValue: Double, rightValue: Double) = leftValue <= rightValue
}

@JsExport
class GreaterThanExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ComparisonOperatorExpression(left, right, position) {
    override val operatorSymbol = ">"
    override fun compare(leftValue: Double, rightValue: Double) = leftValue > rightValue
}

@JsExport
class GreaterThanOrEqualExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ComparisonOperatorExpression(left, right, position) {
    override val operatorSymbol = ">="
    override fun compare(leftValue: Double, rightValue: Double) = leftValue >= rightValue
}
