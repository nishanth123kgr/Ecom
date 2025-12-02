package com.ecommerce.app.services;

import com.ecommerce.app.dao.BrandDAO;
import com.ecommerce.app.dao.CartDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartService {

    public Map<String, Object> addCartItem(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("product_variant_id", "quantity"));

        return new CartDAO().create(data);
    }

    public List<Map<String, Object>> getAll(Map<String, Object> data) {

        Map<String, String> map = Map.of("name", "p.name",
                "description", "p.description",
                "base_price", "v.base_price",
                "quantity", "c.quantity",
                "total_price", "total_price",
                "cart_item_id", "c.id",
                "cartItemId", "c.id");

        return new CartDAO().readAll(Utils.mapQueryParams(data, map));

    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("quantity"));

        Set<String> NOT_ALLOWED_FIELDS = Set.of("cart_id", "product_variant_id");


        Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS);


        Object cartItemId = params.remove("cartId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", cartItemId);


        return new CartDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object cartId = criteria.remove("cartId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", cartId);

        Map<String, Object> result = new HashMap<>();


        if (new CartDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }
}
