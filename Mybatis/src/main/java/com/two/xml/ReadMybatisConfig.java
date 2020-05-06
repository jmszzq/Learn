package com.two.xml;

import com.two.jdbc.BasicDataSource;
import com.two.jdbc.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ReadMybatisConfig {

    private Configuration configuration = new Configuration();

    private void readMybatisXml(){
        InputStream inputStream = getResourceAsStream("mybatis.xml");
        Document document = createDocument(inputStream);
        parserConfiguration(document.getRootElement());
    }

    private InputStream getResourceAsStream(String location){
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    private Document createDocument(InputStream inputStream){
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            return document;
        } catch (Exception e) {
            log.error("解析mybatis.xml配置文件错误");
        }
        return null;
    }

    private void parserConfiguration(Element element){
        Element envirsElement = element.element("environments");
        parseEnvironments(envirsElement);
        Element mappersElement = element.element("mappers");
        parseMappers(mappersElement);
    }

    private void parseMappers(Element envirsElement){
        List<Element> mappersElements = envirsElement.elements("mapper");
        for(Element element:mappersElements){
            parseMapper(element);
        }
    }

    private void parseMapper(Element mapperElement){
        String resource = mapperElement.attributeValue("resource");
        InputStream inputStream = getResourceAsStream(resource);
        Document document = createDocument(inputStream);
        parseXmlMapper(document.getRootElement());
    }

    private void parseXmlMapper(Element rootElement){
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectElements = rootElement.elements("select");
        for(Element selectElement:selectElements){
            parseStatementElement(namespace,selectElement);
        }
    }

    private void parseStatementElement(String namespace,Element selectElement){
        String statementId = selectElement.attributeValue("id");
        statementId = namespace + "." + statementId;

        String parameterType = selectElement.attributeValue("parameterType");
        Class<?> parameterClass = resolveType(parameterType);

        String resultType = selectElement.attributeValue("resultType");
        Class<?> resultClass = resolveType(resultType);

        String statementType = selectElement.attributeValue("statementType");
        statementType = statementType == null || statementType == "" ? "prepared" : statementType;

        SqlSource sqlSource = createSqlSource(selectElement);

    }

    private Class<?> resolveType(String parameterType) {
        try {
            Class<?> clazz = Class.forName(parameterType);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseEnvironments(Element envirsElement){
        String defaultId = envirsElement.attributeValue("default");
        List<Element> envElements = envirsElement.elements("environment");
        for(Element element:envElements){
            String id = element.attributeValue("id");
            if(defaultId.equals(id)){
                Properties properties = parseDataSource(element);
                BasicDataSource basicDataSource = new BasicDataSource();
                basicDataSource.setDriver(properties.getProperty("driver"));
                basicDataSource.setUrl(properties.getProperty("url"));
                basicDataSource.setName(properties.getProperty("name"));
                basicDataSource.setPassword(properties.getProperty("password"));
                configuration.setBasicDataSource(basicDataSource);
            }
        }
    }

    private Properties parseDataSource(Element element){
        Properties properties = new Properties();
        Element dataSourceElement = element.element("dataSource");
        String type = dataSourceElement.attributeValue("type");
        if(type.equals("DBCP")){
            List<Element> propElements = dataSourceElement.elements("property");
            for(Element propertyElement:propElements){
                properties.put(propertyElement.attributeValue("name"),propertyElement.attributeValue("value"));
            }
        }
        return properties;
    }


}
