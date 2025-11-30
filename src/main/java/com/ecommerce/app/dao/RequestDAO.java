package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.Operator;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.Utils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class RequestDAO extends DAO {
    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {
        int userId = Utils.getUserID();
        String gstNumber = (String) data.get("gst_number");
        String panNumber = (String) data.get("pan_number");
        Function<Connection, Object> createRequest = connection -> {
            try {
                String sellerInsertQuery = "INSERT INTO SELLERS (STORE_NAME, STORE_DESC, GST_NUMBER, PAN_NUMBER, USER_ID) VALUES (?,?,?,?,?);";

                try (PreparedStatement stmt = connection.prepareStatement(sellerInsertQuery)) {

                    stmt.setString(1, (String) data.get("store_name"));
                    stmt.setString(2, (String) data.get("store_description"));
                    stmt.setString(3, gstNumber);
                    stmt.setString(4, panNumber);
                    stmt.setInt(5, userId);

                    int sellerInsertStatus = stmt.executeUpdate();

                    if (sellerInsertStatus < 1) {
                        throw new SQLException("Seller Request Creation Failed");
                    }

                    int sellerId = new SellerDAO().getSellerIdByUserId(userId, connection);

                    String requestInsertQuery = "INSERT INTO REQUESTS (SELLER_ID) VALUES (?);";

                    try (PreparedStatement reqStmt = connection.prepareStatement(requestInsertQuery)) {
                        reqStmt.setInt(1, sellerId);

                        int requestInsertStatus = reqStmt.executeUpdate();

                        if (requestInsertStatus < 1) {
                            throw new SQLException("Seller Request Creation Failed _");
                        }

                        String updateUserRole = "update users set role = 'seller' where id = ?";

                        try (PreparedStatement updateUserStmt = connection.prepareStatement(updateUserRole)) {
                            updateUserStmt.setInt(1, userId);

                            if (updateUserStmt.executeUpdate() < 1) {
                                throw new SQLException("Seller Request Creation Failed _");
                            }

                        }

                        Map<String, Object> requestIdMap = getLatestRequestId(sellerId, connection);
                        requestIdMap.put("seller_id", sellerId);

                        connection.commit();

                        return requestIdMap;

                    }


                }


            } catch (PSQLException e) {
                Utils.safeRollBack(connection);

                String sqlState = e.getSQLState();
                if ("23505".equals(sqlState)) {
                    String constraint = getConstraint(e);

                    if ("sellers_gst_number_key".equals(constraint) || (constraint == null && e.getMessage().contains("sellers_gst_number_key"))) {
                        throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, "GST number already exists: " + gstNumber);
                    }

                    if ("sellers_pan_number_key".equals(constraint) || (constraint == null && e.getMessage().contains("sellers_pan_number_key"))) {
                        throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, "PAN number already exists: " + panNumber);
                    }

                    throw new APIException(ErrorCodes.CUSTOM_CLIENT_ERROR, "Duplicate value violates unique constraint" + (constraint != null ? ": " + constraint : ""));
                }

                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            } catch (SQLException e) {
                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }

        };

        return (Map<String, Object>) execute(createRequest, Utils.getUserID());
    }

    private static String getConstraint(PSQLException e) {
        ServerErrorMessage serverMsg = e.getServerErrorMessage();
        return serverMsg != null ? serverMsg.getConstraint() : null;
    }

    @Override
    public List<Map<String, Object>> update(Map<String, String> data) {
        return List.of();
    }

    @Override
    public boolean delete(Map<String, String> data) {
        return false;
    }

    public Map<String, Object> getLatestRequestId(int sellerId, Connection... connections) {


        Criteria criteria = new Criteria("seller_id", sellerId, Operator.EQUALS);

        List<Map<String, Object>> rows;

        if (connections.length == 0) {
            rows = (List<Map<String, Object>>) getDataFromTable("requests", List.of("id"), criteria);
        } else {
            rows = (List<Map<String, Object>>) getDataFromTable(connections[0], "requests", List.of("max(id)"), criteria, " group by seller_id");
        }


        if (rows.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Request");
        }

        Map<String, Object> data = rows.getFirst();

        data.put("id", data.remove("max"));

        return rows.getFirst();


    }


}
