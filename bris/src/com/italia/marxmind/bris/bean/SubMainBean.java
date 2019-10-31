package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;

import com.italia.marxmind.bris.controller.BCard;
import com.italia.marxmind.bris.controller.CaseFilling;
import com.italia.marxmind.bris.controller.Cases;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Document;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 07/11/2017
 * @version 1.0
 *
 */
@ManagedBean(name="submainBean", eager=true)
@ViewScoped
public class SubMainBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 47686587585861L;
	
	private List<BCard> cardTrans = Collections.synchronizedList(new ArrayList<BCard>());
	private List<Clearance> clearances = Collections.synchronizedList(new ArrayList<Clearance>());
	
	private List<Document> docs = Collections.synchronizedList(new ArrayList<Document>());
	private List<Cases> cases = Collections.synchronizedList(new ArrayList<Cases>());
	
	private String searchIDParam;
	private String searchDocParam;
	private String searchCaseParam;
	
	//use by version6 aka avalon
	
	@ManagedProperty("#{mainBean}")
	private MainBean mainBean;
	
	private String genericSeach;
	
	public void loadGenericSearch() {
		String val = getMainBean().getSearchOption();
		System.out.println("search options>> " + val);
		System.out.println("generic seach >> " + getGenericSeach());
		
		if("2".equalsIgnoreCase(val)) {//ID
			setSearchIDParam(getGenericSeach());
			loadIDs();
		}else if("3".equalsIgnoreCase(val)) {//DOC
			setSearchDocParam(getGenericSeach());
			loadDocuments();
		}else if("4".equalsIgnoreCase(val)) {//CASES
			setSearchCaseParam(getGenericSeach());
			loadCases();
		}
		
	}
	
	@PostConstruct
	public void init(){
		loadIDs();
	}
	
	public void onChange(TabChangeEvent event) {
		
		if("Calendar of Activities".equalsIgnoreCase(event.getTab().getTitle())){
			//file location C:\bris\reports\<BarangayName>-<Municipality>
		}else if("List of Created Barangay ID".equalsIgnoreCase(event.getTab().getTitle())){
			loadIDs();
		}else if("List of Created Documents".equalsIgnoreCase(event.getTab().getTitle())){
			loadDocuments();
		}else if("List of Cases".equalsIgnoreCase(event.getTab().getTitle())){
			loadCases();
		}
		
		
		}
	
	public String getPlaceholderMonthYear() {
		return DateUtils.getMonthName(DateUtils.getCurrentMonth()) +" "+ DateUtils.getCurrentYear();
	}
	
	
	public void loadCases(){
		String sql = " AND ciz.caseisactive=1 AND (ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?)";
		String[] params = new String[3];
		params[0] = CaseStatus.SETTLED.getId()+"";
		params[1] = CaseStatus.CANCELLED.getId()+"";
		params[2] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		cases = Collections.synchronizedList(new ArrayList<Cases>());
		if(getSearchCaseParam()!=null && !getSearchCaseParam().isEmpty()){
			params = new String[0];
			sql = " AND ciz.caseisactive=1 ";
			//cases = Collections.synchronizedList(new ArrayList<Cases>());
			 sql += " AND (ciz.complainants like '%"+getSearchCaseParam().replace("--", "")+"%' ";
			 sql += " OR ciz.complainants like '%"+getSearchCaseParam().replace("--", "")+",' ";
			 sql += " OR ciz.complainants like ',"+getSearchCaseParam().replace("--", "")+"%' ";
			 
			 sql += " OR ciz.respondents like '%"+getSearchCaseParam().replace("--", "")+"%' ";
			 sql += " OR ciz.respondents like '%"+getSearchCaseParam().replace("--", "")+",' ";
			 sql += " OR ciz.respondents like ',"+getSearchCaseParam().replace("--", "")+"%' )";
			 
			 
		}
		
		for(Cases ciz : Cases.retrieve(sql, params)){
			
			sql = " AND ciz.caseisactive=1 AND ciz.caseid=? ORDER BY fil.filcount DESC limit 1";
			params = new String[1];
			params[0] = ciz.getId()+"";
			//String oldNarratives=ciz.getNarratives();
			ciz.setNarratives("");
			
			List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
			String attempt = "first";
			String setattempt = "one";
			
			if(fils.get(0).getCount()==1){
				attempt = "first";
				setattempt = "one meeting before the case had been closed";
			}else if(fils.get(0).getCount()==2){
				attempt = "second";
				setattempt = "two meetings before the case had been closed";
			}else if(fils.get(0).getCount()==3){
				attempt = "third";
				setattempt = "three meetings before the case had been closed";
			}
			
			if(CaseStatus.NEW.getId()==ciz.getStatus()){
				ciz.setNarratives("The case was filed on "+ DateUtils.convertDateToMonthDayYear(ciz.getDate()) + ". The "+attempt+" " + (ciz.getType()==1? "invitation" : "summon" ) +" meeting would be on " + DateUtils.convertDateToMonthDayYear(fils.get(0).getSettlementDate())+ " at " + fils.get(0).getSettlementTime() +".");
			}
			
			if(CaseStatus.ON_HOLD.getId()==ciz.getStatus()){
				ciz.setNarratives("Case is currently put on-hold.");
			}
			
			if(CaseStatus.SETTLED.getId()==ciz.getStatus()){
				ciz.setNarratives("Case was closed from the last meeting held on " + DateUtils.convertDateToMonthDayYear(fils.get(0).getSettlementDate()) + ". The case had " + setattempt +".");
			}	
			if(CaseStatus.IN_PROGRESS.getId()==ciz.getStatus()){
				ciz.setNarratives("Case is currently being processed, the next "+ (ciz.getType()==1? "invitation" : "summon" ) +" meeting would be on " + DateUtils.convertDateToMonthDayYear(fils.get(0).getSettlementDate())+ " at " + fils.get(0).getSettlementTime() +".");
			}
			
			if(CaseStatus.MOVED_IN_HIGHER_COURT.getId()==ciz.getStatus()){
				ciz.setNarratives("Case was endorsed to higher court because there has no/confrontation made between the parties and the Complainant wants this matter to reach the HIGHER COURT.");
			}
			
			cases.add(ciz);
		}
		Collections.reverse(cases);
		
	}
	
	public void loadIDs(){
		
		
		String[] params = new String[2];
		String sql = " AND crd.isactivebid=1 AND (crd.datetrans>=? AND crd.datetrans<=?)  ";
		if(getSearchIDParam()!=null && !getSearchIDParam().isEmpty()){
			int len = getSearchIDParam().length();
			
			if(len>=4){
				
				String month = "", year = "";
				boolean isByMonth = false;
				String dateFrom = "";
				String dateTo = "";
				try{
					month = getSearchIDParam().split(" ")[0];
					year = getSearchIDParam().split(" ")[1];
					int size = month.length();
					
					month = month.substring(0, 1).toUpperCase() + month.substring(1,size).toLowerCase();
					int num = Integer.valueOf(year);//use only for error
					dateFrom = year + "-" + DateUtils.getMonthNumber(month) + "-01";
					dateTo = year + "-" + DateUtils.getMonthNumber(month) + "-31";
					
					isByMonth=true;
				}catch(Exception e){}
				
				
				cardTrans = Collections.synchronizedList(new ArrayList<BCard>());
				sql = " AND crd.isactivebid=1 ";
				
				if(isByMonth){
					params = new String[2];
					params[0] = dateFrom;
					params[1] = dateTo;
					sql += " AND (crd.datetrans>=? AND crd.datetrans<=?) ";
				}else{
					params = new String[0];
					sql += " AND cuz.fullname like '%"+ getSearchIDParam().replace("--", "") +"%'";
				}
				
				cardTrans = BCard.retrieve(sql, params);
			}
			
		}else{
			
			cardTrans = Collections.synchronizedList(new ArrayList<BCard>());
			params[0] = DateUtils.convertDate(DateUtils.getDateToday(), DateFormat.YYYY_MM_DD());
			params[1] = DateUtils.convertDate(DateUtils.getDateToday(), DateFormat.YYYY_MM_DD());
			cardTrans = BCard.retrieve(sql, params);
		}
		
		
	}
	
	public void loadDocuments(){
		
		String sql = " AND clz.isactiveclearance=1 AND (clz.clearissueddate>=? AND clz.clearissueddate<=?) ";
		String[] params = new String[2];
		
		if(getSearchDocParam()!=null && !getSearchDocParam().isEmpty()){
			int len = getSearchDocParam().length();
			if(len>=4){
				//clearances = Collections.synchronizedList(new ArrayList<Clearance>());
				docs = Collections.synchronizedList(new ArrayList<Document>());
				sql = " AND clz.isactiveclearance=1 ";
				params = new String[0];
				sql += " AND cuz.fullname like '%" + getSearchDocParam().replace("--", "") +"%'";
				Map<String, Document> docMap = Collections.synchronizedMap(new HashMap<String, Document>());
				List<Document> listDoc = Collections.synchronizedList(new ArrayList<Document>());
				
				for(Clearance clr : Clearance.retrieve(sql, params)){
					Document doc = new Document();
						doc.setName(clr.getPurposeName());
						doc.setCustomer(clr.getTaxPayer());
						doc.setClearance(clr);
						
						if(docMap!=null){
							
							if(docMap.containsKey(clr.getPurposeName())){
								listDoc = docMap.get(clr.getPurposeName()).getListDocs();
								listDoc.add(doc);
								docMap.get(clr.getPurposeName()).setListDocs(listDoc);
							}else{
								listDoc = Collections.synchronizedList(new ArrayList<Document>());
								listDoc.add(doc);
								doc.setListDocs(listDoc);
								docMap.put(clr.getPurposeName(), doc);
							}
							
						}else{
							listDoc.add(doc);
							doc.setListDocs(listDoc);
							docMap.put(clr.getPurposeName(), doc);
							
						}
						
				}
				
				for(Document doc : docMap.values()){
					docs.add(doc);
				}
				
			}
		}else{
			//clearances = Collections.synchronizedList(new ArrayList<Clearance>());
			docs = Collections.synchronizedList(new ArrayList<Document>());
			params[0] = DateUtils.convertDate(DateUtils.getDateToday(), DateFormat.YYYY_MM_DD());
			params[1] = DateUtils.convertDate(DateUtils.getDateToday(), DateFormat.YYYY_MM_DD());
			
			Map<String, Document> docMap = Collections.synchronizedMap(new HashMap<String, Document>());
			List<Document> listDoc = Collections.synchronizedList(new ArrayList<Document>());
			for(Clearance clr : Clearance.retrieve(sql, params)){
				Document doc = new Document();
					doc.setName(clr.getPurposeName());
					doc.setCustomer(clr.getTaxPayer());
					doc.setClearance(clr);
					
					if(docMap!=null){
						
						if(docMap.containsKey(clr.getPurposeName())){
							listDoc = docMap.get(clr.getPurposeName()).getListDocs();
							listDoc.add(doc);
							docMap.get(clr.getPurposeName()).setListDocs(listDoc);
						}else{
							listDoc = Collections.synchronizedList(new ArrayList<Document>());
							listDoc.add(doc);
							doc.setListDocs(listDoc);
							docMap.put(clr.getPurposeName(), doc);
						}
						
					}else{
						listDoc.add(doc);
						doc.setListDocs(listDoc);
						docMap.put(clr.getPurposeName(), doc);
						
					}
					
			}
			
			for(Document doc : docMap.values()){
				docs.add(doc);
			}
			
		}
		
		
		
	}
	
	public List<BCard> getCardTrans() {
		return cardTrans;
	}

	public void setCardTrans(List<BCard> cardTrans) {
		this.cardTrans = cardTrans;
	}

	public List<Clearance> getClearances() {
		return clearances;
	}

	public void setClearances(List<Clearance> clearances) {
		this.clearances = clearances;
	}

	public String getSearchIDParam() {
		return searchIDParam;
	}

	public void setSearchIDParam(String searchIDParam) {
		this.searchIDParam = searchIDParam;
	}

	public String getSearchDocParam() {
		return searchDocParam;
	}

	public void setSearchDocParam(String searchDocParam) {
		this.searchDocParam = searchDocParam;
	}

	public List<Document> getDocs() {
		return docs;
	}

	public void setDocs(List<Document> docs) {
		this.docs = docs;
	}
	
	public String getSearchCaseParam() {
		return searchCaseParam;
	}

	public void setSearchCaseParam(String searchCaseParam) {
		this.searchCaseParam = searchCaseParam;
	}

	public List<Cases> getCases() {
		return cases;
	}

	public void setCases(List<Cases> cases) {
		this.cases = cases;
	}

	public String getGenericSeach() {
		return genericSeach;
	}

	public void setGenericSeach(String genericSeach) {
		this.genericSeach = genericSeach;
	}

	

	public MainBean getMainBean() {
		return mainBean;
	}
	
	public void setMainBean(MainBean mainBean) {
		this.mainBean = mainBean;
	}
}
