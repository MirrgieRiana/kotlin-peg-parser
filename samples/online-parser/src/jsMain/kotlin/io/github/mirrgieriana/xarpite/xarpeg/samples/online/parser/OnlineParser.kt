@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import io.github.mirrgieriana.xarpite.xarpeg.ParseContext
import io.github.mirrgieriana.xarpite.xarpeg.ParseResult
import io.github.mirrgieriana.xarpite.xarpeg.Parser
import io.github.mirrgieriana.xarpite.xarpeg.parseAllOrThrow
import io.github.mirrgieriana.xarpite.xarpeg.parsers.*
import io.github.mirrgieriana.xarpite.xarpeg.text
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

// Variable table with inheritance (parent scope lookup)
data class VariableTable(
    val variables: MutableMap<String, Value> = mutableMapOf(),
    val parent: VariableTable? = null
) {
    fun get(name: String): Value? {
        return variables[name] ?: parent?.get(name)
    }
    
    fun set(name: String, value: Value) {
        variables[name] = value
    }
    
    fun createChild(): VariableTable {
        return VariableTable(mutableMapOf(), this)
    }
}

// Evaluation context that holds call stack information and variable scope
data class EvaluationContext(
    val callStack: List<CallFrame> = emptyList(),
    val sourceCode: String? = null,
    val variableTable: VariableTable = VariableTable()
) {
    fun pushFrame(functionName: String, callPosition: SourcePosition): EvaluationContext {
        return copy(callStack = callStack + CallFrame(functionName, callPosition))
    }
    
    fun withNewScope(): EvaluationContext {
        return copy(variableTable = variableTable.createChild())
    }
}

// Represents a single call frame in the stack
data class CallFrame(val functionName: String, val position: SourcePosition)

// Represents a position in the source code
data class SourcePosition(val start: Int, val end: Int, val text: String) {
    fun formatLineColumn(source: String): String {
        val beforeStart = source.substring(0, start)
        val line = beforeStart.count { it == '\n' } + 1
        val column = start - (beforeStart.lastIndexOf('\n') + 1) + 1
        return "line $line, column $column"
    }
}

// Value types that can be stored in variables
sealed class Value {
    data class NumberValue(val value: Double) : Value() {
        override fun toString() = if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }
    data class BooleanValue(val value: Boolean) : Value() {
        override fun toString() = value.toString()
    }
    data class LambdaValue(
        val params: List<String>,
        val body: (EvaluationContext) -> Value,
        val capturedVars: MutableMap<String, Value>,
        val name: String? = null,
        val definitionPosition: SourcePosition? = null
    ) : Value() {
        override fun toString() = "<lambda(${params.joinToString(", ")})>"
    }
}

// Custom exception that includes call stack
class EvaluationException(
    message: String,
    val context: EvaluationContext? = null,
    val sourceCode: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    fun formatWithCallStack(): String {
        val sb = StringBuilder()
        sb.append("Error: $message")
        
        if (context != null && context.callStack.isNotEmpty()) {
            context.callStack.asReversed().forEach { frame ->
                val location = if (sourceCode != null) {
                    frame.position.formatLineColumn(sourceCode)
                } else {
                    "position ${frame.position.start}-${frame.position.end}"
                }
                sb.append("\n  at $location: ${frame.position.text}")
            }
        }
        
        return sb.toString()
    }
}

private object ExpressionGrammar {
    private val whitespace = -Regex("[ \\t\\r\\n]*")

    // Identifier: alphanumeric and _, but first character cannot be a digit
    private val identifier = +Regex("[a-zA-Z_][a-zA-Z0-9_]*") map { it.value }

    private val number = +Regex("[0-9]+(?:\\.[0-9]+)?") map { LiteralExpression(Value.NumberValue(it.value.toDouble())) }
    
    // Helper function for left-associative binary operator aggregation
    // Takes a term parser and operators that create binary expressions
    private fun leftAssociativeBinaryOp(
        term: Parser<Expression>,
        operators: Parser<(Expression) -> Expression>
    ): Parser<Expression> {
        return (term * operators.zeroOrMore) map { (first, rest) ->
            rest.fold(first) { acc, opFunc -> opFunc(acc) }
        }
    }

    // Variable reference
    private val variableRef: Parser<Expression> = identifier map { name ->
        VariableExpression(name)
    }

    // Helper to parse comma-separated list of identifiers
    private val identifierList: Parser<List<String>> = run {
        val restItem = whitespace * -',' * whitespace * identifier
        (identifier * restItem.zeroOrMore) map { (first, rest) -> listOf(first) + rest }
    }

    // Lambda parameter list: (param1, param2) or ()
    // The alternative (whitespace map { emptyList() }) handles empty parameter lists: ()
    private val paramList: Parser<List<String>> = 
        -'(' * whitespace * (identifierList + (whitespace map { emptyList<String>() })) * whitespace * -')'

    // Lambda expression: (param1, param2, ...) -> body
    private val lambda: Parser<Expression> =
        ((paramList * whitespace * -Regex("->") * whitespace * ref { expression }) mapEx { parseCtx, result ->
            val (params, bodyExpr: Expression) = result.value
            val lambdaText = result.text(parseCtx)
            val position = SourcePosition(result.start, result.end, lambdaText)
            LambdaDefinitionExpression(params, bodyExpr, position)
        })

    // Helper to parse comma-separated list of expressions
    private val exprList: Parser<List<Expression>> = run {
        val restItem = whitespace * -',' * whitespace * ref { expression }
        (ref { expression } * restItem.zeroOrMore) map { (first, rest) -> listOf(first) + rest }
    }

    // Argument list for function calls: (arg1, arg2) or ()
    // The alternative (whitespace map { emptyList() }) handles empty argument lists: ()
    private val argList: Parser<List<Expression>> =
        -'(' * whitespace * (exprList + (whitespace map { emptyList<Expression>() })) * whitespace * -')'

    // Function call: identifier(arg1, arg2, ...)
    private val functionCall: Parser<Expression> =
        ((identifier * whitespace * argList) mapEx { parseCtx, result ->
            val (name, args) = result.value
            val callText = result.text(parseCtx)
            val callPosition = SourcePosition(result.start, result.end, callText)
            FunctionCallExpression(name, args, callPosition, parseCtx.src)
        })

    // Primary expression: number, variable reference, function call, lambda, or grouped expression
    private val primary: Parser<Expression> =
        lambda + functionCall + variableRef + number + 
            (-'(' * whitespace * ref { expression } * whitespace * -')')

    private val factor: Parser<Expression> = primary

    // Multiplication operator parser
    private val multiplyOp = (whitespace * +'*' * whitespace * factor) mapEx { parseCtx, result ->
        val (_, rightExpr: Expression) = result.value
        val opText = result.text(parseCtx)
        val opPosition = SourcePosition(result.start, result.end, opText)
        val opFunc: (Expression) -> Expression = { leftExpr ->
            MultiplyExpression(leftExpr, rightExpr, opPosition)
        }
        opFunc
    }
    
    // Division operator parser
    private val divideOp = (whitespace * +'/' * whitespace * factor) mapEx { parseCtx, result ->
        val (_, rightExpr: Expression) = result.value
        val opText = result.text(parseCtx)
        val opPosition = SourcePosition(result.start, result.end, opText)
        val opFunc: (Expression) -> Expression = { leftExpr ->
            DivideExpression(leftExpr, rightExpr, opPosition)
        }
        opFunc
    }
    
    private val product: Parser<Expression> = 
        leftAssociativeBinaryOp(factor, multiplyOp + divideOp)

    // Addition operator parser
    private val addOp = (whitespace * +'+' * whitespace * product) mapEx { parseCtx, result ->
        val (_, rightExpr: Expression) = result.value
        val opText = result.text(parseCtx)
        val opPosition = SourcePosition(result.start, result.end, opText)
        val opFunc: (Expression) -> Expression = { leftExpr ->
            AddExpression(leftExpr, rightExpr, opPosition)
        }
        opFunc
    }
    
    // Subtraction operator parser
    private val subtractOp = (whitespace * +'-' * whitespace * product) mapEx { parseCtx, result ->
        val (_, rightExpr: Expression) = result.value
        val opText = result.text(parseCtx)
        val opPosition = SourcePosition(result.start, result.end, opText)
        val opFunc: (Expression) -> Expression = { leftExpr ->
            SubtractExpression(leftExpr, rightExpr, opPosition)
        }
        opFunc
    }
    
    private val sum: Parser<Expression> = 
        leftAssociativeBinaryOp(product, addOp + subtractOp)

    // Ordering comparison operators: <, <=, >, >=
    private val orderingComparison: Parser<Expression> = run {
        // Less than or equal operator parser (must come before < to match correctly)
        val lessEqualOp = (whitespace * +"<=" * whitespace * sum) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                LessOrEqualExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        // Greater than or equal operator parser (must come before > to match correctly)
        val greaterEqualOp = (whitespace * +">=" * whitespace * sum) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                GreaterOrEqualExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        // Less than operator parser
        val lessOp = (whitespace * +'<' * whitespace * sum) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                LessThanExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        // Greater than operator parser
        val greaterOp = (whitespace * +'>' * whitespace * sum) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                GreaterThanExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        leftAssociativeBinaryOp(sum, lessEqualOp + greaterEqualOp + lessOp + greaterOp)
    }
    // Equality comparison operators: ==, !=
    private val equalityComparison: Parser<Expression> = run {
        // Equality operator parser
        val equalOp = (whitespace * +"==" * whitespace * orderingComparison) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                EqualExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        // Inequality operator parser
        val notEqualOp = (whitespace * +"!=" * whitespace * orderingComparison) mapEx { parseCtx, result ->
            val (_, rightExpr: Expression) = result.value
            val opText = result.text(parseCtx)
            val opPosition = SourcePosition(result.start, result.end, opText)
            val opFunc: (Expression) -> Expression = { leftExpr ->
                NotEqualExpression(leftExpr, rightExpr, opPosition)
            }
            opFunc
        }
        
        leftAssociativeBinaryOp(orderingComparison, equalOp + notEqualOp)
    }

    // Ternary operator: condition ? trueExpr : falseExpr
    private val ternary: Parser<Expression> = run {
        val ternaryExpr = ref { equalityComparison } * whitespace * -'?' * whitespace *
            ref { equalityComparison } * whitespace * -':' * whitespace *
            ref { equalityComparison }
        ((ternaryExpr mapEx { parseCtx, result ->
            val (cond: Expression, trueExpr: Expression, falseExpr: Expression) = result.value
            val ternaryText = result.text(parseCtx)
            val ternaryPosition = SourcePosition(result.start, result.end, ternaryText)
            TernaryExpression(cond, trueExpr, falseExpr, ternaryPosition)
        }) + equalityComparison)
    }

    // Assignment: variable = expression
    private val assignment: Parser<Expression> = run {
        ((identifier * whitespace * -'=' * whitespace * ref { expression }) map { (name, valueExpr: Expression) ->
            AssignmentExpression(name, valueExpr)
        }) + ternary
    }

    // Root expression parser
    internal val expression: Parser<Expression> = assignment

    internal val root = whitespace * expression * whitespace
}

@JsExport
fun parseExpression(input: String): String {
    return try {
        // Reset function call counter for each evaluation to ensure each call is independent
        FunctionCallExpression.functionCallCount = 0
        
        // Create initial evaluation context with empty call stack, source code, and fresh variable table
        val initialContext = EvaluationContext(sourceCode = input)
        
        // Try to parse as a single expression first
        // If parsing succeeds, evaluate and return the result
        try {
            val resultExpr = ExpressionGrammar.root.parseAllOrThrow(input)
            val result = resultExpr.evaluate(initialContext)
            return result.toString()
        } catch (e: Exception) {
            // If single expression fails, try as multi-line program
            // Split input into lines and evaluate each line
            val lines = input.lines().filter { it.trim().isNotEmpty() }
            if (lines.isEmpty()) {
                return ""
            }
            
            // If there's only one line, rethrow the original error
            if (lines.size == 1) {
                throw e
            }
            
            val results = mutableListOf<Value>()
            for (line in lines) {
                val lineExpr = ExpressionGrammar.root.parseAllOrThrow(line)
                val lineResult = lineExpr.evaluate(initialContext)
                results.add(lineResult)
            }
            
            // Return the last result
            return results.last().toString()
        }
    } catch (e: EvaluationException) {
        // Use custom formatting if call stack is available
        if (e.context != null && e.context.callStack.isNotEmpty()) {
            e.formatWithCallStack()
        } else {
            "Error: ${e.message}"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
