package com.ecommerce.app.dao;

public enum Operator {
    EQUALS("="),
    NOT_EQUALS("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    LESSER_THAN("<"),
    LESSER_THAN_OR_EQUALS("<="),
    LIKE("LIKE");

    private final String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return this.operator;
    }
}
