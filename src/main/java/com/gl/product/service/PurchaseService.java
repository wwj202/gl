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
	private JdbcService jdbcService;
	
	@Autowired
	private ProductService productService;
	
	public List<PurchaseOrder> getPurchaseOrderList() throws Exception {
		List<PurchaseOrder> list = new ArrayList<PurchaseOrder>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
			stmt = conn.createStatement();
			String sql = "select * from tbl_purchase_order";
			rs = stmt.executeQuery(sql);
			PurchaseOrder order;
			while (rs.next()) {
				order = new PurchaseOrder();
				order.setId(rs.getInt("id"));
				order.setFldDate(DateUtils.formatDate(rs.getDate("fld_date")));
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
			jdbcService.closeConn(rs, stmt, conn);
		}
		return list;
	}

	public void addNewPurchaseOrder(PurchaseOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tbl_purchase_order(fld_date, fld_handler, fld_remark, fld_price, fld_voucher, fld_vip_price, fld_vip_voucher, fld_diff_price, fld_diff_voucher)")
			.append(" values('").append(order.getFldDate())
			.append("', '").append(order.getFldHandler())
			.append("', '").append(order.getFldRemark())
			.append("', 0, 0, 0, 0, 0, 0)");
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public void updatePurchaseOrder(PurchaseOrder order) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_purchase_order set fld_date='").append(order.getFldDate())
			.append("', fld_handler='").append(order.getFldHandler())
			.append("', fld_remark='").append(order.getFldRemark())
			.append("' where id=").append(order.getId());
		String sql = sb.toString();
		System.out.println(sql);
		jdbcService.executeSql(sql);
	}

	public List<PurchaseDetail> getPurchaseDetailList(Integer orderId) throws Exception {
		Map<Integer, Product> map = productService.getProductMap();
		List<PurchaseDetail> list = new ArrayList<PurchaseDetail>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = jdbcService.getConn();
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

	public void addNewPurchaseOrder(PurchaseDetail detail) throws Exception {
		StringBuilder sb = new StringBuilder();
		int orderId, count;
		float price, voucher, vipPrice, vipVoucher;
		orderId = detail.getFldOrder();
		count = detail.getFldCount();
		price = detail.getFldPrice();
		voucher = detail.getFldVoucher();
		vipPrice = detail.getFldVipPrice();
		vipVoucher = detail.getFldVipVoucher();
		sb.append("insert into tbl_purchase_detail(fld_order, fld_product, fld_count, fld_price, fld_voucher, fld_vip_price, fld_vip_voucher)")
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
		this.updateMoneyOfPurchaseOrder(detail);
	}

	private void updateMoneyOfPurchaseOrder(PurchaseDetail detail) throws Exception {
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
					+ " from tbl_purchase_detail where fld_order=" + orderId;
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
			sb.append("update tbl_purchase_order set fld_price=").append(price)
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

	public void updatePurchaseDetail(PurchaseDetail detail) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update tbl_purchase_detail set fld_product=").append(detail.getFldProduct())
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
		this.updateMoneyOfPurchaseOrder(detail);
	}

	public void deletePurchaseDetail(PurchaseDetail detail) throws Exception {
		String sql = "delete from tbl_purchase_detail where id=" + detail.getId();
		jdbcService.executeSql(sql);
		
		// update order
		this.updateMoneyOfPurchaseOrder(detail);
	}

}
