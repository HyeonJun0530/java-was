package codesquad.app.domain;

import java.time.LocalDateTime;

public class Article {

    private Long sequence;
    private String title;
    private String content;
    private String writerId;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Article(final Long sequence, final String title,
                   final String content, final String writerId, final String imageUrl,
                   final LocalDateTime createdAt, final LocalDateTime modifiedAt) {
        this.sequence = sequence;
        this.title = title;
        this.content = content;
        this.writerId = writerId;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public Article(final String title, final String content, final String writerId,
                   final String imageUrl, final LocalDateTime createdAt, final LocalDateTime modifiedAt) {
        this(null, title, content, writerId, imageUrl, createdAt, modifiedAt);
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
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

    public String getWriterId() {
        return writerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    @Override
    public String toString() {
        return "Article{" +
                "sequence=" + sequence +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", writerId='" + writerId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }

    public static class Builder {
        private Long sequence;
        private String title;
        private String content;
        private String writerId;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public Builder() {
            this.sequence = sequence;
            this.title = title;
            this.content = content;
            this.writerId = writerId;
            this.imageUrl = imageUrl;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
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

        public Builder writerId(final String writerId) {
            this.writerId = writerId;
            return this;
        }

        public Builder imageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder modifiedAt(final LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public Article build() {
            return new Article(title, content, writerId, imageUrl, createdAt, modifiedAt);
        }
    }
}
