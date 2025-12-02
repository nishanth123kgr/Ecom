package com.ecommerce.app.controllers.categories;

import com.ecommerce.app.services.CategoryService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CategoriesController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new CategoryService().createCategory(Utils.getParams())));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new CategoryService().getAll(Utils.getQueryParams())));
    }
}
