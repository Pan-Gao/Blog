package com.yrw.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.yrw.exception.NotFoundException;

//这个注释可以被应用于Spring应用上下文中的所有@Controller抛出的异常
@ControllerAdvice
public class ExceptionController {
	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);
    
    //404页面
    @ExceptionHandler(NotFoundException.class)
    public String notFoundException() throws Exception{
        return "404";
    }
    /*
    //方便调试
    @ExceptionHandler(value = Exception.class)
	public ModelAndView defaultEorrerHandler(HttpServletRequest req, Exception e) throws Exception{
        logger.error("Request: " + req.getRequestURL() + " raised " + e);

		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null){     
			throw e;
		}

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("Global");
        return mav;
	}*/
}
