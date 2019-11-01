package com.italia.marxmind.bris.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.TabChangeEvent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.application.ApplicationFixes;
import com.italia.marxmind.bris.application.ApplicationVersionController;
import com.italia.marxmind.bris.application.DataMining;
import com.italia.marxmind.bris.controller.BCard;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Blotters;
import com.italia.marxmind.bris.controller.CardRpt;
import com.italia.marxmind.bris.controller.CaseFilling;
import com.italia.marxmind.bris.controller.Cases;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.DocumentPrinting;
import com.italia.marxmind.bris.controller.EducationTrans;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.IncidentReportTypes;
import com.italia.marxmind.bris.controller.Livelihood;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.LuponPerson;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.RacesTrans;
import com.italia.marxmind.bris.controller.ReligionTrans;
import com.italia.marxmind.bris.controller.ReportingPerson;
import com.italia.marxmind.bris.controller.SuspectedPerson;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.controller.VictimPerson;
import com.italia.marxmind.bris.enm.BlotterStatus;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.enm.Religion;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.CasesRpt;
import com.italia.marxmind.bris.reports.ClearanceRpt;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.security.Copyright;
import com.italia.marxmind.bris.security.License;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 * 
 * @author mark italia
 * @since 07/11/2017
 * @version 1.0
 *
 */
//@ManagedBean(name="mainBean", eager=true)
//@ViewScoped
@Named
//@org.omnifaces.cdi.ViewScoped
@SessionScoped
public class MainBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8667946754573341L;
	
	private List<Customer> customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
	private String searchCustomer;
	private Customer customer; 
	
	private String fullName;
	private String fullAddress;
	private String genderType;
	private String civilType;
	private String contactNo;
	private String bid;
	
	private String dateRegistered;
	private String birthdate;
	private int age;
	private String emergencyContactPersonName;
	private String relationship;
	private String photoId="tmp";
	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private List<Clearance> clearances = new ArrayList<Clearance>();//Collections.synchronizedList(new ArrayList<Clearance>());
	
	private boolean hideView1=true;
	private boolean hideView2 =false;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	
	private static final String IDCARD_REPORT_NAME = ReadXML.value(ReportTag.BID);
	private static final String CASES_REPORT_NAME = ReadXML.value(ReportTag.CASES_REPORT);
	private static final String BLOTTER_REPORT_NAME = ReadXML.value(ReportTag.BLOTTER);
	
	private List<SuspectedPerson> personCases = new ArrayList<SuspectedPerson>();//Collections.synchronizedList(new ArrayList<SuspectedPerson>());
	
	private List<Cases> summons = new ArrayList<Cases>();//Collections.synchronizedList(new ArrayList<Cases>());
	
	private boolean lessDetails=true;
	private boolean moreDetails =false;
	
	private List<Livelihood> businesses = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
	private List<BCard> cardTrans = new ArrayList<BCard>();//Collections.synchronizedList(new ArrayList<BCard>());
	
	private String stylePicture;
	
	private List<ReportingPerson> reportingPersons = new ArrayList<ReportingPerson>();//Collections.synchronizedList(new ArrayList<ReportingPerson>());
	private List<SuspectedPerson> suspectedPersons = new ArrayList<SuspectedPerson>();//Collections.synchronizedList(new ArrayList<SuspectedPerson>());
	private List<VictimPerson> victimPersons = new ArrayList<VictimPerson>();//Collections.synchronizedList(new ArrayList<VictimPerson>());
	private List<LuponPerson> luponPersons =new ArrayList<LuponPerson>();// Collections.synchronizedList(new ArrayList<LuponPerson>());
	
	private String religionName;
	private String educationName;
	private String raceName;
	
	private String aboutCitizen;
	private String profileContent;
	
	private String personDtls;
	
	private String searchOption="1";
	
	private void addPersonDetails() {
		
		String val = "";
		
		if(getCustomer()!=null) {
			
			Customer cus = getCustomer();
			
			val = "<p><strong>All about <strong>"+ cus.getFirstname() +"'s</strong> Information</strong></p>";
			val += "<br/>";
			val += "<p>Assigned Barangay ID: <strong>"+ cus.getCardno() +"</strong></p>";
			val += "<p>Full Name: <strong>"+ cus.getLastname().toUpperCase() +", " + cus.getFirstname().toUpperCase() +" y " + cus.getMiddlename().toUpperCase() +"</strong></p>";
			val += "<p>Address: <strong>"+ cus.getCompleteAddress() +"</strong></p>";
			val += "<p>Birthday: <strong>"+ cus.getBirthdate() +" ("+ Math.round(DateUtils.calculateAgeNow(cus.getBirthdate())) + ")</strong></p>";
			val += "<p>Contact No: <strong>"+ (cus.getContactno().isEmpty()? "N/A" : cus.getContactno()) +"</strong></p>";
			////////////////////////////////////////////////////DOCUMENTS////////////////////////
			
			//clearance transactions
			String sql = " AND clz.isactiveclearance=1 AND cuz.fullname=? order by clz.clearid DESC limit 100";
			String[] params = new String[1];
			params[0] = cus.getFullname();
			List<Clearance> clrs = Clearance.retrieve(sql, params);
			
			if(clrs!=null && clrs.size()>0) {
				val += "<br/>";
				val += "<p>Requested Documents (" + clrs.size() + ")</p>";
				val += "<p>Purpose:</p>";
				int cnt=1;
				for(Clearance clr : clrs) {
					
					val += "<ul>";
					val += "<li>"+cnt++ +") <strong> "+ DateUtils.convertDateToMonthDayYear(clr.getIssuedDate()) + " - " + clr.getPurposeName() +"</strong></li>";
					val += "</ul>";
					
				}
			
			}
			
			
			////////////////////////////////////ID//////////////////
			sql = " AND crd.isactivebid=1 AND cuz.fullname=? ORDER BY crd.tranid DESC";
			params = new String[1];
			params[0] = getCustomer().getFullname();
			List<BCard> cards = BCard.retrieve(sql, params); 
			if(cards!=null && cards.size()>0) {
				val += "<br/>";
				val += "<p>Assigned ID No: <strong>" + cus.getCardno().toUpperCase() + "</strong></p>";
				int cnt=1;
				val += "<p>Validity:</p>";
				for(BCard card : cards) {
					val += "<ul>";
					val += "<li>"+cnt++ +") <strong> "+ DateUtils.convertDateToMonthDayYear(card.getValidDateFrom()).toUpperCase()  +" - " + DateUtils.convertDateToMonthDayYear(card.getValidDateTo()).toUpperCase() +"</strong></li>";
					val += "</ul>";
				}
			}
			
			
			///////////////BUSINESS//////////////////////
			sql = " AND live.isactivelive=1 AND cuz.fullname=? ORDER BY live.livelihoodid DESC";
			params = new String[1];
			params[0] = getCustomer().getFullname();
			List<Livelihood> lvs = Livelihood.retrieve(sql, params);
			
			if(lvs!=null && lvs.size()>0) {
				val += "<br/>";
				val += "<p>Source of Income (" + lvs.size() + ")</p>";
				int cnt=0;
				
				boolean isbiz=false;
				String biz = "";
				for(Livelihood lv : lvs){
					if(lv.getTypeLine()==1){
						String name = lv.getBusinessName() + "(" + lv.getAreaMeter() +", " + lv.getSupportingDetails() + ")" + " - " + lv.getPurokName();
						biz += "<ul>";
						biz += "<li>" + name + "</li>";
						biz += "</ul>";
						isbiz=true;
						cnt++;
					}
				}
				
				if(isbiz) {
					val += "<p>Fish Cage/s ("+ cnt +")</p>";
					val += biz;
				}
				
				cnt=0;
				isbiz=false;
				biz = "";
				for(Livelihood lv : lvs){
					
					if(lv.getTypeLine()!=1){
						String name = lv.getBusinessName() + " - " + lv.getPurokName();
						biz += "<ul>";
						biz += "<li>" + name + "</li>";
						biz += "</ul>";
						isbiz=true;
						cnt++;
					}
					
				}
				
				if(isbiz) {
					val += "<p>Business ("+ cnt +")</p>";
					val += biz;
				}
			}
			
			
			///CASES//////////////////////////////////////////////
			sql = " AND ciz.caseisactive=1 ";
			params = new String[0];
			
			String fullName = getCustomer().getFullname();
			sql += " AND ( ciz.respondents like '%"+fullName+"%' ";
			sql += " OR ciz.respondents like '%"+fullName+",' ";
			sql += " OR ciz.respondents like ',"+fullName+"%' )";
			List<Cases> css = Cases.retrieve(sql, params); 
			if(css!=null && css.size()>0) {
				val += "<br/>";
				val += "<p>Case/s ("+css.size()+")</p>";
				for(Cases ciz : css){
					sql = " AND ciz.caseisactive=1 AND ciz.caseid=? ";
					params = new String[1];
					params[0] = ciz.getId()+"";
					val += "<p>Case No: <strong>" + ciz.getCaseNo()+"</strong></p>";
					val += "<p>Kind: <strong>" + ciz.getKindName()+"</strong></p>";
					val += "<p>Status: <strong>" + CaseStatus.typeName(ciz.getStatus())+"</strong></p>";
					val += "<p>Complainant/s: <strong>" + ciz.getComplainants() +"</strong></p>";
					val += "<p>Details of Complain: <strong>" + ciz.getNarratives() +"</strong></p>";
					val += "<p>Forms</p>";
					List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
					for(CaseFilling fil : fils) {
						val += "<ul>";
						val += "<li>" + DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()) + " - " + fil.getFormName() + "</li>";
						val += "</ul>";
					}
					
				}
			}
			
			
			//Link name
			sql = " AND cus.cusisactive=1 AND (cus.cuslastname=? OR cus.cusmiddlename=? OR cus.cuslastname=? OR cus.cusmiddlename=?) AND cus.fullname!=?";
			params = new String[5];
			params[0] = cus.getLastname();
			params[1] = cus.getLastname();
			params[2] = cus.getMiddlename();
			params[3] = cus.getMiddlename();
			params[4] = cus.getFullname();
			List<Customer> cuzs = Customer.retrieve(sql, params);
			Map<String, Customer> rels = new HashMap<String, Customer>();//Collections.synchronizedMap(new HashMap<String, Customer>());
			if(cuzs!=null && cuzs.size()>0) {
				val += "<br/>";
				val += "<p>Relatives and Link names ("+cuzs.size()+")</p>";
				for(Customer cz : cuzs) {
					rels.put(cz.getFullname(), cz);
				}
			}
			Map<String, Customer> relTree = new TreeMap<String, Customer>(rels);
			for(Customer cs : relTree.values()) {
				val += "<ul>";
				val += "<li>" + cs.getFullname() + "</li>";
				val += "</ul>";
			}
			
		}
		
		
		
		if(val==null || val.isEmpty()) {
			setPersonDtls(null);
			val = "<p><h1>Hi There, welcome to BRIS Software, your portal for Barangay Information</h1></p>";
		}
		
		setPersonDtls(val);
		
	}
	
	public void pictureDrop(DragDropEvent dEvent){
		Customer cus = ((Customer) dEvent.getData());
		clickItem(cus);
	}
	
	public void clearImageDrop(DragDropEvent dEvent){
		backView();
	}
	
	@PostConstruct
	public void init(){
		
		String keywords = getSearchCustomer();
		String sql = " AND cus.cusisactive=1 ";
		System.out.println("check search... " + getSearchCustomer());
		if(getSearchCustomer()!=null && !getSearchCustomer().isEmpty()){
		int size = getSearchCustomer().length();
			if(size>=4){
				customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
				sql += " AND (cus.fullname like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " OR  cus.cuscardno like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " OR prov.provname like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " OR mun.munname like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " OR bar.bgname like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " OR pur.purokname like '%" + getSearchCustomer().replace("--", "") +"%' ";
				sql += " )";
				sql += " order by cus.customerid DESC ";
				customers = Customer.retrieve(sql, new String[0]);
			}	
		}else{
			customers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
			sql += " order by cus.customerid DESC limit 4;";
			customers = Customer.retrieve(sql, new String[0]);
		}
		
		//business search
		
		if(customers!=null && customers.size()==0 && (getSearchCustomer()!=null && !getSearchCustomer().isEmpty())){
			
			sql = " AND live.isactivelive=1 ";
			sql += " AND ( live.livename like '%"+ getSearchCustomer().replace("--", "") +"%'";
			sql += " OR live.liveareameter like '%"+ getSearchCustomer().replace("--", "") +"%'";
			sql += " OR live.livedetails like '%"+ getSearchCustomer().replace("--", "") +"%' )";
			
			Map<Long, Customer> mapCsutomer = new HashMap<Long, Customer>();//Collections.synchronizedMap(new HashMap<Long, Customer>());
			for(Livelihood liv : Livelihood.retrieve(sql, new String[0])){
				if(mapCsutomer==null){
					Customer customer = Customer.retrieve(liv.getTaxPayer().getCustomerid());
					customers.add(customer);
					mapCsutomer.put(liv.getTaxPayer().getCustomerid(), customer);
				}else{
					
					if(!mapCsutomer.containsKey(liv.getTaxPayer().getCustomerid())){
						Customer customer = Customer.retrieve(liv.getTaxPayer().getCustomerid());
						customers.add(customer);
						mapCsutomer.put(liv.getTaxPayer().getCustomerid(), customer);
					}
					
				}
				
				
			}
			
		}
		
		setHideView1(true);
		setHideView2(false);
		setLessDetails(true);
		setMoreDetails(false);
		photoId="tmp";
		
		if(customers!=null && customers.size()==1){
			setStylePicture("text-align:center;background-color:red;");
			
			Customer cuz = customers.get(0);
			
			if(Features.isEnabled(Feature.BLOTTERS)){
				//checkReportedIncident(cuz);
				checkSummons(cuz);
			}else{
				setStylePicture("text-align:center;background-color:transparent;");
			}
			
			clickItemProfile(customers.get(0));
			
		}else{
			setStylePicture("text-align:center;background-color:transparent;");
			clearFlds();
			
		}
		
		//locate open/active cases close/settled cases
		if(customers==null || customers.size()==0){
			if("active case".equalsIgnoreCase(keywords) || "open case".equalsIgnoreCase(keywords) 
					|| "close case".equalsIgnoreCase(keywords) || "settled case".equalsIgnoreCase(keywords)
					|| "endorsed case".equalsIgnoreCase(keywords) || "endorse case".equalsIgnoreCase(keywords)
					) {
				
				sql = " AND ciz.caseisactive=1 ";
				
				if("active case".equalsIgnoreCase(keywords) || "open case".equalsIgnoreCase(keywords)) { 
					sql += "AND (ciz.casestatus="+ CaseStatus.NEW.getId() +" OR ciz.casestatus="+ CaseStatus.ON_HOLD.getId()  +" OR ciz.casestatus="+ CaseStatus.IN_PROGRESS.getId() +")";
				}else if("close case".equalsIgnoreCase(keywords) || "settled case".equalsIgnoreCase(keywords)) { 
					sql += "AND (ciz.casestatus="+ CaseStatus.SETTLED.getId()+")";
				}else if("endorsed case".equalsIgnoreCase(keywords) || "endorse case".equalsIgnoreCase(keywords)) { 
					sql += "AND (ciz.casestatus="+ CaseStatus.MOVED_IN_HIGHER_COURT.getId()+")";
				}
				
				String[] params = new String[0];
				List<Cases> css = Cases.retrieve(sql, params); 
				Map<String, Customer> cusMap = new HashMap<String, Customer>();//Collections.synchronizedMap(new HashMap<String, Customer>());
				if(css!=null && css.size()>0) {
					for(Cases ciz : css){
						sql = " AND cus.fullname like '%"+ ciz.getRespondents() + "%'";
						params = new String[0];
						List<Customer> cxz = Customer.retrieve(sql, params);
						if(cxz!=null && cxz.size()>0) {
							cusMap.put(cxz.get(0).getFullname(), cxz.get(0));
							
						}
					}
					//load filter citizen
					for(Customer cs : cusMap.values()) {
						customers.add(cs);
					}
				}
				
			}
		}
		
		if(customers==null || customers.size()==0){
			
			String[] res = {"Ooppsss!, Something went wrong with your search words","<strong>\""+keywords+"\"</strong> is not yet recorded","The keyword <strong>\""+keywords+"\"</strong> is not found or not yet recorded"};
			
			String note = "";
			note = "<p><h1>"+ res[(int)(Math.random() * res.length)] +"</h1></p><br/>";
			note += "<p>Tips</p>";
			note +="<ul>";
			note +="<li>You can search using the name of the citizen(first name, last name)</li>";
			note +="<li>Business Name</li>";
			note +="<li>Province, Municipality, Barangay, Purok, Sitios or Zone</li>";
			note +="<li>Barangay ID</li>";
			note +="</ul>";
			note +="<br/>";
			note +="<p>Type <strong>help</strong> for other related keywords for searching an information</p>";
			setPersonDtls(note);
		}else if(customers!=null && customers.size()>1){
			String note = "<p><h1>Search result/s ("+ customers.size() +") "+ (keywords==null? "" : " from your search keyword <strong>" + keywords+"</strong>") +"</h1></p>";
			note += "<br/>";
			note += "<p>Did you know that?</p>";
			String[] tips = {"Click <strong>Profile</strong> - To show more about the citizen information","Click <strong>Transactions</strong> - To see the transaction of the citizen like Requested Clearance, Certificate, Owned business, ID Requested or Log Summons or Cases Reported", 
					"<strong>List of Created Barangay ID</strong> tab on your right side is displaying recent Generated Barangay ID. This tab is also use for searching of previously created ID. To search use the name of the citizen or search by Month using the keyword like <strong>" + DateUtils.getMonthName(DateUtils.getCurrentMonth()) +" "+ DateUtils.getCurrentYear()+"</strong>",
					"<strong>List of Created Documents</strong> tab on your right side is displaying recent documents transacted. To search, type the name of the citizen",
					"<strong>List of Cases</strong> tab on your right side is displaying new case or pending case. To search, type the name of the complainant or respondent",
					"<strong>Calendar of Activities</strong> tab on your right side is use for displaying scheduled event or you can assign new calendar of event",
					"<strong>BRIS</strong> is the portal of Barangay in terms of Barangay Informations regarding issuance of Documents, writing of cheques, tracking of dispense cheques, total amount collected in transacted Clearance or Certification",
					"If you have found error or wrong behavior of the <strong>BRIS Software</strong>, please don't hesitate to contact the developer at e-mail <strong>itsmarxmind@gmail.com</strong> or call at <strong>09175121252</strong> for immediate response.",
					"At <strong>BRIS</strong> we ensure that the information is secured and maintained accordingly by our developer",
					"<strong>BRIS Software</strong> is the Portal for Barangay Information",
					"For problem encountered call <strong>MARXMIND</strong> hotline at <strong>09175121252</strong> to address your problem."};
			note +="<p>"+ tips[(int)(Math.random() * tips.length)] +"</p>";
			setPersonDtls(note);
		}
		
		//if help keyword is type
				if(customers==null || customers.size()==0){
					if("help".equalsIgnoreCase(keywords)) {
						String note = ""; 
						
						
						Copyright app = appInfo();
						note += "<p>"+app.getAuthor()+" BRIS "+app.getAppname()+" [version "+ app.getCurrentversion()+"]</p>";
						note += "<p>"+app.getCopyrightname()+"</p>";
						List<License> lic = appLicense();
						note += "<p>Application expiration <strong>"+lic.get(0).getMonthExpiration()+"</strong></p>";
						note +="<br/>";
						
						note += "<p>"+getGreetings()+"</p>";
						note += "<p>Welcome to BRIS help module</p>";
						note += "<p>Here are the list of keyword to use when searching specific information";
						note +="<br/>";
						note +="Search keyword";
						note +="<ul>";
						note +="<li>Type the fullname of the citizen</li>";
						note +="<li>Type the business name to display the name of the citizen</li>";
						note +="<li>Type Province Name, Municipality Name, Barangay Name, Purok, Sitio or Zone name to display citizen information</li>";
						note +="</ul>";
						
						note +="<br/>";
						note +="Cases or Summon Information";
						note +="<ul>";
						note +="<li>Type <strong>open case</strong> or <strong>active case</strong> for Cases that are not yet settled, on-going or hold cases</li>";
						note +="<li>Type <strong>close case</strong> or <strong>settled case</strong> for Cases that were settled</li>";
						note +="<li>Type <strong>endorsed case</strong> for Cases that already endorsed to higher court</li>";
						note +="<li>Type <strong>sc case</strong> to show scheduled settlement</li>";
						note +="<li>Type <strong>settlement</strong> to show today settlement</li>";
						note +="</ul>";
						
						note +="<br/>";
						note +="Other Keywords";
						note +="<ul>";
						note +="<li><strong>info</strong> to show the current status of Barangay Information</li>";
						note +="<li><strong>activities</strong> to show scheduled activities</li>";
						note +="<li><strong>staff</strong> to display all staff of barangay including resigned staff</li>";
						note +="</ul>";
						
						setPersonDtls(note);
					
						//if sc case keyword is type	
					}else if("sc case".equalsIgnoreCase(keywords)) {
						setPersonDtls(DataMining.casesInfo());
					}else if("settlement".equalsIgnoreCase(keywords)) {
						setPersonDtls(DataMining.settlementInfo());
					}else if("activities".equalsIgnoreCase(keywords)) {
						setPersonDtls(DataMining.activitiesInfo());
					}else if("staff".equalsIgnoreCase(keywords)) {
						setPersonDtls(DataMining.staffInfo());
					}else if("info".equalsIgnoreCase(keywords)) {
						setPersonDtls(DataMining.infoAll());
					}
					
				}
				
				
				
		
	}
	

	
	private void checkSummons(Customer cuz){
		
		String name = cuz.getFullname();
		int gender = Integer.valueOf(cuz.getGender());
		String[] mrmrs = {"", "Mr.", "Miss","Mrs."};
		String[] heshe = {"","He","She"};
		String[] hisher = {"","His","Her"};
		String[] himher = {"","Him","Her"};
		
		if(CivilStatus.MARRIED.getId()==cuz.getCivilStatus()){
			if(gender==2){
				name =  mrmrs[gender+1] +" " + cuz.getFullname();
			}else{
				name =  mrmrs[gender] +" " + cuz.getFullname();
			}
			
		}else{
		
			name = mrmrs[gender] +" " + cuz.getFullname();
		
		}
		
		String sql = " AND ciz.caseisactive=1 AND (ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?)";
		String[] params = new String[3];
		params[0] = CaseStatus.SETTLED.getId()+"";
		params[1] = CaseStatus.CANCELLED.getId()+"";
		params[2] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		
		String fullName = cuz.getFullname();
		sql += " AND ( ciz.respondents like '%"+fullName+"%' ";
		 sql += " OR ciz.respondents like '%"+fullName+",' ";
		 sql += " OR ciz.respondents like ',"+fullName+"%' )";
		
		
		Map<Long, Cases> caseMap = new HashMap<Long, Cases>();//Collections.synchronizedMap(new HashMap<Long, Cases>());
		boolean hasPendingCase = false;
		String report = "";
		for(Cases cz : Cases.retrieve(sql, params)){
			caseMap.put(cz.getId(), cz);
			hasPendingCase = true;
		}
		
		
		
		if(hasPendingCase){
			
		int caseCount = 1;//caseMap.size();
		
		for(Cases kiz : caseMap.values()){
			if(caseCount==1){
				report += name + " reported " + himher[gender].toLowerCase() + " on " + DateUtils.convertDateToMonthDayYear(kiz.getDate()) + " for " + 
						CaseKind.typeName(kiz.getKind()) + " and the case was tag as " + CaseStatus.typeName(kiz.getStatus()) + ".";
				
			}
			if(caseCount==2){
				report += " Another case was reported by " + " reported " + himher[gender].toLowerCase() + " on " + DateUtils.convertDateToMonthDayYear(kiz.getDate()) + " for " + 
						CaseKind.typeName(kiz.getKind()) + " and the case was tag as " + CaseStatus.typeName(kiz.getStatus()) + ".";
				
			}
			if(caseCount==3){
				report += " And also reported by " + name + " on " + DateUtils.convertDateToMonthDayYear(kiz.getDate()) + " for " + 
						CaseKind.typeName(kiz.getKind()) + " and the case was tag as " + CaseStatus.typeName(kiz.getStatus()) + ". ";
				
			}
			caseCount++;
		}
		caseCount -=4;
		if(caseCount==1){
			report += " And One more case reported also.";
		}else{
			if(caseCount>1){
				report += " And there were " + caseCount + " cases also reported for " + himher[gender].toLowerCase()+".";
			}
		}
		
		
		Application.addMessage(2,"Case Report",report);
		
		}else{//has pending case
			setStylePicture("text-align:center;background-color:transparent;");
		}	
		
	}
	
	/**
	 * 
	 * @This was changed by method name checkSummons(Customer cuz)
	 */
	@Deprecated
	private void checkReportedIncident(Customer cuz){
		String name = cuz.getFullname();
		int gender = Integer.valueOf(cuz.getGender());
		String[] mrmrs = {"", "Mr.", "Miss","Mrs."};
		String[] heshe = {"","He","She"};
		String[] hisher = {"","His","Her"};
		String[] himher = {"","Him","Her"};
		
		if(CivilStatus.MARRIED.getId()==cuz.getCivilStatus()){
			if(gender==2){
				name =  mrmrs[gender+1] +" " + cuz.getFullname();
			}else{
				name =  mrmrs[gender] +" " + cuz.getFullname();
			}
			
		}else{
		
			name = mrmrs[gender] +" " + cuz.getFullname();
		
		}
		String sql = " AND ctz.customerid=? AND ctz.cusisactive=1 AND blot.isactiveblot=1 AND blot.blotstatus!=?";
		String[] params = new String[2];
		params[0] = cuz.getCustomerid()+"";
		params[1] = BlotterStatus.CLOSED.getId()+"";
		
		
		Map<Long, Blotters> blotMap = new HashMap<Long, Blotters>();//Collections.synchronizedMap(new HashMap<Long, Blotters>());
		boolean hasPendingCase = false;
		for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
			blotMap.put(sus.getBlotters().getId(), sus.getBlotters());
			hasPendingCase = true;
		}
		
		if(hasPendingCase){
			
		
		int sizeMap = blotMap.size();
		
		if(sizeMap>0){
			int cnt = 1;
			sql = " AND (";
			for(Blotters blot : blotMap.values()){
				if(cnt==sizeMap){
					sql += " blot.blotid=" + blot.getId() + " )";
				}else{
					sql += " blot.blotid=" + blot.getId() +" OR ";
				}
				
				cnt++;
			}
		}
		params = new String[0];
		sql += " AND blot.isactiveblot=1 AND rps.isactiverep=1 ";
		String report = "";
		Map<Long, ReportingPerson> repMap = new HashMap<Long, ReportingPerson>();//Collections.synchronizedMap(new HashMap<Long, ReportingPerson>());
		int caseCount = 1;
		for(ReportingPerson rep : ReportingPerson.retrieve(sql, params)){
			
			long id = rep.getBlotters().getId();
			
			if(repMap.containsKey(id)){
			
			}else{	
				String reportingName="";
				Customer cz = rep.getReportingPerson();
				int repgender = Integer.valueOf(cz.getGender());
				if(CivilStatus.MARRIED.getId()==cuz.getCivilStatus()){
					if(repgender==2){
						reportingName =  mrmrs[repgender+1] +" " + cz.getFullname();
					}else{
						reportingName =  mrmrs[repgender] +" " + cz.getFullname();
					}
					
				}else{
					reportingName = mrmrs[repgender] +" " + cz.getFullname();
				}
				if(caseCount==1){
					report += reportingName + " reported " + himher[gender].toLowerCase() + " on " + rep.getBlotters().getIncidentDate() + " for " + 
					IncidentReportTypes.retrieve(rep.getBlotters().getIncidentType()).getName() + " and the case was tag as " + BlotterStatus.typeName(rep.getBlotters().getStatus()) + ".";
					
				}
				if(caseCount==2){
					report += " Another case was reported by " + reportingName + " on " + rep.getBlotters().getIncidentDate() + " for " + 
							IncidentReportTypes.retrieve(rep.getBlotters().getIncidentType()).getName() + " and the case was tag as " + BlotterStatus.typeName(rep.getBlotters().getStatus()) + ". ";
					
				}
				if(caseCount==3){
					report += " And also reported by " + reportingName + " on " + rep.getBlotters().getIncidentDate() + " for " + 
							IncidentReportTypes.retrieve(rep.getBlotters().getIncidentType()).getName() + " and the case was tag as " + BlotterStatus.typeName(rep.getBlotters().getStatus()) + ". ";
					
				}
			}
			repMap.put(id, rep);
			caseCount++;
					
		}
		caseCount -=4;
		if(caseCount==1){
			report += " And One more case reported also.";
		}else{
			if(caseCount>1){
				report += " And there were " + caseCount + " cases also reported for " + himher[gender].toLowerCase()+".";
			}
		}
		
		
		Application.addMessage(2,"Incident Report",report);
		
		}else{//has pending case
			setStylePicture("text-align:center;background-color:transparent;");
		}	
	}
	
	public void details(String det){
		if("LESS".equalsIgnoreCase(det)){
			setLessDetails(true);
			setMoreDetails(false);
		}else{
			setLessDetails(false);
			setMoreDetails(true);
		}
	}
	
	public void clickItem(Customer cus){
		
		aboutCitizen(cus);
		setCustomer(cus);
		setFullName(cus.getFullname());
		
		if(cus.getPhotoid()!=null){
			copyPhoto(cus.getPhotoid());
		}
		
		if(Features.isEnabled(Feature.CLEARANCE)){
			loadClearanceTransactions();
		}
		
		loadAddInformation(cus);	
		
		setHideView1(false);
		setHideView2(true);
		
		
	}
	
	public void clickItemProfile(Customer cus){
		
		aboutCitizen(cus);
		setCustomer(cus);
		setFullName(cus.getFullname());
		
		if(cus.getPhotoid()!=null){
			copyPhoto(cus.getPhotoid());
		}
		
		loadAddInformation(cus);
		
		setHideView1(false);
		setHideView2(true);
		addPersonDetails();
	}	
	
	public void loadAddInformation(Customer customer){
		
		String about = getAboutCitizen();
		about += " Additional Inforomation: ";
		boolean isExist = false;
		String[] params = new String[1];
		params[0] = customer.getCustomerid()+"";
		String sql = " AND trn.isactiverel=1 AND trn.ispresentrel=1 AND cz.customerid=?";
		List<ReligionTrans> rels = ReligionTrans.retrieve(sql, params);
		if(rels.size()>0){
			//setReligionName(Religion.typeName(rels.get(0).getReligionId()));
			about += "Practicing "+Religion.typeName(rels.get(0).getReligionId()) + " religion.";
			isExist = true;
		}
		
		params = new String[1];
		sql = " AND trn.isactiveedtran=1 AND trn.ispresented=1 AND cz.customerid=?";
		params[0] = customer.getCustomerid()+"";
		List<EducationTrans> eds = EducationTrans.retrieve(sql, params);
		if(eds.size()>0){
			//setEducationName(eds.get(0).getEducation().getName());
			about += " Educational level is "+eds.get(0).getEducation().getName() + ".";
			isExist = true;
		}
		
		params = new String[1];
		sql = " AND trn.isactiveracetrans=1 AND cz.customerid=?";
		params[0] = customer.getCustomerid()+"";
		List<RacesTrans> rcs = RacesTrans.retrieve(sql, params);
		if(rcs.size()>0){
			//setRaceName(rcs.get(0).getRaces().getName());
			if(rcs.get(0).getRaces().getIsIndigent()==1){
				about += " Member of "+rcs.get(0).getRaces().getName() + " ethnic group.";
			}else{
				about += " Member of "+rcs.get(0).getRaces().getName() + " descendance.";
			}
			isExist = true;
		}
		if(isExist){
			setAboutCitizen(about);
		}
	}
	
	public void backView(){
		//clearFlds();
		//init();
	}
	
	public void onTabChangeView(TabChangeEvent event) {
		
		if("Documents".equalsIgnoreCase(event.getTab().getTitle())){
			if(Features.isEnabled(Feature.CLEARANCE)){
				loadClearanceTransactions();
			}
		}else if("Business".equalsIgnoreCase(event.getTab().getTitle())){
			if(Features.isEnabled(Feature.BUSINESS)){
				loadBusiness();
			}
		}else if("Cards".equalsIgnoreCase(event.getTab().getTitle())){
			if(Features.isEnabled(Feature.ID_GENERATION)){
				loadCards();
			}
		}else if("Case Recorded".equalsIgnoreCase(event.getTab().getTitle())){
			if(Features.isEnabled(Feature.BLOTTERS)){
				//loadBlotters();
				loadCases();
			}
		}
		
	}
	
	public void clearFlds(){
		setAboutCitizen(null);
		setFullName(null);
		setFullAddress(null);
		setGenderType(null);
		setCivilType(null);
		setContactNo(null);
		setBid(null);
		setDateRegistered(null);
		setBirthdate(null);
		setAge(0);
		setEmergencyContactPersonName(null);
		setRelationship(null);
		setCustomer(null);
		setSearchCustomer(null);
		
		setReligionName(null);
		setEducationName(null);
		setRaceName(null);
		
		clearances = new ArrayList<Clearance>();//Collections.synchronizedList(new ArrayList<Clearance>());
		businesses = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
		cardTrans = new ArrayList<BCard>();//Collections.synchronizedList(new ArrayList<BCard>());
	}
	
	public void loadClearanceTransactions(){
		
		Customer cus = getCustomer();
		clearances = new ArrayList<Clearance>();//Collections.synchronizedList(new ArrayList<Clearance>());
		if(getCustomer()!=null){
			String sql = " AND clz.isactiveclearance=1 AND cuz.fullname=? order by clz.clearid DESC limit 100";
			String[] params = new String[1];
			params[0] = cus.getFullname();
			
			clearances = Clearance.retrieve(sql, params);
			
		}
	}
	
	public void loadBusiness(){
		businesses = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
		
		if(getCustomer()!=null){
		String sql = " AND live.isactivelive=1 AND cuz.fullname=? ORDER BY live.livelihoodid DESC";
		String[] params = new String[1];
		params[0] = getCustomer().getFullname();
		
		for(Livelihood lv : Livelihood.retrieve(sql, params)){
			
			if(lv.getTypeLine()==1){
				String name = lv.getBusinessName() + "(" + lv.getAreaMeter() +", " + lv.getSupportingDetails() + ")";
				lv.setBusinessLabel(name);
			}else{
				lv.setBusinessLabel(lv.getBusinessName());
			}
			
			businesses.add(lv);
		}
		
		}
	}
	
	public void loadCards(){
		
		cardTrans = new ArrayList<BCard>();//Collections.synchronizedList(new ArrayList<BCard>());
		
		String sql = " AND crd.isactivebid=1 AND cuz.fullname=? ORDER BY crd.tranid DESC";
		String[] params = new String[1];
		params[0] = getCustomer().getFullname();
		
		cardTrans = BCard.retrieve(sql, params);
	}
	
	/**
	 * change by method loadCases()
	 */
	@Deprecated
	public void loadBlotters(){
		personCases = new ArrayList<SuspectedPerson>();//Collections.synchronizedList(new ArrayList<SuspectedPerson>());
		
		String sql = " AND ctz.customerid=? AND ctz.cusisactive=1 AND blot.isactiveblot=1";
		String[] params = new String[1];
		params[0] = getCustomer().getCustomerid()+"";
		
		for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
			sus.getBlotters().setIncidentName(IncidentReportTypes.retrieve(sus.getBlotters().getIncidentType()).getName());
			sus.getBlotters().setStatusName(BlotterStatus.typeName(sus.getBlotters().getStatus()));
			Blotters blot = sus.getBlotters();
			Blotters blot2 = Blotters.retrieveIncidentInformation(blot.getId());
			blot.setIncidentDetails(blot2.getIncidentDetails());
			blot.setIncidentSolutions(blot2.getIncidentSolutions());
			
			sus.setBlotters(blot);
			personCases.add(sus);
		}
		
		Collections.reverse(personCases);
	}
	public void loadCases(){
		summons = new ArrayList<Cases>();//Collections.synchronizedList(new ArrayList<Cases>());
		
		String sql = " AND ciz.caseisactive=1 ";
		String[] params = new String[0];
		
		String fullName = getCustomer().getFullname();
		sql += " AND ( ciz.respondents like '%"+fullName+"%' ";
		sql += " OR ciz.respondents like '%"+fullName+",' ";
		sql += " OR ciz.respondents like ',"+fullName+"%' )";
		
		for(Cases ciz : Cases.retrieve(sql, params)){
			sql = " AND ciz.caseisactive=1 AND ciz.caseid=? ";
			params = new String[1];
			params[0] = ciz.getId()+"";
			
			List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
			ciz.setCaseFilling(fils);
			summons.add(ciz);
		}
		
	}
	
	private void copyPhoto(String photoId){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		setPhotoId(photoId);
		String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
           
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
	}
	
	
public void printClearance(Clearance clr){
	
	Map<Integer, Object> obj = DocumentPrinting.printRequestedDocument(clr);
	String path = (String)obj.get(1);
	String REPORT_NAME = (String)obj.get(2);
	String jrxmlFile = (String)obj.get(3);
	HashMap param = (HashMap)obj.get(4);
	JRBeanCollectionDataSource beanColl = (JRBeanCollectionDataSource)obj.get(5);
	
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
	
	private String copyPhotoTaxpayer(String photoId){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		//String image_full_path = "";
        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        //String imageFolder =  File.separator + "images" + File.separator + "photocam" + File.separator;    
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            //image_full_path = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator + photoId + ".jpg";
            return driveImage;
	}
	
	public void printCard(BCard card){
		
		
		List<CardRpt> rpts = new ArrayList<CardRpt>();//Collections.synchronizedList(new ArrayList<CardRpt>());
		
		String sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 AND pos.posid=?";
		String[] params = new String[1];
		params[0] = Positions.CAPTAIN.getId()+"";
		
		Employee emp = Employee.retrieve(sql, params).get(0);
		String captain =  emp.getFirstName().toUpperCase() + " " + emp.getMiddleName().substring(0,1).toUpperCase() + ". " + emp.getLastName().toUpperCase();
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
			
			CardRpt rpt = new CardRpt();
			Customer tax = card.getTaxpayer();
			tax = Customer.customer(tax.getCustomerid());
			//String address = tax.getPurok().getPurokName() + ", " + tax.getBarangay().getName() + ", " + tax.getMunicipality().getName() + ", " + tax.getProvince().getName();
			String address = tax.getCompleteAddress();
			Customer emerPerson = null;
			String address2 = "";
			String emerPersonName="";
			
			try{
			emerPerson = Customer.customer(tax.getEmergencyContactPerson().getCustomerid());
			emerPersonName = emerPerson.getLastname().toUpperCase() + ", " + emerPerson.getFirstname().toUpperCase() + " " + emerPerson.getMiddlename().toUpperCase();
			address2 = emerPerson.getPurok().getPurokName() + ", " + emerPerson.getBarangay().getName() + ", " + emerPerson.getMunicipality().getName() + ", " + emerPerson.getProvince().getName();
			address2 = emerPerson.getCompleteAddress();
			}catch(Exception e){}
			
			
			rpt.setF1(tax.getCardno());
			rpt.setF2(tax.getLastname().toUpperCase() + ", " + tax.getFirstname().toUpperCase() + " " + tax.getMiddlename().toUpperCase());
			rpt.setF3(address.toUpperCase());
			int age = tax.getAge();
			try{age = DateUtils.calculateAge(tax.getBirthdate());}catch(Exception e){} //calculate the current age
			rpt.setF4(tax.getBirthdate());
			rpt.setF5(emerPersonName);
			rpt.setF6(address2.toUpperCase());
			rpt.setF7(emerPerson.getContactno());
			rpt.setF8(card.getValidDateFrom());
			rpt.setF9(card.getValidDateTo());
			rpt.setF12(captain);
			rpt.setF14(tax.getGender().equalsIgnoreCase("1")? "MALE" : "FEMALE");
			rpt.setF16(age+"");
			rpt.setF17(CivilStatus.typeName(tax.getCivilStatus()).toUpperCase());
			rpt.setF18(Relationships.typeName(tax.getRelationship()).toUpperCase());
			
			if(tax.getPhotoid()!=null && !tax.getPhotoid().isEmpty()){
				String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + tax.getPhotoid() + ".jpg";
				try{File file = new File(driveImage);
				FileInputStream taxPhoto = new FileInputStream(file);
				rpt.setF10(taxPhoto);
				}catch(Exception e){}
			}
			
			
			
			FileInputStream logo = null;
			String officialLogo = path + "logo.png";
			try{File file = new File(officialLogo);
			logo = new FileInputStream(file);
			rpt.setF13(logo);
			}catch(Exception e){}
			
			
			FileInputStream barPdf = null;
			try{
				Barcode barcode = null;
				//barcode = BarcodeFactory.createPDF417(tax.getCardno());
				barcode = BarcodeFactory.create3of9(tax.getCardno(), false);
				
				barcode.setDrawingText(false);
				File pdf = new File(tax.getCardno()+".png");
				
				BarcodeImageHandler.savePNG(barcode, pdf);
				barPdf = new FileInputStream(pdf);
				rpt.setF11(barPdf);
			}catch(Exception e){e.printStackTrace();}
			
			
			
			//officialseallakesebu
			String lakesebuofficialseal = path + "municipalseal.png";
			try{File file1 = new File(lakesebuofficialseal);
			FileInputStream off2 = new FileInputStream(file1);
			rpt.setF15(off2);
			}catch(Exception e){}
			
			//dilgofficialseal
			String dilgseal = path + "dilg.png";
			try{File dilg1 = new File(dilgseal);
			FileInputStream dilg2 = new FileInputStream(dilg1);
			rpt.setF19(dilg2);
			}catch(Exception e){}
			
			//dilgofficialseal
			String sealTRANS = path + "barangaylogotrans.png";
			try{File seal = new File(sealTRANS);
			FileInputStream seal2 = new FileInputStream(seal);
			rpt.setF20(seal2);
			}catch(Exception e){}
			
			
			rpt.setF21("PROVINCE OF "+PROVINCE.toUpperCase());
			rpt.setF22("MUNICIPALITY OF "+MUNICIPALITY.toUpperCase());
			rpt.setF23("BARANGAY "+BARANGAY.toUpperCase());
			
			//ID Background
			String idbg = path + "idbg.png";
			try{File idsbg = new File(idbg);
			FileInputStream idsbg2 = new FileInputStream(idsbg);
			rpt.setF24(idsbg2);
			}catch(Exception e){}
			
			rpts.add(rpt);
		
			
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(IDCARD_REPORT_NAME, IDCARD_REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
		
		HashMap param = new HashMap();
		
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ IDCARD_REPORT_NAME +".pdf");
	  		}catch(Exception e){e.printStackTrace();}
		
				try{
					System.out.println("REPORT_PATH:" + path + "REPORT_NAME: " + IDCARD_REPORT_NAME);
		  		 File file = new File(path, IDCARD_REPORT_NAME + ".pdf");
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
			            response.setHeader("Content-Disposition", "inline; filename=\"" + IDCARD_REPORT_NAME + ".pdf" + "\"");
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
	
	
	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public String getSearchCustomer() {
		return searchCustomer;
	}

	public void setSearchCustomer(String searchCustomer) {
		this.searchCustomer = searchCustomer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	public String getGenderType() {
		return genderType;
	}

	public void setGenderType(String genderType) {
		this.genderType = genderType;
	}

	public String getCivilType() {
		return civilType;
	}

	public void setCivilType(String civilType) {
		this.civilType = civilType;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public List<Clearance> getClearances() {
		return clearances;
	}

	public void setClearances(List<Clearance> clearances) {
		this.clearances = clearances;
	}

	public String getPhotoId() {
		if(photoId==null){
			photoId="tmp";
		}
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public boolean isHideView1() {
		return hideView1;
	}

	public void setHideView1(boolean hideView1) {
		this.hideView1 = hideView1;
	}

	public boolean isHideView2() {
		return hideView2;
	}

	public void setHideView2(boolean hideView2) {
		this.hideView2 = hideView2;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public boolean isLessDetails() {
		return lessDetails;
	}

	public void setLessDetails(boolean lessDetails) {
		this.lessDetails = lessDetails;
	}

	public boolean isMoreDetails() {
		return moreDetails;
	}

	public void setMoreDetails(boolean moreDetails) {
		this.moreDetails = moreDetails;
	}

	public String getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmergencyContactPersonName() {
		return emergencyContactPersonName;
	}

	public void setEmergencyContactPersonName(String emergencyContactPersonName) {
		this.emergencyContactPersonName = emergencyContactPersonName;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public List<Livelihood> getBusinesses() {
		return businesses;
	}

	public void setBusinesses(List<Livelihood> businesses) {
		this.businesses = businesses;
	}

	public List<BCard> getCardTrans() {
		return cardTrans;
	}

	public void setCardTrans(List<BCard> cardTrans) {
		this.cardTrans = cardTrans;
	}

	public String getStylePicture() {
		if(stylePicture==null){
			stylePicture = "text-align:center;background-color:transparent;";
		}
		return stylePicture;
	}

	public void setStylePicture(String stylePicture) {
		this.stylePicture = stylePicture;
	}

	public List<SuspectedPerson> getPersonCases() {
		return personCases;
	}

	public void setPersonCases(List<SuspectedPerson> personCases) {
		this.personCases = personCases;
	}
	
	public void printCase(CaseFilling fil){
		
		
		
		String REPORT_NAME = CASES_REPORT_NAME;
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		
		//UserDtls user = Login.getUserLogin().getUserDtls();
			HashMap param = new HashMap();
			
			//logo
			String officialLogo = path + "logo.png";
			FileInputStream brgy = null;
			FileInputStream brgy1 = null;
			try{File file = new File(officialLogo);
			brgy = new FileInputStream(file);
			brgy1 = new FileInputStream(file);
			}catch(Exception e){}
			
			String lakesebuofficialseal = path + "municipalseal.png";
			FileInputStream municipal = null;
			FileInputStream municipal1 = null;
			try{File file1 = new File(lakesebuofficialseal);
			municipal = new FileInputStream(file1);
			municipal1 = new FileInputStream(file1);
			}catch(Exception e){}
			
			//logo
			String logo = path + "barangaylogotrans.png";
			FileInputStream brgytrans = null;
			FileInputStream brgytrans1 = null;
			try{File file = new File(logo);
			brgytrans = new FileInputStream(file);
			brgytrans1 = new FileInputStream(file);
			}catch(Exception e){}
			
			//cutter
			String sep = path + "cutter.png";
			FileInputStream cutter = null;
			try{File file = new File(sep);
			cutter = new FileInputStream(file);
			}catch(Exception e){}
			
			FileInputStream barPdf = null;
			FileInputStream barPdf1 = null;
			try{
				Barcode barcode = null;
				
				//barcode = BarcodeFactory.createPDF417(tax.getFullname());
				barcode = BarcodeFactory.create3of9(fil.getCases().getCaseNo(), false);
				
				barcode.setDrawingText(false);
				File pdf = new File(fil.getCases().getCaseNo()+".png");
				
				BarcodeImageHandler.savePNG(barcode, pdf);
				barPdf = new FileInputStream(pdf);
				barPdf1 = new FileInputStream(pdf);
				
			}catch(Exception e){e.printStackTrace();}
			
			String prov = "Province of " + PROVINCE;
			String mun = "Municipality of " + MUNICIPALITY;
			String bar = "BARANGAY " + BARANGAY.toUpperCase();
			
			List<CasesRpt> reports = new ArrayList<CasesRpt>();//Collections.synchronizedList(new ArrayList<CasesRpt>());
			CasesRpt rpt = new CasesRpt();
			rpt.setF1(brgy);
			rpt.setF2(municipal);
			rpt.setF3(brgytrans);
			rpt.setF4(cutter);
			rpt.setF5(barPdf);
			
			rpt.setF6(prov);
			rpt.setF7(mun);
			rpt.setF8(bar);
			
			rpt.setF9(fil.getCases().getCaseNo());
			rpt.setF10(DateUtils.convertDateToMonthDayYear(fil.getFillingDate()));
			
			String attempt = "";
			if(fil.getCount()==1){
				attempt = "( 1st )";
			}else if(fil.getCount()==2){
				attempt = "( 2nd )";
			}else if(fil.getCount()==3){
				attempt = "( 3rd )";
			}
			
			if(fil.getCases().getType()==1){
				rpt.setF11(attempt);
				rpt.setF12("( )");
			}else{
				rpt.setF11("( )");
				rpt.setF12(attempt);
			}
			
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				rpt.setF13(fil.getCases().getOtherCaseName());
			}else{
				rpt.setF13(CaseKind.typeName(fil.getCases().getKind()));
			}
			
			rpt.setF14(DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()) + " " + fil.getSettlementTime());
			
			rpt.setF15(fil.getCases().getComplainants());
			rpt.setF16(fil.getCases().getComAddress());
			rpt.setF17(fil.getCases().getRespondents());
			rpt.setF18(fil.getCases().getResAddress());
			
			rpt.setF19(fil.getCases().getNarratives());
			
			rpt.setF20(fil.getOrNumber());
			rpt.setF21("Php "+Currency.formatAmount(fil.getFees()));
			rpt.setF22("Php "+Currency.formatAmount(fil.getMsgFee()));
			
			rpt.setF23("Complainant Copy");
			
			reports.add(rpt);
			/////////////////////////////////////////////////////////////////
			
			rpt = new CasesRpt();
			rpt.setF1(brgy1);
			rpt.setF2(municipal1);
			rpt.setF3(brgytrans1);
			rpt.setF4(null);
			rpt.setF5(barPdf1);
			
			rpt.setF6(prov);
			rpt.setF7(mun);
			rpt.setF8(bar);
			
			rpt.setF9(fil.getCases().getCaseNo());
			rpt.setF10(DateUtils.convertDateToMonthDayYear(fil.getFillingDate()));
			
			if(fil.getCases().getType()==1){
				rpt.setF11(attempt);
				rpt.setF12("( )");
			}else{
				rpt.setF11("( )");
				rpt.setF12(attempt);
			}
			
			rpt.setF13(CaseKind.typeName(fil.getCases().getKind()));
			rpt.setF14(DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()) + " " + fil.getSettlementTime());
			
			rpt.setF15(fil.getCases().getComplainants());
			rpt.setF16(fil.getCases().getComAddress());
			rpt.setF17(fil.getCases().getRespondents());
			rpt.setF18(fil.getCases().getResAddress());
			
			rpt.setF19(fil.getCases().getNarratives());
			
			rpt.setF20(fil.getOrNumber());
			rpt.setF21("Php "+Currency.formatAmount(fil.getFees()));
			rpt.setF22("Php "+Currency.formatAmount(fil.getMsgFee()));
			
			rpt.setF23("Barangay Copy");
			
			reports.add(rpt);
			
			JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
			
			try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
		  		}catch(Exception e){e.printStackTrace();}
			
					try{
						//System.out.println("REPORT_PATH:" + path + "REPORT_NAME: " + REPORT_NAME);
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
	
	@Deprecated
	public void printBlotter(Blotters blot){
		
		
		
		loadPersons(blot);
		
		String sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 AND pos.posid=?";
		String[] params = new String[1];
		params[0] = Positions.CAPTAIN.getId()+"";
		
		Employee emp = Employee.retrieve(sql, params).get(0);
		String captain =  emp.getFirstName().toUpperCase() + " " + emp.getMiddleName().substring(0,1).toUpperCase() + ". " + emp.getLastName().toUpperCase();
		
		List<ClearanceRpt> reports = Collections.synchronizedList(new ArrayList<ClearanceRpt>());
		ClearanceRpt cl = new ClearanceRpt();
		cl.setF1("");
		reports.add(cl);
		String REPORT_NAME = BLOTTER_REPORT_NAME;
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		UserDtls user = Login.getUserLogin().getUserDtls();
			HashMap param = new HashMap();
			
			param.put("PARAM_CAPTAIN", "HON. "+captain);
			
			//setIncidentDetails(blot.getIncidentDetails());
			//setIncidentSolutions(blot.getIncidentSolutions());
			
			param.put("PARAM_PRINTED_DATE", "Printed Date: "+DateUtils.getCurrentDateMMMMDDYYYY());
			
			param.put("PARAM_PROVINCE_NAME", "Province of " + PROVINCE);
			param.put("PARAM_MUNICIPALITY_NAME", "Municipality of " + MUNICIPALITY);
			param.put("PARAM_BARANGAY_NAME", "BARANGAY " + BARANGAY.toUpperCase());
			
			param.put("PARAM_DATE_TIME_REPORTED", blot.getDateTrans() + " " + blot.getTimeTrans());
			param.put("PARAM_INCIDENT_DATE_TIME", blot.getIncidentDate() + " " + blot.getIncidentTime());
			param.put("PARAM_INCIDENT_PLACE", blot.getIncidentPlace());
			
			param.put("PARAM_INCIDENT_TYPE", IncidentReportTypes.retrieve(blot.getIncidentType()).getName());
			param.put("PARAM_INCIDENT_STATUS", BlotterStatus.typeName(blot.getStatus()));
			
			String value = "";
			int cnt = 1;
			//int size = getReportingPersons().size();
			for(ReportingPerson rep : getReportingPersons()){
				/*String address = rep.getReportingPerson().getPurok().getPurokName() + ", " + 
						rep.getReportingPerson().getBarangay().getName() + ", " + 
						rep.getReportingPerson().getMunicipality().getName() + ", " + 
						rep.getReportingPerson().getProvince().getName();*/
				String address =  rep.getReportingPerson().getPurok().getId()==0? "" : rep.getReportingPerson().getPurok().getPurokName() +", ";
				   address += rep.getReportingPerson().getBarangay().getId()==0? "" : rep.getReportingPerson().getBarangay().getName() + ", ";
				   address += rep.getReportingPerson().getMunicipality().getId()==0? "" : rep.getReportingPerson().getMunicipality().getName() + ", ";
				   address += rep.getReportingPerson().getProvince().getId()==0? "" : rep.getReportingPerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+rep.getReportingPerson().getFullname() + " - " + address + "\t"+ rep.getReportingPerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+rep.getReportingPerson().getFullname() + "\t" + address + "\t"+ rep.getReportingPerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_REPORTING_PERSONS", value);
			
			value += "\nRespondent/s:\n";
			cnt = 1;
			//size = getSuspectedPersons().size();
			for(SuspectedPerson sus : getSuspectedPersons()){
				/*String address = sus.getSuspectedPerson().getPurok().getPurokName() + ", " + 
						sus.getSuspectedPerson().getBarangay().getName() + ", " + 
						sus.getSuspectedPerson().getMunicipality().getName() + ", " + 
						sus.getSuspectedPerson().getProvince().getName();*/
				String address =  sus.getSuspectedPerson().getPurok().getId()==0? "" : sus.getSuspectedPerson().getPurok().getPurokName() +", ";
				   address += sus.getSuspectedPerson().getBarangay().getId()==0? "" : sus.getSuspectedPerson().getBarangay().getName() + ", ";
				   address += sus.getSuspectedPerson().getMunicipality().getId()==0? "" : sus.getSuspectedPerson().getMunicipality().getName() + ", ";
				   address += sus.getSuspectedPerson().getProvince().getId()==0? "" : sus.getSuspectedPerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+sus.getSuspectedPerson().getFullname() + " - " + address + "\t"+ sus.getSuspectedPerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+sus.getSuspectedPerson().getFullname() + "\t" + address + "\t"+ sus.getSuspectedPerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_REPORTED_PERSONS", value);
			
			value += "\nVictim/s:\n";
			cnt = 1;
			//size = getVictimPersons().size();
			for(VictimPerson vic : getVictimPersons()){
				/*String address = vic.getVictimePerson().getPurok().getPurokName() + ", " + 
						vic.getVictimePerson().getBarangay().getName() + ", " + 
						vic.getVictimePerson().getMunicipality().getName() + ", " + 
						vic.getVictimePerson().getProvince().getName();*/
				String address =  vic.getVictimePerson().getPurok().getId()==0? "" : vic.getVictimePerson().getPurok().getPurokName() +", ";
				   address += vic.getVictimePerson().getBarangay().getId()==0? "" : vic.getVictimePerson().getBarangay().getName() + ", ";
				   address += vic.getVictimePerson().getMunicipality().getId()==0? "" : vic.getVictimePerson().getMunicipality().getName() + ", ";
				   address += vic.getVictimePerson().getProvince().getId()==0? "" : vic.getVictimePerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+vic.getVictimePerson().getFullname() + " - " + address + "\t"+ vic.getVictimePerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+vic.getVictimePerson().getFullname() + "\t" + address + "\t"+ vic.getVictimePerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			value += "\nNarratives:\n";
			value += blot.getIncidentDetails()+"\n";
			value += "\nSolution Details:\n";
			value += blot.getIncidentSolutions()+"\n";
			//param.put("PARAM_INCIDENT_DETAILS", blot.getIncidentDetails());
			//param.put("PARAM_INCIDENT_SOLUTIONS", blot.getIncidentSolutions());
			
			//param.put("PARAM_INCIDENT_VICTIM_PERSONS", value);
			
			value += "\nThis incident report was handled by the following barangay incident board committee:\n";
			cnt = 1;
			//size = getLuponPersons().size();
			for(LuponPerson lup : getLuponPersons()){
				//if(cnt==size){
					value +="\t"+ cnt +".)"+(lup.getLuponPerson().getIsOfficial()==1? "HON. " : "") + lup.getLuponPerson().getFirstName() + " " + lup.getLuponPerson().getLastName()+"\n";
				/*}else{
					value +="\t"+ cnt +".)"+(lup.getLuponPerson().getIsOfficial()==1? "HON. " : "") + lup.getLuponPerson().getFirstName() + " " + lup.getLuponPerson().getLastName() +"\n ";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_LUPON_PERSONS", value);
			param.put("PARAM_INCIDENT_REPORTING_PERSONS", value);
			
			
			String userName = user.getFirstname() + " " + user.getLastname();
			param.put("PARAM_INCIDENT_PREPARED_BY", "Prepared By: "+ userName);
			
			//logo
			String officialLogo = path + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_BARANGAY_SEAL", off);
			}catch(Exception e){}
			
			String lakesebuofficialseal = path + "municipalseal.png";
			try{File file1 = new File(lakesebuofficialseal);
			FileInputStream off2 = new FileInputStream(file1);
			param.put("PARAM_MUNICIPAL_SEAL", off2);
			}catch(Exception e){}
			
			//logo
			String logo = path + "barangaylogotrans.png";
			try{File file = new File(logo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_BARANGAY_LOGO_TRANS", off);
			}catch(Exception e){}
			
			try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
		  		}catch(Exception e){e.printStackTrace();}
			
					try{
						//System.out.println("REPORT_PATH:" + path + "REPORT_NAME: " + REPORT_NAME);
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
	
private void loadPersons(Blotters blot){
		
		reportingPersons = new ArrayList<ReportingPerson>();//Collections.synchronizedList(new ArrayList<ReportingPerson>());
		String sql = " AND blot.blotid=? AND rps.isactiverep=1 AND blot.blotdate=?";
		String[] params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(ReportingPerson rep : ReportingPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(rep.getReportingPerson().getBarangay().getId());
			rep.getReportingPerson().setBarangay(bar);
			rep.getReportingPerson().setMunicipality(bar.getMunicipality());
			rep.getReportingPerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(rep.getReportingPerson().getPurok().getId());}catch(Exception e){}
			rep.getReportingPerson().setPurok(purok);
			
			reportingPersons.add(rep);
		}
		
		suspectedPersons = new ArrayList<SuspectedPerson>();//Collections.synchronizedList(new ArrayList<SuspectedPerson>());
		sql = " AND blot.blotid=? AND sus.isactivesus=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(sus.getSuspectedPerson().getBarangay().getId());
			sus.getSuspectedPerson().setBarangay(bar);
			sus.getSuspectedPerson().setMunicipality(bar.getMunicipality());
			sus.getSuspectedPerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(sus.getSuspectedPerson().getPurok().getId());}catch(Exception e){}
			sus.getSuspectedPerson().setPurok(purok);
			
			suspectedPersons.add(sus);
		}
		
		victimPersons = new ArrayList<VictimPerson>();//Collections.synchronizedList(new ArrayList<VictimPerson>());
		sql = " AND blot.blotid=? AND vic.isactivevic=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(VictimPerson vic : VictimPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(vic.getVictimePerson().getBarangay().getId());
			vic.getVictimePerson().setBarangay(bar);
			vic.getVictimePerson().setMunicipality(bar.getMunicipality());
			vic.getVictimePerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(vic.getVictimePerson().getPurok().getId());}catch(Exception e){}
			vic.getVictimePerson().setPurok(purok);
			
			victimPersons.add(vic);
		}
		
		
		luponPersons =new ArrayList<LuponPerson>();// Collections.synchronizedList(new ArrayList<LuponPerson>());
		sql = " AND blot.blotid=? AND lup.isactivelup=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(LuponPerson lup : LuponPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(lup.getLuponPerson().getBarangay().getId());
			lup.getLuponPerson().setBarangay(bar);
			lup.getLuponPerson().setMunicipality(bar.getMunicipality());
			lup.getLuponPerson().setProvince(bar.getProvince());
			luponPersons.add(lup);
		}
		
		
	}

public List<ReportingPerson> getReportingPersons() {
	return reportingPersons;
}

public void setReportingPersons(List<ReportingPerson> reportingPersons) {
	this.reportingPersons = reportingPersons;
}

public List<SuspectedPerson> getSuspectedPersons() {
	return suspectedPersons;
}

public void setSuspectedPersons(List<SuspectedPerson> suspectedPersons) {
	this.suspectedPersons = suspectedPersons;
}

public List<VictimPerson> getVictimPersons() {
	return victimPersons;
}

public void setVictimPersons(List<VictimPerson> victimPersons) {
	this.victimPersons = victimPersons;
}

public List<LuponPerson> getLuponPersons() {
	return luponPersons;
}

public void setLuponPersons(List<LuponPerson> luponPersons) {
	this.luponPersons = luponPersons;
}
	
public String getWelcomeMsg() {
	
	return "Type your search here";
}

private ApplicationVersionController appVersionChanges(){
	
	ApplicationVersionController versionController;
	List<ApplicationFixes> fixes = new ArrayList<ApplicationFixes>();//Collections.synchronizedList(new ArrayList<ApplicationFixes>());
	
	String sql = "SELECT * FROM app_version_control ORDER BY timestamp DESC LIMIT 1";
	String[] params = new String[0];
	versionController = ApplicationVersionController.retrieve(sql, params).get(0);
	
	try{fixes = Collections.synchronizedList(new ArrayList<ApplicationFixes>());}catch(Exception e){}
	sql = "SELECT * FROM buildfixes WHERE buildid=?";
	params = new String[1];
	params[0] = versionController.getBuildid()+"";
	try{fixes = ApplicationFixes.retrieve(sql, params);}catch(Exception e){}
	
	return versionController;
}

public Copyright appInfo(){
	
	Copyright copyright;
	String sql = "SELECT * FROM copyright ORDER BY id desc limit 1";
	String[] params = new String[0];
	copyright = Copyright.retrieve(sql, params).get(0);
	
	return copyright;
}

private List<License> appLicense(){
	List<License> licenses = new ArrayList<License>();// Collections.synchronizedList(new ArrayList<License>());
	
	String sql = "SELECT * FROM license";
	licenses = new ArrayList<License>();//Collections.synchronizedList(new ArrayList<License>());
	licenses = License.retrieve(sql, new String[0]);
	
	return licenses;
}

public String getGreetings() {
	
	String[] greets = {"Top of the", "Good", "Hey there, we hope you're having a fine", "We hope, you are having a wonderful","Cool", "Great"};
	String greetings = greets[(int) (Math.random() * greets.length)];
	
	Calendar c = Calendar.getInstance();
	int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
	if(timeOfDay >= 0 && timeOfDay <12){
		greetings = greetings + " morning";
	}else if(timeOfDay >= 12 && timeOfDay < 16){
		greetings = greetings + " afternoon";
	}else if(timeOfDay >= 16 && timeOfDay < 21){
		greetings = greetings + " evening";
	}else if(timeOfDay >= 21 && timeOfDay < 24){
		greetings = greetings + " night";
	}
	
	return greetings;
}

public String getSearchInfo(){
	String infor="You can put your search criteria here...";
	
	boolean[] rans = {true,false,false,false,true,true,false,true,false,false,true,true,false,true,true,false}; 
	//String[] msgs = {getGreetings() + ", " + getWelcomeMsg(),getGreetings(),getWelcomeMsg()};
	boolean isOk = rans[(int) (Math.random() * rans.length)];
	
	
	if(isOk){
		infor = getWelcomeMsg();
	}
	
	return infor;
}

public String getReligionName() {
	return religionName;
}

public void setReligionName(String religionName) {
	this.religionName = religionName;
}

public String getEducationName() {
	return educationName;
}

public void setEducationName(String educationName) {
	this.educationName = educationName;
}

public String getRaceName() {
	return raceName;
}

public void setRaceName(String raceName) {
	this.raceName = raceName;
}

public List<Cases> getSummons() {
	return summons;
}

public void setSummons(List<Cases> summons) {
	this.summons = summons;
}

private String aboutCitizen(Customer cuz){
	String about = "";
	String name = cuz.getFullname();
	String address = cuz.getCompleteAddress();
	int gender = Integer.valueOf(cuz.getGender());
	String[] mrmrs = {"", "Mr.", "Miss","Mrs."};
	String[] heshe = {"","He","She"};
	String[] hisher = {"","His","Her"};
	String[] himher = {"","Him","Her"};
	
	if(CivilStatus.MARRIED.getId()==cuz.getCivilStatus()){
		if(gender==2){
			name =  mrmrs[gender+1] +" " + cuz.getFullname();
		}else{
			name =  mrmrs[gender] +" " + cuz.getFullname();
		}
		
	}else{
	
		name = mrmrs[gender] +" " + cuz.getFullname();
	
	}
	
	about = name + " was registered on " + DateUtils.convertDateToMonthDayYear(cuz.getDateregistered()) +" with assigned Barangay ID no " + cuz.getCardno()+".";
	about += " " + heshe[gender] + " is "+ CivilStatus.typeName(cuz.getCivilStatus()) +" and a resident of "+ address + ". ";
	
	Customer cper = null;
	int gender2 = 1;
	String address2 = "";
	if(cuz.getEmergencyContactPerson()!=null && cuz.getEmergencyContactPerson().getCustomerid()>0){
		 cper = Customer.retrieve(cuz.getEmergencyContactPerson().getCustomerid());
		 gender2 = Integer.valueOf(cper.getGender());
		 address2 = cper.getCompleteAddress();
	}
	
	about += "Today "+ hisher[gender].toLowerCase() + " age is " + cuz.getAge() +  " years old who was born on " + DateUtils.convertDateToMonthDayYear(cuz.getBirthdate()) +".";
	
	boolean emGiven = false;
	if(cuz.getContactno()!=null && !cuz.getContactno().isEmpty()){
		about += " To reach this person you may contact through " + hisher[gender].toLowerCase() + " mobile number at " + cuz.getContactno();
		
		if(cuz.getEmergencyContactPerson()!=null && cuz.getEmergencyContactPerson().getCustomerid()>0){
			about += " If in the event you can't contact " + himher[gender].toLowerCase();
			about += " you may contact " + hisher[gender].toLowerCase() + " " + Relationships.typeName(cuz.getRelationship());
			about += " " + cper.getFullname();
			if(cper.getContactno()!=null && !cper.getContactno().isEmpty()){
				about += " through " + hisher[gender2].toLowerCase() + " mobile number at " + cper.getContactno();
				about += " or you may personally visit "+ hisher[gender].toLowerCase() + " " + Relationships.typeName(cuz.getRelationship()) + " at";
				about += " " + address2 + ".";
			}
			emGiven = true;
		}else{
			about += ".";
		}
	}
	
	if(cuz.getEmergencyContactPerson()!=null && cuz.getEmergencyContactPerson().getCustomerid()>0 && !emGiven){
		about += " If in the event you can't contact " + himher[gender].toLowerCase();
		about += " you may contact " + hisher[gender].toLowerCase() + " " + Relationships.typeName(cuz.getRelationship());
		about += " " + cper.getFullname();
		if(cper.getContactno()!=null && !cper.getContactno().isEmpty()){
			about += " through mobile number at " + cper.getContactno();
			about += " or you may personally visit "+ hisher[gender].toLowerCase() + " " + Relationships.typeName(cuz.getRelationship()) + " at";
			about += " " + address2 + ".";
		}
	}
	setAboutCitizen(about);
	return about;
}

public String getAboutCitizen() {
	return aboutCitizen;
}

public void setAboutCitizen(String aboutCitizen) {
	this.aboutCitizen = aboutCitizen;
}

public String getProfileContent() {
	if(profileContent==null) {
		profileContent = "Please type the name of the citizen";
	}
	return profileContent;
}

public void setProfileContent(String profileContent) {
	this.profileContent = profileContent;
}

public String getPersonDtls() {
	if(personDtls==null) {
		personDtls = "<p><h1>Hi There, welcome to BRIS Software, your portal for Barangay Information</h1></p>";
	}
	return personDtls;
}

public void setPersonDtls(String personDtls) {
	this.personDtls = personDtls;
}

public String getSearchOption() {
	return searchOption;
}

public void setSearchOption(String searchOption) {
	this.searchOption = searchOption;
}

/////this for version 6 codes
public void viewModeListener() {
	System.out.println("Option selected " + getSearchOption());
	PrimeFaces ins = PrimeFaces.current();
	
	
	String citizenShow = "$(\"#citId\").show(1000);";
	String citizenHide = "$(\"#citId\").hide(1000);";
	
	String cardShow = "$(\"#idcreated\").show(1000);";
	String cardHide = "$(\"#idcreated\").hide(1000);";
	
	String docShow = "$(\"#iddocument\").show(1000);";
	String docHide = "$(\"#iddocument\").hide(1000);";
	
	String casesShow = "$(\"#idcases\").show(1000);";
	String casesHide = "$(\"#idcases\").hide(1000);";
	
	String calShow = "$(\"#idcalendar\").show(1000);";
	String calHide = "$(\"#idcalendar\").hide(1000);";
	
	String showMainSearch = "$(\"#citTextId\").show();";
	String hideMainSearch = "$(\"#citTextId\").hide();";
	
	String showGenericSeach =  "$(\"#geneTextId\").show();";
	String hideGenericSeach =  "$(\"#geneTextId\").hide();";
	
	String showCitizenOnly =  citizenShow + cardHide + docHide + calHide + casesHide + showMainSearch + hideGenericSeach;
	String showCardOnly = cardShow + citizenHide + docHide + calHide + casesHide + hideMainSearch + showGenericSeach;
	String showDocOnly = docShow + citizenHide + cardHide + calHide + casesHide + hideMainSearch + showGenericSeach;
	String showCasesOnly = casesShow + citizenHide + cardHide + docHide + calHide + hideMainSearch + showGenericSeach;
	String showCalOnly = calShow + citizenHide + cardHide + docHide + casesHide + hideMainSearch + hideGenericSeach;
	
	
	
	if("1".equalsIgnoreCase(getSearchOption())) {
		ins.executeScript(showCitizenOnly);
	}else if("2".equalsIgnoreCase(getSearchOption())) {
		ins.executeScript(showCardOnly);
	}else if("3".equalsIgnoreCase(getSearchOption())) {
		ins.executeScript(showDocOnly);
	}else if("4".equalsIgnoreCase(getSearchOption())) {
		ins.executeScript(showCasesOnly);
	}else if("5".equalsIgnoreCase(getSearchOption())) {
		//getEvent().init();
		ins.executeScript(showCalOnly);
	}
	
	
}

public void graphSelected(String type, String door) {
	
	
	if("citizen".equalsIgnoreCase(type)) {
		if("open".equalsIgnoreCase(door)) {
			option("$('#citizenGraph').show(1000);", door);
		}else {
			option("$('#citizenGraph').hide(1000);", door);
		}
	}
}

private void option(String sc, String door) {
	PrimeFaces pm = PrimeFaces.current();
	String opt = getSearchOption();
	
	String citizenShow = "$(\"#citId\").show(1000);";
	String citizenHide = "$(\"#citId\").hide(1000);";
	
	String cardShow = "$(\"#idcreated\").show(1000);";
	String cardHide = "$(\"#idcreated\").hide(1000);";
	
	String docShow = "$(\"#iddocument\").show(1000);";
	String docHide = "$(\"#iddocument\").hide(1000);";
	
	String casesShow = "$(\"#idcases\").show(1000);";
	String casesHide = "$(\"#idcases\").hide(1000);";
	
	String calShow = "$(\"#idcalendar\").show(1000);";
	String calHide = "$(\"#idcalendar\").hide(1000);";
	
	
	if("1".equalsIgnoreCase(opt)) {//citizen
		if("open".equalsIgnoreCase(door)) {
			sc += citizenHide;
		}else {
			sc += citizenShow;
		}
	}else if("2".equalsIgnoreCase(opt)) {//ID
		if("open".equalsIgnoreCase(door)) {
			sc += cardHide;
		}else {
			sc += cardShow;
		}
	}else if("3".equalsIgnoreCase(opt)) {//DOC
		if("open".equalsIgnoreCase(door)) {
			sc += docHide;
		}else {
			sc += docShow;
		}
	}else if("4".equalsIgnoreCase(opt)) {//CASES
		if("open".equalsIgnoreCase(door)) {
			sc += casesHide;
		}else {
			sc += casesShow;
		}
	}else if("5".equalsIgnoreCase(opt)) {//Calendar
		if("open".equalsIgnoreCase(door)) {
			sc += calHide;
		}else {
			sc += calShow;
		}
	}
	
	pm.executeScript(sc);
}

	/*
	 * @ManagedProperty("#{eventBean}") private EventsBean event;
	 * 
	 * public EventsBean getEvent() { return event; }
	 * 
	 * public void setEvent(EventsBean event) { this.event = event; }
	 */



}











