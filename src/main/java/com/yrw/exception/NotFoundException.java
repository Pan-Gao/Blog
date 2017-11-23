package com.yrw.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)//找不到页面
public class NotFoundException extends Exception{
	public NotFoundException(String message) {
        super(message);
    }
}
