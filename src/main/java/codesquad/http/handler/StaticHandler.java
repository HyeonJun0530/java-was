package codesquad.http.handler;

import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static codesquad.utils.FileUtil.getStaticFile;

public class StaticHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);
    public static final List<String> staticExtension = List.of(".css", ".js", ".ico", ".png", ".jpg", ".jpeg", ".gif", ".svg");

    @Override
    public Object handle(final HttpRequest httpRequest) {
        try {
            String path = httpRequest.getRequestStartLine().getPath();

            byte[] staticFiles = getStaticFile(path);

            return HttpResponse.of(ContentType.from(path),
                    HttpStatus.OK,
                    staticFiles);
        } catch (IllegalArgumentException e) {
            log.error("Static file not found", e);
            return HttpResponse.notFound();
        }
    }

    @Override
    public boolean isSupport(final HttpRequest request) {
        return staticExtension.stream().anyMatch(request.getRequestStartLine().getPath()::endsWith);
    }

}
