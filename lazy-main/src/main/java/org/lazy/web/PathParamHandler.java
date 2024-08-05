package org.lazy.web;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface PathParamHandler {

    Object handlePathParam(Parameter parameter, HttpServletRequest req, Method handler) throws IOException;
}
