package org.lazy.app;

import org.lazy.web.BaseHttpServlet;
import org.lazy.core.ApplicationContext;

public interface WebApplicationContext extends ApplicationContext {

    Object getWebComponent(BaseHttpServlet httpServlet);
}
