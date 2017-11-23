package com.yrw.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.yrw.model.Blog;

/*
 * 博客的增删改查
 */
@Mapper
@Repository
public interface BlogDao {
	
	//通过博客id找到博客
	Blog getBlogById(@Param("id") Long id);
	
	//找到某一个作者的所有博客
	List<Blog> getBlogsByAuthor(@Param("id") Long id);
	
    List<Blog> getBlogsByTag(@Param("tag") String tag);
	
	//返回所有博客
	List<Blog> getAllBlogs();
	
	//创建一篇新的博客
	void createBlog(@Param("blog") Blog blog);
	
	//修改一篇博客
	void updateBlog(Blog blog);
	
	//通过id删除一篇博客
	void deleteBlog(@Param("id") Long id);
	
	//增加博客浏览量
	void updateBlogHits(@Param("id") Long id);
}
