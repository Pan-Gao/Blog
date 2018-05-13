package com.yrw.utils;

import com.yrw.model.Blog;
import com.yrw.util.LuceneUtils;

import java.io.IOException;

public class CreateIndex implements Runnable {

  private Blog blog;

  CreateIndex(Blog blog) {
    this.blog = blog;
  }

  @Override
  public void run() {
    try {
      LuceneUtils.createIndex(blog);
    } catch (IOException e) {
      // TODO 自动生成的 catch 块
      e.printStackTrace();
    }
  }

}
