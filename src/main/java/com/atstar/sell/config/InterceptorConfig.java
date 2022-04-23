package com.atstar.sell.config;

import com.atstar.sell.interceptor.SellerAccessInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Dawn
 * @Date: 2022/4/22 14:38
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 要拦截所有访问请求，必须用户登录后才可访问，
        // 但是这样拦截的路径中有一些是不需要用户登录也可访问的
        String[] addPathPatterns = {"/seller/**"};

        // 要排除的路径，排除的路径说明不需要用户登录也可访问
        String[] excludePathPatterns = {"/seller/login", "/seller/logout"};


        // mvc:interceptor
        registry.addInterceptor(sellerAccessInterceptor()).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }

    @Bean
    public SellerAccessInterceptor sellerAccessInterceptor() {

        return new SellerAccessInterceptor();
    }
}
