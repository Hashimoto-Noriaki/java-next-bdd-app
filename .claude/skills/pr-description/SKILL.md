---
name: pr-description
description: このスキルはPRの説明文（タイトル・本文）を生成するときに使う。「PRを作って」「PR説明を書いて」「gh pr create したい」などと言われたら起動する。
version: 0.1.0
allowed-tools: [Bash, Read]
---

# PR Description 生成

このプロジェクト（Java/Spring Boot + BDD）のコーディングスタイルと開発フローに沿った PR タイトルと本文を生成する。

## 手順

1. 以下を並行して取得する:
   - `git log master..HEAD --oneline` でコミット一覧
   - `git diff master...HEAD --stat` で変更ファイル一覧
   - `git diff master...HEAD -- '*.feature'` で追加・変更された Gherkin シナリオ

2. 変更内容を分析する:
   - どのドメイン（auth / profile / matching / message）に関わる変更か
   - feature ファイルに書かれたシナリオ（振る舞い）は何か
   - どのレイヤー（Controller / Service / Repository / Domain）が実装されたか

3. 以下のフォーマットで PR タイトルと本文を出力する。

## 出力フォーマット

### タイトル

```
<prefix>: <日本語で変更の要約>（70文字以内）
```

prefix は `feat` / `fix` / `test` / `refactor` / `chore` から選ぶ。

### 本文

```markdown
## 概要

<このPRで何を実現するか。1〜3行で>

## BDD シナリオ

このPRで追加・変更したシナリオ:

- `<feature ファイルパス>`: <シナリオ名>
- ...

（feature ファイルの変更がない場合は省略）

## 実装内容

- [ ] <変更したクラス・レイヤーの箇条書き>

## テスト計画

- [ ] `./gradlew test` がグリーンであること
- [ ] `./gradlew test -Dcucumber.filter.tags="@<ドメインタグ>"` が通ること
- [ ] （手動確認が必要な場合はここに記載）
```

## 注意事項

- 本文は日本語で書く
- BDD のセンターピン（シナリオは非エンジニアが読める言葉で）を守り、説明にも技術用語を乱用しない
- シナリオが変更されていない場合は「BDD シナリオ」セクションを省略する
- 生成した PR タイトル・本文を表示したあと、`gh pr create` コマンドを実行するか確認する
