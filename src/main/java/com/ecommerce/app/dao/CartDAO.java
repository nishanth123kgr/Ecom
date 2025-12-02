package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class CartDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("cart_items c " +
                        "join product_variants v on c.product_variant_id = v.id " +
                        "join products p on v.product_id = p.id",

                List.of("c.id as cart_item_id",
                        "p.name as product_name",
                        "p.description",
                        "v.attributes",
                        "v.base_price",
                        "c.quantity",
                        "v.base_price * c.quantity as total_price"),
                criteria,
                sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable("add_cart_item(" + data.get("product_variant_id") + ", " + data.get("quantity") + ")", List.of("*"), null)).getFirst();
    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("cart_items", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("cart_items", new CriteriaBuilder().build(criteria));
    }
}
