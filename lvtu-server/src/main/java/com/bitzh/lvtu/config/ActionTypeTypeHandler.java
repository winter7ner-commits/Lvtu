package com.bitzh.lvtu.config;

import com.bitzh.lvtu.enums.evaluation.ActionType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActionTypeTypeHandler extends BaseTypeHandler<ActionType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ActionType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public ActionType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public ActionType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public ActionType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private ActionType parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ActionType.fromValue(value);
    }
}
