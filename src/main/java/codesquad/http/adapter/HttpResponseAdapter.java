package codesquad.http.adapter;

import codesquad.http.message.response.HttpResponse;

import java.io.UnsupportedEncodingException;

public interface HttpResponseAdapter {
    HttpResponse adapt(Object response) throws UnsupportedEncodingException;

    boolean isSupport(Object response);
}
