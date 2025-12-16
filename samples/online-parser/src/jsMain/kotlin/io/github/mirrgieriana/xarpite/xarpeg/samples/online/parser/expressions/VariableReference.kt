@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationException
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class VariableReference(private val name: String) : Expression {
    override fun evaluate(ctx: EvaluationContext): Value {
        return ctx.variableTable.get(name)
            ?: throw EvaluationException("Undefined variable: $name", ctx, ctx.sourceCode)
    }
}
