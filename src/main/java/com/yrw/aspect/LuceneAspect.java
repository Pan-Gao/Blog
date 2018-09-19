package com.yrw.aspect;

import com.yrw.model.Blog;
import com.yrw.util.LuceneUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
public class LuceneAspect {

    @Pointcut("execution(* com.yrw.service.BlogService.createBlog(..))")
    private void createPointCut() {
    }

    @Pointcut("execution(* com.yrw.service.BlogService.updateBlog(..))")
    private void updatePointCut() {
    }

    @Pointcut("execution(* com.yrw.service.BlogService.deleteBlog(..))")
    private void deletePointCut() {
    }

    @AfterReturning(pointcut = "createPointCut()", returning = "blog") //1
    public void createIndex(Blog blog) {
        if (blog != null) {
            try {
                LuceneUtils.createIndex(blog);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(pointcut = "updatePointCut()", returning = "blog") //1
    public void updateIndex(Blog blog) {
        if (blog != null) {
            try {
                LuceneUtils.updateIndex(blog);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
    }

    @AfterReturning(pointcut = "deletePointCut()", returning = "id") //1
    public void deleteIndex(Long id) {
        try {
            LuceneUtils.deleteIndex(id);
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
