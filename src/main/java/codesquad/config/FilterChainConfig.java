package codesquad.config;

import codesquad.http.filter.FilterChain;
import codesquad.http.filter.HttpFilterChain;
import codesquad.http.filter.LoginFilter;

public class FilterChainConfig {

    private final FilterChain filterChain;

    public FilterChainConfig() {
        this.filterChain = new HttpFilterChain();

        filterChain.addFilter(new LoginFilter(100));
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

}
