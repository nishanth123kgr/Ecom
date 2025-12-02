package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;
import com.ecommerce.app.utils.Utils;

import java.util.List;
import java.util.Map;

public class ProductsDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable(
                "products p " +
                        "join brands b on p.brand_id = b.id " +
                        "join categories c on p.category_id = c.id " +
                        "left join product_variants v on v.product_id = p.id",
                List.of("p.name        as product_name",
                        "p.id as product_id",
                        "p.description as product_description",
                        "p.is_active   as product_is_active",
                        "p.brand_id    as product_brand_id",
                        "b.name        as product_brand_name",
                        "p.category_id as product_category_id",
                        "c.name        as product_category_name",
                        "v.id          as variant_id",
                        "v.attributes  as variant_attributes",
                        "v.base_price  as variant_base_price",
                        "v.is_active   as variant_is_active"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        if (Utils.isSeller()) {
            return ((List<Map<String, Object>>) getDataFromTable("add_product_seller('" + data.get("name") + "', '" + data.get("description") + "', " + data.get("brand_id") + ", " + data.get("category_id") + ")", List.of("*"), null)).getFirst();
        }
        return ((List<Map<String, Object>>) getDataFromTable("add_product('" + data.get("name") + "', '" + data.get("description") + "', " + data.get("brand_id") + ", " + data.get("category_id") + ", " + data.get("seller_id") + ")", List.of("*"), null)).getFirst();
    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("products", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("products", new CriteriaBuilder().build(criteria));
    }


}
