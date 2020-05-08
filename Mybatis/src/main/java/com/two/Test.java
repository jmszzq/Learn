package com.two;

import com.one.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.two.main.selectMain.selectList;

public class Test {

    @org.junit.Test
    public void test(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "zq");
        params.put("id", 3);
        List<User> list = selectList("test.findUserById", params);
        System.out.println(list);
    }
}
