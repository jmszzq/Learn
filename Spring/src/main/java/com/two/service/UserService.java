package com.two.service;

import com.two.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<User> queryUsers(Map<String, Object> param);
}
