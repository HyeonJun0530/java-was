package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeadersTest {

    private static final Logger log = LoggerFactory.getLogger(HttpHeadersTest.class);

    @Test
    @DisplayName("HttpHeader 객체를 생성한다. - 디폴트 헤더 생성 확인")
    void newInstant() {
        HttpHeaders httpHeaders = HttpHeaders.newInstance();
        log.debug(httpHeaders.toString());

        assertAll(() -> assertTrue(httpHeaders.toString().contains("Content-Length: 0")),
                () -> assertTrue(httpHeaders.toString().contains("Server: Hyn_053 Server")),
                () -> assertTrue(httpHeaders.toString().contains("Date: ")));
    }

    @Test
    void from() throws IOException {
        String input = "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 1024\r\n" +
                "\r\n";


        HttpHeaders httpHeaders = HttpHeaders.from(new BufferedReader(new StringReader(input)));
        log.debug(httpHeaders.toString());

        assertNotNull(httpHeaders);
        assertAll(() -> assertTrue(httpHeaders.toString().contains("Content-Type: text/html")),
                () -> assertTrue(httpHeaders.toString().contains("Content-Length: 1024")),
                () -> assertTrue(httpHeaders.toString().contains("Host: www.example.com")));
    }

    @Test
    @DisplayName("body를 받아 HttpHeader를 생성한다.")
    void of() throws IOException {
        HttpStatus httpStatus = HttpStatus.OK;
        ClassLoader cl = getClass().getClassLoader();
        InputStream resourceAsStream = cl.getResourceAsStream("static/index.html");

        byte[] bytes = resourceAsStream.readAllBytes();
        ResponseBody responseBody = ResponseBody.from(bytes);
        HttpHeaders httpHeaders = HttpHeaders.of(ContentType.TEXT_HTML, responseBody);
        log.debug(httpHeaders.toString());

        assertNotNull(httpHeaders);
        assertAll(() -> assertTrue(httpHeaders.toString().contains("text/html")),
                () -> assertTrue(httpHeaders.toString().contains("Content-Length: " + responseBody.getBytes().length)));
    }

    @Test
    @DisplayName("쿠키를 파싱해 List<Cookie>로 반환한다.")
    void getCookies() throws IOException {
        String input = "Host: www.example.com\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n" +
                "Cookie: name=hyeon\r\n\r\n";
        HttpHeaders httpHeaders = HttpHeaders.from(new BufferedReader(new StringReader(input)));
        List<Cookie> cookies = httpHeaders.getCookies();

        assertAll(() -> assertEquals(1, cookies.size()),
                () -> assertEquals("name", cookies.get(0).getName()),
                () -> assertEquals("hyeon", cookies.get(0).getValue()));
    }

}
