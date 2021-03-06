package com.gl.subsidy.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.db.JdbcService;
import com.gl.subsidy.entity.Subsidy;
import com.gl.util.DateUtils;

@Service
public class SubsidyService {
	
	@Autowired
	private JdbcService jdbcService;
	
	public List<Subsidy> getSubsidyList() throws Exception {
		List<Subsidy> list = new ArrayList<Subsidy>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_subsidy";
			rs = stmt.executeQuery(sql);
			Subsidy order;
			while (rs.next()) {
				order = new Subsidy();
				order.setId(rs.getInt("id"));
				order.setFldDate(DateUtils.formatDate(rs.getDate("fld_date")));
				order.setFldCustomer(rs.getString("fld_customer"));
				order.setFldPrice(rs.getFloat("fld_price"));
				order.setFldCount(rs.getInt("fld_count"));
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

	public List<Subsidy> listSubsidyStatistics() throws Exception {
		List<Subsidy> list = new ArrayList<Subsidy>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_subsidy";
			rs = stmt.executeQuery(sql);
			Subsidy order;
			while (rs.next()) {
				order = new Subsidy();
				order.setId(rs.getInt("id"));
				order.setFldDate(DateUtils.formatDate(rs.getDate("fld_date")));
				order.setFldCustomer(rs.getString("fld_customer"));
				order.setFldPrice(rs.getFloat("fld_price"));
				order.setFldCount(rs.getInt("fld_count"));
				list.add(order);
			}
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			jdbcService.closeConn(rs, stmt, conn);
		}
		List<Subsidy> listResult = new ArrayList<Subsidy>();
		String oldMonth = "";
		int count = 0;
		float totalPrice = 0;
		Subsidy temp = null;
		for (Subsidy subsidy : list) {
			String month = subsidy.getFldDate().substring(0, 7);
			if (!oldMonth.equals(month)) {
				if (!oldMonth.isEmpty()) {
					temp.setFldDate(oldMonth);
					temp.setFldCount(count);
					temp.setFldTotalPrice(totalPrice);
					listResult.add(temp);
				}
				temp = new Subsidy();
				temp.setId(listResult.size() + 1);
				count = 0;
				totalPrice = 0;
				oldMonth = month;
			}
			count += subsidy.getFldCount();
			totalPrice += subsidy.getFldCount() * subsidy.getFldPrice();
		}
		temp.setFldDate(oldMonth);
		temp.setFldCount(count);
		temp.setFldTotalPrice(totalPrice);
		listResult.add(temp);
		return listResult;
	}

	public void addNewSubsidy(Subsidy order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_subsidy(fld_date, fld_customer, fld_price, fld_count)")
			.append(" values('").append(order.getFldDate())
			.append("', '").append(order.getFldCustomer())
			.append("', ").append(order.getFldPrice())
			.append(", ").append(order.getFldCount())
			.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void updateSubsidy(Subsidy order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_subsidy set fld_date='").append(order.getFldDate())
			.append("', fld_customer='").append(order.getFldCustomer())
			.append("', fld_count=").append(order.getFldCount())
			.append(", fld_price=").append(order.getFldPrice())
			.append(" where id=").append(order.getId());
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void deleteSubsidy(Integer subsidyId) throws Exception {
		String sql = "delete from tbl_subsidy where id=" + subsidyId;
		jdbcService.executeSql(sql);
	}

}
