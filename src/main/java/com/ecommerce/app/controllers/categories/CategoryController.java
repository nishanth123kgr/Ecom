package com.ecommerce.app.controllers.categories;

import com.ecommerce.app.services.AddressService;
import com.ecommerce.app.services.BrandService;
import com.ecommerce.app.services.CategoryService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CategoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse( new Response(200, new CategoryService().getCategory(Utils.getPathParams())));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new CategoryService().modify(Utils.mergeMaps(Utils.getPathParams(), Utils.getParams()))));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Utils.setResponse(new Response(200, new CategoryService().remove(Utils.getPathParams())));
    }
}
