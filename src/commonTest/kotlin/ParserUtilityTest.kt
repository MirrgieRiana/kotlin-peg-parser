import io.github.mirrgieriana.xarpite.xarpeg.ParseContext
import io.github.mirrgieriana.xarpite.xarpeg.text
import io.github.mirrgieriana.xarpite.xarpeg.parseAllOrThrow
import io.github.mirrgieriana.xarpite.xarpeg.parsers.*
import mirrg.xarpite.parser.NumberParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ParserUtilityTest {

    @Test
    fun parseResultTextNormalizesNewlines() {
        val context = ParseContext("line1\r\nline2", useCache = true)
        val parser = +"line1\r\nline2"

        val result = parser.parseOrNull(context, 0)

        assertEquals("line1\nline2", result?.text(context))
    }

    @Test
    fun mapExProvidesContextAndResultRange() {
        val parser = (+"foo") mapEx { ctx, result ->
            "${result.text(ctx)}@${result.start}-${result.end}"
        }

        assertEquals("foo@0-3", parser.parseAllOrThrow("foo"))
    }

    @Test
    fun numberParserStopsAtFirstNonDigit() {
        val context = ParseContext("123abc", useCache = true)
        val result = NumberParser.parseOrNull(context, 0)

        assertNotNull(result)
        assertEquals(123, result.value)
        assertEquals(3, result.end)

        assertNull(NumberParser.parseOrNull(ParseContext("abc", useCache = true), 0))
    }
}
