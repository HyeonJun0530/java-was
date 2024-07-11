package codesquad.http.handler;

import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

public class HttpRequestHandler {

    private HttpRequestHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        if (ApiHandler.isApiRequest(httpRequest)) {
            return ApiHandler.handle(httpRequest);
        }

        return StaticHandler.handle(httpRequest);
    }
}
