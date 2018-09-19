package com.yrw.dao;

import com.yrw.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 标签的增删改查
 */
@Mapper
public interface TagDao {

    //根据标签名字查找标签
    Tag findOne(String name);

    //返回一篇博客的所有标签
    Set<Tag> findByBlog(@Param("blogId") Long blogId);

    //删除一篇博客的所有标签
    void deleteByBlog(@Param("blogId") Long blogId);

    //创建一个标签
    void create(@Param("name") String name);

    //给一篇博客增加一个标签
    void addOneTagToBlog(@Param("tag") String name, @Param("blogId") Long blogId);
}
