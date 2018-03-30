package com.gl.util;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.gl.exception.BusinessException;

public class ExcelUtilPoi {
	
	@SuppressWarnings("resource")
	public static List<Map<String, String>> readExcelDataToMap(String fileFullName, String[] titles) throws IOException, BusinessException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		InputStream is = new FileInputStream(fileFullName);
		Workbook workbook;
		if (fileFullName.endsWith(".xls")) {
			workbook = new HSSFWorkbook(is);
		}
		else if (fileFullName.endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(is);
		}
		else {
			throw new BusinessException("Excel�ļ���ʽ����ȷ�� ��ȷ���ļ�����.xls����.xlsx��β��");
		}
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum() + 1;
		String content;
		Map<String, String> map;
		Row dataRow;
		Cell dataCell;
		for (int row = 0; row < rows; row++) {
			dataRow = sheet.getRow(row);
			if (dataRow != null) {
				map = new HashMap<String, String>();
				for (int col = 0; col < titles.length; col++) {
					dataCell = dataRow.getCell(col);
					if (dataCell == null) {
						content = "";
					}
					else {
						content = getStringCellValue(dataCell).trim();
					}
					map.put(titles[col], content);
				}
				list.add(map);
			}
		}
		
		return list;
	}

	@SuppressWarnings("deprecation")
	private static String getStringCellValue(Cell cell) {
		String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        }  
        //�����ֵ���String�������������1����1.0�����  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //�ж����ݵ�����  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //����  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //�ַ���  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //��ʽ  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_BLANK: //��ֵ   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //����  
                cellValue = "�Ƿ��ַ�";  
                break;  
            default:  
                cellValue = "δ֪����";  
                break;  
        }  
        return cellValue;
	}
	
	public static void exportExcelFromJsonData(OutputStream os, List<JSONObject> headers, List<JSONObject> datas) throws IOException {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		JSONObject header, data;
		Row dataRow = sheet.createRow(0);
		Cell cell;
		for (int i = 0; i < headers.size(); i++) {
			header = headers.get(i);
			cell = dataRow.createCell(i);
			cell.setCellValue(header.getString("name"));
		}
		int rows = datas.size();
		for (int row = 1; row <= rows; row++) {
			dataRow = sheet.createRow(row);
			data = datas.get(row - 1);
			for (int i = 0; i < headers.size(); i++) {
				header = headers.get(i);
				cell = dataRow.createCell(i);
				cell.setCellValue(data.getString(header.getString("key")));
			}
		}
		
		wb.write(os);
		os.flush();
		os.close();
		wb.close();
	}
	
	public static void exportExcelFromJsonData(OutputStream os, int startRow, List<JSONObject> headers, List<JSONObject> datas, String fileUrl) throws IOException {
		exportExcelFromJsonData(os, startRow, headers, datas, null, fileUrl);
	}
	
	public static void exportExcelFromJsonData(OutputStream os, int startRow, List<JSONObject> headers, List<JSONObject> datas, List<JSONObject> extraData, String fileUrl) throws IOException {
		InputStream is = new FileInputStream(fileUrl);
		Workbook wb = new XSSFWorkbook(is);
		is.close();
		Sheet sheet = wb.getSheetAt(0);
		JSONObject header, data;
		Row dataRow;
		Cell cell;
		if (extraData != null) {
			for (JSONObject obj : extraData) {
				dataRow = sheet.getRow(obj.getIntValue("row"));
				cell = dataRow.createCell(obj.getIntValue("col"));
				cell.setCellValue(obj.getString("value"));
			}
		}
		int rows = datas.size();
		for (int row = startRow; row < startRow + rows; row++) {
			dataRow = sheet.createRow(row);
			data = datas.get(row - startRow);
			for (int i = 0; i < headers.size(); i++) {
				header = headers.get(i);
				cell = dataRow.createCell(i);
				cell.setCellValue(data.getString(header.getString("key")));
			}
		}
		
		wb.write(os);
		os.flush();
		os.close();
		wb.close();
	}

}
