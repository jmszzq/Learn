package com.two.ioc;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BeanDefinition {

    private String clazzName;

    private Class<?> clazzType;

    private String beanName;

    private String initMethod;

    private String scope;

    private List<PropertyValue> propertyValues = new ArrayList<>();

    private static final String SCOPE_SINGLETON = "singleton";
    private static final String SCOPE_PROTOTYPE = "prototype";

    public BeanDefinition(String clazzName, String beanName) {
        this.beanName = beanName;
        this.clazzName = clazzName;
        this.clazzType = resolveClassName(clazzName);
    }

    private Class<?> resolveClassName(String clazzName) {
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

}
