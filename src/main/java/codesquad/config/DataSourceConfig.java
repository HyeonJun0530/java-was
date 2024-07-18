package codesquad.config;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataSourceConfig.class);
    private static final String URL = "jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "";

    private final DataSource dataSource;

    public DataSourceConfig() {

        runServer();

        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(URL, USER_NAME, PASSWORD);
        connectionPool.setMaxConnections(3);

        this.dataSource = connectionPool;

        createTable(dataSource);
    }

    private static void runServer() {
        new Thread(() -> {
            try {
                Server server = Server.createWebServer().start();

                log.info("H2 Server Start = {}", server.getStatus());
            } catch (SQLException e) {
                log.error("H2 Server Start Error", e);
            }
        }).start();
    }

    private static void createTable(final DataSource dataSource) {
        new Thread(() -> {
            try (Connection con = dataSource.getConnection()) {
                Statement stmt = con.createStatement();
                stmt.execute("CREATE TABLE articles (sequence BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        "writer VARCHAR(255), " +
                        "title VARCHAR(255), " +
                        "contents TEXT, " +
                        "image_url VARCHAR(255), " +
                        "create_at TIMESTAMP, " +
                        "modified_at TIMESTAMP)");

                stmt.execute("CREATE TABLE comments (sequence BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                        "article_sequence BIGINT, " +
                        "writer VARCHAR(255), " +
                        "contents TEXT)");

                stmt.execute("CREATE TABLE users (user_id VARCHAR(255) PRIMARY KEY, " +
                        "password VARCHAR(255), " +
                        "name VARCHAR(255), " +
                        "email VARCHAR(255))");
            } catch (SQLException e) {
                log.error("Create Table Error", e);
            }
        }).start();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void shutdown() {
        ((JdbcConnectionPool) dataSource).dispose();
    }

}
