package codesquad.http.handler;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StaticHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);
    public static final List<String> staticExtension = List.of(".css", ".js", ".ico", ".png", ".jpg", ".jpeg", ".gif", ".svg");

    @Override
    public Object handle(final HttpRequest httpRequest) {
        try {
            String path = httpRequest.getRequestStartLine().getPath();

            byte[] staticFiles = getStaticFiles(path);

            return HttpResponse.of(ContentType.from(path),
                    httpRequest.getRequestStartLine().getProtocol(),
                    HttpStatus.OK,
                    staticFiles);
        } catch (IllegalArgumentException e) {
            log.error("Static file not found", e);
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public boolean isSupport(final HttpRequest request) {
        return staticExtension.stream().anyMatch(request.getRequestStartLine().getPath()::endsWith);
    }

    public static byte[] getStaticFiles(final String path) {
        ClassLoader classLoader = StaticHandler.class.getClassLoader();
        String resourcePath = "static" + path;

        try (InputStream resourceAsStream = classLoader.getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Static file not found");
            }

            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
