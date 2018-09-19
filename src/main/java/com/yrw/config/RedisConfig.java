package com.yrw.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class RedisConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisPoolConfig getRedisConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory getConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        JedisPoolConfig config = getRedisConfig();
        factory.setPoolConfig(config);
        return factory;
    }


    @Bean(name = "redisTemplate")
    public RedisTemplate initRedisTemplate() {

        RedisTemplate redisTemplate = new RedisTemplate();
        RedisSerializer StringSerializer = new StringRedisSerializer();
        redisTemplate.setConnectionFactory(getConnectionFactory());
        redisTemplate.setDefaultSerializer(StringSerializer);
        redisTemplate.setKeySerializer(StringSerializer);
        redisTemplate.setValueSerializer(StringSerializer);
        redisTemplate.setHashKeySerializer(StringSerializer);
        redisTemplate.setHashValueSerializer(StringSerializer);
        return redisTemplate;
    }
}  
