package codesquad.app.infrastructure;

import codesquad.app.domain.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase implements Database {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    public static User save(User user) {
        if (users.containsKey(user.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        users.put(user.getUserId(), user);

        return user;
    }

    public static User findByUserId(String userId) {
        return users.get(userId);
    }

    public static List<User> findAll() {
        return List.copyOf(users.values());
    }

    public static void remove(String userId) {
        users.remove(userId);
    }
}
