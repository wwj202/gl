package com.gl.product.controller;

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
import com.gl.exception.BusinessException;
import com.gl.product.entity.SellDetail;
import com.gl.product.entity.SellOrder;
import com.gl.product.service.SellService;
import com.gl.util.DateUtils;
import com.gl.util.HttpUtils;


@Controller
@RequestMapping("sell")
public class SellController extends BaseController {
	
	@Autowired
	private SellService service;

	@RequestMapping(value = "/order/list", method = RequestMethod.GET)
	public @ResponseBody void listSellOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getSellOrderList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/order/add", method = RequestMethod.POST)
	public @ResponseBody void addNewSellOrder(SellOrder order, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.addNewSellOrder(order);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/order/update", method = RequestMethod.POST)
	public @ResponseBody void updateSellOrder(SellOrder order, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.updateSellOrder(order);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/order/delete", method = RequestMethod.POST)
	public @ResponseBody void deleteSellOrder(Integer orderId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.deleteSellOrder(orderId);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/detail/list", method = RequestMethod.GET)
	public @ResponseBody void listSellDetail(Integer orderId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (orderId == null || orderId <= 0) {
			throw new BusinessException("参数不正确！");
		}
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getSellDetailList(orderId));
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/detail/add", method = RequestMethod.POST)
	public @ResponseBody void addNewSellDetail(SellDetail detail, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.addNewSellOrder(detail);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/detail/update", method = RequestMethod.POST)
	public @ResponseBody void updateSellDetail(SellDetail detail, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.updateSellDetail(detail);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/detail/delete", method = RequestMethod.POST)
	public @ResponseBody void deleteSellDetail(SellDetail detail, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.deleteSellDetail(detail);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/detail/export", method = RequestMethod.POST)
	public @ResponseBody void exportSellDetail(Integer orderId, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (orderId == null || orderId <= 0) {
			throw new BusinessException("参数不正确！");
		}
		service.exportSellDetail(resp, orderId);
	}

}
