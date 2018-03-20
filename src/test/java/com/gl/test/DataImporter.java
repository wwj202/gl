package com.gl.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gl.db.JdbcService;
import com.gl.product.entity.Product;
import com.gl.util.ExcelUtilPoi;

public class DataImporter {
	
	public static void main(String[] args) {
		try {
			//importSeries();
			importProducts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void importProducts() throws Exception {
		Map<String, Integer> mapSeries = getSeriesMap();
		String fileName = "D:/gl/绿叶产品清单20180307.xlsx";
		
		String [] titles = new String[] {"series", "name", "spec", "price", "vipPrice", "vipVoucher"};
		List<Map<String, String>> list = ExcelUtilPoi.readExcelDataToMap(fileName, titles);
		String series, name, spec;
		float price, vipPrice, vipVoucher;
		int seriesId;
		for (Map<String, String> map : list) {
			series = map.get("series");
			name = map.get("name");
			spec = map.get("spec");
			price = Float.parseFloat(map.get("price"));
			vipPrice = Float.parseFloat(map.get("vipPrice"));
			vipVoucher = Float.parseFloat(map.get("vipVoucher"));
			seriesId = mapSeries.get(series);
			insertProduct(seriesId, name, spec, price, vipPrice, vipVoucher);
		}
		System.out.println(fileName);
	}

	private static void insertProduct(int seriesId, String name, String spec,
			float price, float vipPrice, float vipVoucher) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_product(fld_name, fld_series, fld_spec, fld_price, fld_vip_price, fld_vip_voucher)")
			.append(" values('").append(name)
			.append("', '").append(seriesId)
			.append("', '").append(spec)
			.append("', ").append(price)
			.append(", ").append(vipPrice)
			.append(", ").append(vipVoucher)
			.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		JdbcService.executeSql(sql);
		
	}

	private static Map<String, Integer> getSeriesMap() throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
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
				map.put(name, id);
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

	private static void importSeries() throws Exception {
		String fileName = "D:/gl/绿叶产品清单20180307.xlsx";
		String [] titles = new String[] {"series"};
		List<Map<String, String>> list = ExcelUtilPoi.readExcelDataToMap(fileName, titles);
		Map<String, String> mapSeries = new HashMap<String, String>();
		String series;
		for (Map<String, String> map : list) {
			series = map.get("series");
			if ("产品系列".equals(series)) {
				continue;
			}
			if (!mapSeries.containsKey(series)) {
				insertSeries(series);
				mapSeries.put(series, series);
			}
		}
	}

	private static void insertSeries(String series) throws Exception {
		String sql = "insert into tbl_series(fld_name) values('" + series + "')";
		JdbcService.executeSql(sql);
	}

}
