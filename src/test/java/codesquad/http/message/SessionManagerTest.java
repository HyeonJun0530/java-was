package codesquad.http.message;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.InMemoryUserDatabase;
import codesquad.app.infrastructure.UserDatabase;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    UserDatabase userDatabase;
    User user;

    @BeforeEach
    void save() {
        user = new User.Builder()
                .name("name")
                .email("email")
                .password("password")
                .userId("userId")
                .build();
        userDatabase = new InMemoryUserDatabase();
        userDatabase.save(user);
    }

    @AfterEach
    void tearDown() {
        userDatabase.remove("userId");
    }

    @Test
    @DisplayName("세션을 생성한다.")
    void createSession() {

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        SessionManager.createSession(user, httpResponse);

        assertTrue(httpResponse.toString().contains("Set-Cookie: SID="));
    }

    @Test
    @DisplayName("세션을 통해 유저 아이디를 가져온다.")
    void getUser() {

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user, httpResponse);

        Optional<User> userId = SessionManager.getUser(session);

        assertAll(
                () -> assertTrue(userId.isPresent()),
                () -> assertTrue(userId.get().getUserId().equals(user.getUserId())));
    }

    @Test
    @DisplayName("세션을 제거한다.")
    void removeSession() {

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user, httpResponse);

        SessionManager.removeSession(session);

        assertTrue(SessionManager.getUser(session).isEmpty());
    }

    @Test
    @DisplayName("세션이 유효한지 확인한다.")
    void isValidSession() {

        HttpResponse httpResponse = HttpResponse.of(HttpStatus.OK);
        String session = SessionManager.createSession(user, httpResponse);

        assertTrue(SessionManager.isValidSession(session));
    }

    @Test
    void isValidSessionWithInvalidSession() {
        assertFalse(SessionManager.isValidSession("invalidSession"));
    }
}
