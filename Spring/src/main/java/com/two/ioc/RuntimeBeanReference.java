package com.two.ioc;

import lombok.Data;

@Data
public class RuntimeBeanReference {

    private String ref;

    public RuntimeBeanReference(String ref) {
        super();
        this.ref = ref;
    }
}
