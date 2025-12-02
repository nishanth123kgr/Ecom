package com.ecommerce.app.services;

import com.ecommerce.app.dao.DAO;
import com.ecommerce.app.dao.UserDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.AuthUtils;
import com.ecommerce.app.utils.JWTUtils;
import com.ecommerce.app.utils.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {


    public Map<String, Object> createUser(Map<String, Object> params) {
        List<String> MANDATORY_FIELDS = List.of("password", "email", "name");
        Utils.checkMandatoryFields(params, MANDATORY_FIELDS);

        String email = (String) params.get("email");

        if (Utils.isNotValidEmail(email)) {
            throw new APIException(ErrorCodes.INVALID_PARAM, email);
        }


        params.put("password_hash", AuthUtils.generateHash((String) params.get("password")));

        DAO userDAO = new UserDAO();
        return userDAO.create(params);
    }


    public Map<String, Object> verifyUser(Map<String, Object> params) {
        List<String> MANDATORY_FIELDS = List.of("email", "password");
        Utils.checkMandatoryFields(params, MANDATORY_FIELDS);

        String email = (String) params.get("email");

        if (Utils.isNotValidEmail(email)) {
            throw new APIException(ErrorCodes.INVALID_PARAM, email);
        }

        UserDAO userDAO = new UserDAO();

        List<Map<String, Object>> user = userDAO.getUserByEmail(email, List.of("id", "password_hash", "role", "email"));

        if (user.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "User");
        }

        boolean isValid = AuthUtils.checkPassword((String) params.get("password"), (String) user.getFirst().get("password_hash"));

        if (isValid) {

            user.getFirst().remove("password_hash");

            Map<String, Object> searchQuery = new HashMap<>();

            searchQuery.put("s.user_id", user.getFirst().get("id").toString());

            try {
                int sellerId = (int) new SellerService().getSeller(searchQuery).get("seller_id");
                user.getFirst().put("seller_id", sellerId);
            } catch (APIException e) {
                if (e.getStatus() != 404 || !e.getMessage().startsWith("Seller")) {
                    throw e;
                }
            }


            return getTokens(user.getFirst());


        } else {
            throw new APIException(ErrorCodes.UNAUTHORIZED);
        }


    }


    public Map<String, Object> getTokens(Map<String, Object> payload) {
        Map<String, Object> tokens = new HashMap<>();

        tokens.put("accessToken", JWTUtils.getJWT((HashMap<String, Object>) payload));
        tokens.put("refreshToken", JWTUtils.getJWT((HashMap<String, Object>) payload, Duration.ofDays(7)));

        return tokens;
    }


}
