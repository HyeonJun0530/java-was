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
import codesquad.utils.FileUtil;

import java.time.LocalDateTime;
import java.util.Map;

public class ArticleApi {

    @ApiMapping(path = "/article", method = HttpMethod.GET)
    public Object getArticle(final HttpRequest request) {
        if (!SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/login");
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, FileUtil.getStaticFile("/article/index.html"));
    }

    @ApiMapping(path = "/article", method = HttpMethod.POST)
    public Object createArticle(final HttpRequest request) {
        if (!SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/login");
        }

        Map<String, String> parameter = request.getRequestBody().parseFormUrlEncoded();

        Article article = new Article.Builder()
                .sequence(InMemoryArticleDatabase.sequence.getAndDecrement())
                .title(parameter.get("title"))
                .content(parameter.get("content"))
                .writer(SessionManager.getUser(request.getSessionId()).get())
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();

        Article save = InMemoryArticleDatabase.save(article);

        return HttpResponse.redirect(HttpStatus.FOUND, "/article" + save.getSequence());
    }
}
