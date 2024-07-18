package codesquad.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceConfigTest {

    @Test
    @DisplayName("DataSourceConfig 인스턴스를 생성한다.")
    void existDataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DataSource dataSource = dataSourceConfig.getDataSource();

        assertAll(() -> assertNotNull(dataSource),
                () -> assertInstanceOf(DataSource.class, dataSource));
    }

    @Test
    @DisplayName("커넥션을 얻어 온다.")
    void getConnection() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DataSource dataSource = dataSourceConfig.getDataSource();

        assertAll(() -> assertNotNull(dataSource),
                () -> assertNotNull(dataSourceConfig.getDataSource().getConnection()));
    }

    @Test
    @DisplayName("테이블 생성을 확인한다.")
    void createTable() throws SQLException {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DataSource dataSource = dataSourceConfig.getDataSource();

        try (Connection connection = dataSource.getConnection()) {
            boolean article = connection.prepareStatement("SELECT * FROM articles").execute();
            boolean user = connection.prepareStatement("SELECT * FROM users").execute();
            boolean comment = connection.prepareStatement("SELECT * FROM comments").execute();

            assertAll(() -> assertTrue(article),
                    () -> assertTrue(user),
                    () -> assertTrue(comment));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
