package com.italia.marxmind.bris.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.application.GlobalReportHandler;
import com.italia.marxmind.bris.application.GlobalReportHandler.GlobalReport;
import com.italia.marxmind.bris.controller.BankChequeRpt;
import com.italia.marxmind.bris.controller.BankChequeTrans;
import com.italia.marxmind.bris.controller.Chequedtls;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.NumberToWords;
import com.italia.marxmind.bris.controller.Transmittal;
import com.italia.marxmind.bris.controller.TransmittalRpt;
import com.italia.marxmind.bris.controller.Words;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.qrcode.QRCode;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Mark Italia
 * @since 08/02/2018
 * @version 1.0
 *
 */
@ManagedBean(name="tranBean", eager=true)
@ViewScoped
public class TransmittalBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16787894423L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	//private Date dateTrans;
	private List months;
	private int monthId;
	private List<TransmittalRpt> checkSelected;
	private double amount;
	private List<TransmittalRpt> transmittals = Collections.synchronizedList(new ArrayList<TransmittalRpt>());
	private int year;
	
	private String rcdDate1;
	private String rcdDate2;
	private String rcdDate3;
	private String rcdNo1;
	private String rcdNo2;
	private String rcdNo3;
	private String rcdAmount1;
	private String rcdAmount2;
	private String rcdAmount3;
	
	private String otherDate1;
	private String otherDate2;
	private String otherDate3;
	private String otherTypeReport1;
	private String otherTypeReport2;
	private String otherTypeReport3;
	
	private Transmittal transData;
	private List<Transmittal> transactions = Collections.synchronizedList(new ArrayList<Transmittal>());
	
	@PostConstruct
	public void init() {
		loadMonthCheques();
		loadTransactions();
	}
	
	private void loadTransactions() {
		transactions = Collections.synchronizedList(new ArrayList<Transmittal>());
		String sql = "";
		String[] params = new String[0];
		
		sql = " ORDER BY transid DESC limit 10";
		
		transactions = Transmittal.retrieve(sql, params);
		
	}
	
	public void deleteRow(Transmittal tr) {
		tr.delete();
		loadTransactions();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clickItem(Transmittal tr) {
		
		//empty the fields
		clearNew();
		
		setTransData(tr);
		
		String[] date = tr.getDateTrans().split("-");
		int month = Integer.valueOf(date[1]);
		int year = Integer.valueOf(date[0]);
		
		setMonthId(month);
		setYear(year);
		
		loadMonthCheques();
		
		List<TransmittalRpt> trans = Collections.synchronizedList(new ArrayList<TransmittalRpt>());
		if(tr.getCheckNos()!=null && !tr.getCheckNos().isEmpty() && tr.getCheckNos().contains("<:>")) {
			String[] chk = tr.getCheckNos().split("<:>");
			//filter check no
			for(String chkNo : chk) {
				for(TransmittalRpt t : getTransmittals()) {
					if(chkNo.equalsIgnoreCase(t.getCheckNumber())) {
						trans.add(t);
					}
				}
			}
			
		}else {
			for(TransmittalRpt t : getTransmittals()) {
				if(tr.getCheckNos().equalsIgnoreCase(t.getCheckNumber())) {
					trans.add(t);
				}
			}
		}
		transmittals = trans;
		setCheckSelected(transmittals);
		if(tr.getRcdsContent()!=null && !tr.getRcdsContent().isEmpty() && tr.getRcdsContent().contains("<a>")) {
			String[] sp = tr.getRcdsContent().split("<a>");
			int size = sp.length;
			for(int cnt=0; cnt<size; cnt++) {
				if(cnt==0) {
					String[] rcd = sp[cnt].split("<:>");
					setRcdDate1(rcd[0]);
					setRcdNo1(rcd[1]);
					setRcdAmount1(rcd[2]);
				}
				if(cnt==1) {
					String[] rcd = sp[cnt].split("<:>");
					setRcdDate2(rcd[0]);
					setRcdNo2(rcd[1]);
					setRcdAmount2(rcd[2]);
				}
				if(cnt==2) {
					String[] rcd = sp[cnt].split("<:>");
					setRcdDate3(rcd[0]);
					setRcdNo3(rcd[1]);
					setRcdAmount3(rcd[2]);
				}
			}
		}else {
			if(tr.getRcdsContent()!=null && !tr.getRcdsContent().isEmpty()) {
				String[] rcd = tr.getRcdsContent().split("<:>");
				setRcdDate1(rcd[0]);
				setRcdNo1(rcd[1]);
				setRcdAmount1(rcd[2]);
			}
		}
		
		
		if(tr.getRptsContent()!=null && !tr.getRptsContent().isEmpty() && tr.getRptsContent().contains("<a>")) {
			String[] sp = tr.getRptsContent().split("<a>");
			int size = sp.length;
			for(int cnt=0; cnt<size; cnt++) {
				if(cnt==0) {
					String[] rcd = sp[cnt].split("<:>");
					setOtherDate1(rcd[0]);
					setOtherTypeReport1(rcd[1]);
				}
				if(cnt==1) {
					String[] rcd = sp[cnt].split("<:>");
					setOtherDate2(rcd[0]);
					setOtherTypeReport2(rcd[1]);
				}
				if(cnt==2) {
					String[] rcd = sp[cnt].split("<:>");
					setOtherDate3(rcd[0]);
					setOtherTypeReport3(rcd[1]);
				}
			}
		}else {
			if(tr.getRptsContent()!=null && !tr.getRptsContent().isEmpty()) {
				String[] rcd = tr.getRptsContent().split("<:>");
				setOtherDate1(rcd[0]);
				setOtherTypeReport1(rcd[1]);
			}
		}
		
	}
	
	public void saveTrans() {
		if(getCheckSelected()!=null && getCheckSelected().size()>0) {
			Employee capitan = Employee.retrievePosition(Positions.CAPTAIN.getId());
			Employee treas = Employee.retrievePosition(Positions.TREASURER.getId());
			
			Transmittal tr = new Transmittal();
			
			if(getTransData()!=null) {
				tr = getTransData();
			}else {
				tr.setIsActive(1);
			}
			
			//tr.setDateTrans(DateUtils.convertDate(getDateTrans(), "yyyy-MM-dd"));
			tr.setCaptain(capitan);
			tr.setTreasurer(treas);
			tr.setUserDtls(Login.getUserLogin().getUserDtls());
			String checksNo="";
			String rcds = "";
			String rptContent = "";
			
			int cnt = 1;
			double amount =0d;
			String printedDate = DateUtils.getCurrentDateYYYYMMDD();
			for(TransmittalRpt rpt : getCheckSelected()) {
				amount += Double.valueOf(rpt.getAmount().replace(",", ""));
				if(cnt==1) {
					checksNo = rpt.getCheckNumber();
				}else {
					checksNo += "<:>"+ rpt.getCheckNumber();
				}
				printedDate = rpt.getCheckDate();
				cnt++;
			}
			
			
			tr.setDateTrans(printedDate);
			tr.setTotalAmount(amount);
			tr.setCheckNos(checksNo);
			
			boolean hasValue=false;
			if(getRcdDate1()!=null && !getRcdDate1().isEmpty()){
					rcds += getRcdDate1() + "<:>" + (getRcdNo1()!=null? getRcdNo1() : ".") + "<:>" + (getRcdAmount1()!=null? getRcdAmount1() : ".");
					hasValue = true;		
			}
			if(getRcdDate2()!=null && !getRcdDate2().isEmpty()) {
				if(hasValue) {
					rcds +="<a>"+ getRcdDate2() + "<:>" + (getRcdNo2()!=null? getRcdNo2() : ".") + "<:>" + (getRcdAmount2()!=null? getRcdAmount2() : ".");
					hasValue = true;
				}else {
					rcds = getRcdDate2() + "<:>" + (getRcdNo2()!=null? getRcdNo2() : ".") + "<:>" + (getRcdAmount2()!=null? getRcdAmount2() : ".");
					hasValue = true;
				}
			}
			if(getRcdDate3()!=null && !getRcdDate3().isEmpty()) {
				if(hasValue) {
					rcds +="<a>"+ getRcdDate3() + "<:>" + (getRcdNo3()!=null? getRcdNo3() : ".") + "<:>" + (getRcdAmount3()!=null? getRcdAmount3() : ".");
					hasValue = true;
				}else {
					rcds = getRcdDate3() + "<:>" + (getRcdNo3()!=null? getRcdNo3() : ".") + "<:>" + (getRcdAmount3()!=null? getRcdAmount3() : ".");
					hasValue = true;
				}
			}
			
			tr.setRcdsContent(rcds);
			
			hasValue = false;
			if(getOtherDate1()!=null && !getOtherDate1().isEmpty()) {
				rptContent += getOtherDate1() + "<:>" + (getOtherTypeReport1()!=null? getOtherTypeReport1() : ".");
				hasValue = true;		
			}
			if(getOtherDate2()!=null && !getOtherDate2().isEmpty()) {
				if(hasValue) {
					rptContent +="<a>"+ getOtherDate2() + "<:>" + (getOtherTypeReport2()!=null? getOtherTypeReport2() : ".");
					hasValue = true;
				}else {
					rptContent ="<a>"+ getOtherDate2() + "<:>" + (getOtherTypeReport2()!=null? getOtherTypeReport2() : ".");
					hasValue = true;
				}
			}
			if(getOtherDate3()!=null && !getOtherDate3().isEmpty()) {
				if(hasValue) {
					rptContent +="<a>"+ getOtherDate3() + "<:>" + (getOtherTypeReport3()!=null? getOtherTypeReport3() : ".");
					hasValue = true;
				}else {
					rptContent ="<a>"+ getOtherDate3() + "<:>" + (getOtherTypeReport3()!=null? getOtherTypeReport3() : ".");
					hasValue = true;
				}
			}
			tr.setRptsContent(rptContent);
			tr.save();
			loadTransactions();
			Application.addMessage(1, "Success", "Successfully saved.");
		}else {
			Application.addMessage(3, "Error", "Please check checks");
		}
	}
	
	public void clearNew() {
		//setDateTrans(null);
		setMonthId(DateUtils.getCurrentMonth());
		setCheckSelected(null);
		setAmount(0.00);
		setYear(DateUtils.getCurrentYear());
		
		setRcdDate1(null);
		setRcdDate2(null);
		setRcdDate3(null);
		setRcdNo1(null);
		setRcdNo2(null);
		setRcdNo3(null);
		setRcdAmount1(null);
		setRcdAmount2(null);
		setRcdAmount3(null);
		
		setOtherDate1(null);
		setOtherDate2(null);
		setOtherDate3(null);
		setOtherTypeReport1(null);
		setOtherTypeReport2(null);
		setOtherTypeReport3(null);
		
		setTransData(null);
		
		init();
	}
	
	public void loadMonthCheques() {
		transmittals = Collections.synchronizedList(new ArrayList<TransmittalRpt>());
		String sql = " AND bnk.bankrptisactive=1 AND (bnk.dateapplying>=? ANd bnk.dateapplying<=?)";
		String month = "";
		if(getMonthId()<=9) {
			month = "0"+getMonthId();
		}else {
			month = getMonthId()+"";
		}
		String[] params = new String[2];
		if(year==0) {
			year = DateUtils.getCurrentYear();
		}
		System.out.println("Year selected >> " + year);
		params[0] = year +"-" + month +"-01";
		params[1] = year +"-" + month +"-31";
		
		Map<String, BankChequeTrans> unsortCheck = Collections.synchronizedMap(new HashMap<String, BankChequeTrans>());
		
		for(BankChequeRpt pbc : BankChequeRpt.retrieve(sql, params)) {
			sql = " AND tran.bankisactivetrans=1 AND bnk.bankchkid="+pbc.getId() +" ORDER BY chk.checkno";
			for(BankChequeTrans chk : BankChequeTrans.retrieve(sql, new String[0])){
				unsortCheck.put(chk.getChequedtls().getCheckNo(), chk);
				amount += chk.getChequedtls().getAmount();
			}
		}
		
		Map<String, BankChequeTrans> sortCheck = new TreeMap<String, BankChequeTrans>(unsortCheck);
		for(BankChequeTrans chk : sortCheck.values()) {
			TransmittalRpt tran = new TransmittalRpt();
			
			tran.setId(chk.getChequedtls().getId());
			
			tran.setDvDate(chk.getChequedtls().getDateTrans());
			tran.setDvNumber(chk.getChequedtls().getDvNo());
			
			tran.setCheckDate(chk.getChequedtls().getDateTrans());
			tran.setCheckNumber(chk.getChequedtls().getCheckNo());
			
			tran.setPayee(chk.getChequedtls().getIssueTo());
			tran.setAmount(Currency.formatAmount(chk.getChequedtls().getAmount()));
			
			tran.setPbcDate(chk.getChequeRpt().getDateApplying());
			tran.setPbcNumber(chk.getChequeRpt().getPbcNo());
			transmittals.add(tran);
		}
		
		
	}
	
	public void printTransmittal() {
		Employee capitan = Employee.retrievePosition(Positions.CAPTAIN.getId());
		//Employee treasurer = Employee.retrievePosition(Positions.TREASURER.getId());
		String REPORT_NAME = GlobalReportHandler.report(GlobalReport.TRANSMITAL_LETTER);
		
		HashMap param = new HashMap();
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jasperFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(checkSelected);
		
		param.put("PARAM_BARANGAY", Words.getTagName("barangay-line").replace("<barangay>", BARANGAY).replace("BARANGAY", "Barangay"));
		param.put("PARAM_PROVINCE", Words.getTagName("province-line").replace("<province>", PROVINCE));
		param.put("PARAM_MUNICIPALITY", Words.getTagName("municipality-line").replace("<municipality>", MUNICIPALITY));
		
		param.put("PARAM_HEAD_DEPARTMENT", Words.getTagName("accounting-recepient"));
		param.put("PARAM_POSITION", Words.getTagName("accounting-position"));
		param.put("PARAM_MUNICIPALITY_ASSIGNED", Words.getTagName("accounting-municipal"));
		param.put("PARAM_PROVINCE_ASSIGNED", Words.getTagName("accounting-province"));
		
		String datePrinted = DateUtils.getCurrentDateYYYYMMDD();
		try {
		int sizeRpt = checkSelected.size();
		sizeRpt -= 1;
		datePrinted = checkSelected.get(sizeRpt).getCheckDate();
		}catch(Exception e ) {}
		param.put("PARAM_DATEAPPLYING", DateUtils.convertDateToMonthDayYear(datePrinted).toUpperCase());
		//param.put("PARAM_DATEAPPLYING", DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getDateTrans(), DateFormat.YYYY_MM_DD())).toUpperCase());
		
		param.put("PARAM_SIRMADAM", Words.getTagName("accounting-sirmadam"));
		
		String bodyletter = Words.getTagName("accounting-body-letter");
		bodyletter = bodyletter.replace("<barangay>", BARANGAY.toUpperCase());
		bodyletter = bodyletter.replace("<municipality>", MUNICIPALITY);
		bodyletter = bodyletter.replace("<province>", PROVINCE);
		bodyletter = bodyletter.replace("<month>", DateUtils.getMonthName(getMonthId()));
		bodyletter = bodyletter.replace("<year>", getYear()+"");
		param.put("PARAM_BODY_LETTER", bodyletter.replace("<monthcovered>", DateUtils.getMonthName(datePrinted.split("-")[1]) + " " + datePrinted.split("-")[0]));
		
		param.put("PARAM_TOTAL", "Grand Total: Php "+Currency.formatAmount(amount));
		String words = NumberToWords.changeToWords(Currency.formatAmount(amount).replace(",", ""));
		param.put("PARAM_INWORDS", "** "+ words.toUpperCase() +" **");
		
		String documentNote="";
		documentNote += "Brgy. Document\n";
		documentNote += "Series of " + getYear()+"\n";
		try{param.put("PARAM_DOCUMENT_NOTE", documentNote);}catch(NullPointerException e) {}
		try{param.put("PARAM_TAGLINE", Words.getTagName("tagline"));}catch(NullPointerException e) {}
		//background
		String backlogo = path + "documentbg-gen.png";
		try{File file = new File(backlogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_BACKGROUND", off);
		}catch(Exception e){}
		
		param.put("PARAM_DATE1", getRcdDate1());
		param.put("PARAM_DATE2", getRcdDate2());
		param.put("PARAM_DATE3", getRcdDate3());
		
		param.put("PARAM_NO1", getRcdNo1());
		param.put("PARAM_NO2", getRcdNo2());
		param.put("PARAM_NO3", getRcdNo3());
		
		param.put("PARAM_AMOUNT1", getRcdAmount1()!=null? Currency.formatAmount(getRcdAmount1()) : "");
		param.put("PARAM_AMOUNT2", getRcdAmount2()!=null? Currency.formatAmount(getRcdAmount2()) : "");
		param.put("PARAM_AMOUNT3", getRcdAmount3()!=null? Currency.formatAmount(getRcdAmount3()) : "");
		
		param.put("PARAM_ODATE1", getOtherDate1());
		param.put("PARAM_ODATE2", getOtherDate2());
		param.put("PARAM_ODATE3", getOtherDate3());
		
		param.put("PARAM_REPORT_TYPE1", getOtherTypeReport1());
		param.put("PARAM_REPORT_TYPE2", getOtherTypeReport2());
		param.put("PARAM_REPORT_TYPE3", getOtherTypeReport3());
		
		/**
		 * PARAM_RECEPIENT
		 * PARAM_BANKNAME
		 * PARAM_BANKLOC
		 * PARAM_BANKPROV
		 * PARAM_PBCNO
		 * PARAM_DATEAPPLYING
		 * PARAM_CEDULANO
		 * PARAM_CEDULADATE
		 * PARAM_ISSUEDAT
		 * PARAM_BODY_LETTER
		 * PARAM_TOTAL
		 * PARAM_INWORDS
		 */
		
		param.put("PARAM_OFFICER_DAY", capitan.getFirstName().toUpperCase() + " " + capitan.getMiddleName().substring(0,1).toUpperCase() + ". " + capitan.getLastName().toUpperCase());
		param.put("PARAM_OFFICIAL_TITLE",capitan.getPosition().getName());
		
		Employee treas = Employee.retrievePosition(Positions.TREASURER.getId());
		param.put("PARAM_TREASURER",treas.getFirstName() + " " + treas.getLastName());
		
		
		//logo
		String officialLogo = path + "logo.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){}
		
		//officialseallakesebu
		String lakesebuofficialseal = path + "municipalseal.png";
		try{File file1 = new File(lakesebuofficialseal);
		FileInputStream off2 = new FileInputStream(file1);
		param.put("PARAM_LOGO_LAKESEBU", off2);
		}catch(Exception e){}
		
		//logo
		String logo = path + "barangaylogotrans.png";
		try{File file = new File(logo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_SEALTRANSPARENT", off);
		}catch(Exception e){}
		
		//authorization
		String bankcheque = path + "bankchequestitle.png";
		try{File file1 = new File(bankcheque);
		FileInputStream off1 = new FileInputStream(file1);
		param.put("PARAM_TITLE_DOC", off1);
		}catch(Exception e){}
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jasperFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    //create excel copy
	  	    HashMap params = new HashMap();
	  	          params = param;
	  	    JRBeanCollectionDataSource columnsData = new JRBeanCollectionDataSource(checkSelected);      
	  	    ReportCompiler.exportToExcelFile(REPORT_NAME, path, params, columnsData);
	  	    
	  		}catch(Exception e){e.printStackTrace();}
		
				try{
					System.out.println("REPORT_PATH:" + path + "REPORT_NAME: " + REPORT_NAME);
		  		 File file = new File(path, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	}
	
	public void printTransmittalNew(Transmittal trn) {
		Employee capitan = Employee.retrieve(trn.getCaptain().getId());
		
		String REPORT_NAME = GlobalReportHandler.report(GlobalReport.TRANSMITAL_LETTER);
		
		HashMap param = new HashMap();
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jasperFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		
		String[] date = trn.getDateTrans().split("-");
		setMonthId(Integer.valueOf(date[1]));
		setYear(Integer.valueOf(date[0]));
		
		List<TransmittalRpt> transRpt = Collections.synchronizedList(new ArrayList<TransmittalRpt>());
		String sql = " AND bnk.bankrptisactive=1 AND (bnk.dateapplying>=? ANd bnk.dateapplying<=?)";
		String month = "";
		if(getMonthId()<=9) {
			month = "0"+getMonthId();
		}else {
			month = getMonthId()+"";
		}
		String[] params = new String[2];
		if(year==0) {
			year = DateUtils.getCurrentYear();
		}
		System.out.println("Year selected >> " + year);
		params[0] = year +"-" + month +"-01";
		params[1] = year +"-" + month +"-31";
		
		Map<String, BankChequeTrans> unsortCheck = Collections.synchronizedMap(new HashMap<String, BankChequeTrans>());
		
		for(BankChequeRpt pbc : BankChequeRpt.retrieve(sql, params)) {
			sql = " AND tran.bankisactivetrans=1 AND bnk.bankchkid="+pbc.getId() +" ORDER BY chk.checkno";
			for(BankChequeTrans chk : BankChequeTrans.retrieve(sql, new String[0])){
				unsortCheck.put(chk.getChequedtls().getCheckNo(), chk);
			}
		}
		
		Map<String, BankChequeTrans> sortCheck = new TreeMap<String, BankChequeTrans>(unsortCheck);
		for(BankChequeTrans chk : sortCheck.values()) {
			TransmittalRpt tran = new TransmittalRpt();
			
			tran.setId(chk.getChequedtls().getId());
			
			tran.setDvDate(chk.getChequedtls().getDateTrans());
			tran.setDvNumber(chk.getChequedtls().getDvNo());
			
			tran.setCheckDate(chk.getChequedtls().getDateTrans());
			tran.setCheckNumber(chk.getChequedtls().getCheckNo());
			
			tran.setPayee(chk.getChequedtls().getIssueTo());
			tran.setAmount(Currency.formatAmount(chk.getChequedtls().getAmount()));
			
			tran.setPbcDate(chk.getChequeRpt().getDateApplying());
			tran.setPbcNumber(chk.getChequeRpt().getPbcNo());
			transRpt.add(tran);
		}
		
		List<TransmittalRpt> trans = Collections.synchronizedList(new ArrayList<TransmittalRpt>());
		if(trn.getCheckNos()!=null && !trn.getCheckNos().isEmpty() && trn.getCheckNos().contains("<:>")) {
			String[] chk = trn.getCheckNos().split("<:>");
			//filter check no
			for(String chkNo : chk) {
				for(TransmittalRpt t : transRpt) {
					if(chkNo.equalsIgnoreCase(t.getCheckNumber())) {
						trans.add(t);
					}
				}
			}
			
		}else {
			for(TransmittalRpt t : transRpt) {
				if(trn.getCheckNos().equalsIgnoreCase(t.getCheckNumber())) {
					trans.add(t);
				}
			}
		}
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(trans);
		
		if(trn.getRcdsContent()==null || trn.getRcdsContent().isEmpty()) {
			trn.setRcdsContent(".<:>.<:>0<a>.<:>.<:>0<a>.<:>.<:>0");
		}
		
		if(trn.getRptsContent()==null || trn.getRptsContent().isEmpty()) {
			trn.setRptsContent(".<:>.<a>.<:>.<a>.<:>.");
		}
		
		String[] rcds = {".",".","0",".",".","0",".",".","0"};
				try{rcds = trn.getRcdsContent().split("<a>");}catch(IndexOutOfBoundsException i) {}
		String[] rcd1 = {".",".","0"};  
				try{rcd1 = rcds[0].split("<:>");}catch(IndexOutOfBoundsException i) {}
		String[] rcd2 = {".",".","0"}; 
				try{rcd2 = rcds[1].split("<:>");}catch(IndexOutOfBoundsException i) {}
		String[] rcd3 = {".",".","0"}; 
				try{rcd3 = rcds[2].split("<:>");}catch(IndexOutOfBoundsException i) {}
		
		String[] rpts = {".",".",".",".",".","."};
				try{rpts = trn.getRptsContent().split("<a>");}catch(IndexOutOfBoundsException i) {}
		String[] rpt1 = {".","."}; 
				try{rpt1 = rpts[0].split("<:>");}catch(IndexOutOfBoundsException i) {}
		String[] rpt2 = {".","."};
				try{rpt2 = rpts[1].split("<:>");}catch(IndexOutOfBoundsException i) {}
		String[] rpt3 = {".","."}; 
				try{rpt3 = rpts[2].split("<:>");}catch(IndexOutOfBoundsException i) {}
		
		param.put("PARAM_BARANGAY", Words.getTagName("barangay-line").replace("<barangay>", BARANGAY).replace("BARANGAY", "Barangay"));
		param.put("PARAM_PROVINCE", Words.getTagName("province-line").replace("<province>", PROVINCE));
		param.put("PARAM_MUNICIPALITY", Words.getTagName("municipality-line").replace("<municipality>", MUNICIPALITY));
		
		param.put("PARAM_HEAD_DEPARTMENT", Words.getTagName("accounting-recepient"));
		param.put("PARAM_POSITION", Words.getTagName("accounting-position"));
		param.put("PARAM_MUNICIPALITY_ASSIGNED", Words.getTagName("accounting-municipal"));
		param.put("PARAM_PROVINCE_ASSIGNED", Words.getTagName("accounting-province"));
		
		param.put("PARAM_DATEAPPLYING", DateUtils.convertDateToMonthDayYear(trn.getDateTrans()).toUpperCase());
		
		param.put("PARAM_SIRMADAM", Words.getTagName("accounting-sirmadam"));
		
		String firstdate = trans.get(0).getCheckDate();
		firstdate = DateUtils.convertDateToMonthDayYear(firstdate);
		int lastsize = trans.size()-1;
		String lastdate = trans.get(lastsize).getCheckDate();
		lastdate = DateUtils.convertDateToMonthDayYear(lastdate);
		
		String covereddate = firstdate + "-" + lastdate;
		
		String bodyletter = Words.getTagName("accounting-body-letter");
		bodyletter = bodyletter.replace("<barangay>", BARANGAY.toUpperCase());
		bodyletter = bodyletter.replace("<municipality>", MUNICIPALITY);
		bodyletter = bodyletter.replace("<province>", PROVINCE);
		bodyletter = bodyletter.replace("<month>", DateUtils.getMonthName(Integer.valueOf(trn.getDateTrans().split("-")[1])));
		bodyletter = bodyletter.replace("<year>", trn.getDateTrans().split("-")[0]+"");
		bodyletter = bodyletter.replace("<monthcovered>", covereddate);
		param.put("PARAM_BODY_LETTER", bodyletter);
		
		double amount = trn.getTotalAmount();
		
		param.put("PARAM_TOTAL", "Grand Total: Php "+Currency.formatAmount(amount));
		String words = NumberToWords.changeToWords(Currency.formatAmount(amount).replace(",", ""));
		param.put("PARAM_INWORDS", "** "+ words.toUpperCase() +" **");
		
		String documentNote="";
		documentNote += "Brgy. Document\n";
		documentNote += "Series of " + trn.getDateTrans().split("-")[0]+"\n";
		try{param.put("PARAM_DOCUMENT_NOTE", documentNote);}catch(NullPointerException e) {}
		try{param.put("PARAM_TAGLINE", Words.getTagName("tagline"));}catch(NullPointerException e) {}
		//background
		String backlogo = path + "documentbg-gen.png";
		try{File file = new File(backlogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_BACKGROUND", off);
		}catch(Exception e){}
		
		param.put("PARAM_DATE1", rcd1[0]);
		param.put("PARAM_DATE2", rcd2[0]);
		param.put("PARAM_DATE3", rcd3[0]);
		
		param.put("PARAM_NO1", rcd1[1]);
		param.put("PARAM_NO2", rcd2[1]);
		param.put("PARAM_NO3", rcd3[1]);
		
		try {
		param.put("PARAM_AMOUNT1", rcd1[2].equalsIgnoreCase(".")? "" : Currency.formatAmount(rcd1[2]));
		param.put("PARAM_AMOUNT2", rcd2[2].equalsIgnoreCase(".")? "" : Currency.formatAmount(rcd2[2]));
		param.put("PARAM_AMOUNT3", rcd3[2].equalsIgnoreCase(".")? "" : Currency.formatAmount(rcd3[2]));
		}catch(Exception e) {e.printStackTrace();}
		
		param.put("PARAM_ODATE1", rpt1[0]);
		param.put("PARAM_ODATE2", rpt2[0]);
		param.put("PARAM_ODATE3", rpt3[0]);
		
		param.put("PARAM_REPORT_TYPE1", rpt1[1]);
		param.put("PARAM_REPORT_TYPE2", rpt2[1]);
		param.put("PARAM_REPORT_TYPE3", rpt3[1]);
		
		/**
		 * PARAM_RECEPIENT
		 * PARAM_BANKNAME
		 * PARAM_BANKLOC
		 * PARAM_BANKPROV
		 * PARAM_PBCNO
		 * PARAM_DATEAPPLYING
		 * PARAM_CEDULANO
		 * PARAM_CEDULADATE
		 * PARAM_ISSUEDAT
		 * PARAM_BODY_LETTER
		 * PARAM_TOTAL
		 * PARAM_INWORDS
		 */
		
		param.put("PARAM_OFFICER_DAY", capitan.getFirstName().toUpperCase() + " " + capitan.getMiddleName().substring(0,1).toUpperCase() + ". " + capitan.getLastName().toUpperCase());
		param.put("PARAM_OFFICIAL_TITLE",capitan.getPosition().getName());
		
		Employee treas = Employee.retrieve(trn.getTreasurer().getId());
		param.put("PARAM_TREASURER",treas.getFirstName() + " " + treas.getLastName());
		
		
		//logo
		String officialLogo = path + "logo.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){}
		
		//officialseallakesebu
		String lakesebuofficialseal = path + "municipalseal.png";
		try{File file1 = new File(lakesebuofficialseal);
		FileInputStream off2 = new FileInputStream(file1);
		param.put("PARAM_LOGO_LAKESEBU", off2);
		}catch(Exception e){}
		
		//logo
		String logo = path + "barangaylogotrans.png";
		try{File file = new File(logo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_SEALTRANSPARENT", off);
		}catch(Exception e){}
		
		//authorization
		String bankcheque = path + "bankchequestitle.png";
		try{File file1 = new File(bankcheque);
		FileInputStream off1 = new FileInputStream(file1);
		param.put("PARAM_TITLE_DOC", off1);
		}catch(Exception e){}
		
		FileInputStream qrPdf = null;
		try{
			
			
			File folder = new File(path + "qrcode" + File.separator);
			folder.mkdir();
			String content = "Date Report: " + DateUtils.convertDateToMonthDayYear(trn.getDateTrans()).toUpperCase() +"\n";
			content += "Period Covered: "+ covereddate +"\n";
			content += "Report: Transmittal\n";
			content += "Printed: "+ DateUtils.convertDateToMonthDayYear(trn.getDateTrans()).toUpperCase() +"\n";
			content += "Please report for violation of this document.\n";
			content += "Provider: MARXMIND I.T. SOLUTIONS";
			File pdf = QRCode.createQRCode(content, 200, 200, path + "qrcode" + File.separator, trn.getDateTrans()+"-trans");
			qrPdf = new FileInputStream(pdf);
			param.put("PARAM_QRCODE", qrPdf);
		}catch(Exception e){e.printStackTrace();}
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jasperFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    //create excel copy
	  	    //HashMap paramPrt = new HashMap();
	  	    //		paramPrt = param;
	  	    //JRBeanCollectionDataSource columnsData = new JRBeanCollectionDataSource(checkSelected);      
	  	    //ReportCompiler.exportToExcelFile(REPORT_NAME, path, paramPrt, columnsData);
	  	    
	  		}catch(Exception e){e.printStackTrace();}
		
				try{
					System.out.println("REPORT_PATH:" + path + "REPORT_NAME: " + REPORT_NAME);
		  		 File file = new File(path, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	}
	
	/*
	 * public void pdf(String name) throws JRException, IOException { FacesContext
	 * faces = FacesContext.getCurrentInstance(); ExternalContext context =
	 * faces.getExternalContext(); HttpServletResponse response =
	 * (HttpServletResponse)context.getResponse();
	 * response.addHeader("Content-disposition",
	 * "attachment; filename="+name+".pdf");
	 * 
	 * faces.getCurrentInstance().responseComplete();
	 * 
	 * ServletOutputStream output = response.getOutputStream(); JasperPrint print =
	 * JasperFillManager.fillReport(report, new HashMap<String, String>());
	 * JasperExportManager.exportReportToPdfStream(print, output); output.flush();
	 * output.close(); faces.getCurrentInstance().responseComplete(); }
	 */
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}
	
	/*
	 * public Date getDateTrans() { if(dateTrans==null) { dateTrans =
	 * DateUtils.convertDateString(DateUtils.getCurrentDateYYYYMMDD(),DateFormat.
	 * YYYY_MM_DD()); } return dateTrans; } public void setDateTrans(Date dateTrans)
	 * { this.dateTrans = dateTrans; }
	 */
	public List getMonths() {
		months = new ArrayList<>();
		
		for(int i=1; i<=12; i++) {
			months.add(new SelectItem(i, DateUtils.getMonthName(i)));
		}
		
		return months;
	}
	public void setMonths(List months) {
		this.months = months;
	}
	public int getMonthId() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		return monthId;
	}
	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public List<TransmittalRpt> getTransmittals() {
		return transmittals;
	}

	public void setTransmittals(List<TransmittalRpt> transmittals) {
		this.transmittals = transmittals;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<TransmittalRpt> getCheckSelected() {
		return checkSelected;
	}

	public void setCheckSelected(List<TransmittalRpt> checkSelected) {
		this.checkSelected = checkSelected;
	}

	public int getYear() {
		if(year==0) {
			year = DateUtils.getCurrentYear();
		}
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getRcdDate1() {
		return rcdDate1;
	}

	public String getRcdDate2() {
		return rcdDate2;
	}

	public String getRcdDate3() {
		return rcdDate3;
	}

	public String getRcdNo1() {
		return rcdNo1;
	}

	public String getRcdNo2() {
		return rcdNo2;
	}

	public String getRcdNo3() {
		return rcdNo3;
	}

	public String getRcdAmount1() {
		return rcdAmount1;
	}

	public String getRcdAmount2() {
		return rcdAmount2;
	}

	public String getRcdAmount3() {
		return rcdAmount3;
	}

	public String getOtherDate1() {
		return otherDate1;
	}

	public String getOtherDate2() {
		return otherDate2;
	}

	public String getOtherDate3() {
		return otherDate3;
	}

	public String getOtherTypeReport1() {
		return otherTypeReport1;
	}

	public String getOtherTypeReport2() {
		return otherTypeReport2;
	}

	public String getOtherTypeReport3() {
		return otherTypeReport3;
	}

	public void setRcdDate1(String rcdDate1) {
		this.rcdDate1 = rcdDate1;
	}

	public void setRcdDate2(String rcdDate2) {
		this.rcdDate2 = rcdDate2;
	}

	public void setRcdDate3(String rcdDate3) {
		this.rcdDate3 = rcdDate3;
	}

	public void setRcdNo1(String rcdNo1) {
		this.rcdNo1 = rcdNo1;
	}

	public void setRcdNo2(String rcdNo2) {
		this.rcdNo2 = rcdNo2;
	}

	public void setRcdNo3(String rcdNo3) {
		this.rcdNo3 = rcdNo3;
	}

	public void setRcdAmount1(String rcdAmount1) {
		this.rcdAmount1 = rcdAmount1;
	}

	public void setRcdAmount2(String rcdAmount2) {
		this.rcdAmount2 = rcdAmount2;
	}

	public void setRcdAmount3(String rcdAmount3) {
		this.rcdAmount3 = rcdAmount3;
	}

	public void setOtherDate1(String otherDate1) {
		this.otherDate1 = otherDate1;
	}

	public void setOtherDate2(String otherDate2) {
		this.otherDate2 = otherDate2;
	}

	public void setOtherDate3(String otherDate3) {
		this.otherDate3 = otherDate3;
	}

	public void setOtherTypeReport1(String otherTypeReport1) {
		this.otherTypeReport1 = otherTypeReport1;
	}

	public void setOtherTypeReport2(String otherTypeReport2) {
		this.otherTypeReport2 = otherTypeReport2;
	}

	public void setOtherTypeReport3(String otherTypeReport3) {
		this.otherTypeReport3 = otherTypeReport3;
	}

	public Transmittal getTransData() {
		return transData;
	}

	public void setTransData(Transmittal transData) {
		this.transData = transData;
	}

	public List<Transmittal> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transmittal> transactions) {
		this.transactions = transactions;
	}
	
	public static void main(String[] args) {
		
		String rcdsContent = "May 1, 2019<:>1<:>100<a>May 2 2019<:>2<:>200<a>May 3, 2019<:>3<:>300";
		String rptsContent = "May 1, 2019<:>report 1<*>May 2, 2019<:>report 2<*>May 3, 2019<:>report 3";
		String[] rcds = rcdsContent.split("<a>");
		System.out.println("rcds[0] == " + rcds[0]);
		System.out.println("rcds[1] == " + rcds[1]);
		System.out.println("rcds[2] == " + rcds[2]);
		String[] rcd1 = rcds[0].split("<:>");
		String[] rcd2 = rcds[1].split("<:>");
		String[] rcd3 = rcds[2].split("<:>");
		
		System.out.println(rcd1[0] + " " +rcd1[1] + " " + rcd1[2]);
		System.out.println(rcd2[0] + " " +rcd2[1] + " " + rcd2[2]);
		System.out.println(rcd3[0] + " " +rcd3[1] + " " + rcd3[2]);
		
		String[] rpts = rptsContent.split("*>");
		String[] rpt1 = rpts[0].split("<:>");
		String[] rpt2 = rpts[1].split("<:>");
		String[] rpt3 = rpts[2].split("<:>");
		
		System.out.println(rpt1[0] + " " + rpt1[1]);
		System.out.println(rpt2[0] + " " + rpt2[1]);
		System.out.println(rpt3[0] + " " + rpt3[1]);
	}
}
