package com.ecommerce.app.dao;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JSONUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class UserDAO extends DAO {


    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        return List.of();
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {

        Map<String, String> allowedColumns = Map.of(
                "email", "u.email",
                "userId", "u.id"
        );

        String key = query.containsKey("email") ? "email" : "userId";
        String column = allowedColumns.get(key);

        String sql = String.format("SELECT * FROM USERS u LEFT JOIN ADDRESSES a ON u.id = a.user_id WHERE %s = ?", column);

        Function<Connection, Object> getUser = (Connection con) -> {

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                if (key.equals("email")) {
                    stmt.setString(1, (String) query.get("email"));
                } else {
                    stmt.setInt(1, Integer.parseInt((String) query.get("userId")));
                }

                ResultSet userRS = stmt.executeQuery();


                return JSONUtils.getListFromResultSet(userRS);


            } catch (SQLException e) {
                e.printStackTrace();
                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }

        };

        return (List<Map<String, Object>>) execute(getUser);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {

        String query = "INSERT INTO USERS (name, email, password_hash, role, mobile_number) values (?, ?, ?, ?, ?);";

        Function<Connection, Object> createUserFunction = (Connection con) -> {

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setString(1, (String) data.get("name"));
                statement.setString(2, (String) data.get("email"));
                statement.setString(3, (String) data.get("password_hash"));
                statement.setString(4, (String) data.get("role"));
                statement.setString(5, (String) data.get("mobile_number"));


                if (statement.executeUpdate() > 0) {
                    String getUserId = "SELECT id FROM USERS WHERE email = ?";
                    try (PreparedStatement getUserIdQuery = con.prepareStatement(getUserId)) {
                        getUserIdQuery.setString(1, (String) data.get("email"));
                        ResultSet idResultSet = getUserIdQuery.executeQuery();
                        idResultSet.next();
                        return JSONUtils.getRowMapFromResultSet(idResultSet);
                    }


                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new HashMap<>();
        };

        return (Map<String, Object>) execute(createUserFunction);
    }

    @Override
    public List<Map<String, Object>> update(Map<String, String> data) {
        return List.of(Map.of());
    }

    @Override
    public boolean delete(Map<String, String> data) {
        return false;
    }

    public List<Map<String, Object>> getUserByEmail(String email) {
        return getUserByEmail(email, List.of("*"));
    }

    public List<Map<String, Object>> getUserByEmail(String email, List<String> columns) {
        return (List<Map<String, Object>>) getDataFromTable("users", columns, new Criteria("email", email, Operator.EQUALS));
    }


}
