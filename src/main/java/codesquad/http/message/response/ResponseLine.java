package codesquad.http.message.response;

import codesquad.http.message.constant.HttpStatus;

import java.io.UnsupportedEncodingException;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
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

    public byte[] getBytes() throws UnsupportedEncodingException {
        return (HTTP_PROTOCOL + SPACE + httpStatus.toString()).getBytes(DECODING_CHARSET);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return HTTP_PROTOCOL + SPACE + this.httpStatus.toString();
    }
}
