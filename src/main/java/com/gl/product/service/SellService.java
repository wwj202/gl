package com.gl.product.service;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gl.db.JdbcService;
import com.gl.product.entity.Product;
import com.gl.product.entity.SellDetail;
import com.gl.product.entity.SellOrder;
import com.gl.util.DateUtils;
import com.gl.util.ExcelUtilPoi;

@Service
public class SellService {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private JdbcService jdbcService;
	
	public List<SellOrder> getSellOrderList() throws Exception {
		List<SellOrder> list = new ArrayList<SellOrder>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_sell_order";
			rs = stmt.executeQuery(sql);
			SellOrder order;
			while (rs.next()) {
				order = new SellOrder();
				order.setId(rs.getInt("id"));
				order.setFldDate(DateUtils.formatDate(rs.getDate("fld_date")));
				order.setFldHandler(rs.getString("fld_handler"));
				order.setFldCustomer(rs.getString("fld_customer"));
				order.setFldPrice(rs.getFloat("fld_price"));
				order.setFldVoucher(rs.getFloat("fld_voucher"));
				order.setFldVipPrice(rs.getFloat("fld_vip_price"));
				order.setFldVipVoucher(rs.getFloat("fld_vip_voucher"));
				order.setFldDiffPrice(rs.getFloat("fld_diff_price"));
				order.setFldDiffVoucher(rs.getFloat("fld_diff_voucher"));
				order.setFldRemark(rs.getString("fld_remark"));
				list.add(order);
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public void addNewSellOrder(SellOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_sell_order(fld_date, fld_handler, fld_customer, fld_remark, fld_price, fld_voucher, fld_vip_price, fld_vip_voucher, fld_diff_price, fld_diff_voucher)")
			.append(" values('").append(order.getFldDate())
			.append("', '").append(order.getFldHandler())
			.append("', '").append(order.getFldCustomer())
			.append("', '").append(order.getFldRemark())
			.append("', 0, 0, 0, 0, 0, 0)");
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void updateSellOrder(SellOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_sell_order set fld_date='").append(order.getFldDate())
			.append("', fld_handler='").append(order.getFldHandler())
			.append("', fld_customer='").append(order.getFldCustomer())
			.append("', fld_remark='").append(order.getFldRemark())
			.append("' where id=").append(order.getId());
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public List<SellDetail> getSellDetailList(Integer orderId) throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		List<SellDetail> list = new ArrayList<SellDetail>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_sell_detail where fld_order=" + orderId;
			rs = stmt.executeQuery(sql);
			SellDetail detail;
			int productId;
			Product product;
			while (rs.next()) {
				detail = new SellDetail();
				productId = rs.getInt("fld_product");
				detail.setId(rs.getInt("id"));
				detail.setFldOrder(rs.getInt("fld_order"));
				detail.setFldProduct(productId);
				detail.setFldCount(rs.getInt("fld_count"));
				detail.setFldPrice(rs.getFloat("fld_price"));
				detail.setFldVoucher(rs.getFloat("fld_voucher"));
				detail.setFldVipPrice(rs.getFloat("fld_vip_price"));
				detail.setFldVipVoucher(rs.getFloat("fld_vip_voucher"));
				product = map.get(Integer.valueOf(productId));
				if (product != null) {
					detail.setFldProductName(product.getFldName());
					detail.setFldProductSpec(product.getFldSpec());
				}
				list.add(detail);
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public void addNewSellOrder(SellDetail detail) throws Exception {
		StringBuilder sb = new StringBuilder();
		int orderId, count;
		float price, voucher, vipPrice, vipVoucher;
		orderId = detail.getFldOrder();
		count = detail.getFldCount();
		price = detail.getFldPrice();
		voucher = detail.getFldVoucher();
		vipPrice = detail.getFldVipPrice();
		vipVoucher = detail.getFldVipVoucher();
		sb.append("insert into tbl_sell_detail(fld_order, fld_product, fld_count, fld_price, fld_voucher, fld_vip_price, fld_vip_voucher)")
			.append(" values(").append(orderId)
			.append(", ").append(detail.getFldProduct())
			.append(", ").append(count)
			.append(", ").append(price)
			.append(", ").append(voucher)
			.append(", ").append(vipPrice)
			.append(", ").append(vipVoucher)
			.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
		
		// update order
		this.updateMoneyOfSellOrder(detail);
	}

	private void updateMoneyOfSellOrder(SellDetail detail) throws Exception {
		int orderId = detail.getFldOrder();
		float price = -1, voucher = -1, vipPrice = -1, vipVoucher = -1;
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select sum(fld_count * fld_price) as fld_total_price, sum(fld_count * fld_voucher) as fld_total_voucher, "
					+ "sum(fld_count * fld_vip_price) as fld_total_vip_price, sum(fld_count * fld_vip_voucher) as fld_total_vip_voucher"
					+ " from tbl_sell_detail where fld_order=" + orderId;
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				price = rs.getFloat("fld_total_price");
				voucher = rs.getFloat("fld_total_voucher");
				vipPrice = rs.getFloat("fld_total_vip_price");
				vipVoucher = rs.getFloat("fld_total_vip_voucher");
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		
		if (price >= 0f && voucher >= 0f && vipPrice >= 0f && vipVoucher >= 0f) {
			StringBuilder sb = new StringBuilder();
			sb.append("update tbl_sell_order set fld_price=").append(price)
				.append(", fld_voucher=").append(voucher)
				.append(", fld_vip_price=").append(vipPrice)
				.append(", fld_vip_voucher=").append(vipVoucher)
				.append(", fld_diff_price=").append(price - vipPrice)
				.append(", fld_diff_voucher=").append(voucher - vipVoucher)
				.append(" where id=").append(orderId);
			String sql = sb.toString();
			System.out.println(sql);
			jdbcService.executeSql(sql);
		}
	}

	public void updateSellDetail(SellDetail detail) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_sell_detail set fld_product=").append(detail.getFldProduct())
			.append(", fld_count=").append(detail.getFldCount())
			.append(", fld_price=").append(detail.getFldPrice())
			.append(", fld_voucher=").append(detail.getFldVoucher())
			.append(", fld_vip_price=").append(detail.getFldVipPrice())
			.append(", fld_vip_voucher=").append(detail.getFldVipVoucher())
			.append(" where id=").append(detail.getId());
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
		
		// update order
		this.updateMoneyOfSellOrder(detail);
	}

	public void deleteSellDetail(SellDetail detail) throws Exception {
		String sql = "delete from tbl_sell_detail where id=" + detail.getId();
		jdbcService.executeSql(sql);
		
		// update order
		this.updateMoneyOfSellOrder(detail);
	}

	public void deleteSellOrder(Integer orderId) throws Exception {
		String sql = "delete from tbl_sell_detail where fld_order=" + orderId;
		jdbcService.executeSql(sql);
		sql = "delete from tbl_sell_order where id=" + orderId;
		jdbcService.executeSql(sql);
	}

	public void exportSellDetail(HttpServletResponse resp, Integer orderId) throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		List<JSONObject> datas = new ArrayList<JSONObject>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		SellOrder order = new SellOrder();
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_sell_order where id=" + orderId;
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				order.setId(rs.getInt("id"));
				order.setFldDate(DateUtils.formatDate(rs.getDate("fld_date")));
				order.setFldCustomer(rs.getString("fld_customer"));
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		float totalPrice = 0, totalVoucher = 0;
		JSONObject data;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_sell_detail where fld_order=" + orderId;
			rs = stmt.executeQuery(sql);
			int productId, count;
			float price, voucher;
			Product product;
			while (rs.next()) {
				productId = rs.getInt("fld_product");
				count = rs.getInt("fld_count");
				price = rs.getFloat("fld_price");
				voucher = rs.getFloat("fld_voucher");
				product = map.get(Integer.valueOf(productId));
				data = new JSONObject();
				if (product != null) {
					data.put("fldName", product.getFldName());
					data.put("fldSpec", product.getFldSpec());
				}
				data.put("fldCount", count);
				data.put("fldPrice", price);
				data.put("fldVoucher", voucher);
				data.put("fldTotalPrice", count * price);
				data.put("fldTotalVoucher", count * voucher);
				datas.add(data);
				totalPrice += count * price;
				totalVoucher += count * voucher;
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		data = new JSONObject();
		data.put("fldName", "合计");
		data.put("fldSpec", "");
		data.put("fldCount", "");
		data.put("fldPrice", "");
		data.put("fldVoucher", "");
		data.put("fldTotalPrice", Math.round(totalPrice * 100f) / 100f);
		data.put("fldTotalVoucher", Math.round(totalVoucher * 100f) / 100f);
		datas.add(data);
		
		List<JSONObject> extraData = new ArrayList<JSONObject>();
		data = new JSONObject();
		data.put("row", 0);
		data.put("col", 1);
		data.put("value", orderId);
		extraData.add(data);
		data = new JSONObject();
		data.put("row", 0);
		data.put("col", 3);
		data.put("value", order.getFldCustomer());
		extraData.add(data);
		data = new JSONObject();
		data.put("row", 0);
		data.put("col", 5);
		data.put("value", order.getFldDate());
		extraData.add(data);
		
		String[] keys = new String[] {"fldName", "fldSpec", "fldCount", "fldPrice", "fldVoucher", "fldTotalPrice", "fldTotalVoucher"};
		List<JSONObject> headers = new ArrayList<JSONObject>();
		for (String s : keys) {
			data = new JSONObject();
			data.put("key", s);
			headers.add(data);
		}
		
		String fileName = URLEncoder.encode("产品销售明细_" + order.getFldCustomer() + "_", "utf-8");
		Calendar cal = Calendar.getInstance();
		fileName += DateUtils.formatDate(cal.getTime(), DateUtils.DATE_FORMAT_NUMBER);
		resp.setHeader("content-disposition", "attachment;filename=" + fileName + ".xlsx;filename*=utf-8''" + fileName + ".xlsx");

		String fileUrl = Thread.currentThread().getContextClassLoader().getResource("sellTemplate.xlsx").getPath().substring(1);
		ExcelUtilPoi.exportExcelFromJsonData(resp.getOutputStream(), 3, headers, datas, extraData, fileUrl);
	}

}
