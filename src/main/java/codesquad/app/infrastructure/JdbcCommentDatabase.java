package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;
import codesquad.http.exception.InternalServerException;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcCommentDatabase implements CommentDatabase {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JdbcCommentDatabase.class);

    private final DataSource dataSource;

    public JdbcCommentDatabase(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Comment save(final Comment comment) {
        String query = "INSERT INTO comments (article_sequence, writer, contents) VALUES (?, ?, ?)";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, comment.getArticleSequence());
            pstmt.setString(2, comment.getWriter());
            pstmt.setString(3, comment.getContents());

            pstmt.executeUpdate();

            ResultSet resultSet = pstmt.getGeneratedKeys();

            if (resultSet.next()) {
                comment.setSequence(resultSet.getLong(1));
                return comment;
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return comment;
    }

    @Override
    public List<Comment> findByArticleSequence(final Long sequence) {
        String query = "SELECT * FROM comments WHERE article_sequence = ?";

        List<Comment> comments = new ArrayList<>();

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setLong(1, sequence);

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                comments.add(new Comment(
                        resultSet.getLong("sequence"),
                        resultSet.getLong("article_sequence"),
                        resultSet.getString("writer"),
                        resultSet.getString("contents")
                ));
            }
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }

        return comments;
    }

    @Override
    public void remove(final Long sequence) {
        String query = "DELETE FROM comments WHERE sequence = ?";

        try (Connection con = dataSource.getConnection()) {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setLong(1, sequence);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
