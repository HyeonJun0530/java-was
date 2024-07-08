package codesquad.http.message.constant;

import java.util.Arrays;

import static codesquad.utils.StringUtils.EMPTY;
import static codesquad.utils.StringUtils.SPACE;

public enum HttpStatus {
    CONTINUE(100, HttpStatus.Series.INFORMATIONAL, "Continue"),

    CHECKPOINT(103, HttpStatus.Series.INFORMATIONAL, "Checkpoint"),
    OK(200, HttpStatus.Series.SUCCESSFUL, "OK"),
    CREATED(201, HttpStatus.Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, HttpStatus.Series.SUCCESSFUL, "Accepted"),
    MOVED_PERMANENTLY(301, HttpStatus.Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, HttpStatus.Series.REDIRECTION, "Found"),


    BAD_REQUEST(400, HttpStatus.Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),
    FORBIDDEN(403, HttpStatus.Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, HttpStatus.Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, HttpStatus.Series.CLIENT_ERROR, "Method Not Allowed"),


    INTERNAL_SERVER_ERROR(500, HttpStatus.Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, HttpStatus.Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, HttpStatus.Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, HttpStatus.Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, HttpStatus.Series.SERVER_ERROR, "Gateway Timeout");


    private final int value;
    private final Series series;
    private final String reasonPhrase;

    private HttpStatus(final int value, final Series series, final String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus valueOf(final int statusCode) {
        return Arrays.stream(HttpStatus.values())
                .filter(status -> status.value == statusCode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + statusCode + "]"));
    }

    public int value() {
        return this.value;
    }

    public Series series() {
        return this.series;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public boolean is1xxInformational() {
        return this.series() == HttpStatus.Series.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series() == HttpStatus.Series.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series() == HttpStatus.Series.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series() == HttpStatus.Series.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series() == HttpStatus.Series.SERVER_ERROR;
    }

    public boolean isError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }

    @Override
    public String toString() {
        int var10000 = this.value;
        return EMPTY + var10000 + SPACE + this.getReasonPhrase();
    }

    public static enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        private Series(final int value) {
            this.value = value;
        }

        public static Series resolve(final int statusCode) {
            int seriesCode = statusCode / 100;
            Series[] var2 = values();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Series series = var2[var4];
                if (series.value == seriesCode) {
                    return series;
                }
            }

            return null;
        }

        public int value() {
            return this.value;
        }
    }
}
