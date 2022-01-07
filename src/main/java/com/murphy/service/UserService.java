package com.murphy.service;

import com.murphy.Utils.RedisUtil;
import com.murphy.entity.User;
import com.murphy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisUtil redisUtil;

    public boolean login(User user) {
        if (userMapper.login(user) > 0) {
            return true;
        }
        return false;
    }

    public boolean register(User user) {
        if (userMapper.register(user) > 0) {
            return true;
        }
        return false;
    }

    public int getUid(String name) {

        return userMapper.getUid(name);
    }

}
