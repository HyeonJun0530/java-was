package codesquad.http.message.request;

import codesquad.http.message.HttpHeaders;

import static codesquad.utils.StringUtils.NEW_LINE;

public class HttpRequest {

    private final RequestStartLine requestStartLine;
    private final HttpHeaders httpHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestStartLine requestStartLine, final HttpHeaders httpHeaders,
                       final RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final String requestMessage) {
        String[] requestLine = parseRequest(requestMessage);
        RequestStartLine requestStartLine = RequestStartLine.from(requestLine[0]);
        HttpHeaders httpHeaders = HttpHeaders.from(requestLine[1]);

        if (requestLine[2] == null) {
            return new HttpRequest(requestStartLine, httpHeaders, null);
        }

        RequestBody requestBody = RequestBody.from(requestLine[2]);

        return new HttpRequest(requestStartLine, httpHeaders, requestBody);
    }

    public static String[] parseRequest(final String requestMessage) {
        // HTTP 요청 메시지에서 헤더와 바디를 구분하는 인덱스를 찾습니다.
        int headerBodySeparatorIndex = requestMessage.indexOf(NEW_LINE + NEW_LINE);

        String headersPart; // 헤더 부분을 저장할 변수
        String bodyPart = null; // 바디 부분을 저장할 변수, 기본값은 null

        // 바디가 없는 경우 (헤더와 바디를 구분하는 빈 줄이 없는 경우)
        if (headerBodySeparatorIndex == -1) {
            // 첫 번째 줄바꿈 이후부터 모든 텍스트는 헤더로 간주합니다.
            headersPart = requestMessage.substring(requestMessage.indexOf(NEW_LINE) + 2);
        } else {
            // 헤더와 바디를 구분하는 빈 줄이 있는 경우, 헤더와 바디를 분리합니다.
            headersPart = requestMessage.substring(requestMessage.indexOf(NEW_LINE) + 2, headerBodySeparatorIndex);
            bodyPart = requestMessage.substring(headerBodySeparatorIndex + 4);
        }

        // 요청의 시작 줄을 추출합니다. (첫 번째 줄바꿈까지가 시작 줄입니다.)
        String startLine = requestMessage.substring(0, requestMessage.indexOf(NEW_LINE));

        return new String[]{startLine, headersPart, bodyPart};
    }


    public RequestStartLine getRequestStartLine() {
        return requestStartLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

}
