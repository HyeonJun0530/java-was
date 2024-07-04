package codesquad.app.api;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.app.domain.User;
import codesquad.app.repository.UserRepository;
import codesquad.http.message.constant.HttpMethod;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;

import java.util.Map;

public class UserApi {

    private UserRepository userRepository = new UserRepository();

    @ApiMapping(method = HttpMethod.GET, path = "/create")
    public HttpResponse create(HttpRequest request) {
        Map<String, String> queryString = request.getRequestStartLine().getQueryString();
        String name = queryString.get("name");
        String email = queryString.get("email");
        String password = queryString.get("password");
        String userId = queryString.get("userId");

        User user = new User.Builder()
                .name(name)
                .email(email)
                .password(password)
                .userId(userId)
                .build();

        return HttpResponse.of(request.getRequestStartLine().getProtocol(),
                HttpStatus.MOVED_PERMANENTLY, "/index.html");
    }
}
