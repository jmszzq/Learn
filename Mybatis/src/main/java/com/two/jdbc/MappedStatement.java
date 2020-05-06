package com.two.jdbc;

import lombok.Data;

@Data
public class MappedStatement {

    private Class<?> parameterType;

    private Class<?> resultType;

    private String sql;

}
