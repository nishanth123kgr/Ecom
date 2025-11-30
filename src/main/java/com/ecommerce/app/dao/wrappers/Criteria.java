package com.ecommerce.app.dao.wrappers;

public class Criteria {

    private String column;
    private Object value;
    private Operator operator;
    private Condition condition;

    public void setValue(Object value) {
        this.value = value;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    private Criteria anotherCriteria;

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String getOperator() {
        return operator.toString();
    }

    public Criteria getAnotherCriteria() {
        return anotherCriteria;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setAnotherCriteria(Criteria anotherCriteria) {
        this.anotherCriteria = anotherCriteria;
    }

    public Criteria(String column, Object value, Operator operator, Condition condition, Criteria anotherCriteria) {
        this.column = column;
        this.value = value;
        this.operator = operator;
        this.anotherCriteria = anotherCriteria;
        this.condition = condition;
    }

    public Criteria(String column, Object value, Operator operator) {
        this.column = column;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return String.format(" %s %s ? ", column, operator) + (anotherCriteria == null ? "" : " " + condition + " " + anotherCriteria);
    }
}
