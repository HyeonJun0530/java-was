package codesquad.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationContextTest {

    @Test
    @DisplayName("ApplicationContext 초기화가 실행된다.")
    void initApplicationContext() {
        ApplicationContext.initialize();

        ApplicationContext instance = ApplicationContext.getInstance();

        assertAll(() -> assertNotNull(instance),
                () -> assertNotNull(instance.getFilterChain()),
                () -> assertNotNull(instance.getDispatcherServlet())
        );
    }

}
