package codesquad.http.message.response;

import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;

import static codesquad.utils.StringUtils.NEW_LINE;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final HttpHeaders header;
    private final ResponseBody body;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders header, final ResponseBody body) {
        this.responseLine = responseLine;
        this.header = header;
        this.body = body;
    }

    public static <T> HttpResponse of(final ContentType contentType, final String httpVersion, final HttpStatus httpStatus, final T body) {
        ResponseBody responseBody = ResponseBody.from(body);
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.of(contentType, responseBody), responseBody);
    }

    public static HttpResponse redirect(final String httpVersion, final HttpStatus httpStatus, final String location) {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.of(location), null);
    }

    public static HttpResponse of(final String httpVersion, final HttpStatus httpStatus) {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.newInstance(), null);
    }

    public boolean hasBody() {
        return body != null;
    }

    public byte[] getResponseLineBytes() {
        return responseLine.getBytes();
    }

    public byte[] getHeaderBytes() {
        return header.getBytes();
    }

    public byte[] getBodyBytes() {
        return body.getBytes();
    }

    @Override
    public String toString() {
        if (hasBody()) {
            return responseLine.toString() + NEW_LINE +
                    header.toString() + NEW_LINE + NEW_LINE +
                    body.toString();
        }

        return responseLine.toString() + NEW_LINE +
                header.toString() + NEW_LINE + NEW_LINE;
    }
}
