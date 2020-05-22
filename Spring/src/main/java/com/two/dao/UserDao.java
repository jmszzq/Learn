package com.two.dao;

import com.two.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    List<User> queryUserList(Map<String, Object> param);
}
