package codesquad.http;

import codesquad.http.adapter.HttpResponseAdapter;
import codesquad.http.adapter.TemplateAdapter;
import codesquad.http.exception.HttpException;
import codesquad.http.handler.ApiHandler;
import codesquad.http.handler.HttpRequestHandler;
import codesquad.http.handler.StaticHandler;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DispatcherServlet {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private static final List<HttpRequestHandler> handlers = List.of(
            new ApiHandler(),
            new StaticHandler()
    );

    private static final List<HttpResponseAdapter> adapters = List.of(
            new TemplateAdapter()
    );

    public static HttpResponse service(HttpRequest request) {
        try {
            Object response = handlers.stream()
                    .filter(handler -> handler.isSupport(request))
                    .map(handler -> handler.handle(request))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported request: " + request));

            if (response instanceof HttpResponse) {
                return (HttpResponse) response;
            }

            return adapters.stream()
                    .filter(adapter -> adapter.isSupport(response))
                    .map(adapter -> {
                        try {
                            return adapter.adapt(response);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported response: " + response));
        } catch (HttpException e) {
            log.debug("HttpException: {}", e.getMessage());
            return HttpResponse.of(e.getContentType(), e.getHttpStatus(), e.getBody());
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return HttpResponse.internalServerError();
        }
    }

}
