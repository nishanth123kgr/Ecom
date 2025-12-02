package com.ecommerce.app.controllers.cart;

import com.ecommerce.app.services.AddressService;
import com.ecommerce.app.services.CartService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new CartService().getAll(Utils.getQueryParams())));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new CartService().addCartItem(Utils.getParams())));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new CartService().modify(Utils.mergeMaps(Utils.getPathParams(), Utils.getParams()))));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new CartService().remove(Utils.getPathParams())));
    }
}
