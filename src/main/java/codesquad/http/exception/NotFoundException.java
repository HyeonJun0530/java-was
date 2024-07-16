package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.FileUtil.getStaticFile;

public class NotFoundException extends HttpException {

    public NotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message, ContentType.TEXT_HTML, getStaticFile("/error/404Page.html"));
    }

}
