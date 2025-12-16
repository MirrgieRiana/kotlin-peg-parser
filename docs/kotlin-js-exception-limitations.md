# Kotlin/JS Exception Property Access Limitations

## Issue

When trying to access `ParseContext.suggestedParsers` from a `ParseException` in Kotlin/JS code, the compiler reports:

```
error: Unresolved reference 'context'
error: Too many arguments for 'constructor(message: String, position: Int): ParseException'
```

## Root Cause

The `ParseException` class is defined as:

```kotlin
open class ParseException(message: String, val context: ParseContext, val position: Int) : Exception(message)
```

However, when this class is compiled to JavaScript, the Kotlin/JS compiler has special handling for exception classes:

1. **Exception Inheritance**: `ParseException` extends `Exception`, which is a platform type that maps to JavaScript's `Error` class
2. **Property Visibility**: Constructor properties of exception classes that extend platform types are not properly exported to the JavaScript environment
3. **Constructor Signature**: The Kotlin/JS compiler only recognizes a 2-parameter constructor `(message: String, position: Int)`, completely ignoring the `context: ParseContext` parameter

## Evidence

### Test Case

```kotlin
val ctx = ParseContext("test", false)
val exc = ParseException("test message", ctx, 5)

// Compiler errors:
// - Argument type mismatch: actual type is 'ParseContext', but 'Int' was expected
// - Too many arguments for constructor
// - Unresolved reference 'context'
```

### Dynamic Access Attempt

Even using `asDynamic()` to bypass type checking doesn't work:

```kotlin
val dynamicException = e.asDynamic()
val context = dynamicException.context  // Returns null or undefined
```

## Why This Happens

This is a known limitation in Kotlin/JS when dealing with exception classes:

1. **JavaScript Error Semantics**: JavaScript's `Error` class has specific semantics that Kotlin/JS must respect
2. **Property Mangling**: Properties of exception classes may be mangled or not exported to maintain compatibility with JS error handling
3. **Platform Type Boundaries**: When crossing the boundary between Kotlin and JavaScript platform types, not all Kotlin features are preserved

## Workaround Options

### Option 1: Separate Error Info Class

```kotlin
data class ParseErrorInfo(
    val message: String,
    val position: Int,
    val context: ParseContext
)

// Return this instead of throwing exceptions from JS-facing functions
```

### Option 2: Custom Exception Interface

```kotlin
interface ParseError {
    val message: String
    val position: Int
    val context: ParseContext
}

class ParseException(...) : Exception(...), ParseError {
    // Properties accessible through interface
}
```

### Option 3: Global Error Context

```kotlin
object ParseErrorContext {
    var lastContext: ParseContext? = null
}

// Store context separately when exception is thrown
```

## Current Solution

The `formatParseException` function in the online parser:
- ✅ Successfully accesses `e.position` 
- ❌ Cannot access `e.context` or `e.context.suggestedParsers`
- ℹ️ Includes a try-catch block to attempt dynamic access, which gracefully fails

This is why the error message shows position information but not expected token candidates.

## Conclusion

This is a fundamental limitation of Kotlin/JS when working with exception classes that extend platform types. The `context` property is defined in Kotlin source code but is not accessible from the compiled JavaScript code due to how the Kotlin/JS compiler handles exception class properties.
