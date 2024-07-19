package codesquad.utils;

import codesquad.http.exception.InternalServerException;
import codesquad.http.exception.NotFoundException;
import codesquad.http.handler.StaticHandler;

import java.io.*;
import java.util.UUID;

public class FileUtil {

    public static byte[] getStaticFile(final String path) {
        ClassLoader classLoader = StaticHandler.class.getClassLoader();
        String resourcePath = "static" + path;

        try (InputStream resourceAsStream = classLoader.getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                File file = new File(path);

                if (!file.exists()) {
                    throw new NotFoundException("Static file not found");
                }
                try (FileInputStream fis = new FileInputStream(file)) {
                    return fis.readAllBytes();
                } catch (IOException e) {
                    throw new InternalServerException(e.getMessage());
                }
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

    public static String saveImage(byte[] image, String fileExt) {
        String basePath = System.getProperty("user.images.path", System.getProperty("user.home") + File.separator + "appImages");
        String imagesPath = basePath + File.separator + "images/";

        File imagesDir = new File(imagesPath);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        String imageName = UUID.randomUUID().toString() + fileExt;
        File imageFile = new File(imagesDir, imageName);
        try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(imageFile))) {
            fos.write(image);
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }

        return imageName;
    }

}
