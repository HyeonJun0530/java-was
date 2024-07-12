package codesquad;

import codesquad.app.domain.User;
import codesquad.app.infrastructure.UserDataBase;
import codesquad.config.ExecutorServiceConfiguration;
import codesquad.http.HttpProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int DEFAULT_PORT = 8080;

    /**
     * TODO: Byte 끌어올릴 때 크기 체크
     **/
    public static void main(String[] args) throws IOException {
        logger.info("Server is starting...");

        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            init();
            logger.info("Listening for connection on port 8080 ....");

            ExecutorService executorService = ExecutorServiceConfiguration.getExecutorService();

            while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결을 수락합니다.
                executorService.submit(new HttpProcessor(clientSocket)); // 클라이언트 요청을 병렬로 처리합니다.
            }
        } // 8080 포트에서 서버를 엽니다.
    }

    public static void init() {
        for (int i = 0; i < 10; i++) {
            User user = new User.Builder()
                    .userId("dummy" + i)
                    .name("dummyName" + i)
                    .password("dummyPassword" + i)
                    .email("dummyEmail" + i + "@test.com")
                    .build();

            UserDataBase.save(user);
        }
    }
}

