package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.OD;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 01/16/2019
 *
 */
@Named
@ViewScoped
public class OfficerBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 76788868651L;
	
	private List<OD> ods = new ArrayList<OD>();
	
	private int monthId;
	private List months;
	private int yearId;
	private List years;
	
	private String searchOfficer="";
	private List<Employee> employees = new ArrayList<Employee>();
	private OD selectedDate;
	private String colorButton="background-color: red; color: white";
	
	@PostConstruct
	public void init() {
		
		if(!OD.hasCurrentSetOfficer()) {//if no current officer
			loadOD();
			loadOfficer();
			
			PrimeFaces ins = PrimeFaces.current();
			ins.executeScript("PF('dlgOd').show()");
		}
		
	}
	
	public void loadOD() {
		
		ods = new ArrayList<OD>();
		String sql = " AND od.monthod=? AND od.yearod=? AND od.isactiveod=1";
		String[] params = new String[2];
		params[0] = getMonthId()+"";
		params[1] = getYearId()+"";
		Map<Integer, OD> mapOd = new HashMap<Integer,OD>();
		for(OD o : OD.retrieve(sql , params)) {
			mapOd.put(o.getDay(), o);
		}
		
		String dateInstance = getYearId() + "-" + (getMonthId()<10? "0" + getMonthId() : getMonthId()) + "-01";
		if(mapOd.size()==0) {
			System.out.println("zerooo");
			ods = new ArrayList<OD>();
			System.out.println("dateSelectEd : " + dateInstance);
			for(int day=1; day<=DateUtils.getLastDayOfTheMonth(dateInstance,getYearId(),getMonthId(), Locale.TAIWAN); day++) {
				OD od = new OD();
				//od.setId(day);
				od.setDay(day);
				od.setMonth(getMonthId());
				od.setYear(getYearId());
				od.setIsActive(1);
				
				if(day==DateUtils.getCurrentDay() && getMonthId()==DateUtils.getCurrentMonth() && getYearId()==DateUtils.getCurrentYear()) {
					od.setButtonStyle(getColorButton());
				}
				
				ods.add(od);
			}
		}else {
			System.out.println("with value");
			boolean isContain=false;
			for(int day=1; day<=DateUtils.getLastDayOfTheMonth(dateInstance,getYearId(),getMonthId(), Locale.TAIWAN); day++) {
				OD od = new OD();
				
				
				
				if(mapOd.containsKey(day)) {
					od = mapOd.get(day);
					String name = mapOd.get(day).getOfficer().getFirstName() + " " + mapOd.get(day).getOfficer().getLastName();
					od.setOfficerName(name);
					System.out.println("day:" + day + " contain");
					isContain=true;
				}else {
					//od.setId(day);
					od.setDay(day);
					od.setMonth(getMonthId());
					od.setYear(getYearId());
					od.setIsActive(1);
					od.setOfficerName("");
					System.out.println("day:" + day + " not contain");
				}
				
				if(day==DateUtils.getCurrentDay() && getMonthId()==DateUtils.getCurrentMonth() && getYearId()==DateUtils.getCurrentYear()) {
					if(isContain) {
						od.setButtonStyle("background-color: white; color: black");
						isContain=false;
					}else {	
						od.setButtonStyle(getColorButton());
					}
				}
				
				ods.add(od);
			}
		}
		
	}
	
	public void itemSelect(SelectEvent event) {
		loadOD();
	}
	
	public void selectedDate(OD od) {
		setSelectedDate(od);
		//loadOD();
		loadOfficer();
	}
	
	public void loadOfficer() {
		employees = new ArrayList<Employee>();
		
		String sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 AND emp.isresigned=0";
		String[] params = new String[0];
		
		//if(getSearchOfficer()!=null && !getSearchOfficer().isEmpty()) {
			sql += " AND (emp.firstname like '%"+ getSearchOfficer().replace("--", "")+"%' OR emp.lastname like '%"+ getSearchOfficer().replace("--", "")+"%' )";
		//}
		
		employees = Employee.retrieve(sql, params);
		
		Employee e = new Employee();
		e.setId(0);
		e.setFirstName("DELETE");
		e.setLastName("OD");
		employees.add(e);
	}
	
	public void sectedOfficer(Employee ee) {
		OD od = getSelectedDate();
		
		if(ee.getId()!=0) {
			od.setOfficer(ee);
			od.setOfficerName(ee.getFirstName() + " " + ee.getLastName());
			od.save();
		}else {//delete OD
			od.delete();
		}
		
		
		loadOD();
	}
	
	public List<OD> getOds() {
		return ods;
	}

	public int getMonthId() {
		if(monthId==0) {
			monthId = DateUtils.getCurrentMonth();
		}
		return monthId;
	}

	public int getYearId() {
		if(yearId==0) {
			yearId = DateUtils.getCurrentYear();
		}
		return yearId;
	}

	public void setOds(List<OD> ods) {
		this.ods = ods;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public void setYearId(int yearId) {
		this.yearId = yearId;
	}

	private void loadMonths() {
		months = new ArrayList<>();
		for(int i=1; i<=12; i++) {
			months.add(new SelectItem(i, DateUtils.getMonthName(i)));
		}
	}
	
	public List getMonths() {
		if(months==null) {
			loadMonths();
		}
		return months;
	}

	private void loadYears() {
		years = new ArrayList<>();
		List<OD> od = OD.retrieve(" group by od.yearod", new String[0]);
		if(od!=null && od.size()>0) {
			boolean foundCurrentYear = false;
			int year = DateUtils.getCurrentYear();
			for(OD o : od) {
				years.add(new SelectItem(o.getYear(), o.getYear()+""));
				year = o.getYear();
			}
			if(year!=DateUtils.getCurrentYear()) {
				years.add(new SelectItem(DateUtils.getCurrentYear(), DateUtils.getCurrentYear()+""));
			}
		}else {
			years.add(new SelectItem(DateUtils.getCurrentYear(), DateUtils.getCurrentYear()+""));
		}
	}
	
	public List getYears() {
		if(years==null) {
			loadYears();
		}
		return years;
	}

	public void setMonths(List months) {
		this.months = months;
	}

	public void setYears(List years) {
		this.years = years;
	}

	public String getSearchOfficer() {
		return searchOfficer;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setSearchOfficer(String searchOfficer) {
		this.searchOfficer = searchOfficer;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public OD getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(OD selectedDate) {
		this.selectedDate = selectedDate;
	}

	public String getColorButton() {
		return colorButton;
	}

	public void setColorButton(String colorButton) {
		this.colorButton = colorButton;
	}
}
