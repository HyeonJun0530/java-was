package codesquad.http.message.response;

import codesquad.http.message.constant.HttpStatus;

import java.nio.charset.StandardCharsets;

import static codesquad.utils.StringUtils.SPACE;

public class ResponseLine {

    private final String httpVersion;
    private final HttpStatus httpStatus;

    private ResponseLine(final String httpVersion, final HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public static ResponseLine of(final String httpVersion, final HttpStatus httpStatus) {
        return new ResponseLine(httpVersion, httpStatus);
    }

    public byte[] getBytes() {
        return (httpVersion + SPACE + httpStatus.toString()).getBytes(StandardCharsets.UTF_8);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return this.httpVersion + SPACE + this.httpStatus.toString();
    }
}
