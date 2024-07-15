package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;

import java.util.Optional;

import static codesquad.utils.FileUtil.getStaticFile;

public class MainApi {

    @ApiMapping(method = HttpMethod.GET, path = "/")
    public Object root(HttpRequest request) {
        return getMainPageWithArticle(request);
    }

    @ApiMapping(method = HttpMethod.GET, path = "/main")
    public Object main(HttpRequest request) {
        return getMainPageWithArticle(request);
    }

    private Object getMainPageWithArticle(final HttpRequest request) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("/article/index.html");
        Optional<Article> lastSequence = InMemoryArticleDatabase.findByLastSequence();

        mav.addObject("article", lastSequence);

        if (lastSequence.isEmpty()) {
            return getMainPage(request);
        }

        return mav;
    }

    private HttpResponse getMainPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/index.html"));
    }
}
