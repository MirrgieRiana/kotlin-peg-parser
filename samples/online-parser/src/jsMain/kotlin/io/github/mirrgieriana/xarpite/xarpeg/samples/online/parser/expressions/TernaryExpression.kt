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
class TernaryExpression(
    private val condition: Expression,
    private val trueExpression: Expression,
    private val falseExpression: Expression,
    private val position: SourcePosition
) : Expression {
    override fun evaluate(ctx: EvaluationContext): Value {
        val condVal = condition.evaluate(ctx)
        if (condVal !is Value.BooleanValue) {
            val newCtx = ctx.copy(callStack = ctx.callStack + CallFrame("ternary operator", position))
            throw EvaluationException("Condition in ternary operator must be a boolean", newCtx, ctx.sourceCode)
        }
        return if (condVal.value) trueExpression.evaluate(ctx) else falseExpression.evaluate(ctx)
    }
}
