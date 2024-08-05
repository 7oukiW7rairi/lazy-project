package org.lazy.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.lazy.core.CoreUtils.loadProperties;

public class DefaultEnvironment implements Environment {

    private static final String FILE_NAME = "application";
    private static final String FILE_EXTENSION = ".properties";
    private static final String CLASSPATH = "classpath:";

    private final String profile;
    private final Properties properties;

    public DefaultEnvironment(String profile) {
        this(profile, null);
    }

    public DefaultEnvironment(String profile, String propertiesFile) {
        this.profile = profile != null ? profile : "";
        this.properties = loadProperties(propertiesFilePath(profile, propertiesFile));

    }

    private InputStream propertiesFilePath(String profile, String propertiesFile) {
        if (propertiesFile != null) {
            if (propertiesFile.startsWith(CLASSPATH)) {
                return getClass().getClassLoader().getResourceAsStream(propertiesFile.replace(CLASSPATH, ""));
            } else {
                try {
                    return Files.newInputStream(Paths.get(propertiesFile));
                } catch (IOException e) {
                    throw new CoreException(e.getMessage());
                }
            }
             
        } else {
            return getClass().getClassLoader().getResourceAsStream(profile.isBlank() ? FILE_NAME + FILE_EXTENSION : FILE_NAME + "-" + profile + FILE_EXTENSION);
        }
    }

    @Override
    public String getActiveProfile() {
        return profile;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getPropertyOrDefault(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Set<String> getPropertyNames() {
        return properties.stringPropertyNames();
    }

}
