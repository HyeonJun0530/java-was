package codesquad.http.handler;

import codesquad.app.infrastructure.UserDatabase;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestHandlerTest {

    List<HttpRequestHandler> handlerList = List.of(new ApiHandler(), new StaticHandler());

    private static HttpResponse getHttpResponse(final Object response) {
        assertInstanceOf(HttpResponse.class, response);

        HttpResponse httpResponse = (HttpResponse) response;
        return httpResponse;
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - static 파일 요청이 성공적으로 처리되는 경우")
    void static_handle_success() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /favicon.ico HTTP/1.1"))), null, null);
        Object response = handlerList.get(1).handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - 어디에서도 처리 할 수 없는 경우 - NOT_FOUND")
    void handle_fail() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /no-exit HTTP/1.1"))), null, null);
        Object response = handlerList.get(1).handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.NOT_FOUND.getReasonPhrase()),
                () -> assertThat(httpResponse.toString()).contains(String.valueOf(HttpStatus.NOT_FOUND.value()))
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 없어서 static 핸들러로 처리되는 경우")
    void handle_static() throws IOException {
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("GET /favicon.ico HTTP/1.1"))), null, null);
        Optional<HttpRequestHandler> supportHandler = handlerList.stream()
                .filter(handler -> handler.isSupport(httpRequest))
                .findFirst();
        assertTrue(supportHandler.isPresent());
        assertInstanceOf(StaticHandler.class, supportHandler.get());

        Object response = supportHandler.get().handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isTrue(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.OK.getReasonPhrase())
        );
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 있어서 성공적으로 HttpResponse 타입으로 처리되는 경우")
    void api_handle_success() throws IOException {
        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));
        Object response = handlerList.get(0).handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase()));

        UserDatabase.remove("javajigi");
    }

    @Test
    @DisplayName("HttpRequestHandlerTest 테스트 - api 핸들러에 api가 있어서 api 핸들러로 처리되는 경우")
    void handle_api() throws IOException {
        String body = "userId=javajigi&password=password&name=%EB%B0%95%EC%9E%AC%EC%84%B1&email=javajigi%40slipp.net";
        HttpRequest httpRequest = new HttpRequest(RequestStartLine.from(new BufferedReader(new StringReader("POST /create HTTP/1.1"))), null,
                RequestBody.from(new BufferedReader(new StringReader(body)), body.getBytes().length));
        Optional<HttpRequestHandler> supportHandler = handlerList.stream()
                .filter(handler -> handler.isSupport(httpRequest))
                .findFirst();
        assertTrue(supportHandler.isPresent());
        assertInstanceOf(ApiHandler.class, supportHandler.get());

        Object response = supportHandler.get().handle(httpRequest);

        HttpResponse httpResponse = getHttpResponse(response);

        UserDatabase.remove("javajigi");

        assertAll(() -> assertThat(httpResponse.hasBody()).isFalse(),
                () -> assertThat(httpResponse.toString()).containsIgnoringCase(HttpStatus.FOUND.getReasonPhrase())
        );
    }

}
