package codesquad.app.infrastructure;

import codesquad.app.domain.Comment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentDatabase implements CommentDatabase {

    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();

    @Override
    public Comment save(final Comment comment) {
        comments.put(comment.getSequence(), comment);

        return comment;
    }

    @Override
    public List<Comment> findByArticleSequence(final Long sequence) {
        return comments.values().stream()
                .filter(comment -> comment.getArticle().getSequence().equals(sequence))
                .toList();
    }

    @Override
    public void remove(final Long sequence) {
        comments.remove(sequence);
    }

    @Override
    public AtomicLong getSequence() {
        return sequence;
    }
}
