package com.two.ioc;

import lombok.Data;

@Data
public class TypedStringValue {

    private String value;

    private Class<?> targetType;

    public TypedStringValue(String value) {
        this.value = value;
    }
}
