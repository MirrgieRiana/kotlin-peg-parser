@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationException
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport

fun Value.requireNumber(ctx: EvaluationContext, operatorSymbol: String, side: String): Double {
    if (this !is Value.NumberValue) {
        throw EvaluationException("$side operand of $operatorSymbol must be a number", ctx, ctx.sourceCode)
    }
    return this.value
}

fun Value.requireBoolean(ctx: EvaluationContext, description: String): Boolean {
    if (this !is Value.BooleanValue) {
        throw EvaluationException("$description must be a boolean", ctx, ctx.sourceCode)
    }
    return this.value
}
