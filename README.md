# myBlogSite
一个博客网站

框架：spring boot+mybatis

集成redis实现热门博客维护和标签的维护，ehcache作页面缓存

**目录**

[TOC]

#### 网站url符合rest风格
rest service是一种实现web service构架方式, 在这种框架下面, 对资源的访问和操作都是通过URI(Uniform Resource Identifiers)来实现的。
http://localhost/ 是网站首页
http://localhost/blogs/create 创建新文章页面
http://localhost/blogs/{id}/ 显示某一篇文章页面
http://localhost/blogs/{id}/edit 编辑某一篇文章页面
http://localhost/blogs/{id}/delete 删除某一篇文章
http://localhost/blogs/{id}/search 根据关键字检索


#### [表单验证](https://github.com/yuanrw/Blog/blob/master/note/valid.md)

#### [文件上传](https://github.com/yuanrw/Blog/blob/master/note/file.md)

#### 拦截器

#### Lucene全文检索

####异常处理

#### 缓存
