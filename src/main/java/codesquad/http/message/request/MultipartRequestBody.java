package codesquad.http.message.request;

import codesquad.http.exception.BadRequestException;
import codesquad.http.message.HttpHeaders;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpHeader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codesquad.utils.StringUtils.NEW_LINE;

public class MultipartRequestBody extends RequestBody {

    private final HttpHeaders httpHeaders;

    private MultipartRequestBody(final byte[] body, final HttpHeaders httpHeaders) {
        super(body);
        this.httpHeaders = httpHeaders;
    }

    public static MultipartRequestBody from(final InputStream reader, final int contentLength, final HttpHeaders httpHeaders) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(reader);
        byte[] read = new byte[contentLength];

        bufferedInputStream.readNBytes(read, 0, contentLength);

        return new MultipartRequestBody(read, httpHeaders);
    }

    public static List<byte[]> splitBodyIntoParts(byte[] body, String boundaryString) {
        String modifiedBoundaryString = "--" + boundaryString;
        byte[] boundary = modifiedBoundaryString.getBytes();
        byte[] endBoundary = (modifiedBoundaryString + "--").getBytes(); // End of multipart marker
        List<byte[]> parts = new ArrayList<>();

        int start = 0;
        for (int i = 0; i <= body.length - boundary.length; i++) {
            if (Arrays.equals(Arrays.copyOfRange(body, i, i + boundary.length), boundary)) {
                if (i + boundary.length < body.length && body[i + boundary.length] == '-' && body[i + boundary.length + 1] == '-') {
                    break;
                }
                if (start < i) {
                    byte[] part = Arrays.copyOfRange(body, start, i);
                    parts.add(part);
                }
                i += boundary.length - 1;
                start = i + 1;
            }
        }

        if (start < body.length) {
            byte[] part = Arrays.copyOfRange(body, start, body.length);
            if (part.length > 2 && part[part.length - 2] == '-' && part[part.length - 1] == '-') {
                part = Arrays.copyOf(part, part.length - 2);
            }
            parts.add(part);
        }

        return parts;
    }

    public List<Part> parseMultipart(final HttpHeaders httpHeaders) {
        String contentType = httpHeaders.getHeader(HttpHeader.CONTENT_TYPE.getHeaderName());
        if (!contentType.contains(ContentType.MULTI_PART_FORM.getType())) {
            throw new BadRequestException("Content-Type이 multipart/form-data가 아닙니다.");
        }

        String boundary = httpHeaders.getHeader(HttpHeader.CONTENT_TYPE.getHeaderName())
                .split("boundary=")[1];

        List<byte[]> bytes = splitBodyIntoParts(body, boundary);
        List<Part> parts = new ArrayList<>();
        for (byte[] part : bytes) {
            parts.add(Part.of(part));
        }

        return parts;
    }

    @Override
    public String toString() {
        return parseMultipart(httpHeaders).toString();
    }

    public static class Part {
        private final String name;
        private final String fileName;
        private final String contentType;
        private final String contentDisposition;
        private final byte[] body;

        public Part(final String name, final String fileName, final String contentType, final String contentDisposition, final byte[] body) {
            this.name = name;
            this.fileName = fileName;
            this.contentType = contentType;
            this.contentDisposition = contentDisposition;
            this.body = body;
        }

        public static Part of(final byte[] body) {
            // Split headers and body
            int headerEndIndex = findHeaderEndIndex(body);
            if (headerEndIndex == -1) {
                throw new BadRequestException("Header가 존재하지 않습니다.");
            }
            String headerPart = new String(body, 0, headerEndIndex);
            byte[] contentBytes = Arrays.copyOfRange(body, headerEndIndex + NEW_LINE.length() * 2, body.length);

            // Parse headers
            String name = extractValue(headerPart, "name=\"", "\"");
            String filename = extractValue(headerPart, "filename=\"", "\"");
            String contentType = extractValue(headerPart, "Content-Type: ", NEW_LINE);

            return new Part(name, filename, contentType, headerPart, contentBytes);
        }

        private static int findHeaderEndIndex(byte[] body) {
            String doubleNewLine = NEW_LINE + NEW_LINE;
            byte[] doubleNewLineBytes = doubleNewLine.getBytes();

            for (int i = 0; i < body.length - doubleNewLineBytes.length; i++) {
                boolean match = true;
                for (int j = 0; j < doubleNewLineBytes.length; j++) {
                    if (body[i + j] != doubleNewLineBytes[j]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return i;
                }
            }
            return -1;
        }

        private static String extractValue(String source, String startDelimiter, String endDelimiter) {
            int start = source.indexOf(startDelimiter) + startDelimiter.length();
            int end = source.indexOf(endDelimiter, start);
            if (start < startDelimiter.length() || end == -1) {
                return null;
            }
            return source.substring(start, end);
        }


        public String getName() {
            return name;
        }

        public String getContentType() {
            return contentType;
        }

        public String getContentDisposition() {
            return contentDisposition;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "Part{" +
                    "name='" + name + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", contentDisposition='" + contentDisposition + '\'' +
                    ", body=" + Arrays.toString(body) +
                    '}';
        }
    }
}
