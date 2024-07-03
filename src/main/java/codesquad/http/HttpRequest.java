package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final HttpHeader httpHeader;

    private HttpRequest(final String method, final String path, final String version, final HttpHeader httpHeader) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.httpHeader = httpHeader;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        String requestLine = readRequestLine(reader);
        String[] requestParts = parseRequestLine(requestLine);
        HttpHeader httpHeader = HttpHeader.from(reader);

        return new HttpRequest(requestParts[0], requestParts[1], requestParts[2], httpHeader);
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

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }
}
