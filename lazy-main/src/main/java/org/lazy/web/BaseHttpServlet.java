package org.lazy.web;

import org.lazy.app.WebApplicationContext;
import org.lazy.web.annotation.PathMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BaseHttpServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BaseHttpServlet.class);

    private static final List<RequestHandlingProcessor> PROCESSORS = ServiceLoader.load(RequestHandlingProcessor.class).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());

    private HandlerMapping handlerMapping;

    public BaseHttpServlet() {
        super();
    }

    @Override
    public void init() {
        handlerMapping = new HandlerMapping(this.getClass().getDeclaredMethods());
        // TODO check how to handle null WebApplicationContext
        WebApplicationContext applicationContext = (WebApplicationContext) getServletContext().getAttribute(WebProperties.APP_CONTEXT_NAME.getName());
        if(applicationContext != null) {
            applicationContext.getWebComponent(this);
        }
        /**/
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Start handling request " + req.getRequestURI());
        Response responseBody;
        try {
            Method handler = handlerMapping.getHandler(req);
            PROCESSORS.stream()
                    .filter(requestHandlingProcessor -> requestHandlingProcessor.getProcessOrder() == ProcessOrder.PRE)
                    .forEach(requestHandlingProcessor -> requestHandlingProcessor.processRequest(req, handler));
            List<Object> args = new ArrayList<>();
            int bound = handler.getParameterCount();
            for (int index = 0; index < bound; index++) {
                Object handlerParamValue = PathParamHandlerFactory.getInstance().getPathParamHandler(handler.getParameters()[index])
                         .handlePathParam(handler.getParameters()[index], req, handler);
                args.add(handlerParamValue);
            }
            MediaType mediaType = Stream.of(handler.getAnnotation(PathMapping.class).produces())
                    .findFirst().orElse(MediaType.APPLICATION_JSON);
            resp.setContentType(mediaType.getType());
            ResponseBodyWriter responseWriter = ResponseBodyWriterFactory.getInstance().getResponseBodyWriter(handler.getGenericReturnType());
            if (Response.class == handler.getReturnType()) {
                responseBody = (Response) handler.invoke(this, args.toArray());
                if (responseBody.isView()) {
                    RequestDispatcher view = req.getRequestDispatcher(responseBody.getView());
                    view.forward(req, resp);
                } else {
                    responseWriter.write(responseBody.getBody(), SerializerFactory.getInstance().getSerializer(mediaType), resp.getOutputStream());
                    resp.setStatus(responseBody.getStatus());
                }
            } else {
                responseWriter.write(handler.invoke(this, args.toArray()), SerializerFactory.getInstance().getSerializer(mediaType), resp.getOutputStream());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            // TODO Check if it's really needed
            /*PROCESSORS.stream()
                    .filter(requestHandlingProcessor -> requestHandlingProcessor.getProcessOrder() == ProcessOrder.POST)
                    .forEach(requestHandlingProcessor -> requestHandlingProcessor.processRequest(req, handler));*/
        } catch (Throwable exp) {
            // TODO add more specific exception handling for arguments, serialisation, handler
            log.error(exp.getMessage(), exp);
            responseBody = ExceptionHandlerFactory.getInstance()
                    .getExceptionHandler(Objects.requireNonNullElse(exp.getCause(), exp).getClass())
                    .handleException(exp.getCause());
            ResponseBodyWriterFactory.DEFAULT_WRITER.write(responseBody.getBody(),
                    SerializerFactory.getInstance().getSerializer(req.getContentType()),
                    resp.getOutputStream());
            resp.setStatus(responseBody.getStatus());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO add
        handleRequest(req, resp);
    }
}
