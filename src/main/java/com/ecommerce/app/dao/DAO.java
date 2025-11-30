package com.ecommerce.app.dao;

import com.ecommerce.app.dao.wrappers.Criteria;
import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ErrorCodes;
import com.ecommerce.app.utils.JSONUtils;
import com.ecommerce.app.utils.Utils;
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

        Function<Connection, Object> selectFunction = (con) -> executeSelectQuery(currentCriteria, selectQuery, con);

        return execute(selectFunction, Utils.getUserID(), Utils.isAdmin());
    }

    private Object executeSelectQuery(Criteria currentCriteria, String selectQuery, Connection con) {
        try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {

            int placeHolderCount = 1;

            Criteria c = currentCriteria;

            while (c != null) {
                Object value = c.getValue();
                switch (value) {
                    case String s -> stmt.setString(placeHolderCount++, s);
                    case Integer i -> stmt.setInt(placeHolderCount++, i);
                    case Long l -> stmt.setLong(placeHolderCount++, l);
                    case Double v -> stmt.setDouble(placeHolderCount++, v);
                    case Float v -> stmt.setFloat(placeHolderCount++, v);
                    case Short i -> stmt.setShort(placeHolderCount++, i);
                    case Boolean b -> stmt.setBoolean(placeHolderCount++, b);
                    case Date date -> stmt.setDate(placeHolderCount++, date);
                    case Timestamp timestamp -> stmt.setTimestamp(placeHolderCount++, timestamp);
                    case java.util.Date date -> stmt.setTimestamp(placeHolderCount++, new Timestamp(date.getTime()));
                    default -> stmt.setObject(placeHolderCount++, value);
                }
                c = c.getAnotherCriteria();
            }

            ResultSet resultSet = stmt.executeQuery();

            return JSONUtils.getListFromResultSet(resultSet);


        } catch (SQLException e) {
            e.printStackTrace();
            throw new APIException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    protected Object getDataFromTable(Connection con, String tableName, List<String> columns, Criteria criteria, String... extras) {

        if (StringUtils.isEmpty(tableName) || columns.isEmpty()) {
            throw new APIException(ErrorCodes.INVALID_ARGUMENT);
        }

        Criteria currentCriteria = criteria;

        String selectQuery = "SELECT " +
                String.join(columns.size() == 1 ? "" : ", ", columns) +
                " from " +
                tableName +
                " where " +
                criteria +
                (extras.length > 0 ? " " + extras[0] : "");


        return executeSelectQuery(currentCriteria, selectQuery, con);
    }

    protected Object execute(Function<Connection, Object> executeQueryFunction, int userId) {
        return execute(executeQueryFunction, userId, false);
    }


    protected Object execute(Function<Connection, Object> executeQueryFunction, int userId, boolean isAdmin) {
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/EcommerceDB");
            try (Connection con = ds.getConnection()) {
                con.setAutoCommit(false);
                setRLS(con, userId, isAdmin);
                Object data = executeQueryFunction.apply(con);
                con.commit();

                return data;
            }

        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void setRLS(Connection con, int userId, boolean isAdmin) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET \"app.current_user\" = '" + userId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET \"app.current_user_is_admin\" = '" + isAdmin + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRLS(Connection con, int userId) {
        setRLS(con, userId, false);
    }

    protected List<Map<String, Object>> readALL(Map<String, Object> query) {


        return null;

    }

}
