package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.infrastructure.ArticleDatabase;
import codesquad.app.infrastructure.CommentDatabase;
import codesquad.app.infrastructure.UserDatabase;
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

    private final UserDatabase userDatabase;
    private final ArticleDatabase articleDatabase;
    private final CommentDatabase commentDatabase;

    public MainApi(final UserDatabase userDatabase, final ArticleDatabase articleDatabase, final CommentDatabase commentDatabase) {
        this.userDatabase = userDatabase;
        this.articleDatabase = articleDatabase;
        this.commentDatabase = commentDatabase;
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
        ModelAndView mav = new ModelAndView();

        mav.setViewName("/article/index.html");
        Optional<Article> lastArticle = articleDatabase.findByLastSequence();

        if (lastArticle.isEmpty()) {
            log.debug("lastArticle is empty");
            return getMainPage(request);
        }

        log.debug("lastArticle: {}", lastArticle.get());

        mav.addObject("session", request.getSessionId());
        log.debug("session: {}", request.getSessionId());
        mav.addObject("writerName", userDatabase.findByUserId(lastArticle.get().getWriterId()).get().getName());
        mav.addObject("article", lastArticle.get());
        mav.addObject("comments", commentDatabase.findByArticleSequence(lastArticle.get().getSequence()));

        return mav;
    }

    private HttpResponse getMainPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/main/index.html"));
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/index.html"));
    }
}
