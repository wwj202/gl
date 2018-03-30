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
import com.gl.product.entity.Series;

@Service
public class ProductService {
	
	@Autowired
	private JdbcService jdbcService;
	
	public List<Product> getProductList() throws Exception {
		Map<Integer, String> map = this.getSeriesMap();
		List<Product> list = new ArrayList<Product>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_product";
			rs = stmt.executeQuery(sql);
			int series;
			while (rs.next()) {
				series = rs.getInt("fld_series");
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
			jdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public Map<Integer, Product> getProductMap() throws Exception {
		Map<Integer, Product> map = new HashMap<Integer, Product>();
		List<Product> list = this.getProductList();
		for (Product product : list) {
			map.put(product.getId(), product);
		}
		return map;
	}

	public void addNewProduct(Product product) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_product(fld_name, fld_spec, fld_series, fld_price, fld_vip_price, fld_vip_voucher)")
			.append(" values('").append(product.getFldName())
			.append("', '").append(product.getFldSpec())
			.append("', ").append(product.getFldSeries())
			.append(", ").append(product.getFldPrice())
			.append(", ").append(product.getFldVipPrice())
			.append(", ").append(product.getFldVipVoucher())
			.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void updateProduct(Product product) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_product set fld_name='").append(product.getFldName())
			.append("', fld_spec='").append(product.getFldSpec())
			.append("', fld_series=").append(product.getFldSeries())
			.append(", fld_price=").append(product.getFldPrice())
			.append(", fld_vip_price=").append(product.getFldVipPrice())
			.append(", fld_vip_voucher=").append(product.getFldVipVoucher())
			.append(" where id=").append(product.getId());
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void deleteProduct(Integer id) throws Exception {
		String sql = "delete from tbl_product where id=" + id.intValue();
		jdbcService.executeSql(sql);
	}
	
	public List<Series> getSeriesList() throws Exception {
		List<Series> list = new ArrayList<Series>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_series";
			rs = stmt.executeQuery(sql);
			String name;
			int id;
			Series series;
			while (rs.next()) {
				series = new Series();
				id = rs.getInt("id");
				name = rs.getString("fld_name");
				series.setId(id);
				series.setFldName(name);
				list.add(series);
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

	private Map<Integer, String> getSeriesMap() throws Exception {
		Map<Integer, String> map = new HashMap<Integer, String>();
		List<Series> list = this.getSeriesList();
		for (Series series : list) {
			map.put(series.getId(), series.getFldName());
		}
		return map;
	}
}
