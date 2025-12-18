@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import io.github.mirrgieriana.xarpite.xarpeg.ParseContext
import io.github.mirrgieriana.xarpite.xarpeg.ParseException
import io.github.mirrgieriana.xarpite.xarpeg.ParseResult
import io.github.mirrgieriana.xarpite.xarpeg.Parser
import io.github.mirrgieriana.xarpite.xarpeg.parseAllOrThrow
import io.github.mirrgieriana.xarpite.xarpeg.parsers.leftAssociative
import io.github.mirrgieriana.xarpite.xarpeg.parsers.map
import io.github.mirrgieriana.xarpite.xarpeg.parsers.mapEx
import io.github.mirrgieriana.xarpite.xarpeg.parsers.named
import io.github.mirrgieriana.xarpite.xarpeg.parsers.plus
import io.github.mirrgieriana.xarpite.xarpeg.parsers.ref
import io.github.mirrgieriana.xarpite.xarpeg.parsers.times
import io.github.mirrgieriana.xarpite.xarpeg.parsers.unaryMinus
import io.github.mirrgieriana.xarpite.xarpeg.parsers.unaryPlus
import io.github.mirrgieriana.xarpite.xarpeg.parsers.zeroOrMore
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.AddExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.AssignmentExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.DivideExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.EqualsExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.Expression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.FunctionCallExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.GreaterThanExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.GreaterThanOrEqualExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.LambdaExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.LessThanExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.LessThanOrEqualExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.MultiplyExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.NotEqualsExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.NumberLiteralExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.ProgramExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.SubtractExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.TernaryExpression
import io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser.expressions.VariableReferenceExpression
import io.github.mirrgieriana.xarpite.xarpeg.text
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

// Variable table with inheritance (parent scope lookup)
data class VariableTable(
    val variables: MutableMap<String, Value> = mutableMapOf(),
    val parent: VariableTable? = null
) {
    fun get(name: String): Value? = variables[name] ?: parent?.get(name)

    fun set(name: String, value: Value) {
        variables[name] = value
    }

    fun createChild(): VariableTable = VariableTable(mutableMapOf(), this)
}

// Evaluation context that holds call stack information and variable scope
data class EvaluationContext(
    val callStack: List<CallFrame> = emptyList(),
    val sourceCode: String? = null,
    val variableTable: VariableTable = VariableTable()
) {
    fun pushFrame(functionName: String, callPosition: SourcePosition): EvaluationContext =
        copy(callStack = callStack + CallFrame(functionName, callPosition))

    fun withNewScope(): EvaluationContext =
        copy(variableTable = variableTable.createChild())
}

// Represents a single call frame in the stack
data class CallFrame(val functionName: String, val position: SourcePosition)

// Represents a position in the source code
data class SourcePosition(val start: Int, val end: Int, val text: String) {
    private fun calculateLineAndColumn(source: String): Pair<Int, Int> {
        val beforeStart = source.substring(0, start.coerceAtMost(source.length))
        val line = beforeStart.count { it == '\n' } + 1
        val column = start - (beforeStart.lastIndexOf('\n') + 1) + 1
        return line to column
    }

    fun formatLineColumn(source: String): String {
        val (line, column) = calculateLineAndColumn(source)
        return "line $line, column $column"
    }

    fun formatWithContext(source: String): String {
        val (line, column) = calculateLineAndColumn(source)
        val sourceLine = extractSourceLine(source)
        val highlightedLine = buildHighlightedLine(source, sourceLine)
        return "line $line, column $column: $highlightedLine"
    }

    private fun extractSourceLine(source: String): String {
        val beforeStart = source.substring(0, start.coerceAtMost(source.length))
        val lineStart = beforeStart.lastIndexOf('\n') + 1
        val lineEnd = source.indexOf('\n', start).let { if (it == -1) source.length else it }
        return source.substring(lineStart, lineEnd)
    }

    private fun buildHighlightedLine(source: String, sourceLine: String): String {
        val beforeStart = source.substring(0, start.coerceAtMost(source.length))
        val lineStart = beforeStart.lastIndexOf('\n') + 1
        val highlightStart = start - lineStart
        val highlightEnd = (end - lineStart).coerceAtMost(sourceLine.length)

        val before = sourceLine.substring(0, highlightStart)
        val highlighted = sourceLine.substring(highlightStart, highlightEnd)
        val after = sourceLine.substring(highlightEnd)

        return "$before[$highlighted]$after"
    }
}

// Value types that can be stored in variables
sealed class Value {
    data class NumberValue(val value: Double) : Value() {
        override fun toString(): String = 
            if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    }
    
    data class BooleanValue(val value: Boolean) : Value() {
        override fun toString(): String = value.toString()
    }
    data class LambdaValue(
        val params: List<String>,
        val body: Expression,
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
                    frame.position.formatWithContext(sourceCode)
                } else {
                    "position ${frame.position.start}-${frame.position.end}: ${frame.position.text}"
                }
                sb.append("\n  at $location")
            }
        }

        return sb.toString()
    }
}

private object ExpressionGrammar {
    private val whitespace = -Regex("[ \\t\\r\\n]*")

    // Identifier: alphanumeric and _, but first character cannot be a digit
    private val identifier = +Regex("[a-zA-Z_][a-zA-Z0-9_]*") map { it.value } named "identifier"

    private val number = +Regex("[0-9]+(?:\\.[0-9]+)?") map { Value.NumberValue(it.value.toDouble()) } named "number"

    // Helper function for left-associative binary operator aggregation
    private fun leftAssociativeBinaryOp(
        term: Parser<Expression>,
        operators: Parser<(Expression) -> Expression>
    ): Parser<Expression> = (term * operators.zeroOrMore) map { (first, rest) ->
        rest.fold(first) { acc, opFunc -> opFunc(acc) }
    }

    // Variable reference
    private val variableRef: Parser<Expression> = identifier map { name ->
        VariableReferenceExpression(name)
    }

    // Helper to parse comma-separated list of identifiers
    private val identifierList: Parser<List<String>> = run {
        val separator = whitespace * -',' * whitespace
        val restItem = separator * identifier
        (identifier * restItem.zeroOrMore) map { (first, rest) -> listOf(first) + rest }
    }

    // Lambda parameter list: (param1, param2) or ()
    // The alternative (whitespace map { emptyList() }) handles empty parameter lists: ()
    private val paramList: Parser<List<String>> =
        -'(' * whitespace * (identifierList + (whitespace map { emptyList<String>() })) * whitespace * -')'

    // Lambda expression: (param1, param2, ...) -> body
    private val lambda: Parser<Expression> =
        ((paramList * whitespace * -Regex("->") * whitespace * ref { expression }) mapEx { parseCtx, result ->
            val (params, bodyParser) = result.value
            val lambdaText = result.text(parseCtx)
            val position = SourcePosition(result.start, result.end, lambdaText)
            LambdaExpression(params, bodyParser, position)
        })

    // Helper to parse comma-separated list of expressions
    private val exprList: Parser<List<Expression>> = run {
        val separator = whitespace * -',' * whitespace
        val restItem = separator * ref { expression }
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
        lambda + functionCall + variableRef + (number map { v -> NumberLiteralExpression(v) }) +
            (-'(' * whitespace * ref { expression } * whitespace * -')')

    private val factor: Parser<Expression> = primary

    // Create binary operator parser
    private fun createBinaryOp(
        operator: Char,
        rightTerm: Parser<Expression>,
        expressionFactory: (Expression, Expression, SourcePosition) -> Expression
    ): Parser<(Expression) -> Expression> {
        return (whitespace * +operator * whitespace * rightTerm) mapEx { parseCtx, result ->
            val opStart = result.start + parseCtx.src.substring(result.start, result.end).indexOfFirst { it == operator }
            val (_, rightExpr: Expression) = result.value
            val opPosition = SourcePosition(opStart, result.end, result.text(parseCtx).trimStart())
            return@mapEx { left: Expression -> expressionFactory(left, rightExpr, opPosition) }
        }
    }

    // Multiplication operator parser
    private val multiplyOp = createBinaryOp('*', factor, ::MultiplyExpression)

    // Division operator parser
    private val divideOp = createBinaryOp('/', factor, ::DivideExpression)

    private val product: Parser<Expression> =
        leftAssociativeBinaryOp(factor, multiplyOp + divideOp)

    // Addition operator parser
    private val addOp = createBinaryOp('+', product, ::AddExpression)

    // Subtraction operator parser
    private val subtractOp = createBinaryOp('-', product, ::SubtractExpression)

    private val sum: Parser<Expression> =
        leftAssociativeBinaryOp(product, addOp + subtractOp)

    // Create two-character operator parser
    private fun createTwoCharOp(
        operator: String,
        rightTerm: Parser<Expression>,
        expressionFactory: (Expression, Expression, SourcePosition) -> Expression
    ): Parser<(Expression) -> Expression> {
        return (whitespace * +operator * whitespace * rightTerm) mapEx { parseCtx, result ->
            val opStart = result.start + parseCtx.src.substring(result.start, result.end).indexOf(operator)
            val (_, rightExpr: Expression) = result.value
            val opPosition = SourcePosition(opStart, result.end, result.text(parseCtx).trimStart())
            return@mapEx { left: Expression -> expressionFactory(left, rightExpr, opPosition) }
        }
    }

    // Ordering comparison operators: <, <=, >, >=
    private val orderingComparison: Parser<Expression> = run {
        val lessEqualOp = createTwoCharOp("<=", sum, ::LessThanOrEqualExpression)
        val greaterEqualOp = createTwoCharOp(">=", sum, ::GreaterThanOrEqualExpression)
        val lessOp = createBinaryOp('<', sum, ::LessThanExpression)
        val greaterOp = createBinaryOp('>', sum, ::GreaterThanExpression)

        val operators = lessEqualOp + greaterEqualOp + lessOp + greaterOp
        leftAssociativeBinaryOp(sum, operators)
    }

    // Equality comparison operators: ==, !=
    private val equalityComparison: Parser<Expression> = run {
        val equalOp = createTwoCharOp("==", orderingComparison, ::EqualsExpression)
        val notEqualOp = createTwoCharOp("!=", orderingComparison, ::NotEqualsExpression)

        val operators = equalOp + notEqualOp
        leftAssociativeBinaryOp(orderingComparison, operators)
    }

    // Ternary operator: condition ? trueExpr : falseExpr
    private val ternary: Parser<Expression> = run {
        val ternaryExpr = ref { equalityComparison } * whitespace * -'?' * whitespace *
            ref { equalityComparison } * whitespace * -':' * whitespace *
            ref { equalityComparison }
        ((ternaryExpr mapEx { parseCtx, result ->
            val (cond, trueExpr, falseExpr) = result.value
            val ternaryText = result.text(parseCtx)
            val ternaryPosition = SourcePosition(result.start, result.end, ternaryText)
            TernaryExpression(cond, trueExpr, falseExpr, ternaryPosition)
        }) + equalityComparison)
    }

    // Assignment: variable = expression
    private val assignment: Parser<Expression> = run {
        ((identifier * whitespace * -'=' * whitespace * ref { expression }) map { (name, valueExpr) ->
            AssignmentExpression(name, valueExpr)
        }) + ternary
    }

    // Root expression parser
    val expression: Parser<Expression> = assignment

    // Multi-statement parser: parses multiple expressions separated by newlines
    val program: Parser<Expression> = run {
        val newlineSep = -Regex("[ \\t]*\\r?\\n[ \\t\\r\\n]*")
        ((expression * (newlineSep * expression).zeroOrMore) map { (first, rest) ->
            ProgramExpression(listOf(first) + rest)
        })
    }

    val root = whitespace * expression * whitespace
    val programRoot = whitespace * program * whitespace
}

// Format a ParseException with detailed syntax error information
private fun formatParseException(e: ParseException, input: String): String {
    val position = e.context.errorPosition
    val (line, column) = calculateLineAndColumn(input, position)
    
    return buildString {
        append("Error: Syntax error at line $line, column $column")
        appendSuggestedParsers(e)
        appendSourceLineWithCaret(input, position, line)
    }
}

private fun calculateLineAndColumn(input: String, position: Int): Pair<Int, Int> {
    val beforePosition = input.substring(0, position.coerceAtMost(input.length))
    var line = 1
    var lastNewlinePos = -1
    beforePosition.forEachIndexed { i, char ->
        if (char == '\n') {
            line++
            lastNewlinePos = i
        }
    }
    val column = position - lastNewlinePos
    return line to column
}

private fun StringBuilder.appendSuggestedParsers(e: ParseException) {
    if (e.context.suggestedParsers.isNotEmpty()) {
        val candidates = e.context.suggestedParsers
            .mapNotNull { it.name }
            .distinct()
        if (candidates.isNotEmpty()) {
            append("\nExpected: ${candidates.joinToString(", ")}")
        }
    }
}

private fun StringBuilder.appendSourceLineWithCaret(input: String, position: Int, line: Int) {
    val beforePosition = input.substring(0, position.coerceAtMost(input.length))
    val lineStart = beforePosition.lastIndexOf('\n') + 1
    val lineEnd = input.indexOf('\n', position).let { if (it == -1) input.length else it }
    val sourceLine = input.substring(lineStart, lineEnd)

    if (sourceLine.isNotEmpty()) {
        append("\n")
        append(sourceLine)
        append("\n")
        val caretPosition = position - lineStart
        append(" ".repeat(caretPosition.coerceAtLeast(0)))
        append("^")
    }
}

@JsExport
fun parseExpression(input: String): String = try {
    FunctionCallExpression.functionCallCount = 0
    val initialContext = EvaluationContext(sourceCode = input)
    val resultExpr = ExpressionGrammar.programRoot.parseAllOrThrow(input)
    val result = resultExpr.evaluate(initialContext)
    result.toString()
} catch (e: EvaluationException) {
    formatEvaluationException(e)
} catch (e: ParseException) {
    formatParseException(e, input)
} catch (e: Exception) {
    "Error: ${e.message}"
}

private fun formatEvaluationException(e: EvaluationException): String =
    if (e.context != null && e.context.callStack.isNotEmpty()) {
        e.formatWithCallStack()
    } else {
        "Error: ${e.message}"
    }
