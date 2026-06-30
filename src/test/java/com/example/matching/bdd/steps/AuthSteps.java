package com.example.matching.bdd.steps;

import com.example.matching.infrastructure.UserRepository;
import com.example.matching.presentation.dto.LoginRequest;
import com.example.matching.presentation.dto.RegisterRequest;
import io.cucumber.java.Before;
import io.cucumber.java.ja.かつ;
import io.cucumber.java.ja.もし;
import io.cucumber.java.ja.ならば;
import io.cucumber.java.ja.前提;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    public AuthSteps() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(response -> false);
    }

    private ResponseEntity<String> response;
    private String lastEmail;
    private String lastPassword;

    @Before
    public void setUp() {
        userRepository.deleteAll();
        response = null;
        lastEmail = null;
        lastPassword = null;
    }

    @前提("メールアドレスが未登録である")
    public void メールアドレスが未登録である() {
        // @Before でリセット済み
    }

    @前提("{string} はすでに登録済みである")
    public void はすでに登録済みである(String email) {
        registerUser("既存ユーザー", email, "password123");
        lastEmail = email;
    }

    @前提("{string} で登録済みのユーザーがいる")
    public void で登録済みのユーザーがいる(String email) {
        registerUser("テストユーザー", email, "password123");
        lastEmail = email;
        lastPassword = "password123";
    }

    @もし("ユーザーが名前・メール・パスワードを入力して登録する")
    public void ユーザーが名前メールパスワードを入力して登録する() {
        lastEmail = "taro@example.com";
        lastPassword = "password123";
        response = restTemplate.postForEntity(
                url("/auth/register"),
                new RegisterRequest("山田太郎", lastEmail, lastPassword),
                String.class
        );
    }

    @もし("同じメールアドレスで登録しようとする")
    public void 同じメールアドレスで登録しようとする() {
        response = restTemplate.postForEntity(
                url("/auth/register"),
                new RegisterRequest("別ユーザー", lastEmail, "password123"),
                String.class
        );
    }

    @もし("メールアドレスとパスワードを入力してログインする")
    public void メールアドレスとパスワードを入力してログインする() {
        response = restTemplate.postForEntity(
                url("/auth/login"),
                new LoginRequest(lastEmail, lastPassword),
                String.class
        );
    }

    @もし("間違ったパスワードでログインしようとする")
    public void 間違ったパスワードでログインしようとする() {
        response = restTemplate.postForEntity(
                url("/auth/login"),
                new LoginRequest(lastEmail, "wrongpassword"),
                String.class
        );
    }

    @ならば("登録完了のメッセージが表示される")
    public void 登録完了のメッセージが表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @かつ("登録したメールアドレスでログインできる")
    public void 登録したメールアドレスでログインできる() {
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                url("/auth/login"),
                new LoginRequest(lastEmail, lastPassword),
                String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ならば("「このメールアドレスはすでに使われています」と表示される")
    public void このメールアドレスはすでに使われていますと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("このメールアドレスはすでに使われています");
    }

    @ならば("ログインに成功する")
    public void ログインに成功する() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @ならば("「メールアドレスまたはパスワードが正しくありません」と表示される")
    public void メールアドレスまたはパスワードが正しくありませんと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("メールアドレスまたはパスワードが正しくありません");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private void registerUser(String name, String email, String password) {
        restTemplate.postForEntity(
                url("/auth/register"),
                new RegisterRequest(name, email, password),
                String.class
        );
    }
}
