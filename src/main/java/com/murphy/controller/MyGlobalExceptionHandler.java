package com.murphy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Murphy
 */
@ControllerAdvice
@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyGlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e){
        ModelAndView mv=new ModelAndView("myerror");
        mv.addObject("error",e.getMessage());
        return mv;
    }


}
