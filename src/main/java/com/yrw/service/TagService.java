package com.yrw.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.yrw.model.Tag;

@Component
public class TagService {
	
	private ZSetOperations<String, String> zSetOperations;
	
	@Autowired
	public TagService(RedisTemplate redisTemplate) {
		this.zSetOperations = redisTemplate.opsForZSet();
	}
	
	public List<Tag> getTags() {
		Set<String> tagsSet = zSetOperations.range("tags", 0, 19);
		List<Tag> tags = new ArrayList<>();
		Gson gson = new Gson();
		for(String tag:tagsSet) {
			tags.add(new Tag(tag, zSetOperations.score("tags", tag)));
		}
		return tags;
	}
	
	public void incrblogsNum(Tag tag, int n) {
		zSetOperations.incrementScore("tags", tag.getName(), n);
	}
	
}
