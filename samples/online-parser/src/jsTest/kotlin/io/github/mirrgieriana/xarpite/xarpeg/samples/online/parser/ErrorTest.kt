package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.test.Test
import kotlin.test.assertTrue

class ErrorTest {

    @Test
    fun showsErrorForUndefinedVariable() {
        val result = parseExpression("x + 1")
        assertTrue(result.contains("Error"))
        assertTrue(result.contains("Undefined variable"))
    }

    @Test
    fun showsErrorForDivisionByZero() {
        val result = parseExpression("10 / 0")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorMessageForDivisionByZero() {
        val result = parseExpression("10 / 0")
        assertTrue(result.contains("Division by zero"))
    }

    @Test
    fun showsErrorForInvalidSyntax() {
        val result = parseExpression("1 + + 2")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForMismatchedParentheses() {
        val result = parseExpression("(1 + 2")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForInvalidOperator() {
        val result = parseExpression("1 & 2")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForArgumentCountMismatch() {
        val result = parseExpression("f = (x) -> x * 2\nf(1, 2)")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsCallStackForNestedFunctionError() {
        val result = parseExpression("crash = (n) -> n == 0 ? 1 / 0 : crash(n - 1)\ncrash(3)")
        assertTrue(result.contains("at line"))
    }

    @Test
    fun showsErrorForTypeErrorInArithmetic() {
        val result = parseExpression("true + 5")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForTypeErrorInComparison() {
        val result = parseExpression("true < 5")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForInvalidTernaryCondition() {
        val result = parseExpression("5 ? 10 : 20")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForUndefinedFunction() {
        val result = parseExpression("foo()")
        assertTrue(result.contains("Error"))
        assertTrue(result.contains("Undefined"))
    }

    @Test
    fun showsErrorForMaximumRecursionDepth() {
        val result = parseExpression("inf = (n) -> inf(n + 1)\ninf(0)")
        assertTrue(result.contains("Error"))
    }

    @Test
    fun showsErrorForMixedTypeComparison() {
        val result = parseExpression("5 == true")
        assertTrue(result.contains("Error"))
    }
}
