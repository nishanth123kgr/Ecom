package com.ecommerce.app.controllers.user;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.services.UserService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserService service = new UserService();

        Map<String, Object> pathParams = Utils.getPathParams(req);

        Map<String, Object> payload = Utils.getPayload(req);

        Integer paramID = Integer.parseInt((String) pathParams.get("userId"));

        Integer payloadID = (Integer) payload.get("id");

//        if (!paramID.equals(payloadID)) {
//            throw new APIException(ErrorCodes.FORBIDDEN);
//        }


        Map<String, Object> user = service.getUser(pathParams);

        req.setAttribute("response", new Response(200, user));

    }
}
