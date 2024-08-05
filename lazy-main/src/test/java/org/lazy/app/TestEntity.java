package org.lazy.app;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestEntity implements Serializable {
   private Integer id;
   private String name;

   public TestEntity() {
   }

   public void setId(Integer id) {
      this.id = id;
   }

   @Id
   public Integer getId() {
      return this.id;
   }
}

