package com.yrw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

/*
 * Servlet类所在的包路径必须是BlogApplication所在的包路径的子路径才能被扫描到，
 * 否则需要通过basePackages属性指定Servlet类所在的包。
 */
@SpringBootApplication
@EnableScheduling
public class BlogApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}
}
