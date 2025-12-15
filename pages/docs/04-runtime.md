---
layout: default
title: Step 4 – Runtime
---

# Step 4: Errors and runtime behavior

Review how parsers handle full consumption, exceptions, and memoization cache settings.

## Consume the entire input

`parseAllOrThrow` verifies that the input is matched from start to end and throws informative exceptions when it is not:

- No parser matched at the start: `UnmatchedInputParseException`
- A prefix matches but trailing input remains: `ExtraCharactersParseException`

If a `map` throws, the exception bubbles up and aborts parsing; validate before mapping or catch and wrap errors when you need to recover.

## Cache on or off

`ParseContext` memoizes by default so heavy backtracking stays predictable.  
Disable with `parseAllOrThrow(input, useCache = false)` if you want lower memory usage or need side effects to re-run.

## Error reporting with ParseContext

When parsing fails, `ParseContext` provides detailed information to help you build user-friendly error messages:

- `errorPosition`: The furthest position in the input that was attempted during parsing
- `suggestedParsers`: A set of parsers that failed at `errorPosition`

This information is especially useful when combined with named parsers to tell users what was expected at the failure point.

### Basic error context usage

```kotlin
import io.github.mirrgieriana.xarpite.xarpeg.*
import io.github.mirrgieriana.xarpite.xarpeg.parsers.*

val letter = (+Regex("[a-z]")) named "letter" map { it.value }
val digit = (+Regex("[0-9]")) named "digit" map { it.value }
val identifier = letter * (letter + digit).zeroOrMore

fun main() {
    val context = ParseContext("1abc", useCache = true)
    val result = identifier.parseOrNull(context, 0)
    
    if (result == null) {
        println("Failed at position ${context.errorPosition}")
        
        val expected = context.suggestedParsers
            .mapNotNull { it.name }
            .distinct()
            .sorted()
            .joinToString(", ")
        
        if (expected.isNotEmpty()) {
            println("Expected: $expected")
        }
    }
}
```

This will output:
```
Failed at position 0
Expected: letter
```

### Error context with exceptions

When using `parseAllOrThrow`, you can catch the exception and access its `context` property:

```kotlin
import io.github.mirrgieriana.xarpite.xarpeg.*
import io.github.mirrgieriana.xarpite.xarpeg.parsers.*

val number = (+Regex("[0-9]+")) named "number" map { it.value.toInt() }
val operator = (+'*' + +'+') named "operator"
val expr = number * operator * number

fun main() {
    try {
        expr.parseAllOrThrow("42 + 10")
    } catch (e: UnmatchedInputParseException) {
        val suggestions = e.context.suggestedParsers
            .mapNotNull { it.name }
            .joinToString(", ")
        
        println("Parse error at position ${e.context.errorPosition}")
        if (suggestions.isNotEmpty()) {
            println("Expected: $suggestions")
        }
    }
}
```

### How error tracking works

The error position advances as parsing proceeds through your grammar:

- When a parser fails at a position further than the current `errorPosition`, the `errorPosition` is updated and `suggestedParsers` is cleared
- All parsers that fail at the current `errorPosition` are added to `suggestedParsers`
- Named parsers appear in `suggestedParsers` using their assigned names

**Example**: In a sequence `a * b * c`, if `a` and `b` succeed but `c` fails at position 10, then `errorPosition` will be 10 and `suggestedParsers` will contain `c`.

### Named parsers for better error messages

Use `parser named "name"` to give parsers meaningful names that appear in error messages:

```kotlin
import io.github.mirrgieriana.xarpite.xarpeg.*
import io.github.mirrgieriana.xarpite.xarpeg.parsers.*

val lparen = (+'(') named "left_parenthesis"
val rparen = (+')') named "right_parenthesis"
val number = (+Regex("[0-9]+")) named "number" map { it.value.toInt() }
val expr = lparen * number * rparen

fun main() {
    val context = ParseContext("(42", useCache = true)
    expr.parseOrNull(context, 0)
    
    // After "(" and "42", parser expected ")" at position 3
    // errorPosition: 3
    // suggestedParsers contains parser named "right_parenthesis"
}
```

Without names, parsers are tracked but appear less user-friendly in error messages. Always name parsers that users should recognize.

## Debugging tips

- Reproduce failures with small inputs and confirm how `optional` or `zeroOrMore` rewind.
- When unsure about shapes and types, lean on IDE KDoc and completion.
- Use `ParseContext` directly with `parseOrNull` to inspect `errorPosition` and `suggestedParsers` for debugging.
- For more examples, see the tests in [src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ErrorContextTest.kt](https://github.com/MirrgieRiana/xarpeg-kotlin-peg-parser/blob/main/src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ErrorContextTest.kt).

---

Next, learn how to work with parsing positions using `mapEx` to extract location information when you need it.  
→ [Step 5: Working with parsing positions](05-positions.md)
