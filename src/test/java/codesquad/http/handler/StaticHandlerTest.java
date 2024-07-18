package codesquad.http.handler;

import codesquad.http.exception.NotFoundException;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestStartLine;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class StaticHandlerTest {

    private StaticHandler staticHandler = new StaticHandler();

    private static HttpResponse getHttpResponse(final Object response) {
        assertInstanceOf(HttpResponse.class, response);

        HttpResponse httpResponse = (HttpResponse) response;
        return httpResponse;
    }

    @Test
    @DisplayName("정적 파일 요청이 성공적으로 처리되는 경우")
    void handle_success() throws IOException {
        String input = "GET /favicon.ico HTTP/1.1";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new ByteArrayInputStream(input.getBytes())), null, null);
        Object response = staticHandler.handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("정적 파일 요청이 실패하는 경우 - NOT_FOUND")
    void handle_fail() throws IOException {
        String input = "GET /no-exit HTTP/1.1";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new ByteArrayInputStream(input.getBytes())), null, null);
        assertThatThrownBy(() -> staticHandler.handle(httpRequest))
                .isInstanceOf(NotFoundException.class);
    }

}
