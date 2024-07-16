package codesquad.http.exception;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.FileUtil.getStaticFile;

public class UnAuthorizedException extends HttpException {

    public UnAuthorizedException(final String message) {
        super(HttpStatus.UNAUTHORIZED, message, ContentType.TEXT_HTML, getStaticFile("/error/401Page.html"));
    }

}
