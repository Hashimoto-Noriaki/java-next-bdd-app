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

### Phase 1: シナリオ（feature/scenario-xxx ブランチ）

```text
1. ユーザーが要件を伝える（技術的でも日常語でも可）
2. Claude がシナリオ候補（Gherkin）を提案
3. ユーザーが承認 or 修正                        ← ここで一度止まる
4. @wip タグ付きで feature ファイルに書く
5. ./gradlew test → Red 確認
6. test: シナリオ追加 としてコミット
7. PR を作成 → レビュー → master にマージ
```

### Phase 2: 実装（feature/impl-xxx ブランチ）

```text
8. ステップ定義を実装
9. Controller → Service → Repository の順で実装
10. ./gradlew test → Green 確認
11. シナリオから @wip タグを外す
12. feat: 機能実装 としてコミット
13. PR を作成 → レビュー → master にマージ
```

**2フェーズに分ける理由**:
- 「何を作るか」を先に master に記録し、非エンジニアがレビューできる
- `@wip` タグにより CI はシナリオ単独マージで壊れない
- 実装 PR のレビュー時に「シナリオ通りに動くか」を軸に見られる

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
