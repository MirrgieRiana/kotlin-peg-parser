@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class Program(private val expressions: List<Expression>) : Expression {
    override fun evaluate(ctx: EvaluationContext): Value {
        var result: Value = Value.NumberValue(0.0)
        for (expr in expressions) {
            result = expr.evaluate(ctx)
        }
        return result
    }
}
