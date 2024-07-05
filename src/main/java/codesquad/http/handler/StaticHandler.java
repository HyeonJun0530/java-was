package codesquad.http.handler;

import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class StaticHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticHandler.class);
    private static final String BASE_DIRECTORY = "src/main/resources/static";

    private StaticHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        try {
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(),
                    HttpStatus.OK,
                    getFile(httpRequest.getRequestStartLine().getPath()));
        } catch (IllegalArgumentException e) {
            log.error("Static file not found", e);
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
        }
    }

    private static File getFile(final String path) {
        String staticPath = BASE_DIRECTORY + path;

        File file = new File(staticPath);

        if (file.isDirectory()) {
            file = new File(staticPath + "/index.html");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("Not found static file");
        }

        return file;
    }

    //TODO: 동적으로 받을 수 있지 않을까?
//    private Map<String, String> getStaticFiles() {
//        Map<String, String> filesMap = new HashMap<>();
//        URL directoryUrl = getClass().getClassLoader().getResource(BASE_DIRECTORY);
//        File directory = new File(requireNonNull(directoryUrl).getPath());
//        if (directory.exists() && directory.isDirectory()) {
//            addFilesRecursively(directory, filesMap, BASE_DIRECTORY);
//        }
//        return Collections.unmodifiableMap(filesMap);
//    }
//
//    private void addFilesRecursively(File directory, Map<String, String> filesMap, String basePath) {
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    addFilesRecursively(file, filesMap, "/" + file.getName());
//                } else {
//                    String relativePath = basePath + "/" + file.getName();
//                    filesMap.put(relativePath, relativePath);
//                }
//            }
//        }
//    }
}
