package codesquad.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpResponse<T> {

    private static final String NEW_LINE_LETTER = "\r\n";
    private static final String BLANK_LETTER = " ";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final HttpHeader header;
    private final T body;

    public HttpResponse(final String httpVersion, final HttpStatus httpStatus, final HttpHeader header, final T body) {
        if (httpVersion != null) this.httpVersion = httpVersion;
        else this.httpVersion = DEFAULT_HTTP_VERSION;
        this.httpStatus = httpStatus;
        this.header = header;
        this.body = body;
    }

    public static <T> HttpResponse<T> of(HttpStatus httpStatus, T body) {
        if (httpStatus.is4xxClientError() || httpStatus.is5xxServerError())
            return new HttpResponse<>(DEFAULT_HTTP_VERSION, httpStatus, HttpHeader.error(), body);

        if (httpStatus.is2xxSuccessful()) {
            return ok(body);
        }

        return new HttpResponse<>(DEFAULT_HTTP_VERSION, httpStatus, HttpHeader.of(httpStatus, body), body);
    }

    public static <T> HttpResponse<T> ok(T body) {
        return new HttpResponse<>(DEFAULT_HTTP_VERSION, HttpStatus.OK, HttpHeader.of(HttpStatus.OK, body), body);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        // 상태 라인 추가
        response.append(httpVersion).append(BLANK_LETTER)
                .append(httpStatus.value()).append(BLANK_LETTER)
                .append(httpStatus.getReasonPhrase()).append(NEW_LINE_LETTER);

        // 헤더 추가
        response.append(header.toString()).append(NEW_LINE_LETTER);

        // 빈 줄 추가 (헤더와 본문을 구분)
        response.append(NEW_LINE_LETTER);

        // 본문 추가
        if (body != null) {
            if (body instanceof String) {
                response.append((String) body);
            } else if (body instanceof File) {
                try (InputStream inputStream = new FileInputStream((File) body)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        response.append(new String(buffer, 0, bytesRead));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read file body", e);
                }
            }
        }

        return response.toString();
    }
}
