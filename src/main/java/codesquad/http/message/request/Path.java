package codesquad.http.message.request;

public class Path {

    private static final String QUERY_STRING_DELIMITER = "\\?";

    private final String path;
    private final QueryString queryString;

    private Path(final String path, final QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static Path from(final String path) {
        String[] pathTokens = path.split(QUERY_STRING_DELIMITER);
        validatePath(pathTokens[0]);

        if (pathTokens.length == 1) {
            return new Path(pathTokens[0], QueryString.empty());
        }

        QueryString queryString = QueryString.from(pathTokens[1]);
        return new Path(pathTokens[0], queryString);
    }

    private static void validatePath(final String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 경로입니다.");
        }
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    @Override
    public String toString() {
        return path + queryString.toString();
    }

}
