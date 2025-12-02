package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JSONUtils;
import com.ecommerce.app.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class RequestDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        Criteria criteria = criteriaBuilder.build(query);

        String sortString = criteriaBuilder.getSortString();

        return (List<Map<String, Object>>) getDataFromTable("requests r inner join sellers s on r.seller_id = s.id", List.of("r.id as request_id", "r.status as request_status", "r.request_description", "s.id as seller_id", "s.store_name", "s.store_desc as store_description", "s.gst_number", "s.pan_number"), criteria, sortString);
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        String gstNumber = (String) data.get("gst_number");
        String panNumber = (String) data.get("pan_number");
        Function<Connection, Object> createRequest = connection -> {
            try {
                String createSellerRequest = "Select * from create_seller_request(?, ?, ?, ?, ?);";

                try (PreparedStatement stmt = connection.prepareStatement(createSellerRequest)) {

                    stmt.setString(1, (String) data.get("store_name"));
                    stmt.setString(2, (String) data.get("store_description"));
                    stmt.setString(3, gstNumber);
                    stmt.setString(4, panNumber);
                    stmt.setString(5, (String) data.get("request_description"));

                    ResultSet rs = stmt.executeQuery();

                    rs.next();

                    Map<String, Object> result = JSONUtils.getRowMapFromResultSet(rs);

                    if (!(boolean) result.get("success")) {
                        throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, result.get("message"));
                    }
                    return result;
                }

            } catch (SQLException e) {
                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }

        };

        return (Map<String, Object>) execute(createRequest, Utils.getUserID());
    }


    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("requests", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("requests", new CriteriaBuilder().build(criteria));
    }

    public Map<String, Object> approve(Map<String, Object> data) {
        return ((List<Map<String, Object>>) getDataFromTable("approve_seller_request(" + data.get("requestId") + ")", List.of("*"), null)).getFirst();
    }


}
