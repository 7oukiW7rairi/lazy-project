package org.lazy.app;

import java.util.List;
import org.lazy.jpa.JpaRepository;
import org.lazy.jpa.Query;

public interface TestEntityRepository extends JpaRepository<TestEntity, Integer> {
   @Query("update TestEntity i set i.name = ?2 where i.id = ?1")
   void updateName(int var1, String var2);

   @Query("select i from TestEntity i where i.name = ?1")
   TestEntity findEntityByName(String var1);

   @Query("select lower(i.title) from Item i")
   List<String> findAllLowercaseTitles();

   List<TestEntity> getSearchListTestEntities(int var1, boolean var2, Integer var3, String var4);
}

