package com.kaua.ecommerce.domain.event;

public final class EventsTypes {

    public static final String CATEGORY_CREATED = "category_created";
    public static final String CATEGORY_UPDATED = "category_updated";
    public static final String CATEGORY_DELETED = "category_deleted";

    public static final String PRODUCT_CREATED = "product_created";
    public static final String PRODUCT_UPDATED = "product_updated";
    public static final String PRODUCT_DELETED = "product_deleted";

    public static final String INVENTORY_CREATED_ROLLBACK_BY_SKUS = "inventory_created_rollback_by_skus";

    private EventsTypes() {}
}
