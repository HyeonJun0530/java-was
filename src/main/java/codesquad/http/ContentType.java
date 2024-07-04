package codesquad.http;

import java.util.Arrays;

public enum ContentType {
    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    TEXT_JAVASCRIPT("text/javascript", ".js"),
    IMAGE_JPEG("image/jpeg", ".jpg"),
    IMAGE_PNG("image/png", ".png"),
    IMAGE_GIF("image/gif", ".gif"),
    IMAGE_SVG("image/svg+xml", ".svg"),
    IMAGE_ICO("image/x-icon", ".ico"),
    APPLICATION_JSON("application/json", ".json"),
    APPLICATION_XML("application/xml", ".xml"),
    APPLICATION_XHTML("application/xhtml+xml", ".xhtml"),
    APPLICATION_OCTET_STREAM("application/octet-stream", ".bin");

    private final String type;
    private final String ext;

    ContentType(final String type, final String ext) {
        this.type = type;
        this.ext = ext;
    }

    public static ContentType of(final String ext) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.ext.equals(ext))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 확장자입니다."));
    }

    public String getType() {
        return type;
    }

}
