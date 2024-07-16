package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.FileUtil.getStaticFile;

public class InternalServerException extends HttpException {

    public InternalServerException(final String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, ContentType.TEXT_HTML, getStaticFile("/error/500Page.html"));
    }

}
