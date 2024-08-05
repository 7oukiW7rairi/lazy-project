package org.lazy.core;

import java.util.*;

public interface Environment {

    String getActiveProfile();

    String getProperty(String key);

    String getPropertyOrDefault(String key, String defaultValue);

    Set<String> getPropertyNames();
}
