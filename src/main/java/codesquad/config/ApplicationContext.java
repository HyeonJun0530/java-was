package codesquad.config;

import codesquad.app.api.ArticleApi;
import codesquad.app.api.CommentApi;
import codesquad.app.api.MainApi;
import codesquad.app.api.UserApi;
import codesquad.app.infrastructure.*;

public class ApplicationContext {

    private static ApplicationContext ApplicationContextHolder;
    private final UserApi userApi;
    private final MainApi mainApi;
    private final ArticleApi articleApi;
    private final CommentApi commentApi;
    private final CommentDatabase commentDatabase;
    private final UserDatabase userDatabase;
    private final ArticleDatabase articleDatabase;

    private ApplicationContext(final UserApi userApi, final MainApi mainApi,
                               final ArticleApi articleApi, final CommentApi commentApi,
                               final CommentDatabase commentDatabase, final UserDatabase userDatabase,
                               final ArticleDatabase articleDatabase) {
        this.userApi = userApi;
        this.mainApi = mainApi;
        this.articleApi = articleApi;
        this.commentApi = commentApi;
        this.commentDatabase = commentDatabase;
        this.userDatabase = userDatabase;
        this.articleDatabase = articleDatabase;
    }

    public static void initialize() {
        // Database 초기화
        UserDatabase userDatabase = new InMemoryUserDatabase();
        ArticleDatabase articleDatabase = new InMemoryArticleDatabase();
        CommentDatabase commentDatabase = new InMemoryCommentDatabase();


        UserApi userApi = new UserApi(userDatabase);
        MainApi mainApi = new MainApi(articleDatabase, commentDatabase);
        ArticleApi articleApi = new ArticleApi(articleDatabase, commentDatabase);
        CommentApi commentApi = new CommentApi(articleDatabase, commentDatabase);

        ApplicationContextHolder = new ApplicationContext(userApi, mainApi,
                articleApi, commentApi,
                commentDatabase, userDatabase,
                articleDatabase);
    }

    public static ApplicationContext getInstance() {
        if (ApplicationContextHolder == null) {
            initialize();
        }

        return ApplicationContextHolder;
    }

    public UserApi getUserApi() {
        return userApi;
    }

    public MainApi getMainApi() {
        return mainApi;
    }

    public ArticleApi getArticleApi() {
        return articleApi;
    }

    public CommentApi getCommentApi() {
        return commentApi;
    }

    public CommentDatabase getCommentDatabase() {
        return commentDatabase;
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public ArticleDatabase getArticleDatabase() {
        return articleDatabase;
    }
}
