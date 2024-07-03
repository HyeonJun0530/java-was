package codesquad.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);
    private static final String BASE_DIRECTORY = "src/main/resources/static";
    private final Socket connection;

    public HttpProcessor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            log.debug("Client connected");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 OutputStream client = connection.getOutputStream()) {

                HttpRequest request = HttpRequest.from(in);
                log.debug("Request: {}", request.getHttpHeader());

                File file = getFile(request);

                HttpResponse httpResponse;
                if (file.exists()) {
                    httpResponse = HttpResponse.of(HttpStatus.OK, file);
                } else {
                    HttpStatus httpStatus = HttpStatus.NOT_FOUND;
                    httpResponse = HttpResponse.of(httpStatus, httpStatus.getReasonPhrase());
                }

                log.debug("Response: {}", httpResponse);
                write(httpResponse, client);
            }
        } catch (IOException e) {
            log.error("Error handling client connection", e);
        } catch (IllegalArgumentException e) {
            log.error("Error parsing request = {}", e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }

    }

    private File getFile(final HttpRequest request) {
        String path = request.getPath();
        return new File(BASE_DIRECTORY + path);
    }

    private void write(HttpResponse httpResponse, OutputStream out) throws IOException {
        out.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

}
