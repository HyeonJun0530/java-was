package codesquad.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExecutorServiceConfigurationTest {

    @Test
    @DisplayName("쓰레드 풀이 정상적으로 생성된다.")
    void threadPool() {
        ExecutorService executorService =
                ExecutorServiceConfiguration.getExecutorService();

        assertInstanceOf(ThreadPoolExecutor.class, executorService);
        Assertions.assertThat(((ThreadPoolExecutor) executorService).getCorePoolSize())
                .isEqualTo(10);
        Assertions.assertThat(((ThreadPoolExecutor) executorService).getMaximumPoolSize())
                .isEqualTo(10);
        Assertions.assertThat(((ThreadPoolExecutor) executorService).getQueue().remainingCapacity())
                .isEqualTo(10);
    }
}
