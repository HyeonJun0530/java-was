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
        return null;
    }

    @Override
    public String getTemplateFile(final ModelAndView modelAndView) {
        byte[] templateFile = FileUtil.getTemplateFile(modelAndView.getViewName());

        return new String(templateFile);
    }

    private String replace(final String html, final Article article) {
        StringBuilder render = new StringBuilder();

        render.append(html.replace("${article.title}", article.getTitle()));
        render.append(html.replace("${article.user.name}", article.getWriter().getName()));
        render.append(html.replace("${article.content}", article.getContent()));

        return render.toString();
    }
}
