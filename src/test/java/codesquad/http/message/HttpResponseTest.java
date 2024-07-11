package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - redirect인 경우")
    void of_redirect() {
        HttpResponse httpResponse = HttpResponse.redirect(HttpStatus.FOUND, "/index.html");

        assertAll(() -> assertFalse(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("302 Found")),
                () -> assertTrue(httpResponse.toString().contains("Location: /index.html")));
    }

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - body가 없는 경우")
    void of_ok() {
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);

        assertAll(() -> assertFalse(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("Content-Length: 0")),
                () -> assertTrue(httpResponse.toString().contains("200 OK")));
    }

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - body가 있는 경우")
    void of_ok_with_body() throws UnsupportedEncodingException {
        HttpResponse httpResponse = HttpResponse.of(ContentType.TEXT_PLAIN, HttpStatus.OK,
                "Hello, World!".getBytes(DECODING_CHARSET));

        assertAll(() -> assertTrue(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("Content-Type: text/plain")),
                () -> assertTrue(httpResponse.toString().contains("Content-Length: 13")),
                () -> assertTrue(httpResponse.toString().contains("200 OK")),
                () -> assertTrue(httpResponse.toString().contains("Hello, World!")));
    }

    @Test
    @DisplayName("HttpResponse.setCookie() 메서드를 통해 Cookie를 추가할 수 있다.")
    void setCookie() {
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        httpResponse.setCookie(new Cookie("name", "value"));

        assertAll(() -> assertTrue(httpResponse.toString().contains("Set-Cookie: name=value")),
                () -> assertTrue(httpResponse.toString().contains("200 OK")));
    }

}
