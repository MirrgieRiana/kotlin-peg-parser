@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationException
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class FunctionCallExpression(
    private val name: String,
    private val args: List<Expression>,
    private val position: SourcePosition,
    private val sourceCode: String
) : Expression {
    override fun evaluate(ctx: EvaluationContext): Value {
        val func = ctx.variableTable.get(name)
            ?: throw EvaluationException("Undefined function: $name", ctx, ctx.sourceCode)

        if (func !is Value.LambdaValue) {
            throw EvaluationException("$name is not a function", ctx, ctx.sourceCode)
        }

        validateArguments(func)
        checkCallLimit()

        val newContext = ctx.pushFrame(name, position).withNewScope()
        bindArguments(func, newContext, ctx)

        return func.body.evaluate(newContext)
    }

    private fun validateArguments(func: Value.LambdaValue) {
        if (args.size != func.params.size) {
            throw EvaluationException(
                "Function $name expects ${func.params.size} arguments, but got ${args.size}",
                null,
                sourceCode
            )
        }
    }

    private fun checkCallLimit() {
        functionCallCount++
        if (functionCallCount >= MAX_FUNCTION_CALLS) {
            throw EvaluationException(
                "Maximum function call limit ($MAX_FUNCTION_CALLS) exceeded",
                null,
                sourceCode
            )
        }
    }

    private fun bindArguments(func: Value.LambdaValue, newContext: EvaluationContext, callerContext: EvaluationContext) {
        func.params.zip(args).forEach { (param, argExpr) ->
            newContext.variableTable.set(param, argExpr.evaluate(callerContext))
        }
    }

    companion object {
        var functionCallCount = 0
        private const val MAX_FUNCTION_CALLS = 100
    }
}
