package com.murphy.Interceptor;

import com.murphy.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Murphy
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String token = request.getHeader("token");
        log.info("token:{}", token);
        try{
            TokenUtils.decodeToken(token);
            log.info("token校验通过");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            log.info("token校验失败");
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
    }
}
