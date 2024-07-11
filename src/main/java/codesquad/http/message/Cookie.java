package codesquad.http.message;

import codesquad.http.message.constant.HttpHeader;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    String[] cookiePair = cookieValue.split("=");
                    Cookie cookie = new Cookie(cookiePair[0].trim(), cookiePair[1].trim(), new HashMap<>());
                    setDefaultAttributes(cookie.attributes);
                    return cookie;
                }).toList();
    }

    public static byte[] getCookiesBytes(final List<Cookie> cookies) {
        return cookies.stream()
                .map(cookie -> HttpHeader.SET_COOKIE.getHeaderName() + COLON + SPACE + cookie.format())
                .collect(joining(NEW_LINE)).getBytes(StandardCharsets.UTF_8);
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

    public String format() {
        return name + EQUAL + value;
    }

    @Override
    public String toString() {
        return format();
    }

    private static void setDefaultAttributes(final Map<String, String> attributes) {
        attributes.put(PATH, DEFAULT_PATH);
    }


}
