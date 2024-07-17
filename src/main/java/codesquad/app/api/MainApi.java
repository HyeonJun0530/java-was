package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.infrastructure.ArticleDatabase;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static codesquad.utils.FileUtil.getStaticFile;

public class MainApi {

    private static final Logger log = LoggerFactory.getLogger(MainApi.class);

    private final ArticleDatabase articleDatabase;

    public MainApi(final ArticleDatabase articleDatabase) {
        this.articleDatabase = articleDatabase;
    }

    @ApiMapping(method = HttpMethod.GET, path = "/")
    public Object root(HttpRequest request) {
        return getMainPageWithArticle(request);
    }

    @ApiMapping(method = HttpMethod.GET, path = "/main")
    public Object main(HttpRequest request) {
        return getMainPageWithArticle(request);
    }

    private Object getMainPageWithArticle(final HttpRequest request) {
        Optional<Article> lastArticle = articleDatabase.findByLastSequence();

        if (lastArticle.isEmpty()) {
            log.debug("lastArticle is empty");
            return getMainPage(request);
        }

        return HttpResponse.redirect(HttpStatus.FOUND, "/" + lastArticle.get().getSequence());
    }

    private HttpResponse getMainPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/index.html"));
    }
}
