package org.lazy.jpa;

public enum HibernateProperties {

    CONNECTION_DRIVER_CLASS("hibernate.connection.driver_class"),
    CONNECTION_URL("hibernate.connection.url"),
    CONNECTION_USERNAME("hibernate.connection.username"),
    CONNECTION_PASSWORD("hibernate.connection.password"),
    CONNECTION_POOL_SIZE("hibernate.connection.pool_size"),
    DIALECT("hibernate.dialect"),
    SHOW_SQL("hibernate.show_sql"),
    FORMAT_SQL("hibernate.format_sql"),
    DEFAULT_SCHEMA("hibernate.default_schema"),
    DEFAULT_CATALOG("hibernate.default_catalog"),
    SESSION_FACTORY_NAME("hibernate.session_factory_name"),
    MAX_FETCH_DEPTH("hibernate.max_fetch_depth"),
    DEFAULT_BATCH_FETCH_SIZE("hibernate.default_batch_fetch_size"),
    DEFAULT_ENTITY_MODE("hibernate.default_entity_mode"),
    ORDER_UPDATES("hibernate.order_updates"),
    GENERATE_STATISTICS("hibernate.generate_statistics"),
    USE_IDENTIFIER_ROLLBACK("hibernate.use_identifier_rollback"),
    USE_SQL_COMMENTS("hibernate.use_sql_comments"),
    ID_NEW_GENERATOR_MAPPINGS("hibernate.id.new_generator_mappings"),
    JDBC_FETCH_SIZE("hibernate.jdbc.fetch_size"),
    JDBC_BATCH_SIZE("hibernate.jdbc.batch_size"),
    JDBC_BATCH_VERSIONED_DATA("hibernate.jdbc.batch_versioned_data"),
    JDBC_FACTORY_CLASS("hibernate.jdbc.factory_class"),
    JDBC_USE_SCROLLABLE_RESULTSET("hibernate.jdbc.use_scrollable_resultset"),
    JDBC_USE_STREAMS_FOR_BINARY("hibernate.jdbc.use_streams_for_binary"),
    JDBC_USE_GET_GENERATED_KEYS("hibernate.jdbc.use_get_generated_keys"),
    CONNECTION_PROVIDER_CLASS("hibernate.connection.provider_class"),
    CONNECTION_ISOLATION("hibernate.connection.isolation"),
    CONNECTION_AUTOCOMMIT("hibernate.connection.autocommit"),
    CONNECTION_RELEASE_MODE("hibernate.connection.release_mode"),
    CACHE_PROVIDER_CLASS("hibernate.cache.provider_class"),
    CACHE_USE_MINIMAL_PUTS("hibernate.cache.use_minimal_puts"),
    CACHE_USE_QUERY_CACHE("hibernate.cache.use_query_cache"),
    CACHE_USE_SECOND_LEVEL_CACHE("hibernate.cache.use_second_level_cache"),
    CACHE_QUERY_CACHE_FACTORY("hibernate.cache.query_cache_factory"),
    CACHE_REGION_PREFIX("hibernate.cache.region_prefix"),
    CACHE_USE_STRUCTURED_ENTRIES("hibernate.cache.use_structured_entries"),
    CACHE_AUTO_EVICT_COLLECTION_CACHE("hibernate.cache.auto_evict_collection_cache"),
    CACHE_DEFAULT_CACHE_CONCURRENCY_STRATEGY("hibernate.cache.default_cache_concurrency_strategy"),
    TRANSACTION_FACTORY_CLASS("hibernate.transaction.factory_class"),
    TRANSACTION_MANAGER_LOOKUP_CLASS("hibernate.transaction.manager_lookup_class"),
    TRANSACTION_FLUSH_BEFORE_COMPLETION("hibernate.transaction.flush_before_completion"),
    TRANSACTION_AUTO_CLOSE_SESSION("hibernate.transaction.auto_close_session"),
    CURRENT_SESSION_CONTEXT_CLASS("hibernate.current_session_context_class"),
    QUERY_FACTORY_CLASS("hibernate.query.factory_class"),
    QUERY_SUBSTITUTIONS("hibernate.query.substitutions"),
    HBM2DDL_AUTO("hibernate.hbm2ddl.auto"),
    HBM2DDL_IMPORT_FILES("hibernate.hbm2ddl.import_files"),
    HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR("hibernate.hbm2ddl.import_files_sql_extractor"),
    BYTECODE_USE_REFLECTION_OPTIMIZER("hibernate.bytecode.use_reflection_optimizer"),
    BYTECODE_PROVIDER("hibernate.bytecode.provider"),
    EJB_NAMING_STRATEGY("hibernate.ejb.naming_strategy"),
    EJB_NAMING_STRATEGY_DELEGATOR("hibernate.ejb.naming_strategy_delegator");

    private final String propertyName;

    HibernateProperties(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
