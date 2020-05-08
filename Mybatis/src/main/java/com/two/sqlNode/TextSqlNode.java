package com.two.sqlNode;

import com.one.SimpleType;
import com.two.until.GenericTokenParser;
import com.two.until.OgnlUtils;
import com.two.until.TokenHandler;

public class TextSqlNode implements SqlNode{

    private String sqlText;

    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    public boolean isDynamic() {
        if (sqlText.indexOf("${") > -1) {
            return true;
        }
        return false;
    }

    @Override
    public void apply(DynamicContext context) {
        TokenHandler handler = new BindingTokenHandler(context);
        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", handler);
        sqlText = tokenParser.parse(sqlText);
        context.appendSql(sqlText);
    }

    class BindingTokenHandler implements TokenHandler {

        private DynamicContext context;

        public BindingTokenHandler(DynamicContext context) {
            this.context = context;
        }

        @Override
        public String handleToken(String content) {
            Object parameterObject = context.getBindings().get("_parameter");
            if (parameterObject == null) {
                return "";
            } else if (SimpleType.isSimpleType(parameterObject.getClass())) {
                return parameterObject.toString();
            }

            Object value = OgnlUtils.getValue(content, parameterObject);
            return value == null ? "" : value.toString();
        }

    }
}
