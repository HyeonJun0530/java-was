package codesquad.app.domain;

import java.time.LocalDateTime;

public class Article {

    private final Long sequence;
    private final String title;
    private final String content;
    private final User writer;
    private final LocalDateTime createdTime;
    private final LocalDateTime modifiedTime;

    public Article(final Long sequence, final String title, final String content, final User writer, final LocalDateTime createdTime, final LocalDateTime modifiedTime) {
        this.sequence = sequence;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public Long getSequence() {
        return sequence;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public User getWriter() {
        return writer;
    }

    @Override
    public String toString() {
        return "Article{" +
                "sequence=" + sequence +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", writer=" + writer +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }

    public static class Builder {
        private Long sequence;
        private String title;
        private String content;
        private User writer;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;

        public Builder() {
            this.sequence = sequence;
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.createdTime = createdTime;
            this.modifiedTime = modifiedTime;
        }

        public Builder sequence(final Long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder content(final String content) {
            this.content = content;
            return this;
        }

        public Builder writer(final User writer) {
            this.writer = writer;
            return this;
        }

        public Builder createdTime(final LocalDateTime createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime(final LocalDateTime modifiedTime) {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Article build() {
            return new Article(sequence, title, content, writer, createdTime, modifiedTime);
        }
    }
}
