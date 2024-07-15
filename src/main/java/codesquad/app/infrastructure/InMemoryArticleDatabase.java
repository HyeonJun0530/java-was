package codesquad.app.infrastructure;

import codesquad.app.domain.Article;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryArticleDatabase {

    public static AtomicLong sequence = new AtomicLong(0);
    private static Map<Long, Article> articles = new ConcurrentHashMap<>();

    public static Article save(final Article article) {
        articles.put(article.getSequence(), article);

        return article;
    }

    public static Optional<Article> findBySequence(final Long sequence) {
        return Optional.ofNullable(articles.get(sequence));
    }

    public static Optional<Article> findByLastSequence() {
        return Optional.ofNullable(articles.get(sequence.get()));
    }

}
