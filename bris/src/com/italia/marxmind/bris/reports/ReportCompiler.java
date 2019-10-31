package com.italia.marxmind.bris.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
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

