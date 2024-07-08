package codesquad.http.handler;

import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestStartLine;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaticHandlerTest {

    @Test
    @DisplayName("정적 파일 요청이 성공적으로 처리되는 경우")
    void handle_success() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET / HTTP/1.1"))), null, null);
        HttpResponse httpResponse = StaticHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("정적 파일 요청이 실패하는 경우 - NOT_FOUND")
    void handle_fail() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /no-exit HTTP/1.1"))), null, null);
        HttpResponse httpResponse = StaticHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.NOT_FOUND.getReasonPhrase()),
                () -> assertThat(httpResponse.toString()).contains(String.valueOf(HttpStatus.NOT_FOUND.value()))
        );
    }

}
