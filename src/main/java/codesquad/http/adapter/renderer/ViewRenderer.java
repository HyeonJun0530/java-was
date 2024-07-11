package codesquad.http.adapter.renderer;

import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;

public interface ViewRenderer {
    String render(ModelAndView modelAndView);

    boolean isSupport(ModelAndView modelAndView);

    ContentType getContentType();
}
