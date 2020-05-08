package com.two.sqlSource;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoundSql {

    private String sql;

    private List<ParameterMapping> parameterMappings = new ArrayList<>();

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

}
