package codesquad.http.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static codesquad.utils.StringUtils.EMPTY;
import static codesquad.utils.StringUtils.EQUAL;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    private final String body;

    private RequestBody(final String body) {
        this.body = body;
    }

    public static RequestBody from(final BufferedReader reader, final int contentLength) throws IOException {
        char[] read = new char[contentLength];
        reader.read(read);

        String body = new String(read);
        String decode = URLDecoder.decode(body, StandardCharsets.UTF_8);

        return new RequestBody(decode);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "RequestBody{" +
                "body='" + body + '\'' +
                '}';
    }

    public Map<String, String> parseFormUrlEncoded() {
        return Arrays.stream(this.body.split("&"))
                .map(kv -> kv.split(EQUAL))
                .collect(toMap(kv -> kv[0], kv -> kv.length > 1 ? kv[1] : EMPTY));

    }
}
