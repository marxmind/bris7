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
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.application.GlobalReportHandler;
import com.italia.marxmind.bris.application.GlobalReportHandler.GlobalReport;
import com.italia.marxmind.bris.controller.BankAccounts;
import com.italia.marxmind.bris.controller.BankChequeRpt;
import com.italia.marxmind.bris.controller.BankChequeTrans;
import com.italia.marxmind.bris.controller.Chequedtls;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.NumberToWords;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.controller.Words;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.qrcode.QRCode;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ChequesRpt;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Mark Italia
 * @since 12/14/2017
 * @version 1.0
 *
 */
@ManagedBean(name="bankBean",eager=true)
@ViewScoped
public class BankRptBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 54567867575461L;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	
	private static final String BANK_REPORT_NAME = ReadXML.value(ReportTag.BANK_REPORT);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private String pbcNo;
	private String recepient;
	private Date dateApplying;
	private String bankName;
	private String bankCityName;
	private String bankProvinceName;
	
	private Date cedulaDate;
	private String cedulaNo;
	
	private List<BankChequeTrans> cheques = Collections.synchronizedList(new ArrayList<BankChequeTrans>());
	private List<BankChequeRpt> rpts = Collections.synchronizedList(new ArrayList<BankChequeRpt>());
	private BankChequeRpt bankData;
	
	private Date calendarFrom;
	private Date calendarTo;
	private String searchData;
	private List<Chequedtls> checks = Collections.synchronizedList(new ArrayList<Chequedtls>());
	private List<Chequedtls> checkSelected;
	private List<Chequedtls> checkSelectedTemp;
	private List<Chequedtls> removeCheckSelected;
	private List<BankChequeTrans> checkData;
	
	private String searchParam;
	private Date calendarFromSearch;
	private Date calendarToSearch;
	
	private Map<Long, Chequedtls> mapProcessedCheques = Collections.synchronizedMap(new HashMap<Long, Chequedtls>());
	
	private List bankAccounts;
	private int accountId;
	
	private List bankAccountsSearch;
	private int accountIdSearch;
	
	public void search(){
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
			int len = getSearchParam().length();
			if(len>=10){
				loadData();
			}
		}
	}
	
	@PostConstruct
	public void init(){
		
		bankAccounts = new ArrayList<>();
		bankAccountsSearch = new ArrayList<>();
		bankAccountsSearch.add(new SelectItem(0, "All Accounts"));
		for(BankAccounts account : BankAccounts.retrieve("SELECT * FROM bankaccounts", new String[0]) ){
			bankAccounts.add(new SelectItem(account.getId(), account.getBankAccntNo()));
			bankAccountsSearch.add(new SelectItem(account.getId(), account.getBankAccntNo()));
		}
		
		loadData();
		
	}
	
	public void loadData() {
		rpts = Collections.synchronizedList(new ArrayList<BankChequeRpt>());
		
		String sql = " AND bnk.bankrptisactive=1 AND (bnk.dateapplying>=? AND bnk.dateapplying<=?) ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFromSearch(),DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getCalendarToSearch(),DateFormat.YYYY_MM_DD());
		if(getSearchParam()!=null){
			sql += " AND bnk.pbcno like '%"+ getSearchParam() +"%'";
		}
		
		if(getAccountIdSearch()>0) {
			sql += " AND bnk.accountid=" + getAccountIdSearch();
		}
		
		List<BankChequeRpt> banks = BankChequeRpt.retrieve(sql, params);
		
		//if(banks.size()==1){
			//rpts = bankLoad(banks);
			//clickItem(rpts.get(0));
		//}else{
			//clearFlds();
			rpts = bankLoad(banks);
		//}
	}
	
	private List<BankChequeRpt> bankLoad(List<BankChequeRpt> banks){
		List<BankChequeRpt> rptsbank = Collections.synchronizedList(new ArrayList<BankChequeRpt>());
		setMapProcessedCheques(Collections.synchronizedMap(new HashMap<Long, Chequedtls>()));
		for(BankChequeRpt rpt : banks){
			String sql = " AND tran.bankisactivetrans=1 AND bnk.bankchkid="+rpt.getId();
			List<BankChequeTrans> trans = BankChequeTrans.retrieve(sql, new String[0]);
			double amountTotal = 0d;
			for(BankChequeTrans chk : trans){
				amountTotal += chk.getChequedtls().getAmount();
				getMapProcessedCheques().put(chk.getChequedtls().getId(), chk.getChequedtls());
			}
			rpt.setBanksCheques(trans);
			rpt.setTotalChequesAmount(Currency.formatAmount(amountTotal));
			rptsbank.add(rpt);
		}
		return rptsbank;
	}
	
	public void clearFlds(){
		setAccountId(1);//barangay account
		setRemoveCheckSelected(null);
		setCheckSelectedTemp(null);
		setCheckData(null);
		setBankData(null);
		setPbcNo(generateNewPBCNo());
		setRecepient(Words.getTagName("bank-recepient"));
		setDateApplying(null);
		setBankName(Words.getTagName("bank-name"));
		setBankCityName(Words.getTagName("bank-location"));
		setBankProvinceName(Words.getTagName("bank-province"));
		setCedulaDate(null);
		setCedulaNo(null);
		setSearchData(null);
		setCalendarFrom(null);
		setCalendarTo(null);
		setChecks(Collections.synchronizedList(new ArrayList<Chequedtls>()));
		setCheckSelected(Collections.synchronizedList(new ArrayList<Chequedtls>()));
		
	}
	
	private String generateNewPBCNo(){
		
		String pbcNo = BankChequeRpt.getLastPBCNo(getAccountId());
		
		if("".equalsIgnoreCase(pbcNo)){
			pbcNo = DateUtils.getCurrentYear() +"-" + BARANGAY.toUpperCase() + "-000";
		}
		
		String name = pbcNo.split("-")[1];
		int pbcYear = Integer.valueOf(pbcNo.split("-")[0]);
		int cnt = Integer.valueOf(pbcNo.split("-")[2]);
		cnt +=1;
		String len = cnt+"";
		int size = len.length();
		String topUp="";
		if(size==1){
			topUp = "00"+cnt;
		}else if(size==2){
			topUp = "0"+cnt;
		}else{
			topUp = cnt+"";
		}
		
		if(pbcYear==DateUtils.getCurrentYear()){
			//return pbcYear +"-" + BARANGAY.toUpperCase() + "-" + topUp;
			return pbcYear +"-" + name.toUpperCase() + "-" + topUp;
		}else if(pbcYear!=DateUtils.getCurrentYear()){
			//return DateUtils.getCurrentYear() +"-" + BARANGAY.toUpperCase() + "-001";
			return DateUtils.getCurrentYear() +"-" + name.toUpperCase() + "-001";
		}
		
		return "";
	}
	
	public UserDtls getUser(){
		return Login.getUserLogin().getUserDtls();
	}
	
	public void saveTrans(){
		
		boolean isOk = true;
		
		if(getPbcNo()==null || getPbcNo().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide PBC No", "");
		}
		
		if(getRecepient()==null || getRecepient().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide Recepient", "");
		}
		
		if(getDateApplying()==null){
			isOk = false;
			Application.addMessage(3, "Please provide Date", "");
		}
		
		if(getBankName()==null || getBankName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide Bank Name", "");
		}
		
		if(getBankCityName()==null || getBankCityName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide Bank Address or City address", "");
		}
		
		if(getBankProvinceName()==null || getBankProvinceName().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide Bank Province address", "");
		}
		
		if(getCedulaDate()==null){
			isOk = false;
			Application.addMessage(3, "Please provide Cedula Issued Date", "");
		}
		
		if(getCedulaNo()==null || getCedulaNo().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Please provide Cedula No", "");
		}
		
		if(getCheckSelectedTemp()==null){
			isOk = false;
			Application.addMessage(3, "Please provide Cheques", "");
		}
		
		if(isOk){
			BankChequeRpt rpt = new BankChequeRpt();
			if(getBankData()!=null){
				rpt = getBankData();
			}else{
				rpt.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				rpt.setIsActive(1);
			}
			
			rpt.setRecepient(getRecepient().toUpperCase());
			rpt.setBankName(getBankName());
			rpt.setBankLocation(getBankCityName());
			rpt.setBankProvince(getBankProvinceName());
			rpt.setPbcNo(getPbcNo().toUpperCase());
			rpt.setDateApplying(DateUtils.convertDate(getDateApplying(),DateFormat.YYYY_MM_DD()));
			
			rpt.setCtaxNo(getCedulaNo());
			rpt.setCtaxIssuedDate(DateUtils.convertDate(getCedulaDate(),DateFormat.YYYY_MM_DD()));
			
			Employee emp = Employee.retrieve(" AND emp.isofficial=1 AND emp.isactiveemp=1 AND emp.posid=1", new String[0]).get(0);
			
			rpt.setEmployee(emp);
			
			rpt.setCtaxIssuedAt(MUNICIPALITY.toUpperCase() + ", " + PROVINCE.toUpperCase());
			rpt.setUserDtls(getUser());
			rpt.setAccountId(getAccountId());
			if(rpt.getBanksCheques()!=null){
				for(BankChequeTrans trans : rpt.getBanksCheques()){
					String sql = "DELETE FROM bankchequetrans where btid=?";
					String[] params = new String[1];
					params[0] = trans.getId()+"";
					trans.delete(sql,params);
				}
			}
			
			rpt = BankChequeRpt.save(rpt);
			List<BankChequeTrans> trans = Collections.synchronizedList(new ArrayList<BankChequeTrans>());
			for(Chequedtls chk : getCheckSelectedTemp()){
				BankChequeTrans tran = new BankChequeTrans();
				tran.setChequedtls(chk);
				tran.setChequeRpt(rpt);
				tran.setIsAcative(1);
				tran = BankChequeTrans.save(tran);
				trans.add(tran);
				System.out.println("saving to cheks trans table");
			}
			rpt.setBanksCheques(trans);
			setBankData(rpt);
			setCheckData(trans);
			setCalendarFromSearch(getDateApplying());
			setCalendarToSearch(getDateApplying());
			//init();
			loadData();
			Application.addMessage(1, "Transaction has been successfully saved.", "");
			setAccountIdSearch(getAccountId());
		}
		
	}
	
	public void clickItem(BankChequeRpt rpt){
		clearFlds();
		setBankData(rpt);
		setPbcNo(rpt.getPbcNo());
		setRecepient(rpt.getRecepient());
		setDateApplying(DateUtils.convertDateString(rpt.getDateApplying(),DateFormat.YYYY_MM_DD()));
		setBankName(rpt.getBankName());
		setBankCityName(rpt.getBankLocation());
		setBankProvinceName(rpt.getBankProvince());
		setCedulaDate(DateUtils.convertDateString(rpt.getCtaxIssuedDate(),DateFormat.YYYY_MM_DD()));
		setCedulaNo(rpt.getCtaxNo());
		setCheckSelected(Collections.synchronizedList(new ArrayList<Chequedtls>()));
		setAccountId(rpt.getAccountId());
		String sql = " AND tran.bankisactivetrans=1 AND bnk.bankchkid="+rpt.getId();
		List<BankChequeTrans> trans = BankChequeTrans.retrieve(sql, new String[0]);
		
		for(BankChequeTrans tran : trans){
			Chequedtls chk = Chequedtls.retrieve(tran.getChequedtls().getId());
			getCheckSelected().add(chk);
		}
		setCheckSelectedTemp(getCheckSelected());
		setCheckData(rpt.getBanksCheques());
		
	}
	
	public void deleteRowTrans(BankChequeRpt rpt){
		rpt.delete();
		for(BankChequeTrans tran : rpt.getBanksCheques()){
			tran.delete();
		}
		//init();
		loadData();
		Application.addMessage(1, "Bank Transaction Data has been successfully removed.", "");
	}
	
	public void loadCheckProcessed(){
		checks = Collections.synchronizedList(new ArrayList<Chequedtls>());
		String sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?)";
		String[] params = new String[2];
		
		
		params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
		
		if(getAccountId()>0) {
			sql += " AND bank.bankid="+getAccountId();
		}
		
		if(getSearchData()!=null && !getSearchData().isEmpty()){
			sql += " AND (chk.checkno like '%"+ getSearchData().replace("--", "") +
					"%' OR chk.issueto like '%"+ getSearchData().replace("--", "") +"%')";
		}
		
		//checks = Chequedtls.retrieve(sql, params);
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)){//removed cheques already processed
			if(!getMapProcessedCheques().containsKey(chk.getId())){
				checks.add(chk);
			}
		}
		System.out.println("checkselected>> " + (getCheckSelectedTemp()==null? "null" : getCheckSelectedTemp().size()));
		if(getCheckSelectedTemp()!=null && getCheckSelectedTemp().size()>0){
			for(Chequedtls chk : getCheckSelectedTemp()){
				checks.add(chk);
				getCheckSelected().add(chk);
			}
		}
		
		Collections.reverse(checks);
		
	}
	
	public void updateTempCheckSelected(){
		//for(Chequedtls chk : getCheckSelected()){
			setCheckSelectedTemp(getCheckSelected());
			setPbcNo(generateNewPBCNo());
		//}
	}
	
	public List<String> autoPayToName(String query){
		String sql = "SELECT DISTINCT  issueto from cheques WHERE  issueto like '%" + query.replace("--", "") + "%' LIMIT 20";
		String[] params = new String[0];
		List<String> result = new ArrayList<>();
		result = Chequedtls.retrievePayOrderOf(sql, params);
		return result;
	}
	
	public void deleteCheckSelected(){
		if(getRemoveCheckSelected()!=null){
		for(Chequedtls chk : getRemoveCheckSelected()){	
				for(BankChequeTrans tran : getCheckData()){
					if(tran.getChequedtls().getId()==chk.getId()){
						tran.delete();
					}
				}
			getCheckSelected().remove(chk);
			getCheckSelectedTemp().remove(chk);
		}
		//init();
		loadData();
		clickItem(getBankData());
		Application.addMessage(1, "Check has been successfully removed.", "");
		}
	}
	
	public void printBank(BankChequeRpt rpt){
		Employee employee = Employee.retrieve(rpt.getEmployee().getId());
		String REPORT_NAME = "";
		HashMap param = new HashMap();
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		List<ChequesRpt> reports = Collections.synchronizedList(new ArrayList<ChequesRpt>());
		
		REPORT_NAME = GlobalReportHandler.report(GlobalReport.BANK_LETTER);
		
		String sql = " AND tran.bankisactivetrans=1 AND bnk.bankchkid="+rpt.getId();
		List<BankChequeTrans> trans = BankChequeTrans.retrieve(sql, new String[0]);
		
		double amount=0d;
		Map<String, Chequedtls> unsorted = Collections.synchronizedMap(new HashMap<String, Chequedtls>());
		for(BankChequeTrans tran : trans){
			Chequedtls ck = Chequedtls.retrieve(tran.getChequedtls().getId());
			amount +=ck.getAmount();
			unsorted.put(ck.getCheckNo(), ck);
		}
		Map<String, Chequedtls> sorted = new TreeMap<String, Chequedtls>(unsorted);
		String account = "";
		for(Chequedtls ck : sorted.values()){
			ChequesRpt rptChk = new ChequesRpt();
			account = ck.getAccounts().getBankAccntNo();
			//rptChk.setF1(ck.getAccounts().getBankAccntNo());
			rptChk.setF2(ck.getCheckNo());
			rptChk.setF3(ck.getDateTrans());
			rptChk.setF4(ck.getIssueTo().toUpperCase());
			rptChk.setF5(Currency.formatAmount(ck.getAmount()));
			rptChk.setF6(ck.getPurpose());
			reports.add(rptChk);
		}
		ChequesRpt rptChk = new ChequesRpt();
		//rptChk.setF1("*******************");
		rptChk.setF2("***************");
		rptChk.setF3("***************");
		rptChk.setF4("NOTHING FOLLOWS    *******************************");
		rptChk.setF5("*******************");
		rptChk.setF6("*****************************");
		reports.add(rptChk);
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		
		param.put("PARAM_PROVINCE", "Province of " + PROVINCE);
		param.put("PARAM_MUNICIPALITY", "Municipality of " + MUNICIPALITY);
		param.put("PARAM_BARANGAY", "BARANGAY " + BARANGAY.toUpperCase());
		
		param.put("PARAM_RECEPIENT", "TO: "+rpt.getRecepient());
		param.put("PARAM_BANKNAME", rpt.getBankName());
		param.put("PARAM_BANKLOC", rpt.getBankLocation());
		param.put("PARAM_BANKPROV", rpt.getBankProvince());
		param.put("PARAM_PBCNO", "PBC NO: "+rpt.getPbcNo());
		param.put("PARAM_DATEAPPLYING", "DATE: "+ DateUtils.convertDateToMonthDayYear(rpt.getDateApplying()).toUpperCase());
		param.put("PARAM_CEDULANO", rpt.getCtaxNo());
		param.put("PARAM_CEDULADATE", DateUtils.convertDateToMonthDayYear(rpt.getCtaxIssuedDate()));
		param.put("PARAM_ISSUEDAT", rpt.getCtaxIssuedAt());
		
		param.put("PARAM_ACCOUNT", "Account No: "+account);
		
		String bodyletler =  BARANGAY.toUpperCase(); //Words.getTagName("bank-string-1") +
		param.put("PARAM_BODY_LETTER", bodyletler);
		
		param.put("PARAM_TOTAL", "Grand Total: Php "+Currency.formatAmount(amount));
		String words = NumberToWords.changeToWords(Currency.formatAmount(amount).replace(",", ""));
		param.put("PARAM_INWORDS", "** "+ words.toUpperCase() +" **");
		
		String documentNote="Annex \"A\"\n";
		documentNote += "Brgy. Document No " + rpt.getId() +"\n";
		documentNote += "Series of " + rpt.getDateTrans().split("-")[0]+"\n";
		documentNote += rpt.getPbcNo();
		try{param.put("PARAM_DOCUMENT_NOTE", documentNote);}catch(NullPointerException e) {}
		try{param.put("PARAM_TAGLINE", Words.getTagName("tagline"));}catch(NullPointerException e) {}
		//background
		String backlogo = path + "documentbg-gen.png";
		try{File file = new File(backlogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_BACKGROUND", off);
		}catch(Exception e){}
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
		
		param.put("PARAM_OFFICER_DAY", employee.getFirstName().toUpperCase() + " " + employee.getMiddleName().substring(0,1).toUpperCase() + ". " + employee.getLastName().toUpperCase());
		if(employee.getId()==1){
			param.put("PARAM_OFFICIAL_TITLE",employee.getPosition().getName());
		}else{
			param.put("PARAM_OFFICIAL_TITLE","Barangay "+employee.getPosition().getName());
		}
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
		
		FileInputStream qrPdf = null;
		try{
			
			
			File folder = new File(path + "qrcode" + File.separator);
			folder.mkdir();
			String content = "PBC No: " +rpt.getPbcNo() +"\n";
			content += "Report: Punong Barangay's Certification\n";
			content += "Amount: Php"+Currency.formatAmount(amount)+"\n";
			content += "Barangay: "+BARANGAY.toUpperCase()+"\n";
			content += "Printed: "+ DateUtils.convertDateToMonthDayYear(rpt.getDateApplying()).toUpperCase() +"\n";
			content += "Please report for violation of this document.\n";
			content += "Provider: MARXMIND I.T. SOLUTIONS";
			File pdf = QRCode.createQRCode(content, 200, 200, path + "qrcode" + File.separator, rpt.getPbcNo());
			qrPdf = new FileInputStream(pdf);
			param.put("PARAM_QRCODE", qrPdf);
		}catch(Exception e){e.printStackTrace();}
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
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
	
	public String getRecepient() {
		if(recepient==null){
			recepient=Words.getTagName("bank-recepient");
		}
		return recepient;
	}
	public void setRecepient(String recepient) {
		this.recepient = recepient;
	}
	public Date getDateApplying() {
		if(dateApplying==null){
			dateApplying = DateUtils.getDateToday();
		}
		return dateApplying;
	}
	public void setDateApplying(Date dateApplying) {
		this.dateApplying = dateApplying;
	}
	public String getBankName() {
		if(bankName==null){
			bankName= Words.getTagName("bank-name");
		}
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCityName() {
		if(bankCityName==null){
			bankCityName=Words.getTagName("bank-location");
		}
		return bankCityName;
	}
	public void setBankCityName(String bankCityName) {
		this.bankCityName = bankCityName;
	}
	public String getBankProvinceName() {
		if(bankProvinceName==null){
			bankProvinceName=Words.getTagName("bank-province");
		}
		return bankProvinceName;
	}
	public void setBankProvinceName(String bankProvinceName) {
		this.bankProvinceName = bankProvinceName;
	}
	public Date getCedulaDate() {
		if(cedulaDate==null){
			cedulaDate =  DateUtils.convertDateString(Words.getTagName("bank-cedula-issued-cap"), DateFormat.YYYY_MM_DD());//DateUtils.getDateToday();
		}
		return cedulaDate;
	}
	public void setCedulaDate(Date cedulaDate) {
		this.cedulaDate = cedulaDate;
	}
	public String getCedulaNo() {
		if(cedulaNo==null){
			cedulaNo = Words.getTagName("bank-cedula-cap");
		}
		return cedulaNo;
	}
	public void setCedulaNo(String cedulaNo) {
		this.cedulaNo = cedulaNo;
	}
	public List<BankChequeTrans> getCheques() {
		return cheques;
	}
	public void setCheques(List<BankChequeTrans> cheques) {
		this.cheques = cheques;
	}
	public List<BankChequeRpt> getRpts() {
		return rpts;
	}
	public void setRpts(List<BankChequeRpt> rpts) {
		this.rpts = rpts;
	}

	public String getPbcNo() {
		if(pbcNo==null){
			pbcNo=generateNewPBCNo();
		}
		return pbcNo;
	}

	public void setPbcNo(String pbcNo) {
		this.pbcNo = pbcNo;
	}

	public Date getCalendarFrom() {
		if(calendarFrom==null){
			calendarFrom = DateUtils.getDateToday();
		}
		return calendarFrom;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			calendarTo = DateUtils.getDateToday(); 
		}
		return calendarTo;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}

	public String getSearchData() {
		return searchData;
	}

	public void setSearchData(String searchData) {
		this.searchData = searchData;
	}

	public List<Chequedtls> getChecks() {
		return checks;
	}

	public void setChecks(List<Chequedtls> checks) {
		this.checks = checks;
	}

	public List<Chequedtls> getCheckSelected() {
		return checkSelected;
	}

	public void setCheckSelected(List<Chequedtls> checkSelected) {
		this.checkSelected = checkSelected;
	}

	public BankChequeRpt getBankData() {
		return bankData;
	}

	public void setBankData(BankChequeRpt bankData) {
		this.bankData = bankData;
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}

	public List<BankChequeTrans> getCheckData() {
		return checkData;
	}

	public void setCheckData(List<BankChequeTrans> checkData) {
		this.checkData = checkData;
	}
	
	public Date getCalendarFromSearch() {
		if(calendarFromSearch==null){
			String date = DateUtils.getCurrentYear() + "-" + (DateUtils.getCurrentMonth()>=10? DateUtils.getCurrentMonth() : "0" + DateUtils.getCurrentMonth()) + "-01";
			calendarFromSearch = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			//calendarFromSearch = DateUtils.getDateToday();
		}
		return calendarFromSearch;
	}

	public void setCalendarFromSearch(Date calendarFromSearch) {
		this.calendarFromSearch = calendarFromSearch;
	}

	public Date getCalendarToSearch() {
		if(calendarToSearch==null){
			String date = DateUtils.getLastDayOfTheMonth("yyyy-MM-dd", DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN);
			calendarToSearch = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			//calendarToSearch = DateUtils.getDateToday();
		}
		return calendarToSearch;
	}

	public void setCalendarToSearch(Date calendarToSearch) {
		this.calendarToSearch = calendarToSearch;
	}

	public List<Chequedtls> getRemoveCheckSelected() {
		return removeCheckSelected;
	}

	public void setRemoveCheckSelected(List<Chequedtls> removeCheckSelected) {
		this.removeCheckSelected = removeCheckSelected;
	}

	public Map<Long, Chequedtls> getMapProcessedCheques() {
		return mapProcessedCheques;
	}

	public void setMapProcessedCheques(Map<Long, Chequedtls> mapProcessedCheques) {
		this.mapProcessedCheques = mapProcessedCheques;
	}

	public List<Chequedtls> getCheckSelectedTemp() {
		return checkSelectedTemp;
	}

	public void setCheckSelectedTemp(List<Chequedtls> checkSelectedTemp) {
		this.checkSelectedTemp = checkSelectedTemp;
	}

	public List getBankAccounts() {	    
		return bankAccounts;
	}

	public void setBankAccounts(List bankAccounts) {
		this.bankAccounts = bankAccounts;
	}

	public int getAccountId() {
		if(accountId==0){
			accountId = 1;
		}
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public List getBankAccountsSearch() {
		return bankAccountsSearch;
	}

	public void setBankAccountsSearch(List bankAccountsSearch) {
		this.bankAccountsSearch = bankAccountsSearch;
	}

	public int getAccountIdSearch() {
		return accountIdSearch;
	}

	public void setAccountIdSearch(int accountIdSearch) {
		this.accountIdSearch = accountIdSearch;
	}
}
