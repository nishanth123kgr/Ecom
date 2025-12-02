package com.ecommerce.app.controllers.sellers;

import com.ecommerce.app.services.RequestService;
import com.ecommerce.app.services.SellerService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SellersController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new SellerService().getAll(Utils.getQueryParams())));
    }
}
