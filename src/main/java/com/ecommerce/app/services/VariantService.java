package com.ecommerce.app.services;

import com.ecommerce.app.dao.VariantsDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariantService {

    public Map<String, Object> createVariant(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("productId", "price", "attributes"));

        return new VariantsDAO().create(data);
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
        map.put("productVariantId", "v.id");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);


        return new VariantsDAO().readAll(newQuery);
    }

    public Map<String, Object> getVariant(Map<String, Object> query) {
        List<Map<String, Object>> data = getAll(query);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Variant");
        }

        return data.getFirst();

    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("base_price", "attributes"));

        Set<String> NOT_ALLOWED_FIELDS = Set.of("product_id");

        Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS);


        Object productVariantId = params.remove("productVariantId");
        params.remove("productId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", productVariantId);


        return new VariantsDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object variantId = criteria.remove("productVariantId");
        Object productId = criteria.remove("productId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", variantId);
        criteriaMap.put("product_id", productId);

        Map<String, Object> result = new HashMap<>();


        if (new VariantsDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }
}
