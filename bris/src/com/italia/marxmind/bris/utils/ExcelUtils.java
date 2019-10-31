package com.italia.marxmind.bris.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author Mark Italia
 * @version: 1.0
 * @since 09/18/2018
 *
 */

public class ExcelUtils {

	//Using library commons-collections4-4.2
	
	/*public static void main(String[] args) {
		try {
		
			File file = new File("C://bris//xxx.xlsx");
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file)); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;
			FileInputStream fin = new FileInputStream(file);
			XSSFWorkbook wbx = new XSSFWorkbook(fin);
			XSSFSheet sheetx = wbx.getSheetAt(0);
			XSSFRow rowx;
			XSSFCell cellx;
			
			//int rows; // No of rows
		    //rows = sheetx.getPhysicalNumberOfRows();

		    int cols = 0; // No of columns
		    int tmp = 0;
		    
		    Iterator rows = sheetx.rowIterator();
		    while (rows.hasNext())
	        {
	            rowx=(XSSFRow) rows.next();
	            Iterator cells = rowx.cellIterator();
	            while (cells.hasNext())
	            {
	                cellx=(XSSFCell) cells.next();
	                
	                if(cellx.getCellTypeEnum()==CellType.STRING) {
	                	System.out.print(cellx.getStringCellValue()+" ");
	                }else if(cellx.getCellTypeEnum()==CellType.NUMERIC) {
	                	System.out.print(cellx.getNumericCellValue()+" ");
	                }else {
	                	//U Can Handel Boolean, Formula, Errors
	                }
	                
	            }
	            System.out.println();
	        }
		    
		
			
		    fin.close();
		    
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
}
