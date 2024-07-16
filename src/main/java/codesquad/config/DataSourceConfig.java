package codesquad.config;

import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;

public class DataSourceConfig {

    private static final String URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "";

    private final DataSource dataSource;

    public DataSourceConfig() {
        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(URL, USER_NAME, PASSWORD);

        connectionPool.setMaxConnections(10);

        this.dataSource = connectionPool;
    }

    public DataSource getDataSource() {
        return dataSource;
    }


}
