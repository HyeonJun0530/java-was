package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;
import codesquad.http.message.response.ResponseBody;
import codesquad.utils.HttpMessageUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static codesquad.utils.StringUtils.*;
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

        String type = headers.get(HttpHeader.CONTENT_TYPE.getHeaderName());
        if (type != null) {
            headers.put(HttpHeader.CONTENT_TYPE.getHeaderName(),
                    ContentType.getContentType(type).getType() + "; charset=" + DECODING_CHARSET);
        }
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            URLDecoder.decode(line, DECODING_CHARSET);
            String[] headerTokens = line.split(COLON, 2);
            headers.put(headerTokens[0].trim(), headerTokens[1].trim());
        }

        return headers;
    }

    public List<Cookie> getCookies() {
        String cookie = headers.get(HttpHeader.COOKIE.getHeaderName());

        if (cookie == null) {
            return Collections.unmodifiableList(new ArrayList<>());
        }

        return Cookie.of(cookie);
    }


    public String getHeader(final String key) {
        return headers.get(key);
    }

    public byte[] getBytes() throws UnsupportedEncodingException {
        return formatHeaders(headers).getBytes(DECODING_CHARSET);
    }

    @Override
    public String toString() {
        return formatHeaders(headers);
    }

    private String formatHeaders(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + COLON + SPACE + entry.getValue())
                .collect(joining(NEW_LINE));
    }

}
