package codesquad.http.message.request;

import codesquad.http.message.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import static codesquad.http.message.constant.HttpHeader.CONTENT_LENGTH;

public class HttpRequest {

    private final RequestStartLine requestStartLine;
    private final HttpHeaders httpHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestStartLine requestStartLine, final HttpHeaders httpHeaders,
                       final RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        RequestStartLine requestStartLine = RequestStartLine.from(reader);
        HttpHeaders httpHeaders = HttpHeaders.from(reader);
        String length = Optional.ofNullable(httpHeaders.getHeader(CONTENT_LENGTH.getHeaderName())).orElse("0");
        int contentLength = Integer.parseInt(length);

        if (contentLength == 0) {
            return new HttpRequest(requestStartLine, httpHeaders, null);
        }

        RequestBody requestBody = RequestBody.from(reader, contentLength);

        return new HttpRequest(requestStartLine, httpHeaders, requestBody);
    }

    public RequestStartLine getRequestStartLine() {
        return requestStartLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestStartLine=" + requestStartLine +
                ", httpHeaders=" + httpHeaders +
                ", requestBody=" + requestBody +
                '}';
    }
}
