package codesquad.app.domain;

public class User {

    private final String userId;
    private final String password;
    private final String name;
    private final String email;

    public User(final String userId, final String password, final String name, final String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    private User(final Builder builder) {
        this.userId = builder.userId;
        this.password = builder.password;
        this.name = builder.name;
        this.email = builder.email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String userId;
        private String password;
        private String name;
        private String email;

        public Builder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
