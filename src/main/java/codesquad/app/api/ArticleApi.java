package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.ArticleDatabase;
import codesquad.app.infrastructure.CommentDatabase;
import codesquad.app.infrastructure.UserDatabase;
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

    private final ArticleDatabase articleDatabase;
    private final CommentDatabase commentDatabase;
    private final UserDatabase userDatabase;

    public ArticleApi(final ArticleDatabase articleDatabase, final CommentDatabase commentDatabase, final UserDatabase userDatabase) {
        this.articleDatabase = articleDatabase;
        this.commentDatabase = commentDatabase;
        this.userDatabase = userDatabase;
    }

    @ApiMapping(path = "/article", method = HttpMethod.GET)
    public Object getCreateArticlePage(final HttpRequest request) {
        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, FileUtil.getStaticFile("/article/index.html"));
    }

    @ApiMapping(path = "/article", method = HttpMethod.POST)
    public Object createArticle(final HttpRequest request) {
        Map<String, String> parameter = request.getRequestBody().parseFormUrlEncoded();

        Article article = new Article.Builder()
                .title(parameter.get("title"))
                .content(parameter.get("content"))
                .writerId(SessionManager.getUser(request.getSessionId()).get().getUserId())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        Article save = articleDatabase.save(article);

        log.debug("save article = {}", save);


        return HttpResponse.redirect(HttpStatus.FOUND, "/" + save.getSequence());
    }

    @ApiMapping(path = "/{sequence}", method = HttpMethod.GET)
    public Object getArticle(final HttpRequest request) {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("/article/index.html");

        String pathVariable = request.getRequestStartLine().getPath().split("/")[1];
        Optional<Article> findLastArticle = articleDatabase.findBySequence(Long.parseLong(pathVariable));

        if (findLastArticle.isEmpty()) {
            throw new NotFoundException("Article not found");
        }

        Article article = findLastArticle.get();

        User user = userDatabase.findByUserId(article.getWriterId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Comment> comments = commentDatabase.findByArticleSequence(article.getSequence());

        mav.addObject("session", request.getSessionId());
        mav.addObject("writerName", user.getName());
        mav.addObject("article", article);
        mav.addObject("comments", comments);

        log.debug("get article = {}", articleDatabase.findBySequence(Long.parseLong(pathVariable)).get());

        return mav;
    }
}
