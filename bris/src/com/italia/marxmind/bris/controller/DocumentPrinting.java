package com.italia.marxmind.bris.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CattleColor;
import com.italia.marxmind.bris.enm.CattleKind;
import com.italia.marxmind.bris.enm.CattlePurpose;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.DocStyle;
import com.italia.marxmind.bris.enm.DocTypes;
import com.italia.marxmind.bris.enm.LandTypes;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ClearanceRpt;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
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
 * @since 07/13/2018
 * @version 1.0
 */
public class DocumentPrinting {
	
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
	
	private final static String IMAGE_PATH = ReadConfig.value(Bris.APP_IMG_FILE) ;
	
	public static String copyPhoto(String photoId){
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
	
	public static String getAppellation(Customer customer){
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
	
	public static String documentNote(Clearance clr) {
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
			note += "Certification";
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){
			String valid = clr.getDocumentValidity()==0? "6 Months" : (clr.getDocumentValidity()==1? " 1 Month" : clr.getDocumentValidity() + " Months");
			note = "Valid Only for " + valid +"\n";
			note += "Brgy. Document No " + clr.getId() +"\n";
			note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
			note += "Business Loan";
		}else if(com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType()){
			note = "Brgy. Document No " + clr.getId() +"\n";
			note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()== clr.getPurposeType()){
			note = "Valid Until December 31, "+clr.getIssuedDate().split("-")[0]+"\n";
			note += "Brgy. Document No " + clr.getId() +"\n";
			note += "Series of " + clr.getIssuedDate().split("-")[0]+"\n";
			note += "Business Permit";
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
	}
	
	public static Map<Integer, Object> printRequestedDocument(Clearance clr){
		String docStyle = DocumentFormatter.getTagName("documentLayout");
		if(DocStyle.V5.getName().equalsIgnoreCase(docStyle)) {
			return printDocument(clr);
		}else if(DocStyle.V6.getName().equalsIgnoreCase(docStyle)) {
			return DocumentPrintingV6.printDocumentV6(clr);
		}else if(DocStyle.V7.getName().equalsIgnoreCase(docStyle)) {
			return DocumentPrintingV7.printDocumentV7(clr);		
		}else {
			return new HashMap<Integer, Object>();
		}
	}
	
	private static Map<Integer, Object> printDocument(Clearance clr) {
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		HashMap param = new HashMap();
		Customer taxpayer =Customer.retrieve(clr.getTaxPayer().getCustomerid());
		clr.setTaxPayer(taxpayer);
		
		Employee employee = Employee.retrieve(clr.getEmployee().getId());
		clr.setEmployee(employee);
		
		
		String REPORT_NAME = "";
		StringBuilder str = new StringBuilder();
		String detail_1 = "";
		String detail_2 = "";
		String purpose = "";
		String civilStatus = CivilStatus.typeName(clr.getTaxPayer().getCivilStatus());
		civilStatus = civilStatus.toLowerCase();
		
		String requestor = getAppellation(clr.getTaxPayer()) + " " + clr.getTaxPayer().getFirstname().toUpperCase() + " " + clr.getTaxPayer().getMiddlename().substring(0,1).toUpperCase() + ". " + clr.getTaxPayer().getLastname().toUpperCase();
		//String address = clr.getTaxPayer().getPurok().getPurokName() + ", " + clr.getTaxPayer().getBarangay().getName() + ", " + clr.getTaxPayer().getMunicipality().getName() + ", " + clr.getTaxPayer().getProvince().getName();
		String address = clr.getTaxPayer().getCompleteAddress();
		String municipal = clr.getEmployee().getMunicipality().getName() + ", " + clr.getEmployee().getProvince().getName();
		try{municipal = clr.getCedulaNumber().split("<:>")[2];}catch(Exception e){}
		String barangay = clr.getEmployee().getBarangay().getName() + ", " + clr.getEmployee().getMunicipality().getName() + ", " + clr.getEmployee().getProvince().getName();
		List<ClearanceRpt> reports = Collections.synchronizedList(new ArrayList<ClearanceRpt>());
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== clr.getPurposeType() ||
					com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType() ||
							com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){ //business
			REPORT_NAME = BUSINESS_REPORT_NAME;
			
			if(clr.getMultilivelihood()!=null && clr.getMultilivelihood().size()>0){
				for(MultiLivelihood mu : clr.getMultilivelihood()){
					ClearanceRpt rpt = new ClearanceRpt();
					Livelihood liv = mu.getLivelihood();
					try{
						liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + liv.getId(), new String[0]).get(0);
					}catch(Exception e){}
					
					rpt.setF1(liv.getBusinessName());
					rpt.setF2(liv.getPurokName() + ", " + liv.getBarangay().getName() + ", " + liv.getMunicipality().getName()+ ", " + liv.getProvince().getName());
					//rpt.setF3(com.italia.marxmind.bris.enm.Purpose.BUSINESS.getName());
					reports.add(rpt);
					
				}
			}
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			
			String word = "";
			word = Words.getTagName("business-permit-municipal-string-1");
			word = word.replace("<requestor>", requestor);
			word = word.replace("<civilstatus>", civilStatus);
			word = word.replace("<requestoraddress>", address);
			str.append(word);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				word = Words.getTagName("business-permit-municipal-string-2");
				word = word.replace("<ctcno>", ced[0]);
				word = word.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				word = word.replace("<ctcissuedaddress>", municipal);
				str.append(word);
			}
			
			String businessIdentity=Words.getTagName("business-permit-municipal-string-3");
			
			if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType() ||
					com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){
				businessIdentity = Words.getTagName("business-permit-municipal-string-4");
			}
			
			word = Words.getTagName("business-permit-municipal-string-5");
			word = word.replace("<ownerrep>", businessIdentity);
			str.append(word);
			
			
			
			detail_1 = str.toString();
			str = new StringBuilder();
			if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("business-permit-municipal-string-7");
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("business-permit-municipal-string-8");
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("business-permit-municipal-string-9");
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("business-permit-municipal-string-10");
			}else if(com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("business-permit-municipal-string-11");
			}
			
			word = Words.getTagName("business-permit-municipal-string-12");
			str.append(word);
			
			word = Words.getTagName("business-permit-municipal-string-6");
			word = word.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word = word.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word = word.replace("<year>", clr.getIssuedDate().split("-")[0]);
			word = word.replace("<barangayaddress>", barangay);
			str.append(word);
			
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()== clr.getPurposeType()){ 	
			
			REPORT_NAME = BARANGAY_BUSINESS_PERMIT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] notes = clr.getNotes().split("<:>");
			/**
			 * [0] - Control No
			 * [1] - NEW/RENEWAL
			 * [2] - MEMO
			 * [3] - Business Engaged
			 */
			
			if(clr.getMultilivelihood()!=null && clr.getMultilivelihood().size()>0){
				for(MultiLivelihood mu : clr.getMultilivelihood()){
					
					Livelihood liv = mu.getLivelihood();
					try{
						liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + liv.getId(), new String[0]).get(0);
					}catch(Exception e){}
					
					param.put("PARAM_BUSINESS_NAME", liv.getBusinessName().toUpperCase());
					String businessaddress = liv.getPurokName() + ", " + liv.getBarangay().getName() + ", " + liv.getMunicipality().getName() + ", " + liv.getProvince().getName();
					param.put("PARAM_BUSINESS_ADDRESS", businessaddress.toUpperCase());
					
					String location = liv.getMunicipality().getName() + ", " + liv.getProvince().getName();
					param.put("PARAM_BUSINESS_MUNICIPALITY_ADDRESS", location + " "+Words.getTagName("business-permit-barangay-string-1"));
					
				}
			}
			
			param.put("PARAM_BUSINESS_TYPE", notes[3]);
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				param.put("PARAM_CTAX", Words.getTagName("business-permit-barangay-string-2") + ced[0] +"/" + DateUtils.convertDateToMonthDayYear(ced[1]));
			}else{
				param.put("PARAM_CTAX","N/A");
			}
			
			param.put("PARAM_DAY", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			param.put("PARAM_MONTH", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + DateUtils.getCurrentYear());
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== clr.getPurposeType() || com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== clr.getPurposeType()){ //Fish cage
			REPORT_NAME = FISHCAGE_REPORT_NAME;
			if(clr.getMultilivelihood()!=null && clr.getMultilivelihood().size()>0){
				for(MultiLivelihood mu : clr.getMultilivelihood()){
					ClearanceRpt rpt = new ClearanceRpt();
					Livelihood liv = mu.getLivelihood();
					try{
						liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + liv.getId(), new String[0]).get(0);
					}catch(Exception e){}
					
					rpt.setF1(liv.getPurokName() + ", " + liv.getBarangay().getName() + ", " + liv.getMunicipality().getName()+ ", " + liv.getProvince().getName());
					rpt.setF2(liv.getAreaMeter());
					rpt.setF3(liv.getSupportingDetails());
					reports.add(rpt);
				}
			}
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			String word = "";
			word = Words.getTagName("fish-cage-string-1");
			word = word.replace("<requestor>", requestor);
			word = word.replace("<civilstatus>", civilStatus);
			word = word.replace("<requestoraddress>", address);
			str.append(word);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				word = Words.getTagName("fish-cage-string-2");
				word = word.replace("<ctcno>", ced[0]);
				word = word.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				word = word.replace("<ctcissuedaddress>", municipal);
				str.append(word);
			}
			
			
			str.append(Words.getTagName("fish-cage-string-3"));
			
			detail_1 = str.toString();
			
			str = new StringBuilder();
			if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== clr.getPurposeType()){
				purpose =  Words.getTagName("fish-cage-string-5");
			}else if(com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== clr.getPurposeType()){
				purpose =  Words.getTagName("fish-cage-string-6");
			}
			
			word = Words.getTagName("fish-cage-string-7");
			str.append(word);
			
			word = Words.getTagName("fish-cage-string-4");
			word = word.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word = word.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word = word.replace("<year>", clr.getIssuedDate().split("-")[0]);
			word = word.replace("<barangayaddress>", barangay);
			str.append(word);
			
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()== clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String heshe = "";
			String benCivilStatus = "";
			String benAddress="";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName = getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			benCivilStatus = CivilStatus.typeName(ben.getCivilStatus());
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			benAddress = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			
			
			//note instead of died person to certify it should be the requestor
			String words = "";
			if(address.equalsIgnoreCase(benAddress)){
				words = Words.getTagName("asstance-death-string-1");
				words = words.replace("<requestor>", requestor);
				words = words.replace("<civilstatus>", civilStatus);
				words = words.replace("<relationship>", relationship);
				words = words.replace("<diedperson>", beneciaryName);
			}else{
				words = Words.getTagName("asstance-death-string-2");
				words = words.replace("<requestor>", requestor);
				words = words.replace("<civilstatus>", civilStatus);
				words = words.replace("<requestoraddress>", address);
				words = words.replace("<relationship>", relationship);
				words = words.replace("<diedperson>", beneciaryName);
				words = words.replace("<diedpersonaddress>", benAddress);
			}
			
			str.append(words);
			 
			
			str.append(Words.getTagName("asstance-death-string-3")+ supportdtls);
			
			words = Words.getTagName("asstance-death-string-4");
			words = words.replace("<requestor>", requestor);
			str.append(words);
			
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()== clr.getPurposeType() 
					){
				purpose = Words.getTagName("asstance-death-string-5");
			}else if(com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-death-string-6");
			}
			
			words = Words.getTagName("asstance-death-string-7");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();	
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String heshe = "";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName = getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			str.append(Words.getTagName("asstance-burial-string-1") + beneciaryName +Words.getTagName("asstance-burial-string-2")+ civilStatus + Words.getTagName("asstance-burial-string-3") + address +Words.getTagName("asstance-burial-string-4")); 
			
			str.append(Words.getTagName("asstance-burial-string-5")+ supportdtls);
			
			str.append(Words.getTagName("asstance-burial-string-6") + heshe + " " + Words.getTagName("asstance-burial-string-7") + heshe + " "+Words.getTagName("asstance-burial-string-8"));
			
			str.append(Words.getTagName("asstance-burial-string-9") + requestor + ", "+ relationship +" "+ Words.getTagName("asstance-burial-string-10") + beneciaryName + Words.getTagName("asstance-burial-string-11"));
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-burial-string-12");
				
			str.append(Words.getTagName("asstance-burial-string-13") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-burial-string-14") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("asstance-burial-string-15"));
			str.append(" " + Words.getTagName("asstance-burial-string-16") + barangay +".");
			detail_2 = str.toString();	
			
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FINANCIAL.getId() == clr.getPurposeType()){	
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String heshe = "";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName = getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			//supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			str.append(Words.getTagName("asstance-financial-string-1") + beneciaryName +Words.getTagName("asstance-financial-string-2")+ civilStatus + Words.getTagName("asstance-financial-string-3") + address +".");
			
			str.append(Words.getTagName("asstance-financial-string-4") + heshe + " "+ Words.getTagName("asstance-financial-string-5") + heshe + " " + Words.getTagName("asstance-financial-string-6"));
			
			str.append(Words.getTagName("asstance-financial-string-7") + requestor + ", "+ relationship +" "+ Words.getTagName("asstance-financial-string-8"));
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-financial-string-9");
				
			str.append(Words.getTagName("asstance-financial-string-10") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-financial-string-11") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+Words.getTagName("asstance-financial-string-12"));
			str.append(Words.getTagName("asstance-financial-string-13") + barangay +".");
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId()== clr.getPurposeType() ||
				com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()== clr.getPurposeType() 
				){ //financial medical
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String gender = "";
			int age = clr.getTaxPayer().getAge();
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			age = ben.getAge();
			beneciaryName = getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			gender = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			 
			str.append(Words.getTagName("asstance-medical-string-1"));
			str.append(beneciaryName);
			String ageLabel = Words.getTagName("asstance-medical-string-2");
			
					if(age<=17){
						ageLabel = Words.getTagName("asstance-medical-string-14").replace("<age>", age+"");
						str.append(ageLabel);
						if(age<=5){
							civilStatus = "Baby";
						}else if(age<=12){
							civilStatus = "Kid";
						}else{
							civilStatus = "Teenager";
						}
							
						str.append(civilStatus);
					}else{
						str.append(ageLabel);
						str.append(civilStatus);
					}		
					
			str.append(Words.getTagName("asstance-medical-string-3") + address);
			
			str.append(Words.getTagName("asstance-medical-string-4")+ supportdtls.toUpperCase() +Words.getTagName("asstance-medical-string-5"));
			
			String words = "";
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				str.append(words);
				str.append(Words.getTagName("asstance-medical-string-16"));
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				str.append(words);	
				str.append(Words.getTagName("asstance-medical-string-6"));
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-medical-string-15");
				words = words.replace("he/she", gender);
				str.append(words);
				str.append(Words.getTagName("asstance-medical-string-17"));
			}
			
			str.append(requestor + ", "+ relationship +" "+Words.getTagName("asstance-medical-string-7"));
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-medical-string-8");
			}else if(com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()== clr.getPurposeType()){ 
				purpose = Words.getTagName("asstance-medical-string-9");
			}	
			str.append(Words.getTagName("asstance-medical-string-10") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-medical-string-11") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+Words.getTagName("asstance-medical-string-12"));
			str.append(" " + Words.getTagName("asstance-medical-string-13") + barangay +".");
			detail_2 = str.toString();
		}else if(com.italia.marxmind.bris.enm.Purpose.CALAMITY.getId()== clr.getPurposeType()){
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str.append(Words.getTagName("asstance-calamity-string-1") + requestor +Words.getTagName("asstance-calamity-string-2")+ civilStatus + Words.getTagName("asstance-calamity-string-3") + address +"."); 
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			String words = "";
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-calamity-string-9");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-calamity-string-10");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-calamity-string-11");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}
			
			detail_1 = str.toString();
			str = new StringBuilder();
			purpose = Words.getTagName("asstance-calamity-string-4");
			str.append(Words.getTagName("asstance-calamity-string-5") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " " + Words.getTagName("asstance-calamity-string-6") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+Words.getTagName("asstance-calamity-string-7"));
			str.append(" " + Words.getTagName("asstance-calamity-string-8") + barangay +".");
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ASSISTANCE.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String gender = "";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName =getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			gender = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			str.append(Words.getTagName("asstance-indigent-string-1") + beneciaryName +Words.getTagName("asstance-indigent-string-2")+ civilStatus + Words.getTagName("asstance-indigent-string-3") + address);
			
			str.append(" "+Words.getTagName("asstance-indigent-string-4")+gender+" "+ Words.getTagName("asstance-indigent-string-5")+ gender +" "+Words.getTagName("asstance-indigent-string-6")+ gender +" "+Words.getTagName("asstance-indigent-string-7"));
			
			str.append(Words.getTagName("asstance-indigent-string-8") + requestor + ", "+ relationship +" "+Words.getTagName("asstance-indigent-string-9"));
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-indigent-string-10");
				
			str.append(Words.getTagName("asstance-indigent-string-11") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+Words.getTagName("asstance-indigent-string-12") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+Words.getTagName("asstance-indigent-string-13"));
			str.append(" "+Words.getTagName("asstance-indigent-string-14") + barangay +".");
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ATTORNEYS_ASSISTANCE.getId()== clr.getPurposeType()){
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("asstance-attorney-string-1") + requestor +Words.getTagName("asstance-attorney-string-2")+ civilStatus +Words.getTagName("asstance-attorney-string-3") + address);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			str.append(" "+ Words.getTagName("asstance-attorney-string-4") + ced[0] + " "+ Words.getTagName("asstance-attorney-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("asstance-attorney-string-6") + municipal +".");
			}else{
				str.append(".");
			}
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			String words = "";
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-attorney-string-15");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-attorney-string-16");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				str.append(Words.getTagName("asstance-attorney-string-7") + heshe + " "+ Words.getTagName("asstance-attorney-string-8") + heshe + " "+ Words.getTagName("asstance-attorney-string-9"));
			}
			
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("asstance-attorney-string-10");
			
			str.append(Words.getTagName("asstance-attorney-string-11") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-attorney-string-12") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("asstance-attorney-string-13"));
			str.append(" " +Words.getTagName("asstance-attorney-string-14")+ barangay +".");
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.OTHER_LEGAL_MATTERS.getId()== clr.getPurposeType()){
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("legal-string-1") + requestor +Words.getTagName("legal-string-2")+ civilStatus +Words.getTagName("legal-string-3") + address);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			str.append(" "+ Words.getTagName("legal-string-4") + ced[0] + " "+ Words.getTagName("legal-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("legal-string-6") + municipal +".");
			}else{
				str.append(".");
			}
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			String words = "";
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
				words = Words.getTagName("legal-string-15");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType()){
				words = Words.getTagName("legal-string-16");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				str.append(Words.getTagName("legal-string-7") + heshe + " "+ Words.getTagName("legal-string-8") + heshe + " "+ Words.getTagName("legal-string-9"));
			}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("legal-string-17");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("legal-string-18");
				words = words.replace("he/she", heshe);
				words = words.replace("his/her", hisher);
				str.append(words);
			}
			
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("legal-string-10");
			
			str.append(Words.getTagName("legal-string-11") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("legal-string-12") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("legal-string-13"));
			str.append(" " +Words.getTagName("legal-string-14")+ barangay +".");
			detail_2 = str.toString();	
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_HOSPL_ASS.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			int age=clr.getTaxPayer().getAge();
			String gender = "";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName = getAppellation(ben) +" "+ ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			gender = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			//address = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			age = ben.getAge();
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			str.append(Words.getTagName("asstance-hospital-string-1")); 
			str.append(beneciaryName);
			String ageLabel = Words.getTagName("asstance-hospital-string-2");
			if(age<=17){
				ageLabel = Words.getTagName("asstance-hospital-string-16").replace("<age>", age+"");
				str.append(ageLabel);
				if(age==0){
					civilStatus = "infant";
				}else if(age>=1 && age<=5){
					civilStatus = "baby";
				}else if(age>=6 && age<=9){
					civilStatus = "kid";
				}else if(age>=10 && age<=17){
					civilStatus = "teenager";
				}
					
				str.append(civilStatus);
			}else{
				str.append(ageLabel);
				str.append(civilStatus);
			}
					
		    str.append(Words.getTagName("asstance-hospital-string-3") + address);
			
			str.append(Words.getTagName("asstance-hospital-string-4")+ supportdtls.toUpperCase() +Words.getTagName("asstance-hospital-string-5"));
			
			str.append(Words.getTagName("asstance-hospital-string-6")+ gender +" "+Words.getTagName("asstance-hospital-string-7")+ gender +" "+Words.getTagName("asstance-hospital-string-8"));
			
			str.append(Words.getTagName("asstance-hospital-string-9") + requestor + ", "+ relationship +" "+Words.getTagName("asstance-hospital-string-10"));
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-hospital-string-11");
				
			str.append(Words.getTagName("asstance-hospital-string-12") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-hospital-string-13") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("asstance-hospital-string-14"));
			str.append(" " + Words.getTagName("asstance-hospital-string-15") + barangay +".");
			detail_2 = str.toString();		
		
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_CERT.getId()== clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EDUCATION_SCHOLARSHIP_ASSISTANCE.getId()== clr.getPurposeType() ||
				com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EMPLOYMENT.getId()== clr.getPurposeType() ||
				com.italia.marxmind.bris.enm.Purpose.SPES_REQUIREMENTS.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.CHED_SCHOLARSHIP.getId()== clr.getPurposeType() ||
								com.italia.marxmind.bris.enm.Purpose.SCHOOL_REG.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.TESDA_REQUIREMENT.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.KABUGWASON_REQUIREMENT.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_APPLICATION.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.CONFIRMATION_APPLICATION.getId()== clr.getPurposeType() ||
								com.italia.marxmind.bris.enm.Purpose.ESC_SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType() ||
										com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType() || 
										com.italia.marxmind.bris.enm.Purpose.SCHOOL_REQUIREMENTS.getId()== clr.getPurposeType()
				
				){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()){
				str.append(Words.getTagName("asstance-cert-string-1") + requestor +Words.getTagName("asstance-cert-string-2")+ civilStatus +Words.getTagName("asstance-cert-string-3") + address);
				
				String rel = taxpayer.getGender().equalsIgnoreCase("1")? "son" : "daughter";
				String words = "";
				if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				str.append(" "+ Words.getTagName("asstance-cert-string-4") + ced[0] + " "+ Words.getTagName("asstance-cert-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("asstance-cert-string-6") + municipal);
				words = Words.getTagName("asstance-cert-string-30").replace("<relationship>", rel);
				words = words.replace("<parents>", clr.getNotes());
				str.append(", "+words);
				
				}else{
					words = Words.getTagName("asstance-cert-string-30").replace("<relationship>", rel);
					words = words.replace("<parents>", clr.getNotes());
					str.append(", "+words);
				}
			}else {
			
				str.append(Words.getTagName("asstance-cert-string-1") + requestor +Words.getTagName("asstance-cert-string-2")+ civilStatus +Words.getTagName("asstance-cert-string-3") + address);
				if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				str.append(" "+ Words.getTagName("asstance-cert-string-4") + ced[0] + " "+ Words.getTagName("asstance-cert-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("asstance-cert-string-6") + municipal +".");
				}else{
					str.append(".");
				}
			
			}
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			String words = "";
			if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-cert-string-32");
			    words = words.replace("<he/she>", heshe);
			    str.append(words);
			}else {	
				str.append(Words.getTagName("asstance-cert-string-7") + heshe + " "+ Words.getTagName("asstance-cert-string-8") + heshe + " "+ Words.getTagName("asstance-cert-string-9"));
				words = Words.getTagName("asstance-cert-string-18");
			    words = words.replace("his/her", hisher);
			}
			
			   
		       
			if(DocTypes.INCOME.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-cert-string-26");
			    words = words.replace("his/her", hisher);
				str.append(words);
			}else if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()){
					words = Words.getTagName("asstance-cert-string-29");
				    words = words.replace("his/her", hisher);
					str.append(words);
			}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("asstance-cert-string-31");
				words = words.replace("<opentitle>", clr.getCustomTitle());
			    words = words.replace("his/her", hisher);
				str.append(words);		
			}else{
				str.append(words);
			}
			
			       
			detail_1 = str.toString();
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_CERT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-10");
			}else if(com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EDUCATION_SCHOLARSHIP_ASSISTANCE.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-15");
			}else if(com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EMPLOYMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-16");
			}else if(com.italia.marxmind.bris.enm.Purpose.SPES_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-17");
			}else if(com.italia.marxmind.bris.enm.Purpose.CHED_SCHOLARSHIP.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-19");
			}else if(com.italia.marxmind.bris.enm.Purpose.TESDA_REQUIREMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-20");
			}else if(com.italia.marxmind.bris.enm.Purpose.KABUGWASON_REQUIREMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-21");
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_APPLICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-22");
			}else if(com.italia.marxmind.bris.enm.Purpose.CONFIRMATION_APPLICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-23");
			}else if(com.italia.marxmind.bris.enm.Purpose.ESC_SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-24");
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-25");
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOOL_REG.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-27");
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOOL_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-28");
			}
			
			str.append(Words.getTagName("asstance-cert-string-11") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("asstance-cert-string-12") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("asstance-cert-string-13"));
			str.append(" " +Words.getTagName("asstance-cert-string-14")+ barangay +".");
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()== clr.getPurposeType()){
			
			
			String cattlepurpose = "";
			String kind = "";
			String genderreq = "he";
			//REPORT_NAME = ASSISTANCE_REPORT_NAME;
			REPORT_NAME = LARGE_CATTLE_REPORT_NAME;
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				
				try{
				
				String[] labels = {"Purpose:","Kind:","Color:","Gender:","Age:","COLC No.:","Date of Issue:","CTLC No.:","Date of Issue:","Remarks:"};	
				String[] notes = clr.getNotes().split("<:>"); 
				String[] data = {
								CattlePurpose.typeName(Integer.valueOf(notes[8])),
								CattleKind.typeName(Integer.valueOf(notes[0])),
								CattleColor.typeName(Integer.valueOf(notes[1])),
								"1".equalsIgnoreCase(notes[2])? "MALE" : ("2".equalsIgnoreCase(notes[2])? "FEMALE" : "N/A"),
								"0".equalsIgnoreCase(notes[3])? "N/A" : notes[3],
										"0".equalsIgnoreCase(notes[4])? "N/A" : notes[4],
												"0".equalsIgnoreCase(notes[5])? "N/A" : notes[5],
														"0".equalsIgnoreCase(notes[6])? "N/A" : notes[6],
																"0".equalsIgnoreCase(notes[7])? "N/A" : notes[7],
																		"0".equalsIgnoreCase(notes[9])? "N/A" : notes[9]
								}; 
				/**data
				 * [0] - purpose
				 * [1] - kind
				 * [2] - color
				 * [3] - gender
				 * [4] - age
				 * [5] - colc no
				 * [6] - colc no date issued
				 * [7] - ctlc no
				 * [8] - ctlc no date issued
				 * [9] - others
				 */
				int i = 0;
				for(String val : data){
					ClearanceRpt rpt = new ClearanceRpt();
					rpt.setF1(labels[i++]);
					rpt.setF2(val);
					reports.add(rpt);
				}
				
				//supportdtls = notes[2];
				cattlepurpose = data[0];
				kind = data[1];
				genderreq = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
				//for cattle pictures
				String cattleImages = data[1]+ ".png";
				String cattle = REPORT_PATH + "cattles" + File.separator + cattleImages.toLowerCase();//"whitecow.png";
				try{File file = new File(cattle);
					FileInputStream off = new FileInputStream(file);
					param.put("PARAM_CATTLE_PICTURE", off);
				}catch(Exception e){}
				
				
				}catch(Exception e){
						
					}
			}
			
			
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("large-cattle-string-1") + requestor +Words.getTagName("large-cattle-string-2")+ civilStatus +Words.getTagName("large-cattle-string-3") + address); 
			str.append(" "+Words.getTagName("large-cattle-string-4")+genderreq+" "+ Words.getTagName("large-cattle-string-5") + kind + " "+Words.getTagName("large-cattle-string-6"));
			str.append(Words.getTagName("large-cattle-string-7") +cattlepurpose);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				str.append(" " + Words.getTagName("large-cattle-string-8") + ced[0]+" "+ Words.getTagName("large-cattle-string-9") + DateUtils.convertDateToMonthDayYear(ced[1])+".");
			}else{
				str.append(".");
			}
			detail_1 = str.toString();
			str = new StringBuilder();
			
			//purpose = "PURPOSE: " + com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getName();
			
			str.append(Words.getTagName("large-cattle-string-10") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("large-cattle-string-11") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " " + Words.getTagName("large-cattle-string-12"));
			str.append(" "+ Words.getTagName("large-cattle-string-13") + barangay +".");
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()== clr.getPurposeType() || com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()== clr.getPurposeType()){
			
			REPORT_NAME = LATE_BIRTH_REPORT_NAME;
			//String gender = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			/**
			 * [0] - born date
			 * [1] - born address
			 * [2] - father name
			 * [3] - mother name
			 * [4] - child name
			 */	
			String[] notes = null;
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				notes = clr.getNotes().split("<:>");
			}
				
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			param.put("PARAM_CTAX", "CTC NO/ISSUED DATE: " + ced[0] +"/" +ced[1]);
			
			param.put("PARAM_FATHER", "Mr. " + notes[2].toUpperCase());
			param.put("PARAM_MOTHER", "Mrs. " + notes[3].toUpperCase());
			param.put("PARAM_CHILD", notes[4].toUpperCase());
			param.put("PARAM_DATE_BORN", DateUtils.convertDateToMonthDayYear(notes[0]));
			param.put("PARAM_BORN_PLACE", notes[1].toUpperCase());
			param.put("PARAM_REQUESTED", requestor);
			
			if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()== clr.getPurposeType()){
				param.put("PARAM_REGISTRAR_OFFICE", Words.getTagName("late-birth-string-1")+ clr.getTaxPayer().getMunicipality().getName() + ", " + clr.getTaxPayer().getProvince().getName()+ ".");
			}else if(com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()== clr.getPurposeType()){
				param.put("PARAM_REGISTRAR_OFFICE", Words.getTagName("late-birth-string-2")+ clr.getTaxPayer().getMunicipality().getName() + ", " + clr.getTaxPayer().getProvince().getName()+ ".");
			}
			
			param.put("PARAM_DAY", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			param.put("PARAM_MONTH", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + DateUtils.getCurrentYear() + ".");
			
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_RESIDENCY.getId()== clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.POSTAL.getId()== clr.getPurposeType() ||
					com.italia.marxmind.bris.enm.Purpose.DIRECT_SELLER_AGENT_APPLICATION.getId()== clr.getPurposeType()){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("residency-string-1") + requestor +Words.getTagName("residency-string-2")+ civilStatus +Words.getTagName("residency-string-3") + address);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			str.append(" "+Words.getTagName("residency-string-4") + ced[0] + " "+ Words.getTagName("residency-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("residency-string-6") + municipal +".");
			}else{
				str.append(".");
			}
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_RESIDENCY.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("residency-string-7");
			}else if(com.italia.marxmind.bris.enm.Purpose.POSTAL.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("residency-string-8");
			}else if(com.italia.marxmind.bris.enm.Purpose.DIRECT_SELLER_AGENT_APPLICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("residency-string-13");
			}
			
			str.append(Words.getTagName("residency-string-9") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("residency-string-10") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " "+ Words.getTagName("residency-string-11"));
			str.append(" "+ Words.getTagName("residency-string-12") + barangay +".");
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_EMPLOYMENT.getId()== clr.getPurposeType()){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			long id = Long.valueOf(clr.getNotes());
			Employee emp = null;
			try{emp = Employee.retrieve(id);}catch(Exception e){}
			
			if(emp.getDateResigned()==null){
				emp.setDateResigned(DateUtils.getCurrentDateYYYYMMDD());
			}
			
			str.append(Words.getTagName("employment-string-1") + emp.getFirstName().toUpperCase() + " " + emp.getMiddleName().toUpperCase() + " " + emp.getLastName().toUpperCase() +" "+Words.getTagName("employment-string-2"));
			str.append(Words.getTagName("employment-string-3") + DateUtils.getMonthName(Integer.valueOf(emp.getDateRegistered().split("-")[1])) + " " + emp.getDateRegistered().split("-")[2] + ", " + emp.getDateRegistered().split("-")[0]);
			str.append(" "+ Words.getTagName("employment-string-4") + DateUtils.getMonthName(Integer.valueOf(emp.getDateResigned().split("-")[1])) + " " + emp.getDateResigned().split("-")[2] + ", " + emp.getDateResigned().split("-")[0]);
			str.append(" "+ Words.getTagName("employment-string-5") + Positions.typeName(emp.getPosition().getId()) + ".");
			
			str.append(Words.getTagName("employment-string-6") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " "+ Words.getTagName("employment-string-7") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0]+ " "+Words.getTagName("employment-string-8"));
			str.append(" "+ Words.getTagName("employment-string-9") + barangay +".");
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			//purpose = "PURPOSE: " + com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
			
			//str.append("\tIssued this " + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " Day of " + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " at ");
			//str.append(" the Office of the Punong Barangay of " + barangay +".");
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_CERT_TRANS_ADD.getId()== clr.getPurposeType()){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			
			String words = Words.getTagName("4ps-transferred-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("4ps-transferred-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>",municipal);
				
				str.append(words);
			}
			
			words = Words.getTagName("4ps-transferred-string-3");
			str.append(words);
			
			words = Words.getTagName("4ps-transferred-string-4");
			words = words.replace("<hisher>", hisher);
			str.append(words);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("4ps-transferred-string-5");
			purpose = purpose.replace("<formeradress>", clr.getNotes().split("<:>")[2].toUpperCase());
			String newaddress = clr.getEmployee().getMunicipality().getName() + " PROVINCE OF " + clr.getEmployee().getProvince().getName();
			purpose = purpose.replace("<newaddress>",newaddress.toUpperCase());
			
			words = Words.getTagName("4ps-transferred-string-6");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();
		}else if(com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()
				|| com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String beneciaryName2 = "";
			String relationship = "";
			String supportdtls = "";
			String gender = "";
			String gender2 = "";
			String benAddress = "";
			String bencivilStatus="";
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			beneciaryName = getAppellation(ben) +" "+ ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			beneciaryName2 = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			bencivilStatus = CivilStatus.typeName(ben.getCivilStatus());
			gender = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			gender2 = ben.getGender().equalsIgnoreCase("1")? "his" : "her";
			//benAddress = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
			benAddress = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = AUTHORIZATION_REPORT_NAME;
			
			str.append(Words.getTagName("authorized-string-1") + requestor +Words.getTagName("authorized-string-2")+ civilStatus + Words.getTagName("authorized-string-3") + address + Words.getTagName("authorized-string-4"));
			str.append(Words.getTagName("authorized-string-5") + relationship + " " + Words.getTagName("authorized-string-6") + beneciaryName + Words.getTagName("authorized-string-7"));
			str.append(Words.getTagName("authorized-string-8") + benAddress + Words.getTagName("authorized-string-9"));
			str.append(Words.getTagName("authorized-string-10"));
			
			if(com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()){
				str.append(Words.getTagName("authorized-string-11"));
			}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()){
				str.append(Words.getTagName("authorized-string-12"));
			}
			
			str.append(" " +Words.getTagName("authorized-string-13")+ gender + " " +Words.getTagName("authorized-string-14")+ supportdtls + Words.getTagName("authorized-string-15") + gender2 + " "+Words.getTagName("authorized-string-16"));
			
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			detail_2 = str.toString();	
			
			param.put("PARAM_BENEFICIARY", requestor);
			param.put("PARAM_RELATIONSHIP_TITLE", relationship);
		
		}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.APPLIANCE_LOAN_REQUIREMENT.getId()==clr.getPurposeType() ||
				com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==clr.getPurposeType()){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("mocar-string-1") + requestor +Words.getTagName("mocar-string-2")+ civilStatus + Words.getTagName("mocar-string-3") + address);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			str.append(" "+ Words.getTagName("mocar-string-4") + ced[0] + " "+ Words.getTagName("mocar-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("mocar-string-6") + municipal +".");
			}else{
				str.append(".");
			}
			
			str.append("\n\n\t"+clr.getNotes());
			
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
				str.append(Words.getTagName("mocar-string-7"));
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType()){
				str.append(Words.getTagName("mocar-string-13"));
			}
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("other-string-8") + com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
			
			
			
			str.append(Words.getTagName("other-string-9") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " " + Words.getTagName("other-string-10") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " " + Words.getTagName("other-string-11"));
			str.append(" " + Words.getTagName("other-string-12") + barangay +".");
			detail_2 = str.toString();
		}else if(com.italia.marxmind.bris.enm.Purpose.MULTIPURPOSE.getId()==clr.getPurposeType()){
			String pur = "";
			int cnt = 1;
			//if("<:>".contains(clr.getNotes())){
			try{
				
				for(String p : clr.getNotes().split("<:>")){
					if(cnt>1){
						pur +="\n"+cnt +")"+ com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(p));
					}else{
						pur = cnt +")"+ com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(p));
					}
					cnt++;
				}
				pur +="\n";
			}catch(Exception e){
				pur = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(clr.getNotes()));
			}	
			/*}else{
				pur = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(clr.getNotes()));
			}*/
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String words = Words.getTagName("multi-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("multi-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>",municipal);
				
				str.append(words);
			}else {
				str.append(".");
			}
			
			String gender = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String gender1 = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("multi-string-3");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
				
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("multi-string-6");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
				
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("multi-string-7");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
				
			}
			
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			//purpose = Words.getTagName("other-string-8") + com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
			purpose = pur;
			
			if(cnt==2){
				str.append("\n");
			}else if(cnt==3){
				str.append("\n\n");
			}else if(cnt==4){
				str.append("\n\n\n");	
			}else if(cnt>4){
				str.append("\n\n\n\n\n");	
			}
			
			words = Words.getTagName("multi-string-5");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();	
		
		}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==clr.getPurposeType() ||
				com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==clr.getPurposeType()){	
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String words = "";
			words = Words.getTagName("motor-permit-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("motor-permit-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>", municipal);
				
				str.append(words);
			}
			words = Words.getTagName("motor-permit-string-3");
			str.append(words);
			String[] note = clr.getNotes().split("<:>");
			str.append("\n\tPlate No: " + note[0]);
			str.append("\n\tModel: " + note[1]);
			str.append("\n\tColor: " + note[2]);
			
			String gender = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			words = Words.getTagName("motor-permit-string-4");
			words = words.replace("<hisher>", gender);
			str.append(words);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==clr.getPurposeType()){
				purpose = Words.getTagName("motor-permit-string-5");
			}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==clr.getPurposeType()){
				purpose = Words.getTagName("motor-permit-string-6");
			}
			
			words = Words.getTagName("motor-permit-string-7");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==clr.getPurposeType()){
			
			
			
			REPORT_NAME = LAND_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String words = "";
			words = Words.getTagName("land-permit-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("land-permit-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>", municipal);
				
				str.append(words);
			}
			words = Words.getTagName("land-permit-string-3");
			String[] note = clr.getNotes().split("<:>");
			
			words = words.replace("<landtype>", LandTypes.typeName(Integer.valueOf(note[0])));
			words = words.replace("<lotno>", note[1]);
			words = words.replace("<areasqrt>", note[2]);
			words = words.replace("<barangayaddress>", barangay);
			words = words.replace("<north>", note[3]);
			words = words.replace("<east>", note[4]);
			words = words.replace("<south>", note[5]);
			words = words.replace("<west>", note[6]);
			str.append(words);
			
			words = Words.getTagName("land-permit-string-4");
			str.append(words);
			
			words = Words.getTagName("land-permit-string-5");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_1 = str.toString();
			//str = new StringBuilder();
			
			//detail_2 = str.toString();
			Employee ipmr = Employee.retrievePosition(Positions.IPMR.getId());
			
			
			param.put("PARAM_IPMR","HON. "+ ipmr.getFirstName().toUpperCase() + " " + ipmr.getLastName().toUpperCase());
			param.put("PARAM_IPMR_POSITION","Barangay "+ipmr.getPosition().getName());
			
		}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==clr.getPurposeType()){
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String words = "";
			words = Words.getTagName("tree-permit-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("tree-permit-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>", municipal);
				
				str.append(words);
			}
			
			String[] note = clr.getNotes().split("<:>");
			str.append("\nNAME OF TREE: " + note[0]);
			str.append("\nNO OF HILLS: " + note[1]);
			str.append("\nLOCATION: " + note[2]);
			
			purpose =  note[3];
			
			String gender = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			words = Words.getTagName("tree-permit-string-3");
			words = words.replace("<hisher>", gender);
			str.append(words);
			
			detail_1 = str.toString();
			str = new StringBuilder();
						
			words = Words.getTagName("tree-permit-string-4");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();

			
		}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==clr.getPurposeType()){
			
			
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String words = "";
			words = Words.getTagName("loan-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<civilstatus>", civilStatus);
			words = words.replace("<requestoraddress>", address);
			str.append(words);
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				
				words = Words.getTagName("loan-string-2");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>", municipal);
				
				str.append(words);
			}
			
			String gender1 = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String gender2 = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("loan-string-3");
				words = words.replace("<he/she>", gender1);
				str.append(words);
			
			}else if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("loan-string-4");
				words = words.replace("<he/she>", gender1);
				words = words.replace("<his/her>", gender2);
				str.append(words);
				
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("loan-string-5");
				words = words.replace("<he/she>", gender1);
				words = words.replace("<his/her>", gender2);
				str.append(words);
				
			}else if(DocTypes.RESIDENCY.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("loan-string-8");
				words = words.replace("<he/she>", gender1);
				words = words.replace("<his/her>", gender2);
				str.append(words);
				
			}else if(DocTypes.INCOME.getId()==clr.getDocumentType()){
				
				words = Words.getTagName("loan-string-9");
				words = words.replace("<he/she>", gender1);
				words = words.replace("<his/her>", gender2);
				str.append(words);
				
			}
			
			
			purpose = Words.getTagName("loan-string-6") + clr.getNotes();
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			words = Words.getTagName("loan-string-7");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			
			detail_2 = str.toString();
			
		}else{
			//REPORT_NAME = OTHERS_REPORT_NAME;
			
			REPORT_NAME = ASSISTANCE_REPORT_NAME;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] ced = null;
			
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			str.append(Words.getTagName("other-string-1") + requestor +Words.getTagName("other-string-2")+ civilStatus + Words.getTagName("other-string-3") + address);
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			str.append(" "+ Words.getTagName("other-string-4") + ced[0] + " "+ Words.getTagName("other-string-5") + DateUtils.convertDateToMonthDayYear(ced[1]) + " "+ Words.getTagName("other-string-6") + municipal +".");
			}else{
				str.append(".");
			}
			String gender = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String gender1 = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			String words="";
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("other-string-7");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
				
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				words = Words.getTagName("other-string-13");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
			
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				words = Words.getTagName("other-string-14");
				words = words.replace("he/she", gender);
				words = words.replace("his/her", gender1);
				str.append(words);
			}
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("other-string-8") + com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
			
			
			
			str.append(Words.getTagName("other-string-9") + DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]) + " " + Words.getTagName("other-string-10") + DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0] + " " + Words.getTagName("other-string-11"));
			str.append(" " + Words.getTagName("other-string-12") + barangay +".");
			detail_2 = str.toString();
			
		}
		
		//System.out.println("Report type " + REPORT_NAME + " purpose type " + clr.getPurposeType());
		
		//do not move this code this code use for custom title
		if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType() || 
				DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
			REPORT_NAME = DOC_OPEN_TITLE_REPORT_NAME;
			param.put("PARAM_DOC_OPEN_TITLE", clr.getCustomTitle().toUpperCase());
			//document validity
			if(clr.getDocumentValidity()>0){
					param.put("PARAM_VALIDITY_NOTE", "Note: This Document is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
			}
		}else if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()) {
			REPORT_NAME = DOC_OPEN_TITLE_REPORT_NAME;
			param.put("PARAM_DOC_OPEN_TITLE", "BARANGAY CERTIFICATE OF LOW INCOME");
			//document validity
			if(clr.getDocumentValidity()>0){
					param.put("PARAM_VALIDITY_NOTE", "Note: This Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
			}
		}	
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		UserDtls user = Login.getUserLogin().getUserDtls();
			
			
			param.put("PARAM_ISSUED_DATE", DateUtils.getCurrentDateMMMMDDYYYY());
			param.put("PARAM_REQUESTOR_NAME", clr.getTaxPayer().getFirstname().toUpperCase() + " " + clr.getTaxPayer().getMiddlename().substring(0,1).toUpperCase() + ". " + clr.getTaxPayer().getLastname().toUpperCase());
			//param.put("PARAM_BARCODE", clr.getTaxPayer().getFullname().toUpperCase());
			param.put("PARAM_CEDULA_NO", clr.getCedulaNumber());
			param.put("PARAM_ISSUED_LOCATION",  MUNICIPALITY + ", " + PROVINCE); // "Lake Sebu, South Cotabato");
			
			param.put("PARAM_OFFICER_DAY", employee.getFirstName().toUpperCase() + " " + employee.getMiddleName().substring(0,1).toUpperCase() + ". " + employee.getLastName().toUpperCase());
			
			if(employee.getPosition().getId()==1){
				param.put("PARAM_OFFICIAL_TITLE",clr.getEmployee().getPosition().getName());
			}else if(employee.getPosition().getId()==2){
				param.put("PARAM_OFFICIAL_TITLE","Barangay "+clr.getEmployee().getPosition().getName() +"\nOfficer of the day");
			}else{
				param.put("PARAM_OFFICIAL_TITLE","Barangay "+clr.getEmployee().getPosition().getName());
			}
			
			int age= taxpayer.getAge();
			String legal = "";
			if(age==0){
				legal = "infant";
				detail_1 = detail_1.replace("of legal age", legal);
				detail_1 = detail_1.replace(" single,", "");
			}else if(age>=1 && age<=5){
				legal = age + " year old, baby";
				detail_1 = detail_1.replace("of legal age", legal);
				detail_1 = detail_1.replace(" single,", "");
			}else if(age>=6 && age<=9){
				legal = age + " year old, kid";
				detail_1 = detail_1.replace("of legal age", legal);
				detail_1 = detail_1.replace(" single,", "");
			}else if(age>=10 && age<=17){
				legal = age + " year old, teenager";
				detail_1 = detail_1.replace("of legal age", legal);
				detail_1 = detail_1.replace(" single,", "");
			}
			
			param.put("PARAM_CLEARANCE_DETAILS", detail_1);
			param.put("PARAM_CLEARANCE_DETAILS2", detail_2);
			param.put("PARAM_PURPOSE", purpose);
			
			//Displayed photo
			boolean isOkToShowPicture = true;
			if(DocTypes.DEATH_CERT.getId()==clr.getDocumentType() || 
					com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()==clr.getPurposeType() || 
						DocTypes.LATE_DEATH_CERT.getId()==clr.getDocumentType() || 
							DocTypes.COE.getId()==clr.getDocumentType()){
				isOkToShowPicture = false;
			}
			
			if(isOkToShowPicture){				
				if(clr.getPhotoId()!=null && !clr.getPhotoId().isEmpty()){
					String picture = copyPhoto(clr.getPhotoId()).replace("\\", "/");
					System.out.println("Images: " + picture);
					//InputStream img = this.getClass().getResourceAsStream("/"+clr.getPhotoId()+".jpg");
					File file = new File(picture);
					if(file.exists()){
						try{
						FileInputStream st = new FileInputStream(file);
						param.put("PARAM_PICTURE", st);
						}catch(Exception e){}
					}else{
						picture = copyPhoto(taxpayer.getPhotoid()).replace("\\", "/");
						file = new File(picture);
						try{
							FileInputStream st = new FileInputStream(file);
							param.put("PARAM_PICTURE", st);
						}catch(Exception e){}
					}
				}
			
			}
			
			
			param.put("PARAM_PROVINCE", "Province of " + PROVINCE);
			param.put("PARAM_MUNICIPALITY", "Municipality of " + MUNICIPALITY);
			param.put("PARAM_BARANGAY", "BARANGAY " + BARANGAY.toUpperCase());
			
			//Officials
			String officialPicture = path + "officials.jpg";
			try{File file = new File(officialPicture);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_OFFICIALS", off);
			}catch(Exception e){}
			
			//logo
			String officialLogo = path + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO", off);
			}catch(Exception e){}
			
			
			if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "certificate.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
				
			}else if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "clearance.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Clearance is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			}else if(DocTypes.DEATH_CERT.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "deathcert.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Death Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			}else if(DocTypes.LATE_DEATH_CERT.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "latedeathcert.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Late Death Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "indigency.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Indigency Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}	
				
			}else if(DocTypes.LATE_BIRTH_REG.getId()==clr.getDocumentType()){
				
				//certificate
				String certificate = path + "latebirth.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			
			}else if(DocTypes.LIVE_BIRTH_REG.getId()==clr.getDocumentType()){
				
				//certificate
				String certificate = path + "livebirth.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}	
				
			}else if(DocTypes.BARANGAY_BUSINESS_PERMIT.getId()==clr.getDocumentType()){
				
				//certificate
				String certificate = path + "businesspermit.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Barangay Business Permit is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
				
			}else if(DocTypes.COE.getId()==clr.getDocumentType()){
				
				//certificate
				String certificate = path + "coe.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This COE is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
				
			}else if(DocTypes.AUTHORIZATION_LETTER.getId()==clr.getDocumentType()){
				
				//authorization
				String authorization = path + "authorization.png";
				try{File file1 = new File(authorization);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This authorization letter is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			}else if(DocTypes.RESIDENCY.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "residency.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Residency Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			}else if(DocTypes.INCOME.getId()==clr.getDocumentType()){
				//certificate
				String certificate = path + "income.png";
				try{File file1 = new File(certificate);
				FileInputStream off1 = new FileInputStream(file1);
				param.put("PARAM_CERTIFICATE", off1);
				}catch(Exception e){}
				
				//document validity
				if(clr.getDocumentValidity()>0){
						param.put("PARAM_VALIDITY_NOTE", "Note: This Income Certificate is valid for " + (clr.getDocumentValidity()==1? "1 month.": clr.getDocumentValidity()+" months."));
				}
			}
			
			//officialseallakesebu
			String lakesebuofficialseal = path + "municipalseal.png";
			try{File file1 = new File(lakesebuofficialseal);
			FileInputStream off2 = new FileInputStream(file1);
			param.put("PARAM_LOGO_LAKESEBU", off2);
			}catch(Exception e){}
			
			//DILG
			String dilg = path + "dilg.png";
			try{File file1 = new File(dilg);
			FileInputStream off2 = new FileInputStream(file1);
			param.put("PARAM_DILG", off2);
			}catch(Exception e){}
			
			//logo
			String logo = path + "barangaylogotrans.png";
			try{File file = new File(logo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_SEALTRANSPARENT", off);
			}catch(Exception e){}
			
			//background
			String backlogo = path + "documentbg.png";
			try{File file = new File(backlogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_BACKGROUND", off);
			}catch(Exception e){}
			
			FileInputStream barPdf = null;
			try{
				Barcode barcode = null;
				
				//barcode = BarcodeFactory.createPDF417(tax.getFullname());
				barcode = BarcodeFactory.create3of9(clr.getTaxPayer().getCardno(), false);
				
				barcode.setDrawingText(false);
				File pdf = new File(clr.getTaxPayer().getCardno()+".png");
				
				BarcodeImageHandler.savePNG(barcode, pdf);
				barPdf = new FileInputStream(pdf);
				param.put("PARAM_BARCODE", barPdf);
			}catch(Exception e){e.printStackTrace();}
			
			if(clr.getIsPayable()==0){
				param.put("PARAM_PAID", "Amount Paid: FREE");
				param.put("PARAM_OR_NUMBER", "OR NO: N/A");
			}else{
				param.put("PARAM_PAID", "Amount Paid: Php "+Currency.formatAmount(clr.getAmountPaid()));
				param.put("PARAM_OR_NUMBER", "OR NO: "+clr.getOrNumber());
			}
			
			
			try{param.put("PARAM_DOCUMENT_NOTE", documentNote(clr));}catch(NullPointerException e) {}
			try{param.put("PARAM_TAGLINE", Words.getTagName("tagline"));}catch(NullPointerException e) {}
			
			Map<Integer, Object> mapObject = Collections.synchronizedMap(new HashMap<Integer, Object>());
			//PATH
			mapObject.put(1, path);
			//REPORT NAME
			mapObject.put(2, REPORT_NAME);
			//JRXML FILE
			mapObject.put(3, jrxmlFile);
			//PARAMS
			mapObject.put(4, param);
			//JRBEAN collection
			mapObject.put(5, beanColl);
				
			return mapObject;	
	}
	
}
