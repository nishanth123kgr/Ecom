package com.ecommerce.app.services;

import com.ecommerce.app.dao.CartDAO;
import com.ecommerce.app.dao.CategoryDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryService {

    public Map<String, Object> createCategory(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("name", "description"));
        return new CategoryDAO().create(data);
    }

    public List<Map<String, Object>> getAll(Map<String, Object> query) {


        Map<String, String> map = Map.of("category_id", "l.id",
                "category_name", "l.name",
                "parent_name", "r.name",
                "parent_id", "r.id",
                "category_description", "l.description");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);

        return new CategoryDAO().readAll(newQuery);

    }

    public Map<String, Object> getCategory(Map<String, Object> params) {

        Map<String, String> map = Map.of("categoryId", "l.id");

        Map<String, Object> newQuery = Utils.mapQueryParams(params, map);
        List<Map<String, Object>> data = new CategoryDAO().readAll(newQuery);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "BRAND");
        }

        return data.getFirst();
    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("name", "description", "parent_id"));

        Object categoryId = params.remove("categoryId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", categoryId);


        return new CategoryDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object categoryId = criteria.remove("categoryId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", categoryId);

        Map<String, Object> result = new HashMap<>();


        if (new CategoryDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }
}
