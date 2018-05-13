package com.yrw.config;

import com.yrw.intercepter.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 配置拦截器
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    //registry.addInterceptor(new CheckInterceptor())
    //		.addPathPatterns("/**");
    registry.addInterceptor(new LoginInterceptor())
      .addPathPatterns("/blogs/create");
    registry.addInterceptor(new LoginInterceptor())
      .addPathPatterns("/admin/**");
    registry.addInterceptor(new LoginInterceptor())
      .addPathPatterns("**/comments");
  }
}
