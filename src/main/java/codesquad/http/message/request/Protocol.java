package codesquad.http.message.request;

public class Protocol {

    private static final String HTTP_PROTOCOL_PATTERN = "^HTTP/1\\.[01]$";

    private final String version;

    private Protocol(final String version) {
        this.version = version;
    }

    public static Protocol from(final String version) {
        validateHttpVersion(version);

        return new Protocol(version);
    }

    private static void validateHttpVersion(final String version) {
        if (!version.matches(HTTP_PROTOCOL_PATTERN)) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 버전입니다.");
        }
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return this.version;
    }

}
