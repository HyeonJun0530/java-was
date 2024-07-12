package codesquad.http.message;

import codesquad.http.message.constant.HttpHeader;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static codesquad.utils.StringUtils.*;
import static java.util.stream.Collectors.joining;

public class Cookie {

    private static final String DOMAIN = "Domain";
    private static final String MAX_AGE = "Max-Age";
    private static final String PATH = "Path";
    private static final String DEFAULT_DOMAIN = "localhost";
    private static final String DEFAULT_PATH = "/";

    private final String name;
    private final String value;
    private final Map<String, String> attributes;

    private Cookie(final String name, final String value, final Map<String, String> attributes) {
        this.name = name;
        this.value = value;
        this.attributes = attributes;
    }

    public Cookie(final String name, final String value) {
        Map<String, String> attributes = new HashMap<>();
        setDefaultAttributes(attributes);
        this.name = name;
        this.value = value;
        this.attributes = attributes;
    }

    public static List<Cookie> of(final String headerValue) {
        return Arrays.stream(headerValue.split(SEMICOLON))
                .map(cookieValue -> {
                    String[] cookiePair = cookieValue.split(EQUAL);
                    if (cookiePair.length != 2) {
                        return null;
                    }
                    Cookie cookie = new Cookie(cookiePair[0].trim(), cookiePair[1].trim(), new HashMap<>());
                    setDefaultAttributes(cookie.attributes);
                    return cookie;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static byte[] getCookiesBytes(final List<Cookie> cookies) throws UnsupportedEncodingException {
        return cookies.stream()
                .map(cookie -> HttpHeader.SET_COOKIE.getHeaderName() + COLON + SPACE + cookie.formatWithAttributes())
                .collect(joining(NEW_LINE)).getBytes(DECODING_CHARSET);
    }

    public void setMaxAge(long maxAge) {
        attributes.put(MAX_AGE, String.valueOf(maxAge));
    }

    public void setPath(String path) {
        attributes.put(PATH, path);
    }

    public void setDomain(String domain) {
        attributes.put(DOMAIN, domain);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String formatWithAttributes() {
        return format() + SEMICOLON + SPACE + attributes.entrySet().stream()
                .map(entry -> entry.getKey() + EQUAL + entry.getValue())
                .collect(joining(SEMICOLON + SPACE));
    }

    @Override
    public String toString() {
        return formatWithAttributes();
    }

    private String format() {
        return name + EQUAL + value;
    }

    private static void setDefaultAttributes(final Map<String, String> attributes) {
        attributes.put(PATH, DEFAULT_PATH);
    }


}
