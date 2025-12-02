package com.ecommerce.app.services;

import com.ecommerce.app.dao.RequestDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestService {


    public Map<String, Object> createRequest(Map<String, Object> params) {

        Utils.checkMandatoryFields(params, List.of("store_name", "store_description", "gst_number", "pan_number", "request_description"));

        return new RequestDAO().create(params);

    }

    public Map<String, Object> modifyRequest(Map<String, Object> params) {

        if (params.isEmpty() || (!params.containsKey("status") && !params.containsKey("seller_id") && !params.containsKey("description"))) {
            throw new APIException(ErrorCodes.MISSING_PARAM, "Update param");
        }

        Set<String> NOT_ALLOWED_FIELDS_FOR_SELLER = Set.of("status", "seller_id");

        if (Utils.getRole().equals("seller")) {
            Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS_FOR_SELLER);
        }

        Object reqId = params.remove("requestId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", reqId);


        return new RequestDAO().update(params, criteriaMap).getFirst();
    }

    public List<Map<String, Object>> getAll(Map<String, Object> query) {


        Map<String, String> map = Map.of("seller_id", "r.seller_id", "request_id", "r.id", "requestId", "r.id", "request_status", "r.status", "store_name", "s.store_name", "store_description", "s.store_desc", "gst_number", "s.gst_number", "pan_number", "s.pan_number");

        Map<String, Object> newQuery = Utils.mapQueryParams(query, map);

        return new RequestDAO().readAll(newQuery);

    }

    public Map<String, Object> getRequest(Map<String, Object> query) {
        List<Map<String, Object>> data = getAll(query);
        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Request");
        }
        return data.getFirst();
    }


    public Map<String, Object> approveRequest(Map<String, Object> query) {
        return new RequestDAO().approve(query);
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object requestId = criteria.remove("requestId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", requestId);

        Map<String, Object> result = new HashMap<>();


        if (new RequestDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }

}
