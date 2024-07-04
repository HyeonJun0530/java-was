package codesquad.http.message.response;

import codesquad.http.message.constant.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ResponseBody {

    private final ContentType contentType;
    private final byte[] body;

    private ResponseBody(final ContentType contentType, final byte[] body) {
        this.contentType = contentType;
        this.body = body;
    }

    public static <T> ResponseBody from(final T body) {
        if (body instanceof String) {
            return new ResponseBody(ContentType.APPLICATION_JSON, body.toString().getBytes());
        } else if (body instanceof File) {
            try {
                File file = (File) body;
                return new ResponseBody(ContentType.from(file.getName()), handleFile(file));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read file", e);
            }
        }

        return null;
    }

    private static byte[] handleFile(final File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            int bytesRead = fis.read(fileBytes);
            if (bytesRead != fileBytes.length) {
                throw new IOException("Could not read the entire file");
            }

            return fileBytes;
        }
    }

    public byte[] getBytes() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "body=" + new String(body) +
                '}';
    }
}
