package com.ecommerce.app.services;

import com.ecommerce.app.dao.DAO;
import com.ecommerce.app.dao.UserDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JoinSpec;
import com.ecommerce.app.utils.RowNester;
import com.ecommerce.app.utils.Utils;

import java.util.List;
import java.util.Map;

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

}
