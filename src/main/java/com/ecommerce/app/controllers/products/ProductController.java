package com.ecommerce.app.controllers.products;

import com.ecommerce.app.services.ProductService;
import com.ecommerce.app.services.VariantService;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> params = Utils.mergeMaps(Utils.getParams(), Utils.getPathParams());

        if (req.getRequestURI().contains("productVariants")) {
            if (req.getRequestURI().endsWith("productVariants")) {
                Utils.setResponse(new Response(200, new VariantService().getAll(params)));
                return;
            }
            Utils.setResponse(new Response(200, new VariantService().getVariant(params)));
            return;
        }

        Utils.setResponse(new Response(200, new ProductService().getProduct(params)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().endsWith("productVariants")) {
            Map<String, Object> params = Utils.getParams();
            params.putAll(Utils.getPathParams());
            Utils.setResponse(new Response(200, new VariantService().createVariant(params)));
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> params = Utils.mergeMaps(Utils.getParams(), Utils.getPathParams());

        if (req.getRequestURI().contains("productVariants")) {
            if (!req.getRequestURI().endsWith("productVariants")) {
                Utils.setResponse(new Response(200, new VariantService().modify(params)));
                return;
            }
        }
        Utils.setResponse(new Response(200, new ProductService().modify(Utils.mergeMaps(params))));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().contains("productVariants")) {
            if (!req.getRequestURI().endsWith("productVariants")) {
                Utils.setResponse(new Response(200, new VariantService().remove(Utils.getPathParams())));
                return;
            }
        }
        Utils.setResponse(new Response(200, new ProductService().remove(Utils.getPathParams())));
    }
}
