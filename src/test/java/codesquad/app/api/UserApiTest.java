package codesquad.app.api;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.InMemoryUserDatabase;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.exception.BadRequestException;
import codesquad.http.message.SessionManager;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestBody;
import codesquad.http.message.request.RequestStartLine;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static codesquad.utils.StringUtils.NEW_LINE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserApiTest {

    UserDatabase userDatabase;
    UserApi userApi;
    User user;

    @BeforeEach
    void setUp() {
        userDatabase = new InMemoryUserDatabase();
        userApi = new UserApi(userDatabase);
        user = new User.Builder()
                .name("박재성")
                .email("javajigi@slipp.net")
                .userId("javajigi")
                .password("password")
                .build();
        userDatabase.save(user);
    }

    @AfterEach
    void tearDown() {
        userDatabase.remove(user.getUserId());
    }

    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader("userId=java&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")), 113));

        HttpResponse response = userApi.create(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );

        userDatabase.remove("java");
    }

    @Test
    @DisplayName("유저 생성 테스트 - 중복 회원 가입은 막는다.")
    void createUser_duplicate() throws IOException {
        String body = "userId=javajigi&password=test&name=test&email=test@test.com";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));

        assertThatThrownBy(() -> userApi.create(httpRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("유저 등록 페이지 테스트 - 로그인 상태")
    void registrationPage() throws IOException {
        HttpRequest httpRequest = loginRequest();

        HttpResponse response = userApi.registrationPage(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );
    }

    @Test
    @DisplayName("유저 등록 페이지 테스트 - 비로그인 상태")
    void registrationPage_noLogin() throws IOException {
        HttpRequest httpRequest = noLoginRequest();

        HttpResponse response = userApi.registrationPage(httpRequest);

        assertAll(() -> assertTrue(response.hasBody()),
                () -> assertTrue(response.toString().contains("OK")),
                () -> assertTrue(response.toString().contains("200")),
                () -> assertTrue(response.toString().contains("Content-Type: text/html"))
        );
    }

    @Test
    @DisplayName("로그인 페이지 테스트 - 로그인 상태")
    void loginPage() throws IOException {
        HttpRequest httpRequest = loginRequest();

        HttpResponse response = userApi.loginPage(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );
    }

    @Test
    @DisplayName("로그인 페이지 테스트 - 비로그인 상태")
    void loginPage_noLogin() throws IOException {
        HttpRequest httpRequest = noLoginRequest();

        HttpResponse response = userApi.loginPage(httpRequest);

        assertAll(() -> assertTrue(response.hasBody()),
                () -> assertTrue(response.toString().contains("OK")),
                () -> assertTrue(response.toString().contains("200")),
                () -> assertTrue(response.toString().contains("Content-Type: text/html"))
        );
    }

    @Test
    @DisplayName("로그인 테스트 - 로그인 성공")
    void login() throws IOException {
        String body = "userId=test&password=test";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /login HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));

        HttpResponse response = userApi.login(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );
    }

    @Test
    @DisplayName("로그인 테스트 - 로그인 실패")
    void login_fail() throws IOException {
        String body = "userId=test&password=wrong";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /login HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));

        HttpResponse response = userApi.login(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /login"))
        );
    }

    @Test
    @DisplayName("로그인 테스트 - 로그인 상태")
    void login_alreadyLogin() throws IOException {
        HttpRequest httpRequest = loginRequest();

        HttpResponse response = userApi.login(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );
    }


    @Test
    @DisplayName("로그아웃 테스트 - 로그인 상태")
    void logout() throws IOException {
        HttpRequest httpRequest = loginRequest();
        String session = SessionManager.createSession(user, HttpResponse.ok());

        HttpResponse response = userApi.logout(httpRequest);

        assertAll(
                () -> assertTrue(response.toString().contains("Found")),
                () -> assertTrue(response.toString().contains("302")),
                () -> assertTrue(response.toString().contains("Location: /"))
        );
    }

    @Test
    @DisplayName("유저 리스트 테스트 - 로그인 상태")
    void userList() throws IOException {
        HttpRequest httpRequest = loginRequest();
        String session = SessionManager.createSession(user, HttpResponse.ok());

        ModelAndView userList = userApi.getUserList(httpRequest);

        assertAll(
                () -> assertTrue(userList.getViewName().equalsIgnoreCase("/user/userList.html")),
                () -> assertTrue(userList.containsAttribute("users"))
        );
    }

    private HttpRequest loginRequest() throws IOException {
        final String getMessage = "GET /main HTTP/1.1" + NEW_LINE +
                "Host: localhost:8080" + NEW_LINE +
                "Connection: keep-alive" + NEW_LINE +
                "Accept: */*" + NEW_LINE +
                "Accept-Encoding: gzip, deflate, br" + NEW_LINE +
                "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7" + NEW_LINE;
        String session = SessionManager.createSession(user, HttpResponse.ok());
        String loginMessage = getMessage + "Cookie: SID=" + session + NEW_LINE;
        return HttpRequest.from(new BufferedReader(new StringReader(loginMessage)));
    }

    private HttpRequest noLoginRequest() throws IOException {
        final String getMessage = "GET /main HTTP/1.1" + NEW_LINE +
                "Host: localhost:8080" + NEW_LINE +
                "Connection: keep-alive" + NEW_LINE +
                "Accept: */*" + NEW_LINE +
                "Accept-Encoding: gzip, deflate, br" + NEW_LINE +
                "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7" + NEW_LINE;
        return HttpRequest.from(new BufferedReader(new StringReader(getMessage)));
    }

}
