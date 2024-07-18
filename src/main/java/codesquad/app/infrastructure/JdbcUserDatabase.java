package codesquad.app.infrastructure;

import codesquad.app.domain.User;
import codesquad.http.exception.InternalServerException;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDatabase implements UserDatabase {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcUserDatabase.class);

    private final DataSource dataSource;

    public JdbcUserDatabase(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User save(final User user) {
        String query = "INSERT INTO users (user_id, password, name, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return user;
    }

    @Override
    public Optional<User> findByUserId(final String userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new User.Builder()
                        .userId(resultSet.getString("user_id"))
                        .password(resultSet.getString("password"))
                        .name(resultSet.getString("name"))
                        .email(resultSet.getString("email"))
                        .build());
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new User.Builder()
                        .userId(resultSet.getString("user_id"))
                        .password(resultSet.getString("password"))
                        .name(resultSet.getString("name"))
                        .email(resultSet.getString("email"))
                        .build());
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return users;
    }

    @Override
    public void remove(final String userId) {
        String query = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
