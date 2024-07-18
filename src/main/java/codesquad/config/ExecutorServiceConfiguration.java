package codesquad.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceConfiguration {
    private static final int THREAD_POOL_SIZE = 5;
    private static final int QUEUE_CAPACITY = 5;
    private static final ExecutorService executorService = new ThreadPoolExecutor(
            THREAD_POOL_SIZE,
            THREAD_POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY)
    );

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
