package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.CattleColor;
import com.italia.marxmind.bris.enm.CattleKind;
import com.italia.marxmind.bris.enm.CattlePurpose;
import com.italia.marxmind.bris.enm.CivilStatus;
import com.italia.marxmind.bris.enm.DocTypes;
import com.italia.marxmind.bris.enm.LandTypes;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.Purpose;
import com.italia.marxmind.bris.enm.Relationships;
import com.italia.marxmind.bris.qrcode.QRCode;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ClearanceRpt;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 * 
 * @author mark italia
 * @since 10/27/2019
 * @version 1.0
 * @Description - version 7 layout of document
 */
public class DocumentPrintingV7 {
	
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String BUSINESS_REPORT_MUNICIPAL = DocumentFormatter.getTagName("v7_business-municipal");
	private static final String BUSINESS_REPORT_CERTIFICATE = DocumentFormatter.getTagName("v7_business-municipal");
	private static final String BUSINESS_REPORT_PERMIT = DocumentFormatter.getTagName("v7_business-permit");
	private static final String FISHCAGE_REPORT_NAME = DocumentFormatter.getTagName("v7_fishcage-permit");
	private static final String DEATH_DOC = DocumentFormatter.getTagName("v7_death-document");
	private static final String BURIAL_ASS_DOC = DocumentFormatter.getTagName("v7_burial-indigent");
	private static final String FINANCIAL_ASS_DOC = DocumentFormatter.getTagName("v7_hospital-financial-document");
	private static final String GENERIC_DOC = DocumentFormatter.getTagName("v7_generic-document");
	private static final String DOC_OPEN_TITLE_REPORT_NAME = DocumentFormatter.getTagName("v7_generic-open-document");
	private static final String LARGE_CATTLE_DOC = DocumentFormatter.getTagName("v7_largecattle-document");
	private static final String LATE_BIRTH_DOC = DocumentFormatter.getTagName("v7_birth-document");
	private static final String RESIDENCY_DOC = DocumentFormatter.getTagName("v7_residency-document");
	private static final String COE_DOC = DocumentFormatter.getTagName("v7_coe-document");
	private static final String AUTHORIZATION_DOC = DocumentFormatter.getTagName("v7_authorization-document");
	private static final String LAND_DOC = DocumentFormatter.getTagName("v7_land-document");
	private static final String TRIBAL_LAND_DOC = DocumentFormatter.getTagName("v7_tribal-land-document");
	
public static Map<Integer, Object> printDocumentV7(Clearance clr) {
		System.out.println("============== Building on document form version 7 =====================");
		Map<Integer, Object> mapObject = new HashMap<Integer, Object>();
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		HashMap<String,Object> param = new HashMap<String,Object>();
		Customer taxpayer =Customer.retrieve(clr.getTaxPayer().getCustomerid());
		clr.setTaxPayer(taxpayer);
		
		Employee employee = Employee.retrieve(clr.getEmployee().getId());//officer of the Day
		clr.setEmployee(employee);
		
		Employee kapitan = Employee.retrievePosition(Positions.CAPTAIN.getId());
		String kapNameSign = kapitan.getFirstName() + " "+ kapitan.getMiddleName().substring(0, 1) +". " + kapitan.getLastName(); 
		
		
		String REPORT_NAME = "";
		StringBuilder str = new StringBuilder();
		String detail_1 = "";
		String detail_2 = "";
		String purpose = "";
		String civilStatus = CivilStatus.typeName(clr.getTaxPayer().getCivilStatus());
		civilStatus = civilStatus.toLowerCase();
		
		String requestorPrintedName = clr.getTaxPayer().getFirstname() + " " + clr.getTaxPayer().getMiddlename().substring(0,1) + ". " + clr.getTaxPayer().getLastname();
		String requestor = DocumentPrinting.getAppellation(clr.getTaxPayer()) + " " + requestorPrintedName.toUpperCase();
		int reqage = taxpayer.getAge();
		if(reqage<=17) {
			requestor = clr.getTaxPayer().getFirstname().toUpperCase() + " " + clr.getTaxPayer().getMiddlename().substring(0,1).toUpperCase() + ". " + clr.getTaxPayer().getLastname().toUpperCase();
			
			if(reqage>=0 && reqage<=5){
				civilStatus = "Baby";
			}else if(reqage>=6 && reqage<=12){
				civilStatus = "Kid";
			}else{
				civilStatus = civilStatus+"/Teenager";
			}
			
		}
		
		String address = clr.getTaxPayer().getCompleteAddress();
		String municipal = MUNICIPALITY + ", " + PROVINCE;
				try{municipal = clr.getEmployee().getMunicipality().getName() + ", " + clr.getEmployee().getProvince().getName();}catch(Exception e) {}
		try{municipal = clr.getCedulaNumber().split("<:>")[2];}catch(Exception e){}
		String barangay = BARANGAY +"," + MUNICIPALITY + ", " + PROVINCE;
		try{barangay = clr.getEmployee().getBarangay().getName() + ", " + clr.getEmployee().getMunicipality().getName() + ", " + clr.getEmployee().getProvince().getName();}catch(Exception e) {}
		List<ClearanceRpt> reports = new ArrayList<ClearanceRpt>();
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		StringBuilder word = new StringBuilder();
		if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== clr.getPurposeType() || 
				com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== clr.getPurposeType() ||
					com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType() ||
							com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType() ||
						com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){ //business
			
			
			if(clr.getMultilivelihood()!=null && clr.getMultilivelihood().size()>0){
				for(MultiLivelihood mu : clr.getMultilivelihood()){
					ClearanceRpt rpt = new ClearanceRpt();
					Livelihood liv = mu.getLivelihood();
					try{
						liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + liv.getId(), new String[0]).get(0);
					}catch(Exception e){}
					
					String name = liv.getBusinessName()+"\n";
					       name += liv.getPurokName() + ", Brgy. " + liv.getBarangay().getName() + ", " + liv.getMunicipality().getName()+ ", " + liv.getProvince().getName();
					       rpt.setF1(name);
					            
					reports.add(rpt);
					
				}
			}
			
			//String word = "";
			
			if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_NEW.getId()== clr.getPurposeType()){
				purpose="Business Application";
				REPORT_NAME = BUSINESS_REPORT_MUNICIPAL;
				str=new StringBuilder();
				
				str.append(requestor);
				detail_1 = str.toString();
				str=new StringBuilder();
				str.append(Words.getTagName("business-1"));
				
				param.put("PARAM_ISSUED", Words.getTagName("business-5"));
				param.put("PARAM_REQUESTOR_POSITION", Words.getTagName("business-6"));
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_RENEWAL.getId()== clr.getPurposeType()){
				purpose="Business Renewal";
				REPORT_NAME = BUSINESS_REPORT_MUNICIPAL;
				str=new StringBuilder();
				
				str.append(requestor);
				detail_1 = str.toString();
				str=new StringBuilder();
				str.append(Words.getTagName("business-1"));
				
				param.put("PARAM_ISSUED", Words.getTagName("business-5"));
				param.put("PARAM_REQUESTOR_POSITION", Words.getTagName("business-6"));
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_CERTIFICATION.getId()== clr.getPurposeType()){
				purpose="Business Certification";
				REPORT_NAME = BUSINESS_REPORT_CERTIFICATE;
				str=new StringBuilder();
				
				str.append(requestor);
				detail_1 = str.toString();
				str=new StringBuilder();
				word.append(Words.getTagName("business-3").replace("<barangayname>", "Brgy. " + BARANGAY));
				str.append(word);
				
				param.put("PARAM_ISSUED", Words.getTagName("business-9"));
				param.put("PARAM_REQUESTOR_POSITION", "Owner");
			}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_FOR_LOAN_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose="Requirement for Loan Application";
				REPORT_NAME = BUSINESS_REPORT_CERTIFICATE;
				str=new StringBuilder();
				
				str.append(requestor);
				detail_1 = str.toString();
				str=new StringBuilder();
				str.append(Words.getTagName("business-3").replace("<barangayname>", "Brgy. " + BARANGAY));
				
				param.put("PARAM_ISSUED", Words.getTagName("business-7"));
				param.put("PARAM_REQUESTOR_POSITION", "Owner");
			}else if(com.italia.marxmind.bris.enm.Purpose.RETIREMENT_BUSINESS.getId()== clr.getPurposeType()){
				purpose="Requirement for Retirement of Business";
				REPORT_NAME = BUSINESS_REPORT_CERTIFICATE;
				str=new StringBuilder();
				
				str.append(requestor);
				detail_1 = str.toString();
				str=new StringBuilder();
				str.append(Words.getTagName("business-8").replace("<barangayname>", "Brgy. " + BARANGAY));
				
				param.put("PARAM_ISSUED", Words.getTagName("business-7"));
				param.put("PARAM_REQUESTOR_POSITION", Words.getTagName("business-6"));
			}
			
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("business-4"));
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				String[] ced = clr.getCedulaNumber().split("<:>");
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, ced[0]);
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, DateUtils.convertDateToMonthDayYear(ced[1]));
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, municipal);
				str.append(word);
			}else {
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, "N/A");
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, "N/A");
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, "N/A");
				str.append(word);
			}
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.BUSINESS_PERMIT.getId()== clr.getPurposeType()){ 	
			
			REPORT_NAME = BUSINESS_REPORT_PERMIT;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String[] notes = clr.getNotes().split("<:>");
			/**
			 * [0] - Control No
			 * [1] - NEW/RENEWAL
			 * [2] - MEMO
			 * [3] - Business Engaged
			 */
			String yearReg = DateUtils.getCurrentYear()+"";
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
					yearReg = liv.getDateRegistered().split("-")[0];
				}
			}
			String issuedYear = clr.getIssuedDate().split("-")[0];
			if(issuedYear.equalsIgnoreCase(yearReg)) {
				purpose = "Business Permit Application";
			}else {
				purpose = "Business Permit Renewal";
			}
			
			param.put("PARAM_BUSINESS_TYPE", notes[3]);
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
			}
			
			
			word = new StringBuilder();
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				
				word.append(Words.getTagName("business-permit-barangay-string-2"));
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, ced[0]);
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, DateUtils.convertDateToMonthDayYear(ced[1]));
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, municipal);
				
				param.put("PARAM_CTAX", word.toString());
			}else{
				word.append(Words.getTagName("business-permit-barangay-string-2"));
				
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, "N/A");
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, "N/A");
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, "N/A");
				
				param.put("PARAM_CTAX",word.toString());
			}
			
			param.put("PARAM_DAY", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			param.put("PARAM_MONTH", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])) + " " + clr.getIssuedDate().split("-")[0]);
		}else if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== clr.getPurposeType() || com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== clr.getPurposeType()){ //Fish cage
			REPORT_NAME = FISHCAGE_REPORT_NAME;
			if(clr.getMultilivelihood()!=null && clr.getMultilivelihood().size()>0){
				int cnt = 1;
				for(MultiLivelihood mu : clr.getMultilivelihood()){
					ClearanceRpt rpt = new ClearanceRpt();
					Livelihood liv = mu.getLivelihood();
					try{
						liv = Livelihood.retrieve(" AND live.isactivelive AND live.livelihoodid=" + liv.getId(), new String[0]).get(0);
					}catch(Exception e){}
					
					/*rpt.setF1(liv.getPurokName());
					rpt.setF2(liv.getAreaMeter());
					try{rpt.setF3(liv.getSupportingDetails().toLowerCase());}catch(Exception e) {}*/
					
					String details = cnt + ")" + liv.getTaxPayer().getFullname() + "/" +liv.getPurokName() + "/" +liv.getAreaMeter() + "/" + (liv.getSupportingDetails()!=null? liv.getSupportingDetails().toUpperCase() : "N/A");
					rpt.setF1(details);
					reports.add(rpt);
					cnt++;
				}
			}
			
			word = new StringBuilder();
			word.append(Words.getTagName("fish-cage-string-1"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<requestoraddress>"),word.indexOf("<requestoraddress>")+18, address);
			str.append(word);
			
			str.append(Words.getTagName("fish-cage-string-2"));
			
			detail_1 = str.toString();
			
			str = new StringBuilder();
			if(com.italia.marxmind.bris.enm.Purpose.FISCH_CAGE.getId()== clr.getPurposeType()){
				purpose =  Words.getTagName("fish-cage-string-4");
			}else if(com.italia.marxmind.bris.enm.Purpose.FISH_CAGE_RENEWAL.getId()== clr.getPurposeType()){
				purpose =  Words.getTagName("fish-cage-string-5");
			}
			
			
			str.append(Words.getTagName("fish-cage-string-6"));
			
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("business-4"));
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				String[] ced = clr.getCedulaNumber().split("<:>");
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, ced[0]);
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, DateUtils.convertDateToMonthDayYear(ced[1]));
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, municipal);
				str.append(word);
			}else {
				word.replace(word.indexOf("<purpose>"),word.indexOf("<purpose>")+9, purpose);
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, "N/A");
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, "N/A");
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, "N/A");
				str.append(word);
			}
			
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
			if(ben.getAge()>17) {
				beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}else {
				beneciaryName = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
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
			
			
			REPORT_NAME = DEATH_DOC;
			
			//note instead of died person to certify it should be the requestor
			word = new StringBuilder();
			if(address.equalsIgnoreCase(benAddress)){
				word.append(Words.getTagName("asstance-death-string-1"));
				word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
				word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
				word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
				word.replace(word.indexOf("<diedperson>"),word.indexOf("<diedperson>")+12, beneciaryName);
			}else{
				word.append(Words.getTagName("asstance-death-string-2"));
				word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
				word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
				//word.replace(word.indexOf("<requestoraddress>"),word.indexOf("<requestoraddress>")+18, address);
				word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
				word.replace(word.indexOf("<diedperson>"),word.indexOf("<diedperson>")+12, beneciaryName);
				//word.replace(word.indexOf("<diedpersonaddress>"),word.indexOf("<diedpersonaddress>")+19, benAddress);
			}
			
			str.append(word);
			 
			
			str.append(Words.getTagName("asstance-death-string-3")+ supportdtls);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-death-string-4"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			str.append(word);
			
			if(com.italia.marxmind.bris.enm.Purpose.DEATH_CERT.getId()== clr.getPurposeType() 
					){
				purpose += Words.getTagName("asstance-death-string-5");
			}else if(com.italia.marxmind.bris.enm.Purpose.LATE_DEATH_CERT.getId()== clr.getPurposeType()){
				purpose += Words.getTagName("asstance-death-string-6");
			}
			str.append(purpose);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			//str = new StringBuilder();
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			//str.append(words);
			
			detail_2 = word.toString();	
			
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
			if(ben.getAge()>17) {
				beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}else {
				beneciaryName = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			REPORT_NAME = BURIAL_ASS_DOC;
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-burial-string-1"));
			word.replace(word.indexOf("<diedperson>"),word.indexOf("<diedperson>")+12, beneciaryName);
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<diedpersonaddress>"),word.indexOf("<diedpersonaddress>")+19, address);
			str.append(word);
			
			str.append(supportdtls);
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-burial-string-2").replace("<heshe>", heshe));
			//word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-burial-string-3"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
			word.replace(word.indexOf("<diedperson>"),word.indexOf("<diedperson>")+12, beneciaryName);
			str.append(word);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-burial-string-4");
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();	
			
			
		}else if(com.italia.marxmind.bris.enm.Purpose.FINANCIAL.getId() == clr.getPurposeType()){	
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String heshe = "";
			int age = 0;
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
			if(age>17) {
				beneciaryName =  DocumentPrinting.getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}else {
				beneciaryName =  ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			//supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = FINANCIAL_ASS_DOC;
			
			civilStatus = civilStatus(age, civilStatus);
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-financial-string-1"));
			word.replace(word.indexOf("<patientname>"),word.indexOf("<patientname>")+13, beneciaryName);
			word.replace(word.indexOf("<age>"),word.indexOf("<age>")+5, age+"");
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<patientaddress>"),word.indexOf("<patientaddress>")+16, address);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-financial-string-2"));
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-financial-string-3"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
			str.append(word);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-financial-string-4");
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
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
			if(age>17) {
				beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}else {
				beneciaryName = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
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
			
			
			REPORT_NAME = FINANCIAL_ASS_DOC;
			
			civilStatus = civilStatus(age, civilStatus);	
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-medical-string-1"));
			word.replace(word.indexOf("<patientname>"),word.indexOf("<patientname>")+13, beneciaryName);
			word.replace(word.indexOf("<age>"),word.indexOf("<age>")+5, age+"");
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<patientaddress>"),word.indexOf("<patientaddress>")+16, address);
			word.replace(word.indexOf("<hospital>"),word.indexOf("<hospital>")+10, supportdtls.toUpperCase());
			str.append(word);		
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-medical-string-2"));
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}	
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
			str.append(word);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			if(com.italia.marxmind.bris.enm.Purpose.FIN_MED.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-medical-string-3");
			}else if(com.italia.marxmind.bris.enm.Purpose.MEDICAL.getId()== clr.getPurposeType()){ 
				purpose = Words.getTagName("asstance-medical-string-4");
			}
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-medical-string-5"));
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, gender);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();
		}else if(com.italia.marxmind.bris.enm.Purpose.CALAMITY.getId()== clr.getPurposeType()){
			REPORT_NAME = FINANCIAL_ASS_DOC;
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-calamity-string-1"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<requestoraddress>"),word.indexOf("<requestoraddress>")+18, address);
			str.append(word);	
			
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-calamity-string-2"));
			word.replace(word.indexOf("<hisher>"),word.indexOf("<hisher>")+8, hisher);
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}	
			str.append(word);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			purpose = Words.getTagName("asstance-calamity-string-3");
			
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ASSISTANCE.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			String gender = "";
			int age = 0;
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				try{
				
			String[] notes = clr.getNotes().split("<:>"); 
			/**
			 * [0] - Beneficiary
			 * [1] - Relationship
			 * [2] - supporting details
			 */
			
			Customer ben = Customer.retrieve(Long.valueOf(notes[0]));
			age=ben.getAge();
			if(age>17) {
				beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
				
			}else {
				beneciaryName = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			gender = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = FINANCIAL_ASS_DOC;
			
			civilStatus = civilStatus(age, civilStatus);
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-indigent-string-1"));
			word.replace(word.indexOf("<patientname>"),word.indexOf("<patientname>")+13, beneciaryName);
			word.replace(word.indexOf("<age>"),word.indexOf("<age>")+5, age+"");
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<patientaddress>"),word.indexOf("<patientaddress>")+16, address);
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, gender);
			str.append(word);	
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-indigent-string-2"));
			
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}else if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.LOW_INCOME.getName());
			}	
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
			str.append(word);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			purpose = Words.getTagName("asstance-indigent-string-3");
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_ATTORNEYS_ASSISTANCE.getId()== clr.getPurposeType()){
			
			REPORT_NAME = FINANCIAL_ASS_DOC;
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			String hisher = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-attorney-string-1"));
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<requestoraddress>"),word.indexOf("<requestoraddress>")+18, address);
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-attorney-string-2"));
			word.replace(word.indexOf("<hisher>"),word.indexOf("<hisher>")+8, hisher);
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}	
			str.append(word);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			purpose = Words.getTagName("asstance-attorney-string-3");
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-attorney-string-4"));
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				String[] ced = clr.getCedulaNumber().split("<:>");
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, ced[0]);
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, DateUtils.convertDateToMonthDayYear(ced[1]));
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, municipal);
				str.append(word);
			}else {
				word.replace(word.indexOf("<ctcno>"),word.indexOf("<ctcno>")+7, "N/A");
				word.replace(word.indexOf("<ctcissueddate>"),word.indexOf("<ctcissueddate>")+15, "N/A");
				word.replace(word.indexOf("<ctcissuedaddress>"),word.indexOf("<ctcissuedaddress>")+18, "N/A");
				str.append(word);
			}
			
			detail_2 = str.toString();
		
		}else if(com.italia.marxmind.bris.enm.Purpose.OTHER_LEGAL_MATTERS.getId()== clr.getPurposeType()){
			
			REPORT_NAME = GENERIC_DOC;
			word = new StringBuilder();
			word.append(Words.getTagName("legal-string-1"));
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}		
			str.append(word);		
			
			supplyDetails(clr, reports, address, civilStatus, municipal);
			
			ClearanceRpt rpt = new ClearanceRpt();
			rpt.setF1("PURPOSE:");rpt.setF2(Words.getTagName("legal-string-3"));
			reports.add(rpt);
			
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.OTHER_LEGAL_MATTERS);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			int index = reports.size();
			index -=1;
			
			String heshe = taxpayer.getGender().equalsIgnoreCase("1")? "he" : "she";
			word = new StringBuilder();
			word.append(Words.getTagName("legal-string-2"));
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
				word.replace(word.indexOf("has no derogatory"),word.indexOf("has no derogatory")+17, "has a pending case");
				word.replace(word.indexOf(" and a law abiding citizen in our locality."),word.indexOf(" and a law abiding citizen in our locality.")+43, ".");
			}
			str.append(word);
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office-2"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();	
			
		}else if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_HOSPL_ASS.getId()== clr.getPurposeType()){
			
			String beneciaryName = "";
			String relationship = "";
			String supportdtls = "";
			int age=clr.getTaxPayer().getAge();
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
			age = ben.getAge();
			if(age>17) {
				beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}else {
				beneciaryName = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
			}
			civilStatus = CivilStatus.typeName(ben.getCivilStatus());
			heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
			address = ben.getCompleteAddress();
			relationship = Relationships.typeName(Integer.valueOf(notes[1]));
			supportdtls = notes[2];
			
				}catch(Exception e){
					
				}
			}else{
				
			}	
			
			
			REPORT_NAME = FINANCIAL_ASS_DOC;
			
			civilStatus = civilStatus(age, civilStatus);
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-hospital-string-1"));
			word.replace(word.indexOf("<patientname>"),word.indexOf("<patientname>")+13, beneciaryName);
			word.replace(word.indexOf("<age>"),word.indexOf("<age>")+5, age+"");
			word.replace(word.indexOf("<civilstatus>"),word.indexOf("<civilstatus>")+13, civilStatus);
			word.replace(word.indexOf("<patientaddress>"),word.indexOf("<patientaddress>")+16, address);
			word.replace(word.indexOf("<hospital>"),word.indexOf("<hospital>")+10, supportdtls.toUpperCase());
			str.append(word);	
			
			str.append(Words.getTagName("asstance-hospital-string-2").replace("<heshe>", heshe));	
			
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-hospital-string-3"));
			if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());	
			}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.INDIGENT.getName());
			}else if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.LOW_INCOME.getName());
			}	
			word.replace(word.indexOf("<requestor>"),word.indexOf("<requestor>")+11, requestor);
			word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, relationship);
			str.append(word);
			
			detail_1 = str.toString();
			
			ClearanceRpt rpt = new ClearanceRpt();
			reports.add(rpt);
			
			str = new StringBuilder();
			
			
			purpose = Words.getTagName("asstance-hospital-string-4");
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
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
			
			REPORT_NAME = GENERIC_DOC;
			word = new StringBuilder();
			word.append(Words.getTagName("asstance-cert-string-1"));
			if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
			}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());
			}else {
				word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.typeName(clr.getDocumentType()));
			}
			
			
			str.append(word);
			
			supplyDetails(clr, reports, address, civilStatus, municipal);
			
			if(com.italia.marxmind.bris.enm.Purpose.INDIGENT_CERT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-3") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EDUCATION_SCHOLARSHIP_ASSISTANCE.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-4") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.FOR_INDIGENT_EMPLOYMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-5") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.SPES_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-6") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.CHED_SCHOLARSHIP.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-7") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.TESDA_REQUIREMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-8") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.KABUGWASON_REQUIREMENT.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-9") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_APPLICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-10") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.CONFIRMATION_APPLICATION.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-11") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.ESC_SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-12") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOLARSHIP_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-13") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOOL_REG.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-14") + "\n";
			}else if(com.italia.marxmind.bris.enm.Purpose.SCHOOL_REQUIREMENTS.getId()== clr.getPurposeType()){
				purpose = Words.getTagName("asstance-cert-string-15") + "\n";
			}
			
			ClearanceRpt rpt = new ClearanceRpt();
			rpt.setF1("PURPOSE:");rpt.setF2(purpose);
			reports.add(rpt);
			       
			detail_1 = str.toString();
			str = new StringBuilder();
			
			String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
			word = new StringBuilder();
			if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()){
				String rel = taxpayer.getGender().equalsIgnoreCase("1")? "son" : "daughter";
				word.append(Words.getTagName("asstance-cert-string-16"));
				word.replace(word.indexOf("<relationship>"),word.indexOf("<relationship>")+14, rel);
				word.replace(word.indexOf("<parents>"),word.indexOf("<parents>")+9, clr.getNotes());
				word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
				str.append(word);	
			}else if(DocTypes.INCOME.getId()==clr.getDocumentType()){
				word.append(Words.getTagName("asstance-cert-string-17").replace("<heshe>", heshe));
				str.append(word);
			}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
				word.append(Words.getTagName("asstance-cert-string-18").replace("<heshe>", heshe));
				str.append(word);
			}else {
				word.append(Words.getTagName("asstance-cert-string-17").replace("<heshe>", heshe));
				str.append(word);
			}
			
			word = new StringBuilder();
			word.append(Words.getTagName("issued-date-office-2"));
			word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
			word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
			str.append(word);
			
			detail_2 = str.toString();	
			
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LARGE_CATTLE.getId()== clr.getPurposeType()){
			
			String cattlepurpose = "";
			String kind = "";
			String genderreq = "his";
			String animal="";
			REPORT_NAME = LARGE_CATTLE_DOC;
			if(clr.getNotes()!=null && !clr.getNotes().isEmpty()){
				
				try{
				
				String[] labels = {"PURPOSE:","KIND:","COLOR:","GENDER:","AGE:","COLC NO.:","DATE OF ISSUE:","CTLC NO.:","DATE OF ISSUE:","REMARKS:"};	
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
				animal = data[1];
				kind = data[1];
				genderreq = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "his" : "her";
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
			
			
			String words = Words.getTagName("large-cattle-string-1");
			words = words.replace("<requestor>", requestor);
			words = words.replace("<requestoraddress>", address);
			
			String[] ced = null;
			if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
				ced = clr.getCedulaNumber().split("<:>");
				words = words.replace("<ctcno>", ced[0]);
				words = words.replace("<ctcissueddate>", DateUtils.convertDateToMonthDayYear(ced[1]));
				words = words.replace("<ctcissuedaddress>",municipal);
			}else {
				words = words.replace("<ctcno>", "N/A");
				words = words.replace("<ctcissueddate>", "N/A");
				words = words.replace("<ctcissuedaddress>","N/A");
			}
			
			
			words = words.replace("<hisher>", genderreq);
			words = words.replace("<animal>", animal);
			str.append(words);
			
			detail_1 = str.toString();
			str = new StringBuilder();
			
			words = Words.getTagName("large-cattle-string-2");
			words = words.replace("<requestor>", requestor);
			str.append(words);
			
			words = Words.getTagName("issued-date-office-2");
			words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
			words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
			words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
			words = words.replace("<barangayaddress>", barangay);
			str.append(words);
			detail_2 = str.toString();
			
		}else if(com.italia.marxmind.bris.enm.Purpose.LATE_BIRTH_REGISTRATION.getId()== clr.getPurposeType() || com.italia.marxmind.bris.enm.Purpose.REGISTRATION_OF_LIVE_BIRTH.getId()== clr.getPurposeType()){
			
			REPORT_NAME = LATE_BIRTH_DOC;
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
		
		REPORT_NAME = RESIDENCY_DOC;
		
		
		
		
		String words = Words.getTagName("residency-string-1");
		if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			words = words.replace("<docttype>", DocTypes.CLEARANCE.getName());
		}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
			words = words.replace("<docttype>", DocTypes.CERTIFICATE.getName());
		}else {
			words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		}
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_RESIDENCY.getId()== clr.getPurposeType()){
			purpose = Words.getTagName("residency-string-4");
		}else if(com.italia.marxmind.bris.enm.Purpose.POSTAL.getId()== clr.getPurposeType()){
			purpose = Words.getTagName("residency-string-5");
		}else if(com.italia.marxmind.bris.enm.Purpose.DIRECT_SELLER_AGENT_APPLICATION.getId()== clr.getPurposeType()){
			purpose = Words.getTagName("residency-string-6");
		}
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose);
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		String heshe = taxpayer.getGender().equalsIgnoreCase("1")? "he" : "she";
		words = Words.getTagName("residency-string-3");
		words = words.replace("<heshe>", heshe);
		str.append(words);
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();
	
	}else if(com.italia.marxmind.bris.enm.Purpose.CERTIFICATE_EMPLOYMENT.getId()== clr.getPurposeType()){	
		
		REPORT_NAME = COE_DOC;
		
		ClearanceRpt rpt = new ClearanceRpt();
		reports.add(rpt);
		
		long id = Long.valueOf(clr.getNotes());
		Employee emp = null;
		try{emp = Employee.retrieve(id);}catch(Exception e){}
		
		if(emp.getDateResigned()==null){
			emp.setDateResigned(DateUtils.getCurrentDateYYYYMMDD());
		}
		String name = emp.getFirstName() + " " + emp.getMiddleName() + " " + emp.getLastName();
		String words = Words.getTagName("employment-string-1");
		words = words.replace("<employee>", name.toUpperCase());
		words = words.replace("<startdate>",DateUtils.getMonthName(Integer.valueOf(emp.getDateRegistered().split("-")[1])) + " " + emp.getDateRegistered().split("-")[2] + ", " + emp.getDateRegistered().split("-")[0]);
		words = words.replace("<enddate>", DateUtils.getMonthName(Integer.valueOf(emp.getDateResigned().split("-")[1])) + " " + emp.getDateResigned().split("-")[2] + ", " + emp.getDateResigned().split("-")[0]);
		words = words.replace("<position>", Positions.typeName(emp.getPosition().getId()));
		str.append(words);
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		detail_2 = str.toString();
	
	}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_CERT_TRANS_ADD.getId()== clr.getPurposeType()){	
		
		REPORT_NAME = GENERIC_DOC;
		
		String words = Words.getTagName("4ps-transferred-string-1");
		words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		purpose = Words.getTagName("4ps-transferred-string-3");
		purpose = purpose.replace("<formeradress>", clr.getNotes().split("<:>")[2].toUpperCase());
		String newaddress = clr.getEmployee().getMunicipality().getName() + " PROVINCE OF " + clr.getEmployee().getProvince().getName();
		purpose = purpose.replace("<newaddress>",newaddress.toUpperCase());
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose);
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		words = Words.getTagName("4ps-transferred-string-2");
		str.append(words);
		
		words = Words.getTagName("issued-date-office-2");
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
		String heshe = "";
		String hisher = "";
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
		beneciaryName = DocumentPrinting.getAppellation(ben) +" "+ ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
		beneciaryName2 = ben.getFirstname().toUpperCase() + " " + ben.getMiddlename().substring(0,1).toUpperCase() + ". " + ben.getLastname().toUpperCase();
		bencivilStatus = CivilStatus.typeName(ben.getCivilStatus());
		heshe = ben.getGender().equalsIgnoreCase("1")? "he" : "she";
		hisher = ben.getGender().equalsIgnoreCase("1")? "his" : "her";
		//benAddress = ben.getPurok().getPurokName() + ", " + ben.getBarangay().getName() + ", " + ben.getMunicipality().getName() + ", " + ben.getProvince().getName();
		benAddress = ben.getCompleteAddress();
		relationship = Relationships.typeName(Integer.valueOf(notes[1]));
		supportdtls = notes[2];
		
			}catch(Exception e){
				
			}
		}else{
			
		}	
		
		
		REPORT_NAME = AUTHORIZATION_DOC;
		
		String words = Words.getTagName("authorized-string-1");
		words = words.replace("<requestor>", requestor);
		words = words.replace("<civilstatus>", civilStatus);
		words = words.replace("<requestoraddress>", address);
		words = words.replace("<relationship>", relationship);
		words = words.replace("<beneficiary>", beneciaryName);
		words = words.replace("<beneficiaryaddress>", benAddress);
		
		if(com.italia.marxmind.bris.enm.Purpose.SENIOR_CITIZEN_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()){
			words = words.replace("<organization>", Words.getTagName("authorized-string-2"));
		}else if(com.italia.marxmind.bris.enm.Purpose.FORPS_AUTHORIZATION_LETTER.getId()== clr.getPurposeType()){
			words = words.replace("<organization>", Words.getTagName("authorized-string-3"));
		}
		
		words = words.replace("<heshe>", heshe);
		words = words.replace("<reason>", supportdtls);
		words = words.replace("<hisher>", hisher);
		str.append(words);
		
		detail_1 = str.toString();
		
		ClearanceRpt rpt = new ClearanceRpt();
		reports.add(rpt);
		
		str = new StringBuilder();
		
		detail_2 = str.toString();	
		
		param.put("PARAM_BENEFICIARY", requestor);
		param.put("PARAM_RELATIONSHIP_TITLE", relationship);
	
	}else if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==clr.getPurposeType() ||
			com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==clr.getPurposeType() ||
			com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==clr.getPurposeType() ||
			com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==clr.getPurposeType()){	
		
		REPORT_NAME = GENERIC_DOC;
		
		
		String words = Words.getTagName("mocar-string-1");
		words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		if(com.italia.marxmind.bris.enm.Purpose.MOTORCYCLE_LOAN_REQUIREMENT.getId()==clr.getPurposeType()) {
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.MOTORCYCLE_LOAN_REQUIREMENT);
		}else if(com.italia.marxmind.bris.enm.Purpose.CAR_LOAN_REQUIREMENT.getId()==clr.getPurposeType()){
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.CAR_LOAN_REQUIREMENT);
		}else if(com.italia.marxmind.bris.enm.Purpose.PAG_IBIG_LOAN_REQUIREMENT.getId()==clr.getPurposeType()){
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.PAG_IBIG_LOAN_REQUIREMENT);
		}else if(com.italia.marxmind.bris.enm.Purpose.SSS_LOAN_REQUIREMENT.getId()==clr.getPurposeType()){
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.SSS_LOAN_REQUIREMENT);
		}
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		str.append("\t"+clr.getNotes()+"\n\n");
		
		words = Words.getTagName("mocar-string-3");
		int index = reports.size();
		index -=1;
		if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
			words = words.replace("has no derogatory", "has a pending case");
			words = words.replace(" and a law abiding citizen in our locality.", ".");
		}
		str.append(words);
		
		ClearanceRpt rpt = new ClearanceRpt();
		purpose = com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
		rpt.setF1("PURPOSE:"); rpt.setF2(purpose);
		reports.add(rpt);
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
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
			//pur +="\n";
		}catch(Exception e){
			pur = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(clr.getNotes()));
		}	
		/*}else{
			pur = com.italia.marxmind.bris.enm.Purpose.typeName(Integer.valueOf(clr.getNotes()));
		}*/
		
		REPORT_NAME = GENERIC_DOC;
		
		
		
		String words = Words.getTagName("multi-string-1");
		if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			words = words.replace("<docttype>", DocTypes.CLEARANCE.getName());
		}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			words = words.replace("<docttype>", DocTypes.CERTIFICATE.getName());
		}else {
			words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		}
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:"); rpt.setF2(pur);
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
			
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.MULTIPURPOSE);
			
			/*rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:"); rpt.setF2(Words.getTagName("multi-string-2"));
			reports.add(rpt);*/
			
			words = Words.getTagName("multi-string-4");
			int index = reports.size();
			index -=1;
			if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
				words = words.replace("has no derogatory", "has a pending case");
				words = words.replace(" and a law abiding citizen in our locality.", ".");
			}
			str.append(words);
			
		}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
			
			reports = checkSummonRemarks(clr.getTaxPayer(),reports,Purpose.MULTIPURPOSE);
			
			/*rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:"); rpt.setF2(Words.getTagName("multi-string-2"));
			reports.add(rpt);*/
			
			words = Words.getTagName("multi-string-4");
			int index = reports.size();
			index -=1;
			if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
				words = words.replace("has no derogatory", "has a pending case");
			}
			words = words.replace(" and a law abiding citizen in our locality.", ".");
			str.append(words);
			
		}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:"); rpt.setF2(Words.getTagName("multi-string-3"));
			reports.add(rpt);
			String heshe = taxpayer.getGender().equalsIgnoreCase("1")? "he" : "she";
			words = Words.getTagName("multi-string-5");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
		}
		
		
		
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();	
	
	}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==clr.getPurposeType() ||
			com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==clr.getPurposeType()){	
		
		REPORT_NAME = GENERIC_DOC;
		
		String words = Words.getTagName("multi-string-1");
		words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_APPLICATION.getId()==clr.getPurposeType()){
			purpose = Words.getTagName("motor-permit-string-3");
		}else if(com.italia.marxmind.bris.enm.Purpose.MUNICIPAL_MOTORCYCLE_OPERATOR_RENEWAL.getId()==clr.getPurposeType()){
			purpose = Words.getTagName("motor-permit-string-4");
		}
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:"); rpt.setF2(purpose);
		reports.add(rpt);
		
		String[] note = clr.getNotes().split("<:>");
		rpt = new ClearanceRpt();
		rpt.setF1("PLATE NO:"); rpt.setF2(note[0]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("MODEL:"); rpt.setF2(note[1]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("COLOR:"); rpt.setF2(note[2]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("REMARKS:"); rpt.setF2(Words.getTagName("motor-permit-string-2"));
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();
	
	}else if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==clr.getPurposeType() 
			|| com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()==clr.getPurposeType() 
			|| com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()==clr.getPurposeType()){
		
		REPORT_NAME = LAND_DOC;
		
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
		
		if(com.italia.marxmind.bris.enm.Purpose.LAND_OWNERSHIP.getId()==clr.getPurposeType()) {
			words = words.replace("<owntype>", "owner");
		}else if(com.italia.marxmind.bris.enm.Purpose.LAND_ASSESSTMENT.getId()==clr.getPurposeType()) {
			words = words.replace("<owntype>", "claimant");
		}else if(com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()==clr.getPurposeType()) {
			words = words.replace("<owntype>", "owner");
			REPORT_NAME = TRIBAL_LAND_DOC;
		}
		
		
		
		words = words.replace("<landtype>", LandTypes.typeName(Integer.valueOf(note[0])));
		words = words.replace("<lotno>", note[1]);
		words = words.replace("<areasqrt>", note[2]);
		words = words.replace("<barangayaddress>", barangay);
		words = words.replace("<north>", note[3]);
		words = words.replace("<east>", note[4]);
		words = words.replace("<south>", note[5]);
		words = words.replace("<west>", note[6]);
		str.append(words);
		
		if(com.italia.marxmind.bris.enm.Purpose.TRIBAL_LAND_CERTIFICATION.getId()==clr.getPurposeType()) {
			words = Words.getTagName("land-permit-string-6");
		}else {
			words = Words.getTagName("land-permit-string-4");
		}
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
		
		
		try{
			param.put("PARAM_IPMR","HON. "+ ipmr.getFirstName().toUpperCase() + " " + ipmr.getLastName().toUpperCase());
			param.put("PARAM_IPMR_POSITION","Barangay "+ipmr.getPosition().getName());
			}catch(Exception e) {
			param.put("PARAM_IPMR","");
			param.put("PARAM_IPMR_POSITION","");
		}
		
		
	}else if(com.italia.marxmind.bris.enm.Purpose.TREE_OWNERSHIP.getId()==clr.getPurposeType()){
		
		REPORT_NAME = GENERIC_DOC;
		
		String words = Words.getTagName("tree-permit-string-1");
	    words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		String[] note = clr.getNotes().split("<:>");
		purpose =  Words.getTagName("tree-permit-string-2");
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose);
		reports.add(rpt);
		
		
		rpt = new ClearanceRpt();
		rpt.setF1("NAME OF TREE/s:");rpt.setF2(note[0]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("NO OF HILLS:");rpt.setF2(note[1]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("LOCATION:");rpt.setF2(note[2]);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("REASON:");rpt.setF2(note[3].toUpperCase());
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
					
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();
		
		
	}else if(com.italia.marxmind.bris.enm.Purpose.LOAN_REQUIREMENTS.getId()==clr.getPurposeType()){
		
		
		
		REPORT_NAME = GENERIC_DOC;
		
		String words = Words.getTagName("loan-string-1");
	    words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		purpose = Words.getTagName("loan-string-4") + " " + clr.getNotes().toUpperCase();
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose);
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		String heshe = taxpayer.getGender().equalsIgnoreCase("1")? "he" : "she";
		if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
			
			words = Words.getTagName("loan-string-6");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("loan-string-3"));
			reports.add(rpt);
		
		}else if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
			
			words = Words.getTagName("loan-string-5");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("loan-string-2"));
			reports.add(rpt);
			
		}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType()){
			
			words = Words.getTagName("loan-string-5");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("loan-string-2"));
			reports.add(rpt);
			
		}else if(DocTypes.RESIDENCY.getId()==clr.getDocumentType()){
			
			words = Words.getTagName("loan-string-5");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("loan-string-2"));
			reports.add(rpt);
			
		}else if(DocTypes.INCOME.getId()==clr.getDocumentType()){
			
			words = Words.getTagName("loan-string-5");
			words = words.replace("<heshe>", heshe);
			str.append(words);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("loan-string-2"));
			reports.add(rpt);
			
		}
		
		
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();
		
	}else if(com.italia.marxmind.bris.enm.Purpose.SOLO_PARENT.getId()==clr.getPurposeType()){
		
		REPORT_NAME = GENERIC_DOC;
		
		String words = Words.getTagName("solo-string-1");
		if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			words = words.replace("<docttype>", DocTypes.CLEARANCE.getName());
		}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			words = words.replace("<docttype>", DocTypes.CERTIFICATE.getName());
		}else {
			words = words.replace("<docttype>", DocTypes.typeName(clr.getDocumentType()));
		}
		str.append(words);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		purpose = words = Words.getTagName("solo-string-4");
		String heshe = taxpayer.getGender().equalsIgnoreCase("1")? "he" : "she";
		purpose = purpose.replace("<heshe>", heshe);
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose.toUpperCase());
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		//String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
		words = "\t"+clr.getNotes();
		str.append(words);
		words = Words.getTagName("solo-string-3");
		words = words.replace("<heshe>", heshe);
		str.append(words);
		
		words = Words.getTagName("issued-date-office-2");
		words = words.replace("<day>", DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		words = words.replace("<month>", DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		words = words.replace("<year>", clr.getIssuedDate().split("-")[0]);
		words = words.replace("<barangayaddress>", barangay);
		str.append(words);
		
		detail_2 = str.toString();
		
	}else{
		
		REPORT_NAME = GENERIC_DOC;
		word = new StringBuilder();
		word.append(Words.getTagName("other-string-1"));
		if(DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CLEARANCE.getName());
		}else if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()) {
			word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.CERTIFICATE.getName());
		}else {
			word.replace(word.indexOf("<docttype>"),word.indexOf("<docttype>")+10, DocTypes.typeName(clr.getDocumentType()));
		}
		str.append(word);
		
		supplyDetails(clr, reports, address, civilStatus, municipal);
		
		purpose = com.italia.marxmind.bris.enm.Purpose.typeName(clr.getPurposeType());
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("PURPOSE:");rpt.setF2(purpose);
		reports.add(rpt);
		
		detail_1 = str.toString();
		str = new StringBuilder();
		
		String heshe = clr.getTaxPayer().getGender().equalsIgnoreCase("1")? "he" : "she";
		
		word = new StringBuilder();
		if(DocTypes.CLEARANCE.getId()==clr.getDocumentType() || DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
			
			reports = checkSummonRemarks(clr.getTaxPayer(), reports, Purpose.AFP_TRAINING);
			
			word.append(Words.getTagName("other-string-2"));
			//word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			int index = reports.size();
			index -=1;
			if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
				word.replace(word.indexOf("has no derogatory"),word.indexOf("has no derogatory")+17, "has a pending case");
				word.replace(word.indexOf(" and a law abiding citizen in our locality."),word.indexOf(" and a law abiding citizen in our locality.")+43, ".");
			}
			str.append(word);
			
		}else if(DocTypes.CERTIFICATE.getId()==clr.getDocumentType() || DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType()){
			reports = checkSummonRemarks(clr.getTaxPayer(), reports, Purpose.AFP_TRAINING);
			
			word.append(Words.getTagName("other-string-2"));
			//word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			int index = reports.size();
			index -=1;
			if(!"NO DEROGATORY RECORD ON FILE".equalsIgnoreCase(reports.get(index).getF2())) {
				word.replace(word.indexOf("has no derogatory"),word.indexOf("has no derogatory")+17, "has a pending case");
				word.replace(word.indexOf(" and a law abiding citizen in our locality."),word.indexOf(" and a law abiding citizen in our locality.")+43, ".");
			}
			str.append(word);
			
		}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
			
			word.append(Words.getTagName("other-string-3"));
			word.replace(word.indexOf("<heshe>"),word.indexOf("<heshe>")+7, heshe);
			str.append(word);
			
			rpt = new ClearanceRpt();
			rpt.setF1("REMARKS:");rpt.setF2(Words.getTagName("other-string-5"));
			reports.add(rpt);
			
		}
		
		
		word = new StringBuilder();
		word.append(Words.getTagName("issued-date-office-2"));
		word.replace(word.indexOf("<day>"),word.indexOf("<day>")+5, DateUtils.dayNaming(clr.getIssuedDate().split("-")[2]));
		word.replace(word.indexOf("<month>"),word.indexOf("<month>")+7, DateUtils.getMonthName(Integer.valueOf(clr.getIssuedDate().split("-")[1])));
		word.replace(word.indexOf("<year>"),word.indexOf("<year>")+6, clr.getIssuedDate().split("-")[0]);
		word.replace(word.indexOf("<barangayaddress>"),word.indexOf("<barangayaddress>")+17, barangay);
		str.append(word);
		
		detail_2 = str.toString();
		
	}	
		
		
		
				//do not move this code this code use for custom title
				if(DocTypes.CERTIFICATE_OPEN_TITLE.getId()==clr.getDocumentType() || 
						DocTypes.CLEARANCE_OPEN_TITLE.getId()==clr.getDocumentType()){
					REPORT_NAME = DOC_OPEN_TITLE_REPORT_NAME;
					param.put("PARAM_DOC_OPEN_TITLE", clr.getCustomTitle().toUpperCase());
					
				}else if(DocTypes.LOW_INCOME.getId()==clr.getDocumentType()) {
					REPORT_NAME = DOC_OPEN_TITLE_REPORT_NAME;
					param.put("PARAM_DOC_OPEN_TITLE", "BARANGAY CERTIFICATE OF LOW INCOME");
					
				}	
				
				ReportCompiler compiler = new ReportCompiler();
				String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
				
				JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
				//UserDtls user = Login.getUserLogin().getUserDtls();
					
					
					param.put("PARAM_ISSUED_DATE", DateUtils.getCurrentDateMMMMDDYYYY());
					param.put("PARAM_REQUESTOR_NAME", clr.getTaxPayer().getFirstname().toUpperCase() + " " + clr.getTaxPayer().getMiddlename().substring(0,1).toUpperCase() + ". " + clr.getTaxPayer().getLastname().toUpperCase());
					//param.put("PARAM_BARCODE", clr.getTaxPayer().getFullname().toUpperCase());
					param.put("PARAM_CEDULA_NO", clr.getCedulaNumber());
					param.put("PARAM_ISSUED_LOCATION",  MUNICIPALITY + ", " + PROVINCE); // "Lake Sebu, South Cotabato");
					
					
					
					//String signerOD="";
					//String signerTitle="";
					StringBuilder signerTitle = new StringBuilder();
					StringBuilder signerOD = new StringBuilder();
					try {
						signerOD.append(employee.getFirstName() + " " + employee.getMiddleName().substring(0,1) + ". " + employee.getLastName());
					
					if(employee.getPosition().getId()==1){
						signerTitle.append(clr.getEmployee().getPosition().getName());
					}else if(employee.getPosition().getId()==2){
						signerTitle.append("Barangay "+clr.getEmployee().getPosition().getName() +"\nOfficer of the day");
						signerTitle.append("\nOn behalf of\n\n\n");
						signerTitle.append(kapitan.getPosition().getName());
						signerOD.append("\n\n\n\n"+kapNameSign.toUpperCase());
					}else{
						signerTitle.append("Barangay "+clr.getEmployee().getPosition().getName());
						signerTitle.append("\nOn behalf of\n\n\n\n");
						signerTitle.append(kapitan.getPosition().getName());
						signerOD.append("\n\n\n\n"+kapNameSign.toUpperCase());
					}
					}catch(Exception e) {
						signerOD.append(kapNameSign);
						signerTitle.append(kapitan.getPosition().getName());
					}
					
					param.put("PARAM_OFFICER_DAY", signerOD.toString().toUpperCase());
					param.put("PARAM_OFFICIAL_TITLE",signerTitle.toString());
					
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
					
					detail_1 =detail_1.replace("requested a INDIGENT CERTIFICATION", "requested an INDIGENT CERTIFICATION");
					
					param.put("PARAM_CLEARANCE_DETAILS", detail_1);
					param.put("PARAM_CLEARANCE_DETAILS2", detail_2);
					param.put("PARAM_PURPOSE", purpose);
					
					//Displayed photo
					boolean isOkToShowPicture = true;//com.italia.marxmind.bris.enm.Purpose.BURIAL.getId()==clr.getPurposeType() || 
					if(DocTypes.DEATH_CERT.getId()==clr.getDocumentType() || 
								DocTypes.LATE_DEATH_CERT.getId()==clr.getDocumentType() || 
									DocTypes.COE.getId()==clr.getDocumentType()){
						isOkToShowPicture = false;
					}
					
					if(isOkToShowPicture){				
						if(clr.getPhotoId()!=null && !clr.getPhotoId().isEmpty()){
							String picture = DocumentPrinting.copyPhoto(clr.getPhotoId()).replace("\\", "/");
							System.out.println("Images: " + picture);
							//InputStream img = this.getClass().getResourceAsStream("/"+clr.getPhotoId()+".jpg");
							File file = new File(picture);
							if(file.exists()){
								try{
								FileInputStream st = new FileInputStream(file);
								param.put("PARAM_PICTURE", st);
								}catch(Exception e){}
							}else{
								picture = DocumentPrinting.copyPhoto(taxpayer.getPhotoid()).replace("\\", "/");
								file = new File(picture);
								try{
									FileInputStream st = new FileInputStream(file);
									param.put("PARAM_PICTURE", st);
								}catch(Exception e){}
							}
						}
					
					}
					
					param.put("PARAM_PROVINCE", Words.getTagName("province-line").replace("<province>", PROVINCE));
					param.put("PARAM_MUNICIPALITY", Words.getTagName("municipality-line").replace("<municipality>", MUNICIPALITY));
					param.put("PARAM_BARANGAY", Words.getTagName("barangay-line").replace("<barangay>", BARANGAY.toUpperCase()));
					
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
						
					}else if(DocTypes.CLEARANCE.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "clearance.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
						
					}else if(DocTypes.DEATH_CERT.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "deathcert.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.LATE_DEATH_CERT.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "latedeathcert.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
							
					}else if(DocTypes.INDIGENT.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "indigency.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.LATE_BIRTH_REG.getId()==clr.getDocumentType()){
						
						//certificate
						String certificate = path + "latebirth.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
						
					}else if(DocTypes.LIVE_BIRTH_REG.getId()==clr.getDocumentType()){
						
						//certificate
						String certificate = path + "livebirth.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.BARANGAY_BUSINESS_PERMIT.getId()==clr.getDocumentType()){
						
						//certificate
						String certificate = path + "businesspermit.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.COE.getId()==clr.getDocumentType()){
						
						//certificate
						String certificate = path + "coe.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.AUTHORIZATION_LETTER.getId()==clr.getDocumentType()){
						
						//authorization
						String authorization = path + "authorization.png";
						try{File file1 = new File(authorization);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.RESIDENCY.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "residency.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
					}else if(DocTypes.INCOME.getId()==clr.getDocumentType()){
						//certificate
						String certificate = path + "income.png";
						try{File file1 = new File(certificate);
						FileInputStream off1 = new FileInputStream(file1);
						param.put("PARAM_CERTIFICATE", off1);
						}catch(Exception e){}
						
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
			//File pdf = new File(clr.getTaxPayer().getCardno()+".png");
			File folder = new File(path + "barcode" + File.separator);
			folder.mkdir();
			File pdf = new File(path + "barcode" + File.separator + clr.getTaxPayer().getCardno()+".png");
			
			BarcodeImageHandler.savePNG(barcode, pdf);
			barPdf = new FileInputStream(pdf);
			param.put("PARAM_BARCODE", barPdf);
		}catch(Exception e){e.printStackTrace();}
		
		FileInputStream qrPdf = null;
		try{
			
			
			File folder = new File(path + "qrcode" + File.separator);
			folder.mkdir();
			String content = "Name: " + clr.getTaxPayer().getLastname() + ", " + clr.getTaxPayer().getFirstname() + " " + clr.getTaxPayer().getMiddlename() +"\n";
			content += "Address: "+ address +"\n";
			content += "Purpose: " + purpose + "\n";
			content += "Printed: "+ DateUtils.convertDateToMonthDayYear(clr.getIssuedDate()) +"\n";
			content += "Please report for violation of this document.\n";
			content += "Provider: MARXMIND I.T. SOLUTIONS";
			File pdf = QRCode.createQRCode(content, 200, 200, path + "qrcode" + File.separator, clr.getTaxPayer().getCardno());
			qrPdf = new FileInputStream(pdf);
			param.put("PARAM_QRCODE", qrPdf);
		}catch(Exception e){e.printStackTrace();}
		
		if(clr.getIsPayable()==0){
			param.put("PARAM_PAID", "Amount Paid: FREE");
			param.put("PARAM_OR_NUMBER", "OR NO: N/A");
		}else{
			param.put("PARAM_PAID", "Amount Paid: Php "+Currency.formatAmount(clr.getAmountPaid()));
			param.put("PARAM_OR_NUMBER", "OR NO: "+clr.getOrNumber());
		}
		
		
		try{param.put("PARAM_DOCUMENT_NOTE", DocumentPrinting.documentNote(clr));}catch(NullPointerException e) {}
		try{param.put("PARAM_TAGLINE", Words.getTagName("tagline"));}catch(NullPointerException e) {}
		try{param.put("PARAM_REQUESTOR_PRINTED_NAME", requestorPrintedName.toUpperCase());}catch(NullPointerException e) {}
		
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
	
	private static void supplyDetails(Clearance clr, List<ClearanceRpt> reports, String address, String civilStatus, String municipal) {
		Customer taxpayer = clr.getTaxPayer();
		String requestor = taxpayer.getLastname() +", " + taxpayer.getFirstname() + " " + taxpayer.getMiddlename();
		String gender = taxpayer.getGender().equalsIgnoreCase("1")? "MALE" : "FEMALE";
		
		ClearanceRpt rpt = new ClearanceRpt();
		rpt.setF1("NAME:");rpt.setF2(requestor.toUpperCase());
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("GENDER:");rpt.setF2(gender);
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("ADDRESS:");rpt.setF2(address.toUpperCase());
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("BIRTHDAY:");rpt.setF2(DateUtils.convertDateToMonthDayYear(taxpayer.getBirthdate()).toUpperCase());
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("CIVIL STATUS:");rpt.setF2(civilStatus.toUpperCase());
		reports.add(rpt);
		
		rpt = new ClearanceRpt();
		rpt.setF1("CITIZENSHIP:");rpt.setF2(Words.getTagName("citizenship"));
		reports.add(rpt);
		
		
		String[] ced = null;
		if(clr.getCedulaNumber()!=null && !clr.getCedulaNumber().isEmpty()){
			ced = clr.getCedulaNumber().split("<:>");
			rpt = new ClearanceRpt();
			rpt.setF1("CTC NO:");rpt.setF2(ced[0]);
			reports.add(rpt);
			
			rpt = new ClearanceRpt();
			rpt.setF1("DATE ISSUED:");rpt.setF2(DateUtils.convertDateToMonthDayYear(ced[1]).toUpperCase());
			reports.add(rpt);
			
			rpt = new ClearanceRpt();
			rpt.setF1("LOCATION:");rpt.setF2(municipal.toUpperCase());
			reports.add(rpt);
		}
	}
	
	private static String civilStatus(int age, String civilStatus) {
		if(age<=17){
			if(age==0){
				civilStatus = "infant";
			}else if(age>=1 && age<=5){
				civilStatus = "baby";
			}else if(age>=6 && age<=9){
				civilStatus = "kid";
			}else if(age>=10 && age<=17){
				civilStatus = "teenager";
			}
		}
		return civilStatus;
	}
	
	private static List<ClearanceRpt> checkSummonRemarks(Customer cuz,List<ClearanceRpt> reports, Purpose purpose){
		
		String remarks="NO DEROGATORY RECORD ON FILE";
		String ison = Words.getTagName("activate-case-note");
		if("on".equalsIgnoreCase(ison)) {
			String sql = " AND ciz.caseisactive=1 AND (ciz.casestatus!=? AND ciz.casestatus!=?)";
			String[] params = new String[2];
			params[0] = CaseStatus.SETTLED.getId()+"";
			params[1] = CaseStatus.CANCELLED.getId()+"";
			
			String fullName = cuz.getFullname();
			sql += " AND ( ciz.respondents like '%"+fullName+"%' ";
			 sql += " OR ciz.respondents like '%"+fullName+",' ";
			 sql += " OR ciz.respondents like ',"+fullName+"%' )";
			
			int caseCount = 1;
			String czName = "";
			boolean hasCase = false;
			for(Cases cz : Cases.retrieve(sql, params)){
				czName = CaseKind.typeName(cz.getKind());
				if(caseCount==1){
					if(CaseKind.OTHERS.getName().equalsIgnoreCase(czName)) {
						remarks = cz.getOtherCaseName();
					}else {
						remarks = czName;
					}
					
					if(CaseStatus.MOVED_IN_HIGHER_COURT.getId()==cz.getStatus()) {
						remarks += "(case has been moved in court)";
					}
					
				}else {
					if(CaseKind.OTHERS.getName().equalsIgnoreCase(czName)) {
						remarks +=", " + cz.getOtherCaseName();
					}else {
						remarks +=", " + czName;
					}
					
					if(CaseStatus.MOVED_IN_HIGHER_COURT.getId()==cz.getStatus()) {
						remarks += "(case has been moved in court)";
					}
				}
				caseCount++;
				hasCase= true;
			}
			if(hasCase) {
				remarks = remarks + " CASE REPORTED";
			}
		}
			ClearanceRpt rpt = new ClearanceRpt();
			String word = "";
			if(Purpose.OTHER_LEGAL_MATTERS==purpose) {
				word = Words.getTagName("activate-other-legal");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}else if(Purpose.MOTORCYCLE_LOAN_REQUIREMENT==purpose) {
				word = Words.getTagName("activate-motorcycle-loan");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}else if(Purpose.PAG_IBIG_LOAN_REQUIREMENT==purpose) {
				word = Words.getTagName("activate-pag-ibig-loan");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}else if(Purpose.SSS_LOAN_REQUIREMENT==purpose) {
				word = Words.getTagName("activate-sss-loan");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}	
			}else if(Purpose.CAR_LOAN_REQUIREMENT==purpose) {
				word = Words.getTagName("activate-motorcycle-loan");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}else if(Purpose.MULTIPURPOSE==purpose) {
				word = Words.getTagName("activate-multipurpose");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}else {
				word = Words.getTagName("activate-all");
				if("on".equalsIgnoreCase(word)) {
					rpt.setF1("REMARKS:");rpt.setF2(remarks);
					reports.add(rpt);
				}
			}
			
		
		return reports;
	}
	
	
	
}
