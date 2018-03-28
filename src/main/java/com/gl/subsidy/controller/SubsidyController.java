package com.gl.subsidy.controller;

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
import com.gl.subsidy.entity.Subsidy;
import com.gl.subsidy.service.SubsidyService;
import com.gl.util.DateUtils;
import com.gl.util.HttpUtils;

@Controller
@RequestMapping("subsidy")
public class SubsidyController extends BaseController {
	
	@Autowired
	private SubsidyService service;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody void listSubsidy(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getSubsidyList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/stat", method = RequestMethod.GET)
	public @ResponseBody void listSubsidyStatistics(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.listSubsidyStatistics());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public @ResponseBody void addNewSubsidy(Subsidy subsidy, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.addNewSubsidy(subsidy);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody void updateSubsidy(Subsidy subsidy, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.updateSubsidy(subsidy);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody void deleteSubsidy(Integer subsidyId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.deleteSubsidy(subsidyId);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

}
