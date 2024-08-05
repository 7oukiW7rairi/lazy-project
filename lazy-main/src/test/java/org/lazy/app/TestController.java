package org.lazy.app;

import java.util.Collections;
import java.util.List;
import org.lazy.web.HttpMethod;
import org.lazy.web.Response;
import org.lazy.web.annotation.Controller;
import org.lazy.web.annotation.MessageBody;
import org.lazy.web.annotation.PathMapping;
import org.lazy.web.annotation.PathVariable;

@Controller({"/Test"})
public class TestController {
   private TestEntityRepository testEntityRepository;

   public TestController(TestEntityRepository testEntityRepository) {
      this.testEntityRepository = testEntityRepository;
   }

   @PathMapping
   public List<TestEntity> getUsers() {
      return Collections.emptyList();
   }

   @PathMapping(
      path = "/{id}"
   )
   public Response getTestEntity(@PathVariable("id") Integer id) {
      return Response.body((Object)null).status(200).build();
   }

   @PathMapping(
      method = HttpMethod.POST
   )
   public Integer saveUser(@MessageBody TestEntity testEntity) {
      return null;
   }
}

