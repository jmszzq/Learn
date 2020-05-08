package com.two.sqlSource;

import lombok.Data;

@Data
public class ParameterMapping {

    private String name;

    // 该参数对应的类型
    private Class<?> type;

    public ParameterMapping(String name) {
        this.name = name;
    }
}
