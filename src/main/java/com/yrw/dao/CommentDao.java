package com.yrw.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yrw.model.Comment;

@Mapper
@Repository
public interface CommentDao {
	
	public void createComment(@Param("blogId") Long blogId, Comment comment);
	
	public void deleteComment(@Param("id") Long commentId);
	
	public List<Comment> getCommentsByBlogId(@Param("id") Long id);
}
