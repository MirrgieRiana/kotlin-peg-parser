package io.github.mirrgieriana.xarpite.xarpeg

import io.github.mirrgieriana.xarpite.xarpeg.parsers.toParser
import kotlin.test.Test
import kotlin.test.assertSame

class PlatformTest {

    @Test
    fun stringParserCachesOnNonNativePlatforms() {
        if (isNative) return

        val first = "abc".toParser()
        val second = "abc".toParser()

        assertSame(first, second)
    }
}
