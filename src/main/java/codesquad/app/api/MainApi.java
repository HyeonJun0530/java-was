package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import static codesquad.utils.FileUtil.getStaticFile;

public class MainApi {

    @ApiMapping(method = HttpMethod.GET, path = "/")
    public HttpResponse root(HttpRequest request) {
        return getMainPage(request);
    }

    @ApiMapping(method = HttpMethod.GET, path = "/main")
    public HttpResponse main(HttpRequest request) {
        return getMainPage(request);
    }

    private HttpResponse getMainPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/index.html"));
    }
}
