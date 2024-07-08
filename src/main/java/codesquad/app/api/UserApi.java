package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.User;
import codesquad.app.repository.UserRepository;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApi.class);
    private UserRepository userRepository = new UserRepository();

    @ApiMapping(method = HttpMethod.POST, path = "/create")
    public HttpResponse create(HttpRequest request) {
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

        userRepository.save(user);
        log.debug("User: {}", user.toString());

        return HttpResponse.redirect(request.getRequestStartLine().getProtocol(),
                HttpStatus.MOVED_PERMANENTLY, "/main/index.html");
    }
}
