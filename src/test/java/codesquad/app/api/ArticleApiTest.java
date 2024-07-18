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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Object createArticlePage = articleApi.getCreateArticlePage(HttpRequest.from(new BufferedReader(new StringReader("GET /article HTTP/1.1"))));

        assertAll(() -> assertThat(createArticlePage).isInstanceOf(HttpResponse.class),
                () -> assertTrue(((HttpResponse) createArticlePage).toString().contains("200 OK")),
                () -> assertTrue(((HttpResponse) createArticlePage).toString().contains(ContentType.TEXT_HTML.getType()))
        );
    }

    @Test
    @DisplayName("Article을 생성한다.")
    void createArticle() throws IOException {
        String session = SessionManager.createSession(user, HttpResponse.ok());
        articleApi.createArticle(HttpRequest.from(new BufferedReader(new StringReader("POST /article HTTP/1.1\n" +
                "Content-Length: 31\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Cookie: SID=" + session + "\n" +

                "\n" +
                "title=제목&content=내용"))));
        Optional<Article> byLastSequence = articleDatabase.findByLastSequence();

        assertAll(() -> assertTrue(byLastSequence.isPresent()),
                () -> assertEquals("제목", byLastSequence.get().getTitle()),
                () -> assertEquals("javajigi", byLastSequence.get().getWriterId())
        );
    }

    @Test
    @DisplayName("해당 번호의 Article을 가져온다.")
    void getArticle() throws IOException {
        articleDatabase.save(new Article(1L, "제목", "내용", user.getUserId(), LocalDateTime.now(), LocalDateTime.now()));
        Object article = articleApi.getArticle(HttpRequest.from(new BufferedReader(new StringReader("GET /1 HTTP/1.1"))));

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
                articleApi.getArticle(HttpRequest.from(new BufferedReader(new StringReader("GET /1 HTTP/1.1")))))
                .isInstanceOf(NotFoundException.class);
    }


}
