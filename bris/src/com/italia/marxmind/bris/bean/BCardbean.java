package com.italia.marxmind.bris.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.BCard;
import com.italia.marxmind.bris.controller.CardRpt;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
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
 * @since 07/12/2017
 * @version 1.0
 *
 */
@ManagedBean(name="cardBean", eager=true)
@ViewScoped
public class BCardbean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6556768798761L;
	private String dateTrans;
	private List<BCard> cardTrans = Collections.synchronizedList(new ArrayList<BCard>());
	private List<BCard> cardSelected = Collections.synchronizedList(new ArrayList<BCard>());
	private BCard cardData;
	
	private Customer taxpayerData;
	private List<Customer> taxpayers = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchName;
	/*private String dateFrom;
	private String dateTo;*/
	
	private Date validFrom;
	private Date validTo;
	
	private String taxpayerName;
	private String searchTaxpayerName;
	
	
	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String REPORT_NAME = ReadXML.value(ReportTag.BID);
	
	/*private String searchDateFrom;
	private String searchDateTo;*/
	
	private Date calendarFrom;
	private Date calendarTo;
	
	@PostConstruct
	public void init(){
		
		cardTrans = Collections.synchronizedList(new ArrayList<BCard>());
		
		Login in = Login.getUserLogin();
		boolean isEnable = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isEnable = true;
		}else{
			if(Features.isEnabled(Feature.ID_GENERATION)){
				isEnable = true;
			}
		}
		
		if(isEnable){
		
			String[] params = new String[2];
			String sql = " AND crd.isactivebid=1 AND (crd.datetrans>=? AND crd.datetrans<=?)  ";
			
			
			try{
				String editCard = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editCard");
				System.out.println("Check pass name: " + editCard);
				if(editCard!=null && !editCard.isEmpty() && !"null".equalsIgnoreCase(editCard)){
					setSearchName(editCard.split(":")[0]);
					params[0] = editCard.split(":")[1];
					params[1] = editCard.split(":")[1];
				}else{
					params[0] = DateUtils.convertDate(getCalendarFrom(), DateFormat.YYYY_MM_DD());
					params[1] = DateUtils.convertDate(getCalendarTo(), DateFormat.YYYY_MM_DD());
				}
				}catch(Exception e){}
			
			
			
			if(getSearchName()!=null && !getSearchName().isEmpty()){
				sql += " AND cuz.fullname like '%"+ getSearchName().replace("--", "") +"%'";
			}else{
				
				String dateToday = DateUtils.getCurrentDateYYYYMMDD();
				String fromDate = DateUtils.convertDate(getCalendarFrom(), DateFormat.YYYY_MM_DD());
				String fromTo = DateUtils.convertDate(getCalendarTo(), DateFormat.YYYY_MM_DD());
				
				if(dateToday.equalsIgnoreCase(fromDate) && dateToday.equalsIgnoreCase(fromTo)){
					sql += "ORDER BY crd.tranid DESC limit 100";
				}else{
					sql += "ORDER BY crd.tranid";
				}
			}
			
			cardTrans = BCard.retrieve(sql, params);
			
			if(cardTrans!=null && cardTrans.size()==1){
				clickItem(cardTrans.get(0));
			}else{
				clearFlds();
			}
			
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
	
	public void saveData(){
		
		Login in = Login.getUserLogin();
		boolean isEnable = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isEnable = true;
		}else{
			if(Features.isEnabled(Feature.ID_GENERATION)){
				isEnable = true;
			}
		}
		
		if(isEnable){
		
		BCard card = new BCard();
		
		if(getCardData()!=null){
			card = getCardData();
		}else{
			card.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			card.setIsActive(1);
		}
		
		boolean isOk = true;
		
		if(getTaxpayerData()==null){
			isOk = false;
			Application.addMessage(3, "Please provide taxpayer name", "");
		}
		
		if(isOk){
			card.setValidDateFrom(DateUtils.convertDate(getValidFrom(), DateFormat.YYYY_MM_DD()));
			card.setValidDateTo(DateUtils.convertDate(getValidTo(), DateFormat.YYYY_MM_DD()));
			card.setTaxpayer(getTaxpayerData());
			card.setUserDtls(Login.getUserLogin().getUserDtls());
			BCard.save(card);
			
			clearFlds();
			setCalendarFrom(DateUtils.convertDateString(card.getDateTrans(), DateFormat.YYYY_MM_DD()));
			setCalendarTo(DateUtils.convertDateString(card.getDateTrans(), DateFormat.YYYY_MM_DD()));
			init();
			Application.addMessage(1, "Successfully saved", "");
		}
		
		}else{
			Application.addMessage(3, "This feature is not available. Please contact developer to enable this feature.", "");
		}
		
	}
	
	public void deleteRow(BCard card){
		if(Login.checkUserStatus()){
			card.delete();
			clearFlds();
			init();
		}
	}
	
	public void clearFlds(){
		
		setCardData(null);
		
		setDateTrans(null);
		
		setValidFrom(null);
		setValidTo(null);
		
		setTaxpayerData(null);
		setTaxpayerName(null);
	}
	
	public void clickItem(BCard card){
		setCardData(card);
		Customer cus = card.getTaxpayer();
		
		setDateTrans(card.getDateTrans());
		
		setValidFrom(DateUtils.convertDateString(card.getValidDateFrom(), DateFormat.YYYY_MM_DD()));
		setValidTo(DateUtils.convertDateString(card.getValidDateTo(), DateFormat.YYYY_MM_DD()));
		
		setTaxpayerData(cus);
		setTaxpayerName(cus.getFullname());
		
	}
	
	public void clickItemOwner(Customer cus){
		setTaxpayerData(cus);
		setTaxpayerName(cus.getFullname());
	}
	
	public void printCard(){
		
		if(getCardSelected()!=null && getCardSelected().size()>0){
		List<CardRpt> rpts = Collections.synchronizedList(new ArrayList<CardRpt>());
		
		String sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 AND pos.posid=?";
		String[] params = new String[1];
		params[0] = Positions.CAPTAIN.getId()+"";
		
		Employee emp = Employee.retrieve(sql, params).get(0);
		String captain =  emp.getFirstName().toUpperCase() + " " + emp.getMiddleName().substring(0,1).toUpperCase() + ". " + emp.getLastName().toUpperCase();
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		for(BCard card : getCardSelected()){
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
			//address2 = emerPerson.getPurok().getPurokName() + ", " + emerPerson.getBarangay().getName() + ", " + emerPerson.getMunicipality().getName() + ", " + emerPerson.getProvince().getName();
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
				
				//barcode = BarcodeFactory.createPDF417(tax.getFullname());
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
		}
			
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
		
		HashMap param = new HashMap();
		
		
		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    /*PrimeFaces pm = PrimeFaces.current();
	  	    pm.executeScript("PF('multiDialogPdf').show();");*/
	  	    
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
		
		
		}else{
			Application.addMessage(3, "Please select Person details in order to print Barangay ID", "");
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
	
	public List<BCard> getCardTrans() {
		return cardTrans;
	}
	public void setCardTrans(List<BCard> cardTrans) {
		this.cardTrans = cardTrans;
	}
	public List<BCard> getCardSelected() {
		return cardSelected;
	}
	public void setCardSelected(List<BCard> cardSelected) {
		this.cardSelected = cardSelected;
	}
	public Customer getTaxpayerData() {
		return taxpayerData;
	}
	public void setTaxpayerData(Customer taxpayerData) {
		this.taxpayerData = taxpayerData;
	}
	public List<Customer> getTaxpayers() {
		return taxpayers;
	}
	public void setTaxpayers(List<Customer> taxpayers) {
		this.taxpayers = taxpayers;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
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
			String mm="",dd="",yy="";
			yy = DateUtils.getCurrentDateYYYYMMDD().split("-")[0];
			mm = DateUtils.getCurrentDateYYYYMMDD().split("-")[1];
			dd = DateUtils.getCurrentDateYYYYMMDD().split("-")[2];
			
			int year = DateUtils.getCurrentYear() + 3;
			
			dateTo = year + "-"+ mm + "-" + dd;
			
		}
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}*/


	public String getTaxpayerName() {
		return taxpayerName;
	}


	public void setTaxpayerName(String taxpayerName) {
		this.taxpayerName = taxpayerName;
	}

	public String getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateTrans;
	}

	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getSearchTaxpayerName() {
		return searchTaxpayerName;
	}

	public void setSearchTaxpayerName(String searchTaxpayerName) {
		this.searchTaxpayerName = searchTaxpayerName;
	}

	public BCard getCardData() {
		return cardData;
	}

	public void setCardData(BCard cardData) {
		this.cardData = cardData;
	}
	
	
	/*public String getSearchDateFrom() {
		if(searchDateFrom==null){
			searchDateFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return searchDateFrom;
	}

	public void setSearchDateFrom(String searchDateFrom) {
		this.searchDateFrom = searchDateFrom;
	}

	public String getSearchDateTo() {
		if(searchDateTo==null){
			searchDateTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return searchDateTo;
	}

	public void setSearchDateTo(String searchDateTo) {
		this.searchDateTo = searchDateTo;
	}*/

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
	
	public Date getValidFrom() {
		if(validFrom==null){
			validFrom = DateUtils.getDateToday();
		}
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		
		if(validTo==null){
			String mm="",dd="",yy="";
			String dateTo="";
			yy = DateUtils.getCurrentDateYYYYMMDD().split("-")[0];
			mm = DateUtils.getCurrentDateYYYYMMDD().split("-")[1];
			dd = DateUtils.getCurrentDateYYYYMMDD().split("-")[2];
			
			int year = DateUtils.getCurrentYear() + 3;
			
			dateTo = year + "-"+ mm + "-" + dd;
			validTo = DateUtils.convertDateString(dateTo, DateFormat.YYYY_MM_DD());
		}
		
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public static void main(String[] args) {
		try{
		Barcode barcode = BarcodeFactory.createPDF417("MARK ITALIA");
		barcode.setDrawingText(true);
		File pdf = new File("Hello.png");
		
		BarcodeImageHandler.savePNG(barcode, pdf);
		
		System.out.println("Path: "+pdf.getAbsolutePath());
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
}












