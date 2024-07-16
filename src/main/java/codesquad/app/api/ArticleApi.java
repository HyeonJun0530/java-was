package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.app.infrastructure.InMemoryCommentDatabase;
import codesquad.http.exception.NotFoundException;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArticleApi {

    private static final Logger log = LoggerFactory.getLogger(ArticleApi.class);

    @ApiMapping(path = "/article", method = HttpMethod.GET)
    public Object getCreateArticlePage(final HttpRequest request) {
        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, FileUtil.getStaticFile("/article/index.html"));
    }

    @ApiMapping(path = "/article", method = HttpMethod.POST)
    public Object createArticle(final HttpRequest request) {
        Map<String, String> parameter = request.getRequestBody().parseFormUrlEncoded();

        Article article = new Article.Builder()
                .sequence(InMemoryArticleDatabase.sequence.getAndIncrement())
                .title(parameter.get("title"))
                .content(parameter.get("content"))
                .writer(SessionManager.getUser(request.getSessionId()).get())
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();

        Article save = InMemoryArticleDatabase.save(article);

        log.debug("save article = {}", save);


        return HttpResponse.redirect(HttpStatus.FOUND, "/" + save.getSequence());
    }

    @ApiMapping(path = "/{sequence}", method = HttpMethod.GET)
    public Object getArticle(final HttpRequest request) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("/article/index.html");

        String pathVariable = request.getRequestStartLine().getPath().split("/")[1];
        Optional<Article> findLastArticle = InMemoryArticleDatabase.findBySequence(Long.parseLong(pathVariable));

        if (findLastArticle.isEmpty()) {
            throw new NotFoundException("Article not found");
        }

        List<Comment> comments = InMemoryCommentDatabase.findByArticleSequence(findLastArticle.get().getSequence());

        mav.addObject("session", request.getSessionId());
        mav.addObject("article", findLastArticle.get());
        mav.addObject("comments", comments);

        log.debug("get article = {}", InMemoryArticleDatabase.findBySequence(Long.parseLong(pathVariable)).get());

        return mav;
    }
}
