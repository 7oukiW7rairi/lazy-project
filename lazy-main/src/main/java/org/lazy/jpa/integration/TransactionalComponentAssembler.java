package org.lazy.jpa.integration;

import com.google.auto.service.AutoService;
import org.lazy.core.ComponentAssembler;
import org.lazy.core.DefaultComponentAssembler;
import org.lazy.common.ComponentDefinition;
import org.lazy.core.CoreException;
import org.lazy.common.ComponentProxy;
import org.lazy.jpa.JpaException;
import org.lazy.jpa.Transaction;
import org.lazy.jpa.LocalTransactionManager;
import org.lazy.jpa.TransactionType;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;

import static org.lazy.common.StreamUtils.rethrowBiFunction;

@AutoService(ComponentAssembler.class)
public class TransactionalComponentAssembler implements ComponentAssembler<ComponentDefinition> {

    private final ComponentAssembler defaultComponentAssembler;

    public TransactionalComponentAssembler() {
        this.defaultComponentAssembler = new DefaultComponentAssembler();
    }

    @Override
    public Object assembleComponent(ComponentDefinition definition, Map<String, Object> dependencies) throws CoreException {
        Object componentInstance = defaultComponentAssembler.assembleComponent(definition, dependencies);
        LocalTransactionManager localTransactionManager = (LocalTransactionManager) dependencies.get(LocalTransactionManager.class.getCanonicalName());
        Class<?> clazz = componentInstance.getClass();
        if (clazz.getInterfaces().length > 0) {
            return Proxy.newProxyInstance(
                clazz.getInterfaces()[0].getClassLoader(),
                clazz.getInterfaces(),
                (proxy, method, args) -> transactionWorkflow(
                        rethrowBiFunction(method::invoke),
                        componentInstance,
                        args,
                        getTransaction(localTransactionManager, componentInstance.getClass(), method)));
        } else {
            // TODO create a transaction manager contains the enity manager factory to manage jpa transaction
            try {
                ProxyFactory factory = new ProxyFactory();
                factory.setSuperclass(clazz);
                Class<?> proxyClass = factory.createClass();
                Object instance = proxyClass.newInstance();
                ((ProxyObject) instance).setHandler((self, overridden, forwarder,args) -> transactionWorkflow(
                        rethrowBiFunction(overridden::invoke),
                        componentInstance,
                        args,
                        getTransaction(localTransactionManager, componentInstance.getClass(), forwarder)));
                return clazz.cast(instance);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new CoreException(e.getMessage());
            }
        }
    }

    @Override
    public boolean canAssemble(ComponentDefinition componentDefinition) {
        return ComponentProxy.TRANSACTIONAL == componentDefinition.getComponentProxy();
    }

    private Object transactionWorkflow(BiFunction<Object, Object[], Object> method, Object object, Object[] args, Transaction transaction) {
        Object returnObject;
        if (transaction == null || transaction.isActive()) {
            returnObject = method.apply(object, args);
        } else {
            try {
                transaction.begin();
                returnObject = method.apply(object, args);
                transaction.commit();
            } catch (RuntimeException e) {
                // TODO use rollbackOnly check here instead of isActive
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw new JpaException(e);
            } finally {
                transaction.clean();
            }
        }
        return returnObject;
    }

    private Transaction getTransaction(LocalTransactionManager localTransactionManager, Class<?> clazz, Method method) {
        Transactional annotation = Optional.ofNullable(method.getAnnotation(Transactional.class))
                .orElseGet(() -> clazz.getAnnotation(Transactional.class));
         return localTransactionManager.getTransaction(TransactionType.of(annotation.value().toString()));
    }
}
