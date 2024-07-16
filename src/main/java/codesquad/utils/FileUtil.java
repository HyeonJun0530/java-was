package codesquad.utils;

import codesquad.http.exception.InternalServerException;
import codesquad.http.exception.NotFoundException;
import codesquad.http.handler.StaticHandler;

import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    public static byte[] getStaticFile(final String path) {
        ClassLoader classLoader = StaticHandler.class.getClassLoader();
        String resourcePath = "static" + path;

        try (InputStream resourceAsStream = classLoader.getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new NotFoundException("Static file not found");
            }

            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public static byte[] getTemplateFile(final String path) {
        ClassLoader classLoader = StaticHandler.class.getClassLoader();
        String resourcePath = "templates" + path;

        try (InputStream resourceAsStream = classLoader.getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new NotFoundException("Template file not found");
            }

            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
