package codesquad.http.filter;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HttpFilterChain implements FilterChain {

    private static final List<Filter> filters = new ArrayList<>();
    private ThreadLocal<Integer> index = ThreadLocal.withInitial(() -> 0);

    public HttpFilterChain() {
        filters.add(new LoginFilter(100));
    }

    @Override
    public void doFilter(final HttpRequest request, final HttpResponse response) {
        Integer filterIndex = index.get();
        if (filterIndex < filters.size()) {
            Filter nextFilter = filters.get(filterIndex);
            filterIndex++;
            index.set(filterIndex);
            nextFilter.doFilter(request, response, this);
            return;
        }

        index.set(0);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
        filters.sort(Comparator.comparingInt(Filter::getOrder));
    }

}
