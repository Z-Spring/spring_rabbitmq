package com.murphy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.QueueMessageListener;
import com.murphy.Utils.RedisUtil;
import com.murphy.entity.User;
import com.murphy.message.LoginMessage;
import com.murphy.service.BookService;
import com.murphy.service.MessagingService;
import com.murphy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    MessagingService messagingService;
    @Autowired
    QueueMessageListener queueMessageListener;
    @Autowired
    BookService bookService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    BookController bookController;

    //     首页
    @GetMapping(value = "/base")
    public ModelAndView base(Model model, HttpServletRequest request, HttpSession session) throws IOException {
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            int uid = userService.getUid(user.getName());
            request.setAttribute("name", bookService.getBook());
            model.addAttribute("bookList", bookService.getBook());
            model.addAttribute("price", bookService.getPrice(uid));
            model.addAttribute("pid",bookController.getProduceFromRedis(user.getName()));
            model.addAttribute("HasThisProduct","近期加过购物车商品");
        } else {
            request.setAttribute("name", bookService.getBook());
            model.addAttribute("bookList", bookService.getBook());
            model.addAttribute("price", "0.00");
        }
        return new ModelAndView("base");
    }

    //    商品详细信息

    /**
     * book [GET]
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/book")
    public ModelAndView getBook() throws JsonProcessingException {
        ModelAndView modelAndView = new ModelAndView("book");
        modelAndView.addObject("booklist", bookService.getBook());
        return modelAndView;
    }

    //    错误信息

    /**
     * error page
     * @return
     */
    @GetMapping(value = "/error")
    public ModelAndView error() {
        return new ModelAndView("error");
    }

    //      登录

    /**
     * login [GET]
     * @return
     */
    @GetMapping(value = "/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public String login2(@RequestParam("name") String name, HttpSession session, Model model, @RequestParam("password") String password, @RequestParam("email") String email) throws Exception {
        if (judgeRegex(email)) {
            User user = new User();
            user.setName(name);
            //加密工具类   DigestUtils
            String pwd = DigestUtils.md5DigestAsHex(password.getBytes("UTF-8"));
            user.setPassword(pwd);
            user.setEmail(email);
            if (getUserFromRedis(name) == null) {
                log.info("从数据库中获取数据");
                long begin = System.currentTimeMillis();
                log.info("开始时间: {}", begin);
                if (userService.login(user)) {
                    long end = System.currentTimeMillis();
                    log.info("database结束时间: {}", end);
                    //注意：这里是HttpSession
                    session.setAttribute("user", user);
                    putUserIntoRedis(user, name);
                    log.info("login success, username:{}, password:{}", name, pwd);
//                  session.setMaxInactiveInterval(30*60);   设置session的过期时间  单位：秒
                    messagingService.sendLoginMessage(LoginMessage.of(user.getEmail(), user.getName(), true));
//                  queueMessageListener.onLoginMessageFromMailQueue(LoginMessage.of(user.getEmail(), user.getName(), true));
                    return "redirect:/base";
                } else {
                    model.addAttribute("loginerror", "用户名或密码错误");
                    String s = String.valueOf(model.getAttribute("loginerror"));
                    log.info("login failure, username: {},password: {}", name, pwd);
                    messagingService.sendLoginMessage(LoginMessage.of(email, "unknown", false));
//                  return new ModelAndView("login");
//                  return "login";
                    return "login";
                }
            } else {
                long end2 = System.currentTimeMillis();
                log.info("缓存结束时间 {}", end2);
                //注意：这里是HttpSession
                session.setAttribute("user", user);
                log.info("从缓存中查询数据");
                log.info("login success, username: {}, password: {}", name, pwd);
                messagingService.sendLoginMessage(LoginMessage.of(user.getEmail(), user.getName(), true));
                return "redirect:/base";

            }

        } else {
            model.addAttribute("errorregex", "请输入正确的email格式");
            return "login";
        }
    }

    /**
     * 把user写进Redis
     * @param user  用户登录数据
     * @param name  根据name来查询用户数据
     * @throws JsonProcessingException
     */
    public void putUserIntoRedis(Object user, String name) throws JsonProcessingException {
        user = objectMapper.writeValueAsString(user);
        redisUtil.hset("user", name, user);
        log.info("将数据存储到Redis中");
    }
    //从Redis中取出User

    /**
     * key :String   value: Json
     * 登录时，先从缓存中获取信息，如果缓存中没有的话，再从数据库中获取
     */
    public Object getUserFromRedis(String name) {
        //注意，这里二元运算符是==null,两个等号
        Object obj=redisUtil.hget("user", name)==null?"没有从Redis中获取到数据":"从Redis中获取到数据！";
        log.info("{}", obj);
        return redisUtil.hget("user", name);
    }

    /**
     * 注销用户
     * @param session
     * @return
     */
    @GetMapping(value = "/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("user");
        return new ModelAndView("redirect:base");
    }

    /**
     * register GET
     * @return
     */
    @GetMapping(value = "/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @PostMapping(value = "/register")
    public ModelAndView doRegister(@RequestParam("name") String name, @RequestParam("password") String password, @RequestParam("email") String email) {
        //获取时间戳
        long date = System.currentTimeMillis();
        String uid2 = "u-" + date;
        User user = new User();
        user.setUid2(uid2);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        if (putNameToRedis(name)){
            if (userService.register(user)) {
                log.info("注册成功,username: {}, password: {}", name, password);
                return new ModelAndView("redirect:/base");
            }
            return new ModelAndView("register");
        }else{
            Map<String, String> model=new HashMap();
            model.put("message","用户名重复！请重新输入");
            return new ModelAndView("register",model);
        }

    }

    /**
     * 将注册的用户名放到Redis中，来识别注册的用户名是否重复
     * 之所以没有在注册方法那里设置，是因为user数据库中uid是主键
     * @param name 用户名
     */
    public boolean putNameToRedis(String name){
        if (redisUtil.sSet("RegisterUserName",name)==1){
            return true;
        }
        return false;
    }

    /**
     * 将用户名从Redis中取出
     */
    public void getNameFromRedis(){
        redisUtil.sGet("username");
    }

    /**
     * 验证email合规性，这个一般在前端页面验证
     * @param email
     * @return
     */
    public boolean judgeRegex(String email) {
        String regex = "^[\\w+\\.]+\\@\\w+" +
                "\\.(com|.org)$";
        if (email.matches(regex)) {
            return true;
        }
        return false;
    }

}
