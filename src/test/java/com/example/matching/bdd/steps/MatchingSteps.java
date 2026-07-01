package com.example.matching.bdd.steps;

import com.example.matching.bdd.ScenarioContext;
import com.example.matching.infrastructure.UserRepository;
import com.example.matching.presentation.dto.LoginRequest;
import com.example.matching.presentation.dto.LoginResponse;
import com.example.matching.presentation.dto.RegisterRequest;
import io.cucumber.java.ja.かつ;
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

public class MatchingSteps {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<String> response;

    public MatchingSteps() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(r -> false);
    }

    @前提("いいねを送る相手が存在する")
    public void いいねを送る相手が存在する() {
        scenarioContext.setTargetUserId(registerOtherUser("hanako@example.com", "鈴木花子"));
    }

    @前提("他のユーザーが存在する")
    public void 他のユーザーが存在する() {
        scenarioContext.setTargetUserId(registerOtherUser("hanako@example.com", "鈴木花子"));
    }

    @前提("相手からすでにいいねをもらっている")
    public void 相手からすでにいいねをもらっている() {
        setupOtherUserAndReceiveLike("hanako@example.com", "鈴木花子");
    }

    @かつ("すでにその相手にいいねを送っている")
    public void すでにその相手にいいねを送っている() {
        postWithAuth("/likes/" + scenarioContext.getTargetUserId());
    }

    @前提("マッチが成立している相手がいる")
    public void マッチが成立している相手がいる() {
        setupOtherUserAndReceiveLike("hanako@example.com", "鈴木花子");
        postWithAuth("/likes/" + scenarioContext.getTargetUserId());
    }

    private void setupOtherUserAndReceiveLike(String email, String name) {
        String otherToken = registerAndLogin(email, name);
        Long myId = userRepository.findByEmail("taro@example.com").get().getId();
        Long otherId = userRepository.findByEmail(email).get().getId();
        scenarioContext.setTargetUserId(otherId);
        postWithToken("/likes/" + myId, otherToken);
    }

    @もし("その相手にいいねを送る")
    public void その相手にいいねを送る() {
        response = postWithAuth("/likes/" + scenarioContext.getTargetUserId());
    }

    @もし("マッチ一覧を表示する")
    public void マッチ一覧を表示する() {
        response = getWithAuth("/matches");
    }

    @もし("候補一覧を表示する")
    public void 候補一覧を表示する() {
        response = getWithAuth("/candidates");
    }

    @ならば("いいねが送信される")
    public void いいねが送信される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @ならば("マッチが成立する")
    public void マッチが成立する() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"matched\":true");
    }

    @ならば("マッチは成立しない")
    public void マッチは成立しない() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"matched\":false");
    }

    @ならば("マッチした相手が一覧に表示される")
    public void マッチした相手が一覧に表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("鈴木花子");
    }

    @ならば("他のユーザーが一覧に表示される")
    public void 他のユーザーが一覧に表示される() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("鈴木花子");
    }

    @ならば("いいねした相手は一覧に表示されない")
    public void いいねした相手は一覧に表示されない() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContain("鈴木花子");
    }

    @ならば("マッチした相手は一覧に表示されない")
    public void マッチした相手は一覧に表示されない() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContain("鈴木花子");
    }

    private Long registerOtherUser(String email, String name) {
        restTemplate.postForEntity(url("/auth/register"),
                new RegisterRequest(name, email, "password123"), String.class);
        return userRepository.findByEmail(email).get().getId();
    }

    private String registerAndLogin(String email, String name) {
        restTemplate.postForEntity(url("/auth/register"),
                new RegisterRequest(name, email, "password123"), String.class);
        return loginAndGetToken(email);
    }

    private String loginAndGetToken(String email) {
        ResponseEntity<LoginResponse> res = restTemplate.postForEntity(
                url("/auth/login"),
                new LoginRequest(email, "password123"),
                LoginResponse.class);
        return res.getBody().token();
    }

    private ResponseEntity<String> postWithAuth(String path) {
        HttpHeaders headers = bearerHeaders(scenarioContext.getJwtToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url(path), HttpMethod.POST,
                new HttpEntity<>(headers), String.class);
    }

    private void postWithToken(String path, String token) {
        HttpHeaders headers = bearerHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> setupResponse = restTemplate.exchange(url(path), HttpMethod.POST,
                new HttpEntity<>(headers), String.class);
        if (!setupResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Setup request failed with status: " + setupResponse.getStatusCode());
        }
    }

    private ResponseEntity<String> getWithAuth(String path) {
        return restTemplate.exchange(url(path), HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(scenarioContext.getJwtToken())), String.class);
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
