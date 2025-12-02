package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class OrdersDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("orders o " +
                        "join order_items i on o.id = i.order_id " +
                        "join product_variants v on i.product_variant_id = v.id " +
                        "join products p on v.product_id = p.id",

                List.of("i.id as item_id",
                        "p.name as item_name",
                        "p.description as item_description",
                        "v.attributes as item_attributes",
                        "i.quantity as item_quantity",
                        "i.total_price as item_total_price",
                        "v.base_price as item_price",
                        "o.id as order_id",
                        "o.status as order_status",
                        "o.order_date as order_order_date",
                        "o.total_amount as order_total_amount"),
                criteria,
                sortString);
    }


    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable("convert_cart(" + data.get("address_id") + ")", List.of("*"), null)).getFirst();
    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("orders", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> data) {
        return false;
    }


}
