package codesquad.http.handler;

import codesquad.app.api.UserApi;
import codesquad.app.api.annotation.ApiMapping;
import codesquad.http.message.constant.HttpStatus;
import codesquad.http.message.request.HttpRequest;
import codesquad.http.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ApiHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private static final Map<String, Class> apiMapper = Map.of("/create", UserApi.class);

    private ApiHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        String path = httpRequest.getRequestStartLine().getPath();
        Class<?> apiClass = apiMapper.get(path);

        if (apiClass == null) {
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
        }

        try {
            Constructor<?> constructor = apiClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object apiInstance = constructor.newInstance();

            for (Method method : apiClass.getMethods()) {
                if (method.isAnnotationPresent(ApiMapping.class)) {
                    ApiMapping apiMapping = method.getAnnotation(ApiMapping.class);
                    if (apiMapping.path().equals(path)) {
                        if (!apiMapping.method().equals(httpRequest.getRequestStartLine().getMethod())) {
                            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.METHOD_NOT_ALLOWED);
                        }
                        HttpResponse response = (HttpResponse) method.invoke(apiInstance, httpRequest);
                        return response;
                    }
                }
            }

            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("API handler error", e);
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static boolean isApiRequest(final String path) {
        return apiMapper.containsKey(path);
    }

}
