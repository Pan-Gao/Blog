package com.yrw.controller;

import com.yrw.model.Blog;
import com.yrw.model.Tag;
import com.yrw.service.BlogService;
import com.yrw.service.TagService;
import com.yrw.service.UserService;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    Logger logger = Logger.getLogger(IndexController.class);

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    //网站首页
    @GetMapping("/")
    public String showMainPage(@CookieValue("YRWsBlog_email") Optional<String> cookieEmail,
                               HttpSession session,
                               Model model,
                               @RequestParam("page") Optional<Integer> page) {
        logger.debug("访问controller [/]");
        //自动登录
        if (session.getAttribute("CURRENT_USER") == null
                && cookieEmail != null && cookieEmail.isPresent()) {
            session.setAttribute("CURRENT_USER", userService.getUserByEmail(cookieEmail.get()));
        }

        //获取热门博客
        List<Blog> blogs = blogService.getHotBlogs(page.orElse(1));
        model.addAttribute("blogs", blogs);
        if (page.isPresent()) {
            model.addAttribute("page", page.get());
        } else {
            model.addAttribute("page", 1);
        }
        return "index";
    }

    @ResponseBody
    @GetMapping("/tags")
    public List<Tag> getTags() {
        return tagService.getTags();
    }

    //用户的个人主页
    @GetMapping("/admin/{id}")
    public String showUserPage(@PathVariable("id") long id, Model model) throws IOException, ParseException {
        model.addAttribute("blogs", blogService.showBlogs(id, null, null));
        return "admin";
    }

}
