package mirrg.xarpite.peg

data class ParseResult<T>(val value: T, val rest: String)

typealias Parser<T> = (String) -> ParseResult<T>?

val parseA: Parser<String> = { input ->
    if (input.startsWith("a")) {
        ParseResult("a", input.drop(1))
    } else {
        null
    }
}

fun <T> repeatParser(parser: Parser<T>): Parser<List<T>> = fun(input: String): ParseResult<List<T>>? {
    var rest = input
    val results = mutableListOf<T>()

    while (rest.isNotEmpty()) {
        val result = parser(rest) ?: return null
        if (result.rest.length == rest.length) return null
        results.add(result.value)
        rest = result.rest
    }

    return ParseResult(results, rest)
}
