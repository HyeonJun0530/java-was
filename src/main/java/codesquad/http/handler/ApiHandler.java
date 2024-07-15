package codesquad.http.handler;

import codesquad.app.api.ArticleApi;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ApiHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private static final List<Class> apiList = List.of(UserApi.class, MainApi.class, ArticleApi.class);

    @Override
    public Object handle(final HttpRequest request) {
        String path = request.getRequestStartLine().getPath();
        try {
            List<Method> apiMethods = apiList.stream()
                    .flatMap(apiClass -> Stream.of(apiClass.getMethods()))
                    .filter(method -> matchPath(method, path))
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
                .anyMatch(method -> matchPath(method, request.getRequestStartLine().getPath()));
    }

    private static boolean matchPath(final Method method, final String requestPath) {
        if (!method.isAnnotationPresent(ApiMapping.class)) {
            return false;
        }
        String apiPath = method.getAnnotation(ApiMapping.class).path();
        String[] apiPathSegments = apiPath.split("/");
        String[] requestPathSegments = requestPath.split("/");

        if (apiPathSegments.length != requestPathSegments.length) {
            return false;
        }

        return IntStream.range(0, apiPathSegments.length)
                .allMatch(i -> {
                    if (apiPathSegments[i].startsWith("{") && apiPathSegments[i].endsWith("}")) {
                        try {
                            Long.parseLong(requestPathSegments[i]);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return apiPathSegments[i].equals(requestPathSegments[i]);
                });
    }

    @SuppressWarnings("unchecked")
    private static Object getNewInstance(final Class apiClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = apiClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

}
