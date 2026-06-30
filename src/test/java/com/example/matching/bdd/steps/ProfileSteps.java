package com.example.matching.bdd.steps;

import com.example.matching.bdd.ScenarioContext;
import com.example.matching.presentation.dto.CreateProfileRequest;
import com.example.matching.presentation.dto.ProfileResponse;
import com.example.matching.presentation.dto.RegisterRequest;
import com.example.matching.domain.Gender;
import io.cucumber.java.ja.もし;
import io.cucumber.java.ja.ならば;
import io.cucumber.java.ja.前提;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileSteps {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate;

    @Autowired
    private ScenarioContext scenarioContext;

    private ResponseEntity<String> response;

    public ProfileSteps() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(response -> false);
    }

    @前提("プロフィールを登録済みである")
    public void プロフィールを登録済みである() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        postWithAuth("/profile", request, String.class);
    }

    @前提("別のユーザーがプロフィールを登録済みである")
    public void 別のユーザーがプロフィールを登録済みである() {
        String email = "hanako@example.com";
        String password = "password123";

        restTemplate.postForEntity(
                url("/auth/register"),
                new RegisterRequest("鈴木花子", email, password),
                String.class
        );

        var loginResponse = restTemplate.postForEntity(
                url("/auth/login"),
                new com.example.matching.presentation.dto.LoginRequest(email, password),
                com.example.matching.presentation.dto.LoginResponse.class
        );
        String otherToken = loginResponse.getBody().token();

        var request = new CreateProfileRequest(
                Gender.FEMALE, 23, "大阪府",
                null, null, null, null, null, null, null, null, null
        );
        HttpHeaders headers = bearerHeaders(otherToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.exchange(url("/profile"), HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        var profileResponse = restTemplate.exchange(
                url("/profile/me"), HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(otherToken)), ProfileResponse.class);
        scenarioContext.setTargetUserId(profileResponse.getBody().userId());
    }

    @もし("自己紹介文を新しい内容に変更する")
    public void 自己紹介文を新しい内容に変更する() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, "こんにちは、よろしくお願いします", null, null, null
        );
        response = putWithAuth("/profile", request, String.class);
    }

    @もし("趣味・ライフスタイル・恋愛や結婚歴を入力して更新する")
    public void 趣味ライフスタイル恋愛や結婚歴を入力して更新する() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, null, "読書・映画", "インドア", "独身"
        );
        response = putWithAuth("/profile", request, String.class);
    }

    @もし("住まいを削除して更新しようとする")
    public void 住まいを削除して更新しようとする() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "",
                null, null, null, null, null, null, null, null, null
        );
        response = putWithAuth("/profile", request, String.class);
    }

    @ならば("変更した自己紹介文が表示される")
    public void 変更した自己紹介文が表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("こんにちは、よろしくお願いします");
    }

    @ならば("更新した内容がプロフィールに表示される")
    public void 更新した内容がプロフィールに表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("独身");
    }

    @もし("性別・年齢・住まいを入力してプロフィールを登録する")
    public void 性別年齢住まいを入力してプロフィールを登録する() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("必須項目に加えて職業・年収・身長・学歴・体型・自己紹介文も入力してプロフィールを登録する")
    public void 必須項目に加えて任意項目も入力してプロフィールを登録する() {
        var request = new CreateProfileRequest(
                Gender.MALE, 28, "神奈川県",
                "エンジニア", 600, 175, "大学卒", "普通",
                "よろしくお願いします", "読書・映画", "インドア", "独身"
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("性別を選択せずにプロフィールを登録しようとする")
    public void 性別を選択せずにプロフィールを登録しようとする() {
        var request = new CreateProfileRequest(
                null, 25, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("年齢を入力せずにプロフィールを登録しようとする")
    public void 年齢を入力せずにプロフィールを登録しようとする() {
        var request = new CreateProfileRequest(
                Gender.MALE, null, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("17歳の年齢でプロフィールを登録しようとする")
    public void 歳の年齢でプロフィールを登録しようとする() {
        var request = new CreateProfileRequest(
                Gender.MALE, 17, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("住まいを選択せずにプロフィールを登録しようとする")
    public void 住まいを選択せずにプロフィールを登録しようとする() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("500文字を超える自己紹介文でプロフィールを登録しようとする")
    public void 文字を超える自己紹介文でプロフィールを登録しようとする() {
        String tooLong = "あ".repeat(501);
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, tooLong, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("プロフィールを登録しようとする")
    public void プロフィールを登録しようとする() {
        var request = new CreateProfileRequest(
                Gender.MALE, 25, "東京都",
                null, null, null, null, null, null, null, null, null
        );
        response = postWithAuth("/profile", request, String.class);
    }

    @もし("マイプロフィールを表示する")
    public void マイプロフィールを表示する() {
        response = getWithAuth("/profile/me", String.class);
    }

    @もし("そのユーザーのプロフィールを表示する")
    public void そのユーザーのプロフィールを表示する() {
        response = getWithAuth("/profile/" + scenarioContext.getTargetUserId(), String.class);
    }

    @もし("他のユーザーのプロフィールを表示しようとする")
    public void 他のユーザーのプロフィールを表示しようとする() {
        response = getWithAuth("/profile/1", String.class);
    }

    @ならば("プロフィールが保存される")
    public void プロフィールが保存される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @ならば("入力したすべての情報がプロフィールに表示される")
    public void 入力したすべての情報がプロフィールに表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("エンジニア");
    }

    @ならば("登録した情報が表示される")
    public void 登録した情報が表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("東京都");
    }

    @ならば("そのユーザーの公開情報が表示される")
    public void そのユーザーの公開情報が表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("大阪府");
    }

    @ならば("「性別を選択してください」と表示される")
    public void 性別を選択してくださいと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("性別を選択してください");
    }

    @ならば("「年齢を入力してください」と表示される")
    public void 年齢を入力してくださいと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("年齢を入力してください");
    }

    @ならば("「18歳以上の方のみご利用いただけます」と表示される")
    public void 歳以上の方のみご利用いただけますと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("18歳以上の方のみご利用いただけます");
    }

    @ならば("「住まいを選択してください」と表示される")
    public void 住まいを選択してくださいと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("住まいを選択してください");
    }

    @ならば("「自己紹介文は500文字以内で入力してください」と表示される")
    public void 自己紹介文は文字以内で入力してくださいと表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("自己紹介文は500文字以内で入力してください");
    }

    @ならば("ログインを求めるメッセージが表示される")
    public void ログインを求めるメッセージが表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private <T> ResponseEntity<T> putWithAuth(String path, Object body, Class<T> responseType) {
        HttpHeaders headers = bearerHeaders(scenarioContext.getJwtToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url(path), HttpMethod.PUT,
                new HttpEntity<>(body, headers), responseType);
    }

    private <T> ResponseEntity<T> postWithAuth(String path, Object body, Class<T> responseType) {
        HttpHeaders headers = bearerHeaders(scenarioContext.getJwtToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url(path), HttpMethod.POST,
                new HttpEntity<>(body, headers), responseType);
    }

    private <T> ResponseEntity<T> getWithAuth(String path, Class<T> responseType) {
        return restTemplate.exchange(url(path), HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(scenarioContext.getJwtToken())), responseType);
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
