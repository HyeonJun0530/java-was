package codesquad.http.filter;

import codesquad.http.message.Cookie;
import codesquad.http.message.SessionManager;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import java.util.List;

import static codesquad.http.handler.StaticHandler.staticExtension;

public class LoginFilter implements Filter {

    private static final List<String> permitAll = List.of("/", "/login", "/create", "/registration");
    private final int order;

    public LoginFilter(final int order) {
        this.order = order;
    }

    @Override
    public void doFilter(final HttpRequest request, final HttpResponse response, final HttpFilterChain httpFilterChain) {
        if (isMatched(request.getRequestStartLine().getPath())) {
            httpFilterChain.doFilter(request, response);
            return;
        }

        boolean validSession = request.getCookies().stream()
                .filter(cookie -> cookie.getName().equalsIgnoreCase("SID"))
                .map(Cookie::getValue)
                .anyMatch(SessionManager::isValidSession);

        if (validSession) {
            httpFilterChain.doFilter(request, response);
        } else {
            response.sendRedirect("/login");
        }
    }

    @Override
    public boolean isMatched(final String path) {
        return permitAll.contains(path) || staticExtension.stream().anyMatch(path::endsWith);
    }

    @Override
    public int getOrder() {
        return this.order;
    }

}
