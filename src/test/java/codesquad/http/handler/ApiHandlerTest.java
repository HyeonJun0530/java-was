package codesquad.http.handler;

import codesquad.app.domain.Article;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.exception.MethodNotAllowedException;
import codesquad.http.exception.NotFoundException;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestBody;
import codesquad.http.message.request.RequestStartLine;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ApiHandlerTest {

    ApiHandler apiHandler = new ApiHandler();

    @BeforeEach
    void setUp() {
        UserDatabase.save(new codesquad.app.domain.User.Builder()
                .name("박재성")
                .email("javajigi@slipp.net")
                .userId("javajigi")
                .password("password")
                .build());
    }

    @AfterEach
    void tearDown() {
        UserDatabase.remove("javajigi");
        InMemoryArticleDatabase.remove(1L);
    }

    private static HttpResponse convert(final Object response) {
        assertInstanceOf(HttpResponse.class, response);

        return (HttpResponse) response;
    }

    @Test
    @DisplayName("api 핸들러에 api가 있어서 성공적으로 처리되는 경우")
    public void api_handle_success() throws IOException {
        UserDatabase.remove("javajigi");
        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));
        Object response = apiHandler.handle(httpRequest);

        HttpResponse httpResponse = convert(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(response.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase()));
    }

    @Test
    @DisplayName("api 핸들러에 api가 없어서 실패하는 경우 - NOT_FOUND")
    void api_handle_fail() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /notfound HTTP/1.1"))), null, null);
        assertThatThrownBy(() -> apiHandler.handle(httpRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("api 핸들러 path는 맞는데 메서드가 다른 경우 - METHOD_NOT_ALLOWED")
    void api_handle_not_allowed() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /create HTTP/1.1"))), null, null);
        assertThatThrownBy(() -> apiHandler.handle(httpRequest))
                .isInstanceOf(MethodNotAllowedException.class);
    }

    @Test
    @DisplayName("api 핸들러 - 같은 url에 대해서 GET, POST 모두 처리 가능한 경우")
    void api() throws IOException {
        String getMessage = "GET /login HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n";

        int length = "userId=javajigi&password=password".getBytes().length;

        String postMessage = "POST /login HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "\r\n" +
                "userId=javajigi&password=password";

        HttpRequest get = HttpRequest.from(new BufferedReader(new StringReader(getMessage)));
        HttpRequest post = HttpRequest.from(new BufferedReader(new StringReader(postMessage)));

        assertThat(apiHandler.handle(get).toString()).contains(HttpStatus.OK.getReasonPhrase());
        assertThat(apiHandler.handle(post).toString()).contains(HttpStatus.FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("api 핸들러에서 처리 유무를 반환")
    void is_api_request() throws IOException {
        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        HttpRequest success = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));
        HttpRequest fail = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /global.css HTTP/1.1"))), null, null);

        assertAll(() -> assertThat(apiHandler.isSupport(success)).isTrue(),
                () -> assertThat(apiHandler.isSupport(fail)).isFalse());
    }

    @Test
    @DisplayName("api 핸들러에서 처리 유무를 반환 - pathVariable이 있는 경우")
    void is_api_request_with_path_variable() throws IOException {
        String body = "title=title&content=hello";
        HttpRequest success = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /1 HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));

        assertAll(() -> assertThat(apiHandler.isSupport(success)).isTrue());
    }

    @Test
    @DisplayName("api 핸들러에서 처리 유무를 반환 - pathVariable이 있는 경우")
    void api_request_with_path_variable() throws IOException {
        String session = SessionManager.createSession("javajigi", HttpResponse.ok());

        Article article = InMemoryArticleDatabase.save(new Article.Builder()
                .sequence(1L)
                .title("title")
                .content("content")
                .writer(UserDatabase.findByUserId("javajigi").get())
                .build());

        String message = "POST /1/comment HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 31\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Referer: http://localhost:8080/1\r\n" +
                "Cookie: SID=" + session + "\r\n" +
                "\r\n" +
                "writer=writer&contents=contents";

        HttpRequest success = HttpRequest.from(new BufferedReader(new StringReader(message)));

        assertAll(() -> assertThat(apiHandler.handle(success)).isNotNull(),
                () -> assertThat(apiHandler.handle(success)).isInstanceOf(HttpResponse.class));
    }
}


