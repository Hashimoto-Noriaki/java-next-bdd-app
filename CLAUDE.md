# jave-next-bdd-app — Spring Boot マッチングアプリ (BDD)

## プロジェクト概要

Java / Spring Boot 3.x でマッチングアプリを構築し、**BDD（振る舞い駆動開発）** を実践するプロジェクト。
Cucumber + JUnit 5 を用いて「ビジネス要件 → Gherkin シナリオ → 実装」のサイクルを回す。

---

## 技術スタック

| 役割 | 技術 |
|---|---|
| フレームワーク | Spring Boot 3.x (Java 21) |
| ビルド | Gradle (Kotlin DSL) |
| DB | PostgreSQL (本番) / H2 (テスト) |
| ORM | Spring Data JPA / Hibernate |
| 認証 | Spring Security + JWT |
| BDD | Cucumber 7.x + JUnit 5 |
| モック | Mockito |
| コンテナ | Testcontainers (統合テスト) |
| API仕様 | OpenAPI 3 (springdoc-openapi) |
| フロントエンド | **Next.js（導入検討中）** |

---

## プロジェクト構造

```bash
src/
├── main/
│   ├── java/com/example/matching/
│   │   ├── domain/          # エンティティ・値オブジェクト
│   │   ├── application/     # ユースケース（サービス層）
│   │   ├── infrastructure/  # Repository実装・外部連携
│   │   └── presentation/    # Controller・DTO
│   └── resources/
│       └── application.yml
└── test/
    ├── java/com/example/matching/
    │   ├── bdd/
    │   │   ├── steps/       # Cucumber ステップ定義
    │   │   ├── config/      # CucumberSpringConfiguration
    │   │   └── runner/      # CucumberRunner (JUnit5)
    │   └── unit/            # ユニットテスト
    └── resources/
        └── features/        # Gherkin フィーチャーファイル
            ├── auth/
            ├── profile/
            ├── matching/
            └── message/
```

---

## BDD 開発ルール

### 1. フィーチャーファイルの書き方

- **言語**: 日本語で記述する (`# language: ja`)
- **配置**: `src/test/resources/features/<ドメイン>/<機能名>.feature`
- **命名**: スネークケース（例: `user_registration.feature`）

```gherkin
# language: ja
# feature: <ドメイン名>/<機能名>
機能: ユーザー登録

  背景:
    前提 メールアドレスが未登録である

  シナリオ: 有効な情報で登録できる
    もし ユーザーが以下の情報で登録する:
      | 名前     | メール              | パスワード    |
      | 田中太郎 | taro@example.com | Password123! |
    ならば ステータスコード 201 が返る
    かつ レスポンスにユーザーIDが含まれる

  シナリオ: 既存メールアドレスでは登録できない
    前提 "taro@example.com" はすでに登録済みである
    もし ユーザーが同じメールアドレスで登録しようとする
    ならば ステータスコード 409 が返る
```

### 2. ステップ定義のルール

- ステップクラスはドメインごとに分割: `AuthSteps`, `ProfileSteps`, `MatchingSteps`
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@CucumberContextConfiguration` を使う
- DBリセットは `@Before` フックで行う（テスト間の独立性を保つ）

### 3. 開発サイクル

```bash
1. feature ファイルにシナリオを書く（Red）
2. gradle test → 未定義ステップを確認
3. ステップ定義を実装
4. アプリコードを実装して Green にする
5. リファクタリング
```

### 4. タグ運用

| タグ | 用途 |
|---|---|
| `@smoke` | 最低限の動作確認（CI必須） |
| `@regression` | 全回帰テスト |
| `@wip` | 作業中（CIから除外） |
| `@auth` / `@profile` / `@matching` | ドメイン別実行 |

---

## ドメインモデル（マッチングアプリ）

```bash
User（ユーザー）
  └── Profile（プロフィール）
  └── Like（いいね）
  └── Match（マッチ） ← 相互いいねで成立
  └── Message（メッセージ） ← マッチ成立後に送受信可能
```

### 主要ユースケース

1. **認証**: 登録 / ログイン / JWT発行
2. **プロフィール**: 作成 / 更新 / 閲覧
3. **マッチング**: いいね送信 / マッチ確認 / 候補一覧取得
4. **メッセージ**: 送信 / 一覧取得

---

## コーディング規約

- **パッケージ**: `com.example.matching`
- **例外**: カスタム例外 + `@ControllerAdvice` で統一ハンドリング
- **DTO**: `record` を使用（Java 21）
- **テスト**: BDDシナリオを先に書いてから実装（テストファースト必須）
- **コメント**: 「なぜ」が非自明な箇所のみ記述、「何をしているか」は書かない
- **コミット**: `feat:` / `fix:` / `test:` / `refactor:` プレフィックスを使う

---

## よく使うコマンド

```bash
# ビルド
./gradlew build

# 全テスト実行
./gradlew test

# BDD テストのみ（Cucumberタグ指定）
./gradlew test -Dcucumber.filter.tags="@smoke"

# 作業中シナリオを除いた回帰テスト
./gradlew test -Dcucumber.filter.tags="@regression and not @wip"

# アプリ起動（開発用）
./gradlew bootRun
```
