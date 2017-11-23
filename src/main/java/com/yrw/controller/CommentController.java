package com.yrw.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yrw.form.CommentForm;
import com.yrw.model.Comment;
import com.yrw.model.User;
import com.yrw.service.CommentService;

@Controller
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	//提交评论
	@PostMapping("/blogs/{id}/comments")
	@ResponseBody
	public CommentForm commentBlog(@RequestBody CommentForm form, 
						@PathVariable("id") Long id, 
						Model model, 
						HttpSession session) throws ParseException {

		form.setCommentor(((User)session.getAttribute("CURRENT_USER")));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		form.setCreatedTime(format.format(new Date()));
		Comment newComment = form.toComment();
		commentService.createComment(id, newComment);
		form.setStatus("success");
		form.setMessage("评论成功！");
		return form;
	}
}
