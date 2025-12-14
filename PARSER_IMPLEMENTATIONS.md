# Parserインターフェースの全実装箇所

このドキュメントは、`Parser<out T : Any>`インターフェースを実装しているすべての箇所を網羅的にリストアップしたものです。

## Parserインターフェースの定義

**ファイル**: `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/Parser.kt:7`

```kotlin
fun interface Parser<out T : Any> {
    fun parseOrNull(context: ParseContext, start: Int): ParseResult<T>?
}
```

`Parser`は関数型インターフェース（fun interface）で、SAM変換によりラムダ式で実装可能です。

---

## 実装箇所の分類

### 1. 通常のクラス宣言による実装（12件）

| No. | クラス名 | ファイルパス | 行番号 | 型パラメータ | 説明 |
|-----|---------|-------------|--------|------------|------|
| 1 | `CharParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/CharParser.kt` | 8 | `Parser<Char>` | 単一の文字にマッチするパーサー |
| 2 | `StringParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/StringParser.kt` | 8 | `Parser<String>` | 文字列にマッチするパーサー |
| 3 | `RegexParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/RegexParser.kt` | 7 | `Parser<MatchResult>` | 正規表現にマッチするパーサー |
| 4 | `ListParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/ListParser.kt` | 7 | `Parser<List<T>>` | 繰り返しパターンをパースするパーサー |
| 5 | `OrParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/OrParser.kt` | 7 | `Parser<T>` | 複数のパーサーの選択肢（OR）を表すパーサー |
| 6 | `OptionalParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/OptionalParser.kt` | 8 | `Parser<Tuple1<T?>>` | オプショナルなマッチを扱うパーサー |
| 7 | `ReferenceParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/ReferenceParser.kt` | 7 | `Parser<T>` | 遅延評価による前方参照を可能にするパーサー |
| 8 | `LookAheadParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/LookAheadParser.kt` | 7 | `Parser<T>` | 先読みアサーション（消費しない）パーサー |
| 9 | `NegativeLookAheadParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/NegativeLookAheadParser.kt` | 8 | `Parser<Tuple0>` | 否定先読みアサーション（消費しない）パーサー |
| 10 | `FixedParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/FixedParser.kt` | 8 | `Parser<T>` | 固定値を返すパーサー（入力を消費しない） |
| 11 | `StartOfInputParser` | `src/commonMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/StartOfInputParser.kt` | 18 | `Parser<Tuple0>` | 入力の開始位置にマッチするパーサー |
| 12 | `EndOfInputParser` | `src/commonMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/EndOfInputParser.kt` | 19 | `Parser<Tuple0>` | 入力の終端にマッチするパーサー |

---

### 2. オブジェクト宣言による実装（1件）

| No. | オブジェクト名 | ファイルパス | 行番号 | 型パラメータ | 説明 |
|-----|--------------|-------------|--------|------------|------|
| 1 | `FailParser` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/FailParser.kt` | 7 | `Parser<Nothing>` | 常に失敗するシングルトンパーサー |

---

### 3. ラムダ式による実装（関数内での生成）（5件）

これらはすべて拡張関数や通常の関数として定義され、関数型インターフェースのSAM変換により`Parser`のインスタンスを返します。

| No. | 関数名 | ファイルパス | 行番号 | 戻り値の型 | 説明 |
|-----|-------|-------------|--------|----------|------|
| 1 | `map` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/MapParser.kt` | 7 | `Parser<O>` | パーサーの結果を変換する中置関数。ラムダで`Parser`を生成 |
| 2 | `mapEx` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/MapParser.kt` | 12 | `Parser<O>` | パース結果とコンテキストを使って変換する中置関数。ラムダで`Parser`を生成 |
| 3 | `leftAssociative` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/ParserUtils.kt` | 8 | `Parser<T>` | 左結合演算子のパーサーをラムダで生成 |
| 4 | `rightAssociative` | `src/importedMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/parsers/ParserUtils.kt` | 18 | `Parser<T>` | 右結合演算子のパーサーをラムダで生成 |
| 5 | `combine` (生成コード) | `build-logic/src/main/kotlin/build_logic/TupleSources.kt` | 39 | `Parser<T>` | 2つのパーサーを結合するヘルパー関数。ラムダで`Parser`を生成（コード生成される） |

---

### 4. オブジェクト式による実装（テストとサンプルコード）（8件）

これらは匿名オブジェクトを使って複数のパーサーをグループ化し、内部で`Parser`型のプロパティを定義するパターンです。オブジェクト自体は`Parser`を実装していませんが、その内部に`Parser`型のプロパティを持ちます。

| No. | 変数名 | ファイルパス | 行番号 | 用途 | 説明 |
|-----|-------|-------------|--------|------|------|
| 1 | `parser` | `src/importedTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ParserTest.kt` | 223 | テスト | デリゲーションパーサーのテスト。算術式パーサーを含むオブジェクト式 |
| 2 | `language` | `src/importedTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ParserTest.kt` | 239 | テスト | キャッシュのテスト用言語定義オブジェクト |
| 3 | `jsonString` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/JsonParserTest.kt` | 63 | テスト | JSON文字列パーサー。エスケープシーケンスを含む複雑なパーサー定義 |
| 4 | `templateStringParser` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/TemplateStringTutorialTest.kt` | 26 | テスト | テンプレート文字列パーサーのチュートリアル実装 |
| 5 | `language` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ParserAdditionalTest.kt` | 107 | テスト | 相互再帰パーサーのテスト用言語定義 |
| 6 | `expression` | `samples/minimal-jvm-sample/src/main/kotlin/io/github/mirrgieriana/xarpite/xarpeg/samples/java_run/Main.kt` | 13 | サンプル | 最小限のJVMサンプル。算術式パーサーのデモ |
| 7 | `ArithmeticParser` | `samples/interpreter/src/main/kotlin/io/github/mirrgieriana/xarpite/xarpeg/samples/interpreter/Main.kt` | 40 | サンプル | インタープリターサンプル。遅延評価付き算術式パーサー |
| 8 | `ExpressionGrammar` | `samples/online-parser/src/jsMain/kotlin/io/github/mirrgieriana/xarpite/xarpeg/samples/online/parser/OnlineParser.kt` | 115 | サンプル | オンラインパーサーの文法定義。変数、関数、ラムダを含む完全な式パーサー |

---

### 5. テスト用の直接的なラムダ実装（3件）

テストコードで動作確認のために作成された、`Parser { context, start -> ... }`形式の匿名パーサーです。

| No. | 変数名 | ファイルパス | 行番号 | 用途 | 説明 |
|-----|-------|-------------|--------|------|------|
| 1 | `countingParser` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ImportedParserCoverageTest.kt` | 74 | テスト | キャッシュの動作確認。nullを返すカウンターパーサー |
| 2 | `countingParser` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ImportedParserCoverageTest.kt` | 88 | テスト | キャッシュの動作確認。開始位置別にキャッシュを分離するテスト |
| 3 | `countingParser` | `src/commonTest/kotlin/io/github/mirrgieriana/xarpite/xarpeg/ImportedParserCoverageTest.kt` | 251 | テスト | キャッシュの無効化テスト用パーサー |

---

## 実装の総数

| 実装方法 | 件数 |
|---------|------|
| 通常のクラス宣言 | 12件 |
| オブジェクト宣言（シングルトン） | 1件 |
| ラムダ式（関数内生成） | 5件 |
| オブジェクト式（グルーピング） | 8件 |
| テスト用直接ラムダ | 3件 |
| **合計** | **29件** |

---

## 実装パターンの特徴

### 1. クラス宣言
最も基本的で再利用可能な実装方法。コアパーサーはすべてこの形式で実装されています。

```kotlin
class CharParser(val char: Char) : Parser<Char> {
    override fun parseOrNull(context: ParseContext, start: Int): ParseResult<Char>? {
        // 実装
    }
}
```

### 2. オブジェクト宣言（シングルトン）
状態を持たない唯一のインスタンスが必要な場合に使用。`FailParser`が該当します。

```kotlin
object FailParser : Parser<Nothing> {
    override fun parseOrNull(context: ParseContext, start: Int): ParseResult<Nothing>? {
        return null
    }
}
```

### 3. ラムダ式（SAM変換）
関数型インターフェースの特性を利用した実装。コンビネータ関数でよく使用されます。

```kotlin
infix fun <I : Any, O : Any> Parser<I>.map(function: (I) -> O) = Parser { context, start ->
    val result = context.parseOrNull(this, start) ?: return@Parser null
    ParseResult(function(result.value), result.start, result.end)
}
```

### 4. オブジェクト式
複数のパーサーをグループ化し、スコープを分けるために使用。テストやサンプルコードで頻繁に使われます。

```kotlin
val language = object {
    val number = +Regex("[0-9]+") map { it.value.toInt() }
    val expr: Parser<Int> = leftAssociative(number, -'+') { a, _, b -> a + b }
}
```

### 5. テスト用直接ラムダ
テストでの動作確認のために直接`Parser`コンストラクタを呼び出す形式。

```kotlin
val countingParser = Parser<Int> { _, _ ->
    counter++
    null
}
```

---

## 補足情報

### 生成コード
`combine`関数は`build-logic/src/main/kotlin/build_logic/TupleSources.kt`で定義されていますが、実際のコードは`generateTuples`タスクによってビルド時に生成されます。この関数は内部でラムダ式を使って`Parser`を生成します。

### 拡張関数とコンビネータ
`ParserExtensions.kt`、`MapParser.kt`、`ParserUtils.kt`などには、既存のパーサーを組み合わせたり変換したりするための多数の拡張関数とコンビネータが定義されていますが、これらは内部で上記の基本的な実装パターンを使用しています。

---

## まとめ

このリポジトリでは、`Parser`インターフェースの実装に以下の5つのパターンが使用されています：

1. **通常のクラス宣言**：基本的なパーサークラス（12件）
2. **オブジェクト宣言**：シングルトンパーサー（1件）
3. **関数内ラムダ**：コンビネータ関数（5件）
4. **オブジェクト式**：テスト・サンプル用グルーピング（8件）
5. **直接ラムダ**：テスト用の一時的なパーサー（3件）

合計**29箇所**で`Parser`インターフェースが実装されています。これらはそれぞれ異なる用途と利点を持ち、PEGパーサーライブラリとしての柔軟性と表現力を提供しています。
