package com.ui.utilities;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	public static Object[][] readExcelData(String excelfileName) {
		
		XSSFWorkbook wbook;
		String[][] data = null ;
		try {
			wbook = new XSSFWorkbook("./data/excelData/"+excelfileName+".xlsx");
			XSSFSheet sheet = wbook.getSheetAt(0);
			int rowCount = sheet.getLastRowNum();
			int colCount = sheet.getRow(0).getLastCellNum()-1;
			data = new String[rowCount][colCount];
			for (int row = 1; row <= rowCount; row++) {
				for (int cell = 1; cell <= colCount; cell++) {
					 CellType cellType = sheet.getRow(row).getCell(cell).getCellType();
					 String cellValue = null;
					 switch (cellType) {
						case STRING:
							cellValue=sheet.getRow(row).getCell(cell).getStringCellValue();
							break;
		                case NUMERIC:
		                	Long numeric = (long) sheet.getRow(row).getCell(cell).getNumericCellValue();
		                	String valueOf = String.valueOf(numeric);            	
		                	cellValue=valueOf;
							break;
		                case BLANK:
							break;
		                case _NONE:
		                	break;
						default:
							break;
						}
					 data[row-1][cell-1] =  cellValue;
					 
				} 
			}
			wbook.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return data;
	}
}
