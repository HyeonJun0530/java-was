package codesquad.app.domain;

public class Comment {

    private final Long sequence;
    private final Article article;
    private final String writer;
    private final String contents;

    public Comment(final Long sequence, final Article article, String writer, String contents) {
        this.sequence = sequence;
        this.article = article;
        this.writer = writer;
        this.contents = contents;
    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public Article getArticle() {
        return article;
    }

    public Long getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "writer='" + writer + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

    public static class Builder {
        private Long sequence;
        private Article article;
        private String writer;
        private String contents;

        public Builder sequence(Long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder article(Article article) {
            this.article = article;
            return this;
        }

        public Builder writer(String writer) {
            this.writer = writer;
            return this;
        }

        public Builder contents(String contents) {
            this.contents = contents;
            return this;
        }

        public Comment build() {
            return new Comment(sequence, article, writer, contents);
        }
    }
}
