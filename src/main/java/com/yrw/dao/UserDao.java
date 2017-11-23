package com.yrw.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.yrw.model.User;

/*
 * 用户的增删改查
 */
@Mapper
@Repository
public interface UserDao {
	
	//通过id找到用户
	public User getUserById(@Param("id") long id);
	
	//通过邮箱找到用户
	public User getUserByEmail(@Param("email") String email);
	
	//更新用户信息
	public void updateUser(User user);
	
	//创建一个用户
	public void createUser(User user);
	
	//删除一个用户
	public void deleteUser(@Param("id") long id);
}
