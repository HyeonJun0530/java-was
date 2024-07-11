package codesquad.http.handler;

import codesquad.app.infrastructure.UserDatabase;
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
    }

    private static HttpResponse convert(final Object response) {
        assertInstanceOf(HttpResponse.class, response);

        return (HttpResponse) response;
    }

    @Test
    @DisplayName("api 핸들러에 api가 있어서 성공적으로 처리되는 경우")
    public void api_handle_success() throws IOException {
        UserDatabase.remove("javajigi");
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader("userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")), 113));
        Object response = apiHandler.handle(httpRequest);

        HttpResponse httpResponse = convert(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(response.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase()));
    }

    @Test
    @DisplayName("api 핸들러에 api가 없어서 실패하는 경우 - NOT_FOUND")
    void api_handle_fail() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /notfound HTTP/1.1"))), null, null);
        Object response = apiHandler.handle(httpRequest);

        HttpResponse httpResponse = convert(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(response.toString()).containsIgnoringCase(HttpStatus.NOT_FOUND.getReasonPhrase()),
                () -> assertThat(response.toString()).contains(String.valueOf(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    @DisplayName("api 핸들러 path는 맞는데 메서드가 다른 경우 - METHOD_NOT_ALLOWED")
    void api_handle_not_allowed() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /create HTTP/1.1"))), null, null);
        Object response = apiHandler.handle(httpRequest);

        HttpResponse httpResponse = convert(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase()));
    }

    @Test
    @DisplayName("api 핸들러 - 같은 url에 대해서 GET, POST 모두 처리 가능한 경우")
    void api() throws IOException {
        String getMessage = "GET /login HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n";

        String postMessage = "POST /login HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 33\r\n" +
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
        HttpRequest success = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader("userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")), 113));
        HttpRequest fail = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /global.css HTTP/1.1"))), null, null);

        assertAll(() -> assertThat(apiHandler.isSupport(success)).isTrue(),
                () -> assertThat(apiHandler.isSupport(fail)).isFalse());
    }
}


