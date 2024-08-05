package org.lazy.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JpaLocalTransactionManager implements LocalTransactionManager {


    private EntityManagerFactory entityManagerFactory;

    public JpaLocalTransactionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Transaction getTransaction(TransactionType transactionType) {
        /* TODO create a new Transaction class to store the EntityTransaction information extracted from method @Transactional annotation
            and change the implementation to support more transation type like Transactional.TxType.REQUIRES_NEW
            using a stack to save the transaction in the context, for each new call a transactional method we check the propagation type;
             if the propagation is required, we retreive the last one and use it
             if the propagation is require new, save a new one to the stack
        */
        Transaction transaction = null;
        switch (transactionType) {
            case REQUIRED:
                EntityManager entityManager = EntityManagerContextHolder.getEntityManager();
                if (entityManager == null) {
                    entityManager = entityManagerFactory.createEntityManager();
                    EntityManagerContextHolder.setEntityManager(entityManager);
                }
                transaction = new JpaTransaction(entityManager);
                break;
            case REQUIRES_NEW:
                entityManager = entityManagerFactory.createEntityManager();
                transaction = new JpaTransaction(entityManager);
                EntityManagerContextHolder.setEntityManager(entityManager);
                break;
            case MANDATORY:
                break;
            case NOT_SUPPORTED:
                break;
            case NEVER:
                break;
        }
        return transaction;
    }


}
