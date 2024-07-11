package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.User;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.message.Cookie;
import codesquad.http.message.SessionManager;
import codesquad.http.message.constant.ContentType;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.request.RequestBody;
import codesquad.http.message.response.HttpResponse;
import codesquad.http.model.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static codesquad.utils.FileUtil.getStaticFile;

public class UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApi.class);

    @ApiMapping(method = HttpMethod.POST, path = "/create")
    public HttpResponse create(final HttpRequest request) {
        Map<String, String> body = request.getRequestBody().parseFormUrlEncoded();
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String userId = body.get("userId");

        User user = new User.Builder()
                .name(name)
                .email(email)
                .password(password)
                .userId(userId)
                .build();

        UserDatabase.save(user);
        log.debug("User: {}", user);

        HttpResponse response = HttpResponse.redirect(HttpStatus.FOUND, "/");

        SessionManager.createSession(userId, response);

        return response;
    }

    @ApiMapping(method = HttpMethod.GET, path = "/registration")
    public HttpResponse registrationPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/");
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/registration/index.html"));
    }

    @ApiMapping(method = HttpMethod.GET, path = "/login")
    public HttpResponse loginPage(final HttpRequest request) {
        if (SessionManager.isValidSession(request.getSessionId())) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/");
        }

        return HttpResponse.of(ContentType.TEXT_HTML, HttpStatus.OK, getStaticFile("/login/index.html"));
    }

    @ApiMapping(method = HttpMethod.POST, path = "/login")
    public HttpResponse login(final HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();

        if (requestBody == null) {
            return HttpResponse.redirect(HttpStatus.FOUND, "/login");
        }

        Map<String, String> body = requestBody.parseFormUrlEncoded();
        String userId = body.get("userId");
        String password = body.get("password");

        User user = UserDatabase.findByUserId(userId);

        if (user == null || !user.getPassword().equals(password)) {
            HttpResponse response = HttpResponse.redirect(HttpStatus.FOUND, "/login");
            return response;
        }

        HttpResponse response = HttpResponse.redirect(HttpStatus.FOUND, "/");

        SessionManager.createSession(userId, response);

        return response;
    }

    @ApiMapping(method = HttpMethod.POST, path = "/logout")
    public HttpResponse logout(final HttpRequest request) {
        SessionManager.removeSession(request.getSessionId());

        HttpResponse response = HttpResponse.redirect(HttpStatus.FOUND, "/");

        Cookie cookie = new Cookie("SID", "null");
        cookie.setMaxAge(0);

        response.setCookie(cookie);

        return response;
    }

    @ApiMapping(method = HttpMethod.GET, path = "/user/list")
    public ModelAndView getUserList(final HttpRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/user/userList.html");
        mav.addObject("users", UserDatabase.findAll());

        return mav;
    }
}
