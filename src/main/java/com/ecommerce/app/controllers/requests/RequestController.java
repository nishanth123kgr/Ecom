package com.ecommerce.app.controllers.requests;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.services.RequestService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class RequestController extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new RequestService().getRequest(Utils.getPathParams())));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, Object> params = Utils.mergeMaps(Utils.getPathParams(), Utils.getParams());

        Utils.setResponse(new Response(200, new RequestService().modifyRequest(params)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().endsWith("approval"))
            Utils.setResponse(new Response(200, new RequestService().approveRequest(Utils.getPathParams())));
        else
            throw new APIException(ErrorCodes.INVALID_ARGUMENT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new RequestService().remove(Utils.getPathParams())));
    }
}
