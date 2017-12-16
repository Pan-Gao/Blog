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
	
	//通过一定条件查找博客
	List<Blog> getBlogs(@Param("userId") Long userId, @Param("tag") String tag);
	
	//创建一篇新的博客
	void createBlog(@Param("blog") Blog blog);
	
	//修改一篇博客
	void updateBlog(Blog blog);
	
	//通过id删除一篇博客
	void deleteBlog(@Param("id") Long id);

}
