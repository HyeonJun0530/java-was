package codesquad.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpHeaderTest {

    private static final Logger log = LoggerFactory.getLogger(HttpHeaderTest.class);

    @Test
    @DisplayName("HttpHeader 객체를 생성한다. - error")
    void error() {
        HttpHeader httpHeader = HttpHeader.error();
        log.debug(httpHeader.toString());

        assertNotNull(httpHeader);
        assertTrue(httpHeader.toString().contains("application/json"));
    }

    @Test
    void from() {
        String input = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n";

        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            HttpHeader httpHeader = HttpHeader.from(reader);
            log.debug(httpHeader.toString());

            assertNotNull(httpHeader);
            assertTrue(httpHeader.toString().contains("text/html"));
            assertThat(httpHeader.toString()).contains("host:www.example.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("HttpStatus와 File body를 받아 HttpHeader를 생성한다.")
    void of() {
        HttpStatus httpStatus = HttpStatus.OK;
        File file = new File("src/main/resources/static/index.html");
        HttpHeader httpHeader = HttpHeader.of(httpStatus, file);
        log.debug(httpHeader.toString());

        assertNotNull(httpHeader);
        assertTrue(httpHeader.toString().contains("text/html"));
    }

    @Test
    @DisplayName("HttpStatus와 body를 받아 HttpHeader를 생성한다. - 지원하지 않는 타입")
    void of_fail() {
        HttpStatus httpStatus = HttpStatus.OK;
        int body = 1;

        assertThatThrownBy(() -> HttpHeader.of(httpStatus, body))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
