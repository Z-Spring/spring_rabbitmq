package com.murphy.Controller;

import com.murphy.entity.Book;
import com.murphy.entity.Cart;
import com.murphy.entity.User;
import com.murphy.service.BookService;
import com.murphy.service.UserService;
import com.mysql.cj.util.DnsSrv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/cart/{pid}/{count}")
    public String addCart(@PathVariable("pid") int pid, @PathVariable("count") int count, Model model, HttpSession session, HttpServletResponse response) throws IOException {
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
        if (bookService.getId2(uid).isEmpty()) {                 //这里要注意list为空和null的区别！！
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
    @GetMapping(value = "/bookinfo")
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


/*    @GetMapping (value = "/bookinfo/{name}")
    public void test(@PathVariable String name){
        System.out.println(name);
        log.info("name2:{}",name);
    }*/
    /*@RequestMapping (value = "/error")
    public ModelAndView error2(){
        return new ModelAndView("error");
    }*/
}
