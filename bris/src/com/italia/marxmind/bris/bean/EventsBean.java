package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.italia.marxmind.bris.controller.Reservation;
import com.italia.marxmind.bris.controller.ReservationXML;
import com.italia.marxmind.bris.controller.Scheduler;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 10/26/2018
 * @version 1.0
 *
 */

@ManagedBean(name="eventBean", eager=true)
@ViewScoped
public class EventsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 65757687431L;
	
	private ScheduleModel eventModel;
	private ScheduleEvent event = new DefaultScheduleEvent();
	private List<Scheduler> scheds = Collections.synchronizedList(new ArrayList<Scheduler>());
	
	@PostConstruct
	public void init() {
		eventModel = new DefaultScheduleModel();
		initEvent();
	}
	
	public void initEvent() {
		scheds = Collections.synchronizedList(new ArrayList<Scheduler>());
		eventModel = new DefaultScheduleModel();
		try {
		String sql = " AND (startdate>=? OR endate<=?) ORDER BY startdate";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentDateYYYYMMDD();
		params[1] = DateUtils.getCurrentDateYYYYMMDD();
		List<ScheduleEvent> sched = Collections.synchronizedList(new ArrayList<ScheduleEvent>());
		for(Scheduler sc : Scheduler.retrieve(sql, params)) {
			sched.add(loadEvent(sc));
			memosContent(sc);
		}
		 
		 eventModel = new LazyScheduleModel() {
     		@Override
     		public void loadEvents(Date start, Date end) {
     			for(ScheduleEvent event : sched) {
     				System.out.println("lazy id " + event.getId());
     				addEvent(event);
     			}
     			
     		}
     	};
     	 
		}catch(Exception e) {}
		
	}
	
	private void memosContent(Scheduler sc) {
		
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
    	
    	int lenWords = notes[2].length();
    	String desc = "";
    	if(lenWords>=10){
    		desc = notes[2].substring(0, 10);
    		desc +="...";
    	}else{
    		desc = notes[2];
    	}
    	
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
		
		scheds.add(sc);
	}
	
	private ScheduleEvent loadEvent(Scheduler sc) {
		ScheduleEvent eve = new DefaultScheduleEvent();
			
		try {
			
			String[] notes = sc.getNotes().split("<*>");
			String eventStart = notes[0];
			String eventEnd = notes[1];
			String title = notes[2];
			
	    	DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
			Date dateFrom = formatter.parse(eventStart);
	        Date dateTo = formatter.parse(eventEnd);
	        
	        eve = new DefaultScheduleEvent(title, dateFrom, dateTo); 
	        eve.setId(sc.getId()+"");
	        
	        System.out.println("loadEvent id sc : " + sc.getId());
	        System.out.println("loadEvent id eve : " + eve.getId());
	        
		}catch(Exception e) {}
	        
		return eve;
	}
	
	public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null){
        	event = save(event,1);
            eventModel.addEvent(event); 
            System.out.println("addEvent id is  null");
        }else{
        	event = save(event,2);
            eventModel.updateEvent(event);
            System.out.println("addEvent id is not null " + event.getId());
        }
        
         event = new DefaultScheduleEvent();
         init();
    }
	
	public void removeEvent(){
    	if(event.getId() != null){
    		System.out.println("Event ID: " + event.getId());
    		eventModel.deleteEvent(event);
    		String[] params = new String[1];
    		params[0] = event.getId();
    		Scheduler.delete("UPDATE scheduler SET isactivesc=0 WHERE schedid=?", params);
    		init();
    	}
    }
	
	public void deleteDateSched(Scheduler rs){
		event.setId(rs.getId()+"");
		eventModel.deleteEvent(event);
		rs.delete();
		init();
    }
	
	private ScheduleEvent save(ScheduleEvent event, int type){
    	
		System.out.println("save event : event id : " + event.getId() + " type:  " + type);
		
    	Date dateStart = event.getStartDate();
    	Date dateNext = event.getEndDate();
    	
    	String dbStartDate = new SimpleDateFormat("yyyy-MM-dd").format(dateStart);
    	String dbEndDate = new SimpleDateFormat("yyyy-MM-dd").format(dateNext);
    	
    	String dateFromValue = new SimpleDateFormat("dd-MMM-yyyy").format(dateStart);
        String dateToValue = new SimpleDateFormat("dd-MMM-yyyy").format(dateNext);
        
        String timeFromValue = new SimpleDateFormat("HH:mm:ss aaa").format(dateStart);
        String timeToValue = new SimpleDateFormat("HH:mm:ss aaa").format(dateNext);
               
        String checkTime = timeFromValue.split(":")[0];
        
        if("00".equalsIgnoreCase(checkTime)){
        	timeFromValue="07:00:00 AM";
        	timeToValue="12:00:00 PM";
        }
        
       
        
        try{
        
        String fromDate = dateFromValue+","+timeFromValue;
        String toDate = dateToValue+","+timeToValue;
        DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
        Date dateFrom = formatter.parse(fromDate);
        Date dateTo = formatter.parse(toDate);
        String title = event.getTitle()==null? "" : (event.getTitle().isEmpty()? "" : event.getTitle());
        Scheduler sc = new Scheduler();
        
        if(type==1){
        	sc.setStartDate(dbStartDate);
        	sc.setEndDate(dbEndDate);
        	sc.setNotes(fromDate +"<*>" + toDate + "<*>" + title);
        	sc.setIsActive(1);
        	sc.save();
        }else if(type==2){
        	sc.setId(Long.valueOf(event.getId()));
        	sc.setStartDate(dbStartDate);
        	sc.setEndDate(dbEndDate);
        	sc.setNotes(fromDate +"<*>" + toDate + "<*>" + title);
        	sc.save();
        }else if(type==3){
        	String sql = "DELETE FROM scheduler WHERE schedid=" + event.getId();
        	Scheduler.delete(sql, new String[0]);
        	sc.setId(Long.valueOf(event.getId()));
        	sc.setStartDate(dbStartDate);
        	sc.setEndDate(dbEndDate);
        	sc.setNotes(fromDate +"<*>" + toDate + "<*>" + title);
        	sc.save();
        }
        
        event = new DefaultScheduleEvent(title, dateFrom, dateTo);
        
        }catch(Exception e){}
        
        return event;
    }
	
	public void onSelectEvent(Scheduler sc){
		
		String[] notes = sc.getNotes().split("<*>");
		String startDate = notes[0];
		String endDate = notes[1];
		String title = notes[2];
		
    	String dateFromValue = startDate.split(",")[0];
    	String timeFromValue = startDate.split(",")[1];
    	
    	String dateToValue = endDate.split(",")[0];
    	String timeToValue = endDate.split(",")[1];
    	
    	String fromDate = dateFromValue+","+timeFromValue;
        String toDate = dateToValue+","+timeToValue;
        DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
        try{
        Date dateFrom = formatter.parse(fromDate);
        Date dateTo = formatter.parse(toDate);
        event.setId(sc.getId()+"");
        eventModel.deleteEvent(event);
        ScheduleEvent ev = new DefaultScheduleEvent(title, dateFrom, dateTo); 
        ev.setId(sc.getId()+"");
        event = ev;
        
        
        System.out.println("onSelectEvent >> id : " +event.getId());
        }catch(Exception e){}
    }
	
	public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        
        System.out.println("Event select : " + event.getId());
    }
	
	public void onDateSelect(SelectEvent selectEvent) {
        Date date = (Date) selectEvent.getObject();
        event = new DefaultScheduleEvent("", date, date);
    }
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
    	ScheduleEvent ev = save(event.getScheduleEvent(),3);
    	System.out.println("onEventMove : id is " + ev.getId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        init(); 
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
    	
    	ScheduleEvent ev = save(event.getScheduleEvent(),3);
    	System.out.println("onEventResize : id is " + ev.getId());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         init();
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public ScheduleModel getEventModel() {
		return eventModel;
	}
	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}
	public ScheduleEvent getEvent() {
		return event;
	}
	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public List<Scheduler> getScheds() {
		return scheds;
	}

	public void setScheds(List<Scheduler> scheds) {
		this.scheds = scheds;
	}

}
