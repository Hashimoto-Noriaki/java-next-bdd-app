# コーディング規約
## 基本方針

- **パッケージ**: `com.example.matching`
- **DTO**: `record` を使用（Java 19+）
- **コメント**: 「なぜ」が非自明な箇所のみ。「何をしているか」は書かない
- **コミット**: `feat:` / `fix:` / `test:` / `refactor:` / `chore:` プレフィックスを使う
- **import**: ワイルドカード禁止（`import java.util.*` は不可）

## 命名規則

| 対象 | ルール | 例 |
|---|---|---|
| クラス | PascalCase | `UserRegistrationService` |
| メソッド・変数 | camelCase | `findByEmail` |
| 定数 | UPPER_SNAKE_CASE | `MAX_MESSAGE_LENGTH` |
| パッケージ | すべて小文字 | `com.example.matching.domain` |
| feature ファイル | スネークケース | `user_registration.feature` |

## レイヤー別実装パターン

### presentation（Controller）

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistrationService service;

    public AuthController(UserRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        var result = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
```

- フィールドインジェクション禁止。コンストラクタインジェクションを使う
- `@Valid` でリクエストバリデーションを行う
- レスポンスは `ResponseEntity<T>` で返す

### application（Service）

```java
@Service
@Transactional
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        // ...
    }
}
```

- インターフェースは実装が複数ある場合のみ定義する（単一実装に interface は不要）
- `@Transactional` はクラスレベルに付与し、読み取り専用メソッドには `@Transactional(readOnly = true)` を追記

### domain（Entity / Value Object）

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // ビジネスルールはエンティティに持たせる
    public boolean canSendMessageTo(User other) {
        // ...
    }
}
```

### infrastructure（Repository）

```java
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
```

- カスタムクエリが必要な場合は `@Query` を使う（ネイティブクエリより JPQL を優先）

## 例外処理

カスタム例外 + `@ControllerAdvice` で一元ハンドリングする。

```java
// カスタム例外（ドメイン層）
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("このメールアドレスはすでに使われています: " + email);
    }
}

// ハンドラー（presentation 層）
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

- 例外クラス名は `XxxException` 形式
- HTTP ステータスへのマッピングは `@ControllerAdvice` に閉じ込め、Service 層で `HttpStatus` を参照しない

## バリデーション

リクエスト DTO に Bean Validation アノテーションを付与し、Controller で `@Valid` を使う。

```java
public record RegisterRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @Size(min = 8) String password
) {}
```

カスタムバリデーションが必要な場合は `ConstraintValidator` を実装する。

## テスト

- **BDDシナリオを先に書いてから実装**（テストファースト必須）
- ユニットテストは `src/test/java/.../unit/` 配下に置く
- BDD ステップ定義は `src/test/java/.../bdd/steps/` 配下にドメインごとに分割
