package com.yrw.service;

import com.google.gson.Gson;
import com.yrw.controller.BlogController;
import com.yrw.dao.BlogDao;
import com.yrw.dao.TagDao;
import com.yrw.exception.NotFoundException;
import com.yrw.model.Blog;
import com.yrw.util.LuceneUtils;
import com.yrw.util.TagUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BlogService {

  private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

  private BlogDao blogDao;
  private TagDao tagDao;
  private CacheService cacheService;
  private ZSetOperations zSetOperations;

  @Autowired
  public BlogService(RedisTemplate redisTemplate, BlogDao blogDao, TagDao tagDao, CacheService cacheService) {
    this.blogDao = blogDao;
    this.tagDao = tagDao;
    this.cacheService = cacheService;
    this.zSetOperations = redisTemplate.opsForZSet();
  }

  public Blog getBlogForBrowse(Long id) throws NotFoundException {
    //增加阅读量
    //按照hit从小到大排列，hit减1
    cacheService.incrScore("hotBlogsRank", "" + id, -1);
    return quickGetBlog(id);
  }

  public Blog getBlogForEdit(Long id) throws NotFoundException {
    return quickGetBlog(id);
  }

  private Blog quickGetBlog(Long id) throws NotFoundException {
    //从redis里获取博客数据
    String blogDetails = cacheService.getFromRedis("blogId:" + id);
    if (blogDetails == null) {
      //缓存里没有，从数据库里得到数据，根据浏览次数排名来判断是否要缓存
      logger.debug("从数据库里读取Blog");
      Blog blog = blogDao.getBlogById(id);
      //找不到这篇博客，404
      if (blog == null) {
        throw new NotFoundException("blog not found");
      }
      Long rank = zSetOperations.rank("hotBlogsRank", "" + id);
      if (rank != null && rank <= 20) {
        //排名在前二十，要缓存，转换成json
        Gson gson = new Gson();
        cacheService.setToRedis("blogId:" + id, gson.toJson(blog));
      }
      return blog;
    } else {
      logger.debug("从redis里读取Blog");
      //缓存里有，直接取出
      Gson gson = new Gson();
      Blog blog = gson.fromJson(blogDetails, Blog.class);
      return blog;
    }
  }

  //获取前十五名热门博客（3篇为一页）
  public List<Blog> getHotBlogs(int page) {
    Set<String> hotBlogs = zSetOperations.range("hotBlogsRank", (page - 1) * 3, page * 3 - 1);
    List<Blog> blogs = new ArrayList<>();
    for (String id : hotBlogs) {
      Blog blog = null;
      try {
        blog = quickGetBlog(Long.parseLong(id));
      } catch (NumberFormatException | NotFoundException e) {
        // TODO 自动生成的 catch 块
        e.printStackTrace();
      }
      blog.setContent(Jsoup.parse(blog.getContent()).text());
      blogs.add(blog);
    }
    return blogs;
  }

  /***
   *
   * @param userId 用户id
   * @param tag 标签
   * @param key 关键字
   * 没有就传递null
   * @return 查找到的博客集合
   * @throws IOException
   * @throws ParseException
   */
  public List<Blog> showBlogs(Long userId, Optional<String> tag, Optional<String> key) throws IOException, ParseException {
    if (userId != null) {
      return blogDao.getBlogs(userId, null);
    } else if (tag.isPresent()) {
      List<Blog> blogs = blogDao.getBlogs(null, tag.get());
      for (Blog blog : blogs) {
        blog.setContent(Jsoup.parse(blog.getContent()).text());
      }
      return blogs;
    } else if (key.isPresent() && key.get().length() > 0) {
      return LuceneUtils.search(key.get(), 1, 5);
    } else {
      return blogDao.getBlogs(null, null);
    }
  }

  @Transactional
  public Blog updateBlog(Long id, Blog form, String tags) {

    //找到要修改的博客
    Blog blog = blogDao.getBlogById(id);

    //更新标题和内容
    blog.setTitle(form.getTitle());
    blog.setContent(form.getContent());

    //更新博客
    blogDao.updateBlog(blog);
    //更新标签
    blog = TagUtils.setBlogTags(blog, tags);

    //清除缓存
    cacheService.delFromRedis("blogId:" + id);
    return blog;
  }

  @Transactional
  public Blog createBlog(Blog blog, String tags) {
    blogDao.createBlog(blog);
    TagUtils.setBlogTags(blog, tags);
    return blog;
  }

  @Transactional
  public Long deleteBlog(Long id) {
    blogDao.deleteBlog(id);
    tagDao.deleteByBlog(id);

    //清除缓存
    cacheService.delFromRedis("blogId:" + id);
    zSetOperations.remove("hotBlogsRank", "" + id);
    return id;
  }

}
