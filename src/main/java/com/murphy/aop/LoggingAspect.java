package com.murphy.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author Murphy
 */
@Aspect
@Component
public class LoggingAspect {
    @After("execution(public * com.murphy.controller.BookController.*(..))")
    public void doAccessCheck() {
        System.err.println("[Before] do access check...");
    }
}
