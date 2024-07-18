package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;

import java.util.List;

public interface CommentDatabase extends Database {

    Comment save(final Comment comment);

    List<Comment> findByArticleSequence(final Long sequence);

    void remove(final Long sequence);

}
