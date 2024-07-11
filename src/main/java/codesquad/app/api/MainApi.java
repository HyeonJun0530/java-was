package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import static codesquad.utils.FileUtil.getStaticFiles;

public class MainApi {

    @ApiMapping(method = HttpMethod.GET, path = "/")
    public HttpResponse main(HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFiles("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFiles("/index.html"));
    }
}
