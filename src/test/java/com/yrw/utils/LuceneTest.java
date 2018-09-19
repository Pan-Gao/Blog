package com.yrw.utils;

import com.yrw.model.Blog;
import com.yrw.util.LuceneUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public class LuceneTest {

    public void createTest() {
        Blog[] blogs = new Blog[10];
        for (int i = 0; i < 10; i++) {
            blogs[i] = new Blog("title" + i, "content" + i);
            blogs[i].setId(new Long(i));
            CreateIndex run = new CreateIndex(blogs[i]);
            Thread t = new Thread(run);
            t.start();
        }
    }

    public void updateTest() {
        Blog[] blogs = new Blog[3];
        for (int i = 0; i < 3; i++) {
            blogs[i] = new Blog("update title" + i, "update content" + i);
            blogs[i].setId(new Long(i));
            UpdateIndex run = new UpdateIndex(blogs[i]);
            Thread t = new Thread(run);
            t.start();
        }
    }

    public void deleteTest() {
        for (int i = 8; i < 10; i++) {
            DeleteIndex run = new DeleteIndex(new Long(i));
            Thread t = new Thread(run);
            t.start();
        }

    }

    public static void main(String[] args) throws IOException, ParseException {
        LuceneTest test = new LuceneTest();
        //test.createTest();
        //test.updateTest();
        test.deleteTest();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        List<Blog> blogs = LuceneUtils.search("title", 1, 100);
        for (Blog blog : blogs) {
            System.out.println(blog.toString());
        }
    }
}
