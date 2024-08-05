package org.lazy.app;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.lazy.web.ErrorsServlet;
import org.lazy.web.WebProperties;
import org.lazy.web.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebFilter;

public class JettyServerInitializer implements WebServerInitializer {

    private static final Logger logger = LoggerFactory.getLogger(JettyServerInitializer.class);
    
    private static final String ERROR_SERVLET_PATH = "/errors";
    private static final String DIRECTORY_WITH_WEB_APPS = "webapp";
    private static final String MAIN_APPLICATION_CONTEXT = "/";

    @Override
    public ConfigurableLocalWebServer initializWebServer(int serverPort, String profile) throws WebServerException {
        logger.debug("Beginning Jetty server configuration.");
        
        Server server = new Server(new InetSocketAddress(InetAddress.getLoopbackAddress(), serverPort));

        Arrays.stream(server.getConnectors())
                .flatMap(connector -> connector.getConnectionFactories().stream())
                .filter(HttpConnectionFactory.class::isInstance)
                .forEach(httpConnFactory -> ((HttpConnectionFactory) httpConnFactory).getHttpConfiguration().setSendServerVersion(false));


        //server.addEventListener(new StartListener());
        server.setHandler(new HandlerList(createServletHandler(profile), createResourceHandler()));
        //logger.info("server started lessening to " + server.getURI().toString());
        logger.debug("Finished server configuration.");
        return new JettyWebServer(server);
    }

    private ServletContextHandler createServletHandler(String profile) {
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletHandler.setContextPath(MAIN_APPLICATION_CONTEXT);
        ServletContextConfig servletContextConfig = ServletContextConfig.getInstance();
        servletHandler.setAttribute(WebProperties.APP_CONTEXT_NAME.getName(), servletContextConfig.getApplicationContext(profile));
        
        for (Class<?> clazz : servletContextConfig.getControllers()) {
            if (ERROR_SERVLET_PATH.equals(clazz.getAnnotation(Controller.class).value()[0])) {
                addErrorHandler(servletHandler, (Class<? extends Servlet>) clazz);
            } else {
                String[] servletPaths = Arrays.stream(clazz.getAnnotation(Controller.class).value())
                                                .map(servletContextConfig::converttoServletPathFormat).toArray(String[]::new);
                ServletHolder servlet = servletHandler.addServlet((Class<? extends Servlet>) clazz, servletPaths[0]);
                servlet.setInitOrder(0);
                servlet.getRegistration().addMapping(servletPaths);
            }
        }
        for (Class<?> webFilter : servletContextConfig.getWebFilters()) {
            FilterHolder filterHolder = servletHandler.addFilter((Class<? extends Filter>) webFilter, webFilter.getAnnotation(WebFilter.class).value()[0], EnumSet.of(DispatcherType.REQUEST));
            filterHolder.getRegistration().addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, webFilter.getAnnotation(WebFilter.class).value());
        }
        servletContextConfig.getWebListeners().forEach(webListener -> servletHandler.addEventListener(webListener));
        if (servletHandler.getServletContext().getServletRegistrations().values()
                .stream().noneMatch(servletRegistration -> servletRegistration.getMappings().contains(ERROR_SERVLET_PATH))) {
            addErrorHandler(servletHandler,  ErrorsServlet.class);
        }

        return servletHandler;
    }

    private void addErrorHandler(ServletContextHandler servletHandler, Class<? extends Servlet> errorServlet) {
        servletHandler.addServlet(errorServlet, ERROR_SERVLET_PATH);

        ErrorPageErrorHandler errorPageErrorHandler = new ErrorPageErrorHandler();
        errorPageErrorHandler.addErrorPage(RuntimeException.class, ERROR_SERVLET_PATH);
        servletHandler.setErrorHandler(errorPageErrorHandler);
    }

    private ContextHandler createResourceHandler() {

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setCacheControl("max-age=0, public");

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath(MAIN_APPLICATION_CONTEXT);
        contextHandler.setHandler(resourceHandler);
        contextHandler.setResourceBase(getApplicationPath());

        return contextHandler;
    }

    private String getApplicationPath() {
        URL applicationURL = getClass().getClassLoader().getResource(DIRECTORY_WITH_WEB_APPS);
        if (applicationURL == null) {
            throw new IllegalStateException("webapp directory not found.");
        }
        return applicationURL.toExternalForm();

    }

}
