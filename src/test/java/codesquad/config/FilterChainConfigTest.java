package codesquad.config;

import codesquad.http.filter.FilterChain;
import codesquad.http.filter.HttpFilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterChainConfigTest {

    @Test
    @DisplayName("FilterChain이 HttpFilterChain 인스턴스를 반환한다.")
    void existFilterChainConfig() {
        FilterChain filterChain = FilterChainConfig.filterChain();

        assertAll(() -> assertNotNull(filterChain),
                () -> assertInstanceOf(HttpFilterChain.class, filterChain)
        );
    }

}
