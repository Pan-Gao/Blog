package com.yrw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * Servlet类所在的包路径必须是BlogApplication所在的包路径的子路径才能被扫描到，
 * 否则需要通过basePackages属性指定Servlet类所在的包。
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class BlogApplication {
  public static void main(String[] args) {
    SpringApplication.run(BlogApplication.class, args);
  }
}
