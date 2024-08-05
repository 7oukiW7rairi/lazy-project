package org.lazy.jpa;

import org.lazy.web.DummyTestClass;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static org.testng.Assert.*;

// TODO change it to integration test with a real entitymanager object
public class AbstractJpaRepositoryTest {

    private AbstractJpaRepository<DummyTestClass, Integer> abstractJpaRepository;

    @Mock
    private EntityManager entityManager;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindOne() {
        DummyTestClass dummyTestClass = new DummyTestClass(1, "dummy", true, null);
        Mockito.when(entityManager.find(DummyTestClass.class,1)).thenReturn(dummyTestClass);
        abstractJpaRepository = new TestJpaRepository(entityManager);

        DummyTestClass expected = abstractJpaRepository.findOne(1);

        assertEquals(expected.getId(), dummyTestClass.getId());
    }

    @Test
    public void testFindAll() {
        DummyTestClass dummyTestClass = new DummyTestClass(5, "dummy", false, null);
        Query query = Mockito.mock(Query.class);
        Mockito.when(query.getResultList()).thenReturn(Collections.singletonList(dummyTestClass));
        Mockito.when(entityManager.createQuery("from " + DummyTestClass.class.getName())).thenReturn(query);
        abstractJpaRepository = new TestJpaRepository(entityManager);

        List<DummyTestClass> expected = abstractJpaRepository.findAll();

        assertEquals(expected.size(), 1);
        assertEquals(expected.get(0).getId(), 5);
    }

    @Test
    public void testCreate() {
    }

    @Test
    public void testUpdate() {
    }

    @Test
    public void testDelete() {
    }

    @Test
    public void testDeleteById() {
    }

    @Test
    public void testCount() {
    }

    @Test
    public void testExist() {
    }
}