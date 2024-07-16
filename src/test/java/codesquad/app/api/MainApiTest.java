package codesquad.app.api;

import codesquad.app.domain.Article;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.*;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;

import static codesquad.utils.StringUtils.NEW_LINE;
import static org.junit.jupiter.api.Assertions.*;

class MainApiTest {

    final String getMessage = "GET /main HTTP/1.1" + NEW_LINE +
            "Host: localhost:8080" + NEW_LINE +
            "Connection: keep-alive" + NEW_LINE +
            "Accept: */*" + NEW_LINE +
            "Accept-Encoding: gzip, deflate, br" + NEW_LINE +
            "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7" + NEW_LINE;

    MainApi mainApi;
    ArticleDatabase articleDatabase;
    CommentDatabase commentDatabase;
    UserDatabase userDatabase;
    User user;

    @BeforeEach
    void setUp() {
        articleDatabase = new InMemoryArticleDatabase();
        commentDatabase = new InMemoryCommentDatabase();
        userDatabase = new InMemoryUserDatabase();
        mainApi = new MainApi(articleDatabase, commentDatabase);
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
                .writer(userDatabase.findByUserId("javajigi").get())
                .build());
    }

    @AfterEach
    void tearDown() {
        userDatabase.remove("test");
    }

    @Test
    @DisplayName("MainApi 인스턴스를 생성한다.")
    void createMainApi() {
        mainApi = new MainApi(articleDatabase, commentDatabase);

        assertNotNull(mainApi);
    }

    @Test
    @DisplayName("/main 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 X")
    void main() throws IOException {

        HttpResponse noLogin = (HttpResponse) mainApi.main(noLoginRequest());

        assertAll(() -> assertTrue(noLogin.hasBody()),
                () -> assertTrue(noLogin.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(noLogin.toString().contains("로그인"))
        );
    }

    @Test
    @DisplayName("/main 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 상태")
    void main_login() throws IOException {

        HttpResponse login = (HttpResponse) mainApi.main(loginRequest());

        assertAll(() -> assertTrue(login.hasBody()),
                () -> assertTrue(login.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(login.toString().contains("글쓰기")),
                () -> assertTrue(login.toString().contains("로그아웃"))
        );
    }

    @Test
    @DisplayName("/ 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 X")
    void root() throws IOException {
        HttpResponse noLogin = (HttpResponse) mainApi.root(noLoginRequest());

        assertAll(() -> assertTrue(noLogin.hasBody()),
                () -> assertTrue(noLogin.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(noLogin.toString().contains("로그인"))
        );
    }

    @Test
    @DisplayName("/ 경로로 요청이 들어오면 index.html을 반환한다. - 로그인 상태")
    void root_login() throws IOException {
        HttpRequest httpRequest = loginRequest();
        HttpResponse login = (HttpResponse) mainApi.root(httpRequest);

        assertAll(() -> assertTrue(login.hasBody()),
                () -> assertTrue(login.toString().contains(ContentType.TEXT_HTML.getType())),
                () -> assertTrue(login.toString().contains("글쓰기")),
                () -> assertTrue(login.toString().contains("로그아웃"))
        );
    }

    @Test
    @DisplayName("Article이 있는 상태에서 /main 경로로 요청이 들어오면 최근 Article을 반환한다")
    void getMainPageWithArticle() throws IOException {
        articleDatabase.save(new Article(articleDatabase.getSequence().getAndIncrement(), "test", "contest", user,
                LocalDateTime.now(), LocalDateTime.now()));

        HttpRequest httpRequest = loginRequest();
        Object mav = mainApi.main(httpRequest);

        assertAll(() -> assertTrue(mav instanceof ModelAndView),
                () -> assertTrue(((ModelAndView) mav).containsAttribute("article")),
                () -> assertTrue(((ModelAndView) mav).getObject("article") instanceof Article)
        );

        articleDatabase.remove(articleDatabase.getSequence().getAndIncrement());
    }

    private HttpRequest loginRequest() throws IOException {
        String session = SessionManager.createSession(user, HttpResponse.ok());
        String loginMessage = getMessage + "Cookie: SID=" + session + NEW_LINE;
        return HttpRequest.from(new BufferedReader(new StringReader(loginMessage)));
    }

    private HttpRequest noLoginRequest() throws IOException {
        return HttpRequest.from(new BufferedReader(new StringReader(getMessage)));
    }

}
