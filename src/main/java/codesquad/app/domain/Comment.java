package codesquad.app.domain;

public class Comment {

    private Long sequence;
    private Long articleSequence;
    private String writer;
    private String contents;

    public Comment(final Long sequence, final Long articleSequence, String writer, String contents) {
        this.sequence = sequence;
        this.articleSequence = articleSequence;
        this.writer = writer;
        this.contents = contents;
    }

    public Comment(final Long articleSequence, String writer, String contents) {
        this(null, articleSequence, writer, contents);
    }

    public void setSequence(final Long sequence) {
        this.sequence = sequence;
    }

    public String getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public Long getArticleSequence() {
        return articleSequence;
    }

    public Long getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "sequence=" + sequence +
                ", articleSequence='" + articleSequence + '\'' +
                ", writer='" + writer + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

    public static class Builder {
        private Long sequence;
        private Long articleSequence;
        private String writer;
        private String contents;

        public Builder sequence(Long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder articleSequence(Long articleSequence) {
            this.articleSequence = articleSequence;
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
            return new Comment(sequence, articleSequence, writer, contents);
        }
    }
}
