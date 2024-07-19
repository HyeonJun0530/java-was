package codesquad.http.message.request;

import codesquad.http.exception.BadRequestException;
import codesquad.http.message.Cookie;
import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static codesquad.http.message.constant.HttpHeader.CONTENT_LENGTH;
import static codesquad.utils.StringUtils.NEW_LINE;

public class HttpRequest {

    private static final int limitContentLength = 10000000;

    private final RequestStartLine requestStartLine;
    private final HttpHeaders httpHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestStartLine requestStartLine, final HttpHeaders httpHeaders,
                       final RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final InputStream reader) throws IOException {
        RequestStartLine requestStartLine = RequestStartLine.from(reader);
        HttpHeaders httpHeaders = HttpHeaders.from(reader);
        String length = Optional.ofNullable(httpHeaders.getHeader(CONTENT_LENGTH.getHeaderName())).orElse("0");
        int contentLength = Integer.parseInt(length);

        if (contentLength > limitContentLength) {
            throw new BadRequestException("Content-Length가 너무 큽니다.");
        }

        if (contentLength == 0) {
            return new HttpRequest(requestStartLine, httpHeaders, null);
        }

        if (httpHeaders.getHeader(HttpHeader.CONTENT_TYPE.getHeaderName()).contains(ContentType.MULTI_PART_FORM.getType())) {
            return new HttpRequest(requestStartLine, httpHeaders, MultipartRequestBody.from(reader, contentLength, httpHeaders));
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
            return "invalid";
        }

        return cookies.stream()
                .filter(cookie -> cookie.getName().equals("SID"))
                .map(Cookie::getValue)
                .findFirst().orElse("invalid");
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
