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
import java.util.Set;

public class UserService {

    public Map<String, Object> getUser(Map<String, Object> params) {
        Utils.checkMandatoryFields(params, List.of("userId"));

        DAO userDao = new UserDAO();

        List<Map<String, Object>> user = userDao.read(params);

        if (user.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "User");
        }

        List<JoinSpec> joins = List.of(new JoinSpec("address", "addresses"));

        List<Map<String, Object>> users = RowNester.nestRows(user, "user", "id", joins);

        return users.getFirst();
    }

    public List<Map<String, Object>> getAllUsers(Map<String, Object> query) {

        List<Map<String, Object>> rows = new UserDAO().readAll(query);

        List<JoinSpec> joins = List.of(new JoinSpec("address", "addresses"));

        return RowNester.nestRows(rows, "user", "id", joins);
    }

    public Map<String, Object> modify(Map<String, Object> params) {

        if (params.isEmpty() || (!params.containsKey("name") && !params.containsKey("mobile_number") && !params.containsKey("is_active"))) {
            throw new APIException(ErrorCodes.MISSING_PARAM, "Update param");
        }

        Set<String> NOT_ALLOWED_FIELDS = Set.of("email", "role", "password_hash");


        Utils.validateNotAllowedFieldsForUpdate(params, NOT_ALLOWED_FIELDS);


        Object userId = params.remove("userId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", userId);


        return new UserDAO().update(params, criteriaMap).getFirst();
    }

    public Map<String, Object> remove(Map<String, Object> criteria) {
        Object userId = criteria.remove("userId");

        Map<String, Object> criteriaMap = new HashMap<>();
        criteriaMap.put("id", userId);

        Map<String, Object> result = new HashMap<>();


        if (new UserDAO().delete(criteriaMap)) {
            result.put("success", true);
            result.put("message", "Delete Successful");
        } else {
            result.put("success", false);
            result.put("message", "Delete Failed");
        }

        return result;
    }
}
