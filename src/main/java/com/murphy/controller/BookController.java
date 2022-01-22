package com.murphy.controller;

import com.murphy.Utils.RedisUtil;
import com.murphy.entity.Cart;
import com.murphy.entity.User;
import com.murphy.service.BookService;
import com.murphy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 书籍页面转发
 * @author Murphy
 */
@Controller
@Slf4j
public class BookController {
    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserController userController;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 将商品添加到购物车
     * @param pid  商品id
     * @param count 商品数量
     * @param model
     * @param session 缓存用户信息
     * @param response
     * @return 重定向到相关网站
     * @throws IOException
     */
    @RequestMapping(value = "/cart/{pid}/{count}")
    public String addCart(@PathVariable("pid") int pid, @PathVariable("count") int count, Model model, HttpSession session, HttpServletRequest request,HttpServletResponse response) throws IOException {
        double price = bookService.getBookById(pid).getPrice();
        List cartList1 = bookService.getId();
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            int uid = userService.getUid(user.getName());
            int count2 = bookService.getCount(pid, uid);
            Cart cart = new Cart(pid, count2, price, uid);
            List cartList2 = bookService.getId2(uid);
            //        list不为空写法：  !cartList1.isEmpty()
            if (count2 > 0 && !cartList1.isEmpty() && cartList1.contains(pid) && cartList2.contains(pid)) {
                if (bookService.updateCart(cart)) {
                    //cart
                    return "redirect:/base";
                } else {
                    return "redirect:/error";
                }
            } else if (bookService.addCart(pid, count, price, uid)) {
                //如果购物车中没有此件商品，则直接添加到购物车中
                putProduceIntoRedis(user.getName(), pid);
                model.addAttribute("cartList", bookService.getCart(uid));
                return "redirect:/base";
            }
        }
        return "redirect:/error";
    }

    @GetMapping(value = "/cart")
    public ModelAndView getCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        int uid = userService.getUid(user.getName());
        //这里要注意list为空和null的区别！！
        if (bookService.getId2(uid).isEmpty()) {
            return new ModelAndView("emptyCart");
        } else {
            model.addAttribute("cartList", bookService.getCart(uid));
            return new ModelAndView("cart");
        }
    }

    /**
     * 商品详细信息
     * @return
     */
    @GetMapping(value = "/bookinfo/test")
    public ModelAndView getBookInfo() {
        int a = 1, b = 0, sum;
        sum = a + b;
        return new ModelAndView("bookinfo");
    }

    @PostMapping(value = "/bookinfo")
    public void getBookInfo2(@RequestParam String name, String zhang) {
        System.out.println(name);
        System.out.println(zhang);
        log.info("name:{}", name);
    }

    /**
     * 这里利用Redis的list类型，name为key ,pid为value
     * @param name   key
     * @param pid   value
     */
    public void putProduceIntoRedis(String name,int pid){
        redisTemplate.opsForList().rightPush(name,pid);
        log.info("添加pid成功");
    }
    public List getProduceFromRedis(String name){
        return redisTemplate.opsForList().range(name,0,-1);
    }
}
