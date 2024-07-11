package codesquad.http.message.response;

import codesquad.http.message.constant.HttpStatus;

import java.nio.charset.StandardCharsets;

import static codesquad.utils.StringUtils.SPACE;

public class ResponseLine {

    private static final String HTTP_PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;

    private ResponseLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ResponseLine of(final HttpStatus httpStatus) {
        return new ResponseLine(httpStatus);
    }

    public byte[] getBytes() {
        return (HTTP_PROTOCOL + SPACE + httpStatus.toString()).getBytes(StandardCharsets.UTF_8);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return HTTP_PROTOCOL + SPACE + this.httpStatus.toString();
    }
}
