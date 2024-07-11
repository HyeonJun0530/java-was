package codesquad.http.filter;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginFilterTest {

    HttpRequest successRequest;
    HttpRequest failRequest;
    HttpResponse response;
    HttpFilterChain httpFilterChain;
    LoginFilter loginFilter = new LoginFilter(1);

    @BeforeEach
    void setUp() throws IOException {
        successRequest = HttpRequest.from(new BufferedReader(new StringReader("GET /login HTTP/1.1\r\nCookie: SID=1234\r\n\r\n")));
        failRequest = HttpRequest.from(new BufferedReader(new StringReader("GET /user/list HTTP/1.1")));
        response = HttpResponse.empty();
        httpFilterChain = new HttpFilterChain();
        httpFilterChain.addFilter(loginFilter);
    }

    @Test
    @DisplayName("filter가 정상적으로 동작하는지 테스트")
    void doFilter() {
        loginFilter.doFilter(successRequest, response, httpFilterChain);
        assertThat(response.hasMessage()).isFalse();
    }

    @Test
    @DisplayName("filter가 정상적으로 동작하는지 테스트 - 실패")
    void doFilterFail() {
        loginFilter.doFilter(failRequest, response, httpFilterChain);

        assertAll(() -> assertThat(response.toString()).containsIgnoringCase("HTTP/1.1 302 Found"),
                () -> assertThat(response.toString()).containsIgnoringCase("Location: /login"));
    }

    @Test
    void isMatched() {
        boolean matched = loginFilter.isMatched(successRequest.getRequestStartLine().getPath());
        boolean notMatched = loginFilter.isMatched(failRequest.getRequestStartLine().getPath());

        assertAll(() -> assertTrue(matched),
                () -> assertFalse(notMatched)
        );
    }

    @Test
    void getOrder() {
        assertThat(loginFilter.getOrder()).isEqualTo(1);
    }
}
