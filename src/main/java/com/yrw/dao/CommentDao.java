package com.yrw.dao;

import com.yrw.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface CommentDao {

    void createComment(@Param("blogId") Long blogId, Comment comment);

    void deleteComment(@Param("id") Long commentId);

    List<Comment> getCommentsByBlogId(@Param("id") Long id);
}
