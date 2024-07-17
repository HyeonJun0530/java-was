package codesquad.config;

import codesquad.app.api.ArticleApi;
import codesquad.app.api.CommentApi;
import codesquad.app.api.MainApi;
import codesquad.app.api.UserApi;
import codesquad.app.infrastructure.*;
import codesquad.http.DispatcherServlet;
import codesquad.http.adapter.TemplateAdapter;
import codesquad.http.adapter.renderer.ArticleRenderer;
import codesquad.http.adapter.renderer.UserListRenderer;
import codesquad.http.adapter.renderer.ViewRenderer;
import codesquad.http.filter.FilterChain;
import codesquad.http.handler.ApiHandler;
import codesquad.http.handler.StaticHandler;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private static ApplicationContext ApplicationContextHolder;

    private final FilterChain filterChain;
    private final DispatcherServlet dispatcherServlet;

    private ApplicationContext(final DispatcherServlet dispatcherServlet, final FilterChain filterChain) {
        this.filterChain = filterChain;
        this.dispatcherServlet = dispatcherServlet;
    }

    public static void initialize() {
        DataSource dataSource = new DataSourceConfig().getDataSource();

        // Database 초기화
        UserDatabase userDatabase = new JdbcUserDatabase(dataSource);
        ArticleDatabase articleDatabase = new JdbcArticleDatabase(dataSource);
        CommentDatabase commentDatabase = new JdbcCommentDatabase(dataSource);


        UserApi userApi = new UserApi(userDatabase);
        MainApi mainApi = new MainApi(articleDatabase);
        ArticleApi articleApi = new ArticleApi(articleDatabase, commentDatabase, userDatabase);
        CommentApi commentApi = new CommentApi(articleDatabase, commentDatabase);

        ApiHandler apiHandler = new ApiHandler(Map.of(UserApi.class, userApi,
                MainApi.class, mainApi,
                ArticleApi.class, articleApi,
                CommentApi.class, commentApi));

        StaticHandler staticHandler = new StaticHandler();

        TemplateAdapter templateAdapter = getTemplateAdapter();

        DispatcherServlet dispatcherServlet = new DispatcherServlet(
                List.of(apiHandler, staticHandler),
                List.of(templateAdapter)
        );


        ApplicationContextHolder = new ApplicationContext(dispatcherServlet, new FilterChainConfig().getFilterChain());
    }

    private static TemplateAdapter getTemplateAdapter() {
        return new TemplateAdapter(
                registryRenderer()
        );
    }

    private static List<ViewRenderer> registryRenderer() {
        return List.of(new UserListRenderer(), new ArticleRenderer());
    }

    public static ApplicationContext getInstance() {
        if (ApplicationContextHolder == null) {
            initialize();
        }

        return ApplicationContextHolder;
    }

    public DispatcherServlet getDispatcherServlet() {
        return dispatcherServlet;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

}
