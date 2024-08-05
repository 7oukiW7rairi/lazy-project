package org.lazy.app;

import org.lazy.jpa.EntityManagerContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;

@WebListener()
public class AppRequestListener implements ServletRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(AppRequestListener.class);

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        logger.debug("Request initialized " + request.getRequestURI());
        HttpServletMapping servletMapping = request.getHttpServletMapping();
        logger.debug("servlet " + servletMapping.getServletName() + "  mapping match " + servletMapping.getMatchValue() + " for pattern " + servletMapping.getPattern());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        logger.debug("Request destroyed " + request.getRequestURI());
        //if(request.getRequestURI().startsWith("/api")) {
            EntityManagerContextHolder.clearEntityManager();
        //}
    }

}
