package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class HttpHeaders {

    private static final String NEW_LINE_LETTER = "\r\n";
    private static final String COLON_LETTER = ":";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders error() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.APPLICATION_JSON.getType());
        headers.put("Content-Length", "0");

        return new HttpHeaders(headers);
    }

    public static HttpHeaders from(final BufferedReader reader) throws IOException {
        return new HttpHeaders(parseHeaders(reader));
    }

    public static <T> HttpHeaders of(final HttpStatus status, final T body) {
        Map<String, String> headers = new HashMap<>();
        if (body instanceof String) {
            String bodyStr = (String) body;
            headers.put("Content-Type", ContentType.APPLICATION_JSON.getType());
            headers.put("Content-Length", String.valueOf(bodyStr.getBytes().length));
        } else if (body instanceof File) {
            File bodyFile = (File) body;
            headers.put("Content-Type", getContentType(bodyFile.getName()));
            headers.put("Content-Length", String.valueOf(bodyFile.length()));
        } else {
            throw new IllegalArgumentException("Unsupported body type");
        }

        return new HttpHeaders(headers);
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(COLON_LETTER, 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].toLowerCase(), headerParts[1].trim());
            }
        }

        return headers;
    }

    private static String formatHeaders(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + COLON_LETTER + entry.getValue())
                .collect(Collectors.joining(NEW_LINE_LETTER));
    }

    private static String getContentType(final String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();

        return ContentType.of(ext).getType();
    }

    @Override
    public String toString() {
        return formatHeaders(headers);
    }

}
