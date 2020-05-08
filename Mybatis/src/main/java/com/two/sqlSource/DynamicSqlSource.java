package com.two.sqlSource;

import com.two.sqlNode.DynamicContext;
import com.two.sqlNode.MixedSqlNode;
import com.two.until.GenericTokenParser;
import com.two.until.ParameterMappingTokenHandler;

public class DynamicSqlSource implements SqlSource {

    private MixedSqlNode rootSqlNode;

    public DynamicSqlSource(MixedSqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object param) {
        DynamicContext context = new DynamicContext(param);
        rootSqlNode.apply(context);

        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", handler);
        String sql = tokenParser.parse(context.getSql());
        return new BoundSql(sql, handler.getParameterMappings());
    }

}
