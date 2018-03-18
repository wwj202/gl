package com.gl.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class HttpUtils {

	public static void writeJsonString(HttpServletResponse resp, String jsonStr) {
		if (resp != null) {
			resp.setCharacterEncoding("utf-8");
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setContentType("application/json;charset=utf-8");
			try {
				PrintWriter pw = resp.getWriter();
				pw.write(jsonStr);
				pw.flush();
				pw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
