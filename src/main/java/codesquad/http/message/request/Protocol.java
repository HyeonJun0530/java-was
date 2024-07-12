package codesquad.http.message.request;

public class Protocol {

    public static final String HTTP_PROTOCOL = "HTTP/1.1";

    private final String version;

    private Protocol(final String version) {
        this.version = version;
    }

    public static Protocol from(final String version) {
        validateHttpVersion(version);

        return new Protocol(version);
    }

    private static void validateHttpVersion(final String version) {
        if (!version.equalsIgnoreCase(HTTP_PROTOCOL)) {
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
