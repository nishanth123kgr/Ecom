package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class BrandDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("brands", List.of("id as brand_id", "name", "description", "website"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {

        return readAll(query);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable("create_brand('" + data.get("name") + "', '" + data.get("description") + "')", List.of("*"), null)).getFirst();

    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("brands", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("brands", new CriteriaBuilder().build(criteria));
    }

}
