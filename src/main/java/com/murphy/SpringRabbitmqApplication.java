package com.murphy;

import com.murphy.Interceptor.JwtInterceptor;
import com.murphy.Interceptor.LoginInterceptor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Murphy
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class SpringRabbitmqApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringRabbitmqApplication.class, args);
    }

    @Bean
    Jackson2JsonMessageConverter createMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 拦截器  添加CORS跨域
     * @param interceptors
     * @return
     */
    @Bean
    WebMvcConfigurer createWebMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
//                for (var interceptor : interceptors) {
//                    registry.addInterceptor(interceptor).excludePathPatterns("/register", "/login","/search/*","/test/*",
//                            "/search_page","/base", "/**/*/*.js", "/**/*.css", "/**/*.html", "/**/*.png");
//
//                }
//                registry.addInterceptor(new LoginInterceptor())
//                        .excludePathPatterns("/register", "/login","/search/*","/test/*","/rankingList",
//                            "/search_page","/base", "/**/*/*.js", "/**/*.css", "/**/*.html", "/**/*.png");
//                registry.addInterceptor(new JwtInterceptor())
//                        .excludePathPatterns("/register", "/login","/search/*","/test/*","/passToken",
//                        "/search_page","/base", "/**/*/*.js", "/**/*.css", "/**/*.html", "/**/*.png");
            }
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .maxAge(3600L);
            }
        };
    }
}
