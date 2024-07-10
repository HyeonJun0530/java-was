package codesquad.config;

import codesquad.http.filter.FilterChain;
import codesquad.http.filter.HttpFilterChain;
import codesquad.http.filter.LoginFilter;

public class FilterChainConfig {

    private static final FilterChain filterChain = new HttpFilterChain();

    private FilterChainConfig() {
    }

    public static FilterChain filterChain() {
        filterChain.addFilter(new LoginFilter(100));

        return filterChain;
    }
}
