package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.app.infrastructure.InMemoryCommentDatabase;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;

import static codesquad.utils.FileUtil.getStaticFile;

public class CommentApi {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CommentApi.class);

    @ApiMapping(path = "/{articleId}/comment", method = HttpMethod.POST)
    public HttpResponse createComment(final HttpRequest request) {
        String path = request.getRequestStartLine().getPath();
        log.debug("path: {}", path);

        User user = SessionManager.getUser(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("로그인이 필요합니다."));

        String[] splitPath = path.split("/");
        long articleId = Long.parseLong(splitPath[1]);

        Article article = InMemoryArticleDatabase.findBySequence(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        Comment comment = new Comment.Builder()
                .sequence(InMemoryCommentDatabase.sequence.getAndIncrement())
                .article(article)
                .writer(user.getName())
                .contents(request.getRequestBody().parseFormUrlEncoded().get("comment"))
                .build();

        Comment save = InMemoryCommentDatabase.save(comment);

        log.debug("comment: {}", save);

        return HttpResponse.redirect(HttpStatus.FOUND, "/" + article.getSequence());
    }

    @ApiMapping(path = "/comment", method = HttpMethod.GET)
    public HttpResponse getComment(final HttpRequest request) {
        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/comment/index.html"));
    }

}
