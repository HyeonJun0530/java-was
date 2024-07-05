package codesquad.http.message;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;
import codesquad.http.message.response.ResponseBody;
import codesquad.utils.HttpMessageUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static codesquad.utils.StringUtils.COLON;
import static codesquad.utils.StringUtils.NEW_LINE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;


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

    public static HttpHeaders from(final String headerMessage) {
        Map<String, String> headers = parseHeaders(headerMessage);
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

    private static Map<String, String> parseHeaders(final String headers) {
        return Arrays.stream(headers.split(NEW_LINE))
                .map(header -> header.split(COLON, 2))
                .collect(toMap(
                        headerKeyValue -> headerKeyValue[0].trim(),
                        headerKeyValue -> headerKeyValue[1].trim()
                ));
    }

    private static String formatHeaders(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + COLON + entry.getValue())
                .collect(joining(NEW_LINE));
    }

    public byte[] getBytes() {
        return formatHeaders(headers).getBytes();
    }

    @Override
    public String toString() {
        return formatHeaders(headers);
    }

}
