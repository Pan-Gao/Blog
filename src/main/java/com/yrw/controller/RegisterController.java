package com.yrw.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yrw.form.LoginForm;
import com.yrw.form.UserRegisterForm;
import com.yrw.model.Blog;
import com.yrw.model.User;
import com.yrw.service.BlogService;
import com.yrw.service.UserService;

import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

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
    public LoginForm register(@RequestBody UserRegisterForm form, 
    						HttpSession session) {
    		
    		LoginForm registerStatus = new LoginForm();
    		User user = form.toUser();
    		//创建新用户	
    		user = userService.register(user);
    		
    		if(user == null) {
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
