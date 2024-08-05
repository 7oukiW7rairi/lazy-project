package org.lazy.jpa;

import org.lazy.core.DummyComponent;

import java.util.*;

public interface JpaRepositoryTest extends JpaRepository<Integer, DummyEntityTest> {

    List<DummyComponent> getComponentFromConfig(Long param);
}
