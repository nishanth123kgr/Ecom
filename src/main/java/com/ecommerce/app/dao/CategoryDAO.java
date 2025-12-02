package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class CategoryDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("categories l left join categories r on l.parent_id = r.id", List.of("l.id as category_id", "l.name as category_name", "l.description as category_description", "l.parent_id", "r.name as parent_name"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable("create_category('" + data.get("name") + "', '" + data.get("description") + "', " + data.getOrDefault("parent_id", null) + ")", List.of("*"), null)).getFirst();

    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("categories", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("categories", new CriteriaBuilder().build(criteria));
    }
}
