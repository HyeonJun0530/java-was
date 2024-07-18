package codesquad.app.api;

import codesquad.app.domain.Article;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.*;
import codesquad.http.message.SessionManager;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static codesquad.utils.StringUtils.NEW_LINE;

class CommentApiTest {

    CommentApi commentApi;
    ArticleDatabase articleDatabase;
    CommentDatabase commentDatabase;
    UserDatabase userDatabase;
    User user;

    @BeforeEach
    void setUp() {
        articleDatabase = new InMemoryArticleDatabase();
        commentDatabase = new InMemoryCommentDatabase();
        userDatabase = new InMemoryUserDatabase();
        commentApi = new CommentApi(articleDatabase, commentDatabase);
        user = new User.Builder()
                .name("박재성")
                .email("javajigi@slipp.net")
                .userId("javajigi")
                .password("password")
                .build();
        userDatabase.save(user);

        articleDatabase.save(new Article.Builder()
                .sequence(1L)
                .title("title")
                .content("content")
                .writerId(userDatabase.findByUserId("javajigi").get().getUserId())
                .build());
    }

    @AfterEach
    void tearDown() {
        userDatabase.remove("javajigi");
        articleDatabase.remove(1L);
    }

    @Test
    @DisplayName("CommentApiTest 테스트 - 댓글 생성")
    void createComment() throws IOException {
        String session = SessionManager.createSession(user, HttpResponse.ok());
        String input = "POST /1/comment HTTP/1.1" + NEW_LINE +
                "Connection: keep-alive" + NEW_LINE +
                "Content-Length: 31" + NEW_LINE +
                "Content-Type: application/x-www-form-urlencoded" + NEW_LINE +
                "Referer: http://localhost:8080/1" + NEW_LINE +
                "Cookie: SID=" + session + NEW_LINE +
                NEW_LINE +
                "comment=contents";
        HttpRequest httpRequest = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
        HttpResponse comment = commentApi.createComment(httpRequest);

        Assertions.assertAll(
                () -> comment.toString().contains("HTTP/1.1 302 Found"),
                () -> comment.toString().contains("Location: http://localhost:8080/1")
        );
    }

    @Test
    void getComment() throws IOException {
        String input = "GET /comment HTTP/1.1" + NEW_LINE +
                "Connection: keep-alive" + NEW_LINE +
                "Content-Length: 0" + NEW_LINE +
                "Content-Type: application/x-www-form-urlencoded" + NEW_LINE +
                NEW_LINE;
        HttpRequest httpRequest = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));

        HttpResponse comment = commentApi.getComment(httpRequest);

        Assertions.assertAll(
                () -> comment.toString().contains("HTTP/1.1 200 OK"),
                () -> comment.toString().contains("Content-Type: text/html")
        );
    }
}
