package codesquad.http.adapter.renderer;

import codesquad.app.domain.User;
import codesquad.http.exception.InternalServerException;
import codesquad.http.message.constant.ContentType;
import codesquad.http.model.ModelAndView;
import codesquad.utils.FileUtil;

import java.util.List;

import static codesquad.utils.HttpMessageUtils.sanitizeHtml;

public class UserListRenderer implements ViewRenderer {

    private static final String USER_ATTRIBUTE = "users";

    @Override
    public String render(final ModelAndView modelAndView) {
        String html = getTemplateFile(modelAndView);

        try {
            List<User> users = (List<User>) modelAndView.getObject(USER_ATTRIBUTE);
            return replace(html, users);
        } catch (ClassCastException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    @Override
    public boolean isSupport(final ModelAndView modelAndView) {
        try {
            FileUtil.getTemplateFile(modelAndView.getViewName());

            if (!modelAndView.containsAttribute(USER_ATTRIBUTE)) {
                return false;
            }

            return modelAndView.getObject(USER_ATTRIBUTE) instanceof List;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TEXT_HTML;
    }


    public String replace(final String html, final List<User> users) {
        StringBuilder render = new StringBuilder();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            render.append("<tr>");
            render.append("<td>").append(i + 1).append("</td>");
            render.append("<td>").append(sanitizeHtml(user.getUserId())).append("</td>");
            render.append("<td>").append(sanitizeHtml(user.getName())).append("</td>");
            render.append("<td>").append(sanitizeHtml(user.getEmail())).append("</td>");
            render.append("</tr>");
        }

        return html.replace("${users}", render.toString());
    }
}
