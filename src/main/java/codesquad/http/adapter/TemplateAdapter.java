package codesquad.http.adapter;

import codesquad.http.adapter.renderer.ArticleRenderer;
import codesquad.http.adapter.renderer.UserListRenderer;
import codesquad.http.adapter.renderer.ViewRenderer;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static codesquad.utils.HttpMessageUtils.DECODING_CHARSET;

public class TemplateAdapter implements HttpResponseAdapter {

    private static final List<ViewRenderer> viewRenderers = List.of(new UserListRenderer(), new ArticleRenderer());

    @Override
    public HttpResponse adapt(final Object response) throws UnsupportedEncodingException {
        ModelAndView modelAndView = (ModelAndView) response;

        String viewName = modelAndView.getViewName();

        ViewRenderer renderer = viewRenderers.stream()
                .filter(viewRenderer -> viewRenderer.isSupport(modelAndView))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 viewName 입니다. viewName : " + viewName));

        byte[] body = renderer.render(modelAndView).getBytes(DECODING_CHARSET);

        return HttpResponse.of(renderer.getContentType(), HttpStatus.OK, body);
    }

    @Override
    public boolean isSupport(final Object response) {
        return response instanceof ModelAndView;
    }
}
