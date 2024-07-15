package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.app.infrastructure.InMemoryArticleDatabase;
import codesquad.app.infrastructure.InMemoryCommentDatabase;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import static codesquad.utils.FileUtil.getStaticFile;

public class CommentApi {

    @ApiMapping(path = "/comment", method = HttpMethod.POST)
    public HttpResponse createComment(final HttpRequest request) {
        String refer = request.getHttpHeaders().getHeader(HttpHeader.REFERER.getHeaderName());
        String[] splitRefer = refer.split("/");

        Long sequence = Long.parseLong(splitRefer[splitRefer.length - 1]);

        Article article = InMemoryArticleDatabase.findBySequence(sequence)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        Comment comment = new Comment.Builder()
                .sequence(InMemoryCommentDatabase.sequence.getAndDecrement())
                .article(article)
                .writer(request.getRequestBody().parseFormUrlEncoded().get("writer"))
                .contents(request.getRequestBody().parseFormUrlEncoded().get("contents"))
                .build();

        InMemoryCommentDatabase.save(comment);

        return HttpResponse.redirect(HttpStatus.FOUND, refer);
    }

    @ApiMapping(path = "/comment", method = HttpMethod.GET)
    public HttpResponse getComment(final HttpRequest request) {
        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/comment/index.html"));
    }

}
