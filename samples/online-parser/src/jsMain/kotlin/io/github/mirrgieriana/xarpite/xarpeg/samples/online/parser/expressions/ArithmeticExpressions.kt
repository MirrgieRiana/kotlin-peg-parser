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
abstract class ArithmeticOperatorExpression(
    protected val left: Expression,
    protected val right: Expression,
    protected val position: SourcePosition
) : Expression {
    abstract val operatorSymbol: String
    abstract fun compute(leftValue: Double, rightValue: Double): Double

    override fun evaluate(ctx: EvaluationContext): Value {
        val leftVal = left.evaluate(ctx)
        val rightVal = right.evaluate(ctx)

        if (leftVal !is Value.NumberValue) {
            throw EvaluationException("Left operand of $operatorSymbol must be a number", ctx, ctx.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            throw EvaluationException("Right operand of $operatorSymbol must be a number", ctx, ctx.sourceCode)
        }

        return Value.NumberValue(compute(leftVal.value, rightVal.value))
    }
}

@JsExport
class AddExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ArithmeticOperatorExpression(left, right, position) {
    override val operatorSymbol = "+"
    override fun compute(leftValue: Double, rightValue: Double) = leftValue + rightValue
}

@JsExport
class SubtractExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ArithmeticOperatorExpression(left, right, position) {
    override val operatorSymbol = "-"
    override fun compute(leftValue: Double, rightValue: Double) = leftValue - rightValue
}

@JsExport
class MultiplyExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ArithmeticOperatorExpression(left, right, position) {
    override val operatorSymbol = "*"
    override fun compute(leftValue: Double, rightValue: Double) = leftValue * rightValue
}

@JsExport
class DivideExpression(
    left: Expression,
    right: Expression,
    position: SourcePosition
) : ArithmeticOperatorExpression(left, right, position) {
    override val operatorSymbol = "/"

    override fun evaluate(ctx: EvaluationContext): Value {
        val leftVal = left.evaluate(ctx)
        val rightVal = right.evaluate(ctx)

        if (leftVal !is Value.NumberValue) {
            throw EvaluationException("Left operand of $operatorSymbol must be a number", ctx, ctx.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            throw EvaluationException("Right operand of $operatorSymbol must be a number", ctx, ctx.sourceCode)
        }

        if (rightVal.value == 0.0) {
            val newCtx = ctx.copy(callStack = ctx.callStack + CallFrame("division", position))
            throw EvaluationException("Division by zero", newCtx, ctx.sourceCode)
        }

        return Value.NumberValue(compute(leftVal.value, rightVal.value))
    }

    override fun compute(leftValue: Double, rightValue: Double) = leftValue / rightValue
}
