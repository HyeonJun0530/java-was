package codesquad.http.adapter.renderer;

import codesquad.app.domain.Article;
import codesquad.app.domain.Comment;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;

import java.io.File;
import java.util.List;

import static codesquad.utils.HttpMessageUtils.sanitizeHtml;

public class ArticleRenderer implements ViewRenderer {

    private static final String ARTICLE_ATTRIBUTE = "article";
    private static final String COMMENTS_ATTRIBUTE = "comments";
    private static final String WRITER_NAME_ATTRIBUTE = "writerName";
    private static final String SESSION_ATTRIBUTE = "session";
    private static final String IS_NEXT_PAGE = "isNext";

    @Override
    public String render(final ModelAndView modelAndView) {
        Article article = (Article) modelAndView.getObject(ARTICLE_ATTRIBUTE);
        List<Comment> comments = (List<Comment>) modelAndView.getObject(COMMENTS_ATTRIBUTE);
        String writerName = (String) modelAndView.getObject(WRITER_NAME_ATTRIBUTE);
        boolean login = SessionManager.isValidSession((String) modelAndView.getObject(SESSION_ATTRIBUTE));
        boolean isNext = (boolean) modelAndView.getObject(IS_NEXT_PAGE);

        String templateFile = getTemplateFile(modelAndView);

        return replace(templateFile, article, writerName, comments, login, isNext);
    }

    @Override
    public boolean isSupport(final ModelAndView modelAndView) {
        try {
            FileUtil.getTemplateFile(modelAndView.getViewName());

            if (!modelAndView.containsAttribute(ARTICLE_ATTRIBUTE)) {
                return false;
            }

            if (!modelAndView.containsAttribute(COMMENTS_ATTRIBUTE)) {
                return false;
            }

            if (!modelAndView.containsAttribute(WRITER_NAME_ATTRIBUTE)) {
                return false;
            }

            if (!modelAndView.containsAttribute(IS_NEXT_PAGE)) {
                return false;
            }

            return modelAndView.getObject(ARTICLE_ATTRIBUTE) instanceof Article;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TEXT_HTML;
    }


    private String replace(final String html, final Article article, final String writerName,
                           final List<Comment> comments, final boolean login, final boolean isNext) {
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

        String basePath = System.getProperty("user.images.path", System.getProperty("user.home") + File.separator + "appImages");
        String imagesPath = basePath + File.separator + "images/";

        if (article.getImageUrl() != null) {
            String imageUrl = article.getImageUrl();
            result = result.replace("src=${article.image}", "src=\"" + imagesPath + sanitizeHtml(imageUrl) + "\"");
        } else {
            result = result.replace("src=${article.image}", "");
        }

        result = result.replace("${article.title}", sanitizeHtml(article.getTitle()));
        result = result.replace("${article.user.name}", sanitizeHtml(writerName));
        result = result.replace("${article.content}", sanitizeHtml(article.getContent()));

        StringBuilder renderedComments = new StringBuilder();

        for (Comment comment : comments) {
            renderedComments.append("<li class=\"comment__item\">");
            renderedComments.append("<div class=\"comment__item__user\">");
            renderedComments.append("<img class=\"comment__item__user__img\"/>");
            renderedComments.append("<p class=\"comment__item__user__nickname\">").append(sanitizeHtml(comment.getWriter())).append("</p>");
            renderedComments.append("</div>");
            renderedComments.append("<p class=\"comment__item__article\">").append(sanitizeHtml(comment.getContents())).append("</p>");
            renderedComments.append("</li>");
        }

        result = result.replace("${comments}", renderedComments.toString());

        result = result.replace("${isNext}", isNext ? "true" : "false");

        return result;
    }

}
