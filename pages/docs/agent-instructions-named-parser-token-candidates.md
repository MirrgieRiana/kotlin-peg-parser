# Agent Instructions: Named Parser Token Candidates

## 概要 (Overview)

このドキュメントは、Xarpegにおけるnamed parserのトークン候補の動作を理解し、操作するための指示を提供します。

This document provides instructions for understanding and working with the named parser token candidate behavior in Xarpeg.

## 背景 (Background)

パーサーが失敗した場合、Xarpegは`ParseContext.suggestedParsers`にエラー位置で失敗したパーサーを追跡します。この情報は、ユーザーに期待されるトークンを伝える有用なエラーメッセージの生成に使用されます。

When a parser fails, Xarpeg tracks which parsers failed at the error position in `ParseContext.suggestedParsers`. This information is used to generate helpful error messages that tell users what tokens were expected.

## Named Parserの動作 (Named Parser Behavior)

`named`中置関数を使用してパーサーに名前を付けると、構成要素のパーサーがエラーメッセージのために追跡される方法に影響します。

When a parser is given a name using the `named` infix function, it affects how constituent parsers are tracked for error messages.

### 主な動作 (Key Behavior)

1. **名前付き複合パーサーは構成要素を隠す**: 複合パーサー（例：`parserA * parserB`）に名前を付けて解析が失敗した場合、`suggestedParsers`には名前付き複合パーサーのみが表示され、その構成要素のパーサーは表示されません。

   **Named Composite Parsers Hide Constituents**: When a composite parser (e.g., `parserA * parserB`) is named, and parsing fails, only the named composite parser appears in `suggestedParsers`, not its constituent parsers.

2. **名前なし複合パーサーは構成要素を表示**: 複合パーサーに名前が付けられていない場合、その構成要素の名前付きパーサーが失敗すると`suggestedParsers`に表示されます。

   **Unnamed Composite Parsers Show Constituents**: When a composite parser is NOT named, its constituent named parsers will appear in `suggestedParsers` when they fail.

### 実装の詳細 (Implementation Details)

この動作は`ParseContext.parseOrNull()`で実装されています：

The behavior is implemented in `ParseContext.parseOrNull()`:

- `name`プロパティを持つパーサーが`context.parseOrNull(parser, start)`を通じて呼び出されると、`isInNamedParser`フラグが`true`に設定されます
- `isInNamedParser`が`true`の間、失敗したパーサーは`suggestedParsers`に追加されません
- これにより、名前付きパーサー内にいる場合、構成要素のパーサーがエラーメッセージに表示されなくなります

- When a parser with a `name` property is called through `context.parseOrNull(parser, start)`, the `isInNamedParser` flag is set to `true`
- While `isInNamedParser` is true, failed parsers are not added to `suggestedParsers`
- This prevents constituent parsers from appearing in error messages when inside a named parser

### 重要：呼び出し規約 (Important: Calling Convention)

正しい動作を得るには、`parser.parseOrNull(context, start)`ではなく`context.parseOrNull(parser, start)`を通じてパーサーを呼び出す必要があります。前者は名前付きパーサーロジックが正しく適用されることを保証します。

To get the correct behavior, parsers must be called through `context.parseOrNull(parser, start)` rather than `parser.parseOrNull(context, start)`. The former ensures that the named parser logic is applied correctly.

## 例 (Example)

```kotlin
// 構成要素のパーサーに名前を定義
// Define constituent parsers with names
val parserA = (+'a') named "letter_a"
val parserB = (+'b') named "letter_b"

// 名前付き複合パーサー
// Named composite parser
val composite = (parserA * parserB) named "ab_sequence"

// 解析が失敗した場合：
// When parsing fails:
val context = ParseContext("c", useCache = true)
val result = context.parseOrNull(composite, 0)

// 結果："ab_sequence"のみが提案され、"letter_a"や"letter_b"は提案されない
// Result: only "ab_sequence" is suggested, not "letter_a" or "letter_b"
```

## テストカバレッジ (Test Coverage)

この動作は`NamedParserTest.kt`でテストされています：

The behavior is tested in `NamedParserTest.kt`:

- `namedCompositeParserHidesConstituentTokens()`: 名前付き複合パーサーが構成要素のトークンを隠すことを検証
  
  Verifies that named composite parsers hide constituent tokens

- `unnamedCompositeParserEnumeratesConstituentTokens()`: 名前なし複合パーサーが構成要素のトークンを表示することを検証
  
  Verifies that unnamed composite parsers show constituent tokens

## ユースケース (Use Cases)

### 複合パーサーに名前を付けるべき時 (When to Name Composite Parsers)

エラーメッセージで高レベルのセマンティック名を提供したい場合、複合パーサーに名前を付けます：

Name a composite parser when you want to provide a high-level semantic name in error messages:

```kotlin
val identifier = (letter * (letter + digit).zeroOrMore) named "identifier"
// エラー: "Expected: identifier"（ユーザーフレンドリー）
// Error: "Expected: identifier" (user-friendly)
```

### 複合パーサーを名前なしのままにするべき時 (When to Leave Composite Parsers Unnamed)

特定のトークンレベルのエラーメッセージが必要な場合、複合パーサーを名前なしのままにします：

Leave a composite parser unnamed when you want specific token-level error messages:

```kotlin
val identifier = letter * (letter + digit).zeroOrMore
// エラー: "Expected: letter"（より具体的）
// Error: "Expected: letter" (more specific)
```

## 将来の開発のためのガイドライン (Guidelines for Future Development)

1. **呼び出し規約を維持する**: 適切な名前付きパーサー処理が必要な場合は、常に`context.parseOrNull()`を通じてパーサーを呼び出します
   
   **Maintain the Calling Convention**: Always call parsers through `context.parseOrNull()` when you need proper named parser handling

2. **名前付き動作をテストする**: 新しいパーサーコンビネータを追加する場合、名前付きパーサーメカニズムで正しく動作することを確認します
   
   **Test Named Behavior**: When adding new parser combinators, ensure they work correctly with the named parser mechanism

3. **名前付けの決定を文書化する**: パーサー文法を作成する場合、望ましいエラーメッセージの粒度に基づいて、複合パーサーに名前を付けるべきかどうかを文書化します
   
   **Document Naming Decisions**: When creating parser grammars, document whether composite parsers should be named based on the desired error message granularity

## 関連ファイル (Related Files)

- `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/Parser.kt`: コア実装 (Core implementation)
- `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/NamedParser.kt`: NamedParserクラス (NamedParser class)
- `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/NamedParserTest.kt`: テストカバレッジ (Test coverage)
