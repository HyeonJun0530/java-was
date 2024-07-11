package codesquad.http.message.response;

public class ResponseBody {

    private final byte[] body;

    private ResponseBody(final byte[] body) {
        this.body = body;
    }

    public static ResponseBody from(final byte[] body) {
        return new ResponseBody(body);
    }

    public byte[] getBytes() {
        return body;
    }


    @Override
    public String toString() {
        return new String(body);
    }
}
