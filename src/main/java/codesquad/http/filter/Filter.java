package codesquad.http.filter;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

public interface Filter {

    void doFilter(final HttpRequest request, final HttpResponse response, final HttpFilterChain httpFilterChain);

    boolean isMatched(String path);

    int getOrder();

}
