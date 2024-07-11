package codesquad.app.api;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static codesquad.utils.StringUtils.NEW_LINE;
import static org.junit.jupiter.api.Assertions.*;

class MainApiTest {

    final String getMessage = "GET /main HTTP/1.1" + NEW_LINE +
            "Host: localhost:8080" + NEW_LINE +
            "Connection: keep-alive" + NEW_LINE +
            "Accept: */*" + NEW_LINE +
            "Accept-Encoding: gzip, deflate, br" + NEW_LINE +
            "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7" + NEW_LINE;

    MainApi mainApi = new MainApi();
    User user;

    @BeforeEach
    void setUp() {
        user = new User.Builder()
                .userId("test")
                .password("test")
                .name("test")
                .email("test@test.com")
                .build();
        UserDatabase.save(user);
    }

    @AfterEach
    void tearDown() {
        UserDatabase.remove("test");
    }

    @Test
    @DisplayName("MainApi 인스턴스를 생성한다.")
    void createMainApi() {
        MainApi mainApi = new MainApi();

        assertNotNull(mainApi);
    }

    @Test
    @DisplayName("/main 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 X")
    void main() throws IOException {

        HttpResponse noLogin = mainApi.main(noLoginRequest());

        assertAll(() -> assertTrue(noLogin.hasBody()),
                () -> assertTrue(noLogin.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(noLogin.toString().contains("로그인"))
        );
    }

    @Test
    @DisplayName("/main 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 상태")
    void main_login() throws IOException {

        HttpResponse login = mainApi.main(loginRequest());

        assertAll(() -> assertTrue(login.hasBody()),
                () -> assertTrue(login.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(login.toString().contains("글쓰기")),
                () -> assertTrue(login.toString().contains("로그아웃"))
        );
    }

    @Test
    @DisplayName("/ 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 X")
    void root() throws IOException {
        HttpResponse noLogin = mainApi.root(noLoginRequest());

        assertAll(() -> assertTrue(noLogin.hasBody()),
                () -> assertTrue(noLogin.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(noLogin.toString().contains("로그인"))
        );
    }

    @Test
    @DisplayName("/ 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 상태")
    void root_login() throws IOException {
        MainApi mainApi = new MainApi();

        HttpRequest httpRequest = loginRequest();
        HttpResponse login = mainApi.root(httpRequest);

        assertAll(() -> assertTrue(login.hasBody()),
                () -> assertTrue(login.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(login.toString().contains("글쓰기")),
                () -> assertTrue(login.toString().contains("로그아웃"))
        );
    }

    private HttpRequest loginRequest() throws IOException {
        String session = SessionManager.createSession(user.getUserId(), HttpResponse.ok());
        String loginMessage = getMessage + "Cookie: SID=" + session + NEW_LINE;
        return HttpRequest.from(new BufferedReader(new StringReader(loginMessage)));
    }

    private HttpRequest noLoginRequest() throws IOException {
        return HttpRequest.from(new BufferedReader(new StringReader(getMessage)));
    }

}
