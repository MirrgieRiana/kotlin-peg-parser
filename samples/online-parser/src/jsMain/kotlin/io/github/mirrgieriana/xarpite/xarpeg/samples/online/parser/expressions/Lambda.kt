@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions

import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.EvaluationContext
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.SourcePosition
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
class Lambda(
    private val params: List<String>,
    private val body: Expression,
    private val position: SourcePosition
) : Expression {
    override fun evaluate(ctx: EvaluationContext): Value {
        return Value.LambdaValue(params, body, mutableMapOf(), definitionPosition = position)
    }
}
