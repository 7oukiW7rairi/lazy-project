package org.lazy.app;

import org.lazy.app.DummyWebComponent;
import org.lazy.web.Response;
import org.lazy.web.annotation.Controller;
import org.lazy.web.annotation.PathMapping;

@Controller("dummy-test")
public class DummyController {

    private DummyWebComponent dummyWebComponent;

    public DummyController(DummyWebComponent dummyWebComponent) {
        this.dummyWebComponent = dummyWebComponent;
    }

    @PathMapping
    public Response getDummy() {
        return Response.ok("Successfull").build();
    }
}
