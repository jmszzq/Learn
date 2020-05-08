package com.two.sqlNode;

import com.two.until.OgnlUtils;

public class IfSqlNode implements SqlNode {

    private String test;

    private MixedSqlNode mixedSqlNode;

    public IfSqlNode(String test, MixedSqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public void apply(DynamicContext context) {
        Object parameterObject = context.getBindings().get("_parameter");
        boolean evaluateBoolean = OgnlUtils.evaluateBoolean(test, parameterObject);
        if (evaluateBoolean) {
            mixedSqlNode.apply(context);
        }
    }

}