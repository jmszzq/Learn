package com.two.main;

import com.one.SimpleType;
import com.two.jdbc.Configuration;
import com.two.jdbc.MappedStatement;
import com.two.sqlSource.BoundSql;
import com.two.sqlSource.ParameterMapping;
import com.two.sqlSource.SqlSource;
import com.two.xml.ReadMybatisConfig;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class selectMain {

    public static <T> List<T> selectList(String statementId, Object param) {
        ReadMybatisConfig readMybatisConfig = new ReadMybatisConfig();
        readMybatisConfig.readMybatisXml();
        Configuration configuration = readMybatisConfig.getConfiguration();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        List<T> results = new ArrayList<T>();
        try {
            connection = getConnection(configuration);

            // 定义sql语句 ?表示占位符
            // 先要获取MappedStatement
            MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
            // 再获取MappedStatement中的SqlSource
            SqlSource sqlSource = mappedStatement.getSqlSource();
            // 通过SqlSource的API处理，获取BoundSql
            BoundSql boundSql = sqlSource.getBoundSql(param);
            // 通过BoundSql获取到SQL语句
            String sql = boundSql.getSql();

            System.out.println(sql);
            // 获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);

            // 设置参数，第一个参数为 sql 语句中参数的序号（从 1 开始），第二个参数为设置的
            // 参数处理
            setParameters(preparedStatement, param, boundSql);

            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            // 遍历查询结果集
            // 将每一行映射为一个User对象，每一列作为User对象的属性进行设置
            handleResultSet(rs, results, mappedStatement);

            return results;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        return null;
    }

    private static void setParameters(PreparedStatement preparedStatement, Object param, BoundSql boundSql) throws Exception {
        if (SimpleType.isSimpleType(param.getClass())) {
            preparedStatement.setObject(1, param);
        } else if (param instanceof Map) {
            Map map = (Map) param;

            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String paramName = parameterMapping.getName();
                Object value = map.get(paramName);
                preparedStatement.setObject(i + 1, value);
            }

        } else {
            // todo
        }

    }

    private static <T> void handleResultSet(ResultSet rs, List<T> results, MappedStatement mappedStatement) throws Exception {
        Class<?> resultType = mappedStatement.getResultType();
        while (rs.next()) {
            Object result = resultType.newInstance();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Field field = resultType.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(result, rs.getObject(columnName));
            }

            results.add((T) result);
        }
    }

    private static Connection getConnection(Configuration configuration) {
        try {
            DataSource dataSource = configuration.getDataSource();
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
