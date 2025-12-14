@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.js.ExperimentalJsExport

/**
 * Interface representing an expression that can be evaluated to produce a Value.
 * This replaces the (EvaluationContext) -> Value function type to provide better
 * separation of concerns and allow implementations in separate files.
 */
interface Expression {
    /**
     * Evaluate this expression in the given context and return the resulting value.
     */
    fun evaluate(context: EvaluationContext): Value
}

/**
 * Simple wrapper to convert a lambda to an Expression
 */
class LambdaExpression(private val lambda: (EvaluationContext) -> Value) : Expression {
    override fun evaluate(context: EvaluationContext): Value = lambda(context)
}

/**
 * Helper function to convert Expression to the functional form for compatibility
 */
fun Expression.toFunction(): (EvaluationContext) -> Value = { context -> evaluate(context) }

/**
 * Helper function to convert a function to Expression
 */
fun ((EvaluationContext) -> Value).toExpression(): Expression = LambdaExpression(this)
