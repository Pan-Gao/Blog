package com.yrw.service;


import com.yrw.dao.UserDao;
import com.yrw.model.Blog;
import com.yrw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BlogService blogService;

    //通过id找到用户
    public User getUserById(Long id) {
        return userDao.getUser(id, null);
    }

    //通过邮箱找到用户
    public User getUserByEmail(String email) {
        return userDao.getUser(null, email);
    }

    //更新用户信息
    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    //登陆
    public User login(String email, String password) {
        //从数据库中得到用户信息
        User user = userDao.getUser(null, email);
        if (user != null && user.getPassword().equals(password)) {
            //密码正确
            return user;
        } else {
            //密码错误
            return null;
        }
    }

    //注册
    public User register(User user) {
        //邮箱已存在
        if (userDao.getUser(null, user.getEmail()) != null) {
            return null;
        }
        //注册新用户
        user.setDescription("TA还没有自我介绍哦！");
        userDao.createUser(user);

        //自动给新用户创建一篇博客
        Blog firstBlog = new Blog("我的第一篇博客", "嗨！这是你的第一篇博客！", user);
        firstBlog.setCreatedTime(new Date());
        blogService.createBlog(firstBlog, null);

        return user;
    }
}
