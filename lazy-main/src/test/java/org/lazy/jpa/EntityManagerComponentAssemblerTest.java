package org.lazy.jpa;

import org.lazy.common.ComponentDefinition;
import org.lazy.jpa.integration.EntityManagerComponentAssembler;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class EntityManagerComponentAssemblerTest {

    @Mock
    private EntityManagerFactory entityManagerFactoryMock;
    @Mock
    private EntityManager entityManagerMock;

    private EntityManagerComponentAssembler componentAssembler;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        componentAssembler = new EntityManagerComponentAssembler();
    }

    @Test
    public void testAssembleComponent() {

        ComponentDefinition definition = Mockito.mock(ComponentDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(definition.getFactoryDefinition().getFactoryComponent()).thenReturn(EntityManagerFactory.class.getName());
        Mockito.when(entityManagerFactoryMock.createEntityManager()).thenReturn(entityManagerMock);

        Object entityManagerProxy =  componentAssembler.assembleComponent(definition, Collections.singletonMap(EntityManagerFactory.class.getName(), entityManagerFactoryMock));

        assertNotNull(entityManagerProxy);
        assertEquals(entityManagerProxy.getClass().getInterfaces(), new Class[]{EntityManager.class});
        Mockito.verify(entityManagerFactoryMock, Mockito.never()).createEntityManager();
        assertNull(EntityManagerContextHolder.getEntityManager());

        ((EntityManager) entityManagerProxy).close();
        Mockito.verify(entityManagerFactoryMock, Mockito.times(1)).createEntityManager();
        Mockito.verify(entityManagerMock, Mockito.times(1)).close();
        assertEquals(EntityManagerContextHolder.getEntityManager(), entityManagerMock);
    }

}