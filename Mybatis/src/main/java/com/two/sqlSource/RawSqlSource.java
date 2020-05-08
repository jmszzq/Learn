package com.two.sqlSource;

import com.two.sqlNode.DynamicContext;
import com.two.sqlNode.MixedSqlNode;
import com.two.until.GenericTokenParser;
import com.two.until.ParameterMappingTokenHandler;

public class RawSqlSource implements SqlSource{

    private StaticSqlSource sqlSource;

    public RawSqlSource(MixedSqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(null);
        rootSqlNode.apply(context);
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", handler);
        String sql = tokenParser.parse(context.getSql());

        sqlSource = new StaticSqlSource(sql, handler.getParameterMappings());
    }

    @Override
    public BoundSql getBoundSql(Object param) {
        return null;
    }
}
