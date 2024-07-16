package codesquad.app.infrastructure;

import codesquad.app.domain.Article;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public interface ArticleDatabase extends Database {
    Article save(final Article article);

    Optional<Article> findBySequence(final Long sequence);

    Optional<Article> findByLastSequence();

    void remove(final Long sequence);

    AtomicLong getSequence();
}
