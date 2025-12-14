package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FunctionTest {

    @Test
    fun parsesLambdaExpression() {
        val result = parseExpression("add = (a, b) -> a + b")
        assertTrue(result.contains("lambda"))
    }

    @Test
    fun parsesFunctionCall() {
        assertEquals("15", parseExpression("add = (a, b) -> a + b\nadd(10, 5)"))
    }

    @Test
    fun parsesLambdaWithNoParameters() {
        assertEquals("42", parseExpression("f = () -> 42\nf()"))
    }

    @Test
    fun parsesLambdaWithOneParameter() {
        assertEquals("10", parseExpression("double = (x) -> x * 2\ndouble(5)"))
    }

    @Test
    fun parsesLambdaWithMultipleParameters() {
        assertEquals("6", parseExpression("multiply = (x, y) -> x * y\nmultiply(2, 3)"))
    }

    @Test
    fun parsesLambdaWithWhitespaceInParameters() {
        assertEquals("15", parseExpression("add = ( a , b ) -> a + b\nadd(10, 5)"))
    }

    @Test
    fun parsesLambdaWithComplexBody() {
        assertEquals("17", parseExpression("calc = (x, y) -> x * 2 + y * 3\ncalc(4, 3)"))
    }

    @Test
    fun parsesRecursiveFunctionCall() {
        assertEquals("120", parseExpression("fact = (n) -> n <= 1 ? 1 : n * fact(n - 1)\nfact(5)"))
    }

    @Test
    fun parsesNestedFunctionCalls() {
        assertEquals("30", parseExpression("double = (x) -> x * 2\ntriple = (x) -> x * 3\ndouble(triple(5))"))
    }

    @Test
    fun parsesFunctionCallWithExpressions() {
        assertEquals("15", parseExpression("add = (a, b) -> a + b\nadd(2 + 3, 4 * 2 + 2)"))
    }

    @Test
    fun parsesFunctionWithVariableCapture() {
        assertEquals("15", parseExpression("x = 5\nf = (y) -> x + y\nf(10)"))
    }

    @Test
    fun parsesHigherOrderFunction() {
        assertEquals("20", parseExpression("apply = (f, x) -> f(x)\ndouble = (n) -> n * 2\napply(double, 10)"))
    }

    @Test
    fun parsesMultipleLambdaDefinitions() {
        assertEquals("7", parseExpression("add = (a, b) -> a + b\nmul = (a, b) -> a * b\nadd(3, mul(2, 2))"))
    }

    @Test
    fun parsesFunctionCallInTernary() {
        assertEquals("10", parseExpression("abs = (x) -> x < 0 ? -x : x\nabs(-10)"))
    }

    @Test
    fun parsesRecursiveFibonacci() {
        assertEquals("55", parseExpression("fib = (n) -> n <= 1 ? n : fib(n - 1) + fib(n - 2)\nfib(10)"))
    }

    @Test
    fun parsesMutuallyRecursiveFunctions() {
        assertEquals("true", parseExpression("isEven = (n) -> n == 0 ? true : isOdd(n - 1)\nisOdd = (n) -> n == 0 ? false : isEven(n - 1)\nisEven(4)"))
    }
}
