package codesquad.http.filter;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

public interface FilterChain {
    void doFilter(HttpRequest request, HttpResponse response);

    void addFilter(Filter filter);
}
