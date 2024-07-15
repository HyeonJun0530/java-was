package codesquad.http.adapter.renderer;

import codesquad.app.domain.Article;
import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;

public class ArticleRenderer implements ViewRenderer {
    @Override
    public String render(final ModelAndView modelAndView) {
        Article article = (Article) modelAndView.getObject("article");

        String templateFile = getTemplateFile(modelAndView);

        return replace(templateFile, article);
    }

    @Override
    public boolean isSupport(final ModelAndView modelAndView) {
        try {
            FileUtil.getTemplateFile(modelAndView.getViewName());

            if (!modelAndView.containsAttribute("article")) {
                return false;
            }

            return modelAndView.getObject("article") instanceof Article;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TEXT_HTML;
    }

    @Override
    public String getTemplateFile(final ModelAndView modelAndView) {
        byte[] templateFile = FileUtil.getTemplateFile(modelAndView.getViewName());

        return new String(templateFile);
    }

    private String replace(final String html, final Article article) {
        String result = html;

        result = result.replace("${article.title}", article.getTitle());
        result = result.replace("${article.user.name}", article.getWriter().getName());
        result = result.replace("${article.content}", article.getContent());

        return result;
    }
}
