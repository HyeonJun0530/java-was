package codesquad.app.repository;

import codesquad.app.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    public static User save(User user) {
        users.put(user.getUserId(), user);

        return user;
    }

    public static User findByUserId(String userId) {
        return users.get(userId);
    }
}
