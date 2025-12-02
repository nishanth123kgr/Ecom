package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.dao.wrappers.CriteriaBuilder;
import com.ecommerce.app.dao.wrappers.Operator;
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

public class UserDAO extends DAO {


    @Override
    public List<Map<String, Object>> readAll(Map<String, Object> query) {
        String sql = "SELECT " +
                "u.id          AS user_id, " +
                "u.name        AS user_name, " +
                "u.email       AS user_email, " +
                "u.is_active   AS user_is_active, " +
                "u.mobile_number      AS user_mobile_number, " +
                "u.role AS user_role, " +
                "a.id          AS address_id, " +
                "a.country     AS address_country, " +
                "a.state       AS address_state, " +
                "a.city        AS address_city, " +
                "a.street     AS address_street, " +
                "a.postal_code AS address_postal_code " +
                "FROM USERS u " +
                "LEFT JOIN ADDRESSES a ON u.id = a.user_id ";

        Function<Connection, Object> getUsers = (Connection con) -> {

            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                ResultSet userRS = stmt.executeQuery();

                return JSONUtils.getListFromResultSet(userRS);


            } catch (SQLException e) {
                e.printStackTrace();
                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }

        };

        return (List<Map<String, Object>>) execute(getUsers, Utils.getUserID(), Utils.isAdmin());
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> query) {

        Map<String, String> allowedColumns = Map.of(
                "email", "u.email",
                "userId", "u.id"
        );

        String key = query.containsKey("email") ? "email" : "userId";
        String column = allowedColumns.get(key);

        String sql = String.format(
                "SELECT " +
                        "u.id          AS user_id, " +
                        "u.name        AS user_name, " +
                        "u.email       AS user_email, " +
                        "u.is_active   AS user_is_active, " +
                        "u.mobile_number      AS user_mobile_number, " +
                        "u.role AS user_role, " +
                        "a.id          AS address_id, " +
                        "a.country     AS address_country, " +
                        "a.state       AS address_state, " +
                        "a.city        AS address_city, " +
                        "a.street     AS address_street, " +
                        "a.postal_code AS address_postal_code " +
                        "FROM USERS u " +
                        "LEFT JOIN ADDRESSES a ON u.id = a.user_id " +
                        "WHERE %s = ?",
                column
        );

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

        return (List<Map<String, Object>>) execute(getUser, Utils.getUserID(), Utils.isAdmin());
    }

    @Override
    public Map<String, Object> create(Map<String, Object> data) {

        String query = "select * from register_user(?, ?, ?, ?, ?);";

        Function<Connection, Object> createUserFunction = (Connection con) -> {

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setString(1, (String) data.get("name"));
                statement.setString(2, (String) data.get("email"));
                statement.setString(3, (String) data.get("password_hash"));
                statement.setString(4, (String) data.get("role"));
                statement.setString(5, (String) data.get("mobile_number"));


                ResultSet idResultSet = statement.executeQuery();
                idResultSet.next();
                return JSONUtils.getRowMapFromResultSet(idResultSet);


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        return (Map<String, Object>) execute(createUserFunction, -1);
    }

    @Override
    public List<Map<String, Object>> update(Map<String, Object> data, Map<String, Object> criteria) {
        return update("users", data, new CriteriaBuilder().build(criteria));
    }

    @Override
    public boolean delete(Map<String, Object> criteria) {
        return delete("users", new CriteriaBuilder().build(criteria));
    }

    public List<Map<String, Object>> getUserByEmail(String email) {
        return getUserByEmail(email, List.of("*"));
    }

    public List<Map<String, Object>> getUserByEmail(String email, List<String> columns) {
        return (List<Map<String, Object>>) getDataFromTable("login_user('" + email + "')", columns, new Criteria("email", email, Operator.EQUALS));
    }


}
