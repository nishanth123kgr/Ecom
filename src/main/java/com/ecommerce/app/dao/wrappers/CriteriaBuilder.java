package com.ecommerce.app.dao.wrappers;

import java.util.Map;

public class CriteriaBuilder {
    private final Criteria head = null;
    private Criteria tail = null;

    public void setCriteria(Map<String, String> criteriaMap) {
        for (Map.Entry<String, String> entry : criteriaMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
        }
    }

    private void appendCriteriaChain(Criteria criteria) {
        tail.setCondition(Condition.AND);
        tail.setAnotherCriteria(criteria);
        tail = criteria;
    }

    public String build() {
        return head != null ? head.toString() : "";
    }
}
