package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.KPForms;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.CasesRpt;
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
 * @since 09/06/2018
 * @version 1.0
 *
 */
public class CasesPrintingV6 {

	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String KP_FORM0 = DocumentFormatter.getTagName("v6_kpform0");
	private static final String KP_FORM7 = DocumentFormatter.getTagName("v6_kpform7");
	private static final String KP_FORM8 = DocumentFormatter.getTagName("v6_kpform8");
	private static final String KP_FORM9 = DocumentFormatter.getTagName("v6_kpform9");
	private static final String KP_FORM10 = DocumentFormatter.getTagName("v6_kpform10");
	private static final String KP_FORM12 = DocumentFormatter.getTagName("v6_kpform12");
	private static final String KP_FORM13 = DocumentFormatter.getTagName("v6_kpform13");
	private static final String KP_FORM14 = DocumentFormatter.getTagName("v6_kpform14");
	private static final String KP_FORM15 = DocumentFormatter.getTagName("v6_kpform15");
	private static final String KP_FORM16 = DocumentFormatter.getTagName("v6_kpform16");
	private static final String KP_FORM17 = DocumentFormatter.getTagName("v6_kpform17");
	private static final String KP_FORM18 = DocumentFormatter.getTagName("v6_kpform18");
	private static final String KP_FORM19 = DocumentFormatter.getTagName("v6_kpform19");
	private static final String KP_FORM20 = DocumentFormatter.getTagName("v6_kpform20");
	private static final String KP_FORM20_A = DocumentFormatter.getTagName("v6_kpform20-A");
	private static final String KP_FORM20_B = DocumentFormatter.getTagName("v6_kpform20-B");
	private static final String KP_FORM21 = DocumentFormatter.getTagName("v6_kpform21");
	private static final String KP_FORM22 = DocumentFormatter.getTagName("v6_kpform22");
	private static final String KP_FORM23 = DocumentFormatter.getTagName("v6_kpform23");
	private static final String KP_FORM24 = DocumentFormatter.getTagName("v6_kpform24");
	private static final String KP_FORM25 = DocumentFormatter.getTagName("v6_kpform25");
	private static final String INVITATION_FORM = DocumentFormatter.getTagName("v6_invitationform");
	
	public static Map<Integer, Object> printDocumentV6(CaseFilling fil, int KPFormId) {
		Map<Integer, Object> mapObject = Collections.synchronizedMap(new HashMap<Integer, Object>());
		List<KPFormsRpt> reports = Collections.synchronizedList(new ArrayList<KPFormsRpt>());
		List<CasesRpt> reportsCase = Collections.synchronizedList(new ArrayList<CasesRpt>());
		reports.add(new KPFormsRpt());
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		HashMap param = new HashMap();		
		
		//Employee kapitan = Employee.retrievePosition(Positions.CAPTAIN.getId());//get Captain of the Barangay
		Employee kapitan = Employee.employeeNamePosition(fil.getChaiman().getId());
		String kapNameSign = kapitan.getFirstName() + " "+ kapitan.getMiddleName().substring(0, 1) +". " + kapitan.getLastName(); 
		
		if(Positions.CAPTAIN.getId()==kapitan.getPosition().getId()) {
			param.put("PARAM_OFFICIAL","HON. "+ kapNameSign.toUpperCase());
			param.put("LUPON_CHAIRMAN", "Punong Barangay");
		}else if(Positions.BARANGAY_LUPON_CHAIRMAN.getId()==kapitan.getPosition().getId()) {
			param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
			param.put("LUPON_CHAIRMAN", "Lupon Chairman");
		}else {
			param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
			param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
		}
		
		
		
		String gov = Words.getTagName("province-line");
		param.put("PARAM_PROVINCE", gov.replace("<province>", PROVINCE));
		gov = Words.getTagName("municipality-line");
		param.put("PARAM_MUNICIPALITY", gov.replace("<municipality>", MUNICIPALITY));
		gov = Words.getTagName("barangay-line");
		param.put("PARAM_BARANGAY", gov.replace("<barangay>", BARANGAY.toUpperCase()));
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		String REPORT_NAME = KP_FORM7;
		
		if(KPForms.KP_FORM0.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM0;
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
				
				File folder = new File(path + "barcode" + File.separator);
				folder.mkdir();
				
				File pdf = new File(path + "barcode" + File.separator +fil.getCases().getCaseNo()+".png");
				
				BarcodeImageHandler.savePNG(barcode, pdf);
				barPdf = new FileInputStream(pdf);
				barPdf1 = new FileInputStream(pdf);
			
			}catch(Exception e){e.printStackTrace();}
			
			gov = Words.getTagName("province-line");
			String prov = gov.replace("<province>", PROVINCE);
			gov = Words.getTagName("municipality-line");
			String mun = gov.replace("<municipality>", MUNICIPALITY);
			gov = Words.getTagName("barangay-line");
			String bar = gov.replace("<barangay>", BARANGAY.toUpperCase());
			
			reportsCase = Collections.synchronizedList(new ArrayList<CasesRpt>());
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
			
			reportsCase.add(rpt);
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
			
			rpt.setF23("Barangay Copy");
			
			reportsCase.add(rpt);
			
			param.put("LUPON_CHAIRMAN", "Lupon Chairman");
		
		}else if(KPForms.KP_FORM7.getId()==KPFormId) {
		
			REPORT_NAME = KP_FORM7;
			param.put("PARAM_TITLE", Words.getTagName("kp-form7-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form7-2");
			if(fil.getCases().getNarratives()!=null && !fil.getCases().getNarratives().isEmpty()) {
				content += fil.getCases().getNarratives();
			}
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form7-3");
			if(fil.getCases().getSolutions()!=null && !fil.getCases().getSolutions().isEmpty()) {
				content += fil.getCases().getSolutions();
			}
			param.put("PARAM_CONTENT2", content);
			
			content = Words.getTagName("kp-form7-4");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			
			param.put("PARAM_CREATED", content);
			content = Words.getTagName("kp-form7-5");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
		}else if(KPForms.KP_FORM8.getId()==KPFormId) {
		
			REPORT_NAME = KP_FORM8;
			param.put("PARAM_TITLE", Words.getTagName("kp-form8-1"));
			
			String complianant = fil.getCases().getComplainants().toUpperCase() + "\n" + fil.getCases().getComAddress().toUpperCase();
			param.put("PARAM_COMPLAINANT", complianant);
			
			String content = Words.getTagName("kp-form8-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form8-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form8-4");
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
			param.put("PARAM_COMPLAINANT2", fil.getCases().getComplainants().toUpperCase());
		}else if(KPForms.KP_FORM9.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM9;
			String title = "1st";
			String type =  fil.getCases().getType()==1? "INVITATION" : "SUMMON";
			if(fil.getCount()==1){
				title = "1st " + type;
			}else if(fil.getCount()==2){
				title = "2nd " + type;
			}else if(fil.getCount()==3){
				title = "3rd " + type;
			}
			
			param.put("PARAM_TITLE", title);
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			
			String content = "";
			
			content = Words.getTagName("kp-form9-1");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			
			content += Words.getTagName("kp-form9-2");
			content += Words.getTagName("kp-form9-3");
			content += Words.getTagName("kp-form9-4");
			content = content.replace("<date>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			
			param.put("PARAM_CONTENT", content);
			
		}else if(KPForms.KP_FORM10.getId()==KPFormId) {
		
			REPORT_NAME = KP_FORM10;
			param.put("PARAM_TITLE", Words.getTagName("kp-form10-1"));
			
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form10-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form10-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form10-4");
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
		}else if(KPForms.KP_FORM12.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM12;
			param.put("PARAM_TITLE", Words.getTagName("kp-form12-1"));
			
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form12-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form12-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form12-4");
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
			param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			
		}else if(KPForms.KP_FORM13.getId()==KPFormId) {
		
			REPORT_NAME = KP_FORM13;
			param.put("PARAM_TITLE", Words.getTagName("kp-form13-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			if(fil.getCases().getWitnesses()!=null && !fil.getCases().getWitnesses().isEmpty()) {
			String witness = fil.getCases().getWitnesses();
			witness = witness.toUpperCase().replaceAll("\\r\\n|\\r|\\n", " ") + "\n" + fil.getCases().getWitnessAddress(); 
			try{param.put("PARAM_WITNESS", witness);}catch(NullPointerException e) {}
			}
			
			String content = Words.getTagName("kp-form13-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			
			content = Words.getTagName("kp-form13-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}else if(KPForms.KP_FORM14.getId()==KPFormId) {
		
			REPORT_NAME = KP_FORM14;
			param.put("PARAM_TITLE", Words.getTagName("kp-form14-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form14-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form14-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form14-4");
			param.put("PARAM_CONTENT2", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}else if(KPForms.KP_FORM15.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM15;
			param.put("PARAM_TITLE", Words.getTagName("kp-form15-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form15-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form15-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form15-4");
			param.put("PARAM_CONTENT2", content);
			
			boolean ifCapt = true;
			param.put("LUPON_CHAIRMAN", "Punong Barangay*");
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman*");
				ifCapt = false;
			}
			
			Employee secretary = Employee.employeeNamePosition(fil.getSecretary().getId());
			String secNameSign = secretary.getFirstName() + " "+ secretary.getMiddleName().substring(0, 1) +". " + secretary.getLastName(); 
			param.put("PARAM_SECRETARY", secNameSign);
			
			if(ifCapt) {
					param.put("LUPON_ATTESTED_POSTION", "Lupon Secretary**");
			}else {
				if(Positions.CAPTAIN.getId()==secretary.getPosition().getId()) {
					param.put("LUPON_ATTESTED_POSTION", "Punong Barangay**");
				}else {
					param.put("LUPON_ATTESTED_POSTION", "Lupon Secretary**");
				}
			}
		
		}else if(KPForms.KP_FORM16.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM16;
			param.put("PARAM_TITLE", Words.getTagName("kp-form16-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form16-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form16-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form16-4");
			param.put("PARAM_CONTENT2", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}else if(KPForms.KP_FORM17.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM17;
			param.put("PARAM_TITLE", Words.getTagName("kp-form17-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form17-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form17-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form17-4");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CONTENT2", content);
			
			content = Words.getTagName("kp-form17-5");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
			content = Words.getTagName("kp-form17-6");
			param.put("PARAM_CONTENT3", content);
			
			Employee pangkat = Employee.employeeNamePosition(fil.getSecretary().getId());
			String pangaktNameSign = pangkat.getFirstName() + " "+ pangkat.getLastName(); 
			try {
				pangaktNameSign = pangkat.getFirstName() + " "+ pangkat.getMiddleName().substring(0, 1) +". " + pangkat.getLastName(); 
			}catch(Exception e) {}
			
			param.put("PARAM_PANGKAT_MEMBER", pangaktNameSign);
						
			if(Positions.CAPTAIN.getId()==pangkat.getPosition().getId()) {
				param.put("LUPON_CHAIRMAN", "Punong Barangay");
			}else if(Positions.BARANGAY_LUPON_CHAIRMAN.getId()==pangkat.getPosition().getId()) {
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}else {
				param.put("LUPON_CHAIRMAN", "Pangkat Member");
			}
			
		}else if(KPForms.KP_FORM18.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM18;
			param.put("PARAM_TITLE", Words.getTagName("kp-form18-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form18-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			
			try {
			String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM9.getId() + " ORDER BY fil.filid DESC limit 1";
			String[] params = new String[1];
			params[0] = fil.getCases().getId()+"";
			List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
			content = content.replace("<lastmonthdayyear>",DateUtils.convertDateToMonthDayYear(filz.get(0).getSettlementDate()));
			}catch(Exception e) {
				content = content.replace("<lastmonthdayyear>","_________________(Date)");
			}
			
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form18-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form18-4");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
		}else if(KPForms.KP_FORM19.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM19;
			param.put("PARAM_TITLE", Words.getTagName("kp-form19-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form19-2");

			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			
			try {
			String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM9.getId() + " ORDER BY fil.filid DESC limit 1";
			String[] params = new String[1];
			params[0] = fil.getCases().getId()+"";
			List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
			content = content.replace("<lastmonthdayyear>",DateUtils.convertDateToMonthDayYear(filz.get(0).getSettlementDate()));
			}catch(Exception e) {
				content = content.replace("<lastmonthdayyear>","_________________(Date)");
			}
			
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form19-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form19-4");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
		}else if(KPForms.KP_FORM20.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM20;
			param.put("PARAM_TITLE", Words.getTagName("kp-form20-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form20-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form20-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			
			Employee luponSec = null;
			try {
			luponSec = Employee.employeeNamePosition(fil.getSecretary().getId());
			String luponSecNameSign = luponSec.getFirstName() + " "+ luponSec.getMiddleName().substring(0, 1) +". " + luponSec.getLastName(); 
			param.put("PARAM_PROCCESSBY", luponSecNameSign);
			if(Positions.SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Barangay Secretary");	
			}else if(Positions.BARANGAY_LUPON_SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Lupon Secretary");
			}else {
				param.put("PARAM_PROCESSPOS", luponSec.getPosition().getName());
			}
			
			}catch(Exception e) {luponSec = null;}
			
			
		}else if(KPForms.KP_FORM20A.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM20_A;
			param.put("PARAM_TITLE", Words.getTagName("kp-form20-A-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form20-A-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form20-A-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			
			Employee luponSec = null;
			try {
			luponSec = Employee.employeeNamePosition(fil.getSecretary().getId());
			String luponSecNameSign = luponSec.getFirstName() + " "+ luponSec.getMiddleName().substring(0, 1) +". " + luponSec.getLastName(); 
			param.put("PARAM_PROCCESSBY", luponSecNameSign);

			if(Positions.SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Barangay Secretary");	
			}else if(Positions.BARANGAY_LUPON_SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Lupon Secretary");
			}else if(Positions.BARANGAY_PANGKAT_SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Pangkat Secretary");	
			}else {
				param.put("PARAM_PROCESSPOS", luponSec.getPosition().getName());
			}
			
			}catch(Exception e) {luponSec = null;}
		
		}else if(KPForms.KP_FORM20B.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM20_B;
			param.put("PARAM_TITLE", Words.getTagName("kp-form20-B-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form20-B-2");
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form20-B-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			
			Employee luponSec = null;
			try {
			luponSec = Employee.employeeNamePosition(fil.getSecretary().getId());
			String luponSecNameSign = luponSec.getFirstName() + " "+ luponSec.getMiddleName().substring(0, 1) +". " + luponSec.getLastName(); 
			param.put("PARAM_PROCCESSBY", luponSecNameSign);
			
			if(Positions.SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Barangay Secretary");	
			}else if(Positions.BARANGAY_LUPON_SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Lupon Secretary");
			}else if(Positions.BARANGAY_PANGKAT_SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Pangkat Secretary");	
			}else {
				param.put("PARAM_PROCESSPOS", luponSec.getPosition().getName());
			}
			
			}catch(Exception e) {luponSec = null;}	
			
		}else if(KPForms.KP_FORM21.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM21;
			param.put("PARAM_TITLE", Words.getTagName("kp-form21-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form21-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<complainants>", fil.getCases().getComplainants().toUpperCase().replace("\n", " "));
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form21-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form21-4");
			param.put("PARAM_CONTENT2", content);
			
			
			Employee luponSec = null;
			try {
			luponSec = Employee.employeeNamePosition(fil.getSecretary().getId());
			String luponSecNameSign = luponSec.getFirstName() + " "+ luponSec.getMiddleName().substring(0, 1) +". " + luponSec.getLastName(); 
			param.put("PARAM_PROCCESSBY", luponSecNameSign);
			if(Positions.SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Barangay "+luponSec.getPosition().getName());
			}else {
				param.put("PARAM_PROCESSPOS", luponSec.getPosition().getName());
			}
			}catch(Exception e) {luponSec = null;}
			
			
		}else if(KPForms.KP_FORM22.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM22;
			param.put("PARAM_TITLE", Words.getTagName("kp-form22-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String respondents = fil.getCases().getRespondents().replaceAll("\\r\\n|\\r|\\n", " ");
			
			String content = Words.getTagName("kp-form22-2");
			content = content.replace("<respondents>", respondents.toUpperCase());
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form22-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form22-4");
			param.put("PARAM_CONTENT2", content);
			
			Employee luponSec = null;
			try {
			luponSec = Employee.employeeNamePosition(fil.getSecretary().getId());
			String luponSecNameSign = luponSec.getFirstName() + " "+ luponSec.getMiddleName().substring(0, 1) +". " + luponSec.getLastName(); 
			param.put("PARAM_PROCCESSBY", luponSecNameSign);
			if(Positions.SECRETARY.getId()==luponSec.getPosition().getId()) {
				param.put("PARAM_PROCESSPOS", "Barangay "+luponSec.getPosition().getName());
			}else {
				param.put("PARAM_PROCESSPOS", luponSec.getPosition().getName());
			}
			}catch(Exception e) {luponSec = null;}
			
		}else if(KPForms.KP_FORM23.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM23;
			param.put("PARAM_TITLE", Words.getTagName("kp-form23-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form23-2");
			
			try {
				String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM16.getId() + " ORDER BY fil.filid DESC limit 1";
				String[] params = new String[1];
				params[0] = fil.getCases().getId()+"";
				List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
				content = content.replace("<monthdayyear>",DateUtils.convertDateToMonthDayYear(filz.get(0).getSettlementDate()));
				}catch(Exception e) {
					content = content.replace("<monthdayyear>","_________________(Date)");
				}
			
			
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form23-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
		}else if(KPForms.KP_FORM24A.getId()==KPFormId || KPForms.KP_FORM24B.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM24;
			param.put("PARAM_TITLE", Words.getTagName("kp-form24-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replace("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form24-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			if(KPForms.KP_FORM24A.getId()==KPFormId) {
				content = content.replace("<filedperson>",fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			}else if(KPForms.KP_FORM24B.getId()==KPFormId) {
				content = content.replace("<filedperson>",fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			}
			param.put("PARAM_CONTENT", content);
			
			content = Words.getTagName("kp-form24-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form24-4");
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
			
		}else if(KPForms.KP_FORM25.getId()==KPFormId) {
			
			REPORT_NAME = KP_FORM25;
			param.put("PARAM_TITLE", Words.getTagName("kp-form25-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replace("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			String content = Words.getTagName("kp-form25-2");
			
			try {
				String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM16.getId() + " ORDER BY fil.filid DESC limit 1";
				String[] params = new String[1];
				params[0] = fil.getCases().getId()+"";
				List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
				content = content.replace("<monthdayyear>",DateUtils.convertDateToMonthDayYear(filz.get(0).getSettlementDate()));
				}catch(Exception e) {
					content = content.replace("<monthdayyear>","_________________(Date)");
				}
			
			param.put("PARAM_CONTENT", content);
			content = Words.getTagName("kp-form25-3");
			param.put("PARAM_CONTENT2", content);
			
			content = Words.getTagName("kp-form25-4");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			content = Words.getTagName("kp-form25-5");
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_RECEIVED", content);
			
			
		}else if(KPForms.INVITATION_FORM_WITNESS.getId()==KPFormId) {
			
			REPORT_NAME = INVITATION_FORM;
			param.put("PARAM_TITLE", Words.getTagName("invitation-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			if(fil.getCases().getWitnesses()!=null && !fil.getCases().getWitnesses().isEmpty()) {
			String witness = fil.getCases().getWitnesses();
			witness = witness.toUpperCase().replaceAll("\\r\\n|\\r|\\n", " ") + "\n" + fil.getCases().getWitnessAddress(); 
			try{param.put("PARAM_WITNESS", witness);}catch(NullPointerException e) {}
			}
			
			String content = Words.getTagName("invitation-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			
			content = Words.getTagName("invitation-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}else if(KPForms.INVITATION_FORM_COMPLAINANT.getId()==KPFormId) {
			
			REPORT_NAME = INVITATION_FORM;
			param.put("PARAM_TITLE", Words.getTagName("invitation-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			if(fil.getCases().getComplainants()!=null && !fil.getCases().getComplainants().isEmpty()) {
			String complainants = fil.getCases().getComplainants();
			complainants = complainants.toUpperCase().replaceAll("\\r\\n|\\r|\\n", " ") + "\n" + fil.getCases().getComAddress().toUpperCase(); 
			try{param.put("PARAM_WITNESS", complainants);}catch(NullPointerException e) {}
			}
			
			String content = Words.getTagName("invitation-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			
			content = Words.getTagName("invitation-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}else if(KPForms.INVITATION_FORM_RESPONDENT.getId()==KPFormId) {
			
			REPORT_NAME = INVITATION_FORM;
			param.put("PARAM_TITLE", Words.getTagName("invitation-1"));
			param.put("PARAM_CASENO", fil.getCases().getCaseNo());
			if(CaseKind.OTHERS.getId()==fil.getCases().getKind()){
				param.put("PARAM_KIND_CASE", fil.getCases().getOtherCaseName());
			}else{	
				param.put("PARAM_KIND_CASE", CaseKind.typeName(fil.getCases().getKind()));
			}
			param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
			param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase().replaceAll("\\r\\n|\\r|\\n", " "));
			param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
			
			if(fil.getCases().getRespondents()!=null && !fil.getCases().getRespondents().isEmpty()) {
			String respondents= fil.getCases().getRespondents();
			respondents = respondents.toUpperCase().replaceAll("\\r\\n|\\r|\\n", " ") + "\n" + fil.getCases().getResAddress().toUpperCase(); 
			try{param.put("PARAM_WITNESS", respondents);}catch(NullPointerException e) {}
			}
			
			String content = Words.getTagName("invitation-2");
			content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
			content = content.replace("<time>", fil.getSettlementTime());
			param.put("PARAM_CONTENT", content);
			
			
			content = Words.getTagName("invitation-3");
			content = content.replace("<day>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
			content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
			content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
			param.put("PARAM_CREATED", content);
			
			if(Positions.CAPTAIN.getId()!=kapitan.getPosition().getId()) {
				param.put("PARAM_OFFICIAL", kapNameSign.toUpperCase());
				param.put("LUPON_CHAIRMAN", "Pangkat Chairman");
			}
			
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		
		if(KPForms.KP_FORM0.getId()==KPFormId) {
			beanColl = new JRBeanCollectionDataSource(reportsCase);
		}
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
