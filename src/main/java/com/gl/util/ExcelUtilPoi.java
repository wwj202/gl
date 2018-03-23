package com.gl.util;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
			throw new BusinessException("Excel文件格式不正确， 请确保文件名以.xls或者.xlsx结尾！");
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
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;
	}

}
