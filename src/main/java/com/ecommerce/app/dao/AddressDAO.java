package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class AddressDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("addresses", List.of("id as address_id", "street", "city", "state", "country", "postal_code", "user_id"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable(
                String.format("add_address('%s', '%s', '%s', '%s', '%s')",
                        data.get("street"),
                        data.get("city"),
                        data.get("state"),
                        data.get("postal_code"),
                        data.get("country")
                ), List.of("*"), null)).getFirst();

    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("addresses", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("addresses", new CriteriaBuilder().build(criteria));
    }
}
