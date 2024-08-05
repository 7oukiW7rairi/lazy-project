package org.lazy.jpa;

public interface LocalTransactionManager {

    Transaction getTransaction(TransactionType transactionType);
}
