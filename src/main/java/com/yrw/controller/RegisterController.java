package com.yrw.controller;

import com.yrw.form.LoginStatus;
import com.yrw.form.UserRegisterForm;
import com.yrw.model.User;
import com.yrw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class RegisterController {

  @Autowired
  private UserService userService;

  @GetMapping("/register")
  public String get(@ModelAttribute("user") User user) {
    return "register";
  }

  @PostMapping("/register")
  @ResponseBody
  public LoginStatus register(@RequestBody UserRegisterForm form,
                              HttpSession session) {

    LoginStatus registerStatus = new LoginStatus();
    User user = form.toUser();
    //创建新用户
    user = userService.register(user);

    if (user == null) {
      registerStatus.setStatus("failed");
      registerStatus.setMessage("邮箱已存在！");
      return registerStatus;
    }

    //将用户放进session中
    session.setAttribute("CURRENT_USER", user);

    registerStatus.setStatus("success");
    registerStatus.setMessage("注册成功！");
    registerStatus.setInfo(user.getId().toString());
    return registerStatus;
  }
}
