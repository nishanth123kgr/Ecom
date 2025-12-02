package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;
import com.ecommerce.app.dao.wrappers.Operator;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class SellerDAO extends DAO {

    private static final String TABLE = "sellers";

    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable(TABLE + " s join users u on s.user_id = u.id left join requests r on r.seller_id = s.id", List.of("s.id as seller_id", "r.status as request_status", "s.store_name", "s.store_desc as store_description", "s.gst_number", "s.pan_number", "u.name"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        int userId = (int) query.get("userId");

        Criteria criteria = new Criteria("id", userId, Operator.EQUALS);

        return (List<Map<String, Object>>) getDataFromTable(TABLE, List.of("id"), criteria);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        return Map.of();
    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update(TABLE, data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> data) {
        return (boolean) ((List<Map<String, Object>>) getDataFromTable("delete_seller(" + data.get("sellerId") + ")", List.of("*"), null)).getFirst().get("success");
    }

    public int getSellerIdByUserId(int userId, Connection... connections) {
        Criteria criteria = new Criteria("user_id", userId, Operator.EQUALS);

        List<Map<String, Object>> sellerIds;

        if (connections.length == 0) {
            sellerIds = (List<Map<String, Object>>) getDataFromTable(TABLE, List.of("id"), criteria);
        } else {
            sellerIds = (List<Map<String, Object>>) getDataFromTable(connections[0], TABLE, List.of("id"), criteria);
        }

        if (sellerIds == null || sellerIds.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Seller");
        }

        return (int) sellerIds.getFirst().get("id");

    }


}
