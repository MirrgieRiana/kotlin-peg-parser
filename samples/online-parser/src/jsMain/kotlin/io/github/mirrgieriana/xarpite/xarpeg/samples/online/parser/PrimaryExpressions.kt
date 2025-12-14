@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.js.ExperimentalJsExport

/**
 * A literal value expression (number or boolean)
 */
class LiteralExpression(private val value: Value) : Expression {
    override fun evaluate(context: EvaluationContext): Value = value
}

/**
 * Variable reference expression
 */
class VariableExpression(private val name: String) : Expression {
    override fun evaluate(context: EvaluationContext): Value {
        return context.variableTable.get(name)
            ?: throw EvaluationException("Undefined variable: $name", context, context.sourceCode)
    }
}

/**
 * Lambda expression
 */
class LambdaDefinitionExpression(
    private val params: List<String>,
    private val body: Expression,
    private val position: SourcePosition
) : Expression {
    override fun evaluate(context: EvaluationContext): Value {
        return Value.LambdaValue(params, body.toFunction(), mutableMapOf(), definitionPosition = position)
    }
}

/**
 * Function call expression
 */
class FunctionCallExpression(
    private val functionName: String,
    private val arguments: List<Expression>,
    private val position: SourcePosition,
    private val sourceCode: String
) : Expression {
    companion object {
        var functionCallCount = 0
        private const val MAX_FUNCTION_CALLS = 100
    }
    
    override fun evaluate(context: EvaluationContext): Value {
        val func = context.variableTable.get(functionName)
            ?: throw EvaluationException("Undefined function: $functionName", context, context.sourceCode)
        
        return when (func) {
            is Value.LambdaValue -> {
                if (arguments.size != func.params.size) {
                    throw EvaluationException(
                        "Function $functionName expects ${func.params.size} arguments, but got ${arguments.size}",
                        context,
                        sourceCode
                    )
                }
                
                // Check function call limit before making the call
                functionCallCount++
                if (functionCallCount >= MAX_FUNCTION_CALLS) {
                    throw EvaluationException(
                        "Maximum function call limit ($MAX_FUNCTION_CALLS) exceeded",
                        context,
                        sourceCode
                    )
                }
                
                // Create a new scope for the function call
                // Push call frame onto the stack and create new variable scope
                val newContext = context.pushFrame(functionName, position).withNewScope()
                
                // Evaluate arguments in the caller's context and bind to parameters in the new scope
                func.params.zip(arguments).forEach { (param, argExpr) ->
                    newContext.variableTable.set(param, argExpr.evaluate(context))
                }
                
                // Execute function body in the new context
                func.body(newContext)
            }
            else -> throw EvaluationException("$functionName is not a function", context, context.sourceCode)
        }
    }
}

/**
 * Variable assignment expression
 */
class AssignmentExpression(
    private val variableName: String,
    private val value: Expression
) : Expression {
    override fun evaluate(context: EvaluationContext): Value {
        val result = value.evaluate(context)
        context.variableTable.set(variableName, result)
        return result
    }
}
