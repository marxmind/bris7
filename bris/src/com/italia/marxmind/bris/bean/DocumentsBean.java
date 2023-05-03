package com.italia.marxmind.bris.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.enterprise.context.SessionScoped;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.BusinessEngaged;
import com.italia.marxmind.bris.controller.Cedula;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.DocumentPrinting;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Livelihood;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.MultiLivelihood;
import com.italia.marxmind.bris.controller.OD;
import com.italia.marxmind.bris.controller.ORTransaction;
import com.italia.marxmind.bris.controller.Purpose;
import com.italia.marxmind.bris.controller.Words;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CattleColor;
import com.italia.marxmind.bris.enm.CattleKind;
import com.italia.marxmind.bris.enm.CattlePurpose;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.DocTypes;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.LandTypes;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Mark Italia
 * @since 12/11/2018
 * @version 1.0
 *
 */

@Named
@SessionScoped
public class DocumentsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365476851L;
	
	private String capturedImagePathName;
	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE) ;
	
	private String issuedDate;
	//private Customer requestor;
	private String photoId="camera";
	
	private int purposeTypeId;
	private List purposeTypes;
	
	private List<String> shots = new ArrayList<>();
	private String supportingDetails;
	
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
	private Map<Integer,CattleKind> cattleKindMap = new HashMap<Integer,CattleKind>();//Collections.synchronizedMap(new HashMap<Integer,CattleKind>());
	
	private int cattleColorId;
	private List cattleColors;
	private Map<Integer,CattleColor> cattleColorMap = new HashMap<Integer,CattleColor>();//Collections.synchronizedMap(new HashMap<Integer,CattleColor>());
	
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
	private Map<Integer,CattlePurpose> cattlePurposeMap = new HashMap<Integer,CattlePurpose>();//Collections.synchronizedMap(new HashMap<Integer,CattlePurpose>());
	private String cattleInformation;
	
	private String beneciaryLabel="Beneciary Name:";
	private Employee employeeCoeSelected;
	
	private Date calendarFrom;
	private Date calendarTo;
	
	private List purposeList;
	private int purposeId;
	
	private boolean activeMultipurpose;
	private String clearSearch;
	
	private List multiSelections = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
	private List<String> multipurposeSelected = new ArrayList<String>();//Collections.synchronizedList(new ArrayList<String>());
	
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
	
	private String hospital;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB
	
	private int docId;
	private List docTypes;
	
	private int documentValidity;
	
	private String barcode;
	private String orNumber;
	private String cedulaNumber;
	private Date cedulaIssued;
	private String cedulaIssuedAddress;
	private int cedulaTypeId;
	private List cedulaTypes;
	private String cedulaDetails;
	private String notes;
	private double amountPaid;
	private boolean payable = true;
	
	private Map<Integer, Purpose> purposesData = new HashMap<Integer, Purpose>();//Collections.synchronizedMap(new HashMap<Integer, Purpose>());
	
	private List status;
	private int statusId;
	
	private List clearanceTypes;
	private int clearanceTypeId;
	
	private Employee employee;
	private List<Employee> employees = new ArrayList<Employee>();//Collections.synchronizedList(new ArrayList<Employee>());
	
	private Customer taxPayer;
	private String customerName;
	private String searchTaxpayer;
	private List<Customer> taxpayers = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
	
	private String motherName;
	private String fatherName;
	private String childName;
	private String bornAddress;
	private Date bordDate;
	private String bornDetails;
	
	private Customer beneficiary;
	private String beneficiaryName;
	private String seachBeneficiary;
	private List<Customer> beneficiaries = new ArrayList<Customer>();//Collections.synchronizedList(new ArrayList<Customer>());
	
	private List<Clearance> clearances = new ArrayList<Clearance>();//Collections.synchronizedList(new ArrayList<Clearance>());
	private Clearance selectedData;
	private String searchClearance;
	
	private String searchBusinessName;
	private List<Livelihood> business = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
	private List<Livelihood> selectedBusiness = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
	private List<Livelihood> ownerBusiness = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
	
	private List<Purpose> purposes = new ArrayList<Purpose>();//Collections.synchronizedList(new ArrayList<Purpose>());
	private int relationshipId;
	private List relationships;
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private StreamedContent tempPdfFile; 
	
	private boolean readySave;
	
	public void clearNew(){
		clearFields();
	}
	
	public void saveCedula(){
		
		if(getCedulaNumber()!=null && !getCedulaNumber().isEmpty()) {
			int len = getCedulaNumber().length();
			if(len>8) {
				Application.addMessage(3, "Error", "Please provide 8 digit numbers");
			}else if(len<8) {
				Application.addMessage(3, "Error", "Please provide 8 digit numbers");
			}else {
				setCedulaDetails((getCedulaTypeId()==1? "Individual" : "Corporation") + " : " +getCedulaNumber()+", Issued: "+DateUtils.convertDate(getCedulaIssued(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD()) + " at " + getCedulaIssuedAddress());
			}
		}else {
			Application.addMessage(3, "Error", "Please provide cedula number");
		}
	}
	
	public void saveLand(){
		StringBuilder landDtls = new StringBuilder();
		landDtls.append("Type:"+LandTypes.typeName(getLandTypeId()));
		landDtls.append(" ,Area:" + getAreaQrt());
		landDtls.append(" , Lot No:" + getLotNo());
		landDtls.append(" , North:" + getNorthBound());
		landDtls.append(" , East:" + getEastBound());
		landDtls.append(" , South:" + getSouthBound());
		landDtls.append(" , West:" + getWestBound());
		setSupportingDetails(landDtls.toString());
	}
	
	public void saveTree(){
		StringBuilder treeDtls =  new StringBuilder();
		       treeDtls.append("Name:" + getTreeName());
		       treeDtls.append("No of Hills:" + getTreeHills());
		       treeDtls.append("Loc.:" + getTreeLocation());
		       treeDtls.append("Purpose:" + getTreePurpose());
		       setSupportingDetails(treeDtls.toString());
	}
	
	public void saveBirthDtls(){
		StringBuilder bornDtls = new StringBuilder();
		bornDtls.append("Child name " + getChildName().toUpperCase());
		bornDtls.append(", Born on "+ DateUtils.convertDateToMonthDayYear(DateUtils.convertDate(getBordDate(),com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD())));
		bornDtls.append(", at " + getBornAddress().toUpperCase());
		bornDtls.append(" to Mr. " + getFatherName().toUpperCase() + " and Mrs. " + getMotherName().toUpperCase());
		setBornDetails(bornDtls.toString());
	}
	
	public void saveMotor(){
		StringBuilder motor = new StringBuilder();
		motor.append("Plate No: " + getMotorPlateNo() + ", Model: " + getMotorModel() + ", Color: " + getMotorColor());
		setSupportingDetails(motor.toString().toUpperCase());
	}
	
	public List<Employee> loadEmployeeSigner(String value){
		
		String sql = "";
		String[] params = new String[0];
		
		if(value!=null && !value.isEmpty()){
			sql = " AND emp.isresigned=0 AND emp.isactiveemp=1 AND (emp.firstname like '%" + value.replace("--", "") +"%' OR emp.middlename like '%" + value.replace("--", "") + "%' OR emp.lastname like '%" + value.replace("--", "") +"%')";
		}else{
			sql = " AND emp.isresigned=0 AND emp.isactiveemp=1 limit 10";
		}
		
		return Employee.retrieve(sql, params);
	}
	
	public void saveCattle(){
		String supDtls = CattleKind.typeName(getCattleKindId()) + 
				", Color: " + CattleColor.typeName(getCattleColorId()) + 
				", Gender: " + (getCattleGenderId()==1? "Male" : (getCattleGenderId()==2? "FEMALE" : "N/A")) + 
				", Age: " + (getCattleAgeDescription()==null? "N/A" : getCattleAgeDescription().isEmpty()? "N/A" : getCattleAgeDescription()) + 
				", COLC No.: " + (getCattleCOLCNo()==null? "N/A" : getCattleCOLCNo().isEmpty()? "N/A" : getCattleCOLCNo()) + ", Date Issue: " + (getCattleCOLCNoDateIssue()==null? "N/A" : getCattleCOLCNoDateIssue()) + 
				", CTLC No.: " + (getCattleCTLCNo()==null? "N/A" : getCattleCTLCNo().isEmpty()? "N/A" : getCattleCTLCNo() ) + " Date Issue: " + (getCattleCTLCNoDateIssue()==null? "N/A" : getCattleCTLCNoDateIssue()) + 
				", Other Info: " + (getCattleOtherInfo()==null? "N/A" : getCattleOtherInfo().isEmpty()? "N/A" : getCattleOtherInfo());
		setCattleInformation(supDtls);
	}
	
	public List<String> autoORNumber(String query){
		int size = query.length();
		if(size>=3){
			return Clearance.retrieve(query, "ornumber"," ORDER BY ornumber DESC limit 10");
		}else{
			return new ArrayList<String>();
		}
	}
	
	public void clearUnPaid(){
		if(isPayable()){
			setOrNumber(null);
			setAmountPaid(0.00);
			ownerBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
			selectedBusiness = Collections.synchronizedList(new ArrayList<Livelihood>());
		}
	}
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
	
	public void oncapture(CaptureEvent captureEvent) {
        photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
        shots.add(photoId);
        
        System.out.println("Set picture name " + photoId);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
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
	
	public void loadCedula() {
		Cedula cedula = Cedula.loadCedulaIfExist(getTaxPayer());
		if(cedula!=null){
			setCedulaFld(false);
			setCedulaDetails((cedula.getCedulaType()==1? "Individual" : "Corporation") + ": " + cedula.getCedulaNo()+", Issued: "+cedula.getDateIssued() + " at " + cedula.getIssuedAddress());
			setCedulaIssued(DateUtils.convertDateString(cedula.getDateIssued(), DateFormat.YYYY_MM_DD()));
			setCedulaNumber(cedula.getCedulaNo());
		}
	}
	
	public void clickItemOwner(){
		//try {
		Customer cuz = getTaxPayer();
		try{setPhotoId(cuz.getPhotoid());}catch(NullPointerException e) {}
		shots = new ArrayList<>();
		try{shots.add(cuz.getPhotoid());}catch(NullPointerException e) {}	
			
		loadCedula();
		ORTransaction ort = ORTransaction.loadORIfExist(cuz);
		if(ort!=null){
			setPayableFld(true);
			setPayable(false);
			setOrNumber(ort.getOrNumber());
			setAmountPaid(ort.getAmount());
		}
		//}catch(Exception e) {e.printStackTrace();}
	}
	
	public void clickItemBen() {
		
	}
	
	public void selectedPhoto(String photoId){
		setPhotoId(photoId);
	}
	
	public void clearFields(){
		setReadySave(false);
		setSelectedData(null);
		setIssuedDate(null);
		setBarcode(null);
		setOrNumber(null);
		setCedulaNumber(null);
		setCedulaIssuedAddress(null);
		setPhotoId("camera");
		setAmountPaid(0);
		setPayable(true);
		setPurposeTypeId(0);
		setStatusId(1);
		setClearanceTypeId(1);
		setEmployee(null);
		setTaxPayer(null);
		setCustomerName(null);
		business = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
		selectedBusiness = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
		ownerBusiness = new ArrayList<Livelihood>();//Collections.synchronizedList(new ArrayList<Livelihood>());
		purposes = new ArrayList<Purpose>();//Collections.synchronizedList(new ArrayList<Purpose>());
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
	
	public List<Customer> loadRequestor(String value){
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(value!=null && !value.isEmpty()){
			sql += " AND cus.fullname like '%" + value +"%'";
		}else{
			sql += " order by cus.customerid DESC limit 100;";
		}
		
		return Customer.retrieve(sql, new String[0]);
	}
	
	public List<Employee> loadEmployee(String value){
		String sql = "";
		String[] params = new String[0];
		
		if(value!=null && !value.isEmpty()){
			sql = " AND emp.isactiveemp=1 AND (emp.firstname like '%" + value.replace("--", "") +"%' OR emp.middlename like '%" + value.replace("--", "") + "%' OR emp.lastname like '%" + value.replace("--", "") +"%')";
		}else{
			sql = " AND emp.isactiveemp=1 limit 10";
		}
		
		return Employee.retrieve(sql, params);
	}
	
	
	public List<Customer> loadBeneficiary(String value){
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(value!=null && !value.isEmpty()){
			sql += " AND (cus.fullname like '%" + value.replace("--", "") +"%' OR cus.cuscardno like '%"+ value.replace("--", "") +"%')";
		}else{
			sql += " order by cus.customerid DESC limit 50;";
		}
		
		return Customer.retrieve(sql, new String[0]);
	}
	
	public void reloadPurpose(){
		setSupportingDetails(null);
		purposeSelected();
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
				com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId() == getPurposeTypeId() ||
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
			setBeneciaryLabel("Benificiary Name:");
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
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()== getPurposeTypeId() 
				|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId()
				|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()== getPurposeTypeId()){	
			
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
	
	public void loadMultipurpose(){
		
		
		String[] params = new String[0];
		String sql = "SELECT * FROM purpose WHERE isactivepurpose=1 ";
		if(getClearSearch()!=null && !getClearSearch().isEmpty()){
			sql += " AND purname like '%"+ getClearSearch().replace("--", "") +"%'";
		}
		multiSelections = Collections.synchronizedList(new ArrayList<>());
		for(Purpose p : Purpose.retrieve(sql, params)){
			multiSelections.add(new SelectItem(p.getId(), p.getName()));
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
	
	public String getIssuedDate() {
		if(issuedDate==null) {
			issuedDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return issuedDate;
	}

	public String getPhotoId() {
		return photoId;
	}

	public int getPurposeTypeId() {
		return purposeTypeId;
	}

	public List getPurposeTypes() {
		purposesData = Collections.synchronizedMap(new HashMap<Integer, Purpose>());
		purposeTypes = new ArrayList<>();
		
		String sql = "SELECT * FROM purpose WHERE isactivepurpose=1 ORDER BY purname";
		String[] params = new String[0];
		purposeTypes.add(new SelectItem(0, "Select Purpose"));
		for(Purpose pur : Purpose.retrieve(sql, params)){
			purposeTypes.add(new SelectItem(pur.getId(), pur.getName()));
			purposesData.put(pur.getId(), pur);
		}
		return purposeTypes;
	}
	
	
	
	
	public List<String> getShots() {
		return shots;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public void setPurposeTypeId(int purposeTypeId) {
		this.purposeTypeId = purposeTypeId;
	}

	public void setPurposeTypes(List purposeTypes) {
		this.purposeTypes = purposeTypes;
	}

	public void setShots(List<String> shots) {
		this.shots = shots;
	}

	public String getCapturedImagePathName() {
		return capturedImagePathName;
	}

	public void setCapturedImagePathName(String capturedImagePathName) {
		this.capturedImagePathName = capturedImagePathName;
	}

	public String getSupportingDetails() {
		return supportingDetails;
	}

	public void setSupportingDetails(String supportingDetails) {
		this.supportingDetails = supportingDetails;
	}

	public boolean isRelationshipFld() {
		return relationshipFld;
	}

	public boolean isBeneFld() {
		return beneFld;
	}

	public boolean isPayableFld() {
		return payableFld;
	}

	public boolean isCedulaFld() {
		return cedulaFld;
	}

	public boolean isDocumentValidFld() {
		return documentValidFld;
	}

	public boolean isSupportDtlsFld() {
		return supportDtlsFld;
	}

	public boolean isBorndDtlsFld() {
		return borndDtlsFld;
	}

	public boolean isLargeCattleFld() {
		return largeCattleFld;
	}

	public boolean isEmployeeAsBeneFld() {
		return employeeAsBeneFld;
	}

	public boolean isBussinesFld() {
		return bussinesFld;
	}

	public boolean isMotorcycleFld() {
		return motorcycleFld;
	}

	public boolean isLandFld() {
		return landFld;
	}

	public boolean isTreeFld() {
		return treeFld;
	}

	public String getSupportingDtlsLabel() {
		return supportingDtlsLabel;
	}

	public String getSupportingDtlsPlaceHolder() {
		return supportingDtlsPlaceHolder;
	}

	public int getCattleKindId() {
		return cattleKindId;
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

	public Map<Integer, CattleKind> getCattleKindMap() {
		return cattleKindMap;
	}

	public int getCattleColorId() {
		return cattleColorId;
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

	public Map<Integer, CattleColor> getCattleColorMap() {
		return cattleColorMap;
	}

	public int getCattleGenderId() {
		return cattleGenderId;
	}

	public List getCattleGenders() {
		cattleGenders = new ArrayList<>();
		cattleGenders.add(new SelectItem(0, "N/A"));
		cattleGenders.add(new SelectItem(1, "MALE"));
		cattleGenders.add(new SelectItem(2, "FEMALE"));
		return cattleGenders;
	}

	public String getCattleAgeDescription() {
		return cattleAgeDescription;
	}

	public String getCattleCOLCNo() {
		return cattleCOLCNo;
	}

	public Date getCattleCOLCNoDateIssue() {
		return cattleCOLCNoDateIssue;
	}

	public String getCattleCTLCNo() {
		return cattleCTLCNo;
	}

	public Date getCattleCTLCNoDateIssue() {
		return cattleCTLCNoDateIssue;
	}

	public String getCattleOtherInfo() {
		return cattleOtherInfo;
	}

	public int getCattlePurposeId() {
		return cattlePurposeId;
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

	public Map<Integer, CattlePurpose> getCattlePurposeMap() {
		return cattlePurposeMap;
	}

	public String getCattleInformation() {
		return cattleInformation;
	}

	public String getBeneciaryLabel() {
		return beneciaryLabel;
	}

	public Employee getEmployeeCoeSelected() {
		return employeeCoeSelected;
	}

	public Date getCalendarFrom() {
		if(calendarFrom==null){
			calendarFrom = DateUtils.getDateToday();
		}
		return calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			calendarTo = DateUtils.getDateToday();
		}
		return calendarTo;
	}

	public List getPurposeList() {
		
		purposeList = Collections.synchronizedList(new ArrayList<>());
		purposeList.add(new SelectItem(0, "All Purpose"));
		for(Purpose p : Purpose.retrieve(" SELECT * FROM purpose WHERE isactivepurpose=1 ORDER BY purname", new String[0])){
			purposeList.add(new SelectItem(p.getId(), p.getName()));
		}
		
		return purposeList;
	}
	
	public int getPurposeId() {
		return purposeId;
	}

	
	public boolean isActiveMultipurpose() {
		return activeMultipurpose;
	}

	public String getClearSearch() {
		return clearSearch;
	}

	public List getMultiSelections() {
		return multiSelections;
	}

	public List<String> getMultipurposeSelected() {
		return multipurposeSelected;
	}

	public String getMotorPlateNo() {
		return motorPlateNo;
	}

	public String getMotorModel() {
		return motorModel;
	}

	public String getMotorColor() {
		return motorColor;
	}

	public int getLandTypeId() {
		return landTypeId;
	}

	public List getLandTypes() {
		landTypes = new ArrayList<>();
		
		for(LandTypes type : LandTypes.values()){
			landTypes.add(new SelectItem(type.getId(), type.getName()));
		}
		return landTypes;
	}

	public String getLotNo() {
		return lotNo;
	}

	public String getAreaQrt() {
		return areaQrt;
	}

	public String getNorthBound() {
		return northBound;
	}

	public String getEastBound() {
		return eastBound;
	}

	public String getSouthBound() {
		return southBound;
	}

	public String getWestBound() {
		return westBound;
	}

	public String getTreeName() {
		return treeName;
	}

	public String getTreeHills() {
		return treeHills;
	}

	public String getTreeLocation() {
		return treeLocation;
	}

	public String getTreePurpose() {
		return treePurpose;
	}

	public String getOpenTitle() {
		return openTitle;
	}

	public boolean isDocTitle() {
		return docTitle;
	}

	public String getHospital() {
		return hospital;
	}

	public void setRelationshipFld(boolean relationshipFld) {
		this.relationshipFld = relationshipFld;
	}

	public void setBeneFld(boolean beneFld) {
		this.beneFld = beneFld;
	}

	public void setPayableFld(boolean payableFld) {
		this.payableFld = payableFld;
	}

	public void setCedulaFld(boolean cedulaFld) {
		this.cedulaFld = cedulaFld;
	}

	public void setDocumentValidFld(boolean documentValidFld) {
		this.documentValidFld = documentValidFld;
	}

	public void setSupportDtlsFld(boolean supportDtlsFld) {
		this.supportDtlsFld = supportDtlsFld;
	}

	public void setBorndDtlsFld(boolean borndDtlsFld) {
		this.borndDtlsFld = borndDtlsFld;
	}

	public void setLargeCattleFld(boolean largeCattleFld) {
		this.largeCattleFld = largeCattleFld;
	}

	public void setEmployeeAsBeneFld(boolean employeeAsBeneFld) {
		this.employeeAsBeneFld = employeeAsBeneFld;
	}

	public void setBussinesFld(boolean bussinesFld) {
		this.bussinesFld = bussinesFld;
	}

	public void setMotorcycleFld(boolean motorcycleFld) {
		this.motorcycleFld = motorcycleFld;
	}

	public void setLandFld(boolean landFld) {
		this.landFld = landFld;
	}

	public void setTreeFld(boolean treeFld) {
		this.treeFld = treeFld;
	}

	public void setSupportingDtlsLabel(String supportingDtlsLabel) {
		this.supportingDtlsLabel = supportingDtlsLabel;
	}

	public void setSupportingDtlsPlaceHolder(String supportingDtlsPlaceHolder) {
		this.supportingDtlsPlaceHolder = supportingDtlsPlaceHolder;
	}

	public void setCattleKindId(int cattleKindId) {
		this.cattleKindId = cattleKindId;
	}

	public void setCattleKinds(List cattleKinds) {
		this.cattleKinds = cattleKinds;
	}

	public void setCattleKindMap(Map<Integer, CattleKind> cattleKindMap) {
		this.cattleKindMap = cattleKindMap;
	}

	public void setCattleColorId(int cattleColorId) {
		this.cattleColorId = cattleColorId;
	}

	public void setCattleColors(List cattleColors) {
		this.cattleColors = cattleColors;
	}

	public void setCattleColorMap(Map<Integer, CattleColor> cattleColorMap) {
		this.cattleColorMap = cattleColorMap;
	}

	public void setCattleGenderId(int cattleGenderId) {
		this.cattleGenderId = cattleGenderId;
	}

	public void setCattleGenders(List cattleGenders) {
		this.cattleGenders = cattleGenders;
	}

	public void setCattleAgeDescription(String cattleAgeDescription) {
		this.cattleAgeDescription = cattleAgeDescription;
	}

	public void setCattleCOLCNo(String cattleCOLCNo) {
		this.cattleCOLCNo = cattleCOLCNo;
	}

	public void setCattleCOLCNoDateIssue(Date cattleCOLCNoDateIssue) {
		this.cattleCOLCNoDateIssue = cattleCOLCNoDateIssue;
	}

	public void setCattleCTLCNo(String cattleCTLCNo) {
		this.cattleCTLCNo = cattleCTLCNo;
	}

	public void setCattleCTLCNoDateIssue(Date cattleCTLCNoDateIssue) {
		this.cattleCTLCNoDateIssue = cattleCTLCNoDateIssue;
	}

	public void setCattleOtherInfo(String cattleOtherInfo) {
		this.cattleOtherInfo = cattleOtherInfo;
	}

	public void setCattlePurposeId(int cattlePurposeId) {
		this.cattlePurposeId = cattlePurposeId;
	}

	public void setCattlePurpose(List cattlePurpose) {
		this.cattlePurpose = cattlePurpose;
	}

	public void setCattlePurposeMap(Map<Integer, CattlePurpose> cattlePurposeMap) {
		this.cattlePurposeMap = cattlePurposeMap;
	}

	public void setCattleInformation(String cattleInformation) {
		this.cattleInformation = cattleInformation;
	}

	public void setBeneciaryLabel(String beneciaryLabel) {
		this.beneciaryLabel = beneciaryLabel;
	}

	public void setEmployeeCoeSelected(Employee employeeCoeSelected) {
		this.employeeCoeSelected = employeeCoeSelected;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}

	public void setPurposeList(List purposeList) {
		this.purposeList = purposeList;
	}

	public void setPurposeId(int purposeId) {
		this.purposeId = purposeId;
	}

	public void setActiveMultipurpose(boolean activeMultipurpose) {
		this.activeMultipurpose = activeMultipurpose;
	}

	public void setClearSearch(String clearSearch) {
		this.clearSearch = clearSearch;
	}

	public void setMultiSelections(List multiSelections) {
		this.multiSelections = multiSelections;
	}

	public void setMultipurposeSelected(List<String> multipurposeSelected) {
		this.multipurposeSelected = multipurposeSelected;
	}

	public void setMotorPlateNo(String motorPlateNo) {
		this.motorPlateNo = motorPlateNo;
	}

	public void setMotorModel(String motorModel) {
		this.motorModel = motorModel;
	}

	public void setMotorColor(String motorColor) {
		this.motorColor = motorColor;
	}

	public void setLandTypeId(int landTypeId) {
		this.landTypeId = landTypeId;
	}

	public void setLandTypes(List landTypes) {
		this.landTypes = landTypes;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	public void setAreaQrt(String areaQrt) {
		this.areaQrt = areaQrt;
	}

	public void setNorthBound(String northBound) {
		this.northBound = northBound;
	}

	public void setEastBound(String eastBound) {
		this.eastBound = eastBound;
	}

	public void setSouthBound(String southBound) {
		this.southBound = southBound;
	}

	public void setWestBound(String westBound) {
		this.westBound = westBound;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public void setTreeHills(String treeHills) {
		this.treeHills = treeHills;
	}

	public void setTreeLocation(String treeLocation) {
		this.treeLocation = treeLocation;
	}

	public void setTreePurpose(String treePurpose) {
		this.treePurpose = treePurpose;
	}

	public void setOpenTitle(String openTitle) {
		this.openTitle = openTitle;
	}

	public void setDocTitle(boolean docTitle) {
		this.docTitle = docTitle;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
	
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
	
	public int getDocId() {
		if(docId==0){
			docId = 1;
		}
		return docId;
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
											com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN.getId()==getPurposeTypeId() ||
										com.italia.marxmind.bris.enm.Purpose.CAR_LOAN.getId()==getPurposeTypeId() ||
											com.italia.marxmind.bris.enm.Purpose.TO_TAKE_BOARD_LICENSE.getId()==getPurposeTypeId() ||
													com.italia.marxmind.bris.enm.Purpose.AFP_TRAINING.getId()==getPurposeTypeId() ||
														com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN.getId()==getPurposeTypeId() ||
															com.italia.marxmind.bris.enm.Purpose.SSS_LOAN.getId()==getPurposeTypeId() ||
																	com.italia.marxmind.bris.enm.Purpose.BANK_REQUIREMENT.getId()==getPurposeTypeId() || 
																			com.italia.marxmind.bris.enm.Purpose.PNP_APPLICATION.getId()==getPurposeTypeId() || 
																					com.italia.marxmind.bris.enm.Purpose.SOCOTECO_APPLICATION.getId()==getPurposeTypeId() || 
																							com.italia.marxmind.bris.enm.Purpose.SECURING_PERMIT_FIRECRACKERS.getId()==getPurposeTypeId()
																			
															){		
			
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
				com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
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
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() 
				|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId() 
				|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()== getPurposeTypeId()
				|| com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==getPurposeTypeId()){
			
			docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==getPurposeTypeId()){	
				
				docTypes.add(new SelectItem(DocTypes.CERTIFICATE.getId(), DocTypes.CERTIFICATE.getName()));
				docTypes.add(new SelectItem(DocTypes.CLEARANCE.getId(), DocTypes.CLEARANCE.getName()));
				docTypes.add(new SelectItem(DocTypes.INDIGENT.getId(), DocTypes.INDIGENT.getName()));	
				docTypes.add(new SelectItem(DocTypes.RESIDENCY.getId(), DocTypes.RESIDENCY.getName()));
				docTypes.add(new SelectItem(DocTypes.INCOME.getId(), DocTypes.INCOME.getName()));
				
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_RESIDENCY.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.DIRECT_SELLER_AGENT_APPLICATION.getId()==getPurposeTypeId() ||
				com.italia.marxmind.bris.enm.Purpose.TES_APPLICATION.getId()==getPurposeTypeId()){
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

	public int getDocumentValidity() {
		return documentValidity;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public void setDocTypes(List docTypes) {
		this.docTypes = docTypes;
	}

	public void setDocumentValidity(int documentValidity) {
		this.documentValidity = documentValidity;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getOrNumber() {
		return orNumber;
	}

	public String getCedulaNumber() {
		if(cedulaNumber==null) {
			cedulaNumber = Cedula.getNewCedulaNumber();
		}
		return cedulaNumber;
	}

	public Date getCedulaIssued() {
		if(cedulaIssued==null){
			cedulaIssued = DateUtils.getDateToday();
		}
		return cedulaIssued;
	}

	public String getCedulaIssuedAddress() {
		if(cedulaIssuedAddress==null){
			cedulaIssuedAddress = MUNICIPALITY + ", " + PROVINCE;
		}
		return cedulaIssuedAddress;
	}

	public int getCedulaTypeId() {
		if(cedulaTypeId==0){
			cedulaTypeId = 1;
		}
		return cedulaTypeId;
	}

	public List getCedulaTypes() {
		cedulaTypes = new ArrayList<>();
		
		cedulaTypes.add(new SelectItem(1, "Individual"));
		cedulaTypes.add(new SelectItem(2, "Corporation"));
		
		return cedulaTypes;
	}

	public String getCedulaDetails() {
		return cedulaDetails;
	}

	public String getNotes() {
		return notes;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public boolean isPayable() {
		return payable;
	}

	public Map<Integer, Purpose> getPurposesData() {
		return purposesData;
	}

	public List getStatus() {
		return status;
	}

	public int getStatusId() {
		return statusId;
	}

	public List getClearanceTypes() {
		return clearanceTypes;
	}

	public int getClearanceTypeId() {
		return clearanceTypeId;
	}

	public Employee getEmployee() {
		if(employee==null) {
			try{
				OD od = OD.retrieveAssignedOfficer(DateUtils.getCurrentMonth(), DateUtils.getCurrentDay(), DateUtils.getCurrentYear());
				employee = od.getOfficer();
			}catch(Exception e) {}
		}
		return employee;
	}

	
	public List<Employee> getEmployees() {
		return employees;
	}

	public Customer getTaxPayer() {
		return taxPayer;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getSearchTaxpayer() {
		return searchTaxpayer;
	}

	public List<Customer> getTaxpayers() {
		return taxpayers;
	}

	public String getMotherName() {
		return motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public String getChildName() {
		return childName;
	}

	public String getBornAddress() {
		return bornAddress;
	}

	public Date getBordDate() {
		return bordDate;
	}

	public String getBornDetails() {
		return bornDetails;
	}

	public Customer getBeneficiary() {
		return beneficiary;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public String getSeachBeneficiary() {
		return seachBeneficiary;
	}

	public List<Customer> getBeneficiaries() {
		return beneficiaries;
	}

	public List<Clearance> getClearances() {
		return clearances;
	}

	public Clearance getSelectedData() {
		return selectedData;
	}

	public String getSearchClearance() {
		return searchClearance;
	}

	public String getSearchBusinessName() {
		return searchBusinessName;
	}

	public List<Livelihood> getBusiness() {
		return business;
	}

	public List<Livelihood> getSelectedBusiness() {
		return selectedBusiness;
	}

	public List<Livelihood> getOwnerBusiness() {
		return ownerBusiness;
	}

	public List<Purpose> getPurposes() {
		return purposes;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	public void setCedulaNumber(String cedulaNumber) {
		this.cedulaNumber = cedulaNumber;
	}

	public void setCedulaIssued(Date cedulaIssued) {
		this.cedulaIssued = cedulaIssued;
	}

	public void setCedulaIssuedAddress(String cedulaIssuedAddress) {
		this.cedulaIssuedAddress = cedulaIssuedAddress;
	}

	public void setCedulaTypeId(int cedulaTypeId) {
		this.cedulaTypeId = cedulaTypeId;
	}

	public void setCedulaTypes(List cedulaTypes) {
		this.cedulaTypes = cedulaTypes;
	}

	public void setCedulaDetails(String cedulaDetails) {
		this.cedulaDetails = cedulaDetails;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public void setPayable(boolean payable) {
		this.payable = payable;
	}

	public void setPurposesData(Map<Integer, Purpose> purposesData) {
		this.purposesData = purposesData;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setClearanceTypes(List clearanceTypes) {
		this.clearanceTypes = clearanceTypes;
	}

	public void setClearanceTypeId(int clearanceTypeId) {
		this.clearanceTypeId = clearanceTypeId;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public void setTaxPayer(Customer taxPayer) {
		this.taxPayer = taxPayer;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setSearchTaxpayer(String searchTaxpayer) {
		this.searchTaxpayer = searchTaxpayer;
	}

	public void setTaxpayers(List<Customer> taxpayers) {
		this.taxpayers = taxpayers;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public void setBornAddress(String bornAddress) {
		this.bornAddress = bornAddress;
	}

	public void setBordDate(Date bordDate) {
		this.bordDate = bordDate;
	}

	public void setBornDetails(String bornDetails) {
		this.bornDetails = bornDetails;
	}

	public void setBeneficiary(Customer beneficiary) {
		this.beneficiary = beneficiary;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public void setSeachBeneficiary(String seachBeneficiary) {
		this.seachBeneficiary = seachBeneficiary;
	}

	public void setBeneficiaries(List<Customer> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	public void setClearances(List<Clearance> clearances) {
		this.clearances = clearances;
	}

	public void setSelectedData(Clearance selectedData) {
		this.selectedData = selectedData;
	}

	public void setSearchClearance(String searchClearance) {
		this.searchClearance = searchClearance;
	}

	public void setSearchBusinessName(String searchBusinessName) {
		this.searchBusinessName = searchBusinessName;
	}

	public void setBusiness(List<Livelihood> business) {
		this.business = business;
	}

	public void setSelectedBusiness(List<Livelihood> selectedBusiness) {
		this.selectedBusiness = selectedBusiness;
	}

	public void setOwnerBusiness(List<Livelihood> ownerBusiness) {
		this.ownerBusiness = ownerBusiness;
	}

	public void setPurposes(List<Purpose> purposes) {
		this.purposes = purposes;
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
	
	public void clickItemOfficer(){
		System.out.println("BEN : "+getEmployee().getFullName());
		setEmployee(getEmployee());
	}
	public void clickBeneficiary() {
		System.out.println("BEN : "+getBeneficiary().getFullname());
		setBeneficiary(getBeneficiary());
	}
	
	public void checkValidInput() {
		Login in = Login.getUserLogin();
		boolean isEnable = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isEnable = true;
		}else{
			if(Features.isEnabled(Feature.CLEARANCE)){
				isEnable = true;
			}
		}
		
		if(isEnable) {
		
		
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
					com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
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
				
			}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() 
					|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId() 
					|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()== getPurposeTypeId()){	
				
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
			
			if(isOk) {
				setReadySave(true);//assigned if ready to save
				PrimeFaces pm = PrimeFaces.current();
				pm.executeScript("PF('cfNote').show();");
			}else {
				setReadySave(false);//assigned if ready to save
			}
		
		}
		
		}else {
			Application.addMessage(3, "Error", "You are not authorized to save this information.");
		}
	}
	
	public void saveData(String selected){
		
		if(isReadySave()){
			
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
					
					}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==getPurposeTypeId() 
							|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId() 
							|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()== getPurposeTypeId()){	
						
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
							com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==getPurposeTypeId() ||
							com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==getPurposeTypeId()){
						
						notes = getSupportingDetails();
						cl.setNotes(notes);
						
					}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==getPurposeTypeId()){
						
						int cnt =1;
						for(Object o : getMultipurposeSelected()) {
							int p = (Integer)o;
							if(cnt>1){
								notes += "<:>"+ p;
							}else{
								notes = p+"";
							}
							cnt++;
						}
						cl.setNotes(notes);
						
						/*change by above codes i dont know why this code wont work - java 8 works perfectly not on java 11
						for(String p : getMultipurposeSelected()) {
							if(cnt>1){
								notes += "<:>"+ p;
							}else{
								notes = p;
							}
							cnt++;
						}
						cl.setNotes(notes);
						*/
						
						
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
				List<MultiLivelihood> mulLive = new ArrayList<MultiLivelihood>();
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
						System.out.println("not equal to null getTaxPayer().getPhotoid()");
						if(!getPhotoId().equalsIgnoreCase(getTaxPayer().getPhotoid())){
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
				
				if("print".equalsIgnoreCase(selected)) {
					printDocument(cl);
				}
			
		}else {
			Application.addMessage(3, "Error", "Error saving. Please check your data.");
		}
	}
	
	/**
	 * adding this code to forcefully reloading the init method @see at sidemenu.xhtml actionListener="#{mainBean.reloadinit}"
	 * this problem exist because of changing the scope from @org.omnifaces.cdi.ViewScoped to  javax.enterprise.context.SessionScoped;
	 * PostConstruct in enterprise.sessionScope call init method once only
	 */
	public void reloadinit() {
		System.out.println("Reloading init");
		clearFields();
		init();
		System.out.println("Reloading init end here");
	}
	
	@PostConstruct
	public void init() {
		
		Login in = Login.getUserLogin();
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
					clearances = new ArrayList<Clearance>();// Collections.synchronizedList(new ArrayList<Clearance>());
					clearances = Clearance.retrieve(sql, params);
				}
			}else{
				clearances = new ArrayList<Clearance>();//Collections.synchronizedList(new ArrayList<Clearance>());
				clearances = Clearance.retrieve(sql, params);
			}
			
			PrimeFaces ins = PrimeFaces.current();
			if(clearances!=null && clearances.size()==1){
				clickItem(clearances.get(0));
				ins.executeScript("$('#panelHide').show(1000);showButton();");
			}else{	
				
				if(clearances!=null && clearances.size()==0) {
					
					clearFields();
					Customer cus = Customer.getLatestCitizen();
					if(cus!=null) {
						setTaxPayer(cus);
						clickItemOwner();
					}else {
						System.out.println("Empty records....");
					}
					ins.executeScript("$('#panelHide').show(1000);showButton();");
				}else {
					clearFields();
					ins.executeScript("$('#panelHide').hide(1000);hideButton();");
					Collections.reverse(clearances);
				}
				
			}
			
			//do not move this code this is for create doc functionality
			try {
				String addDoc = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("addDoc");
				System.out.println("AddDoc : " + addDoc);
				if(addDoc!=null && !addDoc.isEmpty() && !"null".equalsIgnoreCase(addDoc)){
					setTaxPayer(Customer.retrieve(Long.valueOf(addDoc)));
					clickItemOwner();
					PrimeFaces pm = PrimeFaces.current();
					pm.executeScript("addNew();");
				}
			}catch(Exception e) {
				
			}
			
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
	public void clickItem(Clearance cl){
		
		clearFields();
		
		setSelectedData(cl);
		setIssuedDate(cl.getIssuedDate());
		setBarcode(cl.getBarcode());
		setOrNumber(cl.getOrNumber());
		setPhotoId(cl.getPhotoId());
		setAmountPaid(cl.getAmountPaid());
		setPayable(cl.getIsPayable()==1? false : true);
		setPurposeTypeId(cl.getPurposeType());
		setStatusId(cl.getStatus());
		setClearanceTypeId(cl.getClearanceType());
		setEmployee(cl.getEmployee());
		setTaxPayer(cl.getTaxPayer());
		//setCustomerName(cl.getTaxPayer().getFirstname() + " " + cl.getTaxPayer().getMiddlename().substring(0, 1) + ". " + cl.getTaxPayer().getLastname());
		
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
				}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==cl.getPurposeType() 
						|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()== getPurposeTypeId() 
						|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()== getPurposeTypeId()){
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
						com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==cl.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==cl.getPurposeType()){
					
					setSupportingDetails(cl.getNotes());
					System.out.println("Motor details: " + getSupportingDetails());
					
				}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==cl.getPurposeType()){
					try{
						setMultipurposeSelected(Collections.synchronizedList(new ArrayList<>()));
						loadMultipurpose();
						for(String p : cl.getNotes().split("<:>")){
							getMultipurposeSelected().add(p);
						}
					}catch(Exception e){
						getMultipurposeSelected().add(cl.getNotes());
					}
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
				
				setCattleInformation(supDtls);
				
			}catch(Exception e){
				
			}
		}	
		
		public boolean birthCondition(boolean isOk){
			
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
		/**
		 * 
		 * For deletion
		 */
		private String copyPhoto(String photoId){
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	        String driveImage =  IMAGE_PATH + ReadConfig.value(Bris.BARANGAY_NAME) + Bris.SEPERATOR.getName() + photoId + ".jpg";
	        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
	            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
	            File file = new File(driveImage);
	            try{
	    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
	    			        StandardCopyOption.REPLACE_EXISTING);
	    			}catch(IOException e){}
	            return driveImage;
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
			
				if(getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId() ||
							getPurposeTypeId()==com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()
								){
					
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
		
		@Deprecated
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
			
		
		public void printDocument(Clearance clr) {
			Map<Integer, Object> obj = DocumentPrinting.printRequestedDocument(clr);
			String path = (String)obj.get(1);
			String REPORT_NAME = (String)obj.get(2);
			String jrxmlFile = (String)obj.get(3);
			HashMap param = (HashMap)obj.get(4);
			JRBeanCollectionDataSource beanColl = (JRBeanCollectionDataSource)obj.get(5); 
			try{
		  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
		  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
		  			
		  	    String pdfName = REPORT_NAME +".pdf";
			    File pdfFile = new File(path+ REPORT_NAME +".pdf");
			    
			    tempPdfFile =  DefaultStreamedContent.builder()
			    	    .contentType("application/pdf")
			    	    .name(REPORT_NAME+".pdf")
			    	    .stream(()-> {
			    			try {
			    				return new FileInputStream(pdfFile);
			    			} catch (FileNotFoundException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			}
			    			return null;
			    		})
			    	    .build();
			    
			    		//new DefaultStreamedContent(new FileInputStream(pdfFile), "application/pdf", pdfName);
			    System.out.println("path>> " + path  +" REPORT_NAME " + REPORT_NAME);
		  	    System.out.println("Printing certification " + pdfName);
		  	    PrimeFaces pm = PrimeFaces.current();
		  	    pm.executeScript("showPdf();hideButton();");
		  	    
			}catch(Exception e){e.printStackTrace();}
			
					
		}
		
	/*
	 * public void printDocument(Clearance clr) { try { Map<Integer, Object> obj =
	 * DocumentPrinting.printRequestedDocument(clr); String path =
	 * (String)obj.get(1); String REPORT_NAME = (String)obj.get(2); String jrxmlFile
	 * = (String)obj.get(3); HashMap param = (HashMap)obj.get(4);
	 * JRBeanCollectionDataSource beanColl = (JRBeanCollectionDataSource)obj.get(5);
	 * 
	 * HttpServletResponse
	 * httpServletResponse=(HttpServletResponse)FacesContext.getCurrentInstance().
	 * getExternalContext().getResponse();
	 * httpServletResponse.addHeader("Content-disposition",
	 * "attachment; filename="+REPORT_NAME+".pdf"); ServletOutputStream
	 * servletOutputStream=httpServletResponse.getOutputStream(); JasperPrint
	 * jasperPrint = JasperFillManager.fillReport(jrxmlFile, param, beanColl);
	 * JasperExportManager.exportReportToPdfStream(jasperPrint,
	 * servletOutputStream); System.out.println("All done the report is done");
	 * servletOutputStream.flush(); servletOutputStream.close();
	 * FacesContext.getCurrentInstance().responseComplete();
	 * 
	 * String pdfName = REPORT_NAME +".pdf"; File pdfFile = new File(path+
	 * REPORT_NAME +".pdf"); tempPdfFile = new DefaultStreamedContent(new
	 * FileInputStream(pdfFile), "application/pdf", pdfName);
	 * 
	 * PrimeFaces pm = PrimeFaces.current();
	 * pm.executeScript("showPdf();hideButton();");
	 * 
	 * }catch(Exception e){e.printStackTrace();} }
	 */
		
		public String generateRandomIdForNotCaching() {
			return java.util.UUID.randomUUID().toString();
		}
		
		public StreamedContent getTempPdfFile() throws IOException {
			
			if(tempPdfFile==null) {
				String pdfName = "assclearanceV6.pdf";
				String pdf = ReadConfig.value(Bris.REPORT) + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
				//pdf += pdfName;
				System.out.println("pdf file >>>> " + pdf);
				
			    File pdfFile = new File(pdf);
	  	    
			    //return new DefaultStreamedContent(new FileInputStream(pdfFile), "application/pdf", pdfName);
			    return DefaultStreamedContent.builder()
			    		.contentType("application/pdf")
			    		.name(pdfName)
			    		.stream(()-> this.getClass().getResourceAsStream(pdf + pdfName))
			    		.build();
			}else {
				return tempPdfFile;
			}
		  }
		public void setTempPdfFile(StreamedContent tempPdfFile) {
			this.tempPdfFile = tempPdfFile;
		}

		public boolean isReadySave() {
			return readySave;
		}

		public void setReadySave(boolean readySave) {
			this.readySave = readySave;
		}
}
