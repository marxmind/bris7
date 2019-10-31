package com.italia.marxmind.bris.bean;

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
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Cedula;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Email;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.ORTransaction;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.EmailType;
import com.italia.marxmind.bris.enm.Job;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

@ManagedBean(name="orBean", eager=true)
@ViewScoped
public class ORTransactionBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6878976901L;
	
	private String searchName;
	private Date calendarFrom;
	private Date calendarTo;
	private List<ORTransaction> orNumbers = Collections.synchronizedList(new ArrayList<ORTransaction>());
	private double grandTotal;
	
	private Date issuedDate;
	private String orNumber;
	private double amount;
	private String issuedAddress;
	private String purpose;
	
	private Customer customerSelected;
	private String citizenName;
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private List<Customer> taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchTaxpayerName;
	
	private ORTransaction orData;
	
	private int statId;
	private List stats;
	
	private List<String> checks;
	
	@PostConstruct
	public void init(){
		loadORs();
	}
	
	public void loadORs(){
		orNumbers = Collections.synchronizedList(new ArrayList<ORTransaction>());
		/*String sql = " AND (clz.clearissueddate>=? AND clz.clearissueddate<=?) AND clz.isactiveclearance=1 AND clz.amountpaid!=0 ";
		String[] params = new String[2];
		grandTotal = 0.0d;
		params[0] = DateUtils.convertDate(getCalendarFrom(), DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getCalendarTo(), DateFormat.YYYY_MM_DD());
		
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql += " AND ( cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
			sql += " OR clz.ornumber like '%"+ getSearchName().replace("--", "") +"%' )";
		}
		
		int cnt = 1;
		Map<String, Clearance> mapOrs = Collections.synchronizedMap(new HashMap<String, Clearance>());
		
		//removing duplicate OR Number
		for(Clearance cl : Clearance.retrieve(sql, params)){
			mapOrs.put(cl.getOrNumber(), cl);
		}
		Map<String, Clearance> sorted = new TreeMap<String, Clearance>(mapOrs);
		for(Clearance cl : sorted.values()){
			ORTransaction tr = new ORTransaction();
			tr.setCnt(cnt);
			tr.setId(tr.getId());
			tr.setDateTrans(cl.getIssuedDate());
			tr.setOrNumber(cl.getOrNumber());
			tr.setAmount(cl.getAmountPaid());
			tr.setCustomer(cl.getTaxPayer());
			grandTotal += cl.getAmountPaid();
			orNumbers.add(tr);
			cnt++;
		}*/
		String sql = " AND (orl.ordate>=? AND orl.ordate<=?) AND orl.oractive=1 ";
		String[] params = new String[2];
		grandTotal = 0.0d;
		params[0] = DateUtils.convertDate(getCalendarFrom(), DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getCalendarTo(), DateFormat.YYYY_MM_DD());
		
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			sql += " AND ( cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
			sql += " OR orl.orno like '%"+ getSearchName().replace("--", "") +"%' )";
		}
		
		int cnt = 1;
		for(ORTransaction ort : ORTransaction.retrieve(sql, params)){
			ort.setCnt(cnt++);
			orNumbers.add(ort);
			grandTotal += ort.getAmount();
		}
		
		ORTransaction tr = new ORTransaction();
		
		tr.setId(0);
		tr.setCnt(cnt++);
		tr.setDateTrans("Grand Total");
		tr.setOrNumber("");
		tr.setAmount(grandTotal);
		Customer cus = new Customer();
		cus.setFullname("");
		tr.setCustomer(cus);
		orNumbers.add(tr);
	}
	
	public void loadTaxpayer(){
		
		taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSearchTaxpayerName()!=null && !getSearchTaxpayerName().isEmpty()){
			//customer.setFullname(Whitelist.remove(getSearchCustomer()));
			sql += " AND cus.fullname like '%" + getSearchTaxpayerName() +"%'";
		}else{
			sql += " order by cus.customerid DESC limit 100;";
		}
		
		taxpayers = Customer.retrieve(sql, new String[0]);
	}
	
	public void saveOR(){
		ORTransaction or = new ORTransaction();
		boolean isOk = true;
		if(getOrData()!=null){
			or = getOrData();
		}else{
			or.setIsActive(1);
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Or Number");
		}
		
		if(getAmount()<=0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Amount");
		}
		
		if(getCustomerSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Citizen Name");
		}
		
		if(getIssuedAddress()==null || getIssuedAddress().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Address	");
		}
		
		if(isOk){
			
			if(or.getId()==0) {
				sendSystemMail();
			}
			
			or.setStatus(getStatId());
			or.setDateTrans(DateUtils.convertDate(getIssuedDate(), DateFormat.YYYY_MM_DD()));
			or.setOrNumber(getOrNumber());
			or.setAmount(getAmount());
			or.setAddress(getIssuedAddress());
			or.setCustomer(getCustomerSelected());
			or.setPurpose(getPurpose().toUpperCase());
			or.setUserDtls(Login.getUserLogin().getUserDtls());
			or.save();
			
			clearFlds();
			init();
			Application.addMessage(1, "Success", "Successfully saved");
			
			PrimeFaces ins = PrimeFaces.current();
			ins.executeScript("$('#panelHide').hide(1000);hideButton()");
		}
		
		
	}
	
	private void sendSystemMail() {
		
		int cnt=1;
		String toMailUser = "";
		boolean isCheckNote=false;
		for(String chk : getChecks()) {
			
			isCheckNote=true;
			
			if("Secretary".equalsIgnoreCase(chk)) {
				UserDtls toUser = UserDtls.retrieve(Job.SECRETARY.getId());
				if(cnt>1) {
					toMailUser += ":"+toUser.getUserdtlsid()+"";
				}else {
					toMailUser = toUser.getUserdtlsid()+"";
				}
			}else if("Treasurer".equalsIgnoreCase(chk)) {
				UserDtls toUser = UserDtls.retrieve(Job.TREASURER.getId());
				if(cnt>1) {
					toMailUser += ":"+toUser.getUserdtlsid()+"";
				}else {
					toMailUser = toUser.getUserdtlsid()+"";
				}
			}else if("Clerk".equalsIgnoreCase(chk)) {
					String sql = "SELECT * FROM userdtls WHERE jobtitleid=?";
					String[] params = new String[1];
					params[0] = Job.CLERK.getId()+"";
					List<UserDtls> toUsers = UserDtls.retrieve(sql, params);
					
					if(toUsers.size()>1) {
						String toM = "";
						int cntM=1;
						for(UserDtls user : toUsers) {
							if(cntM>1) {
								toM += ":"+user.getUserdtlsid()+"";
							}else {
								toM = user.getUserdtlsid()+"";
							}
							cntM++;	
						}
						if(cnt>1) {
							toMailUser += ":"+toM;
						}else {
							toMailUser = toM;
						}
						
					}else {
						if(cnt>1) {
							toMailUser += ":"+toUsers.get(0).getUserdtlsid()+"";
						}else {
							toMailUser = toUsers.get(0).getUserdtlsid()+"";
						}
					}
					
				}
			
			cnt++;
		}
		
		System.out.println("send to " + toMailUser);
		
		if(isCheckNote && !toMailUser.isEmpty()) {
			
			boolean isMore = false;
			try {
				String[] em = toMailUser.split(":");
				isMore=true;
		    }catch(Exception ex) {}
			if(isMore) {
				for(String sendTo : toMailUser.split(":")) {
					Email e = new Email();
					e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
					e.setTitle("New Official Receipt Created for " + getCustomerSelected().getFullname());
					
					e.setType(EmailType.INBOX.getId());
					e.setIsOpen(0);
					e.setIsDeleted(0);
					e.setIsActive(1);
					
					e.setToEmail(toMailUser);
					e.setPersonCopy(Long.valueOf(sendTo));
					e.setFromEmail("0");
					
					String msg = "";
					msg = "<p><strong>Hi</strong></p>";
					msg += "<br/>";
					msg += "<p>Please see below details for the new created official receipt.</p>";
					msg += "<br/>";
					msg += "<p>Official Receipt No: <strong>" + getOrNumber()+"</strong></p>";
					msg += "<p>Name : <strong>" + getCustomerSelected().getFullname()+"</strong></p>";
					msg += "<p>Purpose: <strong>" + getPurpose().toUpperCase() + "</strong></p>";
					msg += "<br/>";
					msg += "<p><strong>This is a system generated email. Please do not reply.</strong></p>";
					String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + sendTo;
					Email.emailSavePath(msg, fileName);
					e.setContendId(fileName);
					e.save();
				}
			}else {
				Email e = new Email();
				e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
				e.setTitle("New Official Receipt Created for " + getCustomerSelected().getFullname());
				
				e.setType(EmailType.INBOX.getId());
				e.setIsOpen(0);
				e.setIsDeleted(0);
				e.setIsActive(1);
				
				e.setToEmail(toMailUser);
				e.setPersonCopy(Long.valueOf(toMailUser));
				e.setFromEmail("0");
				
				String msg = "";
				msg = "<p><strong>Hi</strong></p>";
				msg += "<br/>";
				msg += "<p>Please see below details for the new created official receipt.</p>";
				msg += "<br/>";
				msg += "<p>Official Receipt No: <strong>" + getOrNumber()+"</strong></p>";
				msg += "<p>Name : <strong>" + getCustomerSelected().getFullname()+"</strong></p>";
				msg += "<p>Purpose: <strong>" + getPurpose().toUpperCase() + "</strong></p>";
				msg += "<br/>";
				msg += "<p><strong>This is a system generated email. Please do not reply.</strong></p>";
				String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + toMailUser;
				Email.emailSavePath(msg, fileName);
				e.setContendId(fileName);
				e.save();
			}
		}
		
	}
	
	public void deleteRow(ORTransaction or){
		or.delete();
		Application.addMessage(1, "Success", "Successfully deleted");
		clearFlds();
		init();
	}
	
	public void clickItem(ORTransaction or){
		
		setOrData(or);
		setOrNumber(or.getOrNumber());
		setAmount(or.getAmount());
		setIssuedAddress(or.getAddress());
		setPurpose(or.getPurpose());
		setIssuedDate(DateUtils.convertDateString(or.getDateTrans(),DateFormat.YYYY_MM_DD()));
		setCustomerSelected(or.getCustomer());
		setCitizenName(or.getCustomer().getFullname());
		setStatId(or.getStatus());
		
	}
	
	public void clickItemOwner(Customer cus){
		setCustomerSelected(cus);
		setCitizenName(cus.getFullname());
	}
	
	public void clearFlds(){
		setOrData(null);
		setCustomerSelected(null);
		setCitizenName(null);
		
		setIssuedDate(null);
		setOrNumber(null);
		setAmount(0);
		setIssuedAddress(null);
		setPurpose(null);
		
		setStatId(1);
		
		checks = Collections.synchronizedList(new ArrayList<String>());
	}
	
	public Date getCalendarFrom() {
		if(calendarFrom==null){
			
			String date = DateUtils.getCurrentYear()+"";
			date += "-"+(DateUtils.getCurrentMonth()<=9? "0"+DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+"");
			date += "-01";
			calendarFrom = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			
		}
		return calendarFrom;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			calendarTo = DateUtils.convertDateString(DateUtils.getEndOfMonthDate(1, Locale.TAIWAN), DateFormat.YYYY_MM_DD());
			calendarTo = DateUtils.getDateToday();
		}
		return calendarTo;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}

	public List<ORTransaction> getOrNumbers() {
		return orNumbers;
	}

	public void setOrNumbers(List<ORTransaction> orNumbers) {
		this.orNumbers = orNumbers;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public Customer getCustomerSelected() {
		return customerSelected;
	}

	public void setCustomerSelected(Customer customerSelected) {
		this.customerSelected = customerSelected;
	}

	public String getCitizenName() {
		return citizenName;
	}

	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
	}

	public Date getIssuedDate() {
		if(issuedDate==null){
			issuedDate = DateUtils.getDateToday();
		}
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getOrNumber() {
		if(orNumber==null) {
			/*String orNo =  ORTransaction.getLastORNumber();
			
			int incNo = Integer.valueOf(orNo);
			incNo += 1;
			String newNo = incNo+"";
			int newSize = newNo.length();
			
			switch(newSize) {
				
				case 7: orNumber=newNo; break;
				case 6: orNumber="0"+newNo; break;
				case 5: orNumber="00"+newNo; break;
				case 4: orNumber="000"+newNo; break;
				case 3: orNumber="0000"+newNo; break;
				case 2: orNumber="00000"+newNo; break;
			}*/
			orNumber = ORTransaction.getNewOrNumber();
		}
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getIssuedAddress() {
		if(issuedAddress==null){
			issuedAddress = BARANGAY + ", " + MUNICIPALITY + ", " + PROVINCE;
		}
		return issuedAddress;
	}

	public void setIssuedAddress(String issuedAddress) {
		this.issuedAddress = issuedAddress;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public List<Customer> getTaxpayers() {
		return taxpayers;
	}

	public void setTaxpayers(List<Customer> taxpayers) {
		this.taxpayers = taxpayers;
	}

	public String getSearchTaxpayerName() {
		return searchTaxpayerName;
	}

	public void setSearchTaxpayerName(String searchTaxpayerName) {
		this.searchTaxpayerName = searchTaxpayerName;
	}

	public ORTransaction getOrData() {
		return orData;
	}

	public void setOrData(ORTransaction orData) {
		this.orData = orData;
	}

	public int getStatId() {
		if(statId==0){
			statId=1;
		}
		return statId;
	}

	public void setStatId(int statId) {
		this.statId = statId;
	}

	public List getStats() {
		stats = new ArrayList<>();
		stats.add(new SelectItem(1, "Delivered"));
		stats.add(new SelectItem(2, "Cancelled"));
		
		return stats;
	}

	public void setStats(List stats) {
		this.stats = stats;
	}

	public List<String> getChecks() {
		return checks;
	}

	public void setChecks(List<String> checks) {
		this.checks = checks;
	}
	
}
