package org.lazy.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.lazy.common.CommonProperties;
import org.lazy.core.AutoConfig;
import org.lazy.core.ConfigurableApplicationContext;
import org.lazy.core.CoreUtils;
import org.lazy.core.DefaultEnvironment;
import org.lazy.core.Environment;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContextConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServletContextConfig.class);
    private static final List<AutoConfig> AUTO_CONFIGS = ServiceLoader.load(AutoConfig.class).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());

    private static ServletContextConfig INSTANCE;

    public ServletContextConfig() {
    }
    
    public static ServletContextConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServletContextConfig();
        }
        return INSTANCE;
    }

    public ConfigurableApplicationContext getApplicationContext(String profile) {
        Environment environment = new DefaultEnvironment(profile);
        ConfigurableApplicationContext webApplicationContext = new DefaultWebApplicationContext(CommonProperties.LAZY_APPLICATION_JSON.getName());
        webApplicationContext.registerSingleton(Environment.class.getName(), environment);
        AUTO_CONFIGS.stream()
                .filter(autoConfig -> autoConfig.shouldConfigure(environment.getPropertyNames()))
                .forEach(autoConfig -> autoConfig.configure(webApplicationContext));
        return webApplicationContext;
    }

    public List<Class<?>> getControllers() {
        Properties properties = CoreUtils.loadProperties(getClass().getClassLoader().getResourceAsStream(CommonProperties.LAZY_APPLICATION_PROPERTIES.getName()));
        String[] classesName = properties.getProperty(CommonProperties.CONTROLLERS.getName()).split(",");
        return Arrays.stream(classesName).map(this::loadControllerClass).collect(Collectors.toList());
    }
    
    public List<Class<?>> getWebFilters() {
        return Collections.emptyList();
    }

    public List<EventListener> getWebListeners() {
        return Arrays.asList(new AppRequestListener(), new ApplicationContextListener());
    }

    public String converttoServletPathFormat(String servletPath) {
        if (!servletPath.startsWith("/")) {
            servletPath = "/" + servletPath;
        }
        if (!servletPath.endsWith("/*")) {
            if (servletPath.endsWith("/")) {
                servletPath += "*";
            } else {
                servletPath += "/*";
            }
        }
        return servletPath;
    }

    private Class<?> loadControllerClass(String className) {
        try {
            InputStream resourceAsStream = LazyWebLauncher.class.getClassLoader().getResourceAsStream(convertToJvmFormat(className));
            ClassReader classReader = new ClassReader(resourceAsStream);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ControllerClassAdapter controllerClassAdapter = new ControllerClassAdapter(classWriter);
            classReader.accept(controllerClassAdapter, ClassReader.EXPAND_FRAMES);
            final byte[] bytes = classWriter.toByteArray();
            return new ClassLoader (LazyWebLauncher.class.getClassLoader()) {
                public Class<?> findClass(String name) {
                    return defineClass(name, bytes,0,bytes.length);
                }

            }.findClass(className);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static String convertToJvmFormat(String className) {
        String jvmClassName = className.replaceAll("\\.", "/");
        if (jvmClassName.endsWith("/class")) {
            jvmClassName = jvmClassName.replace("/class", ".class");
        } else {
            jvmClassName += ".class";
        }
        return jvmClassName;   
    }

}
