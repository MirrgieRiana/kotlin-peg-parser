import mirrg.xarpite.parser.ParseContext
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.and
import mirrg.xarpite.parser.parsers.map
import mirrg.xarpite.parser.parsers.plus
import mirrg.xarpite.parser.parsers.times
import mirrg.xarpite.parser.parsers.unaryPlus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AndParserTest {

    @Test
    fun andParserMatchesWithoutConsuming() {
        val parser = (+'a').and()
        val context = ParseContext("abc", useCache = true)
        val result = parser.parseOrNull(context, 0)
        assertNotNull(result)
        assertEquals('a', result.value)
        assertEquals(0, result.start)
        assertEquals(0, result.end)
    }

    @Test
    fun andParserFailsWhenInnerParserFails() {
        val parser = (+'a').and()
        val context = ParseContext("bcd", useCache = true)
        val result = parser.parseOrNull(context, 0)
        assertNull(result)
    }

    @Test
    fun andParserInSequence() {
        val parser = (+'a').and() * +'a' * +'b'
        val result = parser.parseAllOrThrow("ab")
        assertEquals('a', result.a)
        assertEquals('a', result.b)
        assertEquals('b', result.c)
    }

    @Test
    fun andParserWithString() {
        val parser = (+"hello").and() * +"hello" * +"world"
        val result = parser.parseAllOrThrow("helloworld")
        assertEquals("hello", result.a)
        assertEquals("hello", result.b)
        assertEquals("world", result.c)
    }

    @Test
    fun andParserDoesNotConsumeInput() {
        val parser = (+'a').and() * +'a' map { tuple -> tuple.b }
        val context = ParseContext("a", useCache = true)
        val result = parser.parseOrNull(context, 0)
        assertNotNull(result)
        assertEquals('a', result.value)
        assertEquals(0, result.start)
        assertEquals(1, result.end)
    }

    @Test
    fun multipleAndParsersInSequence() {
        val parser = (+'a').and() * (+'a').and() * +'a'
        val result = parser.parseAllOrThrow("a")
        assertEquals('a', result.a)
        assertEquals('a', result.b)
        assertEquals('a', result.c)
    }

    @Test
    fun andParserWithRegex() {
        val parser = (+Regex("[0-9]+")).and() * +Regex("[0-9]+") map { tuple -> tuple.b.value }
        val result = parser.parseAllOrThrow("123")
        assertEquals("123", result)
    }

    @Test
    fun andParserForKeywordLookahead() {
        val keyword = +"if"
        val notWordChar = +Regex("[^a-zA-Z0-9_]")
        val ifKeyword = keyword * notWordChar.and() map { tuple -> tuple.a }
        
        val context1 = ParseContext("if ", useCache = true)
        val result1 = ifKeyword.parseOrNull(context1, 0)
        assertNotNull(result1)
        assertEquals("if", result1.value)
        assertEquals(2, result1.end)
        
        val context2 = ParseContext("ifx", useCache = true)
        val result2 = ifKeyword.parseOrNull(context2, 0)
        assertNull(result2)
    }

    @Test
    fun andParserVsCharSequence() {
        val parser = (+'a').and() * +'a' * (+'b').and() * +'b' * +'c'
        val result = parser.parseAllOrThrow("abc")
        assertEquals('a', result.a)
        assertEquals('a', result.b)
        assertEquals('b', result.c)
        assertEquals('b', result.d)
        assertEquals('c', result.e)
    }
}
