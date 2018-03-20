package com.gl.product.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.db.JdbcService;
import com.gl.product.entity.Product;

@Service
public class ProductService {
	
	public List<Product> getProductList() throws Exception {
		Map<Integer, String> map = this.getSeriesMap();
		List<Product> list = new ArrayList<Product>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = JdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_product";
			rs = stmt.executeQuery(sql);
			String series;
			while (rs.next()) {
				series = rs.getString("fld_series");
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setFldName(rs.getString("fld_name"));
				product.setFldSeries(series);
				product.setFldSeriesName(map.get(Integer.valueOf(series)));
				product.setFldSpec(rs.getString("fld_spec"));
				product.setFldPrice(rs.getFloat("fld_price"));
				product.setFldVipPrice(rs.getFloat("fld_vip_price"));
				product.setFldVipVoucher(rs.getFloat("fld_vip_voucher"));
				list.add(product);
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			JdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public void addNewProduct(Product product) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_product(fld_name, fld_series, fld_spec, fld_price, fld_vip_price, fld_vip_voucher)")
			.append(" values('").append(product.getFldName())
			.append("', '").append(product.getFldSeries())
			.append("', '").append(product.getFldSpec())
			.append("', ").append(product.getFldPrice())
			.append(", ").append(product.getFldVipPrice())
			.append(", ").append(product.getFldVipVoucher())
			.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		JdbcService.executeSql(sql);
	}

	private Map<Integer, String> getSeriesMap() throws Exception {
		Map<Integer, String> map = new HashMap<Integer, String>();
		List<Product> list = new ArrayList<Product>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = JdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_series";
			rs = stmt.executeQuery(sql);
			String name;
			Integer id;
			while (rs.next()) {
				Product product = new Product();
				id = rs.getInt("id");
				name = rs.getString("fld_name");
				map.put(id, name);
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			JdbcService.closeConn(rs, stmt, conn);
		}
		return map;
	}
}
