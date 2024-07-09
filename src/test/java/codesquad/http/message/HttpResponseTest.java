package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - redirect인 경우")
    void of_redirect() {
        HttpResponse httpResponse = HttpResponse.redirect("HTTP/1.1", HttpStatus.MOVED_PERMANENTLY, "/index.html");

        assertAll(() -> assertFalse(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("Location:/index.html")));
    }

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - body가 없는 경우")
    void of_ok() {
        HttpResponse httpResponse = HttpResponse.of("HTTP/1.1", HttpStatus.OK);

        assertAll(() -> assertFalse(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("200 OK")));
    }

    @Test
    @DisplayName("HttpResponse.of() 메서드를 통해 HttpResponse 객체를 생성할 수 있다. - body가 있는 경우")
    void of_ok_with_body() {
        HttpResponse httpResponse = HttpResponse.of(ContentType.APPLICATION_JSON, "HTTP/1.1", HttpStatus.OK,
                "Hello, World!");

        assertAll(() -> assertTrue(httpResponse.hasBody()),
                () -> assertTrue(httpResponse.toString().contains("200 OK")),
                () -> assertTrue(httpResponse.toString().contains("Hello, World!")));
    }

}
