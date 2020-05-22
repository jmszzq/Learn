package com.two;

import com.two.entity.User;
import com.two.ioc.BeanDefinition;
import com.two.ioc.PropertyValue;
import com.two.ioc.RuntimeBeanReference;
import com.two.ioc.TypedStringValue;
import com.two.service.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    private Map<String, Object> singletonObjects = new HashMap<>();

    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    @org.junit.Test
    public void test() {

        UserService userService = (UserService) getBean("userService");

        // 查询参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", "abc");

        // 用户查询
        List<User> list = userService.queryUsers(param);
        System.out.println(list);
    }

    @Before
    public void before() {
        // XML解析
        String location = "beans.xml";

        InputStream inputStream = getInputStream(location);

        Document document = createDocument(inputStream);

        parseBeanDefinitions(document.getRootElement());
    }

    private InputStream getInputStream(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    private Document createDocument(InputStream inputStream) {
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            // 获取标签名称
            String name = element.getName();
            if (name.equals("bean")) {
                // 解析bean标签
                parseDefaultElement(element);
            } else {
                // 解析自定义标签
                parseCustomElement(element);
            }
        }
    }

    private void parseCustomElement(Element element) {
        // TODO 解析其他标签

    }

    private void parseDefaultElement(Element beanElement) {
        try {
            if (beanElement == null)
                return;
            // 获取id属性
            String id = beanElement.attributeValue("id");

            // 获取name属性
            String name = beanElement.attributeValue("name");

            // 获取class属性
            String clazzName = beanElement.attributeValue("class");
            if (clazzName == null || "".equals(clazzName)) {
                return;
            }

            // 获取init-method属性
            String initMethod = beanElement.attributeValue("init-method");
            // 获取scope属性
            String scope = beanElement.attributeValue("scope");
            scope = scope != null && !scope.equals("") ? scope : "singleton";

            // 获取beanName
            String beanName = id == null ? name : id;
            Class<?> clazzType = Class.forName(clazzName);
            beanName = beanName == null ? clazzType.getSimpleName() : beanName;
            // 创建BeanDefinition对象
            // todo 此次可以使用构建者模式进行优化
            BeanDefinition beanDefinition = new BeanDefinition(clazzName, beanName);
            beanDefinition.setInitMethod(initMethod);
            beanDefinition.setScope(scope);
            // 获取property子标签集合
            List<Element> propertyElements = beanElement.elements();
            for (Element propertyElement : propertyElements) {
                parsePropertyElement(beanDefinition, propertyElement);
            }

            // 注册BeanDefinition信息
            this.beanDefinitions.put(beanName, beanDefinition);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parsePropertyElement(BeanDefinition beanDefination, Element propertyElement) {
        if (propertyElement == null)
            return;

        // 获取name属性
        String name = propertyElement.attributeValue("name");
        // 获取value属性
        String value = propertyElement.attributeValue("value");
        // 获取ref属性
        String ref = propertyElement.attributeValue("ref");

        // 如果value和ref都有值，则返回
        if (value != null && !value.equals("") && ref != null && !ref.equals("")) {
            return;
        }

        /**
         * PropertyValue就封装着一个property标签的信息
         */
        PropertyValue pv = null;

        if (value != null && !value.equals("")) {
            // 因为spring配置文件中的value是String类型，而对象中的属性值是各种各样的，所以需要存储类型
            TypedStringValue typeStringValue = new TypedStringValue(value);

            Class<?> targetType = getTypeByFieldName(beanDefination.getClazzName(), name);
            typeStringValue.setTargetType(targetType);

            pv = new PropertyValue(name, typeStringValue);
            beanDefination.addPropertyValue(pv);
        } else if (ref != null && !ref.equals("")) {

            RuntimeBeanReference reference = new RuntimeBeanReference(ref);
            pv = new PropertyValue(name, reference);
            beanDefination.addPropertyValue(pv);
        } else {
            return;
        }
    }

    private Class<?> getTypeByFieldName(String beanClassName, String name) {
        try {
            Class<?> clazz = Class.forName(beanClassName);
            Field field = clazz.getDeclaredField(name);
            return field.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object getBean(String beanName) {
        Object bean = singletonObjects.get(beanName);
        if (bean != null) {
            return bean;
        }

        BeanDefinition bd = beanDefinitions.get(beanName);
        if (bd == null) {
            return null;
        }

        if (bd.isSingleton()) {
            bean = createBean(bd);
            singletonObjects.put(beanName, bean);
        } else if (bd.isPrototype()) {

            bean = createBean(bd);
        }

        return bean;
    }

    private Object createBean(BeanDefinition bd) {
        Class<?> clazzType = bd.getClazzType();
        if (clazzType == null) {
            return null;
        }
        // 实例化bean
        Object bean = createBeanInstance(clazzType);

        // 填充属性（依赖注入）
        populateBean(bean, bd);

        // 初始化bean
        initializeBean(bean, bd);

        return bean;
    }

    private void initializeBean(Object bean, BeanDefinition bd) {
        // TODO 处理Aware接口（标记）

        // TODO 处理InitializingBean的初始化操作

        // 处理初始化方法
        invokeInitMethod(bean, bd);
    }

    private void invokeInitMethod(Object bean, BeanDefinition bd) {
        try {
            String initMethod = bd.getInitMethod();
            if (initMethod == null || "".equals(initMethod)) {
                return;
            }
            Class<?> clazzType = bd.getClazzType();
            Method method = clazzType.getMethod(initMethod);

            method.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Object createBeanInstance(Class<?> clazzType) {
        try {
            Constructor<?> constructor = clazzType.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(Object bean, BeanDefinition bd) {
        List<PropertyValue> propertyValues = bd.getPropertyValues();
        for (PropertyValue pv : propertyValues) {
            String name = pv.getName();
            Object value = pv.getValue();
            // 处理参数
            Object valueToUse = resolveValue(value);

            setProperty(bean, name, valueToUse, bd);
        }

    }

    private Object resolveValue(Object value) {
        if (value instanceof TypedStringValue) {
            TypedStringValue tsv = (TypedStringValue) value;
            String stringValue = tsv.getValue();
            Class<?> targetType = tsv.getTargetType();

            // TODO 可以优化
            if (targetType == Integer.class) {
                return Integer.parseInt(stringValue);
            } else if (targetType == String.class) {
                return stringValue;
            }
        } else if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference rbr = (RuntimeBeanReference) value;
            String ref = rbr.getRef();
            return getBean(ref);
        }
        return null;
    }

    private void setProperty(Object bean, String name, Object valueToUse, BeanDefinition bd) {
        try {
            Class<?> clazzType = bd.getClazzType();
            Field field = clazzType.getDeclaredField(name);
            field.setAccessible(true);
            field.set(bean, valueToUse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
