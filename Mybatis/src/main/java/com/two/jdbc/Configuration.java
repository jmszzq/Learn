package com.two.jdbc;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Configuration {

    private BasicDataSource basicDataSource;

    private Map<String,MappedStatement> mappedStatements = new HashMap<>();
}
