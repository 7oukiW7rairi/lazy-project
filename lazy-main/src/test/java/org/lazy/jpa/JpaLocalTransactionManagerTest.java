package org.lazy.jpa;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.testng.Assert.*;

public class JpaLocalTransactionManagerTest {

    private LocalTransactionManager jpaLocalTransactionManager;

    @Mock
    private EntityManagerFactory entityManagerFactoryMock;

    @Mock
    private EntityManager entityManagerMock;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTransactionRequired() {
        Mockito.when(entityManagerFactoryMock.createEntityManager()).thenReturn(Mockito.mock(EntityManager.class));
        EntityManagerContextHolder.setEntityManager(entityManagerMock);

        jpaLocalTransactionManager = new JpaLocalTransactionManager(entityManagerFactoryMock);
        Transaction transaction = jpaLocalTransactionManager.getTransaction(TransactionType.REQUIRED);

        assertNotNull(transaction);
        Mockito.verify(entityManagerFactoryMock, Mockito.never()).createEntityManager();
        assertEquals(EntityManagerContextHolder.getEntityManager(), entityManagerMock);
    }

    @Test
    public void testGetTransactionRequiredNew() {
        EntityManager entityManagerMock2 = Mockito.mock(EntityManager.class);
        Mockito.when(entityManagerFactoryMock.createEntityManager()).thenReturn(entityManagerMock2);
        EntityManagerContextHolder.setEntityManager(entityManagerMock);

        jpaLocalTransactionManager = new JpaLocalTransactionManager(entityManagerFactoryMock);
        Transaction transaction = jpaLocalTransactionManager.getTransaction(TransactionType.REQUIRES_NEW);

        assertNotNull(transaction);
        Mockito.verify(entityManagerFactoryMock, Mockito.times(1)).createEntityManager();
        assertEquals(EntityManagerContextHolder.getEntityManager(), entityManagerMock2);
    }

    @Test
    public void testGetTransactionNone() {
        jpaLocalTransactionManager = new JpaLocalTransactionManager(entityManagerFactoryMock);
        Transaction transaction = jpaLocalTransactionManager.getTransaction(TransactionType.NEVER);

        assertNull(transaction);
        Mockito.verify(entityManagerFactoryMock, Mockito.never()).createEntityManager();
    }
}