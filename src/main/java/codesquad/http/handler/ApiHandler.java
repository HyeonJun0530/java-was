package codesquad.http.handler;

import codesquad.app.api.annotation.ApiMapping;
import codesquad.http.exception.*;
import codesquad.http.message.request.HttpRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ApiHandler implements HttpRequestHandler {

    private final Map<Class, Object> apiContainer;

    public ApiHandler(final Map<Class, Object> apiContainer) {
        this.apiContainer = apiContainer;
    }

    @Override
    public Object handle(final HttpRequest request) {
        String path = request.getRequestStartLine().getPath();
        try {
            List<Method> apiMethods = apiContainer.keySet().stream()
                    .flatMap(apiClass -> Stream.of(apiClass.getMethods()))
                    .filter(method -> matchPath(method, path))
                    .toList();

            if (apiMethods.isEmpty()) {
                throw new NotFoundException("API not found");
            }

            Optional<Method> findMethod = apiMethods.stream()
                    .filter(method -> method.getAnnotation(ApiMapping.class).method()
                            .equals(request.getRequestStartLine().getMethod()))
                    .findFirst();

            if (findMethod.isEmpty()) {
                throw new MethodNotAllowedException("Method not allowed");
            }

            return findMethod.get().invoke(apiContainer.get(findMethod.get().getDeclaringClass()), request);
        } catch (NumberFormatException e) {
            throw new BadRequestException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new InternalServerException(e.getMessage());
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof HttpException) {
                throw (HttpException) targetException;
            }

            throw new InternalServerException(targetException.getMessage());
        }
    }

    @Override
    public boolean isSupport(final HttpRequest request) {
        return apiContainer.keySet().stream()
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

}
