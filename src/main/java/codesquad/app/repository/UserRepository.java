package codesquad.app.repository;

import codesquad.app.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public String save(User user) {
        users.put(user.getUserId(), user);

        return user.getUserId();
    }
}
