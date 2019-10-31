package com.italia.marxmind.bris.bean;
/**
 * 
 * @author mark italia
 * @since 09/15/2017
 * @version 1.0
 *
 */

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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Blotters;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.IncidentReportTypes;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.LuponPerson;
import com.italia.marxmind.bris.controller.MinorPerson;
import com.italia.marxmind.bris.controller.Persons;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.ReportingPerson;
import com.italia.marxmind.bris.controller.SuspectedPerson;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.controller.VictimPerson;
import com.italia.marxmind.bris.enm.BlotterStatus;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.ClearanceRpt;
import com.italia.marxmind.bris.reports.ReadXML;
import com.italia.marxmind.bris.reports.ReportCompiler;
import com.italia.marxmind.bris.reports.ReportTag;
import com.italia.marxmind.bris.utils.DateUtils;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean(name="blotterBean", eager=true)
@ViewScoped
public class BlottersBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 435456677543531L;
	
	private List<Blotters> blotters = Collections.synchronizedList(new ArrayList<Blotters>());
	/*private String searchDateTransFrom;
	private String searchDateTransTo;*/
	
	private Blotters blottersData;
	
	private String dateTrans;
	private String timeTrans;
	private Date incidentDate;
	private String incidentTime;
	private String incidentPlace;
	private String incidentDetails;//="Incident Details";
	private String incidentSolutions;//="Solution Details";
	
	private List status;
	private int statusId;
	
	private List incidentTypes;
	private int typeId;
	
	private UserDtls userDtls;
	
	private List<ReportingPerson> reportingPersons = Collections.synchronizedList(new ArrayList<ReportingPerson>());
	private List<SuspectedPerson> suspectedPersons = Collections.synchronizedList(new ArrayList<SuspectedPerson>());
	private List<MinorPerson> minorPersons = Collections.synchronizedList(new ArrayList<MinorPerson>());
	private List<VictimPerson> victimPersons = Collections.synchronizedList(new ArrayList<VictimPerson>());
	private List<LuponPerson> luponPersons = Collections.synchronizedList(new ArrayList<LuponPerson>());
	
	private List<Employee> employees = Collections.synchronizedList(new ArrayList<Employee>());
	private List<Customer> citizens = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchEmployee;
	private String seachCitizens;
	
	private String reportingPersonsList;
	private String suspectPersonsList;
	private String minorPersonsList;
	private String victimPersonsList;
	private String luponPersonList;
	
	private String searchdata;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Bris.REPORT);
	private static final String BLOTTER_REPORT_NAME = ReadXML.value(ReportTag.BLOTTER);
	
	private String BARANGAY = ReadConfig.value(Bris.BARANGAY);
	private String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
	private String PROVINCE = ReadConfig.value(Bris.PROVINCE);
	
	private String tempIncidentDetails;
	private String tempSolutions;
	
	private Date calendarFrom;
	private Date calendarTo;
	
	@PostConstruct
	public void init(){
		loadBlotters();
	}
	
	public void loadBlotters(){
		
		Login in = Login.getUserLogin();
		boolean isOk = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOk = true;
		}else{
			if(Features.isEnabled(Feature.BLOTTERS)){
				isOk = true;
			}
		}
		
		blotters = Collections.synchronizedList(new ArrayList<Blotters>());
		
		if(isOk){
		
		String sql = "";
		String[] params = new String[0];
		
		try{
			String blotEdi = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editBlotter");
			System.out.println("Check pass name: " + blotEdi);
			if(blotEdi!=null && !blotEdi.isEmpty() && !"null".equalsIgnoreCase(blotEdi)){
				setSearchdata(blotEdi.split(":")[0]);
				setCalendarFrom(DateUtils.convertDateString(blotEdi.split(":")[1], DateFormat.YYYY_MM_DD()));
				setCalendarTo(DateUtils.convertDateString(blotEdi.split(":")[1], DateFormat.YYYY_MM_DD()));
				sql = " AND ctz.cusisactive=1 AND ctz.fullname='"+ getSearchdata().replace("--", "") +"'";
			}else{
				sql = " AND ctz.cusisactive=1 AND ctz.fullname like '%"+ getSearchdata().replace("--", "") +"%'";
			}
		}catch(Exception e){}
		
		if(getSearchdata()!=null && !getSearchdata().isEmpty()){
			
			int size = getSearchdata().length();
			if(size>=10){
					
				System.out.println("SQL: " + sql);
				
				Map<Long, Blotters> blotMap = Collections.synchronizedMap(new HashMap<Long, Blotters>());
				for(ReportingPerson rep : ReportingPerson.retrieve(sql, params)){
					blotMap.put(rep.getBlotters().getId(), rep.getBlotters());
				}
				for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
					blotMap.put(sus.getBlotters().getId(), sus.getBlotters());
				}
				for(VictimPerson vic : VictimPerson.retrieve(sql, params)){
					blotMap.put(vic.getBlotters().getId(), vic.getBlotters());
				}
				
				sql = " AND emp.isactiveemp=1 AND ( emp.firstname like '%"+ getSearchdata().replace("--", "") +"%'";
				sql += " OR emp.lastname like '%"+ getSearchdata().replace("--", "") +"%')";
				for(LuponPerson lup : LuponPerson.retrieve(sql, params)){
					blotMap.put(lup.getBlotters().getId(), lup.getBlotters());
				}
				
				
				int sizeMap = blotMap.size();
				
				if(sizeMap>0){
					int cnt = 1;
					sql = " AND (";
					for(Blotters blot : blotMap.values()){
						if(cnt==sizeMap){
							sql += " blot.blotid=" + blot.getId() + " )";
						}else{
							sql += " blot.blotid=" + blot.getId() +" OR ";
						}
						
						cnt++;
					}
				}
				
				
				blotMap = Collections.synchronizedMap(new HashMap<Long, Blotters>());
				params = new String[2];
				sql += " AND blot.isactiveblot=1 AND (blot.blotdate>=? AND blot.blotdate<=?) ";
				/*params[0] = getSearchDateTransFrom();
				params[1] = getSearchDateTransTo();*/
				params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
				
				for(Blotters bl : Blotters.retrieve(sql, params)){
					bl.setIncidentName(IncidentReportTypes.retrieve(bl.getIncidentType()).getName());
					bl.setStatusName(BlotterStatus.typeName(bl.getStatus()));
					
					Blotters blot2 = Blotters.retrieveIncidentInformation(bl.getId());
					bl.setIncidentDetails(blot2.getIncidentDetails());
					bl.setIncidentSolutions(blot2.getIncidentSolutions());
					
					blotters.add(bl);
				}
			}	
		}else{
		
			params = new String[2];
			sql = " AND blot.isactiveblot=1 AND (blot.blotdate>=? AND blot.blotdate<=?) ";
			/*params[0] = getSearchDateTransFrom();
			params[1] = getSearchDateTransTo();*/
			params[0] = DateUtils.convertDate(getCalendarFrom(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			params[1] = DateUtils.convertDate(getCalendarTo(), com.italia.marxmind.bris.enm.DateFormat.YYYY_MM_DD());
			
			for(Blotters bl : Blotters.retrieve(sql, params)){
				bl.setIncidentName(IncidentReportTypes.retrieve(bl.getIncidentType()).getName());
				bl.setStatusName(BlotterStatus.typeName(bl.getStatus()));
				
				Blotters blot2 = Blotters.retrieveIncidentInformation(bl.getId());
				bl.setIncidentDetails(blot2.getIncidentDetails());
				bl.setIncidentSolutions(blot2.getIncidentSolutions());
				
				blotters.add(bl);
			}
		
		}
		
			if(blotters!=null && blotters.size()==1){
				clickItem(blotters.get(0));
			}else{
				clearFlds();
			}
		
		}
	}
	
	public void saveData(){
		
		Login in = Login.getUserLogin();
		boolean isOkFeature = false;
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			isOkFeature = true;
		}else{
			if(Features.isEnabled(Feature.BLOTTERS)){
				isOkFeature = true;
			}
		}
		
		if(isOkFeature){
		
		Blotters blot = new Blotters();
		if(getBlottersData()!=null){
			blot = getBlottersData();
		}else{
			blot.setIsActive(1);
		}
		
		boolean isOk = true;
		
		if(getIncidentDate()==null){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide incident date");
		}
		
		if(getIncidentTime()==null || getIncidentTime().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide incident time");
		}
		
		if(getIncidentPlace()==null || getIncidentPlace().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide incident place");
		}
		
		
		if(getIncidentDetails()==null || getIncidentDetails().isEmpty()){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide incident details");
		}
		
		System.out.println("Solution: " + getIncidentSolutions());
		
		if(getIncidentSolutions()==null || getIncidentSolutions().isEmpty()){
			Application.addMessage(2, "Temporary solution", "Currently processing for solutions");
			setIncidentSolutions("Currently processing for solutions");
		}
		
		if(getReportingPersons().size()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide reporting person");
		}
		
		if(getSuspectedPersons().size()==0){
			isOk = false;
			Application.addMessage(3, "Error", "Please provide reported person");
		}
		
		
		if(isOk){
			
			blot.setDateTrans(getDateTrans());
			blot.setTimeTrans(getTimeTrans());
			blot.setIncidentDate(DateUtils.convertDate(getIncidentDate(),DateFormat.YYYY_MM_DD()));
			blot.setIncidentTime(getIncidentTime());
			blot.setIncidentPlace(getIncidentPlace());
			blot.setIncidentDetails(getIncidentDetails());
			blot.setIncidentSolutions(getIncidentSolutions());
			blot.setUserDtls(getUserDtls());
			blot.setIncidentType(getTypeId());
			blot.setStatus(getStatusId());
			blot = Blotters.save(blot);
			
			if(reportingPersons!=null && reportingPersons.size()>0){
				for(ReportingPerson rep : reportingPersons){
					rep.setDateTrans(blot.getDateTrans());
					rep.setIsActive(1);
					rep.setBlotters(blot);
					rep.save();
				}
			}
			
			if(suspectedPersons!=null && suspectedPersons.size()>0){
				for(SuspectedPerson sus : suspectedPersons){
					sus.setDateTrans(blot.getDateTrans());
					sus.setIsActive(1);
					sus.setBlotters(blot);
					sus.save();
				}
			}
			
			if(victimPersons!=null && victimPersons.size()>0){
				for(VictimPerson vic : victimPersons){
					vic.setDateTrans(blot.getDateTrans());
					vic.setIsActive(1);
					vic.setBlotters(blot);
					vic.save();
				}
			}
			
			if(luponPersonList!=null && luponPersons.size()>0){
				for(LuponPerson lup : luponPersons){
					lup.setDateTrans(blot.getDateTrans());
					lup.setIsActive(1);
					lup.setBlotters(blot);
					lup.save();
				}
			}
			
			Application.addMessage(1, "Success", "Successfully saved");
			setBlottersData(blot);
			loadBlotters();
		}
		
		}else{
			Application.addMessage(2, "System Restricted", "You are not allowed to use this module.");
		}
		
	}
	
	public void clearFlds(){
		setBlottersData(null);
		
		setDateTrans(null);
		setTimeTrans(null);
		setIncidentDate(null);
		setIncidentTime(null);
		setIncidentPlace(null);
		
		setIncidentDetails(null);
		setIncidentSolutions(null);
		
		setStatusId(1);
		setTypeId(1);
		
		reportingPersons = Collections.synchronizedList(new ArrayList<ReportingPerson>());
		suspectedPersons = Collections.synchronizedList(new ArrayList<SuspectedPerson>());
		minorPersons = Collections.synchronizedList(new ArrayList<MinorPerson>());
		victimPersons = Collections.synchronizedList(new ArrayList<VictimPerson>());
		luponPersons = Collections.synchronizedList(new ArrayList<LuponPerson>());
		
		setReportingPersonsList(null);
		setSuspectPersonsList(null);
		setVictimPersonsList(null);
		setLuponPersonList(null);
	}
	
	public void clickItem(Blotters blot){
		
		setBlottersData(blot);
		
		setDateTrans(blot.getDateTrans());
		setTimeTrans(blot.getTimeTrans());
		setIncidentDate(DateUtils.convertDateString(blot.getIncidentDate(), DateFormat.YYYY_MM_DD()));
		setIncidentTime(blot.getIncidentTime());
		setIncidentPlace(blot.getIncidentPlace());
		//setIncidentDetails(blot.getIncidentDetails());
		//setIncidentSolutions(blot.getIncidentSolutions());
		
		Blotters blt = Blotters.retrieveIncidentInformation(blot.getId());
		setIncidentDetails(blt.getIncidentDetails());
		setIncidentSolutions(blt.getIncidentSolutions());
		/*setTempIncidentDetails(blt.getIncidentDetails());
		setTempSolutions(blt.getIncidentSolutions());
		
		int sizeDtls = getTempIncidentDetails().length();
		int sizeSol = getTempSolutions().length();*/
		
		/*if(sizeDtls>100){
			setIncidentDetails(getTempIncidentDetails().substring(0,100)+"...");
		}else{*/
		//	setIncidentDetails(getTempIncidentDetails());
		//}
		
		/*if(sizeSol>100){
			setIncidentSolutions(getTempSolutions().substring(0, 100)+"...");
		}else{
			setIncidentSolutions(getTempSolutions());
		}*/
		
		setStatusId(blot.getStatus());
		setTypeId(blot.getIncidentType());
		
		loadPersons(blot);
		
	}
	
	public void loadDetails(){
		setIncidentDetails(getTempIncidentDetails());
	}
	public void loadSolutions(){
		setIncidentSolutions(getTempSolutions());
	}
	
	private void loadPersons(Blotters blot){
		
		reportingPersons = Collections.synchronizedList(new ArrayList<ReportingPerson>());
		String sql = " AND blot.blotid=? AND rps.isactiverep=1 AND blot.blotdate=?";
		String[] params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(ReportingPerson rep : ReportingPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(rep.getReportingPerson().getBarangay().getId());
			rep.getReportingPerson().setBarangay(bar);
			rep.getReportingPerson().setMunicipality(bar.getMunicipality());
			rep.getReportingPerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(rep.getReportingPerson().getPurok().getId());}catch(Exception e){}
			rep.getReportingPerson().setPurok(purok);
			
			reportingPersons.add(rep);
		}
		
		if(reportingPersons.size()>0){
			loadPersonDetails(reportingPersons.get(0));
		}
		
		suspectedPersons = Collections.synchronizedList(new ArrayList<SuspectedPerson>());
		sql = " AND blot.blotid=? AND sus.isactivesus=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(sus.getSuspectedPerson().getBarangay().getId());
			sus.getSuspectedPerson().setBarangay(bar);
			sus.getSuspectedPerson().setMunicipality(bar.getMunicipality());
			sus.getSuspectedPerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(sus.getSuspectedPerson().getPurok().getId());}catch(Exception e){}
			sus.getSuspectedPerson().setPurok(purok);
			
			suspectedPersons.add(sus);
		}
		
		if(suspectedPersons.size()>0){
			loadPersonDetails(suspectedPersons.get(0));
		}
		
		victimPersons = Collections.synchronizedList(new ArrayList<VictimPerson>());
		sql = " AND blot.blotid=? AND vic.isactivevic=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(VictimPerson vic : VictimPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(vic.getVictimePerson().getBarangay().getId());
			vic.getVictimePerson().setBarangay(bar);
			vic.getVictimePerson().setMunicipality(bar.getMunicipality());
			vic.getVictimePerson().setProvince(bar.getProvince());
			
			Purok purok = new Purok();
			try{purok = Purok.retrieve(vic.getVictimePerson().getPurok().getId());}catch(Exception e){}
			vic.getVictimePerson().setPurok(purok);
			
			victimPersons.add(vic);
		}
		
		if(victimPersons.size()>0){
			loadPersonDetails(victimPersons.get(0));
		}
		
		luponPersons = Collections.synchronizedList(new ArrayList<LuponPerson>());
		sql = " AND blot.blotid=? AND lup.isactivelup=1 AND blot.blotdate=?";
		params = new String[2];
		params[0] = blot.getId() +"";
		params[1] = blot.getDateTrans();
		
		for(LuponPerson lup : LuponPerson.retrieve(sql, params)){
			Barangay bar = Barangay.retrieve(lup.getLuponPerson().getBarangay().getId());
			lup.getLuponPerson().setBarangay(bar);
			lup.getLuponPerson().setMunicipality(bar.getMunicipality());
			lup.getLuponPerson().setProvince(bar.getProvince());
			luponPersons.add(lup);
		}
		
		if(luponPersons.size()>0){
			loadPersonDetails(luponPersons.get(0));
		}
		
	}
	
	private void loadPersonDetails(Object obj){
		String listData="";
		int cnt = 1;
		int size = 0;
		if(obj instanceof ReportingPerson){
			
			size = getReportingPersons().size();
			
			for(ReportingPerson rep : getReportingPersons()){
				if(cnt==size){
					listData += rep.getReportingPerson().getFullname(); 
				}else{
					listData += rep.getReportingPerson().getFullname() +", ";
				}
				cnt++;
			}
			
			setReportingPersonsList(listData);
			
		}else if(obj instanceof SuspectedPerson){
			
			size = getSuspectedPersons().size();
			
			for(SuspectedPerson rep : getSuspectedPersons()){
				if(cnt==size){
					listData += rep.getSuspectedPerson().getFullname(); 
				}else{
					listData += rep.getSuspectedPerson().getFullname() +", ";
				}
				cnt++;
			}
			setSuspectPersonsList(listData);
			
		}else if(obj instanceof VictimPerson){
			
			size = getVictimPersons().size();
			
			for(VictimPerson rep : getVictimPersons()){
				if(cnt==size){
					listData += rep.getVictimePerson().getFullname(); 
				}else{
					listData += rep.getVictimePerson().getFullname() +", ";
				}
				cnt++;
			}
			setVictimPersonsList(listData);
		}else if(obj instanceof LuponPerson){
			
			size = getLuponPersons().size();
			
			for(LuponPerson rep : getLuponPersons()){
				if(cnt==size){
					listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName(); 
				}else{
					listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName() +", ";
				}
				cnt++;
			}
			setLuponPersonList(listData);
		}	
	}
	
	public void deleteRow(Blotters blot){
		String sql = " AND blot.blotid=? AND blot.isactiveblot=1";
		String[] params = new String[1];
		params[0] = blot.getId()+"";
		for(ReportingPerson per : ReportingPerson.retrieve(sql, params)){
			per.delete();
		}
		for(SuspectedPerson sus : SuspectedPerson.retrieve(sql, params)){
			sus.delete();
		}
		for(VictimPerson vic : VictimPerson.retrieve(sql, params)){
			vic.delete();
		}
		for(LuponPerson lup : LuponPerson.retrieve(sql, params)){
			lup.delete();
		}
		
		blot.delete();
		init();
		Application.addMessage(1, "Deleted", "Successfuly deleted");
	}
	
	public void loadCitizens(){
		
		
		citizens = Collections.synchronizedList(new ArrayList<Customer>());
		
		
		String sql = " AND cus.cusisactive=1 ";
		
		if(getSeachCitizens()!=null && !getSeachCitizens().isEmpty()){
			sql += " AND (cus.fullname like '%" + getSeachCitizens().replace("--", "") +"%' OR cus.cuscardno like '%"+ getSeachCitizens().replace("--", "") +"%')";
		}else{
			sql += " order by cus.customerid DESC limit 100;";
		}
		
		citizens =  Customer.retrieve(sql, new String[0]);
		
	}
	
	public void loadEmployee(){
		employees = Collections.synchronizedList(new ArrayList<Employee>());
		
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()){
			sql = " AND emp.isofficial=1 AND emp.isactiveemp=1 AND (emp.firstname like '%" + getSearchEmployee() +"%' OR emp.middlename like '%" + getSearchEmployee() + "%' OR emp.lastname like '%" + getSearchEmployee() +"%')";
		}else{
			sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 limit 10";
		}
		
		employees = Employee.retrieve(sql, params);
	}
	
	public void clickItemReportingPerson(Customer cus){
		
		ReportingPerson per = new ReportingPerson();
		
		
		per.setReportingPerson(cus);
		
		getReportingPersons().remove(per);
		getReportingPersons().add(per);
		
		String listData="";
		int cnt = 1;
		int size = getReportingPersons().size();
		
		for(ReportingPerson rep : getReportingPersons()){
			if(cnt==size){
				listData += rep.getReportingPerson().getFullname(); 
			}else{
				listData += rep.getReportingPerson().getFullname() +", ";
			}
			cnt++;
		}
		
		setReportingPersonsList(listData);
		
		Application.addMessage(1, "Adding", "Added " + cus.getFullname() + " on the list");
		
	}
	
	public void clickItemReportingPersonRemove(ReportingPerson rp){
		getReportingPersons().remove(rp);
		try{rp.delete();}catch(Exception e){}
		
		String listData="";
		int cnt = 1;
		int size = getReportingPersons().size();
		
		for(ReportingPerson rep : getReportingPersons()){
			if(cnt==size){
				listData += rep.getReportingPerson().getFullname(); 
			}else{
				listData += rep.getReportingPerson().getFullname() +", ";
			}
			cnt++;
		}
		
		setReportingPersonsList(listData);
		
		Application.addMessage(1, "Removing", "Removed " + rp.getReportingPerson().getFullname() + " on the list");
	}
	
	public void clickItemSuspectPerson(Customer cus){
		
		SuspectedPerson per = new SuspectedPerson();
		per.setSuspectedPerson(cus);
		getSuspectedPersons().remove(per);
		getSuspectedPersons().add(per);
		
		String listData="";
		int cnt = 1;
		int size = getSuspectedPersons().size();
		
		for(SuspectedPerson rep : getSuspectedPersons()){
			if(cnt==size){
				listData += rep.getSuspectedPerson().getFullname(); 
			}else{
				listData += rep.getSuspectedPerson().getFullname() +", ";
			}
			cnt++;
		}
		setSuspectPersonsList(listData);
		
		Application.addMessage(1, "Adding", "Added " + cus.getFullname() + " on the list");
		
	}
	
	public void clickItemSuspectPersonRemove(SuspectedPerson rp){
		getSuspectedPersons().remove(rp);
		try{rp.delete();}catch(Exception e){}
		String listData="";
		int cnt = 1;
		int size = getSuspectedPersons().size();
		
		for(SuspectedPerson rep : getSuspectedPersons()){
			if(cnt==size){
				listData += rep.getSuspectedPerson().getFullname(); 
			}else{
				listData += rep.getSuspectedPerson().getFullname() +", ";
			}
			cnt++;
		}
		
		setSuspectPersonsList(listData);
		
		Application.addMessage(1, "Removing", "Removed " + rp.getSuspectedPerson().getFullname() + " on the list");
	}
	
	public void clickItemVictimPerson(Customer cus){
		
		VictimPerson per = new VictimPerson();
		per.setVictimePerson(cus);
		getVictimPersons().remove(per);
		getVictimPersons().add(per);
		
		String listData="";
		int cnt = 1;
		int size = getVictimPersons().size();
		
		for(VictimPerson rep : getVictimPersons()){
			if(cnt==size){
				listData += rep.getVictimePerson().getFullname(); 
			}else{
				listData += rep.getVictimePerson().getFullname() +", ";
			}
			cnt++;
		}
		setVictimPersonsList(listData);
		
		Application.addMessage(1, "Adding", "Added " + cus.getFullname() + " on the list");
		
	}
	
	
	
	public void clickItemVictimPersonRemove(VictimPerson rp){
		getVictimPersons().remove(rp);
		try{rp.delete();}catch(Exception e){}
		String listData="";
		int cnt = 1;
		int size = getVictimPersons().size();
		
		for(VictimPerson rep : getVictimPersons()){
			if(cnt==size){
				listData += rep.getVictimePerson().getFullname(); 
			}else{
				listData += rep.getVictimePerson().getFullname() +", ";
			}
			cnt++;
		}
		
		setVictimPersonsList(listData);
		
		Application.addMessage(1, "Removing", "Removed " + rp.getVictimePerson().getFullname() + " on the list");
	}
	
	
	public void clickItemJuryPerson(Employee emp){
		
		LuponPerson per = new LuponPerson();
		per.setLuponPerson(emp);
		getLuponPersons().remove(per);
		getLuponPersons().add(per);
		
		String listData="";
		int cnt = 1;
		int size = getLuponPersons().size();
		
		for(LuponPerson rep : getLuponPersons()){
			if(cnt==size){
				listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName(); 
			}else{
				listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName() +", ";
			}
			cnt++;
		}
		
		setLuponPersonList(listData);
		
		Application.addMessage(1, "Adding", "Added " + emp.getFirstName() + " " + emp.getLastName() + " on the list");
	}
	
	public void clickItemJuryPersonRemove(LuponPerson rp){
		getLuponPersons().remove(rp);
		try{rp.delete();}catch(Exception e){}
		String listData="";
		int cnt = 1;
		int size = getLuponPersons().size();
		
		for(LuponPerson rep : getLuponPersons()){
			if(cnt==size){
				listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName(); 
			}else{
				listData += rep.getLuponPerson().getFirstName() + " " + rep.getLuponPerson().getLastName() +", ";
			}
			cnt++;
		}
		
		setLuponPersonList(listData);
		
		Application.addMessage(1, "Removing", "Removed " +  rp.getLuponPerson().getFirstName() + " " + rp.getLuponPerson().getLastName()  + " on the list");
	}
	
	public List<Blotters> getBlotters() {
		return blotters;
	}
	public void setBlotters(List<Blotters> blotters) {
		this.blotters = blotters;
	}
	public Blotters getBlottersData() {
		return blottersData;
	}
	public void setBlottersData(Blotters blottersData) {
		this.blottersData = blottersData;
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
	public String getTimeTrans() {
		if(timeTrans==null){
			timeTrans = DateUtils.getCurrentTIME();
		}
		return timeTrans;
	}
	public void setTimeTrans(String timeTrans) {
		this.timeTrans = timeTrans;
	}
	public Date getIncidentDate() {
		if(incidentDate==null){
			incidentDate = DateUtils.getDateToday();
		}
		return incidentDate;
	}
	public void setIncidentDate(Date incidentDate) {
		this.incidentDate = incidentDate;
	}
	public String getIncidentTime() {
		if(incidentTime==null){
			incidentTime = DateUtils.getCurrentTIME();
		}	
		return incidentTime;
	}
	public void setIncidentTime(String incidentTime) {
		this.incidentTime = incidentTime;
	}
	public String getIncidentPlace() {
		return incidentPlace;
	}
	public void setIncidentPlace(String incidentPlace) {
		this.incidentPlace = incidentPlace;
	}
	public String getIncidentDetails() {
		return incidentDetails;
	}
	public void setIncidentDetails(String incidentDetails) {
		this.incidentDetails = incidentDetails;
	}
	public String getIncidentSolutions() {
		return incidentSolutions;
	}
	public void setIncidentSolutions(String incidentSolutions) {
		this.incidentSolutions = incidentSolutions;
	}

	/*public String getSearchDateTransFrom() {
		if(searchDateTransFrom==null){
			searchDateTransFrom = DateUtils.getCurrentDateYYYYMMDD();
		}
		return searchDateTransFrom;
	}

	public void setSearchDateTransFrom(String searchDateTransFrom) {
		this.searchDateTransFrom = searchDateTransFrom;
	}

	public String getSearchDateTransTo() {
		if(searchDateTransTo==null){
			searchDateTransTo = DateUtils.getCurrentDateYYYYMMDD();
		}
		return searchDateTransTo;
	}

	public void setSearchDateTransTo(String searchDateTransTo) {
		this.searchDateTransTo = searchDateTransTo;
	}*/

	public UserDtls getUserDtls() {
		if(userDtls==null){
			userDtls = Login.getUserLogin().getUserDtls();
		}
		return userDtls;
	}

	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}

	public List<ReportingPerson> getReportingPersons() {
		return reportingPersons;
	}

	public void setReportingPersons(List<ReportingPerson> reportingPersons) {
		this.reportingPersons = reportingPersons;
	}

	public List<SuspectedPerson> getSuspectedPersons() {
		return suspectedPersons;
	}

	public void setSuspectedPersons(List<SuspectedPerson> suspectedPersons) {
		this.suspectedPersons = suspectedPersons;
	}

	public List<MinorPerson> getMinorPersons() {
		return minorPersons;
	}

	public void setMinorPersons(List<MinorPerson> minorPersons) {
		this.minorPersons = minorPersons;
	}

	public List<VictimPerson> getVictimPersons() {
		return victimPersons;
	}

	public void setVictimPersons(List<VictimPerson> victimPersons) {
		this.victimPersons = victimPersons;
	}

	public List<LuponPerson> getLuponPersons() {
		return luponPersons;
	}

	public void setLuponPersons(List<LuponPerson> luponPersons) {
		this.luponPersons = luponPersons;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public List<Customer> getCitizens() {
		return citizens;
	}

	public void setCitizens(List<Customer> citizens) {
		this.citizens = citizens;
	}

	public String getSearchEmployee() {
		return searchEmployee;
	}

	public void setSearchEmployee(String searchEmployee) {
		this.searchEmployee = searchEmployee;
	}

	public String getSeachCitizens() {
		return seachCitizens;
	}

	public void setSeachCitizens(String seachCitizens) {
		this.seachCitizens = seachCitizens;
	}

	public String getReportingPersonsList() {
		return reportingPersonsList;
	}

	public void setReportingPersonsList(String reportingPersonsList) {
		this.reportingPersonsList = reportingPersonsList;
	}

	public String getSuspectPersonsList() {
		return suspectPersonsList;
	}

	public void setSuspectPersonsList(String suspectPersonsList) {
		this.suspectPersonsList = suspectPersonsList;
	}

	public String getMinorPersonsList() {
		return minorPersonsList;
	}

	public void setMinorPersonsList(String minorPersonsList) {
		this.minorPersonsList = minorPersonsList;
	}

	public String getVictimPersonsList() {
		return victimPersonsList;
	}

	public void setVictimPersonsList(String victimPersonsList) {
		this.victimPersonsList = victimPersonsList;
	}

	public String getLuponPersonList() {
		return luponPersonList;
	}

	public void setLuponPersonList(String luponPersonList) {
		this.luponPersonList = luponPersonList;
	}

	public List getStatus() {
		
		status = new ArrayList<>();
		
		for(BlotterStatus stat : BlotterStatus.values()){
			status.add(new SelectItem(stat.getId(), stat.getName()));
		}
		
		return status;
	}

	public void setStatus(List status) {
		this.status = status;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public List getIncidentTypes() {
		
		incidentTypes = new ArrayList<>();
		
		for(IncidentReportTypes type : IncidentReportTypes.retrieve("SELECt * FROM incidenttype WHERE isactivetype=1 ORDER BY typename", new String[0])){
			incidentTypes.add(new SelectItem(type.getId(), type.getName()));
		}
		
		return incidentTypes;
	}

	public void setIncidentTypes(List incidentTypes) {
		this.incidentTypes = incidentTypes;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	public String getSearchdata() {
		return searchdata;
	}

	public void setSearchdata(String searchdata) {
		this.searchdata = searchdata;
	}
	
	public void printBlotter(Blotters blot){
		
		
		
		loadPersons(blot);
		
		String sql = " AND emp.isactiveemp=1 AND emp.isofficial=1 AND pos.posid=?";
		String[] params = new String[1];
		params[0] = Positions.CAPTAIN.getId()+"";
		
		Employee emp = Employee.retrieve(sql, params).get(0);
		String captain =  emp.getFirstName().toUpperCase() + " " + emp.getMiddleName().substring(0,1).toUpperCase() + ". " + emp.getLastName().toUpperCase();
		
		List<ClearanceRpt> reports = Collections.synchronizedList(new ArrayList<ClearanceRpt>());
		ClearanceRpt cl = new ClearanceRpt();
		cl.setF1("");
		reports.add(cl);
		String REPORT_NAME = BLOTTER_REPORT_NAME;
		String path = REPORT_PATH + ReadConfig.value(Bris.BARANGAY_NAME).replace(" ", "") + Bris.SEPERATOR.getName();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, path);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
		UserDtls user = Login.getUserLogin().getUserDtls();
			HashMap param = new HashMap();
			
			param.put("PARAM_CAPTAIN", "HON. "+captain);
			
			param.put("PARAM_PRINTED_DATE", "Printed Date: "+DateUtils.getCurrentDateMMMMDDYYYY());
			
			param.put("PARAM_PROVINCE_NAME", "Province of " + PROVINCE);
			param.put("PARAM_MUNICIPALITY_NAME", "Municipality of " + MUNICIPALITY);
			param.put("PARAM_BARANGAY_NAME", "BARANGAY " + BARANGAY.toUpperCase());
			
			param.put("PARAM_DATE_TIME_REPORTED", blot.getDateTrans() + " " + blot.getTimeTrans());
			param.put("PARAM_INCIDENT_DATE_TIME", blot.getIncidentDate() + " " + blot.getIncidentTime());
			param.put("PARAM_INCIDENT_PLACE", blot.getIncidentPlace());
			
			param.put("PARAM_INCIDENT_TYPE", IncidentReportTypes.retrieve(blot.getIncidentType()).getName());
			param.put("PARAM_INCIDENT_STATUS", BlotterStatus.typeName(blot.getStatus()));
			
			String value = "";
			int cnt = 1;
			//int size = getReportingPersons().size();
			for(ReportingPerson rep : getReportingPersons()){
				/*String address = rep.getReportingPerson().getPurok().getPurokName() + ", " + 
						rep.getReportingPerson().getBarangay().getName() + ", " + 
						rep.getReportingPerson().getMunicipality().getName() + ", " + 
						rep.getReportingPerson().getProvince().getName();*/
				String address =  rep.getReportingPerson().getPurok().getId()==0? "" : rep.getReportingPerson().getPurok().getPurokName() +", ";
					   address += rep.getReportingPerson().getBarangay().getId()==0? "" : rep.getReportingPerson().getBarangay().getName() + ", ";
					   address += rep.getReportingPerson().getMunicipality().getId()==0? "" : rep.getReportingPerson().getMunicipality().getName() + ", ";
					   address += rep.getReportingPerson().getProvince().getId()==0? "" : rep.getReportingPerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+rep.getReportingPerson().getFullname() + " - " + address + "\t"+ rep.getReportingPerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+rep.getReportingPerson().getFullname() + "\t" + address + "\t"+ rep.getReportingPerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_REPORTING_PERSONS", value);
			
			value += "\nRespondent/s:\n";
			cnt = 1;
			//size = getSuspectedPersons().size();
			for(SuspectedPerson sus : getSuspectedPersons()){
				/*String address = sus.getSuspectedPerson().getPurok().getPurokName() + ", " + 
						sus.getSuspectedPerson().getBarangay().getName() + ", " + 
						sus.getSuspectedPerson().getMunicipality().getName() + ", " + 
						sus.getSuspectedPerson().getProvince().getName();*/
				String address =  sus.getSuspectedPerson().getPurok().getId()==0? "" : sus.getSuspectedPerson().getPurok().getPurokName() +", ";
				   address += sus.getSuspectedPerson().getBarangay().getId()==0? "" : sus.getSuspectedPerson().getBarangay().getName() + ", ";
				   address += sus.getSuspectedPerson().getMunicipality().getId()==0? "" : sus.getSuspectedPerson().getMunicipality().getName() + ", ";
				   address += sus.getSuspectedPerson().getProvince().getId()==0? "" : sus.getSuspectedPerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+sus.getSuspectedPerson().getFullname() + " - " + address + "\t"+ sus.getSuspectedPerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+sus.getSuspectedPerson().getFullname() + "\t" + address + "\t"+ sus.getSuspectedPerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_REPORTED_PERSONS", value);
			
			value += "\nVictim/s:\n";
			cnt = 1;
			//size = getVictimPersons().size();
			for(VictimPerson vic : getVictimPersons()){
				/*String address = vic.getVictimePerson().getPurok().getPurokName() + ", " + 
						vic.getVictimePerson().getBarangay().getName() + ", " + 
						vic.getVictimePerson().getMunicipality().getName() + ", " + 
						vic.getVictimePerson().getProvince().getName();*/
				String address =  vic.getVictimePerson().getPurok().getId()==0? "" : vic.getVictimePerson().getPurok().getPurokName() +", ";
				   address += vic.getVictimePerson().getBarangay().getId()==0? "" : vic.getVictimePerson().getBarangay().getName() + ", ";
				   address += vic.getVictimePerson().getMunicipality().getId()==0? "" : vic.getVictimePerson().getMunicipality().getName() + ", ";
				   address += vic.getVictimePerson().getProvince().getId()==0? "" : vic.getVictimePerson().getProvince().getName();
				//if(cnt==size){
					value += cnt +".)"+vic.getVictimePerson().getFullname() + " - " + address + "\t"+ vic.getVictimePerson().getContactno() +"\n";
				/*}else{
					value += cnt +".)"+vic.getVictimePerson().getFullname() + "\t" + address + "\t"+ vic.getVictimePerson().getContactno() +"\n";
				}*/
				cnt++;
			}
			
			value += "\nNarratives:\n";
			value += blot.getIncidentDetails()+"\n";
			value += "\nSolution Details:\n";
			value += blot.getIncidentSolutions()+"\n";
			//param.put("PARAM_INCIDENT_DETAILS", blot.getIncidentDetails());
			//param.put("PARAM_INCIDENT_SOLUTIONS", blot.getIncidentSolutions());
			
			//param.put("PARAM_INCIDENT_VICTIM_PERSONS", value);
			
			value += "\nThis incident report was handled by the following barangay incident board committee:\n";
			cnt = 1;
			//size = getLuponPersons().size();
			for(LuponPerson lup : getLuponPersons()){
				//if(cnt==size){
					value +="\t"+ cnt +".)"+(lup.getLuponPerson().getIsOfficial()==1? "HON. " : "") + lup.getLuponPerson().getFirstName() + " " + lup.getLuponPerson().getLastName()+"\n";
				/*}else{
					value +="\t"+ cnt +".)"+(lup.getLuponPerson().getIsOfficial()==1? "HON. " : "") + lup.getLuponPerson().getFirstName() + " " + lup.getLuponPerson().getLastName() +"\n ";
				}*/
				cnt++;
			}
			
			//param.put("PARAM_INCIDENT_LUPON_PERSONS", value);
			param.put("PARAM_INCIDENT_REPORTING_PERSONS", value);
			
			
			String userName = user.getFirstname() + " " + user.getLastname();
			param.put("PARAM_INCIDENT_PREPARED_BY", "Prepared By: "+ userName);
			
			//logo
			String officialLogo = path + "logo.png";
			try{File file = new File(officialLogo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_BARANGAY_SEAL", off);
			}catch(Exception e){}
			
			String lakesebuofficialseal = path + "municipalseal.png";
			try{File file1 = new File(lakesebuofficialseal);
			FileInputStream off2 = new FileInputStream(file1);
			param.put("PARAM_MUNICIPAL_SEAL", off2);
			}catch(Exception e){}
			
			//logo
			String logo = path + "barangaylogotrans.png";
			try{File file = new File(logo);
			FileInputStream off = new FileInputStream(file);
			param.put("PARAM_BARANGAY_LOGO_TRANS", off);
			}catch(Exception e){}
			
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

	public String getTempIncidentDetails() {
		return tempIncidentDetails;
	}

	public void setTempIncidentDetails(String tempIncidentDetails) {
		this.tempIncidentDetails = tempIncidentDetails;
	}

	public String getTempSolutions() {
		return tempSolutions;
	}

	public void setTempSolutions(String tempSolutions) {
		this.tempSolutions = tempSolutions;
	}

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
	
}
