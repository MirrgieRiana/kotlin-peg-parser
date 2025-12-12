import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.utilities.LazyArithmetic
import mirrg.xarpite.parser.utilities.PositionMarkerException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the LazyArithmetic utility, which demonstrates position tracking
 * in a parser that returns lazy-evaluated arithmetic expressions.
 */
class LazyArithmeticTest {

    @Test
    fun parseSimpleNumber() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("42")
        assertEquals(42, lazyResult())
    }

    @Test
    fun parseAddition() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("1+2")
        assertEquals(3, lazyResult())
    }

    @Test
    fun parseSubtraction() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("10-3")
        assertEquals(7, lazyResult())
    }

    @Test
    fun parseMultiplication() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("3*4")
        assertEquals(12, lazyResult())
    }

    @Test
    fun parseDivision() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("15/3")
        assertEquals(5, lazyResult())
    }

    @Test
    fun parseComplexExpression() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("2+3*4")
        // Should respect operator precedence: 2 + (3 * 4) = 14
        assertEquals(14, lazyResult())
    }

    @Test
    fun parseParentheses() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("(2+3)*4")
        // Parentheses change precedence: (2 + 3) * 4 = 20
        assertEquals(20, lazyResult())
    }

    @Test
    fun parseNestedParentheses() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("((2+3)*4)+1")
        assertEquals(21, lazyResult())
    }

    @Test
    fun parseChainedAdditions() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("1+2+3+4")
        assertEquals(10, lazyResult())
    }

    @Test
    fun parseChainedMultiplications() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("2*3*4")
        assertEquals(24, lazyResult())
    }

    @Test
    fun parseMixedOperators() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("10-2*3+4")
        // Should be: 10 - (2 * 3) + 4 = 10 - 6 + 4 = 8
        assertEquals(8, lazyResult())
    }

    @Test
    fun positionMarkerAtStart() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(0, exception.position)
    }

    @Test
    fun positionMarkerAfterNumber() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("42+!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(3, exception.position)
    }

    @Test
    fun positionMarkerInMiddle() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("1+!+3")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(2, exception.position)
    }

    @Test
    fun positionMarkerInMultiplication() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("2*!*4")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(2, exception.position)
    }

    @Test
    fun positionMarkerInsideParentheses() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("(2+!)")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(3, exception.position)
    }

    @Test
    fun positionMarkerWithComplexExpression() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("(10+20)*!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(8, exception.position)
    }

    @Test
    fun positionMarkerAfterMultipleDigits() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("123+!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(4, exception.position)
    }

    @Test
    fun positionMarkerDeepNesting() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("((1+2)*(3+!))")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(10, exception.position)
    }

    @Test
    fun positionMarkerInSubtraction() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("100-!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(4, exception.position)
    }

    @Test
    fun positionMarkerInDivision() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("50/!")
        val exception = assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
        assertEquals(3, exception.position)
    }

    @Test
    fun lazyEvaluationDoesNotComputeUntilCalled() {
        // Parse without evaluating
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("!")
        // No exception thrown yet during parsing
        
        // Exception only thrown when we evaluate
        assertFailsWith<PositionMarkerException> {
            lazyResult()
        }
    }

    @Test
    fun multipleEvaluationsProduceSameResult() {
        val lazyResult = LazyArithmetic.expr.parseAllOrThrow("2+3*4")
        assertEquals(14, lazyResult())
        assertEquals(14, lazyResult())
        assertEquals(14, lazyResult())
    }
}
