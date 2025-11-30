package com.ecommerce.app.services;

import com.ecommerce.app.dao.DAO;
import com.ecommerce.app.dao.UserDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JoinSpec;
import com.ecommerce.app.utils.RowNester;
import com.ecommerce.app.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    public Map<String, Object> extractUser(Map<String, Object> userData, boolean needPasswordHash) {
        List<String> userColumns = List.of("user_id", "is_active", "name", "mobile_number", "email", "role", needPasswordHash ? "password_hash" : "");
        Map<String, Object> user = new HashMap<>();

        for (String column : userColumns) {
            if (userData.containsKey(column)) {
                user.put(column, userData.remove(column));
            }
        }

        userData.put("user", user);

        userData.remove("password_hash");

        return userData;
    }

    public Map<String, Object> removeUnwantedFields(Map<String, Object> userData) {
        List<String> unWantedFields = List.of("created_at", "updated_at");
        for (String column : unWantedFields) {
            userData.remove(column);
        }
        return userData;
    }

    public Map<String, Object> getUser(Map<String, Object> params, boolean needPasswordHash) {
        Utils.checkMandatoryFields(params, List.of("userId"));

        DAO userDao = new UserDAO();

        List<Map<String, Object>> user = userDao.read(params);

        if (user.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "User");
        }

        /*Map<Map<String, Object>, List<Map<String, Object>>> data = user.stream()
                .map((userData) -> extractUser(userData, needPasswordHash))
                .map(this::removeUnwantedFields)
                .collect(Collectors.groupingBy((userData) -> (Map<String, Object>) userData.get("user")));

        for (List<Map<String, Object>> addresses : data.values()) {
            for (Map<String, Object> address : addresses) {
                address.remove("user");
            }
        }
        Map<String, Object> userData = new HashMap<>();
        for (Map.Entry<Map<String, Object>, List<Map<String, Object>>> entry : data.entrySet()) {
            userData.put("user", entry.getKey());
            entry.getKey().put("addresses", entry.getValue());
        }*/

        List<JoinSpec> joins = List.of(new JoinSpec("address", "addresses"));

        List<Map<String, Object>> users = RowNester.nestRows(user, "user", "id", joins);

        return users.getFirst();
    }

    public Map<String, Object> getUser(Map<String, Object> params) {
        return getUser(params, false);
    }

    public List<Map<String, Object>> getAllUsers(Map<String, Object> query) {

        List<Map<String, Object>> rows = new UserDAO().readAll(query);

        List<JoinSpec> joins = List.of(new JoinSpec("address", "addresses"));

        return RowNester.nestRows(rows, "user", "id", joins);
    }

}
