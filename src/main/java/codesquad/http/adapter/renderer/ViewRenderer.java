package codesquad.http.adapter.renderer;

import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;

public interface ViewRenderer {
    String render(ModelAndView modelAndView);

    boolean isSupport(ModelAndView modelAndView);

    ContentType getContentType();

    default String getTemplateFile(final ModelAndView modelAndView) {
        byte[] templateFile = FileUtil.getTemplateFile(modelAndView.getViewName());

        return new String(templateFile);
    }
}
