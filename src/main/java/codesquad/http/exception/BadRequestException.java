package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.FileUtil.getStaticFile;

public class BadRequestException extends HttpException {

    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message, ContentType.TEXT_HTML, getStaticFile("/error/400Page.html"));
    }
}
