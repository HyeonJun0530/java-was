package codesquad.http.message.request;

import java.util.Arrays;
import java.util.Map;

import static codesquad.utils.StringUtils.EMPTY;
import static codesquad.utils.StringUtils.EQUAL;
import static java.util.stream.Collectors.toMap;

public class QueryString {

    private static final String QUERY_STRING_DELIMITER = "&";

    private final Map<String, String> parameters;

    private QueryString(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static QueryString empty() {
        return new QueryString(Map.of());
    }

    public static QueryString from(final String queryString) {
        return new QueryString(parseQueryString(queryString));
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        return Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(kv -> kv.split(EQUAL))
                .collect(toMap(kv -> kv[0], kv -> kv.length > 1 ? kv[1] : EMPTY));
    }

    public Map<String, String> getQueryString() {
        return parameters;
    }

}
