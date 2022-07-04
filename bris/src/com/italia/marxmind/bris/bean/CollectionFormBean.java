package com.italia.marxmind.bris.bean;

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

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.application.GlobalReportHandler;
import com.italia.marxmind.bris.application.GlobalReportHandler.GlobalReport;
import com.italia.marxmind.bris.application.RCDFormDetails;
import com.italia.marxmind.bris.application.RCDFormSeries;
import com.italia.marxmind.bris.application.RCDReader;
import com.italia.marxmind.bris.controller.CollectionInfo;
import com.italia.marxmind.bris.controller.Collector;
import com.italia.marxmind.bris.controller.Form11Report;
import com.italia.marxmind.bris.controller.IssuedForm;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.FormStatus;
import com.italia.marxmind.bris.enm.FormType;
import com.italia.marxmind.bris.enm.FundType;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.utils.Currency;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 06-12-2019
 *
 */
@Named("colBean")
@SessionScoped
public class CollectionFormBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 464988556561L;
	
	private Date receivedDate;
	private int collectorId;
	private List collectors;
	
	private int formTypeId;
	private List formTypes = new ArrayList<>();
	
	private long issuedId;
	private List issueds;
	
	private long beginningNo;
	private long endingNo;
	private int quantity;
	
	private double amount;
	private String totalAmount;
	private int group;
	
	private int tmpQty;
	private IssuedForm issuedData;
	private List<CollectionInfo> newForms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
	
	private Map<Integer, CollectionInfo> maps = Collections.synchronizedMap(new HashMap<Integer, CollectionInfo>());
	
	private int collectorMapId;
	private List collectorsMap;
	private Map<Integer, Collector> collectotData = Collections.synchronizedMap(new HashMap<Integer, Collector>());
	
	private List<CollectionInfo> infos = Collections.synchronizedList(new ArrayList<CollectionInfo>());
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	
	private int monthId;
	private List months;
	
	private StreamedContent tempPdfFile; 
	
	private CollectionInfo selectedCollectionData;
	
	private int fundId;
	private List funds;
	
	@PostConstruct
	public void init() {
		maps = Collections.synchronizedMap(new HashMap<Integer, CollectionInfo>());
		infos = Collections.synchronizedList(new ArrayList<CollectionInfo>());
		
		String sql = " AND (receiveddate>=? AND receiveddate<=?)";
		String[] params = new String[2];
		int cnt = 0;
		if(getCollectorMapId()>0) {
			params = new String[3];
			sql += " AND cl.isid=?";
		}
		
		String dayStart = DateUtils.getCurrentYear()+"-"+ (DateUtils.getCurrentMonth()<10? "0"+DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+"") +"-01";
		String dayEnd = DateUtils.getCurrentYear()+"-"+ (DateUtils.getCurrentMonth()<10? "0"+DateUtils.getCurrentMonth() : DateUtils.getCurrentMonth()+"") +"-31";
		
		
		if(getMonthId()==0) {
			dayStart = DateUtils.getCurrentYear()+"-01-01";
			dayEnd = DateUtils.getCurrentYear()+"-12-31";
		}else if(getMonthId()>0 && getMonthId()!=DateUtils.getCurrentMonth()) {
			dayStart = DateUtils.getCurrentYear()+"-"+ (getMonthId()<10? "0"+getMonthId() : getMonthId()+"") +"-01";
			dayEnd = DateUtils.getCurrentYear()+"-"+ (getMonthId()<10? "0"+getMonthId() : getMonthId()+"") +"-31";
		}
		
		params[cnt++] =  dayStart;
		params[cnt++] =  dayEnd;
		
		if(getCollectorMapId()>0) {
			params[cnt++] = getCollectorMapId()+"";
		}
		
		for(CollectionInfo in : CollectionInfo.retrieve(sql, params)){
			int key = in.getRptGroup();
			
			if(maps!=null && maps.containsKey(key)) {
				double newAmount = maps.get(key).getAmount();
				newAmount += in.getAmount();
				in.setAmount(newAmount);
				maps.put(key, in);
			}else {
				maps.put(key, in);
			}
		}
		
		for(CollectionInfo i : maps.values()) {
			String value = "";
			String len = i.getRptGroup()+"";
			int size = len.length();
			if(size==1) {
				String[] date = i.getReceivedDate().split("-");
				value = date[0] +"-"+date[1] + "-00"+len;
			}else if(size==2) {
				String[] date = i.getReceivedDate().split("-");
				value = date[0] +"-"+date[1] + "-0"+len;
			}else if(size==3) {
				String[] date = i.getReceivedDate().split("-");
				value = date[0] +"-"+date[1] + "-"+len;
			}
			i.setRptFormat(value);
			
			infos.add(i);
		}
		Collections.reverse(infos);
	}
	
	
	public void loadIssuedForm() {
		
		//clear all above if changing collector
		clearBelowFormList();
		
		issueds = new ArrayList<>();
		
		String sql = " AND frm.fundid=? AND frm.formstatus=1 AND cl.isid=?";
		String[] params = new String[2];
		params[0] = getFundId()+"";
		params[1] = getCollectorId()+"";
		
		//assigned group
		if(getGroup()==0) {
			setGroup(CollectionInfo.getNewReportGroup(getCollectorId()));
		}
		
		List<IssuedForm> forms = IssuedForm.retrieve(sql, params);
		if(forms!=null && forms.size()>1) {
			int x=1;
			for(IssuedForm form : forms) {
				issueds.add(new SelectItem(form.getId(), form.getFormTypeName() +" " +form.getBeginningNo() +"-" + form.getEndingNo()));
				if(x==1) {
					setBeginningNo(form.getEndingNo());
					setFormTypeId(form.getFormType());
					setIssuedId(form.getId());
					loadLatestSeries();
				}
				x++;
			}
		}else if(forms!=null && forms.size()==1) {
			issueds.add(new SelectItem(forms.get(0).getId(), forms.get(0).getFormTypeName() +" " + forms.get(0).getBeginningNo() +"-" + forms.get(0).getEndingNo()));
			setBeginningNo(forms.get(0).getEndingNo());
			setFormTypeId(forms.get(0).getFormType());
			setIssuedId(forms.get(0).getId());
			loadLatestSeries();
		}else {
			issueds.add(new SelectItem(0, "No Issued Form"));
		}
		
	}
	
	public void loadLatestSeries() {
		String sql = " AND frm.fundid=? AND cl.isid=? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
		String[] params = new String[3];
		params[0] = getFundId()+"";
		params[1] = getCollectorId()+"";
		params[2] = getIssuedId()+"";
		
		List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
		if(infos!=null && infos.size()>0) {
			CollectionInfo info = infos.get(0);
			setBeginningNo(info.getEndingNo()+1);
			setFormTypeId(info.getFormType());
			long qty = info.getIssuedForm().getEndingNo() - getBeginningNo();
			setQuantity(Integer.valueOf(qty+"")+1);
			setEndingNo(info.getIssuedForm().getEndingNo());
			setTmpQty(Integer.valueOf(qty+"")+1);
			setIssuedData(info.getIssuedForm());
			
			
			if(getFormTypeId()>8) {
				System.out.println("Retrieved in Collection Cash Ticket>> ");
				long beg =   info.getBeginningNo();
				long to =  info.getEndingNo();
				
				qty = 0;
				
				if(FormType.CT_2.getId()==info.getFormType()) {
					qty = beg / 2;
				}else if(FormType.CT_5.getId()==info.getFormType()) {
					qty = beg / 5;
				} 
				
				setTmpQty(Integer.valueOf(qty+""));
				setBeginningNo(beg);
				setEndingNo(to);
				setSelectedCollectionData(info);
				System.out.println("Set temp qty >> " + getTmpQty());
			}
			
		}else {
			sql = "  AND frm.fundid=? AND frm.formstatus=1 AND cl.isid=? AND frm.logid=?";
			params = new String[3];
			params[0] = getFundId()+"";
			params[1] = getCollectorId()+"";
			params[2] = getIssuedId()+"";
			List<IssuedForm> forms = IssuedForm.retrieve(sql, params);
			setBeginningNo(forms.get(0).getBeginningNo());
			setEndingNo(forms.get(0).getEndingNo());
			setQuantity(forms.get(0).getPcs());
			setFormTypeId(forms.get(0).getFormType());
			setIssuedId(forms.get(0).getId());
			setTmpQty(forms.get(0).getPcs());
			setIssuedData(forms.get(0));
			
			if(getFormTypeId()>8) {
				long beg =   forms.get(0).getBeginningNo();
				long to =  forms.get(0).getEndingNo();
				
				long qty = 0;
				
				if(FormType.CT_2.getId()==forms.get(0).getFormType()) {
					qty = beg / 2;
				}else if(FormType.CT_5.getId()==forms.get(0).getFormType()) {
					qty = beg / 5;
				} 
				
				setTmpQty(Integer.valueOf(qty+""));
				setBeginningNo(beg);
				setEndingNo(to);
				
				CollectionInfo info = new CollectionInfo();
				info.setCollector(forms.get(0).getCollector());
				info.setIssuedForm(forms.get(0));
				info.setPcs(forms.get(0).getPcs());
				info.setBeginningNo(forms.get(0).getBeginningNo());
				info.setEndingNo(forms.get(0).getEndingNo());
				info.setFormType(forms.get(0).getFormType());
				setSelectedCollectionData(info);
				System.out.println("Set temp qty >> " + getTmpQty());
			}
		}
	}
	
	public void calculateEndingNo() {
			
		if(getFormTypeId()<=8) {
			
			if(getQuantity()>getTmpQty()) {
				setQuantity(getTmpQty());
				Application.addMessage(3, "Error", "You have inputed more than the allowed quantity, series will now reset on default end series");
			}
			
			long ending = (getBeginningNo()) + (getQuantity()==0? 0 : getQuantity()-1);
			System.out.println("begin: " + getBeginningNo() + " pcs: " + getQuantity());
			System.out.println("ending: " + ending);
			setEndingNo(ending);
			
		}else {
			
			/*int qty = getQuantity();
			long to = getEndingNo();
			if(FormType.CT_2.getId()==getFormTypeId()) {
				
				qty *= 2;
				
			}else if(FormType.CT_5.getId()==getFormTypeId()) {
				
				qty *= 5;
				
			}
			
			setAmount(qty);
			
			setBeginningNo( to - qty );*/
			
			int qty = getQuantity();
			//long prev = getSelectedCollectionData().getEndingNo() - getSelectedCollectionData().getBeginningNo();
			if(FormType.CT_2.getId()==getFormTypeId()) {
				qty *= 2;
			}else if(FormType.CT_5.getId()==getFormTypeId()) {
				qty *= 5;
			}
			
			setAmount(qty);
			
			long remainingAmount = getSelectedCollectionData().getBeginningNo() - qty;
			setBeginningNo(remainingAmount);
			
			if(getQuantity()==0) {
				setBeginningNo(getSelectedCollectionData().getBeginningNo());
			}
			
		}
		
			
		
	}
	
	public void addGroup() {
		CollectionInfo form = new CollectionInfo();
		
		boolean isOk = true;
		
		
		form.setIsActive(1);
		
		
		if(getBeginningNo()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial From");
		}
		
		if(getEndingNo()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial To");
		}
		
		
		if(getQuantity()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Quantity");
		}
		
		if(getCollectorId()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Collector");
		}
		
		if(getAmount()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		form.setFundId(getFundId());
		form.setRptGroup(getGroup());
		form.setReceivedDate(DateUtils.convertDate(getReceivedDate(), "yyyy-MM-dd"));
		form.setFormType(getFormTypeId());
		form.setPcs(getQuantity());
		form.setBeginningNo(getBeginningNo());
		form.setEndingNo(getEndingNo());
		form.setAmount(getAmount());
		
		long pcs = getIssuedData().getEndingNo() - getBeginningNo();
		form.setPrevPcs(Integer.valueOf(pcs+""));
		
		Collector collector = new Collector();
		collector.setId(getCollectorId());
		form.setCollector(collector);
		
		IssuedForm issued = new IssuedForm();
		issued.setId(getIssuedId());
		form.setIssuedForm(issued);
		
		String start = DateUtils.numberResult(getFormTypeId(), getBeginningNo());
		String end = DateUtils.numberResult(getFormTypeId(), getEndingNo());
		
		form.setStartNo(start);
		form.setEndNo(end);
		
		//tag as all issued if the ending balance is match with the current collection ending series
		if(getIssuedData()!=null) {
			System.out.println("getIssuedData() is not null");
			System.out.println("getEndingNo()="+getEndingNo() +" getIssuedData().getEndingNo()="+getIssuedData().getEndingNo());
			if(getEndingNo()==getIssuedData().getEndingNo()) {
				System.out.println("getIssuedData() is not null equalss");
				getIssuedData().setCollector(collector);
				
				getIssuedData().setStatus(FormStatus.ALL_ISSUED.getId());
				
				form.setStatus(FormStatus.ALL_ISSUED.getId());
				form.setStatusName(FormStatus.ALL_ISSUED.getName());
			}else {
				System.out.println("getIssuedData() is not null not equalss");
				form.setStatus(FormStatus.NOT_ALL_ISSUED.getId());
				form.setStatusName(FormStatus.NOT_ALL_ISSUED.getName());
			}
		}else {
			System.out.println("getIssuedData() is null");
			form.setStatus(FormStatus.NOT_ALL_ISSUED.getId());
			form.setStatusName(FormStatus.NOT_ALL_ISSUED.getName());
		}
		
		try{form.setFormTypeName(FormType.nameId(getFormTypeId()));}catch(NullPointerException e){}
		
		
		if(isOk) {
			newForms.add(form);
			setQuantity(0);
			setBeginningNo(0);
			setEndingNo(0);
			setAmount(0);
			
			//recalculate
			double amount = 0d;
			for(CollectionInfo i : newForms) {
				amount += i.getAmount();
			}
			setTotalAmount(Currency.formatAmount(amount));
		}
	}
	
	public void clear() {
		setReceivedDate(null);
		setCollectorId(0);
		
		setIssuedId(0);
		issueds = new ArrayList<>();
		issueds.add(new SelectItem(0, "No Selected Collector"));
		
		setQuantity(0);
		setBeginningNo(0);
		setEndingNo(0);
		setFormTypeId(0);
		setGroup(0);
		setAmount(0);
		setSelectedCollectionData(null);
		newForms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
		
		setFundId(1);
	}
	
	public void clearBelowFormList() {
		setQuantity(0);
		setBeginningNo(0);
		setEndingNo(0);
		setGroup(0);
		setAmount(0);
		setTotalAmount("0.00");
		newForms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
		setSelectedCollectionData(null);
		Application.addMessage(1, "Success", "Successfully delete forms listed");
	}
	
	public void saveData() {
		
		if(newForms!=null && newForms.size()>0) {
			CollectionInfo info = newForms.get(0);
			
			String dateToday = DateUtils.getCurrentDateYYYYMMDD();
			
			if(dateToday.equalsIgnoreCase(info.getReceivedDate())) {
				
			
			for(CollectionInfo form : newForms) {
				form.save();
				setCollectorMapId(form.getCollector().getId());
				
				if(FormStatus.ALL_ISSUED.getId()==form.getStatus()) {
					IssuedForm is = IssuedForm.retrieveId(form.getIssuedForm().getId());
					is.setStatus(FormStatus.ALL_ISSUED.getId());
					is.save();
				}
			}
			
			
			clear();
			init();
			Application.addMessage(1, "Success", "Successfully saved");
			/*PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("showPdf();");*/
			
			//printReport(info);
			printRDC(info);
			
			newForms = Collections.synchronizedList(new ArrayList<CollectionInfo>());
			
		}else {
			Application.addMessage(3, "Error", "Previous RCD is no longer allowed for editing");
		}
			
		}else {
			Application.addMessage(3, "Error", "Please provide list in order to save");
		}
	}
	
	public void clickItem(CollectionInfo in) {
		String sql = " AND frm.isactivecol=1 AND cl.isid=? AND frm.rptgroup=?";
		String[] params = new String[2];
		params[0] = in.getCollector().getId()+"";
		params[1] = in.getRptGroup()+"";
		
		//loadIssuedForm();
		
		newForms = new ArrayList<CollectionInfo>();
		
		double totalAmount = 0d;
		for(CollectionInfo i : CollectionInfo.retrieve(sql, params)){
			
			String start = DateUtils.numberResult(i.getFormType(), i.getBeginningNo());
			String end = DateUtils.numberResult(i.getFormType(), i.getEndingNo());
			
			i.setStartNo(start);
			i.setEndNo(end);
			
			newForms.add(i);
			totalAmount += i.getAmount();
		}
		
		setTotalAmount(Currency.formatAmount(totalAmount));
		setGroup(in.getRptGroup());
		setCollectorId(in.getCollector().getId());
		
		issueds = new ArrayList<>();
		setIssuedId(0);
		
		sql = " AND frm.formstatus=1 AND cl.isid=?";
		params = new String[1];
		params[0] = getCollectorId()+"";
		
		//assigned group
		setGroup(in.getRptGroup());
		
		List<IssuedForm> forms = IssuedForm.retrieve(sql, params);
		if(forms!=null && forms.size()>1) {
			issueds.add(new SelectItem(0, "Select Issued Form"));	
			int x=1;
			for(IssuedForm form : forms) {
				issueds.add(new SelectItem(form.getId(), form.getFormTypeName() +" " +form.getBeginningNo() +"-" + form.getEndingNo()));
			}
		}else if(forms!=null && forms.size()==1) {
			issueds.add(new SelectItem(0, "Select Issued Form"));
			issueds.add(new SelectItem(forms.get(0).getId(), forms.get(0).getFormTypeName() +" " + forms.get(0).getBeginningNo() +"-" + forms.get(0).getEndingNo()));
		}else {
			issueds.add(new SelectItem(0, "No Issued Form"));
		}
		
		PrimeFaces pm = PrimeFaces.current();
		pm.executeScript("addNew();displayWizard();");
		
	}
	
	public void clickItemForm(CollectionInfo in) {
		setReceivedDate(DateUtils.convertDateString(in.getReceivedDate(), "yyyy-MM-dd"));
		setCollectorId(in.getCollector().getId());
		setIssuedId(in.getIssuedForm().getFormType());
		setQuantity(in.getPcs());
		setBeginningNo(in.getBeginningNo());
		setEndingNo(in.getEndingNo());
		setFormTypeId(in.getFormType());
		setGroup(in.getRptGroup());
		setAmount(in.getAmount());
		setFormTypeId(in.getFormType());
		
		
		loadIssuedForm();
	}
	
	/*public void printRDC(CollectionInfo info) {
		if(CollectionInfo.isGroupLatest(info.getRptGroup(), info.getCollector().getId())) {
			System.out.println("Printing information from database");
			printForm(info);
		}else {
			System.out.println("Printing information from xml file");
			printXML(info);
		}
	}*/
	
	public void printRDC(CollectionInfo info) {
		
		String today = DateUtils.getCurrentDateYYYYMMDD();
		String dbDate = info.getReceivedDate();
		
		if(CollectionInfo.isGroupLatest(info.getRptGroup(), info.getCollector().getId())) {
			System.out.println("Printing information from database");
			if(today.equalsIgnoreCase(dbDate)) {
				printForm(info);
			}else {
				System.out.println("Printing information from xml file parin");
				printXML(info);
			}
		}else {
			System.out.println("Printing information from xml file");
			printXML(info);
		}
	}
	
	public void printXML(CollectionInfo info) {
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		String REPORT_NAME ="rcd";
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		List<Form11Report> reports = Collections.synchronizedList(new ArrayList<Form11Report>());
		Form11Report rpt = new Form11Report();
		reports.add(rpt);
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		
  		String collector = info.getCollector().getName();
		
		String value = "";
		String len = info.getRptGroup()+"";
		int size = len.length();
		if(size==1) {
			String[] date = info.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-00"+len;
		}else if(size==2) {
			String[] date = info.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-0"+len;
		}else if(size==3) {
			String[] date = info.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-"+len;
		}
  		
  		RCDReader xml = RCDReader.readXML(REPORT_PATH + "xml" + File.separator + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + File.separator  + collector +"-"+value+".xml");
  		
  		param.put("PARAM_FUND",xml.getFund());
  		param.put("PARAM_COLLECTOR_NAME",xml.getAccountablePerson());
  		param.put("PARAM_RPT_GROUP",xml.getSeriesReport());
  		
  		param.put("PARAM_PRINTED_DATE", xml.getDateCreated());
  		param.put("PARAM_VERIFIED_DATE", xml.getDateVerified());
  		param.put("PARAM_VERIFIED_PERSON", xml.getVerifierPerson());
  		param.put("PARAM_TREASURER", xml.getTreasurer());
  		
  		int cnt = 1;
  		for(RCDFormDetails d : xml.getRcdFormDtls()) {
  			param.put("PARAM_T"+cnt,d.getName());
	  		param.put("PARAM_FROM"+cnt,d.getSeriesFrom());
			param.put("PARAM_TO"+cnt,d.getSeriesTo());
			param.put("PARAM_A"+cnt,d.getAmount());
			cnt++;
  		}
		
  		cnt = 1;
		for(RCDFormSeries frm : xml.getRcdFormSeries()) {
			param.put("PARAM_F"+cnt,frm.getName());
			
	  		param.put("PARAM_BQ"+cnt,frm.getBeginningQty());
	  		param.put("PARAM_BF"+cnt,frm.getBeginningFrom());
	  		param.put("PARAM_BT"+cnt,frm.getBeginningTo());
	  		
	  		param.put("PARAM_RQ"+cnt,frm.getReceiptQty());
	  		param.put("PARAM_RF"+cnt,frm.getReceiptFrom());
	  		param.put("PARAM_RT"+cnt,frm.getReceiptTo());
	  		
	  		param.put("PARAM_IQ"+cnt,frm.getIssuedQty());
	  		param.put("PARAM_IF"+cnt,frm.getIssuedFrom());
	  		param.put("PARAM_IT"+cnt,frm.getIssuedTo());
	  		
	  		param.put("PARAM_EQ"+cnt,frm.getEndingQty());
	  		param.put("PARAM_EF"+cnt,frm.getEndingFrom());
	  		param.put("PARAM_ET"+cnt,frm.getEndingTo());
	  		cnt++;
		}
  		
  		
  		param.put("PARAM_TOTAL",xml.getAddAmount());
  		
  		//logo
		String officialLogo = path + "municipalseal.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){e.printStackTrace();}
		
		//logo
		String officialLogotrans = path + "municipalseal-transparent.png";
		try{File file = new File(officialLogotrans);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO_TRANS", off);
		}catch(Exception e){e.printStackTrace();}
  		
  		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    String pdfFile = REPORT_NAME + ".pdf";
			 //File file = new File(path + pdfFile);
			 tempPdfFile = DefaultStreamedContent.builder()
					 .contentType("application/pdf")
					 .name(pdfFile)
					 .stream(()-> this.getClass().getResourceAsStream(path + pdfFile))
					 .build();
					 
					 //new DefaultStreamedContent(new FileInputStream(file), "application/pdf", pdfFile);
		  	    
		  	    PrimeFaces pm = PrimeFaces.current();
		  	    pm.executeScript("showPdf();");
	  	}catch(Exception e){e.printStackTrace();}
  		
  		
	}
	
	@Deprecated
	private RCDReader buildSeriesData(CollectionInfo in) {
		
		Collector col = Collector.retrieve(in.getCollector().getId());
		String[] dates = in.getReceivedDate().split("-");
		
		String collector = col.getName();
		String virifiedDate = dates[1]+"/"+dates[2]+"/"+dates[0];
		
		String value = "";
		String len = in.getRptGroup()+"";
		int size = len.length();
		if(size==1) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-00"+len;
		}else if(size==2) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-0"+len;
		}else if(size==3) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-"+len;
		}
		String barangayName =  FundType.typeName(in.getFundId()); //"Brgy. " + ReadConfig.value(Bris.BARANGAY_NAME).replace("-", ", ");
		RCDReader rcd = new RCDReader();
		rcd.setBrisFile("marxmind");
		rcd.setDateCreated(DateUtils.convertDateToMonthDayYear(in.getReceivedDate()));
		rcd.setFund(barangayName);
		rcd.setAccountablePerson(collector);
		rcd.setSeriesReport(value);
		rcd.setDateVerified(virifiedDate);
		
		List<RCDFormDetails> dtls = Collections.synchronizedList(new ArrayList<RCDFormDetails>());
		List<RCDFormSeries> srs = Collections.synchronizedList(new ArrayList<RCDFormSeries>());
		
		String sql = " AND cl.isid="+in.getCollector().getId()+" AND  frm.formstatus=1 AND frm.fundid="+in.getFundId();
		String[] params = new String[0];
		
		double totalAmount = 0d;
		int cnt = 1;
		//forms with issuance
		Map<Long, IssuedForm> issuedMap = Collections.synchronizedMap(new HashMap<Long, IssuedForm>());
		//retrieve active accountable forms
		for(IssuedForm is : IssuedForm.retrieve(sql, params)) {
			System.out.println("check collector before adding to map>> " + is.getCollector().getName());
			issuedMap.put(is.getId(), is);
			
			sql = " AND cl.isid="+is.getCollector().getId()+" AND frm.fundid="+is.getFundId()+" AND frm.rptgroup="+in.getRptGroup()+" AND sud.logid="+is.getId();
			params = new String[0];
			
			//retrieve current issued form
			for(CollectionInfo i : CollectionInfo.retrieve(sql, params)) {
				
				long issuedId = i.getIssuedForm().getId();
				
				if(issuedMap!=null && issuedMap.size()>0 && issuedMap.containsKey(issuedId)) {
				issuedMap.remove(is.getId());//remove if has issuance
				
				RCDFormDetails dt = new RCDFormDetails();
				RCDFormSeries sr = new RCDFormSeries();
				issuedMap.put(i.getIssuedForm().getId(), i.getIssuedForm());
				
				totalAmount += i.getAmount();
				String start = DateUtils.numberResult(i.getFormType(), i.getBeginningNo());
				String end = DateUtils.numberResult(i.getFormType(), i.getEndingNo());
				
					Form11Report frm = reportCollectionInfo(i);
					String ctc = frm.getF1();
					
					dt.setFormId(cnt+"");
					dt.setName(i.getFormTypeName());	
					if(FormType.CT_2.getId()== i .getFormType() || FormType.CT_5.getId() == i .getFormType()) {
						dt.setSeriesFrom(Currency.formatAmount(i.getAmount()));
						dt.setSeriesTo("");
					}else {
						dt.setSeriesFrom(start);
						dt.setSeriesTo(end);
					}
					
					dt.setAmount(Currency.formatAmount(i.getAmount()));
					dtls.add(dt);
					
					sr.setId(cnt+"");
					sr.setName(ctc);
					
					sr.setBeginningQty(frm.getF2());
			  		sr.setBeginningFrom(frm.getF3());
			  		sr.setBeginningTo(frm.getF4());
			  		
			  		sr.setReceiptQty(frm.getF5());
			  		sr.setReceiptFrom(frm.getF6());
			  		sr.setReceiptTo(frm.getF7());

			  		sr.setIssuedQty(frm.getF8());
			  		sr.setIssuedFrom(frm.getF9());
			  		sr.setIssuedTo(frm.getF10());
			  		
			  		sr.setEndingQty(frm.getF11());
			  		sr.setEndingFrom(frm.getF12());
			  		sr.setEndingTo(frm.getF13());
			  		srs.add(sr);
			  		
				cnt++;
				}
			}
					
			
		}
		
		
		
		
		if(issuedMap!=null && issuedMap.size()>0 && issuedMap.get(0)!=null) {
			
			//forms without issuance on current date
			sql = " AND frm.fundid="+ in.getFundId() +" AND cl.isid="+in.getCollector().getId()+" AND frm.rptgroup<"+in.getRptGroup()+" ORDER BY frm.colid DESC limit 1";
			params = new String[0];
			for(CollectionInfo i : CollectionInfo.retrieve(sql, params)) {
				
				long issuidId = i.getIssuedForm().getId();
				
				//items with issued form but no current issuance
				if(issuedMap!=null && issuedMap.size()>0 && issuedMap.containsKey(issuidId)) {//only logid remaining will encode
				
				issuedMap.remove(issuidId);//remove form with previous issuance
				
				RCDFormSeries sr = new RCDFormSeries();
				
				Form11Report frm = reportLastCollectionInfo(i);
				if(frm!=null) {	
						String ctc = "";
						ctc = frm.getF1();
						
						sr.setId(cnt+"");
						sr.setName(ctc);
						
						sr.setBeginningQty(frm.getF2());
				  		sr.setBeginningFrom(frm.getF3());
				  		sr.setBeginningTo(frm.getF4());
				  		
				  		sr.setReceiptQty(frm.getF5());
				  		sr.setReceiptFrom(frm.getF6());
				  		sr.setReceiptTo(frm.getF7());

				  		sr.setIssuedQty(frm.getF8());
				  		sr.setIssuedFrom(frm.getF9());
				  		sr.setIssuedTo(frm.getF10());
				  		
				  		sr.setEndingQty(frm.getF11());
				  		sr.setEndingFrom(frm.getF12());
				  		sr.setEndingTo(frm.getF13());
				  		srs.add(sr);
				  		
				  		cnt++;
				}
				
				
				}//end with logform filter
			}
			
		}
		
		//retrieve not yet recorded on collectioninfo table 
		if(issuedMap!=null && issuedMap.size()>0 && issuedMap.get(0)!=null) {
			
			for(IssuedForm is : issuedMap.values()) {
				RCDFormSeries sr = new RCDFormSeries();
				
				System.out.println("reportIssued Collector>> " + is.getCollector().getName());
				
				Form11Report frm = reportIssued(is);
				String ctc = frm.getF1();
					
				sr.setId(cnt+"");
				sr.setName(ctc);
				
				sr.setBeginningQty(frm.getF2());
		  		sr.setBeginningFrom(frm.getF3());
		  		sr.setBeginningTo(frm.getF4());
		  		
		  		sr.setReceiptQty(frm.getF5());
		  		sr.setReceiptFrom(frm.getF6());
		  		sr.setReceiptTo(frm.getF7());

		  		sr.setIssuedQty(frm.getF8());
		  		sr.setIssuedFrom(frm.getF9());
		  		sr.setIssuedTo(frm.getF10());
		  		
		  		sr.setEndingQty(frm.getF11());
		  		sr.setEndingFrom(frm.getF12());
		  		sr.setEndingTo(frm.getF13());
		  		srs.add(sr);
			  		
			  		cnt++;
			}
			
		}
		
		rcd.setRcdFormDtls(dtls);
		rcd.setRcdFormSeries(srs);
		
		rcd.setBeginningBalancesAmount("0.00");
		rcd.setAddAmount(Currency.formatAmount(totalAmount));
		rcd.setLessAmount(Currency.formatAmount(totalAmount));
		rcd.setBalanceAmount("0.00");
		
		rcd.setCertificationPerson(collector);
		rcd.setVerifierPerson("HENRY E. MAGBANUA");
		rcd.setDateVerified(dates[1]+"/"+dates[2]+"/"+dates[0]);
		rcd.setTreasurer("FERDINAND L. LOPEZ");
		
		
		String XML_FOLDER = REPORT_PATH + "xml" + File.separator + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		System.out.println("XML saving path >> " + XML_FOLDER);
		RCDReader.saveXML(rcd, collector+"-"+value, XML_FOLDER);
		
		return rcd;
	}
	
	
	private RCDReader buildFormData(CollectionInfo in) {
		Collector col = Collector.retrieve(in.getCollector().getId());
		String[] dates = in.getReceivedDate().split("-");
		
		String collector = col.getName();
		String virifiedDate = dates[1]+"/"+dates[2]+"/"+dates[0];
		
		String value = "";
		String len = in.getRptGroup()+"";
		int size = len.length();
		if(size==1) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-00"+len;
		}else if(size==2) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-0"+len;
		}else if(size==3) {
			String[] date = in.getReceivedDate().split("-");
			value = date[0] +"-"+date[1] + "-"+len;
		}
		String barangayName =  FundType.typeName(in.getFundId()); //"Brgy. " + ReadConfig.value(Bris.BARANGAY_NAME).replace("-", ", ");
		RCDReader rcd = new RCDReader();
		rcd.setBrisFile("marxmind");
		rcd.setDateCreated(DateUtils.convertDateToMonthDayYear(in.getReceivedDate()));
		rcd.setFund(barangayName);
		rcd.setAccountablePerson(collector);
		rcd.setSeriesReport(value);
		rcd.setDateVerified(virifiedDate);
		
		List<RCDFormDetails> dtls = Collections.synchronizedList(new ArrayList<RCDFormDetails>());
		List<RCDFormSeries> srs = Collections.synchronizedList(new ArrayList<RCDFormSeries>());
		
		String sql = " AND frm.fundid=? AND frm.isactivecol=1 AND cl.isid=? AND frm.rptgroup=?";
		String[] params = new String[3];
		params[0] = in.getFundId()+"";
		params[1] = in.getCollector().getId()+"";
		params[2] = in.getRptGroup()+"";
		
		double totalAmount = 0d;
		int cnt = 1;
		//forms with issuance
		String tmpReceivedDate = DateUtils.getCurrentDateYYYYMMDD(); //to be use for no issuance
		Map<Long, IssuedForm> issuedMap = Collections.synchronizedMap(new HashMap<Long, IssuedForm>());
		int groupId = 0;
		for(CollectionInfo i : CollectionInfo.retrieve(sql, params)){
			RCDFormDetails dt = new RCDFormDetails();
			RCDFormSeries sr = new RCDFormSeries();
			issuedMap.put(i.getIssuedForm().getId(), i.getIssuedForm());
			
			tmpReceivedDate = i.getReceivedDate();//assigned date -- this will be use for no issuance
			groupId = i.getRptGroup();
			totalAmount += i.getAmount();
			String start = DateUtils.numberResult(i.getFormType(), i.getBeginningNo());
			String end = DateUtils.numberResult(i.getFormType(), i.getEndingNo());
			
				Form11Report frm = reportCollectionInfo(i);
				String ctc = frm.getF1();
				
				dt.setFormId(cnt+"");
				dt.setName(i.getFormTypeName());	
				if(FormType.CT_2.getId()== i .getFormType() || FormType.CT_5.getId() == i .getFormType()) {
					dt.setSeriesFrom(Currency.formatAmount(i.getAmount()));
					dt.setSeriesTo("");
				}else {
					dt.setSeriesFrom(start);
					dt.setSeriesTo(end);
				}
				
				dt.setAmount(Currency.formatAmount(i.getAmount()));
				dtls.add(dt);
				
				sr.setId(cnt+"");
				sr.setName(ctc);
				
				sr.setBeginningQty(frm.getF2());
		  		sr.setBeginningFrom(frm.getF3());
		  		sr.setBeginningTo(frm.getF4());
		  		
		  		sr.setReceiptQty(frm.getF5());
		  		sr.setReceiptFrom(frm.getF6());
		  		sr.setReceiptTo(frm.getF7());

		  		sr.setIssuedQty(frm.getF8());
		  		sr.setIssuedFrom(frm.getF9());
		  		sr.setIssuedTo(frm.getF10());
		  		
		  		sr.setEndingQty(frm.getF11());
		  		sr.setEndingFrom(frm.getF12());
		  		sr.setEndingTo(frm.getF13());
		  		srs.add(sr);
		  		
			cnt++;
			
		}
		
		//forms without issuance
		sql = " AND frm.fundid=? AND frm.isactivelog=1 AND frm.formstatus=1 AND cl.isid=?";
		params = new String[2];
		params[0] = in.getFundId()+"";
		params[1] = in.getCollector().getId()+"";
		
		if(issuedMap!=null && issuedMap.size()>0) {
			
			String[] date = tmpReceivedDate.split("-");
			int day = Integer.valueOf(date[2]);
			String dateRetrieved = date[0] + "-" + date[1] + "-" + (day<10? "0" + day  : day+"");
			
			for(IssuedForm is : IssuedForm.retrieve(sql, params)) {
				
				
				RCDFormSeries sr = new RCDFormSeries();
				
				long key = is.getId();
				if(issuedMap.containsKey(key)) {
					issuedMap.remove(key);//remove forms with issuance
				}else {
					issuedMap.put(key, is);
					
					//sql = " AND frm.fundid=? AND frm.receiveddate<? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
					sql = " AND frm.fundid=? AND frm.rptgroup<? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
					params = new String[3];
					params[0] = is.getFundId()+"";
					//params[1] = dateRetrieved;
					params[1] = groupId+"";
					params[2] = is.getId()+"";
					System.out.println("checking previous ");
					
					List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
					if(infos!=null && infos.size()>0) {
						Form11Report frm = reportLastCollectionInfo(infos.get(0));
						if(frm!=null) {	
								String ctc = "";
								ctc = frm.getF1();
								
								sr.setId(cnt+"");
								sr.setName(ctc);
								
								sr.setBeginningQty(frm.getF2());
						  		sr.setBeginningFrom(frm.getF3());
						  		sr.setBeginningTo(frm.getF4());
						  		
						  		sr.setReceiptQty(frm.getF5());
						  		sr.setReceiptFrom(frm.getF6());
						  		sr.setReceiptTo(frm.getF7());

						  		sr.setIssuedQty(frm.getF8());
						  		sr.setIssuedFrom(frm.getF9());
						  		sr.setIssuedTo(frm.getF10());
						  		
						  		sr.setEndingQty(frm.getF11());
						  		sr.setEndingFrom(frm.getF12());
						  		sr.setEndingTo(frm.getF13());
						  		srs.add(sr);
						  		
						  		cnt++;
						}
					}else {
						Form11Report frm = reportIssued(is);
						
						String ctc = frm.getF1();
							
						sr.setId(cnt+"");
						sr.setName(ctc);
						
						sr.setBeginningQty(frm.getF2());
				  		sr.setBeginningFrom(frm.getF3());
				  		sr.setBeginningTo(frm.getF4());
				  		
				  		sr.setReceiptQty(frm.getF5());
				  		sr.setReceiptFrom(frm.getF6());
				  		sr.setReceiptTo(frm.getF7());

				  		sr.setIssuedQty(frm.getF8());
				  		sr.setIssuedFrom(frm.getF9());
				  		sr.setIssuedTo(frm.getF10());
				  		
				  		sr.setEndingQty(frm.getF11());
				  		sr.setEndingFrom(frm.getF12());
				  		sr.setEndingTo(frm.getF13());
				  		srs.add(sr);
					  		
					  		cnt++;
					}
					
				}
			}
			
		}else {
			
			for(IssuedForm notissued : IssuedForm.retrieve(sql, params)) {
				RCDFormSeries sr = new RCDFormSeries();
				Form11Report frm = reportIssued(notissued);
				String ctc = frm.getF1();
					
				sr.setId(cnt+"");
				sr.setName(ctc);
				
				sr.setBeginningQty(frm.getF2());
		  		sr.setBeginningFrom(frm.getF3());
		  		sr.setBeginningTo(frm.getF4());
		  		
		  		sr.setReceiptQty(frm.getF5());
		  		sr.setReceiptFrom(frm.getF6());
		  		sr.setReceiptTo(frm.getF7());

		  		sr.setIssuedQty(frm.getF8());
		  		sr.setIssuedFrom(frm.getF9());
		  		sr.setIssuedTo(frm.getF10());
		  		
		  		sr.setEndingQty(frm.getF11());
		  		sr.setEndingFrom(frm.getF12());
		  		sr.setEndingTo(frm.getF13());
		  		srs.add(sr);
			  		
			  		cnt++;
				
			}
		
		}
		
		rcd.setRcdFormDtls(dtls);
		rcd.setRcdFormSeries(srs);
		
		rcd.setBeginningBalancesAmount("0.00");
		rcd.setAddAmount(Currency.formatAmount(totalAmount));
		rcd.setLessAmount(Currency.formatAmount(totalAmount));
		rcd.setBalanceAmount("0.00");
		
		rcd.setCertificationPerson(collector);
		rcd.setVerifierPerson("HENRY E. MAGBANUA");
		rcd.setDateVerified(dates[1]+"/"+dates[2]+"/"+dates[0]);
		rcd.setTreasurer("FERDINAND L. LOPEZ");
		
		
		String XML_FOLDER = REPORT_PATH + "xml" + File.separator + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		System.out.println("XML saving path >> " + XML_FOLDER);
		RCDReader.saveXML(rcd, collector+"-"+value, XML_FOLDER);
		
		return rcd;
	}
	
	
	public void printForm(CollectionInfo in) {
		
		RCDReader rcd = buildFormData(in);
		//RCDReader rcd = buildSeriesData(in);
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		String REPORT_NAME ="rcd";
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		List<Form11Report> reports = Collections.synchronizedList(new ArrayList<Form11Report>());
		Form11Report rpt = new Form11Report();
		reports.add(rpt);
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		param.put("PARAM_FUND",rcd.getFund());
  		param.put("PARAM_COLLECTOR_NAME",rcd.getAccountablePerson());
  		param.put("PARAM_PRINTED_DATE", rcd.getDateCreated());
  		param.put("PARAM_VERIFIED_DATE", rcd.getDateVerified());
  		param.put("PARAM_VERIFIED_PERSON", rcd.getVerifierPerson());
  		param.put("PARAM_TREASURER", rcd.getTreasurer());
  		param.put("PARAM_RPT_GROUP",rcd.getSeriesReport());
  		param.put("PARAM_TOTAL",rcd.getAddAmount());
  		
  		int cnt = 1;
  		for(RCDFormDetails d : rcd.getRcdFormDtls()) {
  			param.put("PARAM_T"+cnt,d.getName());
  			param.put("PARAM_FROM"+cnt,d.getSeriesFrom());
			param.put("PARAM_TO"+cnt,d.getSeriesTo());
			param.put("PARAM_A"+cnt,d.getAmount());
			cnt++;
  		}
  		
  		cnt = 1;
  		for(RCDFormSeries s : rcd.getRcdFormSeries()) {
  			param.put("PARAM_F"+cnt,s.getName());
  			
	  		param.put("PARAM_BQ"+cnt,s.getBeginningQty());
	  		param.put("PARAM_BF"+cnt,s.getBeginningFrom());
	  		param.put("PARAM_BT"+cnt,s.getBeginningTo());
	  		
	  		param.put("PARAM_RQ"+cnt,s.getReceiptQty());
	  		param.put("PARAM_RF"+cnt,s.getReceiptFrom());
	  		param.put("PARAM_RT"+cnt,s.getReceiptTo());
	  		
	  		param.put("PARAM_IQ"+cnt,s.getIssuedQty());
	  		param.put("PARAM_IF"+cnt,s.getIssuedFrom());
	  		param.put("PARAM_IT"+cnt,s.getIssuedTo());
	  		
	  		param.put("PARAM_EQ"+cnt,s.getEndingQty());
	  		param.put("PARAM_EF"+cnt,s.getEndingFrom());
	  		param.put("PARAM_ET"+cnt,s.getEndingTo());
	  		
		cnt++;
  		}
  		
  		
  		
  		//logo
		String officialLogo = path + "municipalseal.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){e.printStackTrace();}
		
		//logo
		String officialLogotrans = path + "municipalseal-transparent.png";
		try{File file = new File(officialLogotrans);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO_TRANS", off);
		}catch(Exception e){e.printStackTrace();}
  		
  		try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    
	  	    String pdfFile = REPORT_NAME + ".pdf";
			// File file = new File(path + pdfFile);
			 tempPdfFile = DefaultStreamedContent.builder()
					 .contentType("application/pdf")
					 .name(pdfFile)
					 .stream(()-> this.getClass().getResourceAsStream(path + pdfFile))
					 .build();
					 
					 //new DefaultStreamedContent(new FileInputStream(file), "application/pdf", pdfFile);
		  	    
		  	    PrimeFaces pm = PrimeFaces.current();
		  	    pm.executeScript("showPdf();");
	  	}catch(Exception e){e.printStackTrace();}
			
	  		
	}
	
	
	/*public void printXML() {
		String REPORT_NAME ="rcd";
		
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		List<Form11Report> reports = Collections.synchronizedList(new ArrayList<Form11Report>());
		Form11Report rpt = new Form11Report();
		reports.add(rpt);
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		
  		String xmlFile = REPORT_PATH  + "xml" + Bris.SEPERATOR.getName() + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
  		
  		RCDReader xml = RCDReader.readXML(xmlFile +"rcd-2019-06-001-1.xml");
  		
  		param.put("PARAM_FUND",xml.getFund());
  		param.put("PARAM_COLLECTOR_NAME", xml.getAccountablePerson());
  		param.put("PARAM_RPT_GROUP",xml.getSeriesReport());
  		param.put("PARAM_TOTAL",xml.getAddAmount());
  		
  		int cnt=1;
  		for(RCDFormDetails i : xml.getRcdFormDtls()) {
	  		param.put("PARAM_T"+cnt,i.getName());
	  		param.put("PARAM_FROM"+cnt,i.getSeriesFrom());
			param.put("PARAM_TO"+cnt,i.getSeriesTo());
			param.put("PARAM_A"+cnt,i.getAmount());
			cnt++;
  		}
		
		cnt = 1;
		for(RCDFormSeries frm : xml.getRcdFormSeries()) {
			param.put("PARAM_F"+cnt,frm.getName());
	  		param.put("PARAM_BQ"+cnt,frm.getBeginningQty());
	  		param.put("PARAM_BF"+cnt,frm.getBeginningFrom());
	  		param.put("PARAM_BT"+cnt,frm.getBeginningTo());
	  		param.put("PARAM_RQ"+cnt,frm.getReceiptQty());
	  		param.put("PARAM_RF"+cnt,frm.getReceiptFrom());
	  		param.put("PARAM_RT"+cnt,frm.getReceiptTo());
	  		param.put("PARAM_IQ"+cnt,frm.getIssuedQty());
	  		param.put("PARAM_IF"+cnt,frm.getIssuedFrom());
	  		param.put("PARAM_IT"+cnt,frm.getIssuedTo());
	  		param.put("PARAM_EQ"+cnt,frm.getEndingQty());
	  		param.put("PARAM_EF"+cnt,frm.getEndingFrom());
	  		param.put("PARAM_ET"+cnt,frm.getEndingTo());
	  		cnt++;
		}
  		
  		
  		
  		
  		
  		
  		
  		//logo
		String officialLogo = path + "municipalseal.png";
		try{File file = new File(officialLogo);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO", off);
		}catch(Exception e){e.printStackTrace();}
		
		//logo
		String officialLogotrans = path + "barangaylogotrans.png";
		try{File file = new File(officialLogotrans);
		FileInputStream off = new FileInputStream(file);
		param.put("PARAM_LOGO_TRANS", off);
		}catch(Exception e){e.printStackTrace();}
  		
  			try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
	  	    
	  	    String pdfFile = REPORT_NAME + ".pdf";
  			 File file = new File(path + pdfFile);
  			 tempPdfFile = new DefaultStreamedContent(new FileInputStream(file), "application/pdf", pdfFile);
  		  	    
  		  	    PrimeFaces pm = PrimeFaces.current();
  		  	    pm.executeScript("PF('multiDialogPdf').show();");
	  	    
	  		}catch(Exception e){e.printStackTrace();}
	}*/
	
	/*public void printReport(CollectionInfo in) {
				String REPORT_NAME ="rcd";
				System.out.println("Report path " + REPORT_PATH + " name " + REPORT_NAME);
				
				//String REPORT_NAME = GlobalReportHandler.report(GlobalReport.TRANSMITAL_LETTER);
				String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
				
				ReportCompiler compiler = new ReportCompiler();
				String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
				
				List<Form11Report> reports = Collections.synchronizedList(new ArrayList<Form11Report>());
				Form11Report rpt = new Form11Report();
				reports.add(rpt);
				JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		  		HashMap param = new HashMap();
				
		  		
		  		String sql = " AND frm.isactivecol=1 AND cl.isid=? AND frm.rptgroup=?";
				String[] params = new String[2];
				params[0] = in.getCollector().getId()+"";
				params[1] = in.getRptGroup()+"";
				
				double totalAmount = 0d;
				int cnt = 1;
				//forms with issuance
				String tmpReceivedDate = DateUtils.getCurrentDateYYYYMMDD(); //to be use for no issuance
				Map<Long, IssuedForm> issuedMap = Collections.synchronizedMap(new HashMap<Long, IssuedForm>());
				for(CollectionInfo i : CollectionInfo.retrieve(sql, params)){
					issuedMap.put(i.getIssuedForm().getId(), i.getIssuedForm());
					
					tmpReceivedDate = i.getReceivedDate();//assigned date -- this will be use for no issuance
					
					totalAmount += i.getAmount();
					String start = DateUtils.numberResult(i.getFormType(), i.getBeginningNo());
					String end = DateUtils.numberResult(i.getFormType(), i.getEndingNo());
					
					Form11Report frm = reportCollectionInfo(i);
					String ctc = frm.getF1().replace("IND.", "");
				       ctc = ctc.replace("CORP.", "");
					String ctc = frm.getF1();
					
					
					
						param.put("PARAM_T"+cnt,i.getFormTypeName());
						if(FormType.CT_2.getId()== i .getFormType() || FormType.CT_5.getId() == i .getFormType()) {
							param.put("PARAM_FROM"+cnt,Currency.formatAmount(i.getAmount()));
							param.put("PARAM_TO"+cnt,"");
						}else {
							param.put("PARAM_FROM"+cnt,start);
							param.put("PARAM_TO"+cnt,end);
						}
						
						
						
						param.put("PARAM_A"+cnt,Currency.formatAmount(i.getAmount()));
						param.put("PARAM_F"+cnt,ctc);
				  		param.put("PARAM_BQ"+cnt,frm.getF2());
				  		param.put("PARAM_BF"+cnt,frm.getF3());
				  		param.put("PARAM_BT"+cnt,frm.getF4());
				  		param.put("PARAM_RQ"+cnt,frm.getF5());
				  		param.put("PARAM_RF"+cnt,frm.getF6());
				  		param.put("PARAM_RT"+cnt,frm.getF7());
				  		param.put("PARAM_IQ"+cnt,frm.getF8());
				  		param.put("PARAM_IF"+cnt,frm.getF9());
				  		param.put("PARAM_IT"+cnt,frm.getF10());
				  		param.put("PARAM_EQ"+cnt,frm.getF11());
				  		param.put("PARAM_EF"+cnt,frm.getF12());
				  		param.put("PARAM_ET"+cnt,frm.getF13());
					
					
					
					cnt++;
					
				}
				
				//forms without issuance
				sql = " AND frm.isactivelog=1 AND frm.formstatus=1 AND cl.isid=?";
				params = new String[1];
				params[0] = getCollectorMapId()+"";
				
				if(issuedMap!=null && issuedMap.size()>0) {
					
					String[] date = tmpReceivedDate.split("-");
					int day = Integer.valueOf(date[2]);
					String dateRetrieved = date[0] + "-" + date[1] + "-" + (day<10? "0" + day  : day+"");
					
					for(IssuedForm is : IssuedForm.retrieve(sql, params)) {
						long key = is.getId();
						if(issuedMap.containsKey(key)) {
							issuedMap.remove(key);//remove forms with issuance
						}else {
							issuedMap.put(key, is);
							
							sql = " AND frm.receiveddate<? AND sud.logid=? ORDER BY frm.colid DESC limit 1";
							params = new String[2];
							params[0] = dateRetrieved;
							params[1] = is.getId()+"";
							System.out.println("checking previous ");
							
							List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
							if(infos!=null && infos.size()>0) {
								Form11Report frm = reportLastCollectionInfo(infos.get(0));
								if(frm!=null) {	
										String ctc = "";
									   try{ctc = frm.getF1().replace("IND.", "");
								       ctc = ctc.replace("CORP.", "");}catch(NullPointerException e) {}
										ctc = frm.getF1();
										
										param.put("PARAM_F"+cnt,ctc);
								  		param.put("PARAM_BQ"+cnt,frm.getF2());
								  		param.put("PARAM_BF"+cnt,frm.getF3());
								  		param.put("PARAM_BT"+cnt,frm.getF4());
								  		param.put("PARAM_RQ"+cnt,frm.getF5());
								  		param.put("PARAM_RF"+cnt,frm.getF6());
								  		param.put("PARAM_RT"+cnt,frm.getF7());
								  		param.put("PARAM_IQ"+cnt,frm.getF8());
								  		param.put("PARAM_IF"+cnt,frm.getF9());
								  		param.put("PARAM_IT"+cnt,frm.getF10());
								  		param.put("PARAM_EQ"+cnt,frm.getF11());
								  		param.put("PARAM_EF"+cnt,frm.getF12());
								  		param.put("PARAM_ET"+cnt,frm.getF13());
									
								  		cnt++;
								}
							}else {
								Form11Report frm = reportIssued(is);
								
								String ctc = frm.getF1();
									
									param.put("PARAM_F"+cnt,ctc);
							  		param.put("PARAM_BQ"+cnt,frm.getF2());
							  		param.put("PARAM_BF"+cnt,frm.getF3());
							  		param.put("PARAM_BT"+cnt,frm.getF4());
							  		param.put("PARAM_RQ"+cnt,frm.getF5());
							  		param.put("PARAM_RF"+cnt,frm.getF6());
							  		param.put("PARAM_RT"+cnt,frm.getF7());
							  		param.put("PARAM_IQ"+cnt,frm.getF8());
							  		param.put("PARAM_IF"+cnt,frm.getF9());
							  		param.put("PARAM_IT"+cnt,frm.getF10());
							  		param.put("PARAM_EQ"+cnt,frm.getF11());
							  		param.put("PARAM_EF"+cnt,frm.getF12());
							  		param.put("PARAM_ET"+cnt,frm.getF13());
								
							  		cnt++;
							}
							
						}
					}
					
				}else {
					
					for(IssuedForm notissued : IssuedForm.retrieve(sql, params)) {
						
						Form11Report frm = reportIssued(notissued);
						String ctc = frm.getF1();
							
							param.put("PARAM_F"+cnt,ctc);
					  		param.put("PARAM_BQ"+cnt,frm.getF2());
					  		param.put("PARAM_BF"+cnt,frm.getF3());
					  		param.put("PARAM_BT"+cnt,frm.getF4());
					  		param.put("PARAM_RQ"+cnt,frm.getF5());
					  		param.put("PARAM_RF"+cnt,frm.getF6());
					  		param.put("PARAM_RT"+cnt,frm.getF7());
					  		param.put("PARAM_IQ"+cnt,frm.getF8());
					  		param.put("PARAM_IF"+cnt,frm.getF9());
					  		param.put("PARAM_IT"+cnt,frm.getF10());
					  		param.put("PARAM_EQ"+cnt,frm.getF11());
					  		param.put("PARAM_EF"+cnt,frm.getF12());
					  		param.put("PARAM_ET"+cnt,frm.getF13());
						
					  		cnt++;
						
					}
				
				}
		  		
				//if(getCollectorMapId()>0) {
					Collector collector = Collector.retrieve(in.getCollector().getId());
					param.put("PARAM_COLLECTOR_NAME", collector.getName());//getCollectotData().get(in.getCollector().getId()).getName());
				}else {
					param.put("PARAM_COLLECTOR_NAME","All Collectors");
				}
		  		
		  		
		  		String value = "";
				String len = in.getRptGroup()+"";
				int size = len.length();
				if(size==1) {
					String[] date = in.getReceivedDate().split("-");
					value = date[0] +"-"+date[1] + "-00"+len;
				}else if(size==2) {
					String[] date = in.getReceivedDate().split("-");
					value = date[0] +"-"+date[1] + "-0"+len;
				}else if(size==3) {
					String[] date = in.getReceivedDate().split("-");
					value = date[0] +"-"+date[1] + "-"+len;
				}
		  		
		  		param.put("PARAM_RPT_GROUP",value);
		  		
		  		param.put("PARAM_TOTAL",Currency.formatAmount(totalAmount));
		  		
		  		String barangayName = "Brgy. " + ReadConfig.value(Bris.BARANGAY_NAME).replace("-", ", ");
		  		param.put("PARAM_FUND",barangayName);
		  		
		  		//logo
				String officialLogo = path + "municipalseal.png";
				try{File file = new File(officialLogo);
				FileInputStream off = new FileInputStream(file);
				param.put("PARAM_LOGO", off);
				}catch(Exception e){e.printStackTrace();}
				
				//logo
				String officialLogotrans = path + "barangaylogotrans.png";
				try{File file = new File(officialLogotrans);
				FileInputStream off = new FileInputStream(file);
				param.put("PARAM_LOGO_TRANS", off);
				}catch(Exception e){e.printStackTrace();}
		  		
		  			try{
			  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
			  	    JasperExportManager.exportReportToPdfFile(jrprint,path+ REPORT_NAME +".pdf");
			  	    
			  	    String pdfFile = REPORT_NAME + ".pdf";
		  			 File file = new File(path + pdfFile);
		  			 tempPdfFile = new DefaultStreamedContent(new FileInputStream(file), "application/pdf", pdfFile);
		  		  	    
		  		  	    PrimeFaces pm = PrimeFaces.current();
		  		  	    pm.executeScript("PF('multiDialogPdf').show();");
			  	    
			  		}catch(Exception e){e.printStackTrace();}
					
	}*/
	
	public Form11Report reportCollectionInfo(CollectionInfo info){

		Form11Report rpt = new Form11Report();
		
		rpt.setF1(FormType.nameId(info.getFormType()));
		
		int logmonth = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[1]);
		int logDay = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[2]);
		
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			if(info.getPrevPcs()==49) {
				rpt.setF5("50");
				f6 = be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
				f7 = en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				
			}else {
				/*int qty = info.getPrevPcs()+1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF5(qty+"");
				rpt.setF2(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				f6 = be2==7? "0"+be1 : be1;
				
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF6("");
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
				f7 = en2==7? "0"+en1 : en1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF7("");*/
				
				/*int qty = info.getPrevPcs()+1;
				rpt.setF5(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				
				f6 = be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
			
				f7 = en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));*/
				
				int qty = info.getPrevPcs()+1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF5(qty+"");
				rpt.setF2(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				f6 = be2==7? "0"+be1 : be1;
				
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF6(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f6)));
				rpt.setF6("");
				
				String en1= info.getIssuedForm().getEndingNo()+"";
				int en2 = en1.length();
				f7 = en2==7? "0"+en1 : en1;
				//temporary removed if qty not equal to 50 place it to beginning
				//rpt.setF7(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f7)));
				rpt.setF7("");
				
			}
			
			
			
			
		}else {
		//Write in beginning balance
			String be1= info.getBeginningNo()+"";
			int be2 = be1.length();
			
			if(info.getPrevPcs()==49) {
				rpt.setF2("50");
				f3 = be2==7? "0"+be1 : be1; 
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
			}else {
				
				/*String sql = " AND sud.logid=?";
				String params[]= new String[1];
				params[0]= info.getIssuedForm().getId()+"";
				List<CollectionInfo> infos = CollectionInfo.retrieve(sql, params);
				
				if(infos!=null && infos.size()>1) {
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo() - info.getPrevPcs();
					be1= begNo+"";
					be2 = be1.length();
					f3 = be2==7? "0"+be1 : be1; 
					rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				}else {//correction for those who has a beginning quantity not equal to 49
					int qty = info.getPrevPcs()+1;
					rpt.setF2(qty+"");
					
					long begNo = info.getBeginningNo();
					be1= begNo+"";
					be2 = be1.length();
					f3 = be2==7? "0"+be1 : be1; 
					rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				}*/
				System.out.println("Process on the new changes>>>>>");
				int qty = info.getPrevPcs()+1;
				rpt.setF2(qty+"");
				
				long begNo = info.getBeginningNo();
				be1= begNo+"";
				be2 = be1.length();
				
				f3 = be2==7? "0"+be1 : be1; 
				rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
				
			}
			
			
			String en1= info.getIssuedForm().getEndingNo()+"";
			int en2 = en1.length();
			f4 = en2==7? "0"+en1 : en1;
			rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f4)));
		
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		//issued
		rpt.setF8(info.getPcs()+"");
		
		String beg1= info.getBeginningNo()+"";
		int beg2 = beg1.length();
		f9 = beg2==7? "0"+beg1 : beg1;
		rpt.setF9(DateUtils.numberResult(info.getFormType(), Long.valueOf(f9)));
		
		//String en1= info.getIssuedForm().getEndingNo()+"";
		String en1= info.getEndingNo()+"";
		int en2 = en1.length();
		f10 = en2==7? "0"+en1 : en1;
		rpt.setF10(DateUtils.numberResult(info.getFormType(), Long.valueOf(f10)));
		
		
		//ending balance
		
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		
		if(endingQty==0) {
			rpt.setF11("");
			rpt.setF12("All Issued");
			rpt.setF13("");
		}else {
			rpt.setF11(endingQty+"");
			long enNumber = info.getEndingNo() + 1;
			String enbeg1= enNumber+"";
			int enbeg2 = enbeg1.length();
			f12 = enbeg2==7? "0"+enbeg1 : enbeg1;
			rpt.setF12(DateUtils.numberResult(info.getFormType(), Long.valueOf(f12)));
			
			String enen1= info.getIssuedForm().getEndingNo()+"";
			int enen2 = enen1.length();
			f13 = enen2==7? "0"+enen1 : enen1;
			rpt.setF13(DateUtils.numberResult(info.getFormType(), Long.valueOf(f13)));
		}
		//remarks
		rpt.setF14("");
		
		//change the value if the form is Cash ticket
		if(FormType.CT_2.getId()==info.getFormType() || FormType.CT_5.getId()==info.getFormType()) {
			
			rpt.setF1(FormType.nameId(info.getFormType()));
			String allIssued = info.getBeginningNo()==0? "All Issued" : "";
			double amount = 0d;
			
			if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
				
				amount = info.getBeginningNo() + info.getAmount();
				
				if(amount==info.getEndingNo()) {
					//beginning
					rpt.setF2("");
					rpt.setF3("");
					rpt.setF4("");
					 
					//Receipt
					rpt.setF5("");
					rpt.setF6(Currency.formatAmount(amount));
					rpt.setF7("");
				}else {
					//beginning
					rpt.setF2("");
					rpt.setF3(Currency.formatAmount(amount));
					rpt.setF4("");
					 
					//Receipt
					rpt.setF5("");
					rpt.setF6("");
					rpt.setF7("");
				}
				
				//issued
				rpt.setF8("");
				rpt.setF9(Currency.formatAmount(info.getAmount()));
				rpt.setF10("");
				
				//ending balance
				rpt.setF11("");
				if(info.getBeginningNo()>0) {
					rpt.setF12(Currency.formatAmount(info.getBeginningNo()));
				}else {
					rpt.setF12(allIssued);
				}
				rpt.setF13("");
			}else {
				
				amount = info.getBeginningNo() + info.getAmount();
				
				//beginning
				rpt.setF2("");
				rpt.setF3(Currency.formatAmount(amount));
				rpt.setF4("");
				
				//Receipt
				rpt.setF5("");
				rpt.setF6("");
				rpt.setF7("");
				
				//issued
				rpt.setF8("");
				rpt.setF9(Currency.formatAmount(info.getAmount()));
				rpt.setF10("");
				
				//ending balance
				rpt.setF11("");
				if(info.getBeginningNo()>0) {
					rpt.setF12(Currency.formatAmount(info.getBeginningNo()));
				}else {
					rpt.setF12(allIssued);
				}
				rpt.setF13("");
			}
				
				
				//remarks
				rpt.setF14("");
			//}
		}
		
		return rpt;
	}
	
	public Form11Report reportLastCollectionInfo(CollectionInfo info){

		Form11Report rpt = null;
		
		long endingQty = info.getIssuedForm().getEndingNo() - info.getEndingNo();
		
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if(endingQty>0) {
		rpt = new Form11Report();	
		rpt.setF1(FormType.nameId(info.getFormType()));
		
		rpt.setF2(endingQty+"");
		
		long endCount = info.getEndingNo() + 1;
		
		String enbeg1= endCount+"";
		int enbeg2 = enbeg1.length();
		
		f3 = enbeg2==7? "0"+enbeg1 : enbeg1;
		rpt.setF3(DateUtils.numberResult(info.getFormType(), Long.valueOf(f3)));
		
		String enen1= info.getIssuedForm().getEndingNo()+"";
		int enen2 = enen1.length();
		
		f4 = enen2==7? "0"+enen1 : enen1;
		rpt.setF4(DateUtils.numberResult(info.getFormType(), Long.valueOf(f4)));
		//rpt.setF4(enen2==7? "0"+enen1 : enen1);

		//Receipt
		rpt.setF5("");
		rpt.setF6("");
		rpt.setF7("");
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Iss.");
		rpt.setF10("");
		
		
		//ending balance
		rpt.setF11(endingQty+"");
		
		f12 = enbeg2==7? "0"+enbeg1 : enbeg1;
		f13 = enen2==7? "0"+enen1 : enen1;
		rpt.setF12(DateUtils.numberResult(info.getFormType(), Long.valueOf(f12)));
		rpt.setF13(DateUtils.numberResult(info.getFormType(), Long.valueOf(f13)));
		//rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		//rpt.setF13(enen2==7? "0"+enen1 : enen1);
		
		//remarks
		rpt.setF14("");
		
		//change the value if the form is Cash ticket
		if(FormType.CT_2.getId()==info.getFormType() || FormType.CT_5.getId()==info.getFormType()) {
			if(info.getAmount()>0) {
				int logmonth = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[1]);
				int logDay = Integer.valueOf(info.getIssuedForm().getIssuedDate().split("-")[2]);
				
				rpt.setF1(FormType.nameId(info.getFormType()));
				if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
					//beginning
					rpt.setF2("");
					rpt.setF3("");
					rpt.setF4("");
					
					//Receipt
					rpt.setF5("");
					rpt.setF6(Currency.formatAmount(info.getAmount()));
					rpt.setF7("");
				}else {
					//beginning
					rpt.setF2("");
					rpt.setF3(Currency.formatAmount(info.getAmount()));
					rpt.setF4("");
					
					//Receipt
					rpt.setF5("");
					rpt.setF6("");
					rpt.setF7("");
				}
				
				
				//issued
				rpt.setF8("");
				rpt.setF9(Currency.formatAmount(info.getAmount()));
				rpt.setF10("");
				
				String allIssued = info.getBeginningNo()==0? "All Issued" : "";
				//ending balance
				rpt.setF11("");
				rpt.setF12(allIssued);
				rpt.setF13("");
				
				//remarks
				rpt.setF14("");
			}
		}
		
		}
		
		return rpt;
	}
	
	public Form11Report reportIssued(IssuedForm isform) {
		
		Form11Report rpt = new Form11Report();
		
		String f3 = "";
		String f4 = "";
		String f6 = "";
		String f7 = "";
		String f9 = "";
		String f10 = "";
		String f12 = "";
		String f13 = "";
		
		if("Stock".equalsIgnoreCase(isform.getCollector().getName())) {
			rpt.setF1(FormType.nameId(isform.getFormType()));
			
			int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
			
			if(logmonth==getMonthId()) {
				
				rpt.setF2("");
				rpt.setF3("");
				rpt.setF4("");
				
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF5(isform.getPcs()+"");
				
				f6= be2==7? "0"+be1 : be1;
				rpt.setF6(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f6)));
				//rpt.setF6(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				f7= en2==7? "0"+en1 : en1;
				rpt.setF7(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f7)));
				//rpt.setF7(en2==7? "0"+en1 : en1);
				
			}else {
			
				String be1= isform.getBeginningNo()+"";
				int be2 = be1.length();
				rpt.setF2(isform.getPcs()+"");
				
				f3= be2==7? "0"+be1 : be1;
				rpt.setF3(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f3)));
				//rpt.setF3(be2==7? "0"+be1 : be1);
				
				String en1= isform.getEndingNo()+"";
				int en2 = en1.length();
				rpt.setF4(en2==7? "0"+en1 : en1);
				
				rpt.setF5("");
				rpt.setF6("");
				rpt.setF7("");
			
			}
			
			//issued
			rpt.setF8("");
			rpt.setF9("");
			rpt.setF10("");
			
			
			//ending balance
			
			
			rpt.setF11("");
			rpt.setF12("");
			rpt.setF13("");
			
			//remarks
			rpt.setF14("");
			
			Collector col = Collector.retrieve(isform.getCollector().getId());
			/*if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
				rpt.setF15(col.getName());
			}else {*/
				rpt.setF15(col.getDepartment().getDepartmentName());
			//}
			
		}else {
		
		rpt.setF1(FormType.nameId(isform.getFormType()));
		
		int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
		int logDay = Integer.valueOf(isform.getIssuedDate().split("-")[2]);
		
		if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
			//write in receipt
			rpt.setF2("");
			rpt.setF3("");
			rpt.setF4("");
			
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF5(isform.getPcs()+"");
			
			f6= be2==7? "0"+be1 : be1;
			rpt.setF6(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f6)));
			//rpt.setF6(be2==7? "0"+be1 : be1);
				
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			
			f7= en2==7? "0"+en1 : en1;
			rpt.setF7(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f7)));
			//rpt.setF7(en2==7? "0"+en1 : en1);
		
			
			
		}else {
		//Write in beginning balance
			String be1= isform.getBeginningNo()+"";
			int be2 = be1.length();
			rpt.setF2(isform.getPcs()+"");
			
			f3= be2==7? "0"+be1 : be1;
			rpt.setF3(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f3)));
			//rpt.setF3(be2==7? "0"+be1 : be1);
			
			String en1= isform.getEndingNo()+"";
			int en2 = en1.length();
			f4= en2==7? "0"+en1 : en1;
			rpt.setF4(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f4)));
			//rpt.setF4(en2==7? "0"+en1 : en1);
			
			rpt.setF5("");
			rpt.setF6("");
			rpt.setF7("");
		}
		
		//issued
		rpt.setF8("");
		rpt.setF9("No Iss.");
		rpt.setF10("");
		
		
		//ending balance
		
		
		rpt.setF11(isform.getPcs()+"");
		
		String enbeg1= isform.getBeginningNo()+"";
		int enbeg2 = enbeg1.length();
		f12= enbeg2==7? "0"+enbeg1 : enbeg1;
		rpt.setF12(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f12)));
		//rpt.setF12(enbeg2==7? "0"+enbeg1 : enbeg1);
		
		String enending1= isform.getEndingNo()+"";
		int enending2 = enending1.length();
		f13= enending2==7? "0"+enending1 : enending1;
		rpt.setF13(DateUtils.numberResult(isform.getFormType(), Long.valueOf(f13)));
		//rpt.setF13(enending2==7? "0"+enending1 : enending1);
		
		//remarks
		rpt.setF14("");
		
		Collector col = Collector.retrieve(isform.getCollector().getId());
		/*if(col.getDepartment().getCode().equalsIgnoreCase("1091")) {
			rpt.setF15(col.getName());
		}else {*/
			rpt.setF15(col.getDepartment().getDepartmentName());
		//}
		
		}
		
		//change the value if the form is Cash ticket
if(FormType.CT_2.getId()==isform.getFormType() || FormType.CT_5.getId()==isform.getFormType()) {
			
			int logmonth = Integer.valueOf(isform.getIssuedDate().split("-")[1]);
			int logDay = Integer.valueOf(isform.getIssuedDate().split("-")[2]);
			
						rpt.setF1(FormType.nameId(isform.getFormType()));
						
						
						double amount = 0d;
						if(FormType.CT_2.getId()==isform.getFormType()) {
							amount = isform.getPcs() * 2;
						}else if(FormType.CT_5.getId()==isform.getFormType()) {
							amount = isform.getPcs() * 5;
						}
						
						if(logmonth==getMonthId() && logDay == DateUtils.getCurrentDay()) {
							//beginning
							rpt.setF2("");
							rpt.setF3("");
							rpt.setF4("");
							
							//Receipt
							rpt.setF5("");
							rpt.setF6(Currency.formatAmount(amount));
							rpt.setF7("");
						}else {
							//beginning
							rpt.setF2("");
							rpt.setF3(Currency.formatAmount(amount));
							rpt.setF4("");
							
							//Receipt
							rpt.setF5("");
							rpt.setF6("");
							rpt.setF7("");
						}
						
						
						//issued
						rpt.setF8("");
						rpt.setF9("No Iss.");
						rpt.setF10("");
						
						
						//ending balance
						rpt.setF11("");
						rpt.setF12(Currency.formatAmount(amount));
						rpt.setF13("");
						
						//remarks
						rpt.setF14("");
		}
		
		return rpt;
	
	}
	
	public void deleteRow(CollectionInfo in) {
		
		
			in.delete();
			newForms.remove(in);
			Application.addMessage(1, "Success", "Successfully deleted");
		
	}
	
	public Date getReceivedDate() {
		if(receivedDate==null) {
			receivedDate = DateUtils.getDateToday();
		}
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	
	public int getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(int collectorId) {
		this.collectorId = collectorId;
	}

	public List getCollectors() {
		collectors = new ArrayList<>();
		collectors.add(new SelectItem(0, "Select Collector"));
		for(Collector col : Collector.retrieve("", new String[0])) {
			if(col.getId()!=0) {
				collectors.add(new SelectItem(col.getId(), col.getName()));
			}
		}
		
		return collectors;
	}

	public void setCollectors(List collectors) {
		this.collectors = collectors;
	}
	
	public int getFormTypeId() {
		if(formTypeId==0) {
			formTypeId=1;
		}
		return formTypeId;
	}
	public void setFormTypeId(int formTypeId) {
		this.formTypeId = formTypeId;
	}
	public List getFormTypes() {
		formTypes = new ArrayList<>();
		
		for(FormType form : FormType.values()) {
			formTypes.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formTypes;
	}
	public void setFormTypes(List formTypes) {
		this.formTypes = formTypes;
	}
	public long getIssuedId() {
		return issuedId;
	}

	public void setIssuedId(long issuedId) {
		this.issuedId = issuedId;
	}

	public List getIssueds() {
		return issueds;
	}

	public void setIssueds(List issueds) {
		this.issueds = issueds;
	}

	public long getBeginningNo() {
		return beginningNo;
	}

	public void setBeginningNo(long beginningNo) {
		this.beginningNo = beginningNo;
	}

	public long getEndingNo() {
		return endingNo;
	}

	public void setEndingNo(long endingNo) {
		this.endingNo = endingNo;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTmpQty() {
		return tmpQty;
	}

	public void setTmpQty(int tmpQty) {
		this.tmpQty = tmpQty;
	}

	public List<CollectionInfo> getNewForms() {
		return newForms;
	}

	public void setNewForms(List<CollectionInfo> newForms) {
		this.newForms = newForms;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public IssuedForm getIssuedData() {
		return issuedData;
	}

	public void setIssuedData(IssuedForm issuedData) {
		this.issuedData = issuedData;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}


	public Map<Integer, CollectionInfo> getMaps() {
		return maps;
	}


	public void setMaps(Map<Integer, CollectionInfo> maps) {
		this.maps = maps;
	}


	public int getCollectorMapId() {
		return collectorMapId;
	}


	public void setCollectorMapId(int collectorMapId) {
		this.collectorMapId = collectorMapId;
	}


	public List getCollectorsMap() {
		collectorsMap = new ArrayList<>();
		collectorsMap.add(new SelectItem(0, "Select Collector"));
		collectotData = Collections.synchronizedMap(new HashMap<Integer, Collector>());
		
		Collector co = new Collector();
		co.setId(0);
		collectotData.put(0, co);
		
		for(Collector col : Collector.retrieve("", new String[0])) {
			if(col.getId()==0) {
				collectorsMap.add(new SelectItem(col.getId(), "Issued"));
				collectotData.put(col.getId(), col);
			}else {	
				collectorsMap.add(new SelectItem(col.getId(), col.getName()));
				collectotData.put(col.getId(), col);
			}
		}
		
		return collectorsMap;
	}


	public void setCollectorsMap(List collectorsMap) {
		this.collectorsMap = collectorsMap;
	}


	public List<CollectionInfo> getInfos() {
		return infos;
	}


	public void setInfos(List<CollectionInfo> infos) {
		this.infos = infos;
	}
	
	public int getMonthId() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public List getMonths() {
		months = new ArrayList<>();
		months.add(new SelectItem(0, "All Months"));
		for(int m=1; m<=12;m++) {
			months.add(new SelectItem(m, DateUtils.getMonthName(m)));
		}
		return months;
	}

	public void setMonths(List months) {
		this.months = months;
	}


	public Map<Integer, Collector> getCollectotData() {
		return collectotData;
	}


	public void setCollectotData(Map<Integer, Collector> collectotData) {
		this.collectotData = collectotData;
	}
	
	public String generateRandomIdForNotCaching() {
		return java.util.UUID.randomUUID().toString();
	}
	
	public StreamedContent getTempPdfFile() throws IOException {
		
		if(tempPdfFile==null) {
			String pdf = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
			String REPORT_NAME = "rcd";
			//pdf += REPORT_NAME + ".pdf";
			System.out.println("pdf file >>>> " + pdf);
			
		    File testPdfFile = new File(pdf);
  	    
	    	//return new DefaultStreamedContent(new FileInputStream(testPdfFile), "application/pdf", REPORT_NAME+".pdf");
		    return DefaultStreamedContent.builder()
		    		.contentType("application/pdf")
		    		.name(REPORT_NAME+".pdf")
		    		.stream(()-> this.getClass().getResourceAsStream(pdf + REPORT_NAME + ".pdf"))
		    		.build();
		}else {
			return tempPdfFile;
		}
	  }
	public void setTempPdfFile(StreamedContent tempPdfFile) {
		this.tempPdfFile = tempPdfFile;
	}
	
	public CollectionInfo getSelectedCollectionData() {
		return selectedCollectionData;
	}


	public void setSelectedCollectionData(CollectionInfo selectedCollectionData) {
		this.selectedCollectionData = selectedCollectionData;
	}


	public int getFundId() {
		/*if(fundId==0) {
			fundId = 1;
		}*/
		return fundId;
	}


	public List getFunds() {
		funds = new ArrayList<>();
		for(FundType f : FundType.values()) {
			funds.add(new SelectItem(f.getId(), f.getName()));
		}
		return funds;
	}


	public void setFundId(int fundId) {
		this.fundId = fundId;
	}


	public void setFunds(List funds) {
		this.funds = funds;
	}
}


