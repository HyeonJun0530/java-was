package codesquad.http;

import codesquad.http.handler.HttpRequestHandler;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static codesquad.utils.StringUtils.NEW_LINE;

public class HttpProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);
    private final Socket connection;

    public HttpProcessor(final Socket connection) {
        this.connection = connection;
    }

    private static String parseBufferedReaderToString(final BufferedReader reader) {
        StringBuilder requestMessage = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                requestMessage.append(line).append(NEW_LINE);
                if (line.isEmpty()) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error reading request", e);
        }
        return requestMessage.toString();
    }

    @Override
    public void run() {
        try {
            log.debug("Client connected");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 OutputStream client = connection.getOutputStream()) {

                String requestMessage = parseBufferedReaderToString(in);

                HttpRequest request = HttpRequest.from(requestMessage);
                log.debug("Request: {}", request);

                HttpResponse httpResponse = HttpRequestHandler.handle(request);
                log.debug("Response: {}", httpResponse);

                write(httpResponse, client);
            }
        } catch (IOException e) {
            log.error("Error handling client connection = {}", e);
        } catch (Exception e) {
            log.error("Error parsing request = {}", e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }

    }

    private void write(final HttpResponse httpResponse, final OutputStream out) throws IOException {
        if (httpResponse.hasBody()) {
            out.write(httpResponse.getResponseLineBytes());
            writeNewLine(out);
            out.write(httpResponse.getHeaderBytes());
            writeNewLine(out);
            writeNewLine(out);
            out.write(httpResponse.getBodyBytes());
            out.flush();
            return;
        }

        out.write(httpResponse.getResponseLineBytes());
        writeNewLine(out);
        out.write(httpResponse.getHeaderBytes());
        writeNewLine(out);
        out.flush();
    }

    private void writeNewLine(final OutputStream out) throws IOException {
        out.write(NEW_LINE.getBytes(StandardCharsets.UTF_8));
    }

}
