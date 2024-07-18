package codesquad.app.infrastructure;

import codesquad.app.domain.Article;
import codesquad.http.exception.InternalServerException;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class JdbcArticleDatabase implements ArticleDatabase {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcArticleDatabase.class);

    private final DataSource dataSource;

    public JdbcArticleDatabase(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Article save(final Article article) {
        String query = "INSERT INTO articles (writer, title, contents, image_url, create_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, article.getWriterId());
            pstmt.setString(2, article.getTitle());
            pstmt.setString(3, article.getContent());
            pstmt.setString(4, article.getImageUrl());
            pstmt.setTimestamp(5, Timestamp.valueOf(article.getCreatedAt()));
            pstmt.setTimestamp(6, Timestamp.valueOf(article.getModifiedAt()));

            pstmt.executeUpdate();

            ResultSet resultSet = pstmt.getGeneratedKeys();

            if (resultSet.next()) {
                article.setSequence(resultSet.getLong(1));
                return article;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Article> findBySequence(final Long sequence) {
        String query = "SELECT * FROM articles WHERE sequence = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setLong(1, sequence);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Article(
                        resultSet.getLong("sequence"),
                        resultSet.getString("title"),
                        resultSet.getString("contents"),
                        resultSet.getString("writer"),
                        resultSet.getString("image_url"),
                        resultSet.getTimestamp("create_at").toLocalDateTime(),
                        resultSet.getTimestamp("modified_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Article> findByLastSequence() {
        String query = "SELECT * FROM articles ORDER BY sequence DESC LIMIT 1";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Article(
                        resultSet.getLong("sequence"),
                        resultSet.getString("title"),
                        resultSet.getString("contents"),
                        resultSet.getString("writer"),
                        resultSet.getString("image_url"),
                        resultSet.getTimestamp("create_at").toLocalDateTime(),
                        resultSet.getTimestamp("modified_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public void remove(final Long sequence) {
        String query = "DELETE FROM articles WHERE sequence = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setLong(1, sequence);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }
    }


}
