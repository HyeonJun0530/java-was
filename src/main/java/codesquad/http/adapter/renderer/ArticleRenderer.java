package codesquad.http.adapter.renderer;

import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;

import java.util.List;

public class ArticleRenderer implements ViewRenderer {
    @Override
    public String render(final ModelAndView modelAndView) {
        Article article = (Article) modelAndView.getObject("article");
        List<Comment> comments = (List<Comment>) modelAndView.getObject("comments");
        boolean login = SessionManager.isValidSession((String) modelAndView.getObject("session"));

        String templateFile = getTemplateFile(modelAndView);

        return replace(templateFile, article, comments, login);
    }

    @Override
    public boolean isSupport(final ModelAndView modelAndView) {
        try {
            FileUtil.getTemplateFile(modelAndView.getViewName());

            if (!modelAndView.containsAttribute("article")) {
                return false;
            }

            if (!modelAndView.containsAttribute("comments")) {
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

    private String replace(final String html, final Article article, final List<Comment> comments, final boolean login) {
        String result = html;

        if (!login) {
            result = result.replace("${login}", "<li class=\"header__menu__item\">\n" +
                    "                <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n" +
                    "            </li>\n" +
                    "            <li class=\"header__menu__item\">\n" +
                    "                <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">\n" +
                    "                    회원 가입\n" +
                    "                </a>\n" +
                    "            </li>");
        } else {
            result = result.replace("${login}", "<li class=\"header__menu__item\">\n" +
                    "                <a class=\"btn btn_contained btn_size_s\" href=\"/user/list\">유저 목록</a>\n" +
                    "            </li>\n" +
                    "            <li class=\"header__menu__item\">\n" +
                    "                <a class=\"btn btn_contained btn_size_s\" href=\"/article\">글쓰기</a>\n" +
                    "            </li>\n" +
                    "            <li class=\"header__menu__item\">\n" +
                    "                <form action=\"/logout\" method=\"post\">\n" +
                    "                    <button class=\"btn btn_ghost btn_size_s\" id=\"logout-btn\">\n" +
                    "                        로그아웃\n" +
                    "                    </button>\n" +
                    "                </form>\n" +
                    "            </li>");
        }

        result = result.replace("${article.title}", article.getTitle());
        result = result.replace("${article.user.name}", article.getWriter().getName());
        result = result.replace("${article.content}", article.getContent());

        StringBuilder renderedComments = new StringBuilder();

        for (Comment comment : comments) {
            renderedComments.append("<li class=\"comment__item\">");
            renderedComments.append("<div class=\"comment__item__user\">");
            renderedComments.append("<img class=\"comment__item__user__img\"/>");
            renderedComments.append("<p class=\"comment__item__user__nickname\">").append(comment.getWriter()).append("</p>");
            renderedComments.append("</div>");
            renderedComments.append("<p class=\"comment__item__article\">").append(comment.getContents()).append("</p>");
            renderedComments.append("</li>");
        }

        result = result.replace("${comments}", renderedComments.toString());

        return result;
    }

}
