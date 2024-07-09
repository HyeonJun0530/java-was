package codesquad.http.message.response;

import codesquad.http.message.Cookie;
import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;
import codesquad.http.message.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static codesquad.utils.StringUtils.*;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final ResponseBody body;
    private List<Cookie> cookies;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final ResponseBody body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static <T> HttpResponse of(final ContentType contentType, final String httpVersion, final HttpStatus httpStatus, final T body) {
        ResponseBody responseBody = ResponseBody.from(body);
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.of(contentType, responseBody), responseBody);
    }

    public static HttpResponse redirect(final String httpVersion, final HttpStatus httpStatus, final String location) {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.of(location), null);
    }

    public static HttpResponse of(final String httpVersion, final HttpStatus httpStatus) {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.newInstance(), null);
    }

    public void setCookie(final Cookie cookie) {
        if (cookies == null) {
            cookies = new ArrayList<>();
            cookies.add(cookie);
            return;
        }

        cookies.add(cookie);
    }

    public boolean hasBody() {
        return body != null;
    }

    public byte[] getResponseLineBytes() {
        return responseLine.getBytes();
    }

    public byte[] getHeaderBytes() {
        if (cookies == null) {
            return headers.getBytes();
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(headers.getBytes());
            outputStream.write(NEW_LINE.getBytes(StandardCharsets.UTF_8));
            outputStream.write(Cookie.getCookiesBytes(this.cookies));
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.debug("Error writing header bytes = {}", e);
            return new byte[0];
        }
    }

    public byte[] getBodyBytes() {
        return body.getBytes();
    }

    @Override
    public String toString() {
        StringBuilder httpResponse = new StringBuilder();

        httpResponse.append(responseLine.toString()).append(NEW_LINE);
        httpResponse.append(headers.toString()).append(NEW_LINE);

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                httpResponse.append(HttpHeader.SET_COOKIE.getHeaderName())
                        .append(COLON).append(SPACE)
                        .append(cookie.toString()).append(NEW_LINE);
            }
        }

        if (hasBody()) {
            httpResponse.append(NEW_LINE).append(body.toString());
        }

        return httpResponse.toString();
    }

}
