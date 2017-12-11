package com.yrw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;
import com.yrw.controller.BlogController;
import com.yrw.dao.BlogDao;
import com.yrw.dao.TagDao;
import com.yrw.dao.UserDao;
import com.yrw.exception.NotFoundException;
import com.yrw.model.Blog;
import com.yrw.model.Tag;
import com.yrw.model.User;
import com.yrw.util.TagUtils;

@Service
public class BlogService{
	
	private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

	private BlogDao blogDao;
	private TagDao tagDao;
	private UserDao userDao;
	private RedisTemplate<String, String> redisTemplate;
	private ZSetOperations<String, String> zSetOperations;
	private ValueOperations<String, String> valueOperations;
	
	@Autowired
	public BlogService(RedisTemplate redisTemplate, UserDao userDao, BlogDao blogDao, TagDao tagDao) {
		this.blogDao = blogDao;
		this.tagDao = tagDao;
		this.userDao = userDao;
		this.redisTemplate = redisTemplate;
		this.zSetOperations = redisTemplate.opsForZSet();
		this.valueOperations = redisTemplate.opsForValue();

	}
	
	public Blog getBlog(Long id) throws NotFoundException{
		//增加阅读量
        //按照hit从小到大排列，hit减1
		zSetOperations.incrementScore("hotBlogsRank", "blogId:"+id, -1);
		//asyncService.updatebloghits(id); //目前数据库没有hits这一列
		
		//从redis里获取博客数据
		String blogDetails = valueOperations.get("blogId:"+id);
		if(blogDetails == null) {
			//缓存里没有，从数据库里得到数据，根据浏览次数排名来判断是否要缓存
			logger.debug("从数据库里读取Blog");
			Blog blog = blogDao.getBlogById(id);
			//找不到这篇博客，404
			if(blog == null) {
				throw new NotFoundException("blog not found");
			}
			/*Long rank = zSetOperations.rank("hotBlogsRank", "blogId:"+id);
			if(rank<=20) {
				//排名在前二十，要缓存，转换成json
				Gson gson = new Gson();
				String blogJson = gson.toJson(blog);
				valueOperations.set("blogId:"+id, blogJson);
			}*/
			return blog;
		}else {
			logger.debug("从redis里读取Blog");
			//缓存里有，直接取出
			Gson gson = new Gson();
			Blog blog = gson.fromJson(blogDetails, Blog.class);
			blog.setAuthor(userDao.getUserById(blog.getAuthor().getId()));
			return blog;
		}
	}
	
	//获取前十五名热门博客（3篇为一页）
	public List<Blog> getHotBlogs(int page) {
		Set<String> hotBlogs = zSetOperations.range("hotBlogsRank", (page-1)*3, page*3-1);
		List<Blog> blogs = new ArrayList<>();
		Gson gson = new Gson();
		for(String id:hotBlogs) {
			Blog blog = gson.fromJson(valueOperations.get(id+""), Blog.class); 
			blog.setContent(Jsoup.parse(blog.getContent()).text());
			blog.setAuthor(userDao.getUserById(blog.getAuthor().getId()));
			blogs.add(blog);
		}
		return blogs;
	}

	public List<Blog> findBlogs() {
		List<Blog> blogs =  blogDao.getAllBlogs();
		for(Blog blog:blogs) {
			blog.setContent(Jsoup.parse(blog.getContent()).text());
		}
		return blogs;
	}

	public void updateBlog(Long id, Blog form, String tags) {
	
		//找到要修改的博客
		Blog blog = blogDao.getBlogById(id);
		
		//更新标题和内容
		blog.setTitle(form.getTitle());
		blog.setContent(form.getContent());
		
		//更新博客
		blogDao.updateBlog(blog);
		//更新标签
		blog = TagUtils.setBlogTags(blog, tags);
		
		//如果是热门博客，清除缓存
		if(valueOperations.get("blogId:"+id)!=null) {
			redisTemplate.delete("blogId:"+id);
		}
	}
	
	public void createBlog(Blog blog, String tags) {
		blogDao.createBlog(blog);
		TagUtils.setBlogTags(blog, tags);
	}
	
	public void deleteBlog(Long id) {
		blogDao.deleteBlog(id);
		tagDao.deleteByBlog(id);
		
		//如果是热门博客，从缓存中删除
		if(valueOperations.get("blogId:"+id)!=null) {
			redisTemplate.delete("blogId:"+id);
			zSetOperations.remove("hotBlogsRank", "blogId:"+id);
		}
	}

	public List<Blog> showUserBlogs(Long id) {
		return blogDao.getBlogsByAuthor(id);
	}
	
	public List<Blog> getBlogsByTag(String tag){
		return blogDao.getBlogsByTag(tag);
	}

}
