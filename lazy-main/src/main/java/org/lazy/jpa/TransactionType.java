package org.lazy.jpa;

public enum TransactionType {

    REQUIRED,
    REQUIRES_NEW,
    MANDATORY,
    NOT_SUPPORTED,
    NEVER;

    public static TransactionType of(String transactionType) {
        if (transactionType == null) {
            return NEVER;
        }
        return valueOf(transactionType);
    }

}
