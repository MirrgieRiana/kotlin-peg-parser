@file:OptIn(ExperimentalJsExport::class)

package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.js.ExperimentalJsExport

/**
 * Base class for binary operator expressions
 */
abstract class BinaryOperatorExpression(
    protected val left: Expression,
    protected val right: Expression,
    protected val position: SourcePosition
) : Expression

// Arithmetic operators

class MultiplyExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) throw EvaluationException("Left operand of * must be a number", context, context.sourceCode)
        if (rightVal !is Value.NumberValue) throw EvaluationException("Right operand of * must be a number", context, context.sourceCode)
        return Value.NumberValue(leftVal.value * rightVal.value)
    }
}

class DivideExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) throw EvaluationException("Left operand of / must be a number", context, context.sourceCode)
        if (rightVal !is Value.NumberValue) throw EvaluationException("Right operand of / must be a number", context, context.sourceCode)
        if (rightVal.value == 0.0) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("division", position))
            throw EvaluationException("Division by zero", newCtx, context.sourceCode)
        }
        return Value.NumberValue(leftVal.value / rightVal.value)
    }
}

class AddExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("+ operator", position))
            throw EvaluationException("Left operand of + must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("+ operator", position))
            throw EvaluationException("Right operand of + must be a number", newCtx, context.sourceCode)
        }
        return Value.NumberValue(leftVal.value + rightVal.value)
    }
}

class SubtractExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("- operator", position))
            throw EvaluationException("Left operand of - must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("- operator", position))
            throw EvaluationException("Right operand of - must be a number", newCtx, context.sourceCode)
        }
        return Value.NumberValue(leftVal.value - rightVal.value)
    }
}

// Comparison operators

class LessThanExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("< operator", position))
            throw EvaluationException("Left operand of < must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("< operator", position))
            throw EvaluationException("Right operand of < must be a number", newCtx, context.sourceCode)
        }
        return Value.BooleanValue(leftVal.value < rightVal.value)
    }
}

class LessOrEqualExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("<= operator", position))
            throw EvaluationException("Left operand of <= must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("<= operator", position))
            throw EvaluationException("Right operand of <= must be a number", newCtx, context.sourceCode)
        }
        return Value.BooleanValue(leftVal.value <= rightVal.value)
    }
}

class GreaterThanExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("> operator", position))
            throw EvaluationException("Left operand of > must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("> operator", position))
            throw EvaluationException("Right operand of > must be a number", newCtx, context.sourceCode)
        }
        return Value.BooleanValue(leftVal.value > rightVal.value)
    }
}

class GreaterOrEqualExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        if (leftVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame(">= operator", position))
            throw EvaluationException("Left operand of >= must be a number", newCtx, context.sourceCode)
        }
        if (rightVal !is Value.NumberValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame(">= operator", position))
            throw EvaluationException("Right operand of >= must be a number", newCtx, context.sourceCode)
        }
        return Value.BooleanValue(leftVal.value >= rightVal.value)
    }
}

class EqualExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        val compareResult = when {
            leftVal is Value.NumberValue && rightVal is Value.NumberValue -> leftVal.value == rightVal.value
            leftVal is Value.BooleanValue && rightVal is Value.BooleanValue -> leftVal.value == rightVal.value
            else -> {
                val newCtx = context.copy(callStack = context.callStack + CallFrame("== operator", position))
                throw EvaluationException("Operands of == must be both numbers or both booleans", newCtx, context.sourceCode)
            }
        }
        return Value.BooleanValue(compareResult)
    }
}

class NotEqualExpression(left: Expression, right: Expression, position: SourcePosition) :
    BinaryOperatorExpression(left, right, position) {
    override fun evaluate(context: EvaluationContext): Value {
        val leftVal = left.evaluate(context)
        val rightVal = right.evaluate(context)
        val compareResult = when {
            leftVal is Value.NumberValue && rightVal is Value.NumberValue -> leftVal.value != rightVal.value
            leftVal is Value.BooleanValue && rightVal is Value.BooleanValue -> leftVal.value != rightVal.value
            else -> {
                val newCtx = context.copy(callStack = context.callStack + CallFrame("!= operator", position))
                throw EvaluationException("Operands of != must be both numbers or both booleans", newCtx, context.sourceCode)
            }
        }
        return Value.BooleanValue(compareResult)
    }
}

// Ternary operator

class TernaryExpression(
    private val condition: Expression,
    private val trueBranch: Expression,
    private val falseBranch: Expression,
    private val position: SourcePosition
) : Expression {
    override fun evaluate(context: EvaluationContext): Value {
        val condVal = condition.evaluate(context)
        if (condVal !is Value.BooleanValue) {
            val newCtx = context.copy(callStack = context.callStack + CallFrame("ternary condition", position))
            throw EvaluationException("Ternary condition must be a boolean", newCtx, context.sourceCode)
        }
        return if (condVal.value) {
            trueBranch.evaluate(context)
        } else {
            falseBranch.evaluate(context)
        }
    }
}
