package org.lazy.core;

public interface ApplicationContext extends ComponentFactory {

    Environment getEnvironment();

    void destroyContext();

}
