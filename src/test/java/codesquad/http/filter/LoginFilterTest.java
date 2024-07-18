package codesquad.http.filter;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static codesquad.utils.StringUtils.NEW_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginFilterTest {

    HttpRequest successRequest;
    HttpRequest failRequest;
    HttpRequest pathVariableRequest;
    HttpRequest pathVariableRequestFail;
    HttpResponse response;
    HttpFilterChain httpFilterChain;
    LoginFilter loginFilter = new LoginFilter(1);

    @BeforeEach
    void setUp() throws IOException {
        String input = "GET /login HTTP/1.1" + NEW_LINE + "Cookie: SID=1234" + NEW_LINE + NEW_LINE;
        successRequest = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
        input = "GET /user/list HTTP/1.1";
        failRequest = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
        input = "GET /11421415 HTTP/1.1";
        pathVariableRequest = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
        input = "GET /11421415/aarsrfsr HTTP/1.1";
        pathVariableRequestFail = HttpRequest.from(new ByteArrayInputStream(input.getBytes()));
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
        boolean pathVariableMatch = loginFilter.isMatched(pathVariableRequest.getRequestStartLine().getPath());
        boolean pathVariableNotMatch = loginFilter.isMatched(pathVariableRequestFail.getRequestStartLine().getPath());

        assertAll(() -> assertTrue(matched),
                () -> assertFalse(notMatched),
                () -> assertTrue(pathVariableMatch),
                () -> assertFalse(pathVariableNotMatch)
        );
    }

    @Test
    void getOrder() {
        assertThat(loginFilter.getOrder()).isEqualTo(1);
    }
}
