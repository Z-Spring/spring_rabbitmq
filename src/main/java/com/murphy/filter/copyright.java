package com.murphy.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author Murphy
 */
@Component
//@WebFilter("/*")
public class copyright extends FilterRegistrationBean<Filter> {
    @Override
    public Filter getFilter() {
        return new CopyRightFilter();
    }
}

class CopyRightFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        request.setAttribute("copyright", "Copyright © 2001-2021 Murphy.保留所有权利");
        //调用该方法后，表示过滤器经过原来的url请求处理方法
        //这句话很重要！！！
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
