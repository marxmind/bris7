package com.italia.marxmind.bris.reports;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;

/**
 * 
 * @author Mark Italia
 * @since 11/18/2017
 * @version 1.0
 *
 */

@ManagedBean(name="exporter", eager=true)
@ViewScoped
public class DataExporter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 18767456L;
	private ExcelOptions excelOpt;
	private PDFOptions pdfOpt;
	
	public ExcelOptions getExcelOpt() {
		
		excelOpt = new ExcelOptions();
		excelOpt.setFacetBgColor("#050505");
		excelOpt.setFacetFontSize("12");
		excelOpt.setFacetFontColor("#f9fafa");
		excelOpt.setFacetFontStyle("BOLD");
		excelOpt.setCellFontColor("#050505");
		excelOpt.setCellFontSize("10");
		
		return excelOpt;
	}
	
	public void postProcessXLS(Object document) {
		HSSFWorkbook wb = (HSSFWorkbook) document;
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow header = sheet.getRow(0);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		//cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		//cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		//cellStyle.setFillBackgroundColor(HSSFColor.BLACK.index);
		cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
			header.getCell(i).setCellStyle(cellStyle);
		}
	}
	
	public PDFOptions getPdfOpt(){
		pdfOpt = new PDFOptions();
		pdfOpt.setFacetBgColor("#050505");
		pdfOpt.setFacetFontSize("10");
		pdfOpt.setFacetFontColor("#f9fafa");
		pdfOpt.setFacetFontStyle("BOLD");
		pdfOpt.setCellFontColor("#050505");
		pdfOpt.setCellFontSize("8");
		
		return pdfOpt;
	}
	
	public void preProcessPDF(Object document) throws IOException,
		BadElementException, DocumentException {
		Document pdf = (Document) document;
		pdf.open();
		pdf.setPageSize(PageSize.A4);
		pdf.addTitle("Record of Barangay Citizen");
		/*ExternalContext servletContext = FacesContext.getCurrentInstance().getExternalContext();
		String logo = servletContext.getRealPath("") + File.separator + "resources"+ File.separator +"gif" +
		File.separator + "logo.png";
		System.out.println("Logo path: " + logo);
		Image img = Image.getInstance(logo);
		pdf.add(img);*/
	}
	
}
