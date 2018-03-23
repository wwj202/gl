package com.gl.util;

import java.util.Date;
import java.text.*;

public class DateUtils {
	
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	
	public static String formatDate(Date date) {
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}
	
	public static Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat(DATE_FORMAT).parse(date);
	}

}
