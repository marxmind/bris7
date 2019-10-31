package com.italia.marxmind.bris.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.CaptureEvent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.BusinessEngaged;
import com.italia.marxmind.bris.controller.Cedula;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.DocumentPrinting;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Hospitals;
import com.italia.marxmind.bris.controller.Livelihood;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.MultiLivelihood;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.OD;
import com.italia.marxmind.bris.controller.ORTransaction;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purpose;
import com.italia.marxmind.bris.controller.Words;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CattleColor;
import com.italia.marxmind.bris.enm.CattleKind;
import com.italia.marxmind.bris.enm.CattlePurpose;
import com.italia.marxmind.bris.enm.ClearanceStatus;
import com.italia.marxmind.bris.enm.ClearanceType;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.DocTypes;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.LandTypes;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 07/06/2017
 *
 */
@ManagedBean(name="clearBean", eager=true)
@ViewScoped
public class ClearanceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 767967634557851L;
	
	private String issuedDate;
	private String barcode;
	private String orNumber;
	private String cedulaNumber;
	private Date cedulaIssued;
	private String cedulaIssuedAddress;
	private int cedulaTypeId;
	private List cedulaTypes;
	private String cedulaDetails;
	private String notes;
	private String photoId="camera";
	private double amountPaid;
	private boolean payable = true;
	
	private List purposeTypes;
	private int purposeTypeId;
	private Map<Integer, Purpose> purposesData = Collections.synchronizedMap(new HashMap<Integer, Purpose>());
	
	private List status;
	private int statusId;
	
	private List clearanceTypes;
	private int clearanceTypeId;
	
	private Employee employee;
	private String employeeName;
	private String searchEmployee;
	private List<Employee> employees = Collections.synchronizedList(new ArrayList<Employee>());
	
	private Customer taxPayer;
	private String customerName;
	private String searchTaxpayer;
	private List<Customer> taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
	
	private String motherName;
	private String fatherName;
	private String childName;
	private String bornAddress;
	private Date bordDate;
	private String bornDetails;
	
	private Customer beneficiary;
	private String beneficiaryName;
	private String seachBeneficiary;
	private List<Customer> beneficiaries = Collections.synchronizedList(new ArrayList<Customer>());
	
	private List<Clearance> clearances = Collections.synchronizedList(new ArrayList<Clearance>());
	private Clearance selectedData;
	private String searchClearance;
	
	private String searchBusinessName;
	private List<Livelihood> business = Collections.synchronizedList(new ArrayList<Livelihood>());
	private List<Livelihood> selectedBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
	private List<Livelihood> ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
	
	private List<Purpose> purposes = Collections.synchronizedList(new ArrayList<Purpose>());
	
	/*private String dateFrom;
	private String dateTo;*/
	
	private int docId;
	private List docTypes;
	
	private int documentValidity;
	
	//private String filename;
	private String capturedImagePathName;
	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE) ;
	
	/*
	 * for deletion
	 */
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	/*
	 * for deletion
	 */
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String BUSINESS_REPORT_NAME = ReadXML.value(ReportTag.BUSINESS_CLEARANCE);
	private static final String FISHCAGE_REPORT_NAME = ReadXML.value(ReportTag.FISHCAGE_CLEARANCE);
	private static final String ASSISTANCE_REPORT_NAME = ReadXML.value(ReportTag.ASSISTANCE_CLEARANCE);
	private static final String OTHERS_REPORT_NAME = ReadXML.value(ReportTag.OTHERS_CLEARANCE);
	private static final String LARGE_CATTLE_REPORT_NAME = ReadXML.value(ReportTag.LARGE_CATTLE);
	private static final String LATE_BIRTH_REPORT_NAME = ReadXML.value(ReportTag.LATE_BIRTH);
	private static final String BARANGAY_BUSINESS_PERMIT_NAME = ReadXML.value(ReportTag.BARANGAY_BUSINESS_PERMIT);
	private static final String AUTHORIZATION_REPORT_NAME = ReadXML.value(ReportTag.AUTHORIZATION);
	private static final String LAND_REPORT_NAME = ReadXML.value(ReportTag.LAND_REPORT);
	private static final String DOC_OPEN_TITLE_REPORT_NAME = ReadXML.value(ReportTag.DOCUMENT_OPEN_TITLE);
	
	private int relationshipId;
	private List relationships;
	private String hospital;
	
	private String supportingDetails;
	private List<String> shots = new ArrayList<>();
	
	private boolean relationshipFld;
	private boolean beneFld;
	private boolean payableFld;
	private boolean cedulaFld;
	private boolean documentValidFld;
	private boolean supportDtlsFld;
	private boolean borndDtlsFld;
	private boolean largeCattleFld;
	private boolean employeeAsBeneFld;
	private boolean bussinesFld;
	private boolean motorcycleFld;
	private boolean landFld;
	private boolean treeFld;
	
	private String supportingDtlsLabel;
	private String supportingDtlsPlaceHolder;
	
	private int cattleKindId;
	private List cattleKinds;
	private Map<Integer,CattleKind> cattleKindMap = Collections.synchronizedMap(new HashMap<Integer,CattleKind>());
	
	private int cattleColorId;
	private List cattleColors;
	private Map<Integer,CattleColor> cattleColorMap = Collections.synchronizedMap(new HashMap<Integer,CattleColor>());
	
	private int cattleGenderId;
	private List cattleGenders;
	private String cattleAgeDescription;
	private String cattleCOLCNo;
	private Date cattleCOLCNoDateIssue;
	private String cattleCTLCNo;
	private Date cattleCTLCNoDateIssue;
	private String cattleOtherInfo;
	
	private int cattlePurposeId;
	private List cattlePurpose;
	private Map<Integer,CattlePurpose> cattlePurposeMap = Collections.synchronizedMap(new HashMap<Integer,CattlePurpose>());
	private String cattleInformation;
	
	private String beneciaryLabel;
	private Employee employeeCoeSelected;
	
	private Date calendarFrom;
	private Date calendarTo;
	
	private List purposeList;
	private int purposeId;
	
	//private String multipurpose;
	private boolean activeMultipurpose;
	private String clearSearch;
	
	private List multiSelections = Collections.synchronizedList(new ArrayList<>());
	private List<String> multipurposeSelected = Collections.synchronizedList(new ArrayList<String>());
	//private List<Purpose> purSelected = Collections.synchronizedList(new ArrayList<Purpose>());
	//private List<Purpose> purSelectedTemp = Collections.synchronizedList(new ArrayList<Purpose>());
	
	private String motorPlateNo;
	private String motorModel;
	private String motorColor;
	
	private int landTypeId;
	private List landTypes;
	private String lotNo;
	private String areaQrt;
	private String northBound;
	private String eastBound;
	private String southBound;
	private String westBound;
	
	private String treeName;
	private String treeHills;
	private String treeLocation;
	private String treePurpose;
	
	private String openTitle;
	private boolean docTitle;
	
	public void openDocTitle(){
		if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==getDocId() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==getDocId()){
			setDocTitle(true);
		}else{
			if(DocTypes.LOW_INCOME.getId()==getDocId()) {
				setDocTitle(true);
				
				setSupportDtlsFld(false);
				setSupportingDtlsLabel("Parents Details:");
				String parents = "";
				if(getTaxPayer()!=null) {
					parents = "MR. and MRS. " + getTaxPayer().getLastname();
				}
				setSupportingDetails(parents);
				
			}else {
				setDocTitle(false);
			}
		}
	}
	
	public void loadMultipurpose(){
		
		
		String[] params = new String[0];
		String sql = "SELECT * FROM purpose WHERE isactivepurpose=1 ";
		if(getClearSearch()!=null && !getClearSearch().isEmpty()){
			sql += " AND purname like '%"+ getClearSearch().replace("--", "") +"%'";
		}
		
		/**
		 * replace by new approach as below code for v4.0
		 */
		/*
		purSelection = Collections.synchronizedList(new ArrayList<Purpose>());
		for(Purpose p : Purpose.retrieve(sql, params)){
			purSelection.add(p);
		}*/
		
		multiSelections = Collections.synchronizedList(new ArrayList<>());
		for(Purpose p : Purpose.retrieve(sql, params)){
			multiSelections.add(new SelectItem(p.getId(), p.getName()));
		}
		
	}
	
	/*public void saveMulti(){
		
		if(getPurSelected()!=null && getPurSelected().size()>0){
			String multipurpose = "";
			int cnt =1;
				for(Purpose p : getPurSelected()){
					if(cnt>1){
						multipurpose +=", "+ p.getName();
					}else{
						multipurpose +=p.getName();
					}
					cnt++;
				}
			
			setMultipurpose(multipurpose);
			
		}
	}*/
	
	public void clearNew(){
		clearFields();
		//init();
	}
	
	private void resetFld(){
		setLargeCattleFld(true);
		setRelationshipFld(false);
		setBeneFld(false);
		setPayableFld(false);
		setCedulaFld(false);
		setDocumentValidFld(false);
		setSupportDtlsFld(false);
		setBorndDtlsFld(false);
		setEmployeeAsBeneFld(true);
		setBeneciaryLabel("Beneficiary:");
		setBussinesFld(true);
		setPayable(true);
		setActiveMultipurpose(true);
		//setMultipurpose(null);
		//setPurSelected(null);
		//setPurSelectedTemp(null);
		setMotorcycleFld(true);
		setLandFld(true);
		setTreeFld(true);
		setDocTitle(false);
	}
	
	private void purposeSelected(){
		
		if(com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()== getPurposeTypeId() 
				){ 
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Provide details for " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()).toLowerCase());
			setSupportingDtlsPlaceHolder("CONFINED HOSPITAL AND ADDRESS");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()== getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(true);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Provide details for " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()).toLowerCase());
			setSupportingDtlsPlaceHolder("Date of died ex. " + DateUtils.getCurrentDateMMMMDDYYYY() + " and  Died location, etc.");	
			setBeneciaryLabel("Died Person:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
			Customer diedPerson = null;
			String dtls = "";
			String gender = "";
			String[] borndate = null;
			
			dtls = "Further Certify that <Name of Died Person> who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at <died location> and will buried/cremate at ";
			dtls += "<Cemetery address>";
			dtls += ", <Barangay>";
			dtls += ", <Municipality>";
			dtls += ", <Province>";
			dtls += " burial will be on <burial date>, " + DateUtils.getCurrentYear();
			borndate = DateUtils.getCurrentDateYYYYMMDD().split("-");
			dtls += " that he was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0];
			dtls += " of parents <father name> and <mother name>.";
			if(getTaxPayer()==null){
				setSupportingDetails(dtls);
			}
			
			if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
				dtls = "";
			if(getTaxPayer()!=null){
				diedPerson = Customer.retrieve(getTaxPayer().getCustomerid());
				gender = diedPerson.getGender().equalsIgnoreCase("1")? "he" : "she";
				dtls = "Further Certify that <Name of Died Person> who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at <died location> and will buried/cremate at ";
				dtls += diedPerson.getPurok().getPurokName();
				dtls += ", " + diedPerson.getBarangay().getName();
				dtls += ", " + diedPerson.getMunicipality().getName();
				dtls += ", " + diedPerson.getProvince().getName();
				dtls += " burial will be on <burial date>, " + DateUtils.getCurrentYear();
				borndate = diedPerson.getBirthdate().split("-");
				dtls += " that " + gender + " was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0];
				dtls += " of parents <father name> and <mother name>.";
			}
			if(getBeneficiary()!=null){
				diedPerson = Customer.retrieve(getBeneficiary().getCustomerid());
				gender = diedPerson.getGender().equalsIgnoreCase("1")? "he" : "she";
				dtls = "Further Certify that " + getBeneficiaryName() + " who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at <died location> and will buried/cremate at ";
				dtls += diedPerson.getPurok().getPurokName();
				dtls += ", " + diedPerson.getBarangay().getName();
				dtls += ", " + diedPerson.getMunicipality().getName();
				dtls += ", " + diedPerson.getProvince().getName();
				dtls += " burial will be on <burial date>, " + DateUtils.getCurrentYear();
				borndate = diedPerson.getBirthdate().split("-");
				dtls += " that " + gender + " was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0];
				dtls += " of parents <father name> and <mother name>.";
			}
			
			setSupportingDetails(dtls);
			}
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Provide details for " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()).toLowerCase());
			setSupportingDtlsPlaceHolder("Date of died ex. " + DateUtils.getCurrentDateMMMMDDYYYY() + " and  Died location, etc.");
			setBeneciaryLabel("Died Person:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
			Customer diedPerson = null;
			String dtls = "";
			String gender = "";
			String[] borndate = null;
			
			dtls = "Further Certify that <Name of Died Person> who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at their residence and will buried at ";
			dtls += "<Purok>";
			dtls += ", <Barangay>";
			dtls += ", <Municipality>";
			dtls += ", <Province>";
			dtls += " burial will be on " + DateUtils.getMonthName(DateUtils.getCurrentMonth()) + " " + DateUtils.getCurrentDay() + ", " + DateUtils.getCurrentYear();
			borndate = DateUtils.getCurrentDateYYYYMMDD().split("-");
			dtls += " and was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0] + ".";
			
			if(getTaxPayer()==null){
				setSupportingDetails(dtls);
			}
			
			if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
				dtls = "";
			if(getTaxPayer()!=null){
				diedPerson = Customer.retrieve(getTaxPayer().getCustomerid());
				gender = diedPerson.getGender().equalsIgnoreCase("1")? "he" : "she";
				dtls = "Further Certify that <Name of Died Person> who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at their residence and will buried at ";
				dtls += diedPerson.getPurok().getPurokName();
				dtls += ", " + diedPerson.getBarangay().getName();
				dtls += ", " + diedPerson.getMunicipality().getName();
				dtls += ", " + diedPerson.getProvince().getName();
				dtls += " burial will be on " + DateUtils.getMonthName(DateUtils.getCurrentMonth()) + " " + DateUtils.getCurrentDay() + ", " + DateUtils.getCurrentYear();
				borndate = diedPerson.getBirthdate().split("-");
				dtls += " and " + gender + " was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0] + ".";
			}
			if(getBeneficiary()!=null){
				diedPerson = Customer.retrieve(getBeneficiary().getCustomerid());
				gender = diedPerson.getGender().equalsIgnoreCase("1")? "he" : "she";
				dtls = "Further Certify that " + getBeneficiaryName() + " who died on " + DateUtils.getCurrentDateMMMMDDYYYY() + " at their residence and will buried at ";
				dtls += diedPerson.getPurok().getPurokName();
				dtls += ", " + diedPerson.getBarangay().getName();
				dtls += ", " + diedPerson.getMunicipality().getName();
				dtls += ", " + diedPerson.getProvince().getName();
				dtls += " burial will be on " + DateUtils.getMonthName(DateUtils.getCurrentMonth()) + " " + DateUtils.getCurrentDay() + ", " + DateUtils.getCurrentYear();
				borndate = diedPerson.getBirthdate().split("-");
				dtls += " and " + gender + " was born on " + DateUtils.getMonthName(Integer.valueOf(borndate[1])) + " " + borndate[2] + ", " + borndate[0] + ".";
			}
			
			setSupportingDetails(dtls);
			}
			
		}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId() == getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId() == getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId() == getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId() == getPurposeTypeId()){
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Source of income details:");
			setSupportingDtlsPlaceHolder("Income details");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
			String dtls="";
			String gender = "";
			Customer req=null;
			if(getTaxPayer()!=null){
				req = Customer.retrieve(getTaxPayer().getCustomerid());
				gender = req.getGender().equalsIgnoreCase("1")? "he" : "she";
				dtls = "Certify further the above subject person is known to the undersigned that " + gender + " is earning <amount> ";
				gender = req.getGender().equalsIgnoreCase("1")? "his" : "her";
				dtls += " monthly from " + gender + " <source of income> ";
				
				if(getSupportingDetails()==null || "0".equalsIgnoreCase(getSupportingDetails()) || getSupportingDetails().isEmpty()){
					setSupportingDetails(dtls);
				}
			}
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FINANCIAL.getId() == getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Provide details for " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()).toLowerCase());
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.CALAMITY.getId()== getPurposeTypeId()){
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ASSISTANCE.getId()== getPurposeTypeId()){
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_HOSPL_ASS.getId()== getPurposeTypeId()){
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Hospital Details:");
			setSupportingDtlsPlaceHolder("Hospital Name, Address");	
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()== getPurposeTypeId()||com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(false);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()== getPurposeTypeId()){	
			setLargeCattleFld(false);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Animal Details:");
			setSupportingDtlsPlaceHolder("ANIMAL TYPE, COLOR, AGE , CATTLE NO, ISSUED DATE");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Business Engagement:");
			setSupportingDtlsPlaceHolder("ex. MOBILE IT SERVICES");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(false);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== getPurposeTypeId() || 
				com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setPayable(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(false);	
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_EMPLOYMENT.getId()== getPurposeTypeId()){	
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(true);
			setCedulaFld(true);
			setDocumentValidFld(true);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting details");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Employee:");
			setEmployeeAsBeneFld(false);	
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== getPurposeTypeId() ||
				//com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== getPurposeTypeId()){	
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setPayable(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(false);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()== getPurposeTypeId()){
			setLargeCattleFld(true);
			setRelationshipFld(false);
			setBeneFld(false);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setBorndDtlsFld(true);
			setSupportDtlsFld(false);
			setSupportingDtlsLabel("Authorization Reason:");
			setSupportingDtlsPlaceHolder("ex. not feeling well");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_CERT_TRANS_ADD.getId()== getPurposeTypeId()){
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setBorndDtlsFld(true);
			setSupportDtlsFld(false);
			setSupportingDtlsLabel("Previous Address:");
			setSupportingDtlsPlaceHolder("Old Address");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);	
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
		
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ATTORNEYS_ASSISTANCE.getId()== getPurposeTypeId()){
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()== getPurposeTypeId()){
			
			if(getMultipurposeSelected()!=null && getMultipurposeSelected().size()>0) {
				//do nothing
			}else {
				loadMultipurpose();
			}
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(false);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
		
		}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()== getPurposeTypeId()){	
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setPayable(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Motorcycle Details:");
			setSupportingDtlsPlaceHolder("Plate No, Model, Color");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			setMotorcycleFld(false);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()== getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId()){	
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setPayable(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Land Details:");
			setSupportingDtlsPlaceHolder("Land Details");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			setMotorcycleFld(true);
			setLandFld(false);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()== getPurposeTypeId()){	
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setPayable(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Tree Details:");
			setSupportingDtlsPlaceHolder("Tree Details");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(false);
		
		}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()== getPurposeTypeId()){
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Company Loan Name:");
			setSupportingDtlsPlaceHolder("ex. ASA");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
		}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()== getPurposeTypeId()) {
			
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(false);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Provide details for " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()).toLowerCase());
			setSupportingDtlsPlaceHolder("Solo Parent Details");
			//setBeneciaryLabel("Died Person:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			setDocTitle(true);
			
			Customer soloPerson = null;
			String dtls = "";
			String gender = "";
			setOpenTitle("BARANGAY CERTIFICATE FOR SOLO PARENT");
			if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
				dtls = "";
			if(getTaxPayer()!=null){
				soloPerson = getTaxPayer();
				String words = Words.getTagName("solo-string-2");
				gender = soloPerson.getGender().equalsIgnoreCase("1")? "he" : "she";
				words = words.replace("<heshe>", gender);
				dtls = words;
			}
			
			
			setSupportingDetails(dtls);
			}
			
		}else{
			setLargeCattleFld(true);
			setRelationshipFld(true);
			setBeneFld(true);
			setPayableFld(false);
			setCedulaFld(false);
			setDocumentValidFld(false);
			setSupportDtlsFld(true);
			setBorndDtlsFld(true);
			setSupportingDtlsLabel("Supporting Details:");
			setSupportingDtlsPlaceHolder("N/A");
			setBeneciaryLabel("Beneficiary:");
			setEmployeeAsBeneFld(true);
			setBussinesFld(true);
			setActiveMultipurpose(true);
			//setMultipurpose(null);
			//setPurSelected(null);
			//setPurSelectedTemp(null);
			setMotorcycleFld(true);
			setLandFld(true);
			setTreeFld(true);
			
			if(getSelectedData()!=null) {
				if(DocTypes.LOW_INCOME.getId()==getSelectedData().getDocumentType()) {
					setSupportDtlsFld(false);
					setSupportingDtlsLabel("Parents Details:");
				}
			}
			
		}
	}
	
	public Login getUser() {
		return Login.getUserLogin();
	}
	
	@PostConstruct
	public void init(){
		
		Login in = getUser();
		boolean isOk = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOk = true;
		}else{
			if(Features.isEnabled(Feature.CLEARANCE)){
				isOk = true;
			}
		}
		
		if(isOk){
			
			try{
				String editClearanceName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editClearance");
				System.out.println("Check pass name: " + editClearanceName);
				if(editClearanceName!=null && !editClearanceName.isEmpty() && !"null".equalsIgnoreCase(editClearanceName)){
					setSearchClearance(editClearanceName.split(":")[0]);
					setCalendarFrom(DateUtils.convertDateString(editClearanceName.split(":")[1], DateFormat.YYYY_MM_DD()));
					setCalendarTo(DateUtils.convertDateString(editClearanceName.split(":")[1], DateFormat.YYYY_MM_DD()));
					setPurposeId(Integer.valueOf(editClearanceName.split(":")[2]));
				}
				}catch(Exception e){}
			
			String sql = " AND clz.isactiveclearance=1 AND (clz.clearissueddate>=? AND clz.clearissueddate<=?) ";
			String[] params = new String[3];
			/*params[0] = getDateFrom();
			params[1] = getDateTo();*/
			params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			
			if(getPurposeId()==0){
				sql += " AND clz.purposetype !=? ";
				params[2] = getPurposeId()+"";
			}else{
				sql += " AND clz.purposetype=? ";
				params[2] = getPurposeId()+"";
			}
			if(getSearchClearance()!=null && !getSearchClearance().isEmpty()){
				int size = getSearchClearance().length();
				if(size>=5){
					sql += " AND ( cuz.fullname like '%" + getSearchClearance().replace("--", "") + "%' OR cuz.cuscardno like '%"+ getSearchClearance().replace("--", "") +"%')";
					sql += " LIMIT 10";
					clearances = Collections.synchronizedList(new ArrayList<Clearance>());
					clearances = Clearance.retrieve(sql, params);
				}
			}else{
				clearances = Collections.synchronizedList(new ArrayList<Clearance>());
				sql += " LIMIT 10";
				clearances = Clearance.retrieve(sql, params);
			}
			
			if(clearances!=null && clearances.size()==1){
				clickItem(clearances.get(0));
			}else{	
				clearFields();
				Collections.reverse(clearances);
			}
		}
	}
	
	public void saveCedula(){
		
		
		setCedulaDetails((getCedulaTypeId()==1? "Individual" : "Corporation") + " : " +getCedulaNumber()+", Issued: "+DateUtils.convertDate(getCedulaIssued(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) + " at " + getCedulaIssuedAddress());
	}
	
	public void saveLand(){
		
		String landDtls = "";
			   landDtls = "Type:"+LandTypes.typeName(getLandTypeId());
			   landDtls += " ,Area:" + getAreaQrt();
			   landDtls += " , Lot No:" + getLotNo();
			   landDtls += " , North:" + getNorthBound();
			   landDtls += " , East:" + getEastBound();
			   landDtls += " , South:" + getSouthBound();
			   landDtls += " , West:" + getWestBound();
			   setSupportingDetails(landDtls);
	}
	
	public void saveTree(){
		String treeDtls = "";
		       treeDtls = "Name:" + getTreeName();
		       treeDtls += "No of Hills:" + getTreeHills();
		       treeDtls += "Loc.:" + getTreeLocation();
		       treeDtls += "Purpose:" + getTreePurpose();
		       setSupportingDetails(treeDtls);
	}
	
	public void saveBirthDtls(){
		
		String bornDtls = "";
		
		bornDtls += "Child name " + getChildName().toUpperCase();
		bornDtls += ", Born on "+ DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getBordDate(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()));
		bornDtls += ", at " + getBornAddress().toUpperCase();
		bornDtls += " to Mr. " + getFatherName().toUpperCase() + " and Mrs. " + getMotherName().toUpperCase();
		
		
		setBornDetails(bornDtls);
	}
	
	public void saveCattle(){
		String supDtls = CattleKind.typeName(getCattleKindId()) + 
				", Color: " + CattleColor.typeName(getCattleColorId()) + 
				", Gender: " + (getCattleGenderId()==1? "Male" : (getCattleGenderId()==2? "FEMALE" : "N/A")) + 
				", Age: " + (getCattleAgeDescription()==null? "N/A" : getCattleAgeDescription().isEmpty()? "N/A" : getCattleAgeDescription()) + 
				", COLC No.: " + (getCattleCOLCNo()==null? "N/A" : getCattleCOLCNo().isEmpty()? "N/A" : getCattleCOLCNo()) + ", Date Issue: " + (getCattleCOLCNoDateIssue()==null? "N/A" : getCattleCOLCNoDateIssue()) + 
				", CTLC No.: " + (getCattleCTLCNo()==null? "N/A" : getCattleCTLCNo().isEmpty()? "N/A" : getCattleCTLCNo() ) + " Date Issue: " + (getCattleCTLCNoDateIssue()==null? "N/A" : getCattleCTLCNoDateIssue()) + 
				", Other Info: " + (getCattleOtherInfo()==null? "N/A" : getCattleOtherInfo().isEmpty()? "N/A" : getCattleOtherInfo());
		
		//setSupportingDetails(supDtls);
		setCattleInformation(supDtls);
	}
	
	public void saveMotor(){
		
		String motor = "";
		motor = "Plate No: " + getMotorPlateNo() + ", Model: " + getMotorModel() + ", Color: " + getMotorColor();
		
		setSupportingDetails(motor.toUpperCase());
	}
	
	public List<String> autoSuggestion(String query){
		List result= new ArrayList<>();
		
		if(query!=null && !query.isEmpty()){
		
			int size = query.length();
			
			if(size>=3){
				String sql = " AND bgy.bgisactive=1 AND bgy.bgname like'%"+ query +"%'";
				for(Barangay bgy : Barangay.retrieve(sql, new String[0])){
					result.add(bgy.getName().toUpperCase());
				}
				
				sql = " AND mun.munisactive=1 AND mun.munname like'%"+ query +"%'";
				for(Municipality mun : Municipality.retrieve(sql, new String[0])){
					result.add(mun.getName().toUpperCase());
				}
				
				sql = " AND prv.provisactive=1 AND prv.provname like'%"+ query +"%'";
				for(Province prov : Province.retrieve(sql, new String[0])){
					result.add(prov.getName().toUpperCase());
				}
				
				sql = "SELECT * FROM hospitals WHERE hname like'%"+ query +"%'";
				for(Hospitals hp : Hospitals.retrieve(sql, new String[0])){
					result.add(hp.getName().toUpperCase());
				}
				
			}
		}
		
		return result;
	}
	
	public void loadTaxpayer(){
		/*taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
		
		Customer customer = new Customer();
		customer.setIsactive(1);
		if(getSearchTaxpayer()!=null && !getSearchTaxpayer().isEmpty()){
			customer.setFullname(Whitelist.remove(getSearchTaxpayer()));
		}*/
		
		taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
		
		//Customer customer = new Customer();
		//customer.setIsactive(1);
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSearchTaxpayer()!=null && !getSearchTaxpayer().isEmpty()){
			int size = getSearchTaxpayer().length();
			if(size>=5){
				sql += " AND (cus.fullname like '%" + getSearchTaxpayer().replace("--", "") +"%' OR cus.cuscardno like '%"+ getSearchTaxpayer().replace("--", "") +"%')";
				taxpayers =  Customer.retrieve(sql, new String[0]);
			}
		}else{
			sql += " order by cus.customerid DESC limit 10;";
			taxpayers =  Customer.retrieve(sql, new String[0]);
		}
		
		//Collections.reverse(customers);
		
		/*if(taxpayers!=null && taxpayers.size()==1) {
			PrimeFaces prime = PrimeFaces.current();
			clickItemOwner(taxpayers.get(0));
			String scs = "PF('multiDialogOwner').hide();";
			
			prime.executeScript(scs);
		}*/
		
		//taxpayers =  Customer.retrieve(customer);
	}
	
	public void loadBeneficiary(){
		beneficiaries = Collections.synchronizedList(new ArrayList<Customer>());
		
		/*Customer customer = new Customer();
		customer.setIsactive(1);
		if(getSeachBeneficiary()!=null && !getSeachBeneficiary().isEmpty()){
			customer.setFullname(Whitelist.remove(getSeachBeneficiary()));
		}*/
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSeachBeneficiary()!=null && !getSeachBeneficiary().isEmpty()){
			int size = getSeachBeneficiary().length();
			if(size>=5){
			//customer.setFullname(Whitelist.remove(getSearchCustomer()));
			sql += " AND (cus.fullname like '%" + getSeachBeneficiary().replace("--", "") +"%' OR cus.cuscardno like '%"+ getSeachBeneficiary().replace("--", "") +"%')";
			beneficiaries =  Customer.retrieve(sql, new String[0]);
			}
		}else{
			sql += " order by cus.customerid DESC limit 10;";
			beneficiaries =  Customer.retrieve(sql, new String[0]);
		}
		
		
	}
	
	public void clickItemOwner(Customer cuz){
		setTaxPayer(cuz);
		setCustomerName(cuz.getFirstname() + " " + cuz.getMiddlename().substring(0, 1) + ". " + cuz.getLastname());
		setPhotoId(cuz.getPhotoid());
		shots = new ArrayList<>();
		shots.add(cuz.getPhotoid());
		
		Cedula cedula = Cedula.loadCedulaIfExist(cuz);
		if(cedula!=null){
			setCedulaFld(false);
			setCedulaDetails((cedula.getCedulaType()==1? "Individual" : "Corporation") + ": " + cedula.getCedulaNo()+", Issued: "+cedula.getDateIssued() + " at " + cedula.getIssuedAddress());
			setCedulaIssued(DateUtils.convertDateString(cedula.getDateIssued(), DateFormat.YYYY_MM_DD()));
			setCedulaNumber(cedula.getCedulaNo());
		}
		ORTransaction ort = ORTransaction.loadORIfExist(cuz);
		if(ort!=null){
			setPayableFld(true);
			setPayable(false);
			setOrNumber(ort.getOrNumber());
			setAmountPaid(ort.getAmount());
		}
		
	}
	
	public void clickItemEmployee(Employee emp){
		setEmployeeCoeSelected(emp);
		setBeneficiaryName(emp.getFirstName() + " " + emp.getMiddleName().substring(0, 1) + ". " + emp.getLastName());
	}
	public void clickItemBen(Customer cuz){
		setBeneficiary(cuz);
		setBeneficiaryName(cuz.getFirstname() + " " + cuz.getMiddlename().substring(0, 1) + ". " + cuz.getLastname());
	}
	
	public void loadEmployee(){
		employees = Collections.synchronizedList(new ArrayList<Employee>());
		
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql = " AND emp.isresigned=0 AND emp.isactiveemp=1 AND (emp.firstname like '%" + getSearchEmployee() +"%' OR emp.middlename like '%" + getSearchEmployee() + "%' OR emp.lastname like '%" + getSearchEmployee() +"%')";
		}else{
			sql = " AND emp.isresigned=0 AND emp.isactiveemp=1 limit 10";
		}
		
		employees = Employee.retrieve(sql, params);
	}
	
	public void loadCoeEmployee(){
		employees = Collections.synchronizedList(new ArrayList<Employee>());
		
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql = " AND emp.isactiveemp=1 AND (emp.firstname like '%" + getSearchEmployee() +"%' OR emp.middlename like '%" + getSearchEmployee() + "%' OR emp.lastname like '%" + getSearchEmployee() +"%')";
		}else{
			sql = " AND emp.isactiveemp=1 limit 10";
		}
		
		employees = Employee.retrieve(sql, params);
	}
	
	public void clickItem(Clearance cl){
		
		clearFields();
		
		setSelectedData(cl);
		setIssuedDate(cl.getIssuedDate());
		setBarcode(cl.getBarcode());
		setOrNumber(cl.getOrNumber());
		
		//setNotes(cl.getNotes());
		setPhotoId(cl.getPhotoId());
		setAmountPaid(cl.getAmountPaid());
		setPayable(cl.getIsPayable()==1? false : true);
		setPurposeTypeId(cl.getPurposeType());
		setStatusId(cl.getStatus());
		setClearanceTypeId(cl.getClearanceType());
		setEmployee(cl.getEmployee());
		setTaxPayer(cl.getTaxPayer());
		setEmployeeName(cl.getEmployee().getFirstName() + " " + cl.getEmployee().getMiddleName().substring(0, 1) + ". "  + cl.getEmployee().getLastName());
		setCustomerName(cl.getTaxPayer().getFirstname() + " " + cl.getTaxPayer().getMiddlename().substring(0, 1) + ". " + cl.getTaxPayer().getLastname());
		
		setSearchEmployee(cl.getEmployee().getLastName());
		setSearchTaxpayer(cl.getTaxPayer().getFullname());
		
		setDocId(cl.getDocumentType());
		setDocumentValidity(cl.getDocumentValidity());
		
		if(cl.getCedulaNumber()!=null && !cl.getCedulaNumber().isEmpty()){
			String[] val = cl.getCedulaNumber().split("<:>");
			setCedulaNumber(val[0]);
			setCedulaIssued(DateUtils.convertDateString(val[1],com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()));
			String cedulaAddress = MUNICIPALITY + ", " + PROVINCE;
			int cedType = 1;
			try{cedulaAddress=val[2];}catch(IndexOutOfBoundsException e){}
			try{cedType= Integer.valueOf(val[3]);
			setCedulaTypeId(cedType);}catch(IndexOutOfBoundsException e){}
			
			setCedulaIssuedAddress(cedulaAddress);
			setCedulaDetails((cedType==1? "Individual" : "Corporation") + " : " +getCedulaNumber()+", issued: " + val[1] + " at " + cedulaAddress);
		}
		
		if(cl.getNotes()!=null && !cl.getNotes().isEmpty()){
			
			System.out.println("NOTES:>> ");
			System.out.println("Doctype: " + DocTypes.typeName(DocTypes.CERTIFICATE.getId()));
			System.out.println("Purpose: " + com.italia.marxmind.bris.enm.Purpose.typeName(cl.getPurposeType()));
			
			if(DocTypes.CERTIFICATE.getId()==cl.getDocumentType() || 
					DocTypes.CLEARANCE.getId()==cl.getDocumentType() || 
					DocTypes.INDIGENT.getId()==cl.getDocumentType() ||
					DocTypes.RESIDENCY.getId()==cl.getDocumentType() ||
					DocTypes.INCOME.getId()==cl.getDocumentType() ||
					DocTypes.CERTIFICATE_OPEN_TITLE.getId()==cl.getDocumentType() || 
					DocTypes.CLEARANCE_OPEN_TITLE.getId()==cl.getDocumentType()){
				
				if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==cl.getDocumentType() || 
						DocTypes.CLEARANCE_OPEN_TITLE.getId()==cl.getDocumentType()){
					setOpenTitle(cl.getCustomTitle().toUpperCase());
					setDocTitle(true);
				}else{
					setDocTitle(false);
					setOpenTitle(null);
				}
				
				
				if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()==cl.getPurposeType()){
					
					cattleInfo(cl);
				
				}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==cl.getPurposeType()){
					
					setSupportingDetails(cl.getNotes().toUpperCase());
				
				}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()==cl.getPurposeType()){
					
					setSupportingDetails(cl.getNotes());	
					
				}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==cl.getPurposeType()){	
					try{
					String[] note = cl.getNotes().split("<:>");
					setMotorPlateNo(note[0]);
					setMotorModel(note[1]);
					setMotorColor(note[2]);
					saveMotor();
				}catch(Exception e){}
				}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==cl.getPurposeType() || com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId()){
					try{
					String[] note = cl.getNotes().split("<:>");
					setLandTypeId(Integer.valueOf(note[0]));
					setLotNo(note[1]);
					setAreaQrt(note[2]);
					setNorthBound(note[3]);
					setEastBound(note[4]);
					setSouthBound(note[5]);
					setWestBound(note[6]);
					saveLand();
					/*String landDtls="";
					landDtls = "Type: "+LandTypes.typeName(getLandTypeId());
					landDtls += ", Lot No:"+getLotNo();
					landDtls += ", Area:"+getAreaQrt();
					landDtls += ", North:"+getNorthBound();
					landDtls += ", East:"+getEastBound();
					landDtls += ", South:"+getSouthBound();
					landDtls +=", West:"+getWestBound();
					setSupportingDetails(landDtls);*/
					}catch(Exception e){}
					
				}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==cl.getPurposeType()){
					try{
					String[] note = cl.getNotes().split("<:>");
					setTreeName(note[0]);
					setTreeHills(note[1]);
					setTreeLocation(note[2]);
					setTreePurpose(note[3]);
					saveTree();	
					}catch(Exception e){}
					
				}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==cl.getPurposeType()){
					
					setSupportingDetails(cl.getNotes());
					System.out.println("Motor details: " + getSupportingDetails());
					
				}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==cl.getPurposeType()){
					
					//if("<:>".contains(cl.getNotes())){
					try{
						setMultipurposeSelected(Collections.synchronizedList(new ArrayList<>()));
						//setPurSelectedTemp(Collections.synchronizedList(new ArrayList<Purpose>()));
						loadMultipurpose();
						for(String p : cl.getNotes().split("<:>")){
							getMultipurposeSelected().add(p);
							/*Purpose pr = new Purpose();
							pr.setId(Integer.valueOf(p));
							pr.setName(com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(p)));
							pr.setIsActivePurpose(1);
							getPurSelectedTemp().add(pr);
							System.out.println("process selected >> " + p);*/
							/*if(cnt>1){
								purpose +=", "+ com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(p));
							}else{
								purpose = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(p));
							}*/
						}
					}catch(Exception e){
						getMultipurposeSelected().add(cl.getNotes());
						/*Purpose pr = new Purpose();
						pr.setId(Integer.valueOf(cl.getNotes()));
						pr.setName(com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(cl.getNotes())));
						pr.setIsActivePurpose(1);
						getPurSelectedTemp().add(pr);
						System.out.println("catch process selected >> " + cl.getNotes());*/
						//purpose = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(cl.getNotes()));
					}
					/*}else{
						purpose = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(cl.getNotes()));
					}*/
					//setMultipurpose(purpose);
					//saveMulti();
					
					/*String multipurpose = "";
					int cnt =1;
						//for(Purpose p : getPurSelectedTemp()){
						for(String p : getMultipurposeSelected()) {
							if(cnt>1){
								multipurpose +=", "+ p;
							}else{
								multipurpose +=p;
							}
							cnt++;
						}*/
					
					//setMultipurpose(multipurpose);
					//setPurSelected(getPurSelectedTemp());
					//setPurSelectedTemp(null);
					
				}else{
				
					certClearnanceDtls(cl);
				
				}
			
			}else if(DocTypes.DEATH_CERT.getId()==cl.getDocumentType() || DocTypes.LATE_DEATH_CERT.getId()==cl.getDocumentType()){
				
				deathCertDtls(cl);
				
			}else if(DocTypes.LATE_BIRTH_REG.getId()==cl.getDocumentType() || DocTypes.LIVE_BIRTH_REG.getId()==cl.getDocumentType()){//late birth reg
				
				lateBirth(cl);
				
			}else if(DocTypes.BARANGAY_BUSINESS_PERMIT.getId()==cl.getDocumentType()){
				
				businessPermit(cl);
				
			}else if(DocTypes.COE.getId()==cl.getDocumentType()){
				long id = Long.valueOf(cl.getNotes());
				Employee emp = null;
				try{emp = Employee.retrieve(id);}catch(Exception e){}
				setEmployeeCoeSelected(emp);
				setBeneficiaryName(emp.getFirstName() + " " + emp.getMiddleName().substring(0, 1) + ". " + emp.getLastName());
			}else if(DocTypes.AUTHORIZATION_LETTER.getId()==cl.getDocumentType()){
				authorization(cl);
			}else if(DocTypes.LOW_INCOME.getId()==cl.getDocumentType()) {
				
				setDocTitle(true);
				setSupportDtlsFld(false);
				setSupportingDtlsLabel("Parents Details:");
				setSupportingDetails(cl.getNotes());
			}
			
		}else{
			setBeneficiary(null);
			setBeneficiaryName(null);
			setRelationshipId(2);
			setSupportingDetails(null);
			
			setBornDetails(null);
			setMotherName(null);
			setFatherName(null);
			setBornAddress(null);
			setBordDate(null);
			setChildName(null);
			
			//if notes null but open title
			if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==cl.getDocumentType() || 
					DocTypes.CLEARANCE_OPEN_TITLE.getId()==cl.getDocumentType()){
				setOpenTitle(cl.getCustomTitle().toUpperCase());
				setDocTitle(true);
			}else{
				setDocTitle(false);
				setOpenTitle(null);
			}
			
		}	
		
		if(cl.getPhotoId()!=null && !cl.getPhotoId().isEmpty()){
			setPhotoId(cl.getPhotoId());
			copyPhoto(cl.getPhotoId());
			shots.add(cl.getPhotoId());
		}
		
		if(cl.getMultilivelihood()!=null && cl.getMultilivelihood().size()>0){
			ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
			for(MultiLivelihood lv : cl.getMultilivelihood()){
				Livelihood liv = new Livelihood();
				try{
					liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + lv.getLivelihood().getId(), new String[0]).get(0);
					lv.setLivelihood(liv);
				}catch(Exception e){}
				ownerBusiness.add(lv.getLivelihood());
			}
			System.out.println("Business: " + ownerBusiness.get(0).getBusinessName());
			selectedBusiness = ownerBusiness;
			
		}
		
		purposeSelected();
		
	}
	
	private void businessPermit(Clearance cl){
		String[] notes = cl.getNotes().split("<:>");
		/**
		 * [0] - Control No
		 * [1] - NEW/RENEWAL
		 * [2] - MEMO
		 * [3] - Business Engaged
		 */
		setSupportingDetails(notes[3]);
	}
	
	private void deathCertDtls(Clearance cl){
		try{		
			String[] notes = cl.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = null;
			
			try{ben = Customer.retrieve(Long.valueOf(notes[0]));
			setBeneficiary(ben);
			setBeneficiaryName(ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase());
			}catch(Exception e){}
			
			setRelationshipId(Integer.valueOf(notes[1]));
			
			setSupportingDetails(notes[2]);
	
		}catch(Exception e){
			setBeneficiary(null);
			setBeneficiaryName(null);
			setRelationshipId(2);
			setSupportingDetails(null);
		}
	}
	
	private void certClearnanceDtls(Clearance cl){
		try{		
			String[] notes = cl.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = null;
			
			try{ben = Customer.retrieve(Long.valueOf(notes[0]));
			setBeneficiary(ben);
			setBeneficiaryName(ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase());
			}catch(Exception e){}
			
			setRelationshipId(Integer.valueOf(notes[1]));
			
			setSupportingDetails(notes[2]);
	
		}catch(Exception e){
			setBeneficiary(null);
			setBeneficiaryName(null);
			setRelationshipId(2);
			setSupportingDetails(null);
		}
	}
	
	private void authorization(Clearance cl){
		try{		
			String[] notes = cl.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = null;
			
			try{ben = Customer.retrieve(Long.valueOf(notes[0]));
			setBeneficiary(ben);
			setBeneficiaryName(ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase());
			}catch(Exception e){}
			
			setRelationshipId(Integer.valueOf(notes[1]));
			
			setSupportingDetails(notes[2]);
	
		}catch(Exception e){
			setBeneficiary(null);
			setBeneficiaryName(null);
			setRelationshipId(2);
			setSupportingDetails(null);
		}
	}
	
	private void lateBirth(Clearance cl){
		try{		
			String[] notes = cl.getNotes().split("<:>"); 
			/**
			 * [0] - born date
			 * [1] - born address
			 * [2] - father name
			 * [3] - mother name
			 * [4] - child name
			 */
			setBordDate(DateUtils.convertDateString(notes[0],com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()));
			setBornAddress(notes[1]);
			setFatherName(notes[2]);
			setMotherName(notes[3]);
			setChildName(notes[4]);
			
			String bornDtls = "";
			
			bornDtls += "Child name " + getChildName().toUpperCase();
			bornDtls += ", Born on "+ DateUtils.convertDateToMonthDayYear(notes[0]);
			bornDtls += ", at " + getBornAddress().toUpperCase();
			bornDtls += " to Mr. " + getFatherName().toUpperCase() + " and Mrs. " + getMotherName().toUpperCase();
			
			
			setBornDetails(bornDtls);
			
			
	
		}catch(Exception e){
			
		}
	}
	
	private void cattleInfo(Clearance cl){
		try{
			
			String[] notes = cl.getNotes().split("<:>"); 
			/**
			 * [0] - kind
			 * [1] - color
			 * [2] - gender
			 * [3] - age
			 * [4] - colc no
			 * [5] - colc no date issued
			 * [6] - ctlc no
			 * [7] - ctlc no date issued
			 * [8] - purpose
			 * [9] - others
			 */
			
			try{setCattleKindId(Integer.valueOf(notes[0]));}catch(Exception e){}
			try{setCattleColorId(Integer.valueOf(notes[1]));}catch(Exception e){}
			try{setCattleGenderId(Integer.valueOf(notes[2]));}catch(Exception e){}
			try{setCattleAgeDescription("0".equalsIgnoreCase(notes[3])? "N/A" : notes[3]);}catch(Exception e){setCattleAgeDescription("N/A");}
			try{setCattleCOLCNo("0".equalsIgnoreCase(notes[4])? "N/A" : notes[4]);}catch(Exception e){setCattleCOLCNo("N/A");}
			try{setCattleCOLCNoDateIssue("0".equalsIgnoreCase(notes[5])? null : DateUtils.convertDateString(notes[5],com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()));}catch(Exception e){setCattleCOLCNoDateIssue(null);}
			try{setCattleCTLCNo("0".equalsIgnoreCase(notes[6])? "N/A" : notes[6]);}catch(Exception e){setCattleCTLCNo("N/A");}
			try{setCattleCTLCNoDateIssue("0".equalsIgnoreCase(notes[7])? null : DateUtils.convertDateString(notes[7],com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()));}catch(Exception e){setCattleCTLCNoDateIssue(null);}
			try{setCattlePurposeId(Integer.valueOf(notes[8]));}catch(Exception e){setCattlePurposeId(0);}
			try{setCattleOtherInfo("0".equalsIgnoreCase(notes[9])? "N/A" : notes[9]);}catch(Exception e){setCattleOtherInfo("N/A");}
			
			
			
			String supDtls = CattleKind.typeName(getCattleKindId()) + 
					", Color: " + CattleColor.typeName(getCattleColorId()) + 
					", Gender: " + (getCattleGenderId()==1? "Male" : (getCattleGenderId()==2? "FEMALE" : "N/A")) + 
					", Age: " + getCattleAgeDescription() + 
					", COLC No.: " + getCattleCOLCNo() + ", Date Issue: " + DateUtils.convertDate(getCattleCOLCNoDateIssue(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) + 
					", CTLC No.: " + getCattleCTLCNo() + " Date Issue: " + DateUtils.convertDate(getCattleCTLCNoDateIssue(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) + 
					", Other Info: " + getCattleOtherInfo();
			
			//setSupportingDetails(supDtls);
			setCattleInformation(supDtls);
			
		}catch(Exception e){
			
		}
	}
	
	/**
	 * 
	 * For deletion
	 */
	private String copyPhoto(String photoId){
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
	
	public void clickItemOfficer(Employee em){
		setEmployee(em);
		setEmployeeName(em.getFirstName() + " " + em.getMiddleName().substring(0,1) + ". " + em.getLastName());
	}
	
	public void saveData(){
		
		Login in = Login.getUserLogin();
		boolean isEnable = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isEnable = true;
		}else{
			if(Features.isEnabled(Feature.CLEARANCE)){
				isEnable = true;
			}
		}
		
		if(isEnable){
			
			Clearance cl = new Clearance();
			if(getSelectedData()!=null){
				cl = getSelectedData(); 
			}else{
				cl.setIssuedDate(DateUtils.getCurrentDateYYYYMMDD());
				cl.setIsActive(1);
				if(getEmployee()!=null && getTaxPayer()!=null){
					cl.setBarcode(Clearance.generateBarcode());
				}
			}
			
			boolean isOk = true;
			
			if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==getDocId() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==getDocId()){
				if(getOpenTitle()==null || getOpenTitle().isEmpty()){
					isOk = false;
					Application.addMessage(3, "Please provide Document Title", "");
				}
			}
			
			if(getEmployee()==null && getTaxPayer()==null){
				isOk = false;
				Application.addMessage(3, "Please provide Citizen name and Officer of the day", "");
			}else{
				if(getEmployee()==null){
					Application.addMessage(3, "Please provide Signer.", "");
					isOk = false;
				}else if(getTaxPayer()==null){
					Application.addMessage(3, "Please provide Requestor name.", "");
					isOk = false;
				}	
			}	
			
			//System.out.println("Document >>> " + DocTypes.typeName(getDocId()));
			
			if(getPurposeTypeId()==0){
				Application.addMessage(3, "Please provide purpose name.", "");
				isOk = false;
			}else{
				
				if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()==getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()==getPurposeTypeId()){
					isOk = birthCondition(isOk);
				
				}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==getPurposeTypeId()){
					
					if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide cedula details", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide company loan details.", "");
						isOk = false;
					}	
					
				}else if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()==getPurposeTypeId()){
					isOk = largeCattleCondition(isOk);
				}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()==getPurposeTypeId() || 
						com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()==getPurposeTypeId() || 
								com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()==getPurposeTypeId()	){
					isOk = businessCondition(isOk);
				}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()==getPurposeTypeId()){
					isOk = businessPermitCondition(isOk);
				}else if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()==getPurposeTypeId() || 
						com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()==getPurposeTypeId()){
					isOk = fishCagePermitCondition(isOk);
				}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_EMPLOYMENT.getId()==getPurposeTypeId()){
					
					if(getEmployeeCoeSelected()==null){
						Application.addMessage(3, "Please provide Employee name.", "");
						isOk = false;
					}
					
					if(DocTypes.COE.getId()!=getDocId()){
						Application.addMessage(3, "Please select COE Certification.", "");
						isOk = false;
					}
				}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()==getPurposeTypeId()){
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide authorization reason.", "");
						isOk = false;
					}
				
				}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==getPurposeTypeId()){	
					
					if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide cedula details", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide source of income details.", "");
						isOk = false;
					}
				}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==getPurposeTypeId()){
					
					isOk = multipurposeIsOk(isOk);
					
				}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==getPurposeTypeId()){
					
					isOk = motorCycleValidation(isOk);
					
				}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId()){	
					
					isOk = validateLandDetails(isOk);
					
				}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==getPurposeTypeId()){	
					
					isOk = validateTreeDetails(isOk);	
					
				}else if(com.italia.marxmind.bris.enm.Purpose.SUMMER_JOB_REQUIREMENTS.getId()==getPurposeTypeId()){	
					
					if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide cedula details", "");
						isOk = false;
					}	
				
				}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()==getPurposeTypeId()){	
					
					if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide cedula details", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details", "");
						isOk = false;
					}	
					
				}else{
						isOk = certClearanceCondition(isOk);
				}
				
				/*if(DocTypes.CERTIFICATE.getId()==getDocId() || DocTypes.CLEARANCE.getId()==getDocId() || DocTypes.INDIGENT.getId()==getDocId() || DocTypes.DEATH_CERT.getId()==getDocId()){
					
				}else{
					
				}*/
			
			}
			
			
			if(isOk){
				if(!isPayable()){
					cl.setOrNumber(getOrNumber());
					cl.setAmountPaid(getAmountPaid());
				}
				
				if(getCedulaDetails()!=null && !getCedulaDetails().isEmpty()){
					String cedula = getCedulaNumber()+"<:>";
					cedula += DateUtils.convertDate(getCedulaIssued(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) + "<:>";
					cedula += getCedulaIssuedAddress() + "<:>";
					cedula += getCedulaTypeId();
					cl.setCedulaNumber(cedula);
				}
				
				
				String notes = "";
				
				if(DocTypes.CERTIFICATE.getId()==getDocId() || 
						DocTypes.CLEARANCE.getId()==getDocId() || 
							DocTypes.INDIGENT.getId()==getDocId() || 
								DocTypes.DEATH_CERT.getId()==getDocId() ||
									DocTypes.LATE_DEATH_CERT.getId()==getDocId() ||
										DocTypes.RESIDENCY.getId()==getDocId() ||
											DocTypes.INCOME.getId()==getDocId()
											){
				
					if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()==getPurposeTypeId()){
						
						/**
						 * [0] - kind
						 * [1] - color
						 * [2] - gender
						 * [3] - age
						 * [4] - colc no
						 * [5] - colc no date issued
						 * [6] - ctlc no
						 * [7] - ctlc no date issued
						 * [8] - purpose
						 * [9] - others
						 */
						notes = getCattleKindId() + "<:>";
						notes += getCattleColorId() + "<:>";
						notes += getCattleGenderId() + "<:>";
						notes += getCattleAgeDescription()==null? "0<:>" : (getCattleAgeDescription().isEmpty()? "0<:>" : getCattleAgeDescription().toUpperCase() +"<:>");
						notes += getCattleCOLCNo()==null? "0<:>" : (getCattleCOLCNo().isEmpty()? "0<:>" : getCattleCOLCNo() + "<:>");
						notes += getCattleCOLCNoDateIssue()==null? "0<:>" : DateUtils.convertDate(getCattleCOLCNoDateIssue(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) +"<:>";
						notes += getCattleCTLCNo()==null? "0<:>" : (getCattleCTLCNo().isEmpty()? "0<:>" : getCattleCTLCNo() +"<:>");
						notes += getCattleCTLCNoDateIssue()==null? "0<:>" : DateUtils.convertDate(getCattleCTLCNoDateIssue(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) +"<:>";
						notes += getCattlePurposeId() + "<:>";
						notes += getCattleOtherInfo()==null? "0" : (getCattleOtherInfo().isEmpty()? "0" : getCattleOtherInfo());
						
						cl.setNotes(notes);
					
					}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==getPurposeTypeId()){	
						
						notes = getMotorPlateNo() +"<:>" + getMotorModel() + "<:>" + getMotorColor();
						
						cl.setNotes(notes.toUpperCase());
					
					}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId()){	
						
						notes = getLandTypeId()+"<:>";
						notes += getLotNo()+"<:>";
						notes += getAreaQrt()+"<:>";
						notes += getNorthBound()+"<:>";
						notes += getEastBound()+"<:>";
						notes += getSouthBound()+"<:>";
						notes += getWestBound();
						cl.setNotes(notes.toUpperCase());
						
					}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==getPurposeTypeId()){
						
						notes = getTreeName()+"<:>";
						notes += getTreeHills()+"<:>";
						notes += getTreeLocation()+"<:>";
						notes += getTreePurpose();
						
						cl.setNotes(notes.toUpperCase());
						
					}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==getPurposeTypeId()){		
						
						cl.setNotes(getSupportingDetails().toUpperCase());
						
					/*}else if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()==getPurposeTypeId()){
						notes = getSupportingDetails();
						cl.setNotes(notes);*/
					}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==getPurposeTypeId()){
						
						notes = getSupportingDetails();
						cl.setNotes(notes);
						
					}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==getPurposeTypeId()){
						
						int cnt =1;
						//for(Purpose p : getPurSelected()){
						for(String p : getMultipurposeSelected()) {
							if(cnt>1){
								notes += "<:>"+ p;
							}else{
								notes = p;
							}
							cnt++;
						}
						cl.setNotes(notes);
						
						
					}else{
					/**
					 * [0] - Beneficiary
					 * [1] - Relationship
					 * [2] - supporting details
					 */
					
					if(getBeneficiary()!=null){
						notes +=getBeneficiary().getCustomerid()+"<:>"; 
					}else{
						notes +="0<:>";
					}
					
					if(getRelationshipId()>0){
						notes +=getRelationshipId()+"<:>"; 
					}else{
						notes +="0<:>";
					}
					 
					if(getSupportingDetails()!=null && !getSupportingDetails().isEmpty()){
						notes +=getSupportingDetails();
					}else{
						notes +="0";
					}
					
					cl.setNotes(notes);
					}
					
				
				}else if(DocTypes.BARANGAY_BUSINESS_PERMIT.getId()==getDocId()){	
					
					/**
					 * [0] - Control No
					 * [1] - NEW/RENEWAL
					 * [2] - MEMO
					 * [3] - Business Engaged
					 */
					
					
					notes +="0<:>"; //control no
					notes +="0<:>"; // new/renewal
					notes +="0<:>";//memo
					 
					if(getSupportingDetails()!=null && !getSupportingDetails().isEmpty()){
						notes +=getSupportingDetails();
					}else{
						notes +="0";
					}
					
					cl.setNotes(notes);
					
				}else if(DocTypes.COE.getId()==getDocId()){
					
					/**
					 * [0] - employee id
					 */
					cl.setNotes(getEmployeeCoeSelected().getId()+"");
					
				}else if(DocTypes.LATE_BIRTH_REG.getId()==getDocId() || DocTypes.LIVE_BIRTH_REG.getId()==getDocId()){//late birth
					/**
					 * [0] - born date
					 * [1] - born address
					 * [2] - father name
					 * [3] - mother name
					 * [4] - child name
					 */
					
					notes = DateUtils.convertDate(getBordDate(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD())+"<:>";
					notes += getBornAddress()+"<:>";
					notes += getFatherName()+"<:>";
					notes += getMotherName()+"<:>";
					notes += getChildName();
					
					cl.setNotes(notes);
				}else if(DocTypes.AUTHORIZATION_LETTER.getId()==getDocId()){
					/**
					 * [0] - Beneficiary
					 * [1] - Relationship
					 * [2] - supporting details
					 */
					
					if(getBeneficiary()!=null){
						notes +=getBeneficiary().getCustomerid()+"<:>"; 
					}else{
						notes +="0<:>";
					}
					
					if(getRelationshipId()>0){
						notes +=getRelationshipId()+"<:>"; 
					}else{
						notes +="0<:>";
					}
					 
					if(getSupportingDetails()!=null && !getSupportingDetails().isEmpty()){
						notes +=getSupportingDetails();
					}else{
						notes +="0";
					}
					
					cl.setNotes(notes);
					
				}else if(DocTypes.LOW_INCOME.getId()==getDocId()) {
					cl.setNotes(getSupportingDetails());
				}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==getDocId() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==getDocId()) {
					
					if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==getPurposeTypeId()){
						
						int cnt =1;
						//for(Purpose p : getPurSelected()){
						for(String p : getMultipurposeSelected()) {
							if(cnt>1){
								notes += "<:>"+ p;
							}else{
								notes = p;
							}
							cnt++;
						}
						cl.setNotes(notes);
						
					}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()==getPurposeTypeId()){
						cl.setNotes(getSupportingDetails());
					}
					
				}
				
				cl.setCustomTitle(getOpenTitle());
				
				cl.setPhotoId(getPhotoId());
				
				cl.setIsPayable(isPayable()==true? 0 : 1);
				cl.setPurposeType(getPurposeTypeId());
				cl.setStatus(2);//temporary
				cl.setClearanceType(getClearanceTypeId());
				cl.setEmployee(getEmployee());
				cl.setTaxPayer(getTaxPayer());
				cl.setUserDtls(Login.getUserLogin().getUserDtls());
				
				cl.setDocumentType(getDocId());
				cl.setDocumentValidity(getDocumentValidity());
				
				cl = Clearance.save(cl);
				List<MultiLivelihood> mulLive = Collections.synchronizedList(new ArrayList<MultiLivelihood>());
				if(getOwnerBusiness()!=null && getOwnerBusiness().size()>0){
					
					for(MultiLivelihood mu : cl.getMultilivelihood()){
						mu.delete("DELETE FROM multiLivelihood WHERE multilivid="+mu.getId(), new String[0]);
					}
					
					for(Livelihood lv : getOwnerBusiness()){
						MultiLivelihood hod = new MultiLivelihood();
						hod.setClearance(cl);
						hod.setLivelihood(lv);
						hod.setIsActive(1);
						//hod.save();
						hod = MultiLivelihood.save(hod);
						mulLive.add(hod);
					}
					System.out.println("saving livelihood" + getOwnerBusiness().size());
				}
				cl.setMultilivelihood(mulLive);
				
				//update photo of taxpayer in customer table
				if(getPhotoId()!=null && !"camera".equalsIgnoreCase(getPhotoId())){
					System.out.println("Before "+ getPhotoId() + "getPhotoId() not equal to getTaxPayer().getPhotoid(): " + getTaxPayer().getPhotoid());
					//delete physical image of taxpayer before updating the image
					if(getTaxPayer().getPhotoid()!=null){
						System.out.println("check not equal to null getTaxPayer().getPhotoid()");
						if(getPhotoId()!=null && !getPhotoId().equalsIgnoreCase(getTaxPayer().getPhotoid())){
							System.out.println(getPhotoId() + "getPhotoId() not equal to getTaxPayer().getPhotoid(): " + getTaxPayer().getPhotoid());
							taxpayerPhoto(getTaxPayer().getPhotoid());
						}
					}
					
					String sql = "UPDATE customer SET photoid=? WHERE customerid=?";
					String[] params = new String[2];
					params[0] = getPhotoId();
					params[1] = getTaxPayer().getCustomerid()+"";
					Customer.updatePhoto(sql, params);
				}
				
				deletingImages();
				setCalendarFrom(DateUtils.convertDateString(cl.getIssuedDate(), DateFormat.YYYY_MM_DD()));
				setCalendarTo(DateUtils.convertDateString(cl.getIssuedDate(), DateFormat.YYYY_MM_DD()));
				init();
				clearFields();
				runServices();
				//setSelectedData(cl);
				Application.addMessage(1, "Document has been successfully saved.", "");
			}
		}else{
			Application.addMessage(3, "This feature is not available. Please contact developer to enable this feature.", "");
		}
	}
	
	private void runServices() {
		try {
		System.out.println("running services.jar from folder services folder....");
		
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "services" + Bris.SEPERATOR.getName();
		
		File file = new File(pathFile +  "services.bat");
		String bat = "java -jar "+ Bris.PRIMARY_DRIVE.getName() + File.separator + Bris.APP_FOLDER.getName() + File.separator +"services"+ File.separator +"services.jar";
		 PrintWriter pw = new PrintWriter(new FileWriter(file));
		    pw.println(bat);
	        pw.flush();
	        pw.close();
		
		
		Runtime.getRuntime().exec(pathFile +  "services.bat");
		System.out.println("End run services....");
		
		}catch(Exception e) {e.printStackTrace();}
	}
	
	private boolean multipurposeIsOk(boolean isOk){
		if(getMultipurposeSelected()==null){
			Application.addMessage(3, "Please provide purpose.", "");
			isOk = false;
		}
		
		if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
			Application.addMessage(3, "Please provide taxpayer picture.", "");
			isOk = false;
		}
		
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	private boolean motorCycleValidation(boolean isOk){
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		if(getMotorPlateNo()==null || getMotorPlateNo().isEmpty()){
			Application.addMessage(3, "Please provide Plate Number", "");
			isOk = false;
		}
		
		if(getMotorModel()==null || getMotorModel().isEmpty()){
			Application.addMessage(3, "Please provide Model", "");
			isOk = false;
		}
		
		if(getMotorColor()==null || getMotorColor().isEmpty()){
			Application.addMessage(3, "Please provide Color", "");
			isOk = false;
		}
		
		
		
		return isOk;
	}
	
	public boolean validateLandDetails(boolean isOk){
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		if(getLandTypeId()==0){
			Application.addMessage(3, "Please land type", "");
			isOk = false;
		}
		
		if(getLotNo()==null || getLotNo().isEmpty()){
			Application.addMessage(3, "Please lot number", "");
			isOk = false;
		}
		
		if(getAreaQrt()==null || getAreaQrt().isEmpty()){
			Application.addMessage(3, "Please land area", "");
			isOk = false;
		}
		
		if(getNorthBound()==null || getNorthBound().isEmpty()){
			Application.addMessage(3, "Please details for North boundary", "");
			isOk = false;
		}
		
		if(getSouthBound()==null || getSouthBound().isEmpty()){
			Application.addMessage(3, "Please details for South boundary", "");
			isOk = false;
		}
		
		if(getEastBound()==null || getEastBound().isEmpty()){
			Application.addMessage(3, "Please details for East boundary", "");
			isOk = false;
		}
		
		if(getWestBound()==null || getWestBound().isEmpty()){
			Application.addMessage(3, "Please details for West boundary", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	public boolean validateTreeDetails(boolean isOk){
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		if(getTreeName()==null || getTreeName().isEmpty()){
			Application.addMessage(3, "Please provide Tree Name.", "");
			isOk = false;
		}
		
		if(getTreeHills()==null || getTreeHills().isEmpty()){
			Application.addMessage(3, "Please provide No of Hills.", "");
			isOk = false;
		}
		
		if(getTreeLocation()==null || getTreeLocation().isEmpty()){
			Application.addMessage(3, "Please provide Tree Location.", "");
			isOk = false;
		}
		
		if(getTreePurpose()==null || getTreePurpose().isEmpty()){
			Application.addMessage(3, "Please provide Tree Purpose.", "");
			isOk = false;
		}
		
		return isOk;
	}	
	public boolean fishCagePermitCondition(boolean isOk){
		
		if(getOwnerBusiness().size()==0){
				Application.addMessage(3, "Please provide Fish Cage.", "");
				isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
			Application.addMessage(3, "Please provide taxpayer picture.", "");
			isOk = false;
		}
		
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	public boolean businessPermitCondition(boolean isOk){
		
		if(getOwnerBusiness().size()==0){
			if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId() || getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()){
				Application.addMessage(3, "Please provide Fish Cage.", "");
				isOk = false;
			}else{
				Application.addMessage(3, "Please provide Business.", "");
				isOk = false;
			}
		}
		
		if(DocTypes.BARANGAY_BUSINESS_PERMIT.getId()!=getDocId()){
			Application.addMessage(3, "Please select Business Permit in Document Type field.", "");
			isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
			Application.addMessage(3, "Please provide taxpayer picture.", "");
			isOk = false;
		}
		
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
			Application.addMessage(3, "Please provide business engagement.", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	public boolean businessCondition(boolean isOk){
		
		if(getOwnerBusiness().size()==0){
			if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()){
				Application.addMessage(3, "Please provide Fish Cage.", "");
				isOk = false;
			}else{
				Application.addMessage(3, "Please provide Business.", "");
				isOk = false;
			}
		}
		
		if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()!=getPurposeTypeId()){
		
			if(getOrNumber()==null || getOrNumber().isEmpty()){
				Application.addMessage(3, "Please provide OR Number.", "");
				isOk = false;
			}
			
			if(getAmountPaid()<=0){
				Application.addMessage(3, "Please provide Amount.", "");
				isOk = false;
			}
		
		}
		
		if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
			Application.addMessage(3, "Please provide taxpayer picture.", "");
			isOk = false;
		}
		
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	public boolean certClearanceCondition(boolean isOk){
		
		//if(getClearanceTypeId()==ClearanceType.OTHERS.getId()){
			
			if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId() ||
						getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()
							){
				/*if(getNotes()==null || getNotes().isEmpty()){
					Application.addMessage(3, "Please provide Patient Name.", "");
					isOk = false;
				}*/
				
				if(getBeneficiary()==null){
					Application.addMessage(3, "Please provide Beneficiary Name.", "");
					isOk = false;
				}
				
				if(getRelationshipId()==0){
					Application.addMessage(3, "Please provide Relationship.", "");
					isOk = false;
				}
				
				if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
					Application.addMessage(3, "Please provide supporting details.", "");
					isOk = false;
				}
				
			}else{
				if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
					Application.addMessage(3, "Please provide citizen's picture.", "");
					isOk = false;
				}
				if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.CALAMITY.getId()){
					
					//no cedula
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()){
					
					if(DocTypes.DEATH_CERT.getId()!=getDocId()){
						Application.addMessage(3, "Please select document type is death certificate.", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details.", "");
						isOk = false;
					}
					
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide name of died person.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship to died person.", "");
						isOk = false;
					}
				
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()){
					
					if(DocTypes.LATE_DEATH_CERT.getId()!=getDocId()){
						Application.addMessage(3, "Please select document type is late death certificate.", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details.", "");
						isOk = false;
					}
					
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide name of died person.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship to died person.", "");
						isOk = false;
					}
					
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()){
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details.", "");
						isOk = false;
					}
					
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide name of died person.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship to died person.", "");
						isOk = false;
					}
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FINANCIAL.getId()){
				
				
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide Beneficiary Name.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship.", "");
						isOk = false;
					}
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.INDIGENT_ASSISTANCE.getId()){
					
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide Beneficiary Name.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship.", "");
						isOk = false;
					}
					
					/*if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details for indigent assistance", "");
						isOk = false;
					}*/
				
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.INDIGENT_HOSPL_ASS.getId()){
					
					if(getBeneficiary()==null){
						Application.addMessage(3, "Please provide Beneficiary Name.", "");
						isOk = false;
					}
					
					if(getRelationshipId()==0){
						Application.addMessage(3, "Please provide Relationship.", "");
						isOk = false;
					}
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide supporting details for hospital name and address", "");
						isOk = false;
					}	
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.INDIGENT_CERT.getId() ||
						getPurposeTypeId()== com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EDUCATION_SCHOLARSHIP_ASSISTANCE.getId() ||
								getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EMPLOYMENT.getId() ||
										getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.SPES_REQUIREMENTS.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.CHED_SCHOLARSHIP.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.SCHOOL_REG.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.TESDA_REQUIREMENT.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.KABUGWASON_REQUIREMENT.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_APPLICATION.getId() ||
												getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.CONFIRMATION_APPLICATION.getId() ||
														getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.ESC_SCHOLARSHIP_REQUIREMENTS.getId() ||
																getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_REQUIREMENTS.getId() ||
																		getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.SCHOOL_REQUIREMENTS.getId()
						
						){
					
					//do nothing	
					
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()){
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide Animal details of large cattle", "");
						isOk = false;
					}
				
				}else if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FORPS_CERT_TRANS_ADD.getId()){
					
					if(getSupportingDetails()==null || getSupportingDetails().isEmpty()){
						Application.addMessage(3, "Please provide previous address", "");
						isOk = false;
					}	
					
				}else{
					
					if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
						Application.addMessage(3, "Please provide cedula details", "");
						isOk = false;
					}
					
				}
			}
			
			
			
		/*}else if(getClearanceTypeId()==ClearanceType.NEW.getId() || getClearanceTypeId()==ClearanceType.RENEWAL.getId()){
			if(getOwnerBusiness().size()==0){
				if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()){
					Application.addMessage(3, "Please provide Fish Cage.", "");
					isOk = false;
				}else{
					Application.addMessage(3, "Please provide Business.", "");
					isOk = false;
				}
			}
			
			if(getOrNumber()==null || getOrNumber().isEmpty()){
				Application.addMessage(3, "Please provide OR Number.", "");
				isOk = false;
			}
			
			if(getAmountPaid()<=0){
				Application.addMessage(3, "Please provide Amount.", "");
				isOk = false;
			}
			
			if(getPhotoId()==null || getPhotoId().equalsIgnoreCase("camera")){
				Application.addMessage(3, "Please provide taxpayer picture.", "");
				isOk = false;
			}
			
			
			if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
				Application.addMessage(3, "Please provide cedula details", "");
				isOk = false;
			}
			
			
			
		}	*/
		
		return isOk;
	}
	
	public boolean birthCondition(boolean isOk){
		
		System.out.println("birthCondition " + isOk);		
		
		/*if(DocTypes.LATE_BIRTH_REG.getId()!=getDocId() || DocTypes.LIVE_BIRTH_REG.getId()!=getDocId()){
			Application.addMessage(3, "Please select document type to LATE BIRTH CERTIFICATE", "");
			isOk = false;
		}*/
		
		if(getBordDate()==null){
			Application.addMessage(3, "Please provide born date details", "");
			isOk = false;
		}
		
		if(getBornAddress()==null || getBornAddress().isEmpty()){
			Application.addMessage(3, "Please provide born address details", "");
			isOk = false;
		}
		
		if(getMotherName()==null || getMotherName().isEmpty()){
			Application.addMessage(3, "Please provide mother name details", "");
			isOk = false;
		}
		
		if(getFatherName()==null || getFatherName().isEmpty()){
			Application.addMessage(3, "Please provide father name details", "");
			isOk = false;
		}
		
		if(getChildName()==null || getChildName().isEmpty()){
			Application.addMessage(3, "Please provide child name details", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	private boolean largeCattleCondition(boolean isOk){
		
		if(getCattleKindId()==0){
			Application.addMessage(3, "Please provide animal details", "");
			isOk = false;
		}
		
		if(getOrNumber()==null || getOrNumber().isEmpty()){
			Application.addMessage(3, "Please provide OR Number.", "");
			isOk = false;
		}
		
		if(getAmountPaid()<=0){
			Application.addMessage(3, "Please provide Amount.", "");
			isOk = false;
		}
		
		
		if(getCedulaDetails()==null || getCedulaDetails().isEmpty()){
			Application.addMessage(3, "Please provide cedula details", "");
			isOk = false;
		}
		
		return isOk;
	}
	
	
	private void taxpayerPhoto(String photoId){
		System.out.println("Deleting taxpayer main photo deleting " + photoId);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String deleteImg = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        
        try{
        File img = new File(IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg");
        img.delete();
       
        img = new File(deleteImg + photoId + ".jpg");
        img.delete();
	 }catch(Exception e){}
	}
	
	private void deletingImages(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String deleteImg = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;// + photoId + ".jpg";
        
        try{
        System.out.println("Check before deleting.... " + getPhotoId());
       getShots().remove(getPhotoId());	
       for(String name : shots){ 	
        	if(!getPhotoId().equalsIgnoreCase(name)){
		        File img = new File(IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + name + ".jpg");
		        img.delete();
		       
		        img = new File(deleteImg + name + ".jpg");
		        img.delete();
		        System.out.println("photo deleting "+name);
        	}else{
        		System.out.println("Existing "+name);
        	}
       	}
        }catch(Exception e){}
	}
	
	
	public void clearUnPaid(){
		if(isPayable()){
			setOrNumber(null);
			setAmountPaid(0.00);
			ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
			selectedBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
		}
	}
	
	public void loadBusiness(){
		business = Collections.synchronizedList(new ArrayList<Livelihood>());
		String[] params = new String[0];
		
		String sql = " AND live.isactivelive=1";
		if(getSearchBusinessName()!=null && !getSearchBusinessName().isEmpty()){
			sql += " AND ( live.livename like '%"+getSearchBusinessName()+"%'";
			sql += " OR cuz.fullname like '%"+getSearchBusinessName()+"%' ) ";
		}else{
			sql = " AND cuz.cuslastname like '%" + getTaxPayer().getLastname()+"%' ";
		}
		
		if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()==getPurposeTypeId()){
			
			sql += " AND live.livelihoodtype=1 ";
			
			for(Livelihood lv : Livelihood.retrieve(sql, params)){
				String name = lv.getBusinessName() + "(" + lv.getAreaMeter() +", " + lv.getSupportingDetails() + ")";
				lv.setBusinessLabel(name);
				business.add(lv);
			}
			
		}else{
			
			sql += " AND live.livelihoodtype!=1 ";
			
			for(Livelihood lv : Livelihood.retrieve(sql, params)){
				lv.setBusinessLabel(lv.getBusinessName());
				business.add(lv);
			}
			
		}
		
		
	}
	
	public void ownerBusinessLoad(){
		ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
		ownerBusiness = selectedBusiness;
		if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()==getPurposeTypeId()){
			if(ownerBusiness!=null && ownerBusiness.size()>0){
						BusinessEngaged line =BusinessEngaged.businessName(ownerBusiness.get(0).getTypeLine());
						setSupportingDetails(line.getName().toUpperCase());
			}
		}
	}
	
	public void deleteBizRow(Livelihood li){
		ownerBusiness.remove(li);
		selectedBusiness.remove(li);
		if(getSelectedData()!=null){
			for(MultiLivelihood lv : getSelectedData().getMultilivelihood()){
				if(li.getId()==lv.getLivelihood().getId()){
					lv.delete();
				}
			}
		}
		Application.addMessage(1, "Successfully removed.", "");
	}
	
	public void selectedPhoto(String photoId){
		setPhotoId(photoId);
	}
	
	public void clearFields(){
		setSelectedData(null);
		setIssuedDate(null);
		setBarcode(null);
		setOrNumber(null);
		setCedulaNumber(null);
		setCedulaIssuedAddress(null);
		//setNotes(null);
		setPhotoId("camera");
		setAmountPaid(0);
		setPayable(true);
		setPurposeTypeId(0);
		setStatusId(1);
		setClearanceTypeId(1);
		setEmployee(null);
		setTaxPayer(null);
		setEmployeeName(null);
		setCustomerName(null);
		business = Collections.synchronizedList(new ArrayList<Livelihood>());
		selectedBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
		ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
		purposes = Collections.synchronizedList(new ArrayList<Purpose>());
		shots = new ArrayList<>();
		setBeneficiary(null);
		setBeneficiaryName(null);
		setSeachBeneficiary(null);
		setSupportingDetails(null);
		setDocId(1);
		setDocumentValidity(0);
		setCedulaDetails(null);
		setCedulaIssued(null);
		
		setMotherName(null);
		setFatherName(null);
		setBornAddress(null);
		setBordDate(null);
		setBornDetails(null);
		setChildName(null);
		
		resetFld();
		
		setSupportingDtlsLabel(null);
		setSupportingDtlsPlaceHolder(null);
		
		setCattleKindId(0);
		setCattleKindMap(null);
		setCattleColorId(0);
		setCattleColorMap(null);
		setCattleGenderId(0);
		setCattleAgeDescription(null);
		setCattleCOLCNo(null);
		setCattleCOLCNoDateIssue(null);
		setCattleCTLCNo(null);
		setCattleCTLCNoDateIssue(null);
		setCattleOtherInfo(null);
		setCattlePurposeId(0);
		setCattlePurposeMap(null);
		setCattleInformation(null);
		
		
		setLargeCattleFld(true);
		setRelationshipFld(true);
		setBeneFld(true);
		setPayableFld(true);
		setCedulaFld(true);
		setDocumentValidFld(true);
		setSupportDtlsFld(true);
		setBorndDtlsFld(true);
		setEmployeeAsBeneFld(true);
		setSupportingDtlsLabel("Supporting Details:");
		setSupportingDtlsPlaceHolder("N/A");
		setBeneciaryLabel("Beneficiary:");
		
		setCalendarFrom(null);
		setCalendarTo(null);
		setSearchClearance(null);
		setPurposeId(0);
		
		setEmployeeCoeSelected(null);
		
		//setPurSelected(null);
		//setPurSelectedTemp(null);
		
		//setMultipurpose(null);
		
		setLandTypeId(0);
		setAreaQrt(null);
		setLotNo(null);
		setNorthBound(null);
		setEastBound(null);
		setWestBound(null);
		setSouthBound(null);
		
		setTreeFld(true);
		setTreeName(null);
		setTreeHills(null);
		setTreeLocation(null);
		setTreePurpose(null);
		
		setDocTitle(false);
		setOpenTitle(null);
		
		setMultipurposeSelected(null);
		
	}
	
	public void deleteTmpImages(){
		
		if(getShots()!=null && getShots().size()>0){
			deletingImages();
		}
	}
	
	public void deleteRow(Clearance cl){
		if(Login.checkUserStatus()){
			cl.delete();
			clearFields();
			init();
			Application.addMessage(1, "Successfully deleted.", "");
		}
	}
	
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
	
	public void oncapture(CaptureEvent captureEvent) {
        photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
    	//filename ="cam";
        shots.add(photoId);
        
        System.out.println("Set picture name " + photoId);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        /*String newFileName = externalContext.getRealPath("") + File.separator + "resources" +
                                    File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpg";
        capturedImagePathName =File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpg";
        System.out.println("capture path " + capturedImagePathName.replace("\\", "/"));
        setCapturedImagePathName(capturedImagePathName.replace("\\", "/")); */
        
        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(driveImage));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();    
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            capturedImagePathName = contextImageLoc + photoId + ".jpg";
            System.out.println("capture path " + capturedImagePathName.replace("\\", "/"));
            setCapturedImagePathName(capturedImagePathName.replace("\\", "/"));
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
	
	public String getIssuedDate() {
		if(issuedDate==null){
			issuedDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return issuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
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
	/*public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}*/
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public boolean isPayable() {
		return payable;
	}
	public void setPayable(boolean payable) {
		this.payable = payable;
	}
	
	public void reloadPurpose(){
		//getClearanceTypes();
		setSupportingDetails(null);
		purposeSelected();
		//getDocTypes();
	}
	
	public List getPurposeTypes() {
		purposesData = Collections.synchronizedMap(new HashMap<Integer, Purpose>());
		purposeTypes = new ArrayList<>();
		
		String sql = "SELECT * FROM purpose WHERE isactivepurpose=1 ORDER BY purname";
		String[] params = new String[0];
		/*String[] params = new String[3]; 
		if(getClearanceTypeId()==1){
			sql = "SELECT * FROM purpose WHERE isactivepurpose=1 AND purname!=? AND purname!=? AND purname!=?";
			params[0] = com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getName();
			params[1] = com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getName();
			params[2] = com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getName();
		}else{
			sql = "SELECT * FROM purpose WHERE isactivepurpose=1 AND (purname=? OR purname=? OR purname=?)";
			params[0] = com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getName();
			params[1] = com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getName();
			params[2] = com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getName();
		}*/
		purposeTypes.add(new SelectItem(0, "Select Purpose"));
		for(Purpose pur : Purpose.retrieve(sql, params)){
			purposeTypes.add(new SelectItem(pur.getId(), pur.getName()));
			purposesData.put(pur.getId(), pur);
		}
		return purposeTypes;
	}
	public void setPurposeTypes(List purposeTypes) {
		this.purposeTypes = purposeTypes;
	}
	public int getPurposeTypeId() {
		return purposeTypeId;
	}
	public void setPurposeTypeId(int purposeTypeId) {
		this.purposeTypeId = purposeTypeId;
	}
	public List getStatus() {
		status = new ArrayList<>();
		for(ClearanceStatus stat : ClearanceStatus.values()){
			status.add(new SelectItem(stat.getId(), stat.getName()));
		}
		
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
	public List getClearanceTypes() {
		clearanceTypes = new ArrayList<>();
		/*for(ClearanceType type : ClearanceType.values()){
			clearanceTypes.add(new SelectItem(type.getId(), type.getName()));
		}*/
		
		if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()==getPurposeTypeId() || 
						com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()==getPurposeTypeId()){
			
			clearanceTypes.add(new SelectItem(ClearanceType.NEW.getId(), ClearanceType.NEW.getName()));
			clearanceTypes.add(new SelectItem(ClearanceType.RENEWAL.getId(), ClearanceType.RENEWAL.getName()));
		}else{
			clearanceTypes.add(new SelectItem(ClearanceType.OTHERS.getId(), ClearanceType.OTHERS.getName()));
		}
		
		return clearanceTypes;
	}
	public void setClearanceTypes(List clearanceTypes) {
		this.clearanceTypes = clearanceTypes;
	}
	public int getClearanceTypeId() {
		if(clearanceTypeId==0){
			clearanceTypeId = 1;
		}
		return clearanceTypeId;
	}
	public void setClearanceTypeId(int clearanceTypeId) {
		this.clearanceTypeId = clearanceTypeId;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getSearchEmployee() {
		return searchEmployee;
	}
	public void setSearchEmployee(String searchEmployee) {
		this.searchEmployee = searchEmployee;
	}
	public List<Employee> getEmployees() {
		return employees;
	}
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	public Customer getTaxPayer() {
		return taxPayer;
	}
	public void setTaxPayer(Customer taxPayer) {
		this.taxPayer = taxPayer;
	}
	public String getSearchTaxpayer() {
		return searchTaxpayer;
	}
	public void setSearchTaxpayer(String searchTaxpayer) {
		this.searchTaxpayer = searchTaxpayer;
	}
	public List<Customer> getTaxpayers() {
		return taxpayers;
	}
	public void setTaxpayers(List<Customer> taxpayers) {
		this.taxpayers = taxpayers;
	}
	public List<Clearance> getClearances() {
		return clearances;
	}
	public void setClearances(List<Clearance> clearances) {
		this.clearances = clearances;
	}
	public Clearance getSelectedData() {
		return selectedData;
	}
	public void setSelectedData(Clearance selectedData) {
		this.selectedData = selectedData;
	}
	public String getSearchClearance() {
		return searchClearance;
	}
	public void setSearchClearance(String searchClearance) {
		this.searchClearance = searchClearance;
	}

	/*public String getDateFrom() {
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
	}*/

	public String getEmployeeName() {
		if(employeeName==null) {
			try{
				OD od = OD.retrieveAssignedOfficer(DateUtils.getCurrentMonth(), DateUtils.getCurrentDay(), DateUtils.getCurrentYear());
				employee = od.getOfficer();
				setEmployeeName(employee.getFirstName() + " " + employee.getMiddleName().substring(0,1) + ". " + employee.getLastName());
			}catch(Exception e) {}
		}
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Map<Integer, Purpose> getPurposesData() {
		return purposesData;
	}

	public void setPurposesData(Map<Integer, Purpose> purposesData) {
		this.purposesData = purposesData;
	}

	public String getSearchBusinessName() {
		return searchBusinessName;
	}

	public void setSearchBusinessName(String searchBusinessName) {
		this.searchBusinessName = searchBusinessName;
	}

	public List<Livelihood> getBusiness() {
		return business;
	}

	public void setBusiness(List<Livelihood> business) {
		this.business = business;
	}

	public List<Livelihood> getSelectedBusiness() {
		return selectedBusiness;
	}

	public void setSelectedBusiness(List<Livelihood> selectedBusiness) {
		this.selectedBusiness = selectedBusiness;
	}

	public List<Livelihood> getOwnerBusiness() {
		return ownerBusiness;
	}

	public void setOwnerBusiness(List<Livelihood> ownerBusiness) {
		this.ownerBusiness = ownerBusiness;
	}

	public List<Purpose> getPurposes() {
		return purposes;
	}

	public void setPurposes(List<Purpose> purposes) {
		this.purposes = purposes;
	}

	public String getCapturedImagePathName() {
		return capturedImagePathName;
	}

	public void setCapturedImagePathName(String capturedImagePathName) {
		this.capturedImagePathName = capturedImagePathName;
	}

/*private String getAppellation(Customer customer){
	String appellation="";
	
	if("1".equalsIgnoreCase(customer.getGender())){//male
		appellation = "Mr.";
	}else{//female
		if(CivilStatus.SINGLE.getId()==customer.getCivilStatus()){
			appellation = "Miss";
		}else{
			appellation = "Mrs.";
		}
	}
	
	return appellation.toUpperCase();
}

private String documentNote(Clearance clr) {
	String note = "";
	
	if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== clr.getPurposeType()){
		note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		note += "New Business";
	}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== clr.getPurposeType()){
		note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		note += "Business Renewal";
	}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType()){
		note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
	}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){
		String valid = clr.getDocumentValidity()==0? "6 Months" : (clr.getDocumentValidity()==1? " 1 Month" : clr.getDocumentValidity() + " Months");
		note = "Valid Only for " + valid +"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
	}else if(com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType()){
		note = "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
	}else if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== clr.getPurposeType()){
		note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		note += "Fish Cage Application";
	}else if(com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== clr.getPurposeType()){
		note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		note += "Fish Cage Renewal";
	}else {
		String valid = clr.getDocumentValidity()==0? "6 Months" : (clr.getDocumentValidity()==1? " 1 Month" : clr.getDocumentValidity() + " Months");
		note = "Valid Only for " + valid +"\n";
		note += "Brgy. Document No " + clr.getId() +"\n";
		note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			note += "Certificate";
		}else if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			note += "Clearance";
		}else {
			note += DocTypes.typeName(clr.getDocumentType());
		}
	}
	
	return note;
}*/


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

	public int getRelationshipId() {
		if(relationshipId==0){
			relationshipId = 2;
		}
		return relationshipId;
	}

	public void setRelationshipId(int relationshipId) {
		this.relationshipId = relationshipId;
	}

	public List getRelationships() {
		relationships = new ArrayList<>();
		for(Relationships rel : Relationships.values()){
			relationships.add(new SelectItem(rel.getId(), rel.getName()));
		}
		return relationships;
	}

	public void setRelationships(List relationships) {
		this.relationships = relationships;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public Customer getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(Customer beneficiary) {
		this.beneficiary = beneficiary;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getSeachBeneficiary() {
		return seachBeneficiary;
	}

	public void setSeachBeneficiary(String seachBeneficiary) {
		this.seachBeneficiary = seachBeneficiary;
	}

	public List<Customer> getBeneficiaries() {
		return beneficiaries;
	}

	public void setBeneficiaries(List<Customer> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	public String getSupportingDetails() {
		return supportingDetails;
	}

	public void setSupportingDetails(String supportingDetails) {
		this.supportingDetails = supportingDetails;
	}

	public List<String> getShots() {
		return shots;
	}

	public void setShots(List<String> shots) {
		this.shots = shots;
	}

	public int getDocId() {
		if(docId==0){
			docId = 1;
		}
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public List getDocTypes() {
		
		docTypes = new ArrayList<>();
		/*for(DocTypes type: DocTypes.values()){
			docTypes.add(new SelectItem(type.getId(), type.getName()));
		}*/
		
		System.out.println("Selected Type purpose : " + com.italia.marxmind.bris.enm.Purpose.typeName(getPurposeTypeId()));
		
		if( 
					com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId()==getPurposeTypeId() ||
								com.italia.marxmind.bris.enm.Purpose.CALAMITY.getId()==getPurposeTypeId() ||
										com.italia.marxmind.bris.enm.Purpose.OTHER_LEGAL_MATTERS.getId()==getPurposeTypeId()
									){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ATTORNEYS_ASSISTANCE.getId()==getPurposeTypeId()){	
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.SUMMER_JOB_REQUIREMENTS.getId()==getPurposeTypeId()){		
		
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.POLICE.getId()==getPurposeTypeId() ||
					com.italia.marxmind.bris.enm.Purpose.NBI.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.PASSPORT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.EMPLOYMENT_ABROAD.getId()==getPurposeTypeId() ||
								com.italia.marxmind.bris.enm.Purpose.JOB_APPLICATION.getId()==getPurposeTypeId() ||
									com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN.getId()==getPurposeTypeId() ||
										com.italia.marxmind.bris.enm.Purpose.CAR_LOAN.getId()==getPurposeTypeId() ||
											com.italia.marxmind.bris.enm.Purpose.TO_TAKE_BOARD_LICENSE.getId()==getPurposeTypeId() ||
												com.italia.marxmind.bris.enm.Purpose.AFP_TRAINING.getId()==getPurposeTypeId() ||
													com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN.getId()==getPurposeTypeId() ||
														com.italia.marxmind.bris.enm.Purpose.SSS_LOAN.getId()==getPurposeTypeId()){		
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()==getPurposeTypeId() || 
					com.italia.marxmind.bris.enm.Purpose.FINANCIAL.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.INDIGENT_ASSISTANCE.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.INDIGENT_CERT.getId()==getPurposeTypeId() ||
								com.italia.marxmind.bris.enm.Purpose.INDIGENT_HOSPL_ASS.getId()==getPurposeTypeId() ||
									com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()==getPurposeTypeId() ||
										com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EMPLOYMENT.getId()==getPurposeTypeId() ||
												com.italia.marxmind.bris.enm.Purpose.SCHOOL_REG.getId()==getPurposeTypeId() ||
													com.italia.marxmind.bris.enm.Purpose.CHED_SCHOLARSHIP.getId()==getPurposeTypeId() ||
														com.italia.marxmind.bris.enm.Purpose.TESDA_REQUIREMENT.getId()==getPurposeTypeId() ||
															com.italia.marxmind.bris.enm.Purpose.KABUGWASON_REQUIREMENT.getId()==getPurposeTypeId() ||
																com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_APPLICATION.getId()==getPurposeTypeId() ||
																	com.italia.marxmind.bris.enm.Purpose.CONFIRMATION_APPLICATION.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.LOW_INCOME.getId(), DocTypes.LOW_INCOME.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BANK_ACCOUNT.getId()==getPurposeTypeId() || 
											com.italia.marxmind.bris.enm.Purpose.LENDING.getId()==getPurposeTypeId() ||
												com.italia.marxmind.bris.enm.Purpose.LOAN.getId()==getPurposeTypeId() ||
													com.italia.marxmind.bris.enm.Purpose.FINANCING.getId()==getPurposeTypeId() ||
														com.italia.marxmind.bris.enm.Purpose.POSTAL.getId()==getPurposeTypeId() ||
															
																com.italia.marxmind.bris.enm.Purpose.DRIVER_APPLICATION.getId()==getPurposeTypeId() ||
																	com.italia.marxmind.bris.enm.Purpose.DRIVER_RENEWAL.getId()==getPurposeTypeId() ||
																		com.italia.marxmind.bris.enm.Purpose.FIREARMS_APPLICATION.getId()==getPurposeTypeId() ||
																			com.italia.marxmind.bris.enm.Purpose.FIREARMS_RENEWAL.getId()==getPurposeTypeId() ||
																				com.italia.marxmind.bris.enm.Purpose.SKYLAB.getId()==getPurposeTypeId() ||
																					com.italia.marxmind.bris.enm.Purpose.TRAVEL.getId()==getPurposeTypeId() ||
																						
																							com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE.getId()==getPurposeTypeId() ||
																							
																								
																									com.italia.marxmind.bris.enm.Purpose.JEEPNEY.getId()==getPurposeTypeId() ||
																										com.italia.marxmind.bris.enm.Purpose.VAN.getId()==getPurposeTypeId() ||
																											com.italia.marxmind.bris.enm.Purpose.BUS.getId()==getPurposeTypeId() ||
																												com.italia.marxmind.bris.enm.Purpose.GOOD_MORAL.getId()==getPurposeTypeId() ||
																													com.italia.marxmind.bris.enm.Purpose.OJT.getId()==getPurposeTypeId() ||		
																																com.italia.marxmind.bris.enm.Purpose.OWWA.getId()==getPurposeTypeId() ||
																																	com.italia.marxmind.bris.enm.Purpose.DOCUMENTARY_STAMP.getId()==getPurposeTypeId() ||
																																							com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==getPurposeTypeId()
																																				){
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()==getPurposeTypeId() || 
				com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			docTypes.add(new SelectItem(DocTypes.INCOME.getId(), DocTypes.INCOME.getName()));
		
		}else if(com.italia.marxmind.bris.enm.Purpose.SPES_REQUIREMENTS.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EDUCATION_SCHOLARSHIP_ASSISTANCE.getId()==getPurposeTypeId() ||
						com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_REQUIREMENTS.getId()==getPurposeTypeId() ||
								com.italia.marxmind.bris.enm.Purpose.SCHOOL_REQUIREMENTS.getId()==getPurposeTypeId()
				){
			
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			docTypes.add(new SelectItem(DocTypes.INCOME.getId(), DocTypes.INCOME.getName()));
			docTypes.add(new SelectItem(DocTypes.LOW_INCOME.getId(), DocTypes.LOW_INCOME.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
		
		}else if(com.italia.marxmind.bris.enm.Purpose.ESC_SCHOLARSHIP_REQUIREMENTS.getId()==getPurposeTypeId()){	
			
			docTypes.add(new SelectItem(DocTypes.INCOME.getId(), DocTypes.INCOME.getName()));
			docTypes.add(new SelectItem(DocTypes.LOW_INCOME.getId(), DocTypes.LOW_INCOME.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.LATE_BIRTH_REG.getId(), DocTypes.LATE_BIRTH_REG.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.DEATH_CERT.getId(), DocTypes.DEATH_CERT.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.LATE_DEATH_CERT.getId(), DocTypes.LATE_DEATH_CERT.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.BARANGAY_BUSINESS_PERMIT.getId(), DocTypes.BARANGAY_BUSINESS_PERMIT.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_EMPLOYMENT.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.COE.getId(), DocTypes.COE.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()==getPurposeTypeId() || 
				com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.AUTHORIZATION_LETTER.getId(), DocTypes.AUTHORIZATION_LETTER.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_CERT_TRANS_ADD.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()==getPurposeTypeId()){
			docTypes.add(new SelectItem(DocTypes.LIVE_BIRTH_REG.getId(), DocTypes.LIVE_BIRTH_REG.getName()));
		}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==getPurposeTypeId()){	
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
			docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() || com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==getPurposeTypeId()){	
				
				docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
				docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
				docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));	
				docTypes.add(new SelectItem(DocTypes.RESIDENCY.getId(), DocTypes.RESIDENCY.getName()));
				docTypes.add(new SelectItem(DocTypes.INCOME.getId(), DocTypes.INCOME.getName()));
				
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_RESIDENCY.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.DIRECT_SELLER_AGENT_APPLICATION.getId()==getPurposeTypeId()){
			System.out.println("Residency.....");
			docTypes.add(new SelectItem(DocTypes.RESIDENCY.getId(), DocTypes.RESIDENCY.getName()));
		
		}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()==getPurposeTypeId()){		
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			
		}else{
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE_OPEN_TITLE.getId(), DocTypes.CERTIFICATE_OPEN_TITLE.getName()));
			docTypes.add(new SelectItem(DocTypes.CLEARANCE_OPEN_TITLE.getId(), DocTypes.CLEARANCE_OPEN_TITLE.getName()));
			
		}
		
		return docTypes;
	}

	public void setDocTypes(List docTypes) {
		this.docTypes = docTypes;
	}

	public int getDocumentValidity() {
		return documentValidity;
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

	public String getCedulaDetails() {
		return cedulaDetails;
	}

	public void setCedulaDetails(String cedulaDetails) {
		this.cedulaDetails = cedulaDetails;
	}

	public void setDocumentValidity(int documentValidity) {
		this.documentValidity = documentValidity;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getBornAddress() {
		return bornAddress;
	}

	public void setBornAddress(String bornAddress) {
		this.bornAddress = bornAddress;
	}

	public Date getBordDate() {
		if(bordDate==null){
			bordDate = DateUtils.getDateToday();
		}
		return bordDate;
	}

	public void setBordDate(Date bordDate) {
		this.bordDate = bordDate;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public String getBornDetails() {
		return bornDetails;
	}

	public void setBornDetails(String bornDetails) {
		this.bornDetails = bornDetails;
	}

	public boolean isRelationshipFld() {
		return relationshipFld;
	}

	public void setRelationshipFld(boolean relationshipFld) {
		this.relationshipFld = relationshipFld;
	}

	public boolean isBeneFld() {
		return beneFld;
	}

	public void setBeneFld(boolean beneFld) {
		this.beneFld = beneFld;
	}

	public boolean isPayableFld() {
		return payableFld;
	}

	public void setPayableFld(boolean payableFld) {
		this.payableFld = payableFld;
	}

	public boolean isCedulaFld() {
		return cedulaFld;
	}

	public void setCedulaFld(boolean cedulaFld) {
		this.cedulaFld = cedulaFld;
	}

	public boolean isDocumentValidFld() {
		return documentValidFld;
	}

	public void setDocumentValidFld(boolean documentValidFld) {
		this.documentValidFld = documentValidFld;
	}

	public boolean isSupportDtlsFld() {
		return supportDtlsFld;
	}

	public void setSupportDtlsFld(boolean supportDtlsFld) {
		this.supportDtlsFld = supportDtlsFld;
	}

	public boolean isBorndDtlsFld() {
		return borndDtlsFld;
	}

	public void setBorndDtlsFld(boolean borndDtlsFld) {
		this.borndDtlsFld = borndDtlsFld;
	}

	public String getSupportingDtlsLabel() {
		if(supportingDtlsLabel==null){
			supportingDtlsLabel = "Supporting Details:";
		}
		return supportingDtlsLabel;
	}

	public void setSupportingDtlsLabel(String supportingDtlsLabel) {
		this.supportingDtlsLabel = supportingDtlsLabel;
	}

	public String getSupportingDtlsPlaceHolder() {
		if(supportingDtlsPlaceHolder==null){
			supportingDtlsPlaceHolder = "SUPPORTING DETAILS";
		}
		return supportingDtlsPlaceHolder;
	}

	public void setSupportingDtlsPlaceHolder(String supportingDtlsPlaceHolder) {
		this.supportingDtlsPlaceHolder = supportingDtlsPlaceHolder;
	}

	public int getCattleColorId() {
		return cattleColorId;
	}

	public void setCattleColorId(int cattleColorId) {
		this.cattleColorId = cattleColorId;
	}

	public List getCattleColors() {
		cattleColors = new ArrayList<>();
		cattleColorMap = Collections.synchronizedMap(new HashMap<Integer, CattleColor>());
		for(CattleColor color : CattleColor.values()){
			cattleColors.add(new SelectItem(color.getId(), color.getName()));
			cattleColorMap.put(color.getId(), color);
		}
		return cattleColors;
	}

	public void setCattleColors(List cattleColors) {
		this.cattleColors = cattleColors;
	}

	public Map<Integer, CattleColor> getCattleColorMap() {
		return cattleColorMap;
	}

	public void setCattleColorMap(Map<Integer, CattleColor> cattleColorMap) {
		this.cattleColorMap = cattleColorMap;
	}

	public String getCattleAgeDescription() {
		return cattleAgeDescription;
	}

	public void setCattleAgeDescription(String cattleAgeDescription) {
		this.cattleAgeDescription = cattleAgeDescription;
	}

	public String getCattleCOLCNo() {
		return cattleCOLCNo;
	}

	public void setCattleCOLCNo(String cattleCOLCNo) {
		this.cattleCOLCNo = cattleCOLCNo;
	}

	public Date getCattleCOLCNoDateIssue() {
		return cattleCOLCNoDateIssue;
	}

	public void setCattleCOLCNoDateIssue(Date cattleCOLCNoDateIssue) {
		this.cattleCOLCNoDateIssue = cattleCOLCNoDateIssue;
	}

	public String getCattleCTLCNo() {
		return cattleCTLCNo;
	}

	public void setCattleCTLCNo(String cattleCTLCNo) {
		this.cattleCTLCNo = cattleCTLCNo;
	}

	public Date getCattleCTLCNoDateIssue() {
		return cattleCTLCNoDateIssue;
	}

	public void setCattleCTLCNoDateIssue(Date cattleCTLCNoDateIssue) {
		this.cattleCTLCNoDateIssue = cattleCTLCNoDateIssue;
	}

	public String getCattleOtherInfo() {
		return cattleOtherInfo;
	}

	public void setCattleOtherInfo(String cattleOtherInfo) {
		this.cattleOtherInfo = cattleOtherInfo;
	}

	public int getCattlePurposeId() {
		return cattlePurposeId;
	}

	public void setCattlePurposeId(int cattlePurposeId) {
		this.cattlePurposeId = cattlePurposeId;
	}

	public List getCattlePurpose() {
		cattlePurpose = new ArrayList<>();
		cattlePurposeMap = Collections.synchronizedMap(new HashMap<Integer, CattlePurpose>());
		
		for(CattlePurpose cattle : CattlePurpose.values()){
			cattlePurpose.add(new SelectItem(cattle.getId(), cattle.getName()));
			cattlePurposeMap.put(cattle.getId(), cattle);
		}
		
		return cattlePurpose;
	}

	public void setCattlePurpose(List cattlePurpose) {
		this.cattlePurpose = cattlePurpose;
	}

	public Map<Integer, CattlePurpose> getCattlePurposeMap() {
		return cattlePurposeMap;
	}

	public void setCattlePurposeMap(Map<Integer, CattlePurpose> cattlePurposeMap) {
		this.cattlePurposeMap = cattlePurposeMap;
	}

	public int getCattleKindId() {
		return cattleKindId;
	}

	public void setCattleKindId(int cattleKindId) {
		this.cattleKindId = cattleKindId;
	}

	public List getCattleKinds() {
		cattleKinds = new ArrayList<>();
		cattleKindMap = Collections.synchronizedMap(new HashMap<Integer, CattleKind>());
		
		for(CattleKind kind : CattleKind.values()){
			cattleKinds.add(new SelectItem(kind.getId(), kind.getName()));
			cattleKindMap.put(kind.getId(), kind);
		}
		
		return cattleKinds;
	}

	public void setCattleKinds(List cattleKinds) {
		this.cattleKinds = cattleKinds;
	}

	public Map<Integer, CattleKind> getCattleKindMap() {
		return cattleKindMap;
	}

	public void setCattleKindMap(Map<Integer, CattleKind> cattleKindMap) {
		this.cattleKindMap = cattleKindMap;
	}

	public int getCattleGenderId() {
		return cattleGenderId;
	}

	public void setCattleGenderId(int cattleGenderId) {
		this.cattleGenderId = cattleGenderId;
	}

	public List getCattleGenders() {
		cattleGenders = new ArrayList<>();
		cattleGenders.add(new SelectItem(0, "N/A"));
		cattleGenders.add(new SelectItem(1, "MALE"));
		cattleGenders.add(new SelectItem(2, "FEMALE"));
		
		return cattleGenders;
	}

	public void setCattleGenders(List cattleGenders) {
		this.cattleGenders = cattleGenders;
	}

	public boolean isLargeCattleFld() {
		return largeCattleFld;
	}

	public void setLargeCattleFld(boolean largeCattleFld) {
		this.largeCattleFld = largeCattleFld;
	}

	public String getCattleInformation() {
		return cattleInformation;
	}

	public void setCattleInformation(String cattleInformation) {
		this.cattleInformation = cattleInformation;
	}

	public String getBeneciaryLabel() {
		if(beneciaryLabel==null){
			beneciaryLabel = "Beneficiary:";
		}
		return beneciaryLabel;
	}

	public void setBeneciaryLabel(String beneciaryLabel) {
		this.beneciaryLabel = beneciaryLabel;
	}

	public boolean isEmployeeAsBeneFld() {
		return employeeAsBeneFld;
	}

	public void setEmployeeAsBeneFld(boolean employeeAsBeneFld) {
		this.employeeAsBeneFld = employeeAsBeneFld;
	}

	public Employee getEmployeeCoeSelected() {
		return employeeCoeSelected;
	}

	public void setEmployeeCoeSelected(Employee employeeCoeSelected) {
		this.employeeCoeSelected = employeeCoeSelected;
	}

	public boolean isBussinesFld() {
		return bussinesFld;
	}

	public void setBussinesFld(boolean bussinesFld) {
		this.bussinesFld = bussinesFld;
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

	public List getPurposeList() {
		
		purposeList = Collections.synchronizedList(new ArrayList<>());
		purposeList.add(new SelectItem(0, "All Purpose"));
		for(Purpose p : Purpose.retrieve(" SELECT * FROM purpose WHERE isactivepurpose=1 ORDER BY purname", new String[0])){
			purposeList.add(new SelectItem(p.getId(), p.getName()));
		}
		
		return purposeList;
	}

	public void setPurposeList(List purposeList) {
		this.purposeList = purposeList;
	}

	public int getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(int purposeId) {
		this.purposeId = purposeId;
	}
	
	
	public List<String> autoORNumber(String query){
		int size = query.length();
		if(size>=3){
			return Clearance.retrieve(query, "ornumber"," ORDER BY ornumber DESC limit 10");
		}else{
			return Collections.synchronizedList(new ArrayList<String>());
		}
	}

	/*public String getMultipurpose() {
		return multipurpose;
	}

	public void setMultipurpose(String multipurpose) {
		this.multipurpose = multipurpose;
	}*/

	public boolean isActiveMultipurpose() {
		return activeMultipurpose;
	}

	public void setActiveMultipurpose(boolean activeMultipurpose) {
		this.activeMultipurpose = activeMultipurpose;
	}

	/*public List<Purpose> getPurSelected() {
		return purSelected;
	}

	public void setPurSelected(List<Purpose> purSelected) {
		this.purSelected = purSelected;
	}*/

	public String getClearSearch() {
		return clearSearch;
	}

	public void setClearSearch(String clearSearch) {
		this.clearSearch = clearSearch;
	}

	/*public List<Purpose> getPurSelectedTemp() {
		return purSelectedTemp;
	}

	public void setPurSelectedTemp(List<Purpose> purSelectedTemp) {
		this.purSelectedTemp = purSelectedTemp;
	}*/

	public boolean isMotorcycleFld() {
		return motorcycleFld;
	}

	public void setMotorcycleFld(boolean motorcycleFld) {
		this.motorcycleFld = motorcycleFld;
	}

	public String getMotorPlateNo() {
		return motorPlateNo;
	}

	public void setMotorPlateNo(String motorPlateNo) {
		this.motorPlateNo = motorPlateNo;
	}

	public String getMotorModel() {
		return motorModel;
	}

	public void setMotorModel(String motorModel) {
		this.motorModel = motorModel;
	}

	public String getMotorColor() {
		return motorColor;
	}

	public void setMotorColor(String motorColor) {
		this.motorColor = motorColor;
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

	public int getLandTypeId() {
		if(landTypeId==0){
			landTypeId = 1;
		}
		return landTypeId;
	}

	public void setLandTypeId(int landTypeId) {
		this.landTypeId = landTypeId;
	}

	public List getLandTypes() {
		landTypes = new ArrayList<>();
		
		for(LandTypes type : LandTypes.values()){
			landTypes.add(new SelectItem(type.getId(), type.getName()));
		}
		
		return landTypes;
	}

	public void setLandTypes(List landTypes) {
		this.landTypes = landTypes;
	}

	public String getLotNo() {
		return lotNo;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	public String getAreaQrt() {
		return areaQrt;
	}

	public void setAreaQrt(String areaQrt) {
		this.areaQrt = areaQrt;
	}

	public String getNorthBound() {
		return northBound;
	}

	public void setNorthBound(String northBound) {
		this.northBound = northBound;
	}

	public String getEastBound() {
		return eastBound;
	}

	public void setEastBound(String eastBound) {
		this.eastBound = eastBound;
	}

	public String getSouthBound() {
		return southBound;
	}

	public void setSouthBound(String southBound) {
		this.southBound = southBound;
	}

	public String getWestBound() {
		return westBound;
	}

	public void setWestBound(String westBound) {
		this.westBound = westBound;
	}

	public boolean isLandFld() {
		return landFld;
	}

	public void setLandFld(boolean landFld) {
		this.landFld = landFld;
	}

	public boolean isTreeFld() {
		return treeFld;
	}

	public void setTreeFld(boolean treeFld) {
		this.treeFld = treeFld;
	}

	public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public String getTreeHills() {
		return treeHills;
	}

	public void setTreeHills(String treeHills) {
		this.treeHills = treeHills;
	}

	public String getTreeLocation() {
		return treeLocation;
	}

	public void setTreeLocation(String treeLocation) {
		this.treeLocation = treeLocation;
	}

	public String getTreePurpose() {
		return treePurpose;
	}

	public void setTreePurpose(String treePurpose) {
		this.treePurpose = treePurpose;
	}

	public String getOpenTitle() {
		return openTitle;
	}

	public void setOpenTitle(String openTitle) {
		this.openTitle = openTitle;
	}

	public boolean isDocTitle() {
		return docTitle;
	}

	public void setDocTitle(boolean docTitle) {
		this.docTitle = docTitle;
	}

	public List getMultiSelections() {
		return multiSelections;
	}

	public void setMultiSelections(List multiSelections) {
		this.multiSelections = multiSelections;
	}

	public List<String> getMultipurposeSelected() {
		return multipurposeSelected;
	}

	public void setMultipurposeSelected(List<String> multipurposeSelected) {
		this.multipurposeSelected = multipurposeSelected;
	}
	
}











