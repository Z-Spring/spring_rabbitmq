package com.murphy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.entity.KeyWord;
import com.murphy.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;


/**
 * 搜索框
 *
 * @author Murphy
 */
@Controller
@Slf4j
public class SearchController {
    @Autowired
    BookService bookService;
    @Autowired
    ObjectMapper objectMapper;

    @ResponseBody
    @GetMapping(value = "/search")
    public List searchBook(String q) throws IOException {
        log.info("keyword:{}", q);
        log.info("{}", bookService.searchBook(q));
        return bookService.searchBook(q);
    }

/*    @GetMapping(value = "/search")
    public ModelAndView searchBook(String q, Model model) throws IOException {
        log.info("keyword:{}", q);
        log.info("{}", bookService.searchBook(q));
        model.addAttribute("bookInfo", bookService.searchBook(q));
        return new ModelAndView("search_page");
//        return bookService.searchBook(q);
    }*/

    @GetMapping("/search_page")
    public ModelAndView searchPage1(Model model) throws IOException {
        log.info("get search_page");
        model.addAttribute("bookInfo", "dad");
        return new ModelAndView("search_page");
    }

}
