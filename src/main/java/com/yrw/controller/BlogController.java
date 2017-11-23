package com.yrw.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yrw.exception.NotFoundException;
import com.yrw.form.BlogCreateForm;
import com.yrw.model.Blog;
import com.yrw.model.User;
import com.yrw.service.BlogService;
import com.yrw.service.CommentService;
import com.yrw.service.UserService;
import com.yrw.util.TagUtils;

@Controller
public class BlogController{
	
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private CommentService commentService;
	
	//展示所有博文
	@GetMapping("/blogs")
	public String showBlogs(Model model, 
			@RequestParam("tag") Optional<String> tag, 
			@RequestParam("page") Optional<Integer> page) {
		PageHelper.startPage(page.orElse(1), 5);
		if(tag.isPresent()) {
			model.addAttribute("blogs",new PageInfo<>(blogService.getBlogsByTag(tag.get())));
		}else {
			model.addAttribute("blogs",new PageInfo<>(blogService.findBlogs()));
		}
		return "list";
	}
		
	//展示某一篇博文
	@GetMapping("/blogs/{id}")
	public String showBlog(@PathVariable("id") Long id, Model model) throws NotFoundException {
		Blog blog = blogService.getBlog(id);
		model.addAttribute("blog", blog);
		model.addAttribute("comments", commentService.getCommentsByBlogId(id));
		return "item";
	}

	//获取编辑某一篇博文的页面
	@GetMapping("/blogs/{id}/edit")
	public String showEditBlog(@PathVariable("id") Long id, Model model) throws NotFoundException {
		Blog blog = blogService.getBlog(id);
		model.addAttribute("blog", blog);
		model.addAttribute("alltags", TagUtils.toStringTags(blog.getTags()));
		model.addAttribute("edit", 1);
		return "create";
	}

	//提交修改过的博文
	@PostMapping("/blogs/{id}")
	public String updateBlog(@PathVariable("id") Long id, 
						@RequestParam(value="alltags", required=false) String tags, 
						@ModelAttribute("blog") @Valid BlogCreateForm form, 
						BindingResult result, 
						HttpSession session) {
		if(result.hasErrors()){
			return "create";
		}
		Blog blog = form.toBlog();
		blogService.updateBlog(id, blog, tags);
		return "redirect:/blogs/"+id;
	}
	
	//获取新建博文的页面
	@GetMapping("blogs/create")
	public String showCreatePage(@ModelAttribute("user") User user, 
							@ModelAttribute("blog") Blog blog) {
		return "create";
	}
	
	//提交新建的博文
	@PostMapping("/blogs")
	public String createBlog(@ModelAttribute ("blog") @Valid BlogCreateForm form, 
						BindingResult result, 
						@RequestParam(value="alltags", required=false) String tags, 
						HttpSession session) {
		if(result.hasErrors()){
    			return "create";
    		}
		User user = ((User)session.getAttribute("CURRENT_USER"));
		Blog blog = form.toBlog();
		blog.setAuthor(user);
		blogService.createBlog(blog, tags);
		return "redirect:/blogs/"+blog.getId();
	}

	//删除一篇博文
	@DeleteMapping("/blogs/{id}")
	public String deleteBlog(@PathVariable("id") Long id, 
						HttpSession session, 
						final RedirectAttributes redirectAttributes) {
		blogService.deleteBlog(id);
		redirectAttributes.addFlashAttribute("delete", "success");
		return "redirect:/admin/"+((User)session.getAttribute("CURRENT_USER")).getId();
	}
}
