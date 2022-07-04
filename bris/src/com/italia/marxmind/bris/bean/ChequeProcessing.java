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

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.BankAccounts;
import com.italia.marxmind.bris.controller.Chequedtls;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.MOOE;
import com.italia.marxmind.bris.controller.NumberToWords;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;
import com.italia.marxmind.bris.utils.Numbers;

@Named("checkBean")
@ViewScoped
public class ChequeProcessing implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6677898798675643341L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private List bankAccounts;
	private int accountId;
	private Map<Integer, BankAccounts> accountMaps = Collections.synchronizedMap(new HashMap<Integer, BankAccounts>());
	
	private List<Chequedtls> checks = Collections.synchronizedList(new ArrayList<Chequedtls>());
	private String dateFrom;
	private String dateTo;
	private String searchData;
	private List searchBankAccounts;
	private int searchAccountId;
	private String dvNumber;
	
	private Chequedtls checkSelected;
	private String checkNumber;
	//private String dateTrans;
	private double amount;
	private String issueTo;
	private String numberInToWords;
	
	private List status;
	private int statusId;
	
	private List signatory1;
	private long sigId1;
	private Map<Long, Employee> sigMap1 = Collections.synchronizedMap(new HashMap<Long, Employee>());
	
	private List signatory2;
	private long sigId2;
	private Map<Long, Employee> sigMap2 = Collections.synchronizedMap(new HashMap<Long, Employee>());
	
	private String totalAmount;
	
	private Date calendarFrom;
	private Date calendarTo;
	private Date calendarTrans;
	
	private List moes = Collections.synchronizedList(new ArrayList<MOOE>());
	private long moi;
	
	private List moeSearch = Collections.synchronizedList(new ArrayList<MOOE>());
	private long moid;
	
	private List<MOOE> budgetsInfo = Collections.synchronizedList(new ArrayList<MOOE>()); 
	private String totalUsedMooe;
	private String totalMooe;
	private String totalRemMooe;
	private String totalMooePercentage;
	private String totalBudgetUsedPercentage;
	private String purpose;
	@PostConstruct
	public void init(){
		
		Login in = Login.getUserLogin();
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			
			loadCheckProcessed();
			
		}else{
		
			if(Features.isEnabled(Feature.CHEQUE)){
				loadCheckProcessed();
			}
		
		}
	}
	
	private void loadCheckProcessed(){
		checks = Collections.synchronizedList(new ArrayList<Chequedtls>());
		double amount = 0d;
		String sql = " AND chk.isactivechk=1 ";
		String[] params = new String[2];
		
		if(getMoid()!=0) {
			sql += " AND moe.moid=?";
			
		}/*else {
			sql += " AND moe.moid!=";
		}*/
		
		sql +=" AND (chk.datetrans>=? AND chk.datetrans<=?)";
		
		if(getSearchAccountId()!=0){
			params = new String[3];
			sql +=" AND  bank.bankid=?";
			
			if(getMoid()!=0) {
				params = new String[4];
				params[0] = getMoid()+"";
				params[1] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[2] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[3] = getSearchAccountId()+"";
			}else {
				params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[2] = getSearchAccountId()+"";
			}
			
		}else{
			if(getMoid()!=0) {
				params = new String[3];
				params[0] = getMoid()+"";
				params[1] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[2] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			}else {
				params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			}
		}
		
		if(getSearchData()!=null && !getSearchData().isEmpty()){
			sql += " AND (chk.checkno like '%"+ getSearchData().replace("--", "") +
					"%' OR chk.issueto like '%"+ getSearchData().replace("--", "") +"%')";
		}
		
		
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)){
			checks.add(chk);
			amount += chk.getAmount();
		}
		Collections.reverse(checks);
		setTotalAmount(Currency.formatAmount(amount));
	}
	
	public List<String> autoPayToName(String query){
		String sql = "SELECT DISTINCT  issueto from cheques WHERE  issueto like '%" + query.replace("--", "") + "%' LIMIT 20";
		String[] params = new String[0];
		List<String> result = new ArrayList<>();
		result = Chequedtls.retrievePayOrderOf(sql, params);
		return result;
	}
	
	public void generateWords(){
		String words = NumberToWords.changeToWords(getAmount());
		setNumberInToWords(words.toUpperCase());
	}
	
	public void createNew(){
		clearFields();
		//loadNewCheckNo();
	}
	
	public void saveData(){
		boolean isOk = false;
		
		Login in = Login.getUserLogin();
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOk = true;
		}else{
			if(Features.isEnabled(Feature.CHEQUE)){
				isOk = true;
			}
		}
		
		if(isOk){
		Chequedtls chk = new Chequedtls();
		
		if(getCheckSelected()!=null){
			chk = getCheckSelected();
		}else{
			chk.setIsActive(1);
		}
		
		if(getMoi()==0){
			Application.addMessage(2, "Please provide MOOE Budget", "");
			isOk=false;
		}
		
		if(getAccountId()==0){
			Application.addMessage(2, "Please provide account.", "");
			isOk=false;
		}
		
		if(getCheckNumber()==null || getCheckNumber().isEmpty()){
			Application.addMessage(2, "Please provide check no.", "");
			isOk=false;
		}
		
		if(getIssueTo()==null || getIssueTo().isEmpty()){
			Application.addMessage(2, "Please provide Issue To.", "");
			isOk=false;
		}
		
		if(getCalendarTrans()==null){
			Application.addMessage(2, "Please provide date", "");
			isOk=false;
		}
		
		if(getAmount()==0){
			Application.addMessage(2, "Please provide Amount", "");
			isOk=false;
		}else if(getAmount()<0){
			Application.addMessage(2, "Please provide Positive Amount", "");
			isOk=false;
		}
		
		if(getPurpose()==null){
			Application.addMessage(2, "Please provide purpose", "");
			isOk=false;
		}
		
		if(isOk){
			chk.setStatus(getStatusId());
			chk.setPurpose(getPurpose());
			chk.setDateTrans(DateUtils.convertDate(getCalendarTrans(), DateFormat.YYYY_MM_DD()));
			chk.setCheckNo(getCheckNumber());
			if(getStatusId()==2){//cancelled
				chk.setAmount(0.00);
				chk.setIssueTo("CANCELLED");
			}else{
				chk.setAmount(getAmount());
				chk.setIssueTo(getIssueTo().toUpperCase());
			}
			
			Employee emp1 = getSigMap1().get(getSigId1());
			Employee emp2 = getSigMap2().get(getSigId2());
			
			chk.setSignatory1(emp1);
			chk.setSignatory2(emp2);
			
			chk.setAccounts(getAccountMaps().get(getAccountId()));
			
			UserDtls user = Login.getUserLogin().getUserDtls();
			chk.setUserDtls(user);
			
			MOOE mo = new MOOE();
			mo.setId(getMoi());
			chk.setMooe(mo);
			
			chk.setDvNo(getDvNumber());
			
			chk = Chequedtls.save(chk);
			setCheckSelected(chk);
			init();
			Application.addMessage(1, "Check has been successfully saved.", "");
		}
		
		}else{
			Application.addMessage(3, "Feature is not available. Please contact developer to enable this feature.", "");
		}
	}
	
	public void deleteRow(Chequedtls dtls){
		if(dtls.getStatus()==2){ //only cancelled can be deleted
			dtls.delete();
			Application.addMessage(1, "Successfully deleted.", "");
			init();
		}else{
			Application.addMessage(3, "Only Cancelled check can be deleted.", "");
		}
	}
	
	
	
	public void processAccnt(AjaxBehaviorEvent e){
		System.out.println("processAccnt......");
		loadNewCheckNo();
	}
	
	private void loadNewCheckNo(){
		if(getCheckSelected()==null){
			
			long i = 0;
			System.out.println("processAccnt...... check account id " + getAccountId());
			if(getAccountId()==0){
				i = Chequedtls.getLastCheckNo()!=0?  Chequedtls.getLastCheckNo() + 1 : 0;
			}else{
				i = Chequedtls.getLastAccountCheckNo(getAccountId());
				i +=1; //increment 1
			}
			
			String len = i+"";
			int ln = len.length();
			switch(ln){
			case 0 : setCheckNumber("0000000000"); break;
			case 1 : setCheckNumber("000000000"+i); break;
			case 2 : setCheckNumber("00000000"+i); break;
			case 3 : setCheckNumber("0000000"+i); break;
			case 4 : setCheckNumber("000000"+i); break;
			case 5 : setCheckNumber("00000"+i); break;
			case 6 : setCheckNumber("0000"+i); break;
			case 7 : setCheckNumber("000"+i); break;
			case 8 : setCheckNumber("00"+i); break;
			case 9 : setCheckNumber("0"+i); break;
			case 10 : setCheckNumber(""+i); break;
			
			}
		}
	}
	
	public void closePopup(){
		clearFields();
		init();
	}
	
	public void clearFields(){
		setPurpose(null);
		setSearchData(null);
		setCheckSelected(null);
		setCheckNumber(null);
		//setDateTrans(null);
		setCalendarTrans(null);
		setAmount(0);
		setNumberInToWords(null);
		setIssueTo(null);
		setAccountId(1);
		setIssueTo(null);
		setStatusId(1);
		setSigId1(0);
		setSigId2(0);
		getSignatory1();
		getSignatory2();
		loadNewCheckNo();
		setDvNumber(null);
	}
	
	public void clickItem(Chequedtls chk){
		clearFields();
		setPurpose(chk.getPurpose());
		setCheckSelected(chk);
		setCheckNumber(chk.getCheckNo());
		setCalendarTrans(DateUtils.convertDateString(chk.getDateTrans(), DateFormat.YYYY_MM_DD()));
		setAmount(chk.getAmount());
		setNumberInToWords(NumberToWords.changeToWords(chk.getAmount()).toUpperCase());
		setIssueTo(chk.getIssueTo());
		setStatusId(chk.getStatus());
		setAccountId(chk.getAccounts().getId());
		Employee emp1 = chk.getSignatory1();
		Employee emp2 = chk.getSignatory2();
		setSigId1(emp1.getId());
		setSigId2(emp2.getId());
		setMoi(chk.getMooe().getId());
		setDvNumber(chk.getDvNo());
	}

	public List getBankAccounts() {
		
		bankAccounts = new ArrayList<>();
		accountMaps = Collections.synchronizedMap(new HashMap<Integer, BankAccounts>());
			bankAccounts.add(new SelectItem(0, "Select Accounts"));
		for(BankAccounts account : BankAccounts.retrieve("SELECT * FROM bankaccounts", new String[0]) ){
			bankAccounts.add(new SelectItem(account.getId(), account.getBankAccntNo()));
			accountMaps.put(account.getId(), account);
		}
		
		return bankAccounts;
	}


	public void setBankAccounts(List bankAccounts) {
		this.bankAccounts = bankAccounts;
	}


	public int getAccountId() {
		if(accountId==0){
			accountId = 1;
			loadNewCheckNo();
		}
		return accountId;
	}


	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}


	public Map<Integer, BankAccounts> getAccountMaps() {
		return accountMaps;
	}


	public void setAccountMaps(Map<Integer, BankAccounts> accountMaps) {
		this.accountMaps = accountMaps;
	}

	public List<Chequedtls> getChecks() {
		return checks;
	}

	public void setChecks(List<Chequedtls> checks) {
		this.checks = checks;
	}

	public String getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		if(dateTo==null){
			dateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getSearchData() {
		return searchData;
	}

	public void setSearchData(String searchData) {
		this.searchData = searchData;
	}

	public List getSearchBankAccounts() {
		
		searchBankAccounts = new ArrayList<>();
		searchBankAccounts.add(new SelectItem(0, "All Accounts"));
		for(BankAccounts account : BankAccounts.retrieve("SELECT * FROM bankaccounts", new String[0]) ){
			searchBankAccounts.add(new SelectItem(account.getId(), account.getBankAccntNo() + " " + account.getBankAccntBranch()));
		}
		
		return searchBankAccounts;
	}

	public void setSearchBankAccounts(List searchBankAccounts) {
		this.searchBankAccounts = searchBankAccounts;
	}

	public int getSearchAccountId() {
		if(searchAccountId==0) {
			searchAccountId=1;
		}
		return searchAccountId;
	}

	public void setSearchAccountId(int searchAccountId) {
		this.searchAccountId = searchAccountId;
	}

	public Chequedtls getCheckSelected() {
		return checkSelected;
	}

	public void setCheckSelected(Chequedtls checkSelected) {
		this.checkSelected = checkSelected;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	/*public String getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}*/

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getIssueTo() {
		return issueTo;
	}

	public void setIssueTo(String issueTo) {
		this.issueTo = issueTo;
	}

	public List getSignatory1() {
		
		signatory1 = new ArrayList<>();
		sigMap1 = Collections.synchronizedMap(new HashMap<Long, Employee>());
		
		for(Employee emp : Employee.retrieve(" AND emp.isactiveemp=1 AND emp.isofficial=1 AND emp.isresigned=0", new String[0])){
			signatory1.add(new SelectItem(emp.getId(), emp.getFirstName() + " " + emp.getMiddleName().substring(0,1) + ". " + emp.getLastName()));
			sigMap1.put(emp.getId(), emp);
		}
		
		return signatory1;
	}

	public void setSignatory1(List signatory1) {
		this.signatory1 = signatory1;
	}

	public long getSigId1() {
		return sigId1;
	}

	public void setSigId1(long sigId1) {
		this.sigId1 = sigId1;
	}

	public Map<Long, Employee> getSigMap1() {
		return sigMap1;
	}

	public void setSigMap1(Map<Long, Employee> sigMap1) {
		this.sigMap1 = sigMap1;
	}

	public List getSignatory2() {
		
		signatory2 = new ArrayList<>();
		sigMap2 = Collections.synchronizedMap(new HashMap<Long, Employee>());
		
		for(Employee emp : Employee.retrieve(" AND emp.isactiveemp=1 AND emp.isofficial=0 AND emp.isresigned=0", new String[0])){
			signatory2.add(new SelectItem(emp.getId(), emp.getFirstName() + " " + emp.getMiddleName().substring(0,1) + ". " + emp.getLastName()));
			sigMap2.put(emp.getId(), emp);
		}
		
		return signatory2;
	}

	public void setSignatory2(List signatory2) {
		this.signatory2 = signatory2;
	}

	public long getSigId2() {
		return sigId2;
	}

	public void setSigId2(long sigId2) {
		this.sigId2 = sigId2;
	}

	public Map<Long, Employee> getSigMap2() {
		return sigMap2;
	}

	public void setSigMap2(Map<Long, Employee> sigMap2) {
		this.sigMap2 = sigMap2;
	}

	public String getNumberInToWords() {
		return numberInToWords;
	}

	public void setNumberInToWords(String numberInToWords) {
		this.numberInToWords = numberInToWords;
	}

	public List getStatus() {
		status = new ArrayList<>();
		status.add(new SelectItem(1, "PROCESSED"));
		status.add(new SelectItem(2, "CANCELLED"));
		return status;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public int getStatusId() {
		if(statusId==0){
			statusId = 1;
		}
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public  void printReportIndividual(Chequedtls chk){
		//String date = chk.getDateTrans();
		//String tmpDate = date;
		//chk.setDate_disbursement(convertDateToMonthDayYear(date));
		
		chk.compileReport(chk);
		//chk.setDate_disbursement(tmpDate);
		try{
		String REPORT_PATH = Bris.PRIMARY_DRIVE.getName() +  Bris.SEPERATOR.getName() + Bris.APP_FOLDER.getName() +  Bris.SEPERATOR.getName() +
				 Bris.REPORT.getName() + Bris.SEPERATOR.getName();
		String REPORT_NAME = ReadXML.value(ReportTag.CHECK);
		
		
		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
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
	            response.setBufferSize(DEFAULT_BUFFER_SIZE);
	            response.setHeader("Content-Type", "application/pdf");
	            response.setHeader("Content-Length", String.valueOf(file.length()));
	            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
	            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
	            
	            // Write file contents to response.
	            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	            int length;
	            while ((length = input.read(buffer)) > 0) {
	                output.write(buffer, 0, length);
	                System.out.println("printReportIndividual read : " + length);
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
	    
	       // com.italia.municipality.lakesebu.controller.Chequedtls.backupPrint();
	        
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public Date getCalendarFrom() {
		if(calendarFrom==null){
			String date = DateUtils.getCurrentYear() + "-" + (DateUtils.getCurrentMonth()>=10? DateUtils.getCurrentMonth() : "0" + DateUtils.getCurrentMonth()) + "-01";
			calendarFrom = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			//calendarFrom = DateUtils.getDateToday();
		}
		return calendarFrom;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			String date = DateUtils.getLastDayOfTheMonth("yyyy-MM-dd", DateUtils.getCurrentDateYYYYMMDD(), Locale.TAIWAN);
			calendarTo = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			//calendarTo = DateUtils.getDateToday();
		}
		return calendarTo;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}

	public Date getCalendarTrans() {
		if(calendarTrans==null){
			calendarTrans = DateUtils.getDateToday();
		}
		return calendarTrans;
	}

	public void setCalendarTrans(Date calendarTrans) {
		this.calendarTrans = calendarTrans;
	}

	public List getMoes() {
		moes = Collections.synchronizedList(new ArrayList<MOOE>());
		String sql = " AND  mo.moisactive=1 AND mo.moyear=?";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYear()+"";
		for(MOOE mo : MOOE.retrieve(sql, params)){
			moes.add(new SelectItem(mo.getId(), mo.getName()));
		}
		
		return moes;
	}

	public void setMoes(List moes) {
		this.moes = moes;
	}

	public long getMoi() {
		if(moi==0){
			String sql = "SELECT * FROM mooe WHERE moisactive=1 AND moyear=? limit 1";
			String[] params = new String[1];
			params[0] = DateUtils.getCurrentYear()+"";
			MOOE mo = null;
			try{
				mo = MOOE.retrieveMOOE(sql, params);
				moi = mo.getId();
			}catch(Exception e){moi=0;}
		}
		return moi;
	}

	public void setMoi(long moi) {
		this.moi = moi;
	}

	public List getMoeSearch() {
		
		moeSearch = Collections.synchronizedList(new ArrayList<MOOE>());
		moeSearch.add(new SelectItem(0, "All BUDGET"));
		String sql = " AND  mo.moisactive=1 AND mo.moyear=?";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYear()+"";
		for(MOOE mo : MOOE.retrieve(sql, params)){
			moeSearch.add(new SelectItem(mo.getId(), mo.getName()));
		}
		
		return moeSearch;
	}

	public void setMoeSearch(List moeSearch) {
		this.moeSearch = moeSearch;
	}

	public long getMoid() {
		return moid;
	}

	public void setMoid(long moid) {
		this.moid = moid;
	}
	
	public void loadBudgetDtls() {
		
		
		
		String sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentYear() + "-01-01";
		params[1] = DateUtils.getCurrentYear() + "-12-31";
		
		List<Chequedtls> chks = Collections.synchronizedList(new ArrayList<Chequedtls>());
		Map<Long, List<Chequedtls>> chkMap = Collections.synchronizedMap(new HashMap<Long, List<Chequedtls>>());
		for(Chequedtls ck : Chequedtls.retrieve(sql, params)) {
			long moeid = ck.getMooe().getId();
			if(chkMap!=null) {
				if(chkMap.containsKey(moeid)){
					chks = chkMap.get(moeid);
					chks.add(ck);
					chkMap.put(moeid, chks);
				}else {
					chks = Collections.synchronizedList(new ArrayList<Chequedtls>());
					chks.add(ck);
					chkMap.put(moeid, chks);
				}
			}else {
				chks.add(ck);
				chkMap.put(moeid, chks);
			}
		}
		
		double mooeBudget = MOOE.mooeAmount(DateUtils.getCurrentYear());
		
		budgetsInfo = Collections.synchronizedList(new ArrayList<MOOE>());
		double toalRemainingMOOE_tmp=0d;
		double usedBudget_tmp = 0d;
		double totalbudget_tmp = 0d;
		double totalMooePercentage=0d;
		double totalpercentBudgetUsed=0d;
		for(long moeid : chkMap.keySet()) {
			double totalUsedMOOE=0d;
			double totalMOOE=0d;
			double toalRemainingMOOE=0d;
			double usedBudget = 0d;
			double percentage= 0d;
			double percentPerMooe = 0d;
			double percentBudgetUded=0d;
			List<Chequedtls> chqs = chkMap.get(moeid);
			MOOE moe = new MOOE();
			for(Chequedtls chk : chqs) {
				usedBudget += chk.getAmount();
				moe = chk.getMooe();
			}
			double budget = MOOE.moeBudget(moeid, DateUtils.getCurrentYear());
			moe.setAmountMo(Currency.formatAmount(budget));
			moe.setUsedBudget(Currency.formatAmount(usedBudget));
			moe.setCheques(chqs);
			
			totalUsedMOOE += usedBudget;
			totalMOOE += budget;
			toalRemainingMOOE = totalMOOE - totalUsedMOOE;
			
			//percentage per mooe
			percentPerMooe = (budget/mooeBudget) * 100;
			moe.setPercentPerMooe(Numbers.formatDouble(percentPerMooe)+"%");
			
			//percentage used
			percentBudgetUded = (usedBudget/mooeBudget) * 100;
			moe.setPercentBudgetUsed(Numbers.formatDouble(percentBudgetUded)+"%");
			
			//percentage calculation
			percentage = (usedBudget/budget) * 100;
			moe.setPercentage(Numbers.formatDouble(percentage)+"%");
			
			moe.setRemaining(Currency.formatAmount(toalRemainingMOOE));
			budgetsInfo.add(moe);
			
			usedBudget_tmp += usedBudget;
			toalRemainingMOOE_tmp +=toalRemainingMOOE;
			totalbudget_tmp +=budget;
			
			totalMooePercentage +=percentPerMooe;
			
			totalpercentBudgetUsed +=percentBudgetUded;
		}
		
		sql = " AND mo.moisactive=1 AND mo.moyear=" + DateUtils.getCurrentYear();
		params = new String[0];
		for(MOOE mo : MOOE.retrieve(sql, params)) {
			long moid = mo.getId();
			if(!chkMap.containsKey(moid)) {
				chks = Collections.synchronizedList(new ArrayList<Chequedtls>());
				mo.setCheques(chks);
				//percentage per mooe
				double percentPerMooe = (mo.getAmount()/mooeBudget) * 100;
				mo.setPercentPerMooe(Numbers.formatDouble(percentPerMooe)+"%");
				
				//percentage used
				double percentBudgetUded = (0/mooeBudget) * 100;
				mo.setPercentBudgetUsed(Numbers.formatDouble(percentBudgetUded)+"%");
				
				mo.setPercentage("0%");
				mo.setAmountMo(Currency.formatAmount(mo.getAmount()));
				mo.setUsedBudget(Currency.formatAmount(0));
				mo.setRemaining(Currency.formatAmount(mo.getAmount()));
				budgetsInfo.add(mo);
				toalRemainingMOOE_tmp +=mo.getAmount();
				totalbudget_tmp +=mo.getAmount();
				
				totalMooePercentage +=percentPerMooe;
				totalpercentBudgetUsed +=percentBudgetUded;
			}
		}
		setTotalBudgetUsedPercentage(Numbers.formatDouble(totalpercentBudgetUsed)+"%");
		setTotalMooePercentage(Numbers.formatDouble(totalMooePercentage)+"%");
		setTotalRemMooe(Currency.formatAmount(toalRemainingMOOE_tmp));
		setTotalUsedMooe(Currency.formatAmount(usedBudget_tmp));
		setTotalMooe(Currency.formatAmount(totalbudget_tmp));
	}

	public List<MOOE> getBudgetsInfo() {
		return budgetsInfo;
	}

	public void setBudgetsInfo(List<MOOE> budgetsInfo) {
		this.budgetsInfo = budgetsInfo;
	}

	public String getTotalUsedMooe() {
		return totalUsedMooe;
	}

	public void setTotalUsedMooe(String totalUsedMooe) {
		this.totalUsedMooe = totalUsedMooe;
	}

	public String getTotalMooe() {
		return totalMooe;
	}

	public void setTotalMooe(String totalMooe) {
		this.totalMooe = totalMooe;
	}

	public String getTotalRemMooe() {
		return totalRemMooe;
	}

	public void setTotalRemMooe(String totalRemMooe) {
		this.totalRemMooe = totalRemMooe;
	}

	public String getTotalMooePercentage() {
		return totalMooePercentage;
	}

	public void setTotalMooePercentage(String totalMooePercentage) {
		this.totalMooePercentage = totalMooePercentage;
	}

	public String getTotalBudgetUsedPercentage() {
		return totalBudgetUsedPercentage;
	}

	public void setTotalBudgetUsedPercentage(String totalBudgetUsedPercentage) {
		this.totalBudgetUsedPercentage = totalBudgetUsedPercentage;
	}

	public String getDvNumber() {
		if(dvNumber==null) {
			dvNumber = Chequedtls.getLastDVNo();
		}
		return dvNumber;
	}

	public void setDvNumber(String dvNumber) {
		this.dvNumber = dvNumber;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
}
