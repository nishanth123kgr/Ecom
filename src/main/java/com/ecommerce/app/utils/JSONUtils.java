package com.ecommerce.app.utils;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtils {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static List<Map<String, Object>> getListFromResultSet(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                list.add(getRowMapFromResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    public static Map<String, Object> getRowMapFromResultSet(ResultSet rs) {
        try {
            ResultSetMetaData md = rs.getMetaData();
            return getRowMap(rs, md);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    private static Map<String, Object> getRowMap(ResultSet rs, ResultSetMetaData md) {
        try {
            Map<String, Object> rowMap = new HashMap<>();
            int columnCount = md.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                rowMap.put(md.getColumnLabel(i), rs.getObject(i));
            }

            return rowMap;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }

    }


}
