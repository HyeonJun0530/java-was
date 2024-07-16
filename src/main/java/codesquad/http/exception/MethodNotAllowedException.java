package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.FileUtil.getStaticFile;

public class MethodNotAllowedException extends HttpException {

    public MethodNotAllowedException(final String message) {
        super(HttpStatus.METHOD_NOT_ALLOWED, message, ContentType.TEXT_HTML, getStaticFile("/error/405Page.html"));
    }

}
