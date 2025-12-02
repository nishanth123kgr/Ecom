package com.ecommerce.app.services;

import com.ecommerce.app.dao.ProductsDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JoinSpec;
import com.ecommerce.app.utils.RowNester;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductService {

    public Map<String, Object> addProduct(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("name", "description", "brand_id", "category_id"));
        data.put("seller_id", Utils.getSellerID());
        return new ProductsDAO().create(data);
    }

    public List<Map<String, Object>> getAll(Map<String, Object> query) {

        Map<String, String> map = new HashMap<>(Map.of("product_id", "p.id",
                "category_name", "c.name",
                "description", "p.description",
                "is_active", "p.is_active",
                "brand_name", "b.name",
                "category_id", "p.category_id",
                "seller_id", "p.seller_id",
                "seller_store_name", "s.store_name",
                "variant_id", "v.id",
                "base_price", "v.base_price"));

        map.put("productId", "p.id");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);

        List<Map<String, Object>> data = new ProductsDAO().readAll(newQuery);


        return RowNester.nestRows(data, "product", "id", List.of(new JoinSpec("variant", "variants")));
    }

    public Map<String, Object> getProduct(Map<String, Object> params) {

        List<Map<String, Object>> data = getAll(params);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Product");
        }

        return data.getFirst();
    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("name", "description", "category_id", "brand_id", "is_active"));

        Set<String> NOT_ALLOWED_FIELDS = Set.of("seller_id");

        Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS);


        Object productId = params.remove("productId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", productId);


        return new ProductsDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object productId = criteria.remove("productId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", productId);

        Map<String, Object> result = new HashMap<>();


        if (new ProductsDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }

}
