# 開発フロー・協業方法

## シナリオの起票パターン

### パターン A: エンジニアが指示する場合

技術的な言葉で要件を伝えてよい。Claude が business-readable な Gherkin に変換して提案する。

```
ユーザー: 「POST /auth/register で重複メールは 409 を返したい」
Claude:  → 技術用語なしのシナリオ候補を提案
         → 合意後に feature ファイルに書く
```

### パターン B: 非エンジニアが参加する場合

非エンジニアが日常言語でユースケースを話す → Claude がシナリオ化 → 全員でレビュー。

```
非エンジニア: 「すでに使われているメールで登録しようとしたらエラーにしたい」
Claude:      → シナリオ候補を提案
             → エンジニアが技術詳細をステップ定義に落とす
```

## 1ユースケースの開発サイクル

```
1. ユーザーが要件を伝える（技術的でも日常語でも可）
2. Claude がシナリオ候補（Gherkin）を提案
3. ユーザーが承認 or 修正                        ← ここで一度止まる
4. feature ファイルに書いて ./gradlew test → Red 確認
5. test: シナリオ追加 としてコミット              ← 実装前にコミット
6. ステップ定義を実装
7. Controller → Service → Repository の順で実装
8. ./gradlew test → Green 確認
9. feat: 機能実装 としてコミット
```

**シナリオコミットを実装前に切る理由**:
- 「何を作るか」と「どう作ったか」を git 履歴で分離できる
- シナリオは非エンジニアもレビューできる成果物として記録に残る

## よく使うコマンド

```bash
# 全テスト実行（@wip 除く）
./gradlew test

# ドメイン別・タグ指定実行
./gradlew test -Dcucumber.filter.tags="@smoke"
./gradlew test -Dcucumber.filter.tags="@auth"

# アプリ起動
./gradlew bootRun
```
