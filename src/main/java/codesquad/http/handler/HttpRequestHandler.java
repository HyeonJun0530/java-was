package codesquad.http.handler;

import codesquad.http.message.request.HttpRequest;


//TODO: 추상화
public interface HttpRequestHandler {

    Object handle(HttpRequest request);

    boolean isSupport(HttpRequest request);
}
