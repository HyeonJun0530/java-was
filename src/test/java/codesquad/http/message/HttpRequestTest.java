package codesquad.http.message;

import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 없는 경우")
    void from() {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";

        HttpRequest from = HttpRequest.from(input);

        assertThat(from.getRequestStartLine().getMethod()).isNotNull();
        assertThat(from.getRequestStartLine().getPath()).isNotNull();
        assertThat(from.getRequestStartLine().getProtocol()).isNotNull();
        assertThat(from.getHttpHeaders()).isNotNull();
    }

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트 - body가 있는 경우")
    void from_body() {
        String input = "POST /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" + "Hello, World!";

        HttpRequest from = HttpRequest.from(input);

        assertThat(from.getRequestStartLine().getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(from.getRequestStartLine().getPath()).isEqualTo("/index.html");
        assertThat(from.getRequestStartLine().getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(from.getHttpHeaders()).isNotNull();
        assertThat(from.getRequestBody().toString()).contains("Hello, World!");
    }

}
