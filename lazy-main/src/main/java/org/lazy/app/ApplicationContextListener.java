package org.lazy.app;


import org.lazy.core.ApplicationContext;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener()
public class ApplicationContextListener implements ServletContextListener {

    // Public constructor is required by servlet spec
    public ApplicationContextListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {

    }

    public void contextDestroyed(ServletContextEvent sce) {
        ApplicationContext appContext = (ApplicationContext) sce.getServletContext().getAttribute("APP_CONTEXT");
        if (appContext != null) {
            appContext.destroyContext();
        }
    }

}
