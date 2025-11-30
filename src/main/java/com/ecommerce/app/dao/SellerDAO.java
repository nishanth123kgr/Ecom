package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.Operator;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class SellerDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        int userId = (int) query.get("userId");

        Criteria criteria = new Criteria("id", userId, Operator.EQUALS);

        return (List<Map<String, Object>>) getDataFromTable("sellers", List.of("id"), criteria);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return Map.of();
    }

    @Override
    public List<Map<String, Object>> update(Map<String, String> data) {
        return List.of();
    }

    @Override
    public boolean delete(Map<String, String> data) {
        return false;
    }

    public int getSellerIdByUserId(int userId, Connection... connections) {
        Criteria criteria = new Criteria("user_id", userId, Operator.EQUALS);

        List<Map<String, Object>> sellerIds;

        if (connections.length == 0) {
            sellerIds = (List<Map<String, Object>>) getDataFromTable("sellers", List.of("id"), criteria);
        } else {
            sellerIds = (List<Map<String, Object>>) getDataFromTable(connections[0], "sellers", List.of("id"), criteria);
        }

        if (sellerIds == null || sellerIds.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Seller");
        }

        return (int) sellerIds.getFirst().get("id");

    }


}
