package com.yrw.util;

import com.yrw.dao.TagDao;
import com.yrw.model.Blog;
import com.yrw.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagUtils {

    private static TagDao tagDao;

    @Autowired
    public void setTagDao(TagDao tagDao) {
        TagUtils.tagDao = tagDao;
    }

    //得到String类型的tag
    public static String toStringTags(Set<Tag> tags) {
        StringBuilder tagStringBuilder = new StringBuilder();
        boolean first = true;
        for (Tag tag : tags) {
            if (first) {
                tagStringBuilder.append(tag.getName());
                first = false;
            } else {
                tagStringBuilder.append(",").append(tag.getName());
            }
        }
        return tagStringBuilder.toString();
    }

    //得到Set类型的tag
    public static Set<Tag> toSetTags(String tags) {
        String[] tagStr = tags.split(",");
        Set<Tag> tagList = new HashSet<>();
        for (String tag : tagStr) {
            tagList.add(new Tag(tag));
        }
        return tagList;
    }

    //给博客设置标签，并把设置好的博客返回
    public static Blog setBlogTags(Blog blog, String tags) {
        Set<Tag> oldTagsSet = blog.getTags();
        if (oldTagsSet == null || oldTagsSet.isEmpty()) {
            if (tags == null || tags.isEmpty()) {
                //本来标签为空，现在也为空
                return blog;
            }
        } else {
            //本来标签不为空，删除老的标签
            tagDao.deleteByBlog(blog.getId());
        }

        Set<Tag> newTagsSet = new HashSet<>();
        String[] newTags = tags.split(",");    //获取新的标签列表
        for (String tag : newTags) {
            if (tag.trim().isEmpty()) {
                continue;
            }
            //去重
            if (!newTagsSet.contains(new Tag(tag))) {
                newTagsSet.add(new Tag(tag));
                if (tagDao.findOne(tag) == null) {
                    //数据库里没有的标签
                    tagDao.create(tag);
                }
                tagDao.addOneTagToBlog(tag, blog.getId());
            }
        }
        blog.setTags(newTagsSet);
        return blog;
    }

}
