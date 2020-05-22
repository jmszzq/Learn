package com.two.service;

import com.two.dao.UserDao;
import com.two.entity.User;

import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> queryUsers(Map<String, Object> param) {
        return userDao.queryUserList(param);
    }

}