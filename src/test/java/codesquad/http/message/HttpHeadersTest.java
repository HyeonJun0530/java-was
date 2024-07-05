package codesquad.http.message;

import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeadersTest {

    private static final Logger log = LoggerFactory.getLogger(HttpHeadersTest.class);

    @Test
    @DisplayName("HttpHeader 객체를 생성한다. - 디폴트 헤더 생성 확인")
    void newInstant() {
        HttpHeaders httpHeaders = HttpHeaders.newInstance();
        log.debug(httpHeaders.toString());

        assertAll(() -> assertTrue(httpHeaders.toString().contains("Content-Length:0")),
                () -> assertTrue(httpHeaders.toString().contains("Server:Hyn_053 Server")),
                () -> assertTrue(httpHeaders.toString().contains("Date:")));
    }

    @Test
    void from() {
        String input = "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 1024\r\n" +
                "\r\n";


        HttpHeaders httpHeaders = HttpHeaders.from(input);
        log.debug(httpHeaders.toString());

        assertNotNull(httpHeaders);
        assertAll(() -> assertTrue(httpHeaders.toString().contains("Content-Type:text/html")),
                () -> assertTrue(httpHeaders.toString().contains("Content-Length:1024")),
                () -> assertTrue(httpHeaders.toString().contains("Host:www.example.com")));
    }

    @Test
    @DisplayName("body를 받아 HttpHeader를 생성한다.")
    void of() {
        HttpStatus httpStatus = HttpStatus.OK;
        File file = new File("src/main/resources/static/index.html");
        ResponseBody responseBody = ResponseBody.from(file);
        HttpHeaders httpHeaders = HttpHeaders.of(responseBody);
        log.debug(httpHeaders.toString());

        assertNotNull(httpHeaders);
        assertAll(() -> assertTrue(httpHeaders.toString().contains("text/html")),
                () -> assertTrue(httpHeaders.toString().contains("Content-Length:" + responseBody.getBytes().length)));
    }
}
