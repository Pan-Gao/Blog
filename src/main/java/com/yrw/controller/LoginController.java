package com.yrw.controller;

import com.yrw.form.LoginStatus;
import com.yrw.form.UserLoginForm;
import com.yrw.model.User;
import com.yrw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    //用户登录界面
    @GetMapping("/login")
    public String get(@ModelAttribute("user") User user) {
        return "login";
    }

    //用户登录，通过email
    @PostMapping("/login")
    @ResponseBody
    public LoginStatus login(@RequestBody UserLoginForm form,
                             @RequestParam("remember-me") Optional<String> rememberMe,
                             @RequestParam("next") Optional<String> next,
                             HttpServletResponse response,
                             HttpSession session) {

        LoginStatus loginStatus = new LoginStatus();
        //登陆
        User user = userService.login(form.getEmail(), form.getPassword());

        if (user == null) {
            //登陆失败
            loginStatus.setStatus("failed");
            loginStatus.setMessage("登陆失败！");
            return loginStatus;
        } else {
            //登陆成功
            session.setAttribute("CURRENT_USER", user);
            //添加cookie，7天内免登陆
            if (rememberMe != null) {
                Cookie cookie = new Cookie("YRWsBlog_email", user.getEmail());
                cookie.setMaxAge(60 * 60 * 24 * 7);
                response.addCookie(cookie);
            }
            loginStatus.setStatus("success");
            loginStatus.setMessage("登陆成功！");
            loginStatus.setInfo(next.isPresent() ? next.get() : "");
            return loginStatus;
        }
    }

    //用户登出
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {
        //清空session和cookie
        Cookie cookie = new Cookie("YRWsBlog_email", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        session.removeAttribute("CURRENT_USER");

        //回到主页
        return "redirect:/";
    }
}
