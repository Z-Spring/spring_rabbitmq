package com.murphy.controller;

import com.murphy.mapper.BookMapper;
import com.murphy.utils.RedisUtil;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    BookMapper bookMapper;

     private int incr=0;

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
        List<Integer> cartList1 = bookService.getId();
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            int uid = userService.getUid(user.getName());
            int count2 = bookService.getCount(pid, uid);
            Cart cart = new Cart(pid, count2, price, uid);
            List<Integer> cartList2 = bookService.getId2(uid);
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
                putProductIntoRedis(user.getName(), pid);
                model.addAttribute("cartList", bookService.getCart(uid));
                return "redirect:/base";
            }
        }
        return "redirect:/error";
    }

    @GetMapping(value = "/cart")
    public ModelAndView getCart(Model model, HttpSession session, HttpServletRequest request) throws IOException {
/*        String token = request.getHeader("token");
        DecodedJWT decodedJWT = TokenUtils.decodeToken(token);
        String user = decodedJWT.getClaim("user").asString();*/
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
    @ResponseBody
    @GetMapping(value = "/bookInfo")
    public String getBookInfo2(){
        log.info("进入bookInfo");
        return "123";
    }
    @PostMapping(value = "/bookInfo")
    public void getBookInfo2(@RequestParam String name, String zhang) {
        System.out.println(name);
        System.out.println(zhang);
        log.info("name:{}", name);
    }

    /**
     * 用来显示用户最近是否将商品添加过购物车
     * <p></p>
     * 近期添加过购物车提示会在7天后过期
     * <p/>
     * 这里利用Redis的list类型，name为key ,pid为value
     * 将商品pid添加到Redis中
     * @param name   用户姓名
     * @param pid   商品id
     */
    public void putProductIntoRedis(String name,int pid){
        redisTemplate.opsForList().rightPush(name,pid);
        redisTemplate.expire(name, 7, TimeUnit.DAYS);

        log.info("添加pid成功");
    }
    public List<Integer> getProductFromRedis(String name){
        return redisTemplate.opsForList().range(name,0,-1);
    }

    /**
     * 转发购买图书排行榜页面
     * @param response
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/rankingList")
    public ModelAndView rankingList(HttpServletResponse response,HttpServletRequest request,Model model){
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange("rankingList", 0, 2);
        log.info("rankingList: {}",set);
        model.addAttribute("rankingList",set);
        return new ModelAndView("rankingList");
    }

}
