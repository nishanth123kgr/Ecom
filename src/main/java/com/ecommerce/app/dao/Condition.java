package com.ecommerce.app.dao;

public enum Condition {
    AND(" and "),
    OR(" or ");

    private final String condition;

    Condition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}
