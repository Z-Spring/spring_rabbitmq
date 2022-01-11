package com.murphy.springboot_rebbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.Controller.BookController;
import com.murphy.Controller.UserController;
import com.murphy.Utils.RedisUtil;
import com.murphy.entity.User;
import com.murphy.mapper.BookMapper;
import com.murphy.service.BookService;
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
    BookService bookService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserController userController;
    @Autowired
    BookController bookController;
    @Autowired
    BookMapper bookMapper;
    @Disabled
    @Test
    void testRedis2() {
        for (int i=0;i<bookService.getId2(166).size();i++){

            System.out.println(bookService.getId2(166).get(i));
        }

 /*           for (int i=0;i<bookService.getId2(166).size();i++){
                if (bookController.getProduceFromRedis("zhang").contains(bookService.getId2(uid).get())){
                    model.addAttribute("HasThisProduct","近期加购物车商品");
                }
            }*/
    }
}
