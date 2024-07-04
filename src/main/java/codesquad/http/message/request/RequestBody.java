package codesquad.http.message.request;

public class RequestBody {

    private final String body;

    private RequestBody(final String body) {
        this.body = body;
    }

    public static RequestBody from(final String body) {
        return new RequestBody(body);
    }

    public String getBody() {
        return body;
    }

}
