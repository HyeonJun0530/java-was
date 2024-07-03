package codesquad.http.message;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final HttpHeaders httpHeaders;


    private HttpRequest(final String method, final String path, final String version, final HttpHeaders httpHeaders) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.httpHeaders = httpHeaders;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        String requestLine = readRequestLine(reader);
        String[] requestParts = parseRequestLine(requestLine);
        HttpHeaders httpHeaders = HttpHeaders.from(reader);

        return new HttpRequest(requestParts[0], requestParts[1], requestParts[2], httpHeaders);
    }

    private static String readRequestLine(final BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid HTTP request line");
        }
        return requestLine;
    }

    private static String[] parseRequestLine(final String requestLine) throws IOException {
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            throw new IOException("Invalid HTTP request line format");
        }
        return requestParts;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHttpHeader() {
        return httpHeaders;
    }
}
