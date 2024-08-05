package org.lazy.core;

import javax.transaction.Transactional;

@Transactional
public class TransactionalDummyComponent {

    public void doSomethingTransactional() {

    }

    @Transactional(Transactional.TxType.NEVER)
    public void doSomethingNotTransactional() {

    }

    public void doSomethingThrowException() {
        throw new RuntimeException("Dummy exception");
    }
}
