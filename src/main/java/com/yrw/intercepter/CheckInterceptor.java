package com.yrw.intercepter;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
    String userAgent = request.getHeader("User-Agent");
    if (userAgent != null && userAgent != "") {
      if (userAgent.indexOf("Mozilla") != -1 || userAgent.indexOf("Opera") != -1)
        return true;
    }
    return false;
  }

  @Override
  public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
    throws Exception {
    // TODO 自动生成的方法存根
  }

  @Override
  public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
    throws Exception {
    // TODO 自动生成的方法存根

  }

}
