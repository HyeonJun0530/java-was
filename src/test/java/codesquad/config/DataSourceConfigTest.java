package codesquad.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataSourceConfigTest {

    @Test
    @DisplayName("DataSourceConfig 인스턴스를 생성한다.")
    void existDataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        DataSource dataSource = dataSourceConfig.getDataSource();

        Assertions.assertAll(() -> assertNotNull(dataSource),
                () -> assertInstanceOf(DataSource.class, dataSource));
    }

}
