package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.italia.marxmind.bris.controller.Scheduler;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @since 10/26/2018
 * @version 1.0
 *
 */

@Named
@ViewScoped
public class EventBean implements Serializable{

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
     		public void loadEvents(LocalDateTime start, LocalDateTime end) {
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
			
			
	    	//DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
	    	
	    	//LocalDateTime dateFrom = formatter.parse(eventStart);
	    	//LocalDateTime dateTo = formatter.parse(eventEnd);
	    	//LocalDateTime dateFrom = LocalDateTime.parse(eventStart,formatter);
	    	//LocalDateTime dateTo = LocalDateTime.parse(eventEnd,formatter);
	        
	        //eve = new DefaultScheduleEvent(title, dateFrom, dateTo);
	    	
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;//DateTimeFormatter.ofPattern("dd-MM-yyyy,HH:mm:ss a");
	    	eve = DefaultScheduleEvent.builder()
	        		.title(title)
	        		.startDate(LocalDateTime.parse(eventStart,formatter))
	        		.endDate(LocalDateTime.parse(eventEnd,formatter))
	        		.build();
	    	
	    	
	        eve.setId(sc.getId()+"");
	        
	        System.out.println("loadEvent id sc : " + sc.getId());
	        System.out.println("loadEvent id eve : " + eve.getId());
	        
		}catch(Exception e) {}
	        
		return eve;
	}
	
	public void addEvent() {
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
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 
		
    	LocalDateTime dateStart = event.getStartDate();
    	LocalDateTime dateNext = event.getEndDate();
    	
    	String f = formatter.format(dateStart);
    	String t = formatter.format(dateNext);
    	System.out.println("save date from " + f.split("T")[0] + " = " + f.split("T")[1]);
    	System.out.println("save date to " + t.split("T")[0] + " = " + t.split("T")[1]);
    	
    	//String dbStartDate = new SimpleDateFormat("yyyy-MM-dd").format(f.split("T")[0]);
    	//String dbEndDate = new SimpleDateFormat("yyyy-MM-dd").format(t.split("T")[0]);
    	String dbStartDate = f.split("T")[0];
    	String dbEndDate = t.split("T")[0];
    	
    	//String dateFromValue = new SimpleDateFormat("dd-MMM-yyyy").format(f.split("T")[0]);
        //String dateToValue = new SimpleDateFormat("dd-MMM-yyyy").format(t.split("T")[0]);
        
        String dateFromValue = f.split("T")[0];
        String dateToValue = t.split("T")[0];
        
        //String timeFromValue = new SimpleDateFormat("HH:mm:ss aaa").format(f.split("T")[1]);
        //String timeToValue = new SimpleDateFormat("HH:mm:ss aaa").format(t.split("T")[1]);
        String timeFromValue = f.split("T")[1];
        String timeToValue = t.split("T")[1];       
        String checkTime = timeFromValue.split(":")[0];
        
        if("00".equalsIgnoreCase(checkTime)){
        	timeFromValue="07:00:00 AM";
        	timeToValue="12:00:00 PM";
        }
        
       
        
        try{
        
        String fromDate = dateFromValue+","+timeFromValue;
        String toDate = dateToValue+","+timeToValue;
       // DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
       // Date dateFrom = formatter.parse(fromDate);
        //Date dateTo = formatter.parse(toDate);
        
        //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;//DateTimeFormatter.ofPattern("d-MMM-yyyy,HH:mm:ss a");
        //LocalDateTime dateFrom = LocalDateTime.parse(fromDate,formatter);
        //LocalDateTime dateTo = LocalDateTime.parse(toDate,formatter);
        
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
        
        DefaultScheduleEvent def = DefaultScheduleEvent.builder()
        		.title(title)
        		.startDate(LocalDateTime.parse(fromDate,formatter))
        		.endDate(LocalDateTime.parse(toDate,formatter))
        		.build();
       event = def; 
       // event = new DefaultScheduleEvent(title, dateFrom, dateTo);
        
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
       // DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
        try{
        //Date dateFrom = formatter.parse(fromDate);
        //Date dateTo = formatter.parse(toDate);
        event.setId(sc.getId()+"");
        eventModel.deleteEvent(event);
        //ScheduleEvent ev = new DefaultScheduleEvent(title, dateFrom, dateTo);
        
        
        DateTimeFormatter formatter =DateTimeFormatter.ISO_LOCAL_DATE_TIME;// DateTimeFormatter.ofPattern("dd-MM-yyyy,HH:mm:ss a");
        ScheduleEvent ev = DefaultScheduleEvent.builder()
        		.title(title)
        		.startDate(LocalDateTime.parse(fromDate,formatter))
        		.endDate(LocalDateTime.parse(toDate,formatter))
        		.build();
        
        ev.setId(sc.getId()+"");
        event = ev;
        
        
        System.out.println("onSelectEvent >> id : " +event.getId());
        }catch(Exception e){}
    }
	
	public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
        event = selectEvent.getObject();
        
        System.out.println("Event select : " + event.getId());
    }
	
	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
        //Date date = (Date) selectEvent.getObject();
       // event = new DefaultScheduleEvent("", date, date);
       
		/*LocalDateTime date = (LocalDateTime)selectEvent.getObject();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy,HH:mm:ss aaa");
        event = DefaultScheduleEvent.builder()
        		.title("")
        		.startDate(date)
        		.endDate(date)
        		.build();*/
		event = DefaultScheduleEvent.builder().startDate(selectEvent.getObject()).endDate(selectEvent.getObject().plusHours(1)).build(); 
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
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDeltaStart() + ", Minute delta:" + event.getDayDeltaEnd());
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
