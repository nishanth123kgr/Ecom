package com.ecommerce.app.controllers.products;

import com.ecommerce.app.services.ProductService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new ProductService().getAll(Utils.getQueryParams())));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new ProductService().addProduct(Utils.getParams())));
    }
}
