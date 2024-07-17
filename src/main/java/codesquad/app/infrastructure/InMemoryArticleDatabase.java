package codesquad.app.infrastructure;

import codesquad.app.domain.Article;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryArticleDatabase implements ArticleDatabase {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, Article> articles = new ConcurrentHashMap<>();

    @Override
    public Article save(final Article article) {
        if (article.getSequence() == null) {
            article.setSequence(sequence.getAndIncrement());
        }

        articles.put(article.getSequence(), article);

        return article;
    }

    @Override
    public Optional<Article> findBySequence(final Long sequence) {
        return Optional.ofNullable(articles.get(sequence));
    }

    @Override
    public Optional<Article> findByLastSequence() {
        return Optional.ofNullable(articles.get(sequence.get() - 1));
    }

    @Override
    public void remove(final Long sequence) {
        articles.remove(sequence);
    }

}
