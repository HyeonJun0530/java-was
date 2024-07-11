package codesquad.http.message;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.message.response.HttpResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    //TODO: 재로그인시 기존 세션 제거
    public static String createSession(final String userId, final HttpResponse response) {
        UUID sessionId = UUID.randomUUID();
        User user = UserDatabase.findByUserId(userId);
        sessions.put(sessionId.toString(), user);

        response.setCookie(new Cookie("SID", sessionId.toString()));

        return sessionId.toString();
    }

    public static Optional<User> getUserId(final String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public static void removeSession(final String sessionId) {
        sessions.remove(sessionId);
    }

    public static boolean isValidSession(final String sessionId) {
        return sessions.containsKey(sessionId);
    }

}
