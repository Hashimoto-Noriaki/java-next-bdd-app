# BDD 開発ルール

## シナリオの考え方

### ユーザーの旅から始める

機能を実装する前に「ユーザーが何をするか」の流れを整理する。

```
登録 → ログイン → プロフィール作成 → 相手を探す → いいね → マッチ → メッセージ
```

この旅の順番が、シナリオを書く優先順位になる。後のステップは前のステップに依存するため、土台から実装する。

### Given / When / Then の考え方

| キーワード | 日本語 | 考え方 |
|---|---|---|
| Given | 前提 | テストが始まる前の世界の状態 |
| When | もし | ユーザーが起こすアクション（1シナリオに1つ） |
| Then | ならば | 目に見える結果（エラーメッセージ・画面の変化） |

**「もし」は1シナリオに1アクション。** 複数のアクションが必要なら複数シナリオに分ける。

### シナリオの洗い出し方

1つの機能に対して以下の観点でシナリオを列挙する:

- **正常系**: 期待通りに操作できる
- **異常系（入力）**: 不正な値・重複・未入力
- **異常系（権限）**: 未ログイン・他人のリソース

全部書いてから実装するのではなく、**1シナリオ書いて → 実装 → Green を確認してから次へ**進む。

---

## センターピン：シナリオは非エンジニアが読める言葉で書く

feature ファイルはビジネス・開発・QA の三者が共通言語として読むもの。
**技術用語（HTTPステータスコード・JSON・レスポンス等）をシナリオに書かない。**
技術的な詳細はステップ定義の中に隠す。

```
❌ ならば ステータスコード 201 が返る
✅ ならば 登録完了のメッセージが表示される

❌ ならば ステータスコード 409 が返る
✅ ならば 「このメールアドレスはすでに使われています」と表示される
```

## フィーチャーファイルの書き方

- **言語**: 日本語で記述する (`# language: ja`)
- **配置**: `src/test/resources/features/<ドメイン>/<機能名>.feature`
- **命名**: スネークケース（例: `user_registration.feature`）
- **粒度**: 1シナリオ = 1つの振る舞い。複数の結果を1シナリオに詰め込まない

```gherkin
# language: ja
機能: ユーザー登録

  背景:
    前提 メールアドレスが未登録である

  @smoke @auth
  シナリオ: 有効な情報で登録できる
    もし ユーザーが名前・メール・パスワードを入力して登録する
    ならば 登録完了のメッセージが表示される
    かつ 登録したメールアドレスでログインできる

  @auth
  シナリオ: すでに使われているメールアドレスでは登録できない
    前提 "taro@example.com" はすでに登録済みである
    もし 同じメールアドレスで登録しようとする
    ならば 「このメールアドレスはすでに使われています」と表示される
```

## アンチパターン

### ❌ 技術用語・実装詳細をシナリオに書く

```gherkin
# NG: HTTPステータス・JSONフィールドが入っている
ならば ステータスコード 201 が返る
かつ レスポンスの "token" フィールドにJWTが含まれる
```

```gherkin
# OK: ユーザー視点の言葉
ならば 登録完了のメッセージが表示される
かつ 登録したメールアドレスでログインできる
```

### ❌ UI操作の詳細を書く

```gherkin
# NG: UI実装に依存している
もし 「名前」フィールドに "山田太郎" を入力する
  かつ 「登録」ボタンをクリックする
```

```gherkin
# OK: ユーザーの意図を書く
もし ユーザーが名前・メール・パスワードを入力して登録する
```

### ❌ 1シナリオに複数の振る舞いを詰め込む

```gherkin
# NG: 登録・ログイン・プロフィール更新を1シナリオでテストしている
シナリオ: ユーザーが一連の操作を行う
  もし ユーザーが登録する
  かつ ログインする
  かつ プロフィールを更新する
  ならば プロフィールが反映されている
```

```gherkin
# OK: 1シナリオ1振る舞い。前提条件として既存機能を使う
シナリオ: プロフィールを更新できる
  前提 ユーザーとしてログイン済みである
  もし プロフィールの自己紹介文を変更する
  ならば 変更した自己紹介文が表示される
```

### ❌ Background に関係のないセットアップを置く

```gherkin
# NG: 一部のシナリオにしか必要ない前提が Background に入っている
背景:
  前提 メールアドレスが未登録である
  かつ "taro@example.com" はすでに登録済みである  ← 重複チェックシナリオにしか不要
```

```gherkin
# OK: すべてのシナリオに共通する前提のみ Background に置く
背景:
  前提 メールアドレスが未登録である
```

### ❌ Scenario Outline を乱用して可読性を下げる

```gherkin
# NG: 入力値の組み合わせテストに Scenario Outline を使うのは OK だが、
#     シナリオの意味が失われるほど汎化してはいけない
シナリオアウトライン: 操作を行う
  もし <操作> する
  ならば <結果> が表示される
  例:
    | 操作         | 結果                 |
    | 登録         | 登録完了             |
    | 重複登録     | エラーメッセージ     |
```

```gherkin
# OK: 同じビジネスルールで値だけ変わるパターンに限定して使う
シナリオアウトライン: パスワードが条件を満たさない場合は登録できない
  もし パスワードに "<パスワード>" を入力して登録しようとする
  ならば 「パスワードは8文字以上で入力してください」と表示される
  例:
    | パスワード |
    | abc        |
    | 1234567    |
```

## ステップ定義のルール

- ステップクラスはドメインごとに分割: `AuthSteps`, `ProfileSteps`, `MatchingSteps`
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@CucumberContextConfiguration` を使う
- DBリセットは `@Before` フックで行う（テスト間の独立性を保つ）
- HTTP ステータスコードや JSON の検証はステップ定義の中に閉じ込める

### ステップ定義の実例

```java
@Component
@ScenarioScope
public class AuthSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<RegisterResponse> response;

    @Before
    public void setUp() {
        userRepository.deleteAll();
        response = null;
    }

    @Given("メールアドレスが未登録である")
    public void メールアドレスが未登録である() {
        // DBリセット済みなので何もしない（@Beforeで保証）
    }

    @Given("{string} はすでに登録済みである")
    public void はすでに登録済みである(String email) {
        restTemplate.postForEntity(
            "/auth/register",
            new RegisterRequest("既存ユーザー", email, "password123"),
            Void.class
        );
    }

    @When("ユーザーが名前・メール・パスワードを入力して登録する")
    public void ユーザーが名前メールパスワードを入力して登録する() {
        response = restTemplate.postForEntity(
            "/auth/register",
            new RegisterRequest("山田太郎", "taro@example.com", "password123"),
            RegisterResponse.class
        );
    }

    @Then("登録完了のメッセージが表示される")
    public void 登録完了のメッセージが表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Then("「このメールアドレスはすでに使われています」と表示される")
    public void このメールアドレスはすでに使われていますと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
```

**ポイント**:
- `@ScenarioScope`（`io.cucumber.spring.ScenarioScope`）を付与してシナリオ単位でインスタンスを生成する。`@Component` のデフォルトはシングルトンのため、並列実行時に `response` などのフィールドがシナリオ間で漏洩する
- `@Before` で `response = null` を明示的にリセットする。`@When` が実行されないシナリオや例外発生時に前シナリオの値が残るのを防ぐ
- シナリオの日本語をそのままメソッド名にする（Cucumber が自動でマッピング）
- アサーションには AssertJ（`assertThat`）を使う

## タグ運用

| タグ | 用途 |
|---|---|
| `@smoke` | 最低限の動作確認（CI必須） |
| `@regression` | 全回帰テスト |
| `@wip` | 作業中（CIから除外） |
| `@auth` / `@profile` / `@matching` | ドメイン別実行 |
