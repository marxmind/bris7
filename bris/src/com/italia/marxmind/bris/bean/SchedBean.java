package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 05/04/2017
 * @version 1.0 This functionality has been changed. Please check Events class
 */
@ManagedBean(name = "schedBean", eager = true)
@ViewScoped
@Deprecated
public class SchedulerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000077786554543L;

	private ScheduleModel eventModel;

	private ScheduleModel lazyEventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	private List<Reservation> rvs;

	@PostConstruct
	public void init() {
		/*
		 * eventModel = new DefaultScheduleModel(); // eventModel = new
		 * LazyScheduleModel(); try{ List<Reservation> val =
		 * ReservationXML.readReservationXML(); if(val.size()>0){ for(Reservation rs :
		 * val){ ScheduleEvent eve = new DefaultScheduleEvent(); eve = loadSchedule(rs);
		 * eventModel.addEvent(eve); eve.setId(rs.getId()); } rvs = new
		 * ArrayList<Reservation>(); Map<String, Reservation> scs =
		 * Collections.synchronizedMap(new HashMap<String, Reservation>());
		 * for(Reservation rc : val){
		 * 
		 * String dateFrom = rc.getStartDate().split(",")[0]; String hFrom =
		 * rc.getStartDate().split(",")[1]; String dateTo =
		 * rc.getEndDate().split(",")[0]; String hTo = rc.getEndDate().split(",")[1];
		 * 
		 * try{ int fhour = Integer.valueOf(hFrom.split(":")[0]); if(fhour>=13){
		 * DateUtils.timeTo12Format(time, isIncludePM) } hFrom = hFrom.split(" ")[0];
		 * hTo = hTo.split(" ")[0]; hFrom = DateUtils.timeTo12Format(hFrom, true); hTo =
		 * DateUtils.timeTo12Format(hTo, true); }catch(Exception e){}
		 * 
		 * 
		 * String fday,fmonth,fyear; String tday,tmonth,tyear;
		 * 
		 * fday = dateFrom.split("-")[0]; fmonth = dateFrom.split("-")[1]; fyear =
		 * dateFrom.split("-")[2];
		 * 
		 * tday = dateTo.split("-")[0]; tmonth = dateTo.split("-")[1]; tyear =
		 * dateTo.split("-")[2];
		 * 
		 * int lenWords = rc.getDescription().length(); String desc = "";
		 * if(lenWords>=10){ desc = rc.getDescription().substring(0, 10); desc +="...";
		 * }else{ desc = rc.getDescription(); }
		 * 
		 * if(fmonth.equalsIgnoreCase(tmonth)){ if(fday.equalsIgnoreCase(tday)){
		 * rc.setMemos(fmonth + " " + fday + " "+ tyear +" ("+ hFrom +"-"+ hTo +"): "
		 * +desc); scs.put(fmonth+fday+fyear+"-"+tmonth+tday+tyear, rc); }else{
		 * if(fyear.equalsIgnoreCase(tyear)){ rc.setMemos(fmonth + " " + fday + "-" +
		 * tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
		 * scs.put(fmonth+fday+fyear+"-"+tmonth+tday+tyear, rc); }else{
		 * rc.setMemos(fmonth + " " + fday + " "+ fyear +"-" + tday + " " + tyear +" ("+
		 * hFrom +"-"+ hTo +"): " +desc);
		 * scs.put(fmonth+fday+fyear+"-"+tmonth+tday+tyear, rc); } } }else{
		 * if(fyear.equalsIgnoreCase(tyear)){ rc.setMemos(fmonth + " " + fday + "-"+
		 * tmonth+ " " + tday + " " + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
		 * scs.put(fmonth+fday+fyear+"-"+tmonth+tday+tyear, rc); }else{
		 * rc.setMemos(fmonth + " " + fday + " "+ fyear + "-"+ tmonth+ " " + tday + " "
		 * + tyear +" ("+ hFrom +"-"+ hTo +"): " +desc);
		 * scs.put(fmonth+fday+fyear+"-"+tmonth+tday+tyear, rc); } }
		 * 
		 * 
		 * 
		 * //rvs.add(rc); }
		 * 
		 * Map<String, Reservation> sorted = new TreeMap<String,Reservation>(scs);
		 * for(Reservation rs : sorted.values()){ rvs.add(rs); }
		 * 
		 * 
		 * }
		 * 
		 * }catch(Exception e){ }
		 */

	}

	public Date getRandomDate(Date base) {
		Calendar date = Calendar.getInstance();
		date.setTime(base);
		date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1); // set random day of month

		return date.getTime();
	}

	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar.getTime();
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	private Calendar today() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar;
	}

	private Date previousDay8Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 8);

		return t.getTime();
	}

	private Date previousDay11Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 11);

		return t.getTime();
	}

	private Date today1Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 1);

		return t.getTime();
	}

	private Date theDayAfter3Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 3);

		return t.getTime();
	}

	private Date today6Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 6);

		return t.getTime();
	}

	private Date nextDay9Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 9);

		return t.getTime();
	}

	private Date nextDay11Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 11);

		return t.getTime();
	}

	private Date fourDaysLater3pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
		t.set(Calendar.HOUR, 3);

		return t.getTime();
	}

	private Date today2Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 2);
		System.out.println("today2Pm: " + t.getTime());
		return t.getTime();
	}

	private Date nextDay12NN() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 12);
		/*
		 * System.out.println("nextDay12NN: " + t.getTime());
		 * 
		 * String testDate = "29-Apr-2010,13:00:14 PM"; DateFormat formatter = new
		 * SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa"); Date date; try { date =
		 * formatter.parse(testDate); System.out.println("Print date "+date); } catch
		 * (ParseException e) { // TODO Auto-generated catch block e.printStackTrace();
		 * }
		 */

		return t.getTime();
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public void addEvent(ActionEvent actionEvent) {
		if (event.getId() == null) {
			event = save(event, 1);
			eventModel.addEvent(event);
			System.out.println("addEvent id is  null");
		} else {
			event = save(event, 2);
			eventModel.updateEvent(event);
			System.out.println("addEvent id is not null " + event.getId());
		}

		event = new DefaultScheduleEvent();
		init();
	}

	public void removeEvent() {
		if (event.getId() != null) {
			System.out.println("Event ID: " + event.getId());
			eventModel.deleteEvent(event);
			String[] val = new String[1];
			val[0] = event.getId();
			ReservationXML.deleteElement(val);
			init();
		}
	}

	private ScheduleEvent loadSchedule(Reservation rs) {

		String title = rs.getDescription();
		String startDate = rs.getStartDate();
		String endDate = rs.getEndDate();

		String dateFromValue = startDate.split(",")[0];
		String timeFromValue = startDate.split(",")[1];

		String dateToValue = endDate.split(",")[0];
		String timeToValue = endDate.split(",")[1];

		String fromDate = dateFromValue + "," + timeFromValue;
		String toDate = dateToValue + "," + timeToValue;
		DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
		try {
			Date dateFrom = formatter.parse(fromDate);
			Date dateTo = formatter.parse(toDate);

			ScheduleEvent ev = new DefaultScheduleEvent(title, dateFrom, dateTo);
			ev.setId(rs.getId());

			System.out.println("check load id " + ev.getId());

			return ev;
		} catch (Exception e) {
		}
		return new DefaultScheduleEvent();
	}

	public void deleteDateSched(Reservation rs) {
		String[] val = new String[1];
		val[0] = rs.getId();
		ReservationXML.deleteElement(val);
		event.setId(rs.getId());
		eventModel.deleteEvent(event);
		rvs.remove(rs);
		init();
	}

	private ScheduleEvent save(ScheduleEvent event, int type) {

		/*
		 * Date dateStart = event.getStartDate(); Date dateNext = event.getEndDate();
		 * 
		 * String dateFromValue = new SimpleDateFormat("dd-MMM-yyyy").format(dateStart);
		 * String dateToValue = new SimpleDateFormat("dd-MMM-yyyy").format(dateNext);
		 * 
		 * String timeFromValue = new
		 * SimpleDateFormat("HH:mm:ss aaa").format(dateStart); String timeToValue = new
		 * SimpleDateFormat("HH:mm:ss aaa").format(dateNext);
		 * 
		 * String checkTime = timeFromValue.split(":")[0];
		 * 
		 * if("00".equalsIgnoreCase(checkTime)){ timeFromValue="07:00:00 AM";
		 * timeToValue="12:00:00 PM"; }
		 * 
		 * 
		 * 
		 * try{
		 * 
		 * String fromDate = dateFromValue+","+timeFromValue; String toDate =
		 * dateToValue+","+timeToValue; DateFormat formatter = new
		 * SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa"); Date dateFrom =
		 * formatter.parse(fromDate); Date dateTo = formatter.parse(toDate);
		 * 
		 * 
		 * String[] val = new String[4];
		 * 
		 * val[0] = type==1? (ReservationXML.getLastId()+1) +"" : event.getId(); val[1]
		 * = event.getTitle(); val[2] = fromDate; val[3] = toDate;
		 * 
		 * if(type==1){ ReservationXML.addElement(val); }else if(type==2){
		 * ReservationXML.updateElement(val); }else if(type==3){
		 * ReservationXML.deleteElement(val); ReservationXML.addElement(val); }
		 * 
		 * event = new DefaultScheduleEvent(event.getTitle(), dateFrom, dateTo);
		 * 
		 * }catch(Exception e){}
		 */

		return event;
	}

	public void onSelectEvent(Reservation rs) {
		String title = rs.getDescription();
		String startDate = rs.getStartDate();
		String endDate = rs.getEndDate();

		String dateFromValue = startDate.split(",")[0];
		String timeFromValue = startDate.split(",")[1];

		String dateToValue = endDate.split(",")[0];
		String timeToValue = endDate.split(",")[1];

		String fromDate = dateFromValue + "," + timeFromValue;
		String toDate = dateToValue + "," + timeToValue;
		DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
		try {
			Date dateFrom = formatter.parse(fromDate);
			Date dateTo = formatter.parse(toDate);
			event.setId(rs.getId());
			eventModel.deleteEvent(event);
			ScheduleEvent ev = new DefaultScheduleEvent(title, dateFrom, dateTo);
			ev.setId(rs.getId());
			event = ev;
		} catch (Exception e) {
		}
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
		System.out.println("onEventSelect : id is " + event.getId());
	}

	public void onDateSelect(SelectEvent selectEvent) {
		// event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date)
		// selectEvent.getObject());

		Date date = (Date) selectEvent.getObject();
		event = new DefaultScheduleEvent("", date, date);
		System.out.println("onDateSelect : id is " + event.getId());
		System.out.println("Day Selected : start : " + event.getStartDate() + " end " + event.getEndDate());
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		ScheduleEvent ev = save(event.getScheduleEvent(), 3);
		System.out.println("onEventMove : id is " + ev.getId());
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		init();
		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {

		ScheduleEvent ev = save(event.getScheduleEvent(), 3);
		System.out.println("onEventResize : id is " + ev.getId());
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		init();
		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<Reservation> getRvs() {
		return rvs;
	}

	public void setRvs(List<Reservation> rvs) {
		this.rvs = rvs;
	}

}
