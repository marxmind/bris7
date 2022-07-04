package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Cedula;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Email;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.ORTransaction;
import com.italia.marxmind.bris.controller.Position;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.EmailType;
import com.italia.marxmind.bris.enm.Job;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 01/14/2018
 * @version 1.0
 *
 */
@Named("cedulaBean")
@ViewScoped
public class CedulaBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 56778951L;
	
	private Date dateFrom;
	private Date dateTo;
	private String searchParam;
	private List<Cedula> cedulas = Collections.synchronizedList(new ArrayList<Cedula>());

	private String cedulaNumber;
	private Date cedulaIssued;
	private String cedulaIssuedAddress;
	private int cedulaTypeId;
	private List cedulaTypes;
	
	private String tinNo;
	private double basicTax;
	private double grossTax;
	private double totalAmount;
	private String weight;
	private String hieght;
	private Customer customerSelected;
	private String citizenName;
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private Cedula cedulaData;
	
	private List<Customer> taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchTaxpayerName;
	
	private int cedId;
	private List ceds;
	
	private List<String> checks;
	
	@PostConstruct
	public void init(){
		cedulas = Collections.synchronizedList(new ArrayList<Cedula>());
		String[] params = new String[0];
		String sql = " AND ced.cisactive=1 ";
		
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
			
			sql += " AND ( cuz.fullname like '%"+ getSearchParam().replace("--", "") +"%'";
			sql += " OR ced.cedno like '%"+ getSearchParam().replace("--", "")  +"%') ";
			
		}else{
			sql += " AND (ced.cedate>=? AND ced.cedate<=?) ";
			params = new String[2];
			params[0] = DateUtils.convertDate(getDateFrom(), DateFormat.YYYY_MM_DD());//DateUtils.getCurrentYear() + "-01-01";
			params[1] = DateUtils.convertDate(getDateTo(), DateFormat.YYYY_MM_DD());//DateUtils.getCurrentYear() + "-12-31";
		}
		
		int cnt = 1;
		
		sql += " ORDER BY ced.cid DESC";
		cedulas = Cedula.retrieve(sql, params);
		
		/*for(Cedula ced : Cedula.retrieve(sql, params)){
			ced.setCnt(cnt++);
			cedulas.add(ced);
		}*/
	}
	
	/**
	 * Do not change this is used by clearance and MOOE module
	 */
	public void loadCedula(){
		loadSearch();
		/*cedulas = Collections.synchronizedList(new ArrayList<Cedula>());
		String sql = " AND (clz.clearissueddate>=? AND clz.clearissueddate<=?) AND clz.isactiveclearance=1 AND clz.cedulanumber is not null";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getDateTo(), DateFormat.YYYY_MM_DD());
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
			
			sql += " AND ( cuz.fullname like '%"+ getSearchParam().replace("--", "") +"%'";
			sql += " OR clz.cedulanumber like '"+ getSearchParam().replace("--", "")  +"<:>%') ";
			
		}
		
		int cnt = 1;
		Map<String, Clearance> mapCedula = Collections.synchronizedMap(new HashMap<String, Clearance>());
		
		//removing duplicate OR Number
		for(Clearance cl : Clearance.retrieve(sql, params)){
			
			String cedulaNo = cl.getCedulaNumber().split("<:>")[0];
			mapCedula.put(cedulaNo, cl);
			
		}
		
		Map<String, Clearance> sorted = new TreeMap<String, Clearance>(mapCedula);
		UserDtls CURRENT_USER = Login.getUserLogin().getUserDtls();
		for(Clearance cl : sorted.values()){
			Cedula ced = new Cedula();
			ced.setDateIssued(cl.getCedulaNumber().split("<:>")[1]);
			ced.setCedulaNo(cl.getCedulaNumber().split("<:>")[0]);
			ced.setCustomer(cl.getTaxPayer());
			ced.setCedulaType(1);
			ced.setIsActive(1);
			ced.setUserDtls(CURRENT_USER);
			ced = Cedula.checkSaveIfNotExist(ced);
			
			ced.setCnt(cnt);
			//ced.setId(cl.getId());
			
			cedulas.add(ced);
			cnt++;
		}*/
		
		
	}
	
	public void saveCedula(){
		Cedula ced = new Cedula();
		boolean isOk = true;
		if(getCedulaData()!=null){
			ced = getCedulaData();
		}else{
			ced.setIsActive(1);
		}
		
		if(getCustomerSelected()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Citizen name");
		}
		
		if(getHieght()==null || getHieght().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide height");
		}
		
		if(getWeight()==null || getWeight().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide weight");
		}
		
		if(getCedulaIssuedAddress()==null || getCedulaIssuedAddress().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Address");
		}
		
		if(getCedulaNumber()==null || getCedulaNumber().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide number");
		}
		
		if(getCedulaData()==null && getCustomerSelected()!=null){
			if(Cedula.isExistCedula(getCustomerSelected())){
				isOk = false;
				Application.addMessage(3, "Error", "Cedula information for " + getCustomerSelected().getFullname() + " is already recorded");
			}
		}
		
		if(isOk){
			
			if(ced.getId()==0) {
				sendSystemMail();
			}
			
			ced.setStatus(getCedId());
			ced.setCustomer(getCustomerSelected());
			ced.setTinNumber(getTinNo());
			ced.setHeight(getHieght());
			ced.setWeight(getWeight());
			
			ced.setBasicTax(getBasicTax());
			ced.setGrossTax(getGrossTax());
			ced.setTotalAmount(getTotalAmount());
			
			ced.setDateIssued(DateUtils.convertDate(getCedulaIssued(), DateFormat.YYYY_MM_DD()));
			ced.setCedulaNo(getCedulaNumber());
			ced.setCedulaType(getCedulaTypeId());
			ced.setIssuedAddress(getCedulaIssuedAddress());
			
			ced.setUserDtls(Login.getUserLogin().getUserDtls());
			
			ced.save();
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
					
					if(toUsers.size()>0) {
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
					e.setTitle("New Cedula Created for " + getCustomerSelected().getFullname());
					
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
					msg += "<p>Please see below details for the new created cedula</p>";
					msg += "<br/>";
					msg += "<p>Cedula No: <strong>" + getCedulaNumber()+"</strong></p>";
					msg += "<p>Name : <strong>" + getCustomerSelected().getFullname()+"</strong></p>";
					msg += "<p>Type: <strong>" + (getCedulaTypeId()==1? "Individual" : "Corporation") + "</strong></p>";
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
				e.setTitle("New Cedula Created for " + getCustomerSelected().getFullname());
				
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
				msg += "<p>Please see below details for the new created cedula</p>";
				msg += "<br/>";
				msg += "<p>Cedula No: <strong>" + getCedulaNumber()+"</strong></p>";
				msg += "<p>Name : <strong>" + getCustomerSelected().getFullname()+"</strong></p>";
				msg += "<p>Type: <strong>" + (getCedulaTypeId()==1? "Individual" : "Corporation") + "</strong></p>";
				String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + toMailUser;
				Email.emailSavePath(msg, fileName);
				e.setContendId(fileName);
				e.save();
			}
		}
	}
	
	public void deleteRow(Cedula ced){
		ced.delete();
		Application.addMessage(1, "Success", "Successfully deleted");
		clearFlds();
		init();
	}
	
	public void clearFlds(){
		setCedulaNumber(null);
		setCedulaIssued(null);
		setCedulaIssuedAddress(null);
		setCedulaTypeId(0);
		setCedulaData(null);
		
		setTinNo(null);
		setBasicTax(0.0);
		setGrossTax(0.0);
		setTotalAmount(0.0);
		setWeight(null);
		setHieght(null);
		setCustomerSelected(null);
		setCitizenName(null);
		checks = Collections.synchronizedList(new ArrayList<String>());
		setCedId(1);
	}
	
	/**
	 * Do not change this is used by clearance and MOOE module
	 */
	public void loadSearch(){
		
		cedulas = Collections.synchronizedList(new ArrayList<Cedula>());
		String sql = " AND (ced.cedate>=? AND ced.cedate<=?) AND ced.cisactive=1 ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getDateFrom(), DateFormat.YYYY_MM_DD());
		params[1] = DateUtils.convertDate(getDateTo(), DateFormat.YYYY_MM_DD());
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
			
			sql += " AND ( cuz.fullname like '%"+ getSearchParam().replace("--", "") +"%'";
			sql += " OR ced.cedno like '%"+ getSearchParam().replace("--", "")  +"%') ";
			
		}
		
		int cnt = 1;
		
		for(Cedula ced : Cedula.retrieve(sql, params)){
			ced.setCnt(cnt++);
			cedulas.add(ced);
		}
		
		
		
		
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
	
	public void clickItem(Cedula ced){
		setCedulaData(ced);
		setCustomerSelected(ced.getCustomer());
		
		setCedulaNumber(ced.getCedulaNo());
		setCedulaIssued(DateUtils.convertDateString(ced.getDateIssued(),DateFormat.YYYY_MM_DD()));
		setCedulaIssuedAddress(ced.getIssuedAddress());
		setCedulaTypeId(ced.getCedulaType());
		
		
		setTinNo(ced.getTinNumber());
		setBasicTax(ced.getBasicTax());
		setGrossTax(ced.getGrossTax());
		setTotalAmount(ced.getTotalAmount());
		setWeight(ced.getWeight());
		setHieght(ced.getHeight());
		setCedId(ced.getStatus());
		
		setCitizenName(ced.getCustomer().getFullname());
		
	}
	
	public void clickItemOwner(Customer cus){
		setCustomerSelected(cus);
		setCitizenName(cus.getFullname());
	}
	
	
	
	
	/**
	 * Do not change this is used by clearance and MOOE module
	 */
	public Date getDateFrom() {
		
		if(dateFrom==null){
			
			String date = DateUtils.getCurrentYear()+"";
			date += "-"+(DateUtils.getCurrentMonth()<=9? "0"+DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+"");
			date += "-01";
			dateFrom = DateUtils.convertDateString(date, DateFormat.YYYY_MM_DD());
			
		}
		
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	/**
	 * Do not change this is used by clearance and MOOE module
	 */
	public Date getDateTo() {
		
		if(dateTo==null){
			dateTo = DateUtils.convertDateString(DateUtils.getEndOfMonthDate(1, Locale.TAIWAN), DateFormat.YYYY_MM_DD());
			dateTo = DateUtils.getDateToday();
		}
		
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
	public String getSearchParam() {
		return searchParam;
	}
	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	public List<Cedula> getCedulas() {
		return cedulas;
	}
	public void setCedulas(List<Cedula> cedulas) {
		this.cedulas = cedulas;
	}

	public String getCedulaNumber() {
		if(cedulaNumber==null) {
			cedulaNumber = Cedula.getNewCedulaNumber();
		}
		return cedulaNumber;
	}

	public void setCedulaNumber(String cedulaNumber) {
		this.cedulaNumber = cedulaNumber;
	}

	public Date getCedulaIssued() {
		if(cedulaIssued==null){
			cedulaIssued = DateUtils.getDateToday();
		}
		return cedulaIssued;
	}

	public void setCedulaIssued(Date cedulaIssued) {
		this.cedulaIssued = cedulaIssued;
	}

	public String getCedulaIssuedAddress() {
		if(cedulaIssuedAddress==null){
			cedulaIssuedAddress = MUNICIPALITY + ", " + PROVINCE;
		}
		return cedulaIssuedAddress;
	}

	public void setCedulaIssuedAddress(String cedulaIssuedAddress) {
		this.cedulaIssuedAddress = cedulaIssuedAddress;
	}

	public int getCedulaTypeId() {
		if(cedulaTypeId==0){
			cedulaTypeId = 1;
		}
		return cedulaTypeId;
	}

	public void setCedulaTypeId(int cedulaTypeId) {
		this.cedulaTypeId = cedulaTypeId;
	}

	public List getCedulaTypes() {
		cedulaTypes = new ArrayList<>();
		
		cedulaTypes.add(new SelectItem(1, "Individual"));
		cedulaTypes.add(new SelectItem(2, "Corporation"));
		
		return cedulaTypes;
	}

	public void setCedulaTypes(List cedulaTypes) {
		this.cedulaTypes = cedulaTypes;
	}

	public Cedula getCedulaData() {
		return cedulaData;
	}

	public void setCedulaData(Cedula cedulaData) {
		this.cedulaData = cedulaData;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public double getBasicTax() {
		return basicTax;
	}

	public void setBasicTax(double basicTax) {
		this.basicTax = basicTax;
	}

	public double getGrossTax() {
		return grossTax;
	}

	public void setGrossTax(double grossTax) {
		this.grossTax = grossTax;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getHieght() {
		return hieght;
	}

	public void setHieght(String hieght) {
		this.hieght = hieght;
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

	public int getCedId() {
		if(cedId==0){
			cedId=1;
		}
		return cedId;
	}

	public void setCedId(int cedId) {
		this.cedId = cedId;
	}

	public List getCeds() {
		ceds = new ArrayList<>();
		
		ceds.add(new SelectItem(1, "Delivered"));
		ceds.add(new SelectItem(2, "Cancelled"));
		
		return ceds;
	}

	public void setCeds(List ceds) {
		this.ceds = ceds;
	}

	public List<String> getChecks() {
		return checks;
	}

	public void setChecks(List<String> checks) {
		this.checks = checks;
	}
	
}
