package com.ecommerce.app.services;

import com.ecommerce.app.dao.RequestDAO;
import com.ecommerce.app.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestService {


    public Map<String, Object> createRequest(Map<String, Object> params) {

        Utils.checkMandatoryFields(params, List.of("store_name", "store_description", "gst_number", "pan_number", "request_description"));

        return new RequestDAO().create(params);

    }

    public Map<String, Object> modifyRequest(Map<String, Object> params, String role) {

        Set<String> NOT_ALLOWED_FIELDS_FOR_SELLER = Set.of("status", "seller_id");

        if (role.equals("seller")) {
            Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS_FOR_SELLER);
        }

        return null;


    }

}
