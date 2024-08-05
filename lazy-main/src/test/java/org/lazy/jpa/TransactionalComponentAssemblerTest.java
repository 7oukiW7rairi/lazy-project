package org.lazy.jpa;

import org.lazy.common.ConstructorDefinition;
import org.lazy.common.ComponentDefinition;
import org.lazy.core.ComponentFromConfig;
import org.lazy.core.DummyInterface;
import org.lazy.jpa.integration.TransactionalComponentAssembler;
import org.lazy.common.ComponentType;
import org.lazy.common.SetterDefinition;
import org.lazy.core.TransactionalDummyComponent;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class TransactionalComponentAssemblerTest {

    private TransactionalComponentAssembler componentAssembler;

    @Mock
    private ComponentDefinition definition;
    @Mock
    private LocalTransactionManager localTransactionManager;
    @Mock
    private Transaction transaction;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        componentAssembler = new TransactionalComponentAssembler();
    }

    @Test
    public void testAssembleComponentImplementingInterface() {

        prepareAssembleComponentTestCase();

        Mockito.when(localTransactionManager.getTransaction(TransactionType.REQUIRED)).thenReturn(transaction);
        Object proxyInstance = componentAssembler.assembleComponent(definition,
                Map.of("org.lazy.core.ComponentFromConfig", new ComponentFromConfig(),
                        LocalTransactionManager.class.getName(), localTransactionManager));

        assertNotNull(proxyInstance);
        assertTrue(Proxy.isProxyClass(proxyInstance.getClass()));
        assertEquals(proxyInstance.getClass().getInterfaces(), new Class[]{DummyInterface.class});

        ((DummyInterface) proxyInstance).doSomethingDummy();
        Mockito.verify(transaction, Mockito.times(1)).begin();
        Mockito.verify(transaction, Mockito.times(1)).commit();
        Mockito.verify(transaction, Mockito.times(1)).clean();
    }

    @Test
    public void testAssembleComponentOverrideTransactionClassAnnotation() {

        prepareAssembleComponentTestCase();

        Mockito.when(localTransactionManager.getTransaction(TransactionType.NEVER)).thenReturn(null);
        Object proxyInstance = componentAssembler.assembleComponent(definition,
                Map.of("org.lazy.core.ComponentFromConfig", new ComponentFromConfig(),
                        LocalTransactionManager.class.getName(), localTransactionManager));

        assertNotNull(proxyInstance);
        assertTrue(Proxy.isProxyClass(proxyInstance.getClass()));
        assertEquals(proxyInstance.getClass().getInterfaces(), new Class[]{DummyInterface.class});

        ((DummyInterface) proxyInstance).doSomethingDummyNotTransactional();
        Mockito.verify(transaction, Mockito.never()).begin();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).clean();
    }

    @Test
    public void testAssembleComponentNotImplementingInterface() {

        ConstructorDefinition constructorDefinition = Mockito.mock(ConstructorDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(constructorDefinition.getParameterTypes()).thenReturn(Collections.emptyList());

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.TransactionalDummyComponent");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.SINGLETON);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies()).thenReturn(Collections.singletonList(LocalTransactionManager.class.getName()));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(null);
        Mockito.when(definition.getConstructor()).thenReturn(constructorDefinition);
        Mockito.when(definition.getSetters()).thenReturn(Collections.emptyList());

        Mockito.when(localTransactionManager.getTransaction(TransactionType.REQUIRED)).thenReturn(transaction);
        Object proxyInstance = componentAssembler.assembleComponent(definition,
                Collections.singletonMap(LocalTransactionManager.class.getName(), localTransactionManager));

        assertNotNull(proxyInstance);
        assertEquals(proxyInstance.getClass().getSuperclass(), TransactionalDummyComponent.class);

        assertThrows(JpaException.class, ((TransactionalDummyComponent) proxyInstance)::doSomethingThrowException);
        Mockito.verify(transaction, Mockito.times(1)).begin();
        Mockito.verify(transaction, Mockito.never()).commit();
        //Mockito.verify(transaction, Mockito.times(1)).rollback();
        Mockito.verify(transaction, Mockito.times(1)).clean();
    }

    private void prepareAssembleComponentTestCase() {
        ConstructorDefinition constructorDefinition = Mockito.mock(ConstructorDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(constructorDefinition.getParameterTypes()).thenReturn(Collections.emptyList());

        SetterDefinition setterDefinition = Mockito.mock(SetterDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(setterDefinition.getSetterName()).thenReturn("setComponent");
        Mockito.when(setterDefinition.getParameterType()).thenReturn("org.lazy.core.ComponentFromConfig");

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.DummyComponent");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.SINGLETON);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies()).thenReturn(Arrays.asList("org.lazy.core.ComponentFromConfig", LocalTransactionManager.class.getName()));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(null);
        Mockito.when(definition.getConstructor()).thenReturn(constructorDefinition);
        Mockito.when(definition.getSetters()).thenReturn(Collections.singletonList(setterDefinition));
    }
}