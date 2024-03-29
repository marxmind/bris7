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
import com.italia.marxmind.bris.controller.Stocks;
import com.italia.marxmind.bris.enm.FormType;
import com.italia.marxmind.bris.enm.StockStatus;
import com.italia.marxmind.bris.utils.DateUtils;
/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 06-12-2019
 *
 */
@Named("stockBean")
@ViewScoped
public class StocksBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 154687697435L;
	
	private Date recordedDate;
	private int numberOfStab;
	private String seriesFrom;
	private String seriesTo;
	private Stocks stockData;
	private List<Stocks> stocks = new ArrayList<Stocks>();
	
	private int formTypeId;
	private List formType;
	
	private int formTypeIdSearch;
	private List formTypeSearch;
	
	@PostConstruct
	public void init() {
		stocks = new ArrayList<Stocks>();
		
		String sql = " AND cl.isid=0 ";
		String[] params = new String[0];
		
		if(getFormTypeIdSearch()==0) {
			sql += " AND st.formType!=0";
		}else {
				sql += "AND (st.statusstock="+ StockStatus.NOT_HANDED.getId() +" OR st.statusstock="+ StockStatus.PARTIAL_ISSUED.getId() +") AND st.formType="+getFormTypeIdSearch();
		}
		
		sql +=" ORDER BY st.datetrans ASC";
		
		stocks = Stocks.retrieve(sql, params);
	}
	
	public void generateSeries() {
		
		long series = 0;
		
		try{series = Long.valueOf(getSeriesFrom().replace(",", ""));}catch(NullPointerException e) {}
		System.out.println("Form type " + FormType.nameId(getFormTypeId()));
		System.out.println(">>> stab " + getNumberOfStab());
		if(getFormTypeId()<=8) {
			System.out.println("If " + getFormTypeId());
			if(getNumberOfStab()>1) {
				
				series += 50 * getNumberOfStab();
				series -=1;
			}else {
				series += 49;
			}
			
			String newSeries = DateUtils.numberResult(getFormTypeId(), series);
			
			setSeriesTo(newSeries);
		
			
		}else {// for Cash ticket
			setSeriesFrom(1+"");
			System.out.println("if else " + getFormTypeId());
			if(getNumberOfStab()>1) {
				
				series += 2000 * getNumberOfStab();
				series -= 1;
				
				System.out.println("series>>> " + series);
			}else {
				series += 1999;
				
				System.out.println("else series>>> " + series);
			}
			
			setSeriesTo(series+"");
		}
		
	}
	
	public void saveData() {
		
		Stocks st = new Stocks();
		
		if(getStockData()!=null) {
			st = getStockData();
			setNumberOfStab(1);
		}
		
		boolean isOk = true;
		
		if(getNumberOfStab()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide number of stab");
		}
		
		if(getFormTypeId()<=8) {
		
			if(getSeriesFrom()==null || getSeriesFrom().isEmpty()) {
				isOk = false;
				Application.addMessage(3, "Error", "Please provide start of series");
			}
		
		}
		
		if(isOk) {
		
		if(FormType.CT_2.getId()==getFormTypeId() || FormType.CT_5.getId()==getFormTypeId()) {
			
					st = new Stocks();
					st.setIsActive(1);
					st.setDateTrans(DateUtils.convertDate(getRecordedDate(), "yyyy-MM-dd"));
					st.setSeriesFrom(DateUtils.numberResult(getFormTypeId(),Long.valueOf(getSeriesFrom())));
					st.setSeriesTo(getSeriesTo());
					st.setFormType(getFormTypeId());
					st.setStatus(1);//not issued
					st.setCollector(null);
					st.setQuantity(Integer.valueOf(getSeriesTo()));
					st.save();
			
			
					setFormTypeIdSearch(getFormTypeId());
					createNew();
					init();
			Application.addMessage(1, "Success", "Successfully saved.");
			
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("$('#panelHide').hide(1000);hideButton()");
			
		}else {
		
		long seriesFrom = Long.valueOf(getSeriesFrom());
		long seriesTo = Long.valueOf(getSeriesTo());
			
		if(getNumberOfStab()>1) {
			
			
			int cnt = 1;
			long from = 0;
			for(long i=seriesFrom; i<=seriesTo; i++) {
				
				if(cnt==1) {
					from = i;
				}
				
				if(cnt==50) {
					
					st = new Stocks();
					st.setIsActive(1);
					st.setDateTrans(DateUtils.convertDate(getRecordedDate(), "yyyy-MM-dd"));
					st.setSeriesFrom(DateUtils.numberResult(getFormTypeId(), from));
					st.setSeriesTo(DateUtils.numberResult(getFormTypeId(), i));
					st.setFormType(getFormTypeId());
					st.setStatus(1);//not issued
					st.setCollector(null);
					st.setQuantity(50);
					st.save();
					
					cnt=1;
				}else {
					cnt++;
				}
				
				
			}
			
			
			setFormTypeIdSearch(getFormTypeId());
			createNew();
			init();
			Application.addMessage(1, "Success", "Successfully saved.");
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("$('#panelHide').hide(1000);hideButton()");
		}else {
			
			long is49 = seriesTo - seriesFrom;
			
			if(is49==49) {
			
			st.setIsActive(1);
			st.setDateTrans(DateUtils.convertDate(getRecordedDate(), "yyyy-MM-dd"));
			st.setSeriesFrom(DateUtils.numberResult(getFormTypeId(), Long.valueOf(getSeriesFrom())));
			st.setSeriesTo(DateUtils.numberResult(getFormTypeId(), Long.valueOf(getSeriesTo())));
			st.setFormType(getFormTypeId());
			st.setStatus(1);//not issued
			st.setCollector(null);
			st.setQuantity(50);
			st.save();
			
			setFormTypeIdSearch(getFormTypeId());
			createNew();
			init();
			Application.addMessage(1, "Success", "Successfully saved.");
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("$('#panelHide').hide(1000);hideButton()");
			}else {
				Application.addMessage(3, "Error", "Please input the correct range of of series which is corresponding to 50 pieces");
			}
		}
		
		}
		}
	}
	
	public void clickItem(Stocks st) {
		setStockData(st);
		setFormTypeId(st.getFormType());
		setRecordedDate(DateUtils.convertDateString(st.getDateTrans(), "yyyy-MM-dd"));
		if(st.getFormType()<=8) {
			setNumberOfStab(1);
			setSeriesFrom(st.getSeriesFrom());
			setSeriesTo(st.getSeriesTo());
		}else {
			int qty = Integer.valueOf(st.getSeriesTo()) / 2000;
			setNumberOfStab(qty);
			setSeriesFrom(st.getSeriesFrom());
			setSeriesTo(st.getSeriesTo());
		}
		
		setFormTypeId(st.getFormType());
	}
	
	public void deleteRow(Stocks st) {
		st.delete();
		init();
		Application.addMessage(1, "Success", "Successfully saved.");
	}
	
	public void createNew() {
		setStockData(null);
		setRecordedDate(null);
		setNumberOfStab(0);
		setSeriesFrom("");
		setSeriesTo("");
		setFormTypeId(1);
	}
	
	public Date getRecordedDate() {
		if(recordedDate==null) {
			recordedDate = DateUtils.getDateToday();
		}
		return recordedDate;
	}
	public void setRecordedDate(Date recordedDate) {
		this.recordedDate = recordedDate;
	}
	public int getNumberOfStab() {
		return numberOfStab;
	}
	public void setNumberOfStab(int numberOfStab) {
		this.numberOfStab = numberOfStab;
	}
	public String getSeriesFrom() {
		return seriesFrom;
	}
	public void setSeriesFrom(String seriesFrom) {
		this.seriesFrom = seriesFrom;
	}
	public String getSeriesTo() {
		return seriesTo;
	}
	public void setSeriesTo(String seriesTo) {
		this.seriesTo = seriesTo;
	}

	public List<Stocks> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stocks> stocks) {
		this.stocks = stocks;
	}

	public Stocks getStockData() {
		return stockData;
	}

	public void setStockData(Stocks stockData) {
		this.stockData = stockData;
	}

	public List getFormType() {
		
		formType = new ArrayList<>();
		
		for(FormType form : FormType.values()) {
			formType.add(new SelectItem(form.getId(), form.getName()));
		}
		
		return formType;
	}

	public void setFormType(List formType) {
		this.formType = formType;
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

}

