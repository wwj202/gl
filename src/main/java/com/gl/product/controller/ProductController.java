package com.gl.product.controller;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.gl.common.controller.BaseController;
import com.gl.common.entity.SimpleResult;
import com.gl.product.service.ProductService;
import com.gl.util.HttpUtils;

@Controller
@RequestMapping("product")
public class ProductController extends BaseController {
	
	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody void testMVC(HttpServletRequest req, HttpServletResponse resp) {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		File dir = new File("");
		rs.setData(Thread.currentThread().getContextClassLoader().getResource("gl.mdb").getPath());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody void listProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(productService.getProductList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}
}