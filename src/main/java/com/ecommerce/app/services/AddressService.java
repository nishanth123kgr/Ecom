package com.ecommerce.app.services;

import com.ecommerce.app.dao.AddressDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddressService {

    public List<Map<String, Object>> getAll(Map<String, Object> query) {


        Map<String, String> map = Map.of("address_id", "id", "addressId", "id");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);

        return new AddressDAO().readAll(newQuery);

    }

    public Map<String, Object> getAddress(Map<String, Object> query) {

        List<Map<String, Object>> data = getAll(query);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Address");
        }

        return data.getFirst();

    }

    public Map<String, Object> addAddress(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("street", "city", "state", "postal_code", "country"));

        return new AddressDAO().create(data);
    }

    public Map<String, Object> modify(Map<String, Object> params) {

        Utils.checkAnyFieldExists(params, List.of("street", "city", "country", "postal_code", "state"));

        Set<String> NOT_ALLOWED_FIELDS = Set.of("user_id");


        Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS);


        Object addressId = params.remove("addressId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", addressId);


        return new AddressDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object addressId = criteria.remove("addressId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", addressId);

        Map<String, Object> result = new HashMap<>();


        if (new AddressDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }


}
