package com.yrw.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;


public class ClearPageCachingFilter implements Filter {

	private final CacheManager cacheManager;

    private final String customCacheName;
    
    public ClearPageCachingFilter(String name){
        this.customCacheName = name;
        cacheManager = CacheManager.getInstance();
        assert cacheManager != null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    //暂时先简单地把缓存全部删光
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        Ehcache ehcache = cacheManager.getEhcache(customCacheName);
        ehcache.removeAll();
    }

    @Override
    public void destroy() {}
	
}
