package codesquad.config;

import codesquad.http.filter.FilterChain;
import codesquad.http.filter.HttpFilterChain;

public class FilterChainConfig {

    private static final FilterChain filterChain = new HttpFilterChain();

    private FilterChainConfig() {
    }

    public static FilterChain filterChain() {
        return filterChain;
    }
}
