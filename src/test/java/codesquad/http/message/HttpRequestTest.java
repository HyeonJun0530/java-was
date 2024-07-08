package codesquad.http.message;

import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 없는 경우")
    void from() throws IOException {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n";

        HttpRequest from = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(from.getRequestStartLine().getMethod()).isNotNull();
        assertThat(from.getRequestStartLine().getPath()).isNotNull();
        assertThat(from.getRequestStartLine().getProtocol()).isNotNull();
        assertThat(from.getHttpHeaders()).isNotNull();
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우")
    void from_body() throws IOException {
        String input = "POST /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 13\r\n" +
                "\r\n" + "Hello, World!";

        HttpRequest from = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(from.getRequestStartLine().getPath()).isEqualTo("/index.html");
        assertThat(from.getRequestStartLine().getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(from.getHttpHeaders()).isNotNull();
        assertThat(from.getRequestBody().toString()).contains("Hello, World!");
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우, body 디코딩 확인")
    void from_body_decode() throws IOException {
        String input = "POST /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: 113\r\n" +
                "\r\n" + "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";

        HttpRequest from = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(from.getRequestStartLine().getPath()).isEqualTo("/index.html");
        assertThat(from.getRequestStartLine().getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(from.getHttpHeaders()).isNotNull();
        assertThat(from.getRequestBody().toString()).contains("재성");
    }

}
