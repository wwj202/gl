package com.gl.product.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gl.db.JdbcService;
import com.gl.product.entity.Product;

@Service
public class StatisticsService {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private JdbcService jdbcService;

	public List<JSONObject> getSellRankingList() throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		List<JSONObject> list = new ArrayList<JSONObject>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select sum(fld_count) as fld_sell_count, fld_product from tbl_sell_detail"
					+ " group by fld_product order by sum(fld_count) desc";
			rs = stmt.executeQuery(sql);
			JSONObject rank;
			Product product;
			int productId;
			while (rs.next()) {
				productId = rs.getInt("fld_product");
				product = map.get(productId);
				rank = new JSONObject();
				rank.put("fldProduct", productId);
				rank.put("fldName", product.getFldName());
				rank.put("fldSeriesName", product.getFldSeriesName());
				rank.put("fldSpec", product.getFldSpec());
				rank.put("fldSellCount", rs.getInt("fld_sell_count"));
				list.add(rank);
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

	public Object getStockStatList() throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		Map<Integer, Integer> mapSell = new HashMap<Integer, Integer>();
		List<JSONObject> list = new ArrayList<JSONObject>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select sum(fld_count) as fld_sell_count, fld_product from tbl_sell_detail"
					+ " group by fld_product order by sum(fld_count) desc";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				mapSell.put(rs.getInt("fld_product"), rs.getInt("fld_sell_count"));
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select sum(fld_count) as fld_sell_count, fld_product from tbl_purchase_detail group by fld_product";
			rs = stmt.executeQuery(sql);
			JSONObject stock;
			Product product;
			Integer productId;
			int count;
			while (rs.next()) {
				productId = rs.getInt("fld_product");
				product = map.get(productId);
				stock = new JSONObject();
				stock.put("fldProduct", productId);
				stock.put("fldName", product.getFldName());
				stock.put("fldSeriesName", product.getFldSeriesName());
				stock.put("fldSpec", product.getFldSpec());
				count = rs.getInt("fld_sell_count");
				if (mapSell.containsKey(productId)) {
					count -= mapSell.get(productId).intValue();
				}
				stock.put("fldStockCount", count);
				if (count > 0) {
					list.add(stock);
				}
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

}
