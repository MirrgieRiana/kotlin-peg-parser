# Documentation (Tutorial)

This page focuses on “how to compose the DSL and where to use it” at a high level. For API signatures, rely on your IDE’s completion and KDoc.

## 1. Build your first parser

The library offers an **operator-based DSL** to assemble parsers. Frequently used operators:

- `+literal` / `+Regex("...")`: create a parser that must match the literal/regex at the current position  
- `*`: sequence; results are packed into `TupleX`  
- `-parser`: match but drop the value (`Tuple0`)  
- `parserA + parserB`: alternatives (try in order)  
- `parser.optional` / `zeroOrMore` / `oneOrMore`: optionals and repetitions

A minimal key/value parser looks like this:

```kotlin
import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

val identifier = +Regex("[a-zA-Z][a-zA-Z0-9_]*")
val number = +Regex("[0-9]+") map { it.value.toInt() }
val kv = identifier * -'=' * number map { (key, value) -> key to value }

fun main() {
    println(kv.parseAllOrThrow("count=42")) // => (count, 42)
}
```

`*` builds a sequence and returns `Tuple2`; use `map` to shape the result. Wrap delimiters with `-` to drop them from the tuple.

## 2. Combine repetition and option

Repetition and option builders stack naturally. Here’s a signed integer and a repeated string example:

```kotlin
val sign = (+'+' + +'-').optional map { it.a ?: '+' }
val unsigned = +Regex("[0-9]+") map { it.value.toInt() }
val signedInt = sign * unsigned map { (s, value) ->
    if (s == '-') -value else value
}

val repeatedA = (+'a').oneOrMore map { it.joinToString("") }

signedInt.parseAllOrThrow("-42") // => -42
signedInt.parseAllOrThrow("99")  // => 99
repeatedA.parseAllOrThrow("aaaa") // => "aaaa"
```

`optional` rewinds when it does not match, so it never blocks later parsers. Repetition helpers (`zeroOrMore` / `oneOrMore` / `list`) return `List<T>`, ready to transform with `map`.

## 3. Build expressions with recursion and associativity

Use `parser { ... }` or `by lazy` for recursion. The associativity helpers save you from writing recursive descent by hand.

```kotlin
val expr: Parser<Int> = object {
    val number = +Regex("[0-9]+") map { it.value.toInt() }
    val paren: Parser<Int> by lazy { -'(' * root * -')' }
    val factor = number + paren
    val mul = leftAssociative(factor, -'*') { a, _, b -> a * b }
    val add = leftAssociative(mul, -'+') { a, _, b -> a + b }
    val root = add
}.root

expr.parseAllOrThrow("2*(3+4)") // => 14
```

`leftAssociative` / `rightAssociative` take just a term parser, an operator parser, and a combinator. Operators are ordinary parsers, so adding whitespace handling or multi-character operators is straightforward.

## 4. Errors and full consumption

`parseAllOrThrow` throws if the input is not fully consumed.

- Nothing matches at the start: `UnmatchedInputParseException`
- Only part matches and trailing input remains: `ExtraCharactersParseException`

If a `map` throws, that branch simply fails, which makes it easy to embed validation in transformations.

## 5. Cache on/off

`ParseContext` memoizes by default so heavy backtracking stays predictable. Disable with `parseAllOrThrow(..., useCache = false)` when you need less memory or want side effects to re-run.

## 6. Next steps

- For precise signatures and return shapes, follow KDoc in your IDE.  
- To see behavior, check the test cases in `imported/src/commonTest/kotlin/ParserTest.kt`.  
- To understand the implementation, browse `imported/src/commonMain/kotlin/mirrg/xarpite/parser`.
