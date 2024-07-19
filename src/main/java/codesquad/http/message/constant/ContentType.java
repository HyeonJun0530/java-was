package codesquad.http.message.constant;

import java.util.Arrays;

public enum ContentType {
    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    TEXT_JAVASCRIPT("text/javascript", ".js"),
    TEXT_PLAIN("text/plain", ".txt"),
    IMAGE_JPG("image/jpeg", ".jpg"),
    IMAGE_JPEG("image/jpeg", ".jpeg"),
    IMAGE_PNG("image/png", ".png"),
    IMAGE_GIF("image/gif", ".gif"),
    IMAGE_SVG("image/svg+xml", ".svg"),
    IMAGE_ICO("image/x-icon", ".ico"),
    APPLICATION_JSON("application/json", ".json"),
    APPLICATION_FORM("application/x-www-form-urlencoded", ".form"),
    APPLICATION_XML("application/xml", ".xml"),
    APPLICATION_XHTML("application/xhtml+xml", ".xhtml"),
    APPLICATION_OCTET_STREAM("application/octet-stream", ".bin"),
    APPLICATION_PDF("application/pdf", ".pdf"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js"),
    MULTI_PART_FORM("multipart/form-data", ".form");

    private final String type;
    private final String ext;

    ContentType(final String type, final String ext) {
        this.type = type;
        this.ext = ext;
    }

    public static ContentType from(final String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf("."));

        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.ext.equals(ext))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 확장자입니다."));
    }

    public static ContentType getContentType(final String type) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 확장자입니다."));
    }

    public static boolean isImage(final String contentType) {
        String lowerCase = contentType.toLowerCase();
        return lowerCase.equals(IMAGE_JPEG.type) || lowerCase.equals(IMAGE_PNG.type)
                || lowerCase.equals(IMAGE_GIF.type) || lowerCase.equals(IMAGE_SVG.type)
                || lowerCase.equals(IMAGE_ICO.type);
    }

    public boolean isStatic() {
        return this == TEXT_HTML || this == TEXT_CSS
                || this == TEXT_JAVASCRIPT || this == IMAGE_JPEG
                || this == IMAGE_PNG || this == IMAGE_GIF
                || this == IMAGE_SVG || this == IMAGE_ICO;
    }

    public String getType() {
        return type;
    }

}
