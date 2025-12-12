# Step 3: 式と再帰を扱う

複雑な構文では自己参照や結合規則が必要になります。ここでは再帰パーサーの定義方法と、左右結合ヘルパーの使い方を紹介します。

## 再帰を含む式パーサー

```kotlin
import mirrg.xarpite.parser.Parser
import mirrg.xarpite.parser.parseAllOrThrow
import mirrg.xarpite.parser.parsers.*

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

- `parser { ... }` あるいは `by lazy` を使って自己参照を解決します。
- `leftAssociative` / `rightAssociative` に「項のパーサー」「演算子のパーサー」「結合関数」を渡すだけで、再帰 descent を自前で書く手間を省けます。
- 演算子も通常のパーサーなので、空白スキップや複数文字の記号も同じ構成で扱えます。

## パターンを追加する

- 優先順位の異なる演算子は、上の例のように「低優先度のレイヤー」を後に定義して重ねていきます。
- 単項演算子や前置/後置の記号も、`leftAssociative` 以前に前処理用の `map` を挟むだけで対応できます。

再帰と結合規則を押さえたら、実行時の例外やキャッシュの扱いを確認して完成度を高めましょう。  
→ [Step 4: エラーと実行時の振る舞い](04-runtime.md)
