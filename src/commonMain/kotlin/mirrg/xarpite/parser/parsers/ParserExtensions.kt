package mirrg.xarpite.parser.parsers

import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.Tuple0
import mirrg.xarpite.parser.Tuple1

/**
 * Extension property equivalent to unaryPlus operator.
 * Captures the parser result into a Tuple1.
 * 
 * Example:
 * ```
 * val parser = +"hello"
 * val captured = parser.capture  // equivalent to +parser
 * ```
 */
val <T : Any> Parser<T>.capture: Parser<Tuple1<T>>
    get() = +this

/**
 * Extension property equivalent to unaryMinus operator.
 * Ignores the parser result and returns Tuple0.
 * 
 * Example:
 * ```
 * val parser = +"hello"
 * val ignored = parser.ignore  // equivalent to -parser
 * ```
 */
val Parser<*>.ignore: Parser<Tuple0>
    get() = -this

/**
 * Extension property equivalent to not operator.
 * Performs negative lookahead - succeeds if the parser fails.
 * 
 * Example:
 * ```
 * val parser = +"hello"
 * val notParser = parser.not  // equivalent to !parser
 * ```
 */
val Parser<*>.not: Parser<Tuple0>
    get() = !this
