package com.one;

import com.one.entity.User;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class VersionOne {

    private static final String JDBC_DRIVER = "jdbc.driver";
    private static final String JDBC_URL = "jdbc.url";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";
    private static final String JDBC_CLASS = "jdbc.class";

    private static Properties properties = new Properties();

    private void readConfig(){
        try {
            String name = "Mybatis.properties";
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name);
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {

            return null;
        }
    }

    private static <T> List<T> select(String statementId,Object param){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        List<T> returnlist = new ArrayList<>();
        try{
            Class.forName(properties.getProperty(JDBC_DRIVER));

            conn = DriverManager.getConnection(properties.getProperty(JDBC_URL),properties.getProperty(JDBC_USER),properties.getProperty(JDBC_PASSWORD));

            String sql = properties.getProperty("jdbc.sql."+statementId);
            preparedStatement = conn.prepareStatement(sql);

            if (SimpleType.isSimpleType(param.getClass())) {
                preparedStatement.setObject(1, param);
            } else if (param instanceof Map) {
                Map map = (Map) param;

                String params = properties.getProperty("jdbc.sql." + statementId + ".params");
                String[] paramArray = params.split(",");
                for (int i = 0; i < paramArray.length; i++) {
                    String paramName = paramArray[i];
                    Object value = map.get(paramName);
                    preparedStatement.setObject(i + 1, value);
                }

            } else {
                String params = properties.getProperty("jdbc.sql." + statementId + ".class.params");
                Class clazz = param.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for(Field field:fields){
                    if(field.getName().equals(params)){
                        preparedStatement.setObject(1, getFieldValueByName(field.getName(),param));
                    }
                }
            }

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                Class clazz = Class.forName(properties.getProperty(JDBC_CLASS+"."+statementId));
                Object object = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for(Field field:fields){
                    field.setAccessible(true);
                    field.set(object,rs.getObject(field.getName()));
                }
                returnlist.add((T)object);

            }
            rs.close();
            preparedStatement.close();
            conn.close();

        } catch(Exception se){
            se.printStackTrace();
        } finally{
            try{
                if(preparedStatement!=null){
                    preparedStatement.close();
                }
            }catch(SQLException se2){
                se2.printStackTrace();
            }
            try{
                if(conn!=null){
                    conn.close();
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return returnlist;
    }

    public static void main(String[] args) {
        new VersionOne().readConfig();
        Map<String,String> param = new HashMap<>();
        param.put("name","zzq");
        param.put("phone","111");
        String name = "123";
        User user = new User();
        user.setName("张三");
        List<User> list = select("selectUser",param);
        System.out.println(list);
    }
}
