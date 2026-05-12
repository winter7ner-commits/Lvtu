package com.bitzh.lvtu.config;

import com.bitzh.lvtu.dto.SpecialtyDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialtiesJsonTypeHandler extends BaseTypeHandler<List<SpecialtyDTO>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<SpecialtyDTO> parameter, JdbcType jdbcType) throws SQLException {
        // 写入不需要实现，因为这个字段只用于查询结果的反序列化
    }

    @Override
    public List<SpecialtyDTO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public List<SpecialtyDTO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public List<SpecialtyDTO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private List<SpecialtyDTO> parseJson(String json) {
        List<SpecialtyDTO> list = new ArrayList<>();
        if (json == null || json.isEmpty() || "[]".equals(json)) {
            return list;
        }

        try {
            JsonNode jsonArray = objectMapper.readTree(json);
            if (jsonArray.isArray()) {
                jsonArray.forEach(node -> {
                    if (node.isObject()) {
                        SpecialtyDTO specialty = new SpecialtyDTO();
                        if (node.has("id")) {
                            specialty.setId(node.get("id").asInt());
                        }
                        if (node.has("specialtyId")) {
                            specialty.setSpecialtyId(node.get("specialtyId").asInt());
                        }
                        if (node.has("name")) {
                            specialty.setName(node.get("name").asText());
                        }
                        list.add(specialty);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
