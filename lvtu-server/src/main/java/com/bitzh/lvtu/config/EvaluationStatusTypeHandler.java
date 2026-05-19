package com.bitzh.lvtu.config;

import com.bitzh.lvtu.enums.evaluation.EvaluationStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EvaluationStatusTypeHandler extends BaseTypeHandler<EvaluationStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EvaluationStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public EvaluationStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public EvaluationStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public EvaluationStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private EvaluationStatus parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return EvaluationStatus.fromValue(value);
    }
}
