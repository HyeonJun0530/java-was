package codesquad.http;

import codesquad.http.message.constant.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HttpStatusTest {

    private static Stream<Arguments> provideHttpStatus() {
        return Stream.of(
                Arguments.of(200, HttpStatus.Series.SUCCESSFUL, "OK"),
                Arguments.of(201, HttpStatus.Series.SUCCESSFUL, "Created"),
                Arguments.of(400, HttpStatus.Series.CLIENT_ERROR, "Bad Request"),
                Arguments.of(401, HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),
                Arguments.of(403, HttpStatus.Series.CLIENT_ERROR, "Forbidden"),
                Arguments.of(404, HttpStatus.Series.CLIENT_ERROR, "Not Found"),
                Arguments.of(500, HttpStatus.Series.SERVER_ERROR, "Internal Server Error"),
                Arguments.of(501, HttpStatus.Series.SERVER_ERROR, "Not Implemented"),
                Arguments.of(503, HttpStatus.Series.SERVER_ERROR, "Service Unavailable")
        );
    }

    @ParameterizedTest
    @MethodSource("provideHttpStatus")
    @DisplayName("HttpStatus의 valueOf 메서드는 주어진 상태 코드에 해당하는 HttpStatus를 반환한다.")
    void valueOf(int statusCode, HttpStatus.Series series, String reasonPhrase) {
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        assertEquals(statusCode, httpStatus.value());
        assertEquals(series, httpStatus.series());
        assertEquals(reasonPhrase, httpStatus.getReasonPhrase());
    }

    @ParameterizedTest
    @MethodSource("provideHttpStatus")
    @DisplayName("숫자 번호 코드가 어떤 응답인지 확인한다.")
    void series_resolve(int statusCode, HttpStatus.Series series) {
        HttpStatus.Series resolve = HttpStatus.Series.resolve(statusCode);
        assertEquals(series, resolve);
    }

    @Test
    void is1xxInformational() {
        assertFalse(HttpStatus.OK.is1xxInformational());
        assertFalse(HttpStatus.NOT_FOUND.is1xxInformational());
        assertFalse(HttpStatus.INTERNAL_SERVER_ERROR.is1xxInformational());
        assertFalse(HttpStatus.MOVED_PERMANENTLY.is1xxInformational());
    }

    @Test
    void is2xxSuccessful() {
        assertTrue(HttpStatus.OK.is2xxSuccessful());
        assertFalse(HttpStatus.NOT_FOUND.is2xxSuccessful());
        assertFalse(HttpStatus.INTERNAL_SERVER_ERROR.is2xxSuccessful());
        assertFalse(HttpStatus.MOVED_PERMANENTLY.is2xxSuccessful());
    }

    @Test
    void is3xxRedirection() {
        assertFalse(HttpStatus.OK.is3xxRedirection());
        assertFalse(HttpStatus.NOT_FOUND.is3xxRedirection());
        assertFalse(HttpStatus.INTERNAL_SERVER_ERROR.is3xxRedirection());
        assertTrue(HttpStatus.MOVED_PERMANENTLY.is3xxRedirection());
    }

    @Test
    void is4xxClientError() {
        assertFalse(HttpStatus.OK.is4xxClientError());
        assertTrue(HttpStatus.NOT_FOUND.is4xxClientError());
        assertFalse(HttpStatus.INTERNAL_SERVER_ERROR.is4xxClientError());
        assertFalse(HttpStatus.MOVED_PERMANENTLY.is4xxClientError());
    }

    @Test
    void is5xxServerError() {
        assertFalse(HttpStatus.OK.is5xxServerError());
        assertFalse(HttpStatus.NOT_FOUND.is5xxServerError());
        assertTrue(HttpStatus.INTERNAL_SERVER_ERROR.is5xxServerError());
        assertFalse(HttpStatus.MOVED_PERMANENTLY.is5xxServerError());
    }
}
