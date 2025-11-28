package com.ecommerce.app.dao;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class DAO {

    public abstract List<Map<String, Object>> readAll(Map<String, Object> query);

    public abstract List<Map<String, Object>> read(Map<String, Object> query);

    public abstract Map<String, Object> create(Map<String, Object> data);

    public abstract List<Map<String, Object>> update(Map<String, String> data);

    public abstract boolean delete(Map<String, String> data);


    public Object getDataFromTable(String tableName, List<String> columns, Criteria criteria) {

        if (StringUtils.isEmpty(tableName) || columns.isEmpty()) {
            throw new APIException(ErrorCodes.INVALID_ARGUMENT);
        }

        Criteria currentCriteria = criteria;

        String selectQuery = "SELECT " +
                String.join(columns.size() == 1 ? "" : ", ", columns) +
                " from " +
                tableName +
                " where " +
                criteria;

        Function<Connection, Object> selectFunction = (con) -> {
            try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {

                int placeHolderCount = 1;

                Criteria c = currentCriteria;

                while (c != null) {
                    Object value = c.getValue();
                    if (value instanceof String) {
                        stmt.setString(placeHolderCount++, (String) value);
                    } else if (value instanceof Integer) {
                        stmt.setInt(placeHolderCount++, (Integer) value);
                    } else if (value instanceof Float) {
                        stmt.setFloat(placeHolderCount++, (Float) value);
                    } else if (value instanceof Date) {
                        stmt.setDate(placeHolderCount++, (Date) value);
                    }
                    c = c.getAnotherCriteria();
                }

                ResultSet resultSet = stmt.executeQuery();

                return JSONUtils.getListFromResultSet(resultSet);


            } catch (SQLException e) {
                e.printStackTrace();
                throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }
        };

        return execute(selectFunction);
    }

    protected Object execute(Function<Connection, Object> executeQueryFunction) {
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/EcommerceDB");
            try (Connection con = ds.getConnection()) {
                return executeQueryFunction.apply(con);
            }

        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
