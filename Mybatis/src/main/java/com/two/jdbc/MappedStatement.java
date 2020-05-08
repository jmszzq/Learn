package com.two.jdbc;

import com.two.sqlSource.SqlSource;
import lombok.Data;

@Data
public class MappedStatement {

    private Class<?> parameterType;

    private Class<?> resultType;

    private String statementId;
    private SqlSource sqlSource;

    private String statementType;

    public MappedStatement(String statementId, Class<?> parameterTypeClass, Class<?> resultTypeClass,
                           String statementType, SqlSource sqlSource) {
        this.statementId = statementId;
        this.parameterType = parameterTypeClass;
        this.resultType = resultTypeClass;
        this.statementType = statementType;
        this.sqlSource = sqlSource;
    }

}
