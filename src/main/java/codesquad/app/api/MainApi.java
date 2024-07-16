package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.app.infrastructure.InMemoryCommentDatabase;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static codesquad.utils.FileUtil.getStaticFile;

public class MainApi {

    private static final Logger log = LoggerFactory.getLogger(MainApi.class);

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
        Optional<Article> lastArticle = InMemoryArticleDatabase.findByLastSequence();

        if (lastArticle.isEmpty()) {
            log.debug("lastArticle is empty");
            return getMainPage(request);
        }

        log.debug("lastArticle: {}", lastArticle.get());

        mav.addObject("session", request.getSessionId());
        log.debug("session: {}", request.getSessionId());
        mav.addObject("article", lastArticle.get());
        mav.addObject("comments", InMemoryCommentDatabase.findByArticleSequence(lastArticle.get().getSequence()));

        return mav;
    }

    private HttpResponse getMainPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/index.html"));
    }
}
