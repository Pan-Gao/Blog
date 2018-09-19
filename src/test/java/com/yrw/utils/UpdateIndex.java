package com.yrw.utils;

import com.yrw.model.Blog;
import com.yrw.util.LuceneUtils;

import java.io.IOException;

public class UpdateIndex implements Runnable {
    private Blog blog;

    UpdateIndex(Blog blog) {
        this.blog = blog;
    }

    @Override
    public void run() {
        try {
            LuceneUtils.updateIndex(blog);
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
