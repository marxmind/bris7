package com.italia.marxmind.bris.application;

import java.util.List;

import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.CaseFilling;
import com.italia.marxmind.bris.controller.Cases;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Employee;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.Scheduler;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.KPForms;
import com.italia.marxmind.bris.enm.Positions;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.utils.DateUtils;

public class DataMining {
	
	

	public static String infoAll() {
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		String note = "<p><h1>About Barangay "+ BARANGAY +"</h1></p><br/>";
		
		note += staffInfo();
		note += zoneInfo();
		note += citizenInfo();
		note += settlementInfo();
		note += activitiesInfo();
		
		return note;
	}
	
	public static String activitiesInfo() {
		String note = "<br/>";
		String sql = " AND (startdate>=? OR endate<=?) ORDER BY startdate";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentDateYYYYMMDD();
		params[1] = DateUtils.getCurrentDateYYYYMMDD();
		List<Scheduler> scs = Scheduler.retrieve(sql, params);
		if(scs!=null && scs.size()>0) {
			note +="<p>Scheduled Activities</p>";
			for(Scheduler sc : scs ) {
				note +="<ul>";
				note += "<li>"+ memosContent(sc).getMemos() +"</li>";
				note +="</ul>";
			}
		}else {
			note +="<p>No activities yet recorded</p>";
		}
		return note;
	}
	
private static Scheduler memosContent(Scheduler sc) {
		
		String[] notes = sc.getNotes().split("<*>");
		
		String dateFrom = notes[0].split(",")[0];
    	String hFrom = notes[0].split(",")[1];
    	String dateTo = notes[1].split(",")[0];
    	String hTo = notes[1].split(",")[1];
    	
    	try{
    		hFrom = hFrom.split(" ")[0];
    		hTo = hTo.split(" ")[0];
    		hFrom = DateUtils.timeTo12Format(hFrom, true);
    		hTo = DateUtils.timeTo12Format(hTo, true);
    	}catch(Exception e){}
    	
    	
    	String fday,fmonth,fyear;
    	String tday,tmonth,tyear;
    	
    	fday = dateFrom.split("-")[0];
    	fmonth = dateFrom.split("-")[1];
    	fyear = dateFrom.split("-")[2];
    	
    	tday = dateTo.split("-")[0];
    	tmonth = dateTo.split("-")[1];
    	tyear = dateTo.split("-")[2];
    	
    	String desc = "";
    	desc = notes[2];
    	
    	if(fmonth.equalsIgnoreCase(tmonth)){
    		if(fday.equalsIgnoreCase(tday)){
    			sc.setMemos(fmonth + " " + fday + " "+ tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
    		}else{
    			if(fyear.equalsIgnoreCase(tyear)){
    				sc.setMemos(fmonth + " " + fday + "-" + tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
    			}else{
    				sc.setMemos(fmonth + " " + fday + " "+ fyear +"-" + tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
    			}
    		}
    	}else{
    		if(fyear.equalsIgnoreCase(tyear)){
    			sc.setMemos(fmonth + " " + fday + "-"+ tmonth+ " " + tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
    		}else{
    			sc.setMemos(fmonth + " " + fday + " "+ fyear + "-"+ tmonth+ " " + tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
    		}
    	}
		
		return sc;
	}
	
	public static String casesInfo() {
		String note = "<br/>"; 
		
		String sql = " AND ciz.caseisactive=1 AND ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?";
		String[] params = new String[3];
		params[0] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		params[1] = CaseStatus.SETTLED.getId()+"";
		params[2] = CaseStatus.CANCELLED.getId()+"";
		
		note +="<p>Scheduled Settlement</p>";
		
		for(Cases cz : Cases.retrieve(sql, params)){
			sql = " AND ciz.caseid=? AND fil.filisactive=1 AND fil.settlementdate>=? ORDER BY fil.filid DESC LIMIT 1";
			params = new String[2];
			params[0] = cz.getId()+"";
			params[1] = DateUtils.getCurrentDateYYYYMMDD();
				List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
				for(CaseFilling fil : fils) {
				note +="<ul>";
				note +="<li>"+ cz.getKindName() + " on " + DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()) + " @" + fil.getSettlementTime();
				
				String notify = "";
				if(KPForms.KP_FORM9.getId()==fil.getFormType()) {
					notify = " ("+  DateUtils.dayNaming(fil.getCount()<9? "0"+fil.getCount() : fil.getCount()+"")+" settlement)";
				}
				
				note += "<ul><li>" + cz.getComplainants() + " vs "+ cz.getRespondents() + notify +"</li></ul></li>";
				note +="</ul>";
					
				}
		}
		return note;
	}
	
	public static String settlementInfo() {
		String note = "<br/>"; 
		
		String sql = " AND ciz.caseisactive=1 AND ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?";
		String[] params = new String[3];
		params[0] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		params[1] = CaseStatus.SETTLED.getId()+"";
		params[2] = CaseStatus.CANCELLED.getId()+"";

		note +="<p>Today's Settlement "+ DateUtils.convertDateToMonthDayYear(DateUtils.getCurrentDateYYYYMMDD()) +"</p>";
		
		boolean hasNoSettlement = true;
		for(Cases cz : Cases.retrieve(sql, params)){
			sql = " AND ciz.caseid=? AND fil.filisactive=1 AND fil.settlementdate=? ORDER BY fil.filid DESC LIMIT 1";
			params = new String[2];
			params[0] = cz.getId()+"";
			params[1] = DateUtils.getCurrentDateYYYYMMDD();
				List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
				for(CaseFilling fil : fils) {
				note +="<ul>";
				note +="<li>"+ cz.getKindName() + " @ " + fil.getSettlementTime();
				note += "<ul><li>" + cz.getComplainants() + " vs "+ cz.getRespondents() + "</li></ul></li>";
				note +="</ul>";
				
					hasNoSettlement = false;
				}
		}
		if(hasNoSettlement) {
			note = "<p><strong>NO SETTLEMENT FOR TODAY...</strong></p><br/>";
			
			sql = " AND ciz.caseisactive=1 AND ciz.casestatus!=? AND ciz.casestatus!=? AND ciz.casestatus!=?";
			params = new String[3];
			params[0] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
			params[1] = CaseStatus.SETTLED.getId()+"";
			params[2] = CaseStatus.CANCELLED.getId()+"";
			
			String tmpNote = "";
			tmpNote +="<p>Below are the list of upcoming settlement</p>";
			boolean hasInCommingSettlement = false;
			for(Cases cz : Cases.retrieve(sql, params)){
				sql = " AND ciz.caseid=? AND fil.filisactive=1 AND fil.settlementdate>=? ORDER BY fil.filid DESC LIMIT 1";
				params = new String[2];
				params[0] = cz.getId()+"";
				params[1] = DateUtils.getCurrentDateYYYYMMDD();
					List<CaseFilling> fils = CaseFilling.retrieve(sql, params);
					for(CaseFilling fil : fils) {
						tmpNote +="<ul>";
						tmpNote +="<li>"+ cz.getKindName() + " on " + DateUtils.convertDateToMonthDayYear(fil.getSettlementDate()) + " @" + fil.getSettlementTime();
						
						String notify = "";
						if(KPForms.KP_FORM9.getId()==fil.getFormType()) {
							notify = " ("+  DateUtils.dayNaming(fil.getCount()<9? "0"+fil.getCount() : fil.getCount()+"")+" settlement)";
						}
						
						tmpNote += "<ul><li>" + cz.getComplainants() + " vs "+ cz.getRespondents() + notify +"</li></ul></li>";
						tmpNote +="</ul>";
						hasInCommingSettlement = true;
					}
			}
			if(hasInCommingSettlement) {
				note += tmpNote;
			}else {
				note += "<p><strong>Please note that there are no upcoming settlement...</strong><p/>";
			}
			
		}
		return note;
	}
	
	public static String citizenInfo() {
		String note = "<br/>";
		
		String sql = "";
		String[] params = new String[0];
		
		int regCnt = Customer.getRecordedCitizen("");
		
		if(regCnt>0) {
			sql = " AND cus.cusisactive=1 LIMIT 1";
			Customer cus = Customer.retrieve(sql,params).get(0);
			
			note += "<p>BRIS System recorded data was started on " + DateUtils.convertDateToMonthDayYear(cus.getDateregistered()) + ".</p>";
			note += "<p>The first recorded data was <strong>"+ cus.getFullname() +"</strong></p>";
			note += "<br/>";
			sql = " AND cus.cusisactive=1 ORDER BY cus.customerid DESC LIMIT 1";
			cus = Customer.retrieve(sql,params).get(0);
			note += "<p>As of this moment the last recorded data is <strong>"+ cus.getFullname() +"</strong> on "+ DateUtils.convertDateToMonthDayYear(cus.getDateregistered()) +".</p>";
			
			note += "<br/>";
			
			note +="<p>There are <strong>"+ Customer.getRecordedCitizen(" AND cusgender='1'") +"</strong> Male and <strong>" + Customer.getRecordedCitizen(" AND cusgender='2'") +"</strong> Female were recorded.</p>";
			note += "<br/>";
			
		}else {
			note += "<p>No Recorded data yet....";
		}
		
		return note;
	}
	
	public static String zoneInfo() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String PROVINCE = ReadConfig.value(Bris.PROVINCE);
		
		String note = "<br/>";
		note += "<p>Barangay Purok/Zone/Sitio</p>";
		String sql = " AND bgy.bgisactive=1 AND bgy.bgname=? AND mun.munname=? AND prv.provname=?";
		String[] params = new String[3];
		params[0] = BARANGAY;
		params[1] = MUNICIPALITY;
		params[2] = PROVINCE;
		
		Barangay bar = Barangay.retrieve(sql, params).get(0);
		sql = " AND pur.isactivepurok=1 AND bgy.bgid=? ORDER BY pur.purokname";
		params = new String[1];
		params[0] = bar.getId()+"";
		int cnt = 1;
		for(Purok p : Purok.retrieve(sql, params)) {
			int maleCnt = Customer.getRecordedCitizen(" AND cusgender='1' AND purid="+p.getId());
			int femaleCnt = Customer.getRecordedCitizen(" AND cusgender='2' AND purid="+p.getId());
			int total = maleCnt + femaleCnt;
			note += "<p>"+ cnt +" "+p.getPurokName()+"("+total+")</p>";
			note +="<ul>";
			note +="<li>Male = "+ maleCnt +"</li>";
			note +="<li>Female = "+ femaleCnt +"</li>";
			note +="</ul>";
			cnt++;
		}
		
		return note;
	}
	
	public static String staffInfo() {
		
				String note = "<br/>";
		
				//present staff
				String sql = " AND emp.isactiveemp=1 AND emp.isresigned=0 ORDER BY pos.posid";
				List<Employee> ems = Employee.retrieve(sql, new String[0]); 
				
				if(ems!=null && ems.size()>0) {
					note +="<p>Barangay staff</p>";
					for(Employee em : ems) {
						note +="<ul>";
						String official = em.getIsOfficial()==1? "Hon. " : "";
						note += "<li>"+ official + em.getFirstName() + " " + em.getLastName() + " " + em.getPosition().getName() + " (" + DateUtils.convertDateToMonthDayYear(em.getDateRegistered()) +"-Present)";
						if(em.getIsOfficial()==1 && em.getPosition().getId()!=Positions.CAPTAIN.getId()) {
							note +="<ul>";
							note +="<li>" + em.getCommittee().getName() +"</li>";
							note +="</ul>";
						}
						note +="</li>";
						note +="</ul>";
					}
				}else {
					note +="<p>No present staff has been recored</p>";
				}
				
				sql = " AND emp.isactiveemp=1 AND emp.isresigned=1 ORDER BY pos.posid";
				ems = Employee.retrieve(sql, new String[0]); 
				
				if(ems!=null && ems.size()>0) {
					note +="<p>Previous staff</p>";
					for(Employee em : ems) {
						note +="<ul>";
						String official = em.getIsOfficial()==1? "Hon. " : "";
						note += "<li>"+ official + em.getFirstName() + " " + em.getLastName() + " " + em.getPosition().getName() + " ("+ DateUtils.convertDateToMonthDayYear(em.getDateRegistered()) +"-" + DateUtils.convertDateToMonthDayYear(em.getDateResigned()) +")</li>";
						note +="</ul>";
					}
				}
				
				return note;
	}
	
}
