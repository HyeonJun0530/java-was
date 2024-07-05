package codesquad.http.message.response;

import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.HttpStatus;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final HttpHeaders header;
    private final ResponseBody body;

    private HttpResponse(final ResponseLine responseLine, final HttpHeaders header, final ResponseBody body) {
        this.responseLine = responseLine;
        this.header = header;
        this.body = body;
    }

    public static <T> HttpResponse of(final String httpVersion, final HttpStatus httpStatus, final T body) {
        ResponseBody responseBody = ResponseBody.from(body);
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatus), HttpHeaders.of(responseBody), responseBody);
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
        return "HttpResponse{" +
                "responseLine=" + responseLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
