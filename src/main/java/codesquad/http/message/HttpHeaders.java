package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;
import codesquad.http.message.response.ResponseBody;
import codesquad.utils.HttpMessageUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static codesquad.utils.StringUtils.COLON;
import static codesquad.utils.StringUtils.NEW_LINE;
import static java.util.stream.Collectors.joining;


public class HttpHeaders {

    private static final String DEFAULT_SERVER_NAME = "Hyn_053 Server";

    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders newInstance() {
        Map<String, String> headers = new HashMap<>();
        addResponseDefaultHeaders(headers);
        headers.put(HttpHeader.CONTENT_LENGTH.getHeaderName(), "0");

        return new HttpHeaders(headers);
    }

    public static HttpHeaders from(final BufferedReader reader) throws IOException {
        Map<String, String> headers = parseHeaders(reader);
        addResponseDefaultHeaders(headers);
        return new HttpHeaders(headers);
    }

    public static HttpHeaders of(final ContentType contentType, final ResponseBody body) {
        Map<String, String> headers = new HashMap<>();
        addResponseDefaultHeaders(headers);

        headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(), contentType.getType());
        headers.put(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(body.getBytes().length));

        return new HttpHeaders(headers);
    }

    public static HttpHeaders of(final String location) {
        HttpHeaders httpHeaders = newInstance();
        httpHeaders.headers.put(HttpHeader.LOCATION.getHeaderName(), location);

        return httpHeaders;
    }

    private static void addResponseDefaultHeaders(final Map<String, String> headers) {
        headers.put(HttpHeader.SERVER.getHeaderName(), DEFAULT_SERVER_NAME);
        headers.put(HttpHeader.DATE.getHeaderName(), HttpMessageUtils.getCurrentTime());
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            URLDecoder.decode(line, StandardCharsets.UTF_8);
            String[] headerTokens = line.split(COLON);
            headers.put(headerTokens[0].trim(), headerTokens[1].trim());
        }

        return headers;
    }

    private static String formatHeaders(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + COLON + entry.getValue())
                .collect(joining(NEW_LINE));
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }

    public byte[] getBytes() {
        return formatHeaders(headers).getBytes();
    }

    @Override
    public String toString() {
        return formatHeaders(headers);
    }

}
