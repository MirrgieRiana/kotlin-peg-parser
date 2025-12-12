# Kotlin PEG Parser Guide

このライブラリを段階的に学べるよう、Docs はテーマごとのサブページに分けています。  
API シグネチャは IDE の補完や KDoc を参照してください。

## 読み進め方（分岐ページ）

- **Step 1 – 最初のパーサーを作る:** DSL の最小例と実行方法  
  → [01-quickstart.md](01-quickstart.md)
- **Step 2 – パーサーを組み合わせる:** シーケンス・選択・繰り返しなど基本の合成パターン  
  → [02-combinators.md](02-combinators.md)
- **Step 3 – 式と再帰を扱う:** `parser {}` / `by lazy` を使った再帰と結合規則ヘルパー  
  → [03-expressions.md](03-expressions.md)
- **Step 4 – エラーと実行時の振る舞い:** 例外、全入力消費、キャッシュのオン/オフ  
  → [04-runtime.md](04-runtime.md)

## さらに詳しく知りたいとき

- 実際の挙動はテストケース `imported/src/commonTest/kotlin/ParserTest.kt` が手早いリファレンスです。
- 実装の詳細は `imported/src/commonMain/kotlin/mirrg/xarpite/parser` 配下を参照してください。
- API の戻り値や型は IDE の KDoc、コード補完を使うと確実です。
