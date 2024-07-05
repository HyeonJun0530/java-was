package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ContentTypeTest {

    private static Stream<Arguments> param() {
        return Stream.of(
                Arguments.of(".html", "text/html"),
                Arguments.of(".css", "text/css"),
                Arguments.of(".js", "text/javascript"),
                Arguments.of(".png", "image/png"),
                Arguments.of(".gif", "image/gif"),
                Arguments.of(".svg", "image/svg+xml"),
                Arguments.of(".ico", "image/x-icon"),
                Arguments.of(".json", "application/json"),
                Arguments.of(".xml", "application/xml"),
                Arguments.of(".xhtml", "application/xhtml+xml"),
                Arguments.of(".bin", "application/octet-stream")
        );
    }

    @ParameterizedTest
    @MethodSource("param")
    @DisplayName("확장자에 해당하는 ContentType을 반환한다.")
    void from(String ext, String expectedType) {
        ContentType contentType = ContentType.from(ext);
        System.out.println(contentType);

        assertThat(contentType.getType()).isEqualTo(expectedType);
    }
}
