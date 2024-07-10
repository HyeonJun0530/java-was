package codesquad.http.handler;

import codesquad.app.api.MainApi;
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
import java.util.List;
import java.util.stream.Stream;

public class ApiHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private static final List<Class> apiList = List.of(UserApi.class, MainApi.class);

    private ApiHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) {
        String path = httpRequest.getRequestStartLine().getPath();
        try {
            for (Class apiClass : apiList) {
                Object apiInstance = getNewInstance(apiClass);
                for (Method method : apiClass.getMethods()) {
                    if (method.isAnnotationPresent(ApiMapping.class)) {
                        ApiMapping apiMapping = method.getAnnotation(ApiMapping.class);
                        if (apiMapping.path().equals(path)) {
                            if (!apiMapping.method().equals(httpRequest.getRequestStartLine().getMethod())) {
                                return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.METHOD_NOT_ALLOWED);
                            }
                            return (HttpResponse) method.invoke(apiInstance, httpRequest);
                        }
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("API handler error", e);
            return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return HttpResponse.of(httpRequest.getRequestStartLine().getProtocol(), HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unchecked")
    private static Object getNewInstance(final Class apiClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = apiClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static boolean isApiRequest(final HttpRequest httpRequest) {
        return apiList.stream()
                .flatMap(apiClass -> Stream.of(apiClass.getMethods()))
                .anyMatch(method -> method.isAnnotationPresent(ApiMapping.class) &&
                        method.getAnnotation(ApiMapping.class).path().equals(httpRequest.getRequestStartLine().getPath()));
    }

}
