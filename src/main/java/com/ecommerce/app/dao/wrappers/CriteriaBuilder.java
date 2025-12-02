package com.ecommerce.app.dao.wrappers;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.ecommerce.app.utils.Utils.cast;

public class CriteriaBuilder {
    private Criteria head = null;
    private Criteria tail = null;

    private String sortString = null;

    public Criteria build(Map<String, Object> criteriaMap) {

        String sort = " ORDER BY ";
        String column = null, type = " ASC";

        for (Map.Entry<String, Object> entry : criteriaMap.entrySet()) {
            String key = entry.getKey();
            Object value = cast((String) entry.getValue());

            if (!key.contains("sort")) {
                if (key.startsWith("min")) {
                    String col = key.split("_", 2)[1];
                    Criteria criteria = new Criteria(col, value, Operator.GREATER_THAN_OR_EQUALS);
                    appendCriteriaChain(criteria);
                    continue;
                }
                if (key.startsWith("max")) {
                    String col = key.split("_", 2)[1];
                    Criteria criteria = new Criteria(col, value, Operator.LESSER_THAN_OR_EQUALS);
                    appendCriteriaChain(criteria);
                    continue;
                }
                Criteria criteria = new Criteria(key, value, Operator.EQUALS);
                appendCriteriaChain(criteria);
            } else {
                if (key.contains("type") && ((String) value).startsWith("d")) {
                    type = " DESC";

                }
                if (key.contains("column")) {
                    column = (String) value;
                }

            }

        }
        if (StringUtils.isNotEmpty(column)) {
            sortString = sort + column + type;
        }

        return head;
    }

    private void appendCriteriaChain(Criteria criteria) {
        if (head == null) {
            head = criteria;
            tail = criteria;
            return;
        }
        tail.setCondition(Condition.AND);
        tail.setAnotherCriteria(criteria);
        tail = criteria;
    }




    public String getSortString() {
        return sortString != null ? sortString : "";
    }
}
