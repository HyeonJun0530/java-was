package codesquad.http.handler;

import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestBody;
import codesquad.http.message.request.RequestStartLine;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestHandlerTest {

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 있어서 성공적으로 처리되는 경우")
    void api_handle_success() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader("userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")), 113));
        HttpResponse response = ApiHandler.handle(httpRequest);

        assertAll(() -> assertThat(response.hasBody()).isFalse(),
                () -> assertThat(response.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase()));
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - static 파일 요청이 성공적으로 처리되는 경우")
    void static_handle_success() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET / HTTP/1.1"))), null, null);
        HttpResponse httpResponse = StaticHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - 어디에서도 처리 할 수 없는 경우 - NOT_FOUND")
    void handle_fail() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /no-exit HTTP/1.1"))), null, null);
        HttpResponse httpResponse = StaticHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.NOT_FOUND.getReasonPhrase()),
                () -> assertThat(httpResponse.toString()).contains(String.valueOf(HttpStatus.NOT_FOUND.value()))
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 없어서 static 핸들러로 처리되는 경우")
    void handle_static() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET / HTTP/1.1"))), null, null);
        HttpResponse httpResponse = HttpRequestHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 있어서 api 핸들러로 처리되는 경우")
    void handle_api() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader("userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net")), 113));
        HttpResponse httpResponse = HttpRequestHandler.handle(httpRequest);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase())
        );
    }

}
