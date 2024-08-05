package org.lazy.web;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public interface RequestHandlingProcessor {

    void processRequest(HttpServletRequest request, Method handler);

    ProcessOrder getProcessOrder();
}
