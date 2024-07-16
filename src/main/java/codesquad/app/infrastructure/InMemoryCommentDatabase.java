package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentDatabase {

    public static AtomicLong sequence = new AtomicLong(0);
    private static Map<Long, Comment> comments = new ConcurrentHashMap<>();

    public static Comment save(final Comment comment) {
        comments.put(comment.getSequence(), comment);

        return comment;
    }

    public static List<Comment> findByArticleSequence(final Long sequence) {
        return comments.values().stream()
                .filter(comment -> comment.getArticle().getSequence().equals(sequence))
                .toList();
    }

    public static void remove(final Long sequence) {
        comments.remove(sequence);
    }
}
