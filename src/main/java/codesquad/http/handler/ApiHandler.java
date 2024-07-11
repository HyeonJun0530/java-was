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
import java.util.Optional;
import java.util.stream.Stream;

public class ApiHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private static final List<Class> apiList = List.of(UserApi.class, MainApi.class);

    @Override
    public Object handle(final HttpRequest request) {
        String path = request.getRequestStartLine().getPath();
        try {
            List<Method> apiMethods = apiList.stream()
                    .flatMap(apiClass -> Stream.of(apiClass.getMethods()))
                    .filter(method -> method.isAnnotationPresent(ApiMapping.class))
                    .filter(method -> method.getAnnotation(ApiMapping.class).path().equals(path))
                    .toList();

            if (apiMethods.isEmpty()) {
                return HttpResponse.notFound();
            }

            Optional<Method> findMethod = apiMethods.stream()
                    .filter(method -> method.getAnnotation(ApiMapping.class).method()
                            .equals(request.getRequestStartLine().getMethod()))
                    .findFirst();

            if (findMethod.isEmpty()) {
                return HttpResponse.of(HttpStatus.METHOD_NOT_ALLOWED);
            }

            return findMethod.get().invoke(getNewInstance(findMethod.get().getDeclaringClass()), request);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error("API handler error", e);
            return HttpResponse.internalServerError();
        }
    }

    @Override
    public boolean isSupport(final HttpRequest request) {
        return apiList.stream()
                .flatMap(apiClass -> Stream.of(apiClass.getMethods()))
                .anyMatch(method -> method.isAnnotationPresent(ApiMapping.class) &&
                        method.getAnnotation(ApiMapping.class).path().equals(request.getRequestStartLine().getPath()));
    }

    @SuppressWarnings("unchecked")
    private static Object getNewInstance(final Class apiClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = apiClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

}
