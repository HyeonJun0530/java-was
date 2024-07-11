package codesquad.utils;

import codesquad.http.handler.StaticHandler;

import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

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
