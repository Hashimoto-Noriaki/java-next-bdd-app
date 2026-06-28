# 技術スタック・プロジェクト構造

## 技術スタック

| 役割 | 技術 |
|---|---|
| フレームワーク | Spring Boot 4.1.0 (Java 19) |
| ビルド | Gradle (Kotlin DSL) |
| DB | PostgreSQL (本番) / H2 (テスト) |
| ORM | Spring Data JPA / Hibernate |
| 認証 | Spring Security + JWT |
| BDD | Cucumber 7.x + JUnit 5 |
| モック | Mockito |
| コンテナ | Testcontainers (統合テスト) |
| API仕様 | OpenAPI 3 (springdoc-openapi) |
| フロントエンド | **Next.js（導入検討中）** |

## プロジェクト構造

```
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

## ドメインモデル

```
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
