package com.gl.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class JdbcService {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public Connection getConn() throws ClassNotFoundException, SQLException {
		String dbFilePath = Thread.currentThread().getContextClassLoader().getResource("gl.mdb").getPath().substring(1);
		System.out.println(dbFilePath);
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		String url = "jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=" + dbFilePath;
		Connection conn = DriverManager.getConnection(url, "", "");
		return conn;
	}
	
	public void closeConn(ResultSet rs, Statement stmt, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void closeConn(Statement stmt, Connection conn) {
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			}
			catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public int executeSql(String sql) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			conn = this.getConn();
			stmt = conn.prepareStatement(sql);
			count = stmt.executeUpdate();
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			this.closeConn(stmt, conn);
		}
		return count;
	}

}
