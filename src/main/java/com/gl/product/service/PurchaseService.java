package com.gl.product.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.db.JdbcService;
import com.gl.product.entity.Product;
import com.gl.product.entity.PurchaseDetail;
import com.gl.product.entity.PurchaseOrder;
import com.gl.util.DateUtils;

@Service
public class PurchaseService {
	
	@Autowired
	private ProductService productService;
	
	public List<PurchaseOrder> getPurchaseOrderList() throws Exception {
		List<PurchaseOrder> list = new ArrayList<PurchaseOrder>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = JdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_purchase_order";
			rs = stmt.executeQuery(sql);
			PurchaseOrder order;
			while (rs.next()) {
				order = new PurchaseOrder();
				order.setId(rs.getInt("id"));
				order.setFldDate(rs.getDate("fld_date"));
				order.setFldHandler(rs.getString("fld_handler"));
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
			JdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public void addNewPurchaseOrder(PurchaseOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_purchase_order(fld_date, fld_handler, fld_remark, fld_price, fld_voucher, fld_vip_price, fld_vip_voucher, fld_diff_price, fld_diff_voucher)")
			.append(" values('").append(DateUtils.formatDate(order.getFldDate()))
			.append("', '").append(order.getFldHandler())
			.append("', '").append(order.getFldRemark())
			.append("', 0, 0, 0, 0, 0, 0)");
		String sql = sb.toString();
		System.out.println(sql);
		JdbcService.executeSql(sql);
	}

	public void updatePurchaseOrder(PurchaseOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_purchase_order set fld_date='").append(DateUtils.formatDate(order.getFldDate()))
			.append("', fld_handler='").append(order.getFldHandler())
			.append("', fld_remark='").append(order.getFldRemark())
			.append("' where id=").append(order.getId());
		String sql = sb.toString();
		System.out.println(sql);
		JdbcService.executeSql(sql);
	}

	public List<PurchaseDetail> getPurchaseDetailList(Integer orderId) throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		List<PurchaseDetail> list = new ArrayList<PurchaseDetail>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = JdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_purchase_detail where fld_order=" + orderId;
			rs = stmt.executeQuery(sql);
			PurchaseDetail detail;
			int productId;
			Product product;
			while (rs.next()) {
				detail = new PurchaseDetail();
				productId = rs.getInt("fld_product");
				detail.setId(rs.getInt("id"));
				detail.setFldOrder(rs.getInt("fld_order"));
				detail.setFldProduct(productId);
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
			JdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

}
