package com.two.sqlSource;

public interface SqlSource {

    BoundSql getBoundSql(Object param);
}
