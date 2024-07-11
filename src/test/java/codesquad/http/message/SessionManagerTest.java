package codesquad.http.message;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private static User save() {
        User user = new User.Builder()
                .name("name")
                .email("email")
                .password("password")
                .userId("userId")
                .build();
        UserDatabase.save(user);
        return user;
    }

    @AfterEach
    void tearDown() {
        UserDatabase.remove("userId");
    }

    @Test
    @DisplayName("세션을 생성한다.")
    void createSession() {
        User user = save();

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        SessionManager.createSession(user.getUserId(), httpResponse);

        assertTrue(httpResponse.toString().contains("Set-Cookie: SID="));
    }

    @Test
    @DisplayName("세션을 통해 유저 아이디를 가져온다.")
    void getUserId() {
        User user = save();

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user.getUserId(), httpResponse);

        Optional<User> userId = SessionManager.getUserId(session);

        assertAll(
                () -> assertTrue(userId.isPresent()),
                () -> assertTrue(userId.get().getUserId().equals(user.getUserId())));
    }

    @Test
    @DisplayName("세션을 제거한다.")
    void removeSession() {
        User user = save();

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user.getUserId(), httpResponse);

        SessionManager.removeSession(session);

        assertTrue(SessionManager.getUserId(session).isEmpty());
    }

    @Test
    @DisplayName("세션이 유효한지 확인한다.")
    void isValidSession() {
        User user = save();

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user.getUserId(), httpResponse);

        assertTrue(SessionManager.isValidSession(session));
    }

    @Test
    void isValidSessionWithInvalidSession() {
        assertFalse(SessionManager.isValidSession("invalidSession"));
    }
}
