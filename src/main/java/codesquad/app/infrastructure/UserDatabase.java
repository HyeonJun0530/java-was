package codesquad.app.infrastructure;

import codesquad.app.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDatabase extends Database {
    User save(final User user);

    Optional<User> findByUserId(final String userId);

    List<User> findAll();

    void remove(final String userId);
}
