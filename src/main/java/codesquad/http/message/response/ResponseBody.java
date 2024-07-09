package codesquad.http.message.response;

public class ResponseBody {

    private final byte[] body;

    private ResponseBody(final byte[] body) {
        this.body = body;
    }

    public static <T> ResponseBody from(final T body) {
        if (body instanceof String) {
            return new ResponseBody(body.toString().getBytes());
        } else if (body instanceof byte[]) {
            return new ResponseBody((byte[]) body);
        }

        return null;
    }

    public byte[] getBytes() {
        return body;
    }


    @Override
    public String toString() {
        return "ResponseBody{" +
                "body=" + new String(body) +
                '}';
    }
}
