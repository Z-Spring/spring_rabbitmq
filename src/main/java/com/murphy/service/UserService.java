package com.murphy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.utils.RedisUtil;
import com.murphy.entity.User;
import com.murphy.mapper.UserMapper;
import com.murphy.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    public String getToken(String name){
        Map<String, String> token_map=new HashMap<>();
        token_map.put("user", name);
        String token = TokenUtils.getToken(token_map);
        return token;
    }
    public String getUser(User user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(user);
    }

}
