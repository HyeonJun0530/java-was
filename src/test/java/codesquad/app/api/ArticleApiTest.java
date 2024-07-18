package codesquad.app.api;

import codesquad.app.domain.Article;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.*;
import codesquad.http.exception.NotFoundException;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static codesquad.utils.StringUtils.NEW_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ArticleApiTest {

    ArticleApi articleApi;
    User user;
    ArticleDatabase articleDatabase;

    @BeforeEach
    void setUp() {
        UserDatabase userDatabase = new InMemoryUserDatabase();
        articleDatabase = new InMemoryArticleDatabase();
        articleApi = new ArticleApi(articleDatabase, new InMemoryCommentDatabase(), userDatabase);
        user = new User("javajigi", "password", "자바지기", "1@1.com");
        userDatabase.save(user);
    }

    @Test
    @DisplayName("Article 작성 페이지를 가져온다.")
    void getCreateArticlePage() throws IOException {
        String input = "GET /article HTTP/1.1";
        Object createArticlePage = articleApi.getCreateArticlePage(HttpRequest.from(new ByteArrayInputStream(input.getBytes())));

        assertAll(() -> assertThat(createArticlePage).isInstanceOf(HttpResponse.class),
                () -> assertTrue(((HttpResponse) createArticlePage).toString().contains("200 OK")),
                () -> assertTrue(((HttpResponse) createArticlePage).toString().contains(ContentType.TEXT_HTML.getType()))
        );
    }

    @Test
    @DisplayName("Article을 생성한다.")
    void createArticle() throws IOException {
        String session = SessionManager.createSession(user, HttpResponse.ok());
        String input = "POST /article HTTP/1.1" + NEW_LINE +
                "Cookie: " + "SID=" + session + NEW_LINE +
                "Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW" + NEW_LINE +
                "Content-Length: 200" + NEW_LINE +
                NEW_LINE +
                "------WebKitFormBoundary7MA4YWxkTrZu0gW" + NEW_LINE +
                "Content-Disposition: form-data; name=\"title\"" + NEW_LINE +
                NEW_LINE +
                "제목" + NEW_LINE +
                "------WebKitFormBoundary7MA4YWxkTrZu0gW" + NEW_LINE +
                "Content-Disposition: form-data; name=\"content\"" + NEW_LINE +
                NEW_LINE +
                "내용" + NEW_LINE +
                "------WebKitFormBoundary7MA4YWxkTrZu0gW" + NEW_LINE +
                "Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"" + NEW_LINE +
                "Content-Type: image/png" + NEW_LINE +
                NEW_LINE +
                "이미지" + NEW_LINE +
                "------WebKitFormBoundary7MA4YWxkTrZu0gW--" + NEW_LINE;
        HttpRequest from = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
        System.out.println(from);
        articleApi.createArticleWithImage(from);
        Optional<Article> byLastSequence = articleDatabase.findByLastSequence();

        assertAll(() -> assertTrue(byLastSequence.isPresent()),
                () -> assertTrue(byLastSequence.get().getTitle().contains("제목")),
                () -> assertTrue(byLastSequence.get().getWriterId().contains("javajigi"))
        );
    }

    @Test
    @DisplayName("해당 번호의 Article을 가져온다.")
    void getArticle() throws IOException {
        articleDatabase.save(new Article(1L, "제목", "내용", user.getUserId(), "image", LocalDateTime.now(), LocalDateTime.now()));
        String input = "GET /1 HTTP/1.1";
        Object article = articleApi.getArticle(HttpRequest.from(new ByteArrayInputStream(input.getBytes())));

        assertAll(() -> assertInstanceOf(ModelAndView.class, article),
                () -> assertEquals("/article/index.html", ((ModelAndView) article).getViewName()),
                () -> assertThat(((ModelAndView) article).getObject("article")).isInstanceOf(Article.class),
                () -> assertTrue(((ModelAndView) article).getObject("article").toString().contains("제목")),
                () -> assertTrue(((ModelAndView) article).getObject("article").toString().contains("내용")),
                () -> assertTrue(((ModelAndView) article).getObject("article").toString().contains("javajigi")),
                () -> assertThat(((ModelAndView) article).getObject("comments")).isInstanceOf(List.class)
        );
    }

    @Test
    @DisplayName("해당 번호의 Article을 가져온다. - 실패")
    void getArticle_fail() throws IOException {
        assertThatThrownBy(() ->
        {
            String input = "GET /1 HTTP/1.1";
            articleApi.getArticle(HttpRequest.from(new ByteArrayInputStream(input.getBytes())));
        })
                .isInstanceOf(NotFoundException.class);
    }


}
