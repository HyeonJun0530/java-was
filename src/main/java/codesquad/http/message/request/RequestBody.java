package codesquad.http.message.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;
import static codesquad.utils.StringUtils.*;
import static java.util.stream.Collectors.toMap;

public class RequestBody {

    protected final byte[] body;

    protected RequestBody(final byte[] body) {
        this.body = body;
    }

    public static RequestBody from(final InputStream reader, final int contentLength) throws IOException {
        byte[] read = new byte[contentLength];
        reader.read(read);

        return new RequestBody(read);
    }

    public String getBody() {
        try {
            String body = new String(this.body);

            return URLDecoder.decode(body, DECODING_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return getBody();
    }

    public Map<String, String> parseFormUrlEncoded() {
        return Arrays.stream(getBody().split(AND))
                .map(kv -> kv.split(EQUAL))
                .collect(toMap(kv -> kv[0], kv -> kv.length > 1 ? kv[1] : EMPTY));
    }

}
