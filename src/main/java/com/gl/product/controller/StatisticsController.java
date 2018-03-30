package com.gl.product.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.gl.common.entity.SimpleResult;
import com.gl.product.service.StatisticsService;
import com.gl.util.DateUtils;
import com.gl.util.HttpUtils;

@Controller
@RequestMapping("stat")
public class StatisticsController {
	
	@Autowired
	private StatisticsService service;

	@RequestMapping(value = "/sellRanking", method = RequestMethod.GET)
	public @ResponseBody void getSellRankingList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getSellRankingList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/stock", method = RequestMethod.GET)
	public @ResponseBody void getStockStatList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getStockStatList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

}
