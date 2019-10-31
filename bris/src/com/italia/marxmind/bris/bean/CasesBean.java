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
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.CaseFilling;
import com.italia.marxmind.bris.controller.Cases;
import com.italia.marxmind.bris.controller.CasesPrintingV6;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.DocumentPrinting;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Job;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.ORTransaction;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.controller.Words;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.KPForms;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.CasesRpt;
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
 * @since 03/06/2018
 * @version 1.0
 *
 */
@ManagedBean(name="caseBean", eager=true)
@ViewScoped
public class CasesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 654568774307371L;

	private String fillingDate;
	private String caseNumber;
	private Date settlementDate;
	private String settlementTime;
	private int attemps;
	
	private String complainants;
	private String respondents;
	private String compAddress;
	private String resAddress;
	
	
	private String narratives;
	private String orNumber;
	private double fees;
	private double messengerFees;
	
	private int statusId;
	private List status;
	
	private int typeId;
	private List types;
	
	private int kindId;
	private List kinds;
	
	private List<Cases> cases = Collections.synchronizedList(new ArrayList<Cases>());
	
	private Date dateFrom;
	private Date dateTo;
	private String searchParam;
	
	private Cases selectedCase;
	private CaseFilling selectedCaseFilling;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String CASES_REPORT_NAME = ReadXML.value(ReportTag.CASES_REPORT);
	private static final String CASE_NOTICE_REPORT_NAME = ReadXML.value(ReportTag.CASE_NOTICE);
	private static final String CASE_ENDORSEMENT_REPORT_NAME = ReadXML.value(ReportTag.CASE_ENDORSEMENT);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private boolean severalConfrontation;
	
	private boolean otherCase;
	private String otherCaseName;
	
	private List<Cases> caseLookup = Collections.synchronizedList(new ArrayList<Cases>());
	
	private int kpId;
	private List kps;
	
	private String witness;
	private String witnessAddress;
	private String solutions;
	
	private List chairmans;
	private long chairmanId;
	
	private List secretaries;
	private long secId;
	
	@PostConstruct
	public void init(){
		

		Login in = Login.getUserLogin();
		boolean isOk = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOk = true;
		}else{
			if(Features.isEnabled(Feature.BLOTTERS)){
				isOk = true;
			}
		}
		
		if(isOk){
			
			String[] params = new String[0];
			String sql = " AND ciz.caseisactive=1 ";
		
			try{
				String caseEdi = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editCase");
				System.out.println("Check pass name: " + caseEdi);
				if(caseEdi!=null && !caseEdi.isEmpty() && !"null".equalsIgnoreCase(caseEdi)){
					setSearchParam(caseEdi.split(":")[0]);
					setDateFrom(DateUtils.convertDateString(caseEdi.split(":")[1], DateFormat.YYYY_MM_DD()));
					setDateTo(DateUtils.convertDateString(caseEdi.split(":")[1], DateFormat.YYYY_MM_DD()));
					params = new String[2];
					sql += "AND (ciz.casedate>=? AND ciz.casedate<=?) ";
					params[0] = DateUtils.convertDate(getDateFrom(), DateFormat.YYYY_MM_DD());
					params[1] = DateUtils.convertDate(getDateTo(), DateFormat.YYYY_MM_DD());
				}
			}catch(Exception e){}
			
			if(getSearchParam()!=null && !getSearchParam().isEmpty()){
				int len = getSearchParam().length();
				
				if(len>=4){
				
				 sql += " AND (ciz.complainants like '%"+getSearchParam().replace("--", "")+"%' ";
				 sql += " OR ciz.complainants like '%"+getSearchParam().replace("--", "")+",' ";
				 sql += " OR ciz.complainants like ',"+getSearchParam().replace("--", "")+"%' ";
				 
				 sql += " OR ciz.respondents like '%"+getSearchParam().replace("--", "")+"%' ";
				 sql += " OR ciz.respondents like '%"+getSearchParam().replace("--", "")+",' ";
				 sql += " OR ciz.respondents like ',"+getSearchParam().replace("--", "")+"%' )";
				} 
			}else{
				
				sql += "AND (ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?)";
				//sql += "AND (ciz.casedate>=? AND ciz.casedate<=?) ";
				params = new String[3];
				params[0] = CaseStatus.SETTLED.getId()+"";
				params[1] = CaseStatus.CANCELLED.getId()+"";
				params[2] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
				//params[1] = DateUtils.convertDate(getDateFrom(), DateFormat.YYYY_MM_DD());
				//params[2] = DateUtils.convertDate(getDateTo(), DateFormat.YYYY_MM_DD());
			}
			cases = Collections.synchronizedList(new ArrayList<Cases>());
			for(Cases ciz : Cases.retrieve(sql, params)){
				
				String[] param = new String[1];
				String sqlAdd = " AND ciz.caseisactive=1 AND ciz.caseid=? AND fil.filisactive=1";
				param[0] = ciz.getId()+"";
				
				List<CaseFilling> fils = CaseFilling.retrieve(sqlAdd, param);
				ciz.setCaseFilling(fils);
				
				if(CaseStatus.MOVED_IN_HIGHER_COURT.getId()==ciz.getStatus()){
					ciz.setEndorsed(false);
				}else{
					ciz.setEndorsed(true);
				}
				
				cases.add(ciz);
			}
			
			if(cases!=null && cases.size()==1){
				clickItem(cases.get(0).getCaseFilling().get(cases.get(0).getCaseFilling().size()-1));
			}
			
			Collections.reverse(cases);
			
		}
		
	}
	
	public void triggerCase(){
		setOtherCase(true);
		setOtherCaseName(null);
	}
	
	public void saveData(){
		
		boolean isOk = true;
		
		if(getComplainants()==null || getComplainants().isEmpty()){
			Application.addMessage(3, "Error", "Please provide complainant name");
			isOk = false;
		}
		if(getCompAddress()==null || getCompAddress().isEmpty()){
			Application.addMessage(3, "Error", "Please provide complainant address");
			isOk = false;
		}
		
		if(getRespondents()==null || getRespondents().isEmpty()){
			Application.addMessage(3, "Error", "Please provide respondent name");
			isOk = false;
		}
		if(getResAddress()==null || getResAddress().isEmpty()){
			Application.addMessage(3, "Error", "Please provide respondent address");
			isOk = false;
		}
		
		if(CaseKind.OTHERS.getId()==getKindId()){
			if(getOtherCaseName()==null || getOtherCaseName().isEmpty()){
				Application.addMessage(3, "Error", "Please provide case name");
				isOk = false;
			}
		}
		
		if(isOk){
			
			CaseFilling fil = new CaseFilling();
			Cases cz = new Cases();
			if(getSelectedCase()!=null){
				cz = getSelectedCase();
			}else{
				cz.setDate(DateUtils.getCurrentDateYYYYMMDD());
				cz.setIsActive(1);
			}
			
			cz.setOtherCaseName(getOtherCaseName()==null? "" : getOtherCaseName().toUpperCase());
			cz.setStatus(getStatusId());
			cz.setCaseNo(getCaseNumber());
			cz.setType(getTypeId());
			cz.setKind(getKindId());
			cz.setComplainants(getComplainants().toUpperCase());
			cz.setComAddress(getCompAddress().toUpperCase());
			cz.setRespondents(getRespondents().toUpperCase());
			cz.setResAddress(getResAddress().toUpperCase());
			cz.setNarratives(getNarratives().toUpperCase());
			cz.setUserDtls(getUser());
			
			cz.setWitnesses(getWitness());
			cz.setWitnessAddress(getWitnessAddress());
			cz.setSolutions(getSolutions());
			
			
			cz = Cases.save(cz);
			
			boolean isAnotherReceipt=false;
			if(getSelectedCaseFilling()!=null){
				fil = getSelectedCaseFilling();
			}else{
				fil.setIsActive(1);
				fil.setFillingDate(DateUtils.getCurrentDateYYYYMMDD());
				fil.setFormType(KPForms.KP_FORM0.getId());
				
				// for another reciept // use for another summon
				//AB
				if(KPForms.KP_FORM0.getId()==getKpId()) {
					String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM0.getId();
					String[] params = new String[1];
					params[0] = cz.getId()+"";
					List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
					if(filz!=null && filz.size()>=1) {
						setAttemps(filz.size()+1);
						isAnotherReceipt = true;
					}else {
						setAttemps(1);
					}
					
				}
			}
			
			
			fil.setSettlementDate(DateUtils.convertDate(getSettlementDate(), DateFormat.YYYY_MM_DD()));
			fil.setSettlementTime(getSettlementTime());
			fil.setOrNumber(getOrNumber());
			fil.setFees(getFees());
			fil.setMsgFee(getMessengerFees());
			
			if(KPForms.KP_FORM9.getId()==getKpId()) {
				fil.setCount(getAttemps());
			}else {
				
				//code for AB above
				if(KPForms.KP_FORM0.getId()==getKpId()) {// for another reciept // use for another summon
					if(isAnotherReceipt) {
						fil.setCount(getAttemps()==0? 1 : getAttemps());
					}else {
						fil.setCount(1);
					}
				}else {
					fil.setCount(1);
				}
			}
			
			fil.setCases(cz);
			fil.setUserDtls(getUser());
			
			fil.setFormType(getKpId());
			Employee chaiman = new Employee();
			chaiman.setId(getChairmanId());
			fil.setChaiman(chaiman);
			
			Employee sec = new Employee();
			sec.setId(getSecId());
			fil.setSecretary(sec);
			
			fil.save();
			init();
			clearFlds();
			Application.addMessage(1, "Success", "Successfully saved.");
		}
		
		
	}
	
	private UserDtls getUser(){
		return Login.getUserLogin().getUserDtls();
	}
	
	public void clickItem(CaseFilling fil){
		Cases cz = fil.getCases();
		setSelectedCaseFilling(fil);
		setSelectedCase(cz);
		
		if(CaseKind.OTHERS.getId()==cz.getKind()){
			setOtherCase(true);
			setOtherCaseName(cz.getOtherCaseName().toUpperCase());
		}else{
			setOtherCase(false);
			setOtherCaseName(null);
		}
		
		setFillingDate(fil.getFillingDate());
		setCaseNumber(cz.getCaseNo());
		setSettlementDate(DateUtils.convertDateString(fil.getSettlementDate(), DateFormat.YYYY_MM_DD()));
		setSettlementTime(fil.getSettlementTime());
		setAttemps(fil.getCount());
		setKpId(fil.getFormType());
		setChairmanId(fil.getChaiman().getId());
		setSecId(fil.getSecretary().getId());
		
		setComplainants(cz.getComplainants());
		setRespondents(cz.getRespondents());
		setCompAddress(cz.getComAddress());
		setResAddress(cz.getResAddress());
		setNarratives(cz.getNarratives());
		setOrNumber(fil.getOrNumber());
		setFees(fil.getFees());
		setMessengerFees(fil.getMsgFee());
		
		setStatusId(cz.getStatus());
		setTypeId(cz.getType());
		setKindId(cz.getKind());
		
		setSolutions(cz.getSolutions());
		setWitness(cz.getWitnesses());
		setWitnessAddress(cz.getWitnessAddress());
		
	}
	
	public void deleteRow(CaseFilling cz){
		
		cz.delete();
		init();
		clearFlds();
		Application.addMessage(1, "Success", "Successfully removed.");
		
	}
	
	public List<String> completeNames(String query) {
		//return Customer.retrieve(query, "fullname", "limit 10");
		return new ArrayList<String>();
	}
	
	public List<String> completeAddress(String query) {
		List<String> result = new ArrayList<>();
		
		for(Purok p : Purok.retrieve("AND pur.purokname like '%"+ query +"%'", new String[0])){
			result.add(p.getPurokName());
		}
		
		for(Purok p : Purok.retrieve("AND pur.purokname like '%"+ query +"%'", new String[0])){
			result.add(p.getPurokName() + ", " + p.getBarangay().getName() + ", " + p.getMunicipality().getName());
		}
		
		for(Barangay p : Barangay.retrieve("AND bgy.bgname like '%"+ query +"%'", new String[0])){
			result.add(p.getName());
		}
		
		for(Barangay p : Barangay.retrieve("AND bgy.bgname like '%"+ query +"%'", new String[0])){
			result.add( p.getName() + ", " + p.getMunicipality().getName() + ", " + p.getProvince().getName());
		}
		
		for(Municipality p : Municipality.retrieve("AND mun.munname like '%"+ query +"%'", new String[0])){
			result.add(p.getName());
		}
		
		for(Municipality p : Municipality.retrieve("AND mun.munname like '%"+ query +"%'", new String[0])){
			result.add( p.getName() + ", " + p.getProvince().getName());
		}
		
		for(Province p : Province.retrieve("AND prv.provname like '%"+ query +"%'", new String[0])){
			result.add(p.getName());
		}
		
		return new ArrayList<String>();
		//return result;
	}
	
	public void newInvitation(List<CaseFilling> fils){
		Cases cz = fils.get(0).getCases();
		setSelectedCaseFilling(null);
		setSelectedCase(cz);
		
		setFillingDate(DateUtils.getCurrentDateYYYYMMDD());
		setCaseNumber(cz.getCaseNo());
		setSettlementDate(DateUtils.getDateToday());
		setSettlementTime(DateUtils.getCurrentTIME());
		setAttemps(fils.size()+1);
		
		setComplainants(cz.getComplainants());
		setRespondents(cz.getRespondents());
		setCompAddress(cz.getComAddress());
		setResAddress(cz.getResAddress());
		setNarratives(cz.getNarratives());
		setOrNumber(null);
		setFees(0);
		setMessengerFees(0);
		
		setStatusId(cz.getStatus());
		setTypeId(cz.getType());
		setKindId(cz.getKind());
		
	}
	
	public void createNewForm(List<CaseFilling> fils) {
		Cases cz = fils.get(0).getCases();
		setSelectedCaseFilling(null);
		setSelectedCase(cz);
		
		setFillingDate(DateUtils.getCurrentDateYYYYMMDD());
		setCaseNumber(cz.getCaseNo());
		setSettlementDate(DateUtils.getDateToday());
		setSettlementTime(DateUtils.getCurrentTIME());
		
		setComplainants(cz.getComplainants());
		setRespondents(cz.getRespondents());
		setCompAddress(cz.getComAddress());
		setResAddress(cz.getResAddress());
		setNarratives(cz.getNarratives());
		setSolutions(cz.getSolutions());
		setOrNumber(null);
		setFees(0);
		setMessengerFees(0);
		
		setStatusId(cz.getStatus());
		setTypeId(cz.getType());
		setKindId(cz.getKind());
		setKpId(KPForms.KP_FORM9.getId());
		
		String sql = " AND fil.filisactive=1 AND ciz.caseid=? AND fil.formtype="+KPForms.KP_FORM9.getId();
		String[] params = new String[1];
		params[0] = cz.getId()+"";
		List<CaseFilling> filz = CaseFilling.retrieve(sql, params);
		if(filz!=null && filz.size()>0) {
			setAttemps(filz.size()+1);
			int index = filz.size()==1? 0 : filz.size()-1; 
			//assign temporary date and time from previous work
			setSettlementDate(DateUtils.convertDateString(filz.get(index).getSettlementDate(),DateFormat.YYYY_MM_DD()));
			setSettlementTime(filz.get(index).getSettlementTime());
		}else {
			setAttemps(1);
		}
		
		setWitness(cz.getWitnesses());
		setWitnessAddress(cz.getWitnessAddress());
		
		if(cz.getKind()==CaseKind.OTHERS.getId()) {
			setOtherCaseName(cz.getOtherCaseName());
			setOtherCase(false);
		}
	}
	
	public void clearFlds(){
		setSelectedCase(null);
		setSelectedCaseFilling(null);
		setFillingDate(null);
		setCaseNumber(null);
		setSettlementDate(null);
		setSettlementTime(null);
		setAttemps(1);
		
		setComplainants(null);
		setRespondents(null);
		setCompAddress(null);
		setResAddress(null);
		setNarratives(null);
		setOrNumber(null);
		setFees(0);
		setMessengerFees(0);
		
		setStatusId(1);
		setTypeId(2);
		setKindId(1);
		setOtherCase(false);
		setOtherCaseName(null);
		
		setSeveralConfrontation(false);
		
		setKpId(0);
		setWitness(null);
		setWitnessAddress(null);
		setSolutions(null);
		
		setChairmanId(0);
		setSecId(0);
		
		
		orNumber = ORTransaction.getNewOrNumber();
		
	}
	
	public void deleteCase(Cases cz){
			
			String[] param = new String[1];
			String sqlAdd = " AND ciz.caseisactive=1 AND ciz.caseid=? AND fil.filisactive=1";
			param[0] = cz.getId()+"";
			
			for(CaseFilling f : CaseFilling.retrieve(sqlAdd, param)){
				f.delete();
			}
			
		cz.delete();
		clearFlds();
		init();
		Application.addMessage(1, "Success", "Successfully deleted");
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
			
			List<CasesRpt> reports = Collections.synchronizedList(new ArrayList<CasesRpt>());
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

	
	public String getFillingDate() {
		if(fillingDate==null){
			fillingDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return fillingDate;
	}
	public void setFillingDate(String fillingDate) {
		this.fillingDate = fillingDate;
	}
	public String getCaseNumber() {
		if(caseNumber==null){
			caseNumber = Cases.getCaseNumber();
		}
		return caseNumber;
	}
	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}
	public Date getSettlementDate() {
		if(settlementDate==null){
			settlementDate = DateUtils.getDateToday();
		}
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getSettlementTime() {
		if(settlementTime==null){
			settlementTime = DateUtils.getCurrentTIME();
		}
		return settlementTime;
	}
	public void setSettlementTime(String settlementTime) {
		this.settlementTime = settlementTime;
	}
	
	
	public String getNarratives() {
		return narratives;
	}
	public void setNarratives(String narratives) {
		this.narratives = narratives;
	}
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}
	public double getFees() {
		return fees;
	}
	public void setFees(double fees) {
		this.fees = fees;
	}
	public double getMessengerFees() {
		return messengerFees;
	}
	public void setMessengerFees(double messengerFees) {
		this.messengerFees = messengerFees;
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
	public List getStatus() {
		
		status = new ArrayList<>();
		for(CaseStatus s : CaseStatus.values()){
			status.add(new SelectItem(s.getId(), s.getName()));
		}
		
		return status;
	}
	public void setStatus(List status) {
		this.status = status;
	}
	public int getTypeId() {
		if(typeId==0){
			typeId=2;
		}
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public List getTypes() {
		
		types = new ArrayList<>();
		types.add(new SelectItem(1, "INVITATION"));
		types.add(new SelectItem(2, "SUMMON"));
		
		return types;
	}
	public void setTypes(List types) {
		this.types = types;
	}
	public int getKindId() {
		if(kindId==0){
			kindId = 1;
		}
		return kindId;
	}
	public void setKindId(int kindId) {
		this.kindId = kindId;
	}
	public List getKinds() {
		
		kinds = new ArrayList<>();
		Map<String, CaseKind> unsort = Collections.synchronizedMap(new HashMap<String, CaseKind>());
		for(CaseKind c : CaseKind.values()){
			unsort.put(c.getName(), c);
		}
		Map<String, CaseKind> sorted = new TreeMap<String, CaseKind>(unsort);
		for(CaseKind c : sorted.values()){
			kinds.add(new SelectItem(c.getId(), c.getName()));
		}
		
		
		
		return kinds;
	}
	public void setKinds(List kinds) {
		this.kinds = kinds;
	}
	public int getAttemps() {
		if(attemps==0){
			attemps = 1;
		}
		return attemps;
	}
	public void setAttemps(int attemps) {
		this.attemps = attemps;
	}
	public String getComplainants() {
		return complainants;
	}
	public void setComplainants(String complainants) {
		this.complainants = complainants;
	}
	public String getRespondents() {
		return respondents;
	}
	public void setRespondents(String respondents) {
		this.respondents = respondents;
	}
	

	public List<Cases> getCases() {
		return cases;
	}

	public void setCases(List<Cases> cases) {
		this.cases = cases;
	}


	public Date getDateFrom() {
		if(dateFrom==null){
			dateFrom = DateUtils.getDateToday();
		}
		return dateFrom;
	}


	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public Date getDateTo() {
		if(dateTo==null){
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

	public Cases getSelectedCase() {
		return selectedCase;
	}

	public void setSelectedCase(Cases selectedCase) {
		this.selectedCase = selectedCase;
	}

	public String getCompAddress() {
		return compAddress;
	}

	public void setCompAddress(String compAddress) {
		this.compAddress = compAddress;
	}

	public String getResAddress() {
		return resAddress;
	}

	public void setResAddress(String resAddress) {
		this.resAddress = resAddress;
	}

	public CaseFilling getSelectedCaseFilling() {
		return selectedCaseFilling;
	}

	public void setSelectedCaseFilling(CaseFilling selectedCaseFilling) {
		this.selectedCaseFilling = selectedCaseFilling;
	}
	
	public void generateEndorsement(Cases cz){
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		List<ClearanceRpt> reports = Collections.synchronizedList(new ArrayList<ClearanceRpt>());
		reports.add(new ClearanceRpt());
		
		String REPORT_NAME = CASE_ENDORSEMENT_REPORT_NAME;
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		HashMap param = new HashMap();
		
		Employee kapitan = Employee.retrievePosition(Positions.CAPTAIN.getId());
		param.put("PARAM_OFFICIAL","HON. "+ kapitan.getFirstName().toUpperCase() + " "+ kapitan.getMiddleName().substring(0, 1).toUpperCase() +". " + kapitan.getLastName().toUpperCase());
		
		param.put("PARAM_PROVINCE", "Province of " + PROVINCE);
		param.put("PARAM_MUNICIPALITY", "Municipality of " + MUNICIPALITY);
		param.put("PARAM_BARANGAY", "BARANGAY " + BARANGAY.toUpperCase());
		
		param.put("PARAM_TITLE", "ENDORSEMENT");
		param.put("PARAM_CASENO", cz.getCaseNo());
		if(CaseKind.OTHERS.getId()==cz.getKind()){
			param.put("PARAM_KIND_CASE", cz.getOtherCaseName());
		}else{	
			param.put("PARAM_KIND_CASE", CaseKind.typeName(cz.getKind()));
		}
		param.put("PARAM_COMPLAINANT", cz.getComplainants().toUpperCase());
		param.put("PARAM_COMP_ADDRESS", cz.getComAddress().toUpperCase());
		param.put("PARAM_RESPONDENT", cz.getRespondents().toUpperCase());
		param.put("PARAM_RES_ADDRESS", cz.getResAddress().toUpperCase());
		
		UserDtls user = getUser();
		
		param.put("PARAM_PROCCESSBY", user.getFirstname().toUpperCase() + " " + user.getMiddlename().substring(0, 1).toUpperCase()+". " + user.getLastname().toUpperCase());
		
		String position = "Barangay Secretary";
		
		try{
		String sql = "SELECT * FROM jobtitle WHERE jobtitleid=" + user.getJob().getJobid();
		Job job = Job.retrieve(sql, new String[0]).get(0);
		
		if(UserAccess.DEVELOPER.getId()==job.getJobid()){
			position = "Acting Barangay Secretary";
		}else{
			position = "Barangay " + job.getJobname();
		}
		
		}catch(Exception e){}
		
		param.put("PARAM_PROCESSPOS", position);
		
		String content = "";
		int cnt = 1;
		if(isSeveralConfrontation()){
			content = cnt++ + Words.getTagName("case-endorsement-string2");
		}else{
			content = cnt++ + Words.getTagName("case-endorsement-string1");
			content += cnt++ + Words.getTagName("case-endorsement-string3");
		}
		
		content += cnt++ + Words.getTagName("case-endorsement-string4");
		content += cnt++ + Words.getTagName("case-endorsement-string5");
		
		content += Words.getTagName("case-endorsement-string6");
		
		content = content.replace("<date>", DateUtils.dayNaming(DateUtils.getCurrentDateYYYYMMDD().split("-")[2]));
		content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(DateUtils.getCurrentDateYYYYMMDD().split("-")[1])));
		content = content.replace("<year>",DateUtils.getCurrentDateYYYYMMDD().split("-")[0]);
				
		param.put("PARAM_CONTENT", content);
		
		//logo
		String officialLogo = path + "logo.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){}
		
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
			barcode = BarcodeFactory.create3of9(cz.getCaseNo(), false);
			
			barcode.setDrawingText(false);
			File pdf = new File(cz.getCaseNo()+".png");
			
			BarcodeImageHandler.savePNG(barcode, pdf);
			barPdf = new FileInputStream(pdf);
			param.put("PARAM_BARCODE", barPdf);
		}catch(Exception e){e.printStackTrace();}
		
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
	
	public void printNotice(CaseFilling fil){
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		List<ClearanceRpt> reports = Collections.synchronizedList(new ArrayList<ClearanceRpt>());
		reports.add(new ClearanceRpt());
		
		String REPORT_NAME = CASE_NOTICE_REPORT_NAME;
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		HashMap param = new HashMap();
		
		Employee kapitan = Employee.retrievePosition(Positions.CAPTAIN.getId());
		param.put("PARAM_OFFICIAL","HON. "+ kapitan.getFirstName().toUpperCase() + " "+ kapitan.getMiddleName().substring(0, 1).toUpperCase() +". " + kapitan.getLastName().toUpperCase());
		
		param.put("PARAM_PROVINCE", "Province of " + PROVINCE);
		param.put("PARAM_MUNICIPALITY", "Municipality of " + MUNICIPALITY);
		param.put("PARAM_BARANGAY", "BARANGAY " + BARANGAY.toUpperCase());
		
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
		param.put("PARAM_COMPLAINANT", fil.getCases().getComplainants().toUpperCase());
		param.put("PARAM_COMP_ADDRESS", fil.getCases().getComAddress().toUpperCase());
		param.put("PARAM_RESPONDENT", fil.getCases().getRespondents().toUpperCase());
		param.put("PARAM_RES_ADDRESS", fil.getCases().getResAddress().toUpperCase());
		
		
		String content = "";
		
		content = Words.getTagName("case-note-string1");
		content = content.replace("<monthdayyear>", DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
		content = content.replace("<time>", fil.getSettlementTime());
		
		content += Words.getTagName("case-note-string2");
		content += Words.getTagName("case-note-string3");
		content += Words.getTagName("case-note-string4");
		content = content.replace("<date>", DateUtils.dayNaming(fil.getFillingDate().split("-")[2]));
		content = content.replace("<month>",DateUtils.getMonthName(Integer.valueOf(fil.getFillingDate().split("-")[1])));
		content = content.replace("<year>",fil.getFillingDate().split("-")[0]);
		
		param.put("PARAM_CONTENT", content);
		
		//logo
		String officialLogo = path + "logo.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){}
		
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
			barcode = BarcodeFactory.create3of9(fil.getCases().getCaseNo(), false);
			
			barcode.setDrawingText(false);
			File pdf = new File(fil.getCases().getCaseNo()+".png");
			
			BarcodeImageHandler.savePNG(barcode, pdf);
			barPdf = new FileInputStream(pdf);
			param.put("PARAM_BARCODE", barPdf);
		}catch(Exception e){e.printStackTrace();}
		
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
	
	
	/*public void checkConflictTime(){
		if(CaseFilling.checkConflictTime(DateUtils.convertDate(getSettlementDate(), DateFormat.YYYY_MM_DD()), getSettlementTime())){
			Application.addMessage(2, "Conflict", "The inputted date is conflict");
		}
	}*/
	
	public void loadCaseScheduled(){
		caseLookup = Collections.synchronizedList(new ArrayList<Cases>());
		
		String sql = " AND ciz.caseisactive=1 AND (ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=? )";
		String[] params = new String[3];
		params[0] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		params[1] = CaseStatus.SETTLED.getId()+"";
		params[2] = CaseStatus.CANCELLED.getId()+"";
		
		for(Cases cz : Cases.retrieve(sql, params)){
			sql = " AND ciz.caseid=? AND fil.filisactive=1 AND fil.settlementdate>=? ORDER BY fil.filid DESC LIMIT 1";
			params = new String[2];
			params[0] = cz.getId()+"";
			params[1] = DateUtils.getCurrentDateYYYYMMDD();
			try{
				CaseFilling fil = CaseFilling.retrieve(sql, params).get(0);
				
				//add color for conflict date and time
				if(CaseFilling.isConflictDateAndTime(fil.getSettlementDate(), fil.getSettlementTime())){
					fil.setStyle("color: red;");
				}
				fil.setSettlementDate(DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()));
				
				cz.setFilling(fil);
				caseLookup.add(cz);
			}catch(Exception e){}
		}
		
	}
	
	public void printSummon(CaseFilling fil) {
		setKpId(KPForms.KP_FORM9.getId());
		setSelectedCaseFilling(fil);
		//printForm();
	}
	
	public void printForm(CaseFilling fil) {
		//if(getSelectedCaseFilling()!=null) {
			
			//CaseFilling fil = getSelectedCaseFilling();
			
			Map<Integer, Object> obj = CasesPrintingV6.printDocumentV6(fil,fil.getFormType());
			String path = (String)obj.get(1);
			String REPORT_NAME = (String)obj.get(2);
			String jrxmlFile = (String)obj.get(3);
			HashMap param = (HashMap)obj.get(4);
			JRBeanCollectionDataSource beanColl = (JRBeanCollectionDataSource)obj.get(5); 
			
			//logo
			String officialLogo = path + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_LOGO", off);
			}catch(Exception e){}
			
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
				barcode = BarcodeFactory.create3of9(fil.getCases().getCaseNo(), false);
				
				barcode.setDrawingText(false);
				File folder = new File(path + "barcode" + File.separator);
				folder.mkdir();
				
				File pdf = new File(path + "barcode" + File.separator+fil.getCases().getCaseNo()+".png");
				
				BarcodeImageHandler.savePNG(barcode, pdf);
				barPdf = new FileInputStream(pdf);
				param.put("PARAM_BARCODE", barPdf);
			}catch(Exception e){e.printStackTrace();}
			
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
			
			
		//}
	}
	
	public boolean isSeveralConfrontation() {
		return severalConfrontation;
	}

	public void setSeveralConfrontation(boolean severalConfrontation) {
		this.severalConfrontation = severalConfrontation;
	}

	public boolean isOtherCase() {
		return otherCase;
	}

	public void setOtherCase(boolean otherCase) {
		this.otherCase = otherCase;
	}

	public String getOtherCaseName() {
		return otherCaseName;
	}

	public void setOtherCaseName(String otherCaseName) {
		this.otherCaseName = otherCaseName;
	}

	public List<Cases> getCaseLookup() {
		return caseLookup;
	}

	public void setCaseLookup(List<Cases> caseLookup) {
		this.caseLookup = caseLookup;
	}

	public int getKpId() {
		/*if(kpId==0) {
			kpId=7;
		}*/
		return kpId;
	}

	public void setKpId(int kpId) {
		this.kpId = kpId;
	}

	public List getKps() {
		kps = new ArrayList<>();
		
		for(KPForms form : KPForms.values()) {
			switch(form.getId()) {
			case 0:
			case 7:
			case 8:
			case 9:	
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 30:
			case 31:
			case 32:kps.add(new SelectItem(form.getId(), form.getName()));break;
			}
			
		}
		
		return kps;
	}

	public void setKps(List kps) {
		this.kps = kps;
	}

	public String getWitness() {
		return witness;
	}

	public void setWitness(String witness) {
		this.witness = witness;
	}

	public String getWitnessAddress() {
		return witnessAddress;
	}

	public void setWitnessAddress(String witnessAddress) {
		this.witnessAddress = witnessAddress;
	}

	public String getSolutions() {
		return solutions;
	}

	public void setSolutions(String solutions) {
		this.solutions = solutions;
	}

	public List getChairmans() {
		
		chairmans = new ArrayList<>();
		String sql = " AND (pos.posid=1 OR pos.posid=14)  AND emp.isactiveemp=1 AND emp.isresigned=0";
		String[] params = new String[0];
		/*params[0] = Positions.CAPTAIN.getId()+"";
		params[1] = Positions.BARANGAY_LUPON_CHAIRMAN.getId()+"";*/
		for(Employee e : Employee.retrieve(sql, params)) {
			chairmans.add(new SelectItem(e.getId(),e.getFirstName().toUpperCase() + " " + e.getLastName().toUpperCase()));
		}
		
		return chairmans;
	}

	public void setChairmans(List chairmans) {
		this.chairmans = chairmans;
	}

	public long getChairmanId() {
		if(chairmanId==0) {
			Employee e = Employee.retrievePosition(Positions.CAPTAIN.getId());
			chairmanId = e.getId();
		}
		return chairmanId;
	}

	public void setChairmanId(long chairmanId) {
		this.chairmanId = chairmanId;
	}

	public List getSecretaries() {
		
		secretaries = new ArrayList<>();
		String sql = " AND emp.isactiveemp=1 AND emp.isresigned=0";
		String[] params = new String[0];
		for(Employee e : Employee.retrieve(sql, params)) {
			secretaries.add(new SelectItem(e.getId(),e.getFirstName().toUpperCase() + " " + e.getLastName().toUpperCase()));
		}
		
		return secretaries;
	}

	public void setSecretaries(List secretaries) {
		this.secretaries = secretaries;
	}

	public long getSecId() {
		if(secId==0) {
			Employee e = Employee.retrievePosition(Positions.SECRETARY.getId());
			secId = e.getId();
		}
		return secId;
	}

	public void setSecId(long secId) {
		this.secId = secId;
	}
	
}
