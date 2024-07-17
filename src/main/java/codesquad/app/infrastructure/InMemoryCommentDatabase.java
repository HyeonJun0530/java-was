package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentDatabase implements CommentDatabase {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();

    @Override
    public Comment save(final Comment comment) {
        if (comment.getSequence() == null) {
            comment.setSequence(sequence.getAndIncrement());
        }
        comments.put(comment.getSequence(), comment);

        return comment;
    }

    @Override
    public List<Comment> findByArticleSequence(final Long sequence) {
        return comments.values().stream()
                .filter(comment -> comment.getArticleSequence().equals(sequence))
                .toList();
    }

    @Override
    public void remove(final Long sequence) {
        comments.remove(sequence);
    }

}
