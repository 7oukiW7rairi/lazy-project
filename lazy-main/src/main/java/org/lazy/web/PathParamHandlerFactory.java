package org.lazy.web;

import org.lazy.web.annotation.MessageBody;
import org.lazy.web.annotation.MessageHeader;
import org.lazy.web.annotation.PathMapping;
import org.lazy.web.annotation.PathVariable;
import org.lazy.web.annotation.QueryParam;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// TODO Better naming
public class PathParamHandlerFactory {

    private static final String SPLIT_CHAR = "/";
    private static final Map<Predicate<Parameter>, PathParamHandler> PATH_PARAM_HANDLER = initializePathParamHandler();
    private static PathParamHandlerFactory INSTANCE;

    private PathParamHandlerFactory() {
    }

    public static PathParamHandlerFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PathParamHandlerFactory();
        }
        return INSTANCE;
    }

    //
    private static Map<Predicate<Parameter>, PathParamHandler> initializePathParamHandler() {
        Map<Predicate<Parameter>, PathParamHandler> paramHandlerMap = new HashMap<>();
        paramHandlerMap.put(parameter -> parameter.getType().equals(Principal.class), (parameter, req, handler) -> req.getUserPrincipal());
        paramHandlerMap.put(parameter -> parameter.getType().equals(HttpServletRequest.class), (parameter, req, handler) -> req);
        paramHandlerMap.put(parameter -> parameter.isAnnotationPresent(QueryParam.class), (parameter, req, handler) ->
                PathParamConverter.getInstance().convertPathParam(
                        req.getParameter(parameter.getAnnotation(QueryParam.class).value()),
                        parameter.getType()));
        paramHandlerMap.put(parameter -> parameter.isAnnotationPresent(MessageHeader.class), (parameter, req, handler) ->
                PathParamConverter.getInstance().convertPathParam(
                        req.getHeader(parameter.getAnnotation(MessageHeader.class).value()),
                        parameter.getType()));
        paramHandlerMap.put(parameter -> parameter.isAnnotationPresent(PathVariable.class), (parameter, req, handler) -> {
            String[] reqPathArray = handler.getAnnotation(PathMapping.class).path().replaceFirst("^/", "").split(SPLIT_CHAR);
            int index = IntStream.range(0, reqPathArray.length)
                    .filter(value -> PathPartType.of(reqPathArray[value]).containVariable())
                    .filter(value -> reqPathArray[value].contains(parameter.getAnnotation(PathVariable.class).value()))
                    .findAny().orElse(-1);
            return PathParamConverter.getInstance()
                    .convertPathParam(req.getPathInfo().replaceFirst("^/", "").split(SPLIT_CHAR)[index], parameter.getType());
        });
        paramHandlerMap.put(parameter -> parameter.isAnnotationPresent(MessageBody.class), (parameter, req, handler) -> {
            MediaType mediaType = Stream.of(handler.getAnnotation(PathMapping.class).consumes())
                    .findFirst().orElse(MediaType.APPLICATION_JSON);
            return RequestBodyReaderFactory.getReader(parameter.getType()).read(
                    parameter.getType(),
                    SerializerFactory.getInstance().getSerializer(mediaType),
                    req.getInputStream());
        });
        return paramHandlerMap;
    }

    public PathParamHandler getPathParamHandler(Parameter parameter) {
        return PATH_PARAM_HANDLER.entrySet().stream()
                .filter(entry -> entry.getKey().test(parameter))
                .findAny().map(Map.Entry::getValue).orElseThrow(() -> new UnsupportedOperationException("No Matching"));
    }

}
