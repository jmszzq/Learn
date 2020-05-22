package com.two.dao;

import com.two.entity.User;
import lombok.Data;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class UserDaoImpl implements UserDao {

    private DataSource dataSource;

    public void init() {
        System.out.println("初始化方法被调用");
    }

    @Override
    public List<User> queryUserList(Map<String, Object> param) {
        for(Map.Entry<String,Object> entry:param.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        List<User> list = new ArrayList<>();
        list.add(new User("abc","111"));
        return list;
    }
}
