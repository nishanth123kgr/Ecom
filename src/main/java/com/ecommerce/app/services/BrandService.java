package com.ecommerce.app.services;

import com.ecommerce.app.dao.AddressDAO;
import com.ecommerce.app.dao.BrandDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandService {

    public Map<String, Object> createBrand(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("name", "description"));
        return new BrandDAO().create(data);
    }

    public List<Map<String, Object>> getAll(Map<String, Object> query) {


        Map<String, String> map = Map.of("brand_id", "id");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);

        return new BrandDAO().readAll(newQuery);

    }

    public Map<String, Object> getBrand(Map<String, Object> params) {

        Map<String, String> map = Map.of("brandId", "id");

        Map<String, Object> newQuery = Utils.mapQueryParams(params, map);
        List<Map<String, Object>> data = new BrandDAO().readAll(newQuery);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "BRAND");
        }

        return data.getFirst();
    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("name", "description", "website"));

        Object brandId = params.remove("brandId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", brandId);


        return new BrandDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object brandId = criteria.remove("brandId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", brandId);

        Map<String, Object> result = new HashMap<>();


        if (new BrandDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }
}
