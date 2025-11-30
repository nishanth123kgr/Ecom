package com.ecommerce.app.services;

import com.ecommerce.app.dao.SellerDAO;

import java.util.HashMap;
import java.util.Map;

public class SellerService {


    public Map<String, Object> getSellerById(int userId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", userId);

        return new SellerDAO().read(dataMap).getFirst();
    }

}
