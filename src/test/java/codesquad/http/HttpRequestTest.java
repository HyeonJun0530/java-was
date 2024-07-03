package codesquad.http;

import codesquad.http.message.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트")
    void from() {
        try (BufferedReader br = new BufferedReader(new StringReader("GET /index.html HTTP/1.1"))) {
            HttpRequest httpRequest = HttpRequest.from(br);
            assertEquals("GET", httpRequest.getMethod());
            assertEquals("/index.html", httpRequest.getPath());
            assertEquals("HTTP/1.1", httpRequest.getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
