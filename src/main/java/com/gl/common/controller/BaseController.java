package com.gl.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gl.common.entity.SimpleResult;
import com.gl.exception.BusinessException;

public class BaseController {
	
	@ExceptionHandler
	public void exception(HttpServletRequest req, HttpServletResponse resp, Exception ex) {
		SimpleResult rs = new SimpleResult();
		rs.setResult("fail");
		if (ex instanceof BusinessException) {
			rs.setMsg(ex.getMessage());
		}
		else {
			ex.printStackTrace(System.out);
		}
	}

}
