package io.github.mirrgieriana.xarpite.xarpeg.samples.online.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class GeneralTest {

    @Test
    fun parsesExpressionWithWhitespaceAroundPlus() {
        assertEquals("3", parseExpression("1 + 2"))
    }

    @Test
    fun parsesExpressionWithLeadingWhitespace() {
        assertEquals("3", parseExpression(" 1+2"))
    }

    @Test
    fun parsesExpressionWithTrailingWhitespace() {
        assertEquals("3", parseExpression("1+2 "))
    }

    @Test
    fun parsesExpressionWithBothLeadingAndTrailingWhitespace() {
        assertEquals("3", parseExpression(" 1+2 "))
    }

    @Test
    fun parsesVariableAssignment() {
        assertEquals("5", parseExpression("x = 5"))
    }

    @Test
    fun parsesVariableReference() {
        assertEquals("10", parseExpression("y = 10"))
    }

    @Test
    fun parsesIdentifierWithUnderscore() {
        assertEquals("42", parseExpression("my_var = 42"))
    }

    @Test
    fun parsesIdentifierWithNumbers() {
        assertEquals("100", parseExpression("var123 = 100"))
    }

    @Test
    fun parsesDecimalNumber() {
        assertEquals("3.14", parseExpression("3.14"))
    }

    @Test
    fun parsesDecimalNumberInExpression() {
        assertEquals("5.5", parseExpression("2.5 + 3"))
    }

    @Test
    fun parsesDecimalNumberInVariableAssignment() {
        assertEquals("3.14", parseExpression("pi = 3.14"))
    }

    @Test
    fun parsesIdentifierStartingWithUnderscore() {
        assertEquals("42", parseExpression("_private = 42"))
    }

    @Test
    fun parsesIdentifierWithMultipleUnderscores() {
        assertEquals("42", parseExpression("__value__ = 42"))
    }

    @Test
    fun parsesIdentifierWithMixedCase() {
        assertEquals("42", parseExpression("myVariable = 42"))
    }

    @Test
    fun parsesIdentifierEndingWithNumber() {
        assertEquals("42", parseExpression("value123 = 42"))
    }

    @Test
    fun parsesExpressionWithVariousWhitespace() {
        assertEquals("15", parseExpression("  10   +   5  "))
    }

    @Test
    fun parsesExpressionWithNewlines() {
        assertEquals("15", parseExpression("x = 10\nx + 5"))
    }

    @Test
    fun parsesExpressionWithTabs() {
        assertEquals("15", parseExpression("10\t+\t5"))
    }

    @Test
    fun parsesMultipleVariableAssignments() {
        assertEquals("15", parseExpression("x = 10\ny = 5\nx + y"))
    }

    @Test
    fun parsesZero() {
        assertEquals("0", parseExpression("0"))
    }

    @Test
    fun parsesNegativeNumber() {
        assertEquals("-5", parseExpression("0 - 5"))
    }

    @Test
    fun parsesLargeNumber() {
        assertEquals("1000000", parseExpression("1000000"))
    }

    @Test
    fun parsesComplexExpression() {
        assertEquals("42", parseExpression("x = 10\ny = 3\nz = 2\nx * y + z * 6"))
    }

    @Test
    fun parsesEmptyProgram() {
        assertEquals("", parseExpression(""))
    }

    @Test
    fun parsesWhitespaceOnly() {
        assertEquals("", parseExpression("   \n\t  "))
    }

    @Test
    fun parsesVariableReassignment() {
        assertEquals("20", parseExpression("x = 10\nx = 20\nx"))
    }
}
