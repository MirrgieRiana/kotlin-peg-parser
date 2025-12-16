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
abstract class EqualityOperator(
    protected val left: Expression,
    protected val right: Expression,
    protected val position: SourcePosition
) : Expression {
    abstract val operatorSymbol: String
    abstract fun compareValues(result: Boolean): Boolean

    override fun evaluate(ctx: EvaluationContext): Value {
        val leftVal = left.evaluate(ctx)
        val rightVal = right.evaluate(ctx)

        val compareResult = when {
            leftVal is Value.NumberValue && rightVal is Value.NumberValue -> leftVal.value == rightVal.value
            leftVal is Value.BooleanValue && rightVal is Value.BooleanValue -> leftVal.value == rightVal.value
            else -> {
                val newCtx = ctx.copy(callStack = ctx.callStack + CallFrame("$operatorSymbol operator", position))
                throw EvaluationException("Operands of $operatorSymbol must be both numbers or both booleans", newCtx, ctx.sourceCode)
            }
        }

        return Value.BooleanValue(compareValues(compareResult))
    }
}
