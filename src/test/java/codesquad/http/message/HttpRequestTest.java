package codesquad.http.message;

import codesquad.http.exception.BadRequestException;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpRequestTest {

    private static HttpRequest getHttpRequest(final String input) throws IOException {
        HttpRequest from = HttpRequest.from(new BufferedReader(new StringReader(input)));
        return from;
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 없는 경우")
    void from() throws IOException {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n";

        HttpRequest from = getHttpRequest(input);

        assertAll(() -> assertThat(from.getRequestStartLine().getMethod()).isNotNull(),
                () -> assertThat(from.getRequestStartLine().getPath()).isNotNull(),
                () -> assertThat(from.getRequestStartLine().getProtocol()).isNotNull(),
                () -> assertThat(from.getHttpHeaders()).isNotNull(),
                () -> assertThat(from.getRequestBody()).isNull(),
                () -> assertThat(from.getHttpHeaders().getHeader("Host")).isEqualTo("www.example.com"));
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 없는 경우")
    void from_fail() throws IOException {
        String input = "GET HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n";

        Assertions.assertThatThrownBy(() -> getHttpRequest(input))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우")
    void from_body() throws IOException {
        String input = "POST /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + "Hello, World!".getBytes().length + "\r\n" +
                "\r\n" + "Hello, World!";

        HttpRequest from = getHttpRequest(input);

        assertAll(() -> assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(from.getRequestStartLine().getPath()).isEqualTo("/index.html"),
                () -> assertThat(from.getRequestStartLine().getProtocol()).isEqualTo("HTTP/1.1"),
                () -> assertThat(from.getHttpHeaders()).isNotNull(),
                () -> assertThat(from.getRequestBody().toString()).contains("Hello, World!"));
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우, body 디코딩 확인")
    void from_body_decode() throws IOException {

        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        String input = "POST /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: " + body.getBytes().length + "\r\n" +
                "\r\n" + body;

        HttpRequest from = getHttpRequest(input);

        assertAll(() -> assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(from.getRequestStartLine().getPath()).isEqualTo("/index.html"),
                () -> assertThat(from.getRequestStartLine().getProtocol()).isEqualTo("HTTP/1.1"),
                () -> assertThat(from.getHttpHeaders()).isNotNull(),
                () -> assertThat(from.getRequestBody().toString()).contains("javajigi"),
                () -> assertThat(from.getRequestBody().toString()).contains("password"),
                () -> assertThat(from.getRequestBody().toString()).contains("박재성"));
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우, 쿼리 스트링이 있는 경우")
    void create_all() throws IOException {
        String input = "GET /create?userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n";

        HttpRequest from = getHttpRequest(input);

        assertAll(() -> assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(from.getRequestStartLine().getQueryString().get("userId")).isEqualTo("javajigi"),
                () -> assertThat(from.getRequestStartLine().getQueryString().get("password")).isEqualTo("password"),
                () -> assertThat(from.getRequestStartLine().getQueryString().get("name")).isEqualTo("박재성"),
                () -> assertThat(from.getRequestStartLine().getQueryString().get("email")).isEqualTo("javajigi@slipp.net"));
    }

    @Test
    @DisplayName("HttpRequest 객체에서 쿠키를 가져올 수 있다.")
    void getCookies() throws IOException {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n" +
                "Cookie: SID=1234; remember=true\r\n";

        HttpRequest from = getHttpRequest(input);

        assertAll(() -> assertThat(from.getCookies().get(0).getName()).isEqualTo("SID"),
                () -> assertThat(from.getCookies().get(0).getValue()).isEqualTo("1234"),
                () -> assertThat(from.getCookies().get(1).getName()).isEqualTo("remember"),
                () -> assertThat(from.getCookies().get(1).getValue()).isEqualTo("true"));
    }

    @Test
    @DisplayName("HttpRequest 쿠키가 없으면 빈 배열을 반환한다.")
    void getCookies_null() throws IOException {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n";

        HttpRequest from = getHttpRequest(input);

        assertTrue(from.getCookies().isEmpty());
    }

}
