package com.gl.product.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.db.JdbcService;
import com.gl.product.entity.Product;

@Service
public class ProductService {

	@Autowired
	private JdbcService jdbcService;
	
	public List<Product> getProductList() throws Exception {
		List<Product> list = new ArrayList<Product>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_product";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setFldName(rs.getString("fld_name"));
				product.setFldSeries(rs.getString("fld_series"));
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
		jdbcService.executeSql(sql);
	}
}
