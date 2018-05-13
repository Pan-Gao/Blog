package com.yrw.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(CacheConfiguration.class)
public class PageCacheConfiguration {

	/*
	//这两个页面缓存一下
    @Bean
    public FilterRegistrationBean registerBlogsPageFilter(){
        PageCachingFilter customPageCachingFilter = new PageCachingFilter("com.yrw.mapper.index");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customPageCachingFilter);
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/", "/blogs"));
        return filterRegistrationBean;
    }

    //清除页面缓存
    @Bean
    public FilterRegistrationBean registerClearBlogsPageFilter(){
        ClearPageCachingFilter clearPageCachingFilter = new ClearPageCachingFilter("com.yrw.mapper.index");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(clearPageCachingFilter);
        filterRegistrationBean.setUrlPatterns(Lists.newArrayList("/clear"));
        return filterRegistrationBean;
    }
*/
}