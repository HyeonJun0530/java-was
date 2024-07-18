package codesquad.http;

import codesquad.http.filter.FilterChain;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static codesquad.utils.StringUtils.NEW_LINE;

public class HttpProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);
    private final Socket connection;
    private final FilterChain filterChain;
    private final DispatcherServlet dispatcherServlet;

    public HttpProcessor(final Socket connection, final FilterChain filterChain,
                         final DispatcherServlet dispatcherServlet) {
        this.connection = connection;
        this.filterChain = filterChain;
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    public void run() {
        try {
            log.debug("Client connected");

            try (InputStream inputStream = connection.getInputStream();
                 OutputStream client = connection.getOutputStream()) {

                HttpRequest request = HttpRequest.from(inputStream);
                log.debug("Request: {}", request);

                HttpResponse response = HttpResponse.empty();
                filterChain.doFilter(request, response);

                if (response.hasMessage()) {
                    log.debug("Response: {}", response);
                    write(response, client);
                    return;
                }

                HttpResponse httpResponse = dispatcherServlet.service(request);
                log.debug("Response: {}", httpResponse);

                write(httpResponse, client);
            }
        } catch (IOException e) {
            log.error("Error handling client connection = {}", e);
        } catch (Exception e) {
            log.error("Error processing client request", e);
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
        out.write(NEW_LINE.getBytes(DECODING_CHARSET));
    }

}
