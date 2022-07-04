package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.CollectionInfo;
import com.italia.marxmind.bris.controller.Collector;
import com.italia.marxmind.bris.controller.IssuedForm;
import com.italia.marxmind.bris.controller.Stocks;
import com.italia.marxmind.bris.enm.FormStatus;
import com.italia.marxmind.bris.enm.FormType;
import com.italia.marxmind.bris.enm.FundType;
import com.italia.marxmind.bris.enm.StockStatus;
import com.italia.marxmind.bris.utils.DateUtils;
/**
 * 
 * @author Mark Italia
 * @since 08-11-2018
 * @version 1.0
 *
 */
@Named("formisBean")
@ViewScoped
public class IssuedFormBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8654686645551L;
	
	
	private Date issuedDate;
	
	private int formTypeId;
	private List formTypes;
	
	private long beginningNo;
	private long endingNo;
	private int quantity;
	
	private int collectorId;
	private List collectors;
	
	private int statusId;
	private List status;
	
	private IssuedForm selectedForm;
	
	private List<IssuedForm> forms = new ArrayList<IssuedForm>();
	
	private List<Stocks> stocks = new ArrayList<Stocks>();
	private int formTypeIdSearch;
	private List formTypeSearch;
	
	private Stocks stockData;
	
	private int fundId;
	private List funds;
	
	@PostConstruct
	public void init() {
		
		forms = new ArrayList<IssuedForm>();
		forms =  IssuedForm.retrieve("", new String[0]);
		Collections.reverse(forms);
	}
	
	public void loadForms() {
		
		stocks = new ArrayList<Stocks>();
		String sql = " AND cl.isid=0 AND st.qty>0 ";
		String[] params = new String[0];
		
		if(getFormTypeIdSearch()==0) {
			sql += " AND st.formType!=0";
		}else {
			sql += " AND st.formType="+getFormTypeIdSearch();
		}
		
		sql +=" ORDER BY st.datetrans ASC";
		
		stocks = Stocks.retrieve(sql, params);
		
	}
	
	public void stocksSelected(Stocks st) {
		setFormTypeId(st.getFormType());
		if(st.getFormType()<=8) {
			setBeginningNo(Long.valueOf(st.getSeriesFrom()));
			setEndingNo(Long.valueOf(st.getSeriesTo()));
			setQuantity(50);
		}else {
			int qty = st.getQuantity();
			
			if(FormType.CT_2.getId()==st.getFormType()) {
				
				qty *= 2;
				
			}else if(FormType.CT_5.getId()==st.getFormType()) {
				
				qty *= 5;
				
			}
			
			setBeginningNo(qty);
			setEndingNo(qty);
			setQuantity(st.getQuantity());
		}
		setStatusId(FormStatus.HANDED.getId());
		setStockData(st);
	}
	
	public void calculateEndingNo() {
		
		if(getFormTypeId()<=8) {
			
			long ending = (getBeginningNo()) + (getQuantity()==0? 0 : getQuantity()-1);
			System.out.println("begin: " + getBeginningNo() + " pcs: " + getQuantity());
			System.out.println("ending: " + ending);
			setEndingNo(ending);
			
		}else {
			
			int qty = getQuantity();
			
			if(FormType.CT_2.getId()==getFormTypeId()) {
				
				qty *= 2;
				
			}else if(FormType.CT_5.getId()==getFormTypeId()) {
				
				qty *= 5;
				
			}
			
			setBeginningNo(qty);
			setEndingNo(qty);
		}
		
		
	}
	
	public void createNew() {
		setStockData(null);
		setFormTypeSearch(null);
		setFormTypeIdSearch(0);
		setFundId(1);
		setFormTypeId(0);
		setIssuedDate(null);
		setBeginningNo(0);
		setEndingNo(0);
		setQuantity(0);
		setCollectorId(0);
		setStatusId(0);
		setSelectedForm(null);
		init();
	}
	
	public void saveData() {
		
		IssuedForm form = new IssuedForm();
		boolean isOk = true;
		
		if(getSelectedForm()!=null) {
			form = getSelectedForm();
		}else {
			form.setIsActive(1);
		}
		
		if(getBeginningNo()<=0 && getFormTypeId()<=8) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial From");
		}
		
		if(getEndingNo()<=0 && getFormTypeId()<=8) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Serial To");
		}
		
		
		if(getQuantity()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Quantity");
			
			if(FormType.CT_2.getId()==getFormTypeId() || FormType.CT_2.getId()==getFormTypeId()) {
				if(getQuantity()==2000 || getQuantity()==4000 || getQuantity()==6000 || getQuantity()==8000 || getQuantity()==10000 || getQuantity()==12000) {
					
				}else {
					isOk = false;
					Application.addMessage(3, "Error", "Please provide exact quantity");
				}
			}
			
		}
		
		if(getCollectorId()<=0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Collector");
		}
		
		if(getSelectedForm()!=null) {
			boolean isExist = IssuedForm.isExistInCollection(getSelectedForm().getId());
			if(isExist) {
				isOk = false;
				Application.addMessage(3, "Error", "Updating this form is not allowed. This form already used in Collection Recording.");
			}
		}
		
		if(isOk) {
			
			form.setFundId(getFundId());
			form.setIssuedDate(DateUtils.convertDate(getIssuedDate(), "yyyy-MM-dd"));
			form.setStatus(getStatusId());
			form.setFormType(getFormTypeId());
			form.setPcs(getQuantity());
			
			if(getFormTypeId()<=8) {
				form.setBeginningNo(getBeginningNo());
				form.setEndingNo(getEndingNo());
			}else {
				
				int qty = getQuantity();
				
				if(FormType.CT_2.getId()==getFormTypeId()) {
					
					qty *= 2;
					
				}else if(FormType.CT_5.getId()==getFormTypeId()) {
					
					qty *= 5;
					
				}
				
				
				int quantity = getQuantity();
				if(getStockData()!=null) {
					quantity = getStockData().getQuantity();
					if(quantity>getQuantity()) {
						quantity = getQuantity();
					}
				}
				form.setBeginningNo(qty);
				form.setEndingNo(qty);
			}
			
			
			Collector collector = new Collector();
			collector.setId(getCollectorId());
			form.setCollector(collector);
			
			
			
			//update stocks
			if(getStockData()!=null) {
				Stocks st = getStockData();
				
				if(getFormTypeId()<=8) {
					st.setStatus(StockStatus.ALL_ISSUED.getId());
					st.setCollector(collector);
				}else {	
					
					int quantity = getStockData().getQuantity();
					if(quantity>getQuantity()) {
						quantity -= getQuantity();
						st.setStatus(StockStatus.PARTIAL_ISSUED.getId());
					}else if(quantity==getQuantity()) {
						quantity = 0;
						st.setStatus(StockStatus.ALL_ISSUED.getId());
					}else if(quantity<getQuantity()) {
						quantity = 0;
						st.setStatus(StockStatus.ALL_ISSUED.getId());
					}
					st.setQuantity(quantity);
				}
				
				st.save();
				
				form.setStock(st);
			}
			
			form.save();
			
			createNew();
			init();
			Application.addMessage(1, "Success", "Successfully saved.");
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("$('#panelHide').hide(1000);hideButton()");
		}
		
	}
	
	public void deleteRow(IssuedForm form) {
		
		if(IssuedForm.isExistInCollection(form.getId())) {
			Application.addMessage(2, "Not Allowed", "Sorry, data has been already use in collections.");
		}else {	
			//return to stock
			Stocks st = form.getStock();
			Collector collector = new Collector();
			collector.setId(0);
			st.setCollector(collector);
			st.save();
			
			form.delete();
			init();
			Application.addMessage(1, "Success", "Successfully deleted.");
		}
	}
	
	public void clickItem(IssuedForm form) {
		setFormTypeId(form.getFormType());
		setIssuedDate(DateUtils.convertDateString(form.getIssuedDate(),"yyyy-MM-dd"));
		setQuantity(form.getPcs());
		
		if(getFormTypeId()<=8) {
			setBeginningNo(form.getBeginningNo());
			setEndingNo(form.getEndingNo());
		}else {
			setBeginningNo(form.getPcs());
			setEndingNo(form.getPcs());
		}
		
		setCollectorId(form.getCollector().getId());
		setStatusId(form.getStatus());
		setSelectedForm(form);
	}
	
	public Date getIssuedDate() {
		if(issuedDate==null) {
			issuedDate = DateUtils.getDateToday();
		}
		return issuedDate;
	}
	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public List getStatus() {
		status = new ArrayList<>();
		for(FormStatus st : FormStatus.values()) {
			status.add(new SelectItem(st.getId(), st.getName()));
		}
		return status;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public IssuedForm getSelectedForm() {
		return selectedForm;
	}

	public void setSelectedForm(IssuedForm selectedForm) {
		this.selectedForm = selectedForm;
	}

	public List<IssuedForm> getForms() {
		return forms;
	}

	public void setForms(List<IssuedForm> forms) {
		this.forms = forms;
	}

	public List<Stocks> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stocks> stocks) {
		this.stocks = stocks;
	}

	public int getFormTypeIdSearch() {
		return formTypeIdSearch;
	}

	public void setFormTypeIdSearch(int formTypeIdSearch) {
		this.formTypeIdSearch = formTypeIdSearch;
	}

	public List getFormTypeSearch() {
		
		formTypeSearch = new ArrayList<>();
		formTypeSearch.add(new SelectItem(0, "All Forms"));
		for(FormType form : FormType.values()) {
			formTypeSearch.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formTypeSearch;
	}

	public void setFormTypeSearch(List formTypeSearch) {
		this.formTypeSearch = formTypeSearch;
	}

	public Stocks getStockData() {
		return stockData;
	}

	public void setStockData(Stocks stockData) {
		this.stockData = stockData;
	}
	
	public int getFundId() {
		if(fundId==0) {
			fundId = 1;
		}
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

