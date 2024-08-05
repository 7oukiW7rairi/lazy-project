package org.lazy.app;

import org.lazy.core.CoreProperties;
import org.lazy.web.WebProperties;
import org.lazy.web.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.WebFilter;
import java.util.*;

//@HandlesTypes({Controller.class, WebFilter.class, WebListener.class})
public class WebAppInitializer implements ServletContainerInitializer {

    private static final Logger  logger = LoggerFactory.getLogger(WebAppInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        
        ServletContextConfig servletContextConfig = ServletContextConfig.getInstance();    
        servletContextConfig.getControllers().forEach(clazz -> {
            logger.info("controller paths " + Arrays.toString(clazz.getAnnotation(Controller.class).value()));
            ServletRegistration.Dynamic servlet = servletContext.addServlet(clazz.getSimpleName(), (Class<? extends Servlet>) clazz);
            servlet.addMapping(Arrays.stream(clazz.getAnnotation(Controller.class).value())
            .map(servletContextConfig::converttoServletPathFormat).toArray(String[]::new));
        });
        servletContextConfig.getWebFilters().forEach(clazz -> {
            FilterRegistration filterRegistration = servletContext.addFilter(clazz.getSimpleName(), (Class<? extends Filter>) clazz);
            filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, clazz.getAnnotation(WebFilter.class).value());
        });
        servletContextConfig.getWebListeners().forEach(eventListener -> servletContext.addListener(eventListener));
        logger.debug("registred servlets : " + servletContext.getServletRegistrations());
        String profile = Optional.ofNullable(servletContext.getInitParameter(CoreProperties.PROFILE.getName()))
                .or(() -> Optional.ofNullable(System.getProperty(CoreProperties.PROFILE.getName()))).orElse(null);
        servletContext.setAttribute(WebProperties.APP_CONTEXT_NAME.getName(), servletContextConfig.getApplicationContext(profile));
        

    }

}
