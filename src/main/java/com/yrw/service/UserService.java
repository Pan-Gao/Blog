package com.yrw.service;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yrw.controller.BlogController;
import com.yrw.dao.UserDao;
import com.yrw.model.Blog;
import com.yrw.model.User;

@Service
public class UserService{
	
	private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private BlogService blogService;
	
	private ZSetOperations<String, String> zSetOperations;
	private ValueOperations<String, String> valueOperations;
	
	@Autowired
	public UserService(RedisTemplate redisTemplate) {
		this.zSetOperations = redisTemplate.opsForZSet();
		this.valueOperations = redisTemplate.opsForValue();

	}
	
	//通过id找到用户
	public User getUserById(Long id) {
		//增加阅读量
        //按照hit从小到大排列，hit减1
		zSetOperations.incrementScore("hotUsersRank", "userId:"+id, -1);
		//asyncService.updatebloghits(id); //目前数据库没有hits这一列
		
		//从redis里获取博客数据
		String userDetails = valueOperations.get("userId:"+id);
		if(userDetails == null) {
			//缓存里没有，从数据库里得到数据，根据浏览次数排名来判断是否要缓存
			logger.info("从数据库里读取User");
			User user = userDao.getUserById(id);
			Long rank = zSetOperations.rank("hotUsersRank", "userId:"+id);
			if(rank<=20) {
				//排名在前二十，要缓存，转换成json
				Gson gson = new Gson();
				String userJson = gson.toJson(user);
				valueOperations.set("userId:"+id, userJson);
			}
			return user;
		}else {
			logger.info("从redis里读取User");
			//缓存里有，直接取出
			Gson gson = new Gson();
			return gson.fromJson(userDetails, User.class);
		}
	}
		
	//通过邮箱找到用户
	public User getUserByEmail(String email) {
		return userDao.getUserByEmail(email);
	}
	
	//更新用户信息
	public void updateUser(User user) {
		userDao.updateUser(user);
		if(valueOperations.get("userId:"+user.getId())!=null) {
			//更新缓存
			Gson gson = new Gson();
			System.out.println("updateuser");
			valueOperations.set("userId:"+user.getId(), gson.toJson(user));
		}
	}
	
	//登陆
	public User login(String email, String password) {
		//从数据库中得到用户信息
		User user = userDao.getUserByEmail(email);
		if(user!=null && user.getPassword().equals(password)) {
			//密码正确
			return user;
		}else {
			//密码错误
			return null;
		}
	}
	
	//注册
	public User register(User user) {
		//邮箱已存在
		if(userDao.getUserByEmail(user.getEmail())!=null) {
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
