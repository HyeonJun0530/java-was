package codesquad.http.message.request;

import codesquad.http.message.constant.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static codesquad.utils.StringUtils.SPACE;

public class RequestStartLine {

    private final HttpMethod method;
    private final Path path;
    private final Protocol protocol;

    private RequestStartLine(final HttpMethod method, final Protocol protocol, final Path path) {
        this.method = method;
        this.protocol = protocol;
        this.path = path;
    }

    public static RequestStartLine from(final BufferedReader reader) throws IOException {
        String[] startLineTokens = URLDecoder.decode(reader.readLine(), StandardCharsets.UTF_8).split(SPACE);

        HttpMethod method = HttpMethod.from(startLineTokens[0]);
        Path path = Path.from(startLineTokens[1]);
        Protocol protocol = Protocol.from(startLineTokens[2]);

        return new RequestStartLine(method, protocol, path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getPath();
    }

    public Map<String, String> getQueryString() {
        return path.getQueryString().getQueryString();
    }

    public String getProtocol() {
        return protocol.getVersion();
    }

    @Override
    public String toString() {
        return "RequestStartLine{" +
                "method=" + method +
                ", path=" + path +
                ", protocol=" + protocol +
                '}';
    }

}
