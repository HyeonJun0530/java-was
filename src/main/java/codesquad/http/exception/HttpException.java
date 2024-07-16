package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

public abstract class HttpException extends RuntimeException {

    private final ContentType contentType;
    private final HttpStatus httpStatus;
    private final byte[] body;

    public HttpException(final HttpStatus httpStatus, final String message, final ContentType contentType, final byte[] body) {
        super(message);
        this.contentType = contentType;
        this.httpStatus = httpStatus;
        this.body = body;
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }
}
