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
import com.gl.product.entity.PurchaseOrder;
import com.gl.product.service.PurchaseService;
import com.gl.util.DateUtils;
import com.gl.util.HttpUtils;


@Controller
@RequestMapping("purchase")
public class PurchaseController extends BaseController {
	
	@Autowired
	private PurchaseService service;

	@RequestMapping(value = "/order/list", method = RequestMethod.GET)
	public @ResponseBody void listPurchaseOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getPurchaseOrderList());
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

	@RequestMapping(value = "/order/add", method = RequestMethod.POST)
	public @ResponseBody void addNewPurchaseOrder(PurchaseOrder order, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.addNewPurchaseOrder(order);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/order/update", method = RequestMethod.POST)
	public @ResponseBody void updatePurchaseOrder(PurchaseOrder order, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		SimpleResult rs = new SimpleResult();
		service.updatePurchaseOrder(order);
		rs.setResult("suc");
		
		HttpUtils.writeJsonString(resp, JSON.toJSONString(rs));
	}

	@RequestMapping(value = "/detail/list", method = RequestMethod.GET)
	public @ResponseBody void listDetail(Integer id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (id == null || id <= 0) {
			throw new BusinessException("参数不正确！");
		}
		SimpleResult rs = new SimpleResult();
		rs.setResult("suc");
		rs.setData(service.getPurchaseDetailList(id));
		
		HttpUtils.writeJsonString(resp, JSON.toJSONStringWithDateFormat(rs, DateUtils.DATE_FORMAT));
	}

}
