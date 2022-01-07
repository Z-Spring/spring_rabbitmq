package com.murphy.springboot_rebbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.Utils.RedisUtil;
import com.murphy.entity.User;
import com.murphy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.util.Calendar;

@SpringBootTest
@Slf4j
public class SpringRabbitmqApplicationTests {


/*    @Test
    void preHandleTest() throws Exception {}{
        long date=System.currentTimeMillis();//获取时间戳
        String uid2="u-"+date;
        System.out.println(uid2);
        log.info("timestamp:{}",uid2);
    }*/
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    /**
     * 首先从缓存中查询数据，如果查不到的话再从数据库中查询，同时也要将查询到的数据放到Redis中
     */
    @Test
    Object testRedis(String name){
        Object obj=redisUtil.get("name");
        if (obj==null){
            obj=userService.getUid(name);
            log.info("查询数据库................");
            redisUtil.set("name",obj);
        }
        log.info("查询缓存中.................");
        return obj;
    }

    @Test
    void testRedis2() throws JsonProcessingException {
//        key:String   value:json
        User user = new User();
        user.setName("zhang");
        user.setPassword("1");
        user.setEmail("123@qq.com");
        Object user2= objectMapper.writeValueAsString(user);
        redisUtil.hset("person","family",user2);
/*        if (redisUtil.get("user")){
            log.info("success.......");
            System.out.println(redisUtil.get("user"));
        }*/
        System.out.println(redisUtil.hget("person","family"));

    }
}
