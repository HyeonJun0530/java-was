package codesquad.http.message.request;

import codesquad.http.message.Cookie;
import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static codesquad.http.message.constant.HttpHeader.CONTENT_LENGTH;
import static codesquad.utils.StringUtils.NEW_LINE;

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

    public List<Cookie> getCookies() {
        String header = httpHeaders.getHeader(HttpHeader.COOKIE.getHeaderName());

        if (header == null) {
            return List.of();
        }

        return Cookie.of(header);
    }

    public String getSessionId() {
        List<Cookie> cookies = getCookies();

        if (cookies == null) {
            return null;
        }

        return cookies.stream()
                .filter(cookie -> cookie.getName().equals("SID"))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder request = new StringBuilder();

        request.append(requestStartLine).append(NEW_LINE)
                .append(httpHeaders).append(NEW_LINE)
                .append(NEW_LINE);

        if (requestBody != null) {
            request.append(requestBody);
        }

        return request.toString();
    }

}
