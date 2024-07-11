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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static codesquad.utils.StringUtils.*;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private ResponseLine responseLine;
    private HttpHeaders headers;
    private ResponseBody body;
    private List<Cookie> cookies;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final ResponseBody body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse empty() {
        return new HttpResponse(null, null, null);
    }

    public static HttpResponse of(final ContentType contentType, final HttpStatus httpStatus, final byte[] body) {
        ResponseBody responseBody = ResponseBody.from(body);
        return new HttpResponse(ResponseLine.of(httpStatus), HttpHeaders.of(contentType, responseBody), responseBody);
    }

    public static HttpResponse of(final HttpStatus httpStatus) {
        return new HttpResponse(ResponseLine.of(httpStatus), HttpHeaders.newInstance(), null);
    }

    public static HttpResponse ok() {
        return HttpResponse.of(HttpStatus.OK);
    }

    public static HttpResponse redirect(final HttpStatus httpStatus, final String location) {
        return new HttpResponse(ResponseLine.of(httpStatus), HttpHeaders.of(location), null);
    }

    public static HttpResponse notFound() {
        return HttpResponse.of(HttpStatus.NOT_FOUND);
    }

    public static HttpResponse internalServerError() {
        return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static HttpResponse badRequest() {
        return HttpResponse.of(HttpStatus.BAD_REQUEST);
    }

    public void sendRedirect(final String path) {
        this.responseLine = ResponseLine.of(HttpStatus.FOUND);
        this.headers = HttpHeaders.of(path);
        this.body = null;
    }

    public void setCookie(final Cookie cookie) {
        if (cookies == null) {
            cookies = new ArrayList<>();
            cookies.add(cookie);
            return;
        }

        cookies.add(cookie);
    }

    public boolean hasMessage() {
        return responseLine != null && headers != null;
    }

    public boolean hasBody() {
        return body != null;
    }

    public byte[] getResponseLineBytes() throws UnsupportedEncodingException {
        return responseLine.getBytes();
    }

    public byte[] getHeaderBytes() throws UnsupportedEncodingException {
        if (cookies == null) {
            return headers.getBytes();
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(headers.getBytes());
            outputStream.write(NEW_LINE.getBytes(DECODING_CHARSET));
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
