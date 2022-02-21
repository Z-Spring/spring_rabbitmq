package com.murphy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.entity.KeyWord;
import com.murphy.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;


/**
 * 搜索框
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
    @GetMapping (value = "/search/{keyword}")
    public List searchBook(@PathVariable String keyword) throws IOException {
        log.info("success");
        return bookService.searchBook(keyword);
    }

    @GetMapping("/search_page")
    public ModelAndView searchPage1() throws IOException {
        log.info("get search_page");
        return new ModelAndView("search_page");
    }

}
