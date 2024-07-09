package codesquad.http.handler;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StaticHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);

    private static final Map<String, String> pathMap = Map.of("/registration", "/registration/index.html",
            "/login", "/login/index.html",
            "/main", "/main/index.html",
            "/", "/index.html");


    private StaticHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        try {
            String path = httpRequest.getRequestStartLine().getPath();

            if (pathMap.containsKey(path)) {
                path = pathMap.get(path);
            }

            return HttpResponse.of(ContentType.from(path),
                    httpRequest.getRequestStartLine().getProtocol(),
                    HttpStatus.OK,
                    getStaticFiles(path));
        } catch (IllegalArgumentException e) {
            log.error("Static file not found", e);
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getStaticFiles(final String path) throws IOException {
        ClassLoader classLoader = StaticHandler.class.getClassLoader();
        String resourcePath = "static" + path;

        try (InputStream resourceAsStream = classLoader.getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Static file not found");
            }

            return resourceAsStream.readAllBytes();
        }
    }

}
