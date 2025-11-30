package com.ecommerce.app.utils;

/**
 * Specification for a join/child collection to be nested under a parent.
 */
public class JoinSpec {
    public final String prefix;           // e.g. "address" (columns like address_id, address_street)
    public final String collectionName;   // e.g. "addresses" (target key on parent)
    public final String parentPrefix;     // e.g. "user" (which parent to attach to). null -> rootPrefix
    public final String idColumnSuffix;   // usually "id" (so full column is prefix + "_" + idColumnSuffix)

    public JoinSpec(String prefix, String collectionName, String parentPrefix, String idColumnSuffix) {
        this.prefix = prefix;
        this.collectionName = collectionName;
        this.parentPrefix = parentPrefix;
        this.idColumnSuffix = idColumnSuffix;
    }

    public JoinSpec(String prefix, String collectionName) {
        this(prefix, collectionName, null, "id");
    }
}
