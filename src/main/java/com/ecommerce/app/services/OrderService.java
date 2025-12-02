package com.ecommerce.app.services;

import com.ecommerce.app.dao.OrdersDAO;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JoinSpec;
import com.ecommerce.app.utils.RowNester;
import com.ecommerce.app.utils.Utils;

import java.util.List;
import java.util.Map;

public class OrderService {

    public Map<String, Object> createOrder(Map<String, Object> data) {
        Utils.checkMandatoryFields(data, List.of("address_id"));

        return new OrdersDAO().create(data);
    }

    public List<Map<String, Object>> getAll(Map<String, Object> query) {

        Map<String, String> map = new java.util.HashMap<>(Map.of(
                "order_id", "o.id",
                "item_id", "i.id ",
                "item_name", "p.name",
                "item_description", "p.description",
                "item_attributes", "v.attributes",
                "item_quantity", "i.quantity",
                "item_total_price", "i.total_price",
                "order_status", "o.status",
                "order_order_date", "o.order_date",
                "order_total_amount", "o.total_amount"
        ));

        map.put("orderId", "o.id");

        List<Map<String, Object>> data = new OrdersDAO().readAll(Utils.mapQueryParams(query, map));

        return RowNester.nestRows(data, "order", "id", List.of(new JoinSpec("item", "order_items")));
    }

    public Map<String, Object> getOrder(Map<String, Object> query) {
        List<Map<String, Object>> data = getAll(query);

        if (data.isEmpty()) {
            throw new APIException(ErrorCodes.NOT_FOUND, "Order");
        }

        return data.getFirst();
    }


}
