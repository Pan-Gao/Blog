package com.yrw.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;


public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private RedisTemplate redisTemplate;

    //维护热门博客排行榜
    @Scheduled(cron = "0 0/10 * * * *")
    public void rescaleHotBlogs() {
        logger.info("开始整理排行榜");
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        //删除排名在20名之后的
        zSetOperations.removeRangeByScore("hotBlogsRank", 0, -21);
        //把浏览量数字减半

        logger.info("排行榜整理完成");
    }
}
