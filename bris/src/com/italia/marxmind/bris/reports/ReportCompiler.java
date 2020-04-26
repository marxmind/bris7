package com.italia.marxmind.bris.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * 
 * @author mark italia
 * @since 02/22/2017
 * @version 1.0
 *
 */
public class ReportCompiler {

	/**
	 * 
	 * @param rptFileJrxml
	 * @param rptFileJasper
	 * @param rptLocation
	 * @return jasper file realpath
	 */
	public String compileReport(String rptFileJrxml, String rptFileJasper, String rptLocation){
		String jasperFile="";
		try{
		JasperCompileManager.compileReportToFile(rptLocation + rptFileJrxml + ".jrxml", rptLocation + rptFileJasper + ".jasper");
		jasperFile = rptLocation + rptFileJasper + ".jasper";
		}catch(JRException jre){
			jre.getMessage();
		}
		return jasperFile;
	}
	/**
	 * 
	 * @param reportLocation
	 * @param params
	 * @return JasperPrint object
	 */
	public JasperPrint report(String reportLocation, HashMap params){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport (reportLocation, params, new JREmptyDataSource());
		}catch(JRException jre){
			System.out.println("JasperPrint report()");
			jre.getMessage();
		}
		return jasperPrint;
	}
	/**
	 * 
	 * @param jasperReport
	 * @param params
	 * @param jrBeanColl
	 * @return JasperPrint object
	 */
	public JasperPrint report(String jasperReport, HashMap params,JRBeanCollectionDataSource jrBeanColl){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport(jasperReport, params,jrBeanColl);
		System.out.println("JasperPrint report()");
		}catch(Exception jre){
			
			jre.getMessage();
		}
		return jasperPrint;
	}
	
	/**
	 * 
	 * @param reportName is the actual file name of jrxml file
	 * @param reportLocation location of jrxml file
	 * @param params
	 * @param jrBeanColl collections
	 * 
	 * Generate pdf report
	 */
	public static void sendPdfReportToBrowser(String reportName, String reportLocation, HashMap<String,Object> params, JRBeanCollectionDataSource jrBeanColl) {
		System.out.println("creating pdf report....");
		try {
			  JasperCompileManager.compileReportToFile(reportLocation + reportName + ".jrxml", reportLocation + reportName + ".jasper");
			  String jrxmlFile 	= reportLocation + reportName + ".jasper";
			  FacesContext faces = FacesContext.getCurrentInstance();
			  ExternalContext context = faces.getExternalContext();
			  HttpServletResponse response = (HttpServletResponse)context.getResponse();
			  ServletOutputStream servletOutputStream = response.getOutputStream();
			  byte[] bytes = JasperRunManager.runReportToPdf(jrxmlFile, params, jrBeanColl);
			  response.setContentType("application/pdf");
			  response.setContentLength(bytes.length);
			  
			  servletOutputStream.write(bytes, 0, bytes.length);
		      servletOutputStream.flush();
		      servletOutputStream.close();
		      System.out.println("sending report to browser....");
		      faces.responseComplete();
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void exportToExcelFile(String reportName, String reportLocation, HashMap params,JRBeanCollectionDataSource jrBeanColl) {
		try {
		ReportCompiler com = new ReportCompiler();
		String xlsJasper = reportName+"_tmp";
		String jasperFile = com.compileReport(reportName, xlsJasper, reportLocation);
				
		JasperPrint jasPrint = com.report(jasperFile, params, jrBeanColl);
  	    JRXlsxExporter exporter = new JRXlsxExporter();
  	    exporter.setExporterInput(new SimpleExporterInput(jasPrint));
  	    File outputFile = new File(reportLocation + reportName +".xlsx");
  	    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
  	    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
  	    configuration.setDetectCellType(false);//Set configuration as you like it!!
  	    configuration.setCollapseRowSpan(true);
  	    exporter.setConfiguration(configuration);
  	    exporter.exportReport();
		}catch(JRException e) {}
	}
	
	
}

