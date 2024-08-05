package org.lazy.web;

import org.lazy.core.ComponentWithoutDependency;
import org.lazy.web.annotation.Controller;
import org.lazy.web.annotation.MessageBody;
import org.lazy.web.annotation.PathMapping;
import org.lazy.web.annotation.PathVariable;
import org.lazy.web.annotation.QueryParam;

import javax.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Controller("/test")
public class DummyServletTest extends BaseHttpServlet {

    @Inject
    private ComponentWithoutDependency componentWithoutDependency;

    public DummyServletTest() {
        super();
    }

    @PathMapping()
    public List<DummyTestClass> getList() {
        return Arrays.asList(
                new DummyTestClass(1, "dummy-1", true, Date.from(Instant.parse("2023-12-02T17:49:37.332Z"))),
                new DummyTestClass(2, "dummy-2", false, Date.from(Instant.parse("2023-10-27T17:49:37.332Z"))));
    }

    @PathMapping(path = "/{id}", produces = MediaType.APPLICATION_XML)
    public DummyTestClass getOne(@PathVariable("id") Integer id) {
        return new DummyTestClass(id, "dummy", false, Date.from(Instant.parse("2023-12-02T17:49:37.332Z")));
    }

    @PathMapping(method = HttpMethod.POST)
    public Response post(@MessageBody DummyTestClass dummyObject) {
        return Response.body("Dummy object with id " + dummyObject.getId() + " saved successfully").status(200).build();
    }

    @PathMapping(method = HttpMethod.PUT, consumes = MediaType.APPLICATION_XML, produces = MediaType.APPLICATION_JSON)
    public Response put(@QueryParam("id") Integer id, @QueryParam("dateTime") String dateTime, @MessageBody DummyTestClass dummyObject) {
        return Response
                .body(new DummyTestClass(id, dummyObject.getName(), dummyObject.isValid(), Date.from(Instant.parse(dateTime))))
                .status(200).build();
    }

    @PathMapping(path = "/delete/{id}", method = HttpMethod.DELETE)
    public Response delete(@PathVariable("id") Integer id) {
        return Response.body("Dummy object with id " + id + " deleted successfully").status(200).build();
    }

    public ComponentWithoutDependency getComponentWithoutDependency() {
        return componentWithoutDependency;
    }

    // this is only to test handle request behaviour
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }
}
