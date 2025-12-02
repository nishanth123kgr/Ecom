package com.ecommerce.app.filters;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.utils.JSONUtils;
import com.ecommerce.app.utils.Response;
import com.ecommerce.app.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class ResponseFilter extends HttpFilter {

    private void writeResponseAsJSON(HttpServletResponse res, Object responseObject) throws IOException {
        String jsonString = JSONUtils.OBJECT_MAPPER.writeValueAsString(responseObject);
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        out.print(jsonString);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        Response response;
        try {
            Utils.setPayload(new HashMap<>());
            Utils.setRequest(req);
            chain.doFilter(req, res);
            response = (Response) req.getAttribute("response");
        } catch (APIException e) {
            e.printStackTrace();
            response = new Response(e.getStatus(), e.getMessage(), e.getExtraDetails());
        }

        if (response != null) {
            res.setStatus(response.status());
            writeResponseAsJSON(res, response);
        }
    }
}
