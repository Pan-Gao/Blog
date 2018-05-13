package com.yrw.dao;

import com.yrw.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户的增删改查
 */
@Mapper
public interface UserDao {

  //通过id或邮箱找到用户
  public User getUser(@Param("id") Long id, @Param("email") String email);

  //更新用户信息
  public void updateUser(User user);

  //创建一个用户
  public void createUser(User user);

  //删除一个用户
  public void deleteUser(@Param("id") long id);
}
