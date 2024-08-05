package org.lazy.core;

import org.lazy.common.Configuration;
import org.lazy.common.Produces;

@Configuration
public class ConfigComponent {

    @Produces
    public ComponentFromConfig getComponentFromConfig() {
        return new ComponentFromConfig();
    }
}
