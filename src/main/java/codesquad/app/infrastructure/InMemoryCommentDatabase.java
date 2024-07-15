package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentDatabase {

    public static AtomicLong sequence = new AtomicLong(0);
    private static Map<Long, Comment> comments = new ConcurrentHashMap<>();

    public static Comment save(final Comment comment) {
        comments.put(comment.getSequence(), comment);

        return comment;
    }

    public static Optional<Comment> findBySequence(final Long sequence) {
        return Optional.ofNullable(comments.get(sequence));
    }

    public static Optional<Comment> findByLastSequence() {
        return Optional.ofNullable(comments.get(sequence.get()));
    }

    public static void remove(final Long sequence) {
        comments.remove(sequence);
    }
}
