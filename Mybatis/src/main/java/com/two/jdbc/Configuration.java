package com.two.jdbc;

import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Data
public class Configuration {

    private DataSource dataSource;

    private Map<String,MappedStatement> mappedStatements = new HashMap<>();

    public MappedStatement getMappedStatementById(String statementId) {
        return mappedStatements.get(statementId);
    }

    public void addMappedStatement(String statementId, MappedStatement mappedStatement) {
        this.mappedStatements.put(statementId, mappedStatement);
    }
}
