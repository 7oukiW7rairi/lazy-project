package org.lazy.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lazy.web.annotation.Controller;
import org.lazy.web.annotation.PathMapping;

@Controller("/errors")
public class ErrorsServlet extends BaseHttpServlet {

    @PathMapping
    public String getError(HttpServletRequest request, HttpServletResponse response) {
        int httpErrorCode = getErrorCode(request);
        response.setStatus(httpErrorCode);
        return  "forward:/index.html";
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

}
