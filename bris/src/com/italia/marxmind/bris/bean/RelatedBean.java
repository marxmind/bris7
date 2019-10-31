package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.TabChangeEvent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Education;
import com.italia.marxmind.bris.controller.Races;
import com.italia.marxmind.bris.enm.Religion;

/**
 * 
 * @author mark italia
 * @since 10/17/2017
 * @version 1.0
 *
 */
@ManagedBean(name="relBean", eager=true)
@ViewScoped
public class RelatedBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6556676785661L;
	
	private List<Education> educations = Collections.synchronizedList(new ArrayList<Education>());
	private List<Races> races = Collections.synchronizedList(new ArrayList<Races>());
	private String nameDescription;
	private String nameEducation;
	private boolean indigent;
	private String searchDescription;
	private Education educationData;
	private Races racesData;
	
	
	public void onTabChangeView(TabChangeEvent event){
		
		if("Educations".equalsIgnoreCase(event.getTab().getTitle())){
			clearFlds();
			loadEducations();
		}else if("Races".equalsIgnoreCase(event.getTab().getTitle())){
			clearFlds();
			loadRaces();
		}
		
	}
	
	@PostConstruct
	public void init(){
		loadEducations();
	}
	
	public void loadEducations(){
		
		educations = Collections.synchronizedList(new ArrayList<Education>());
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND ed.educationname like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		educations = Education.retrieve(sql, params);
		Collections.reverse(educations);
	}
	
	public void loadRaces(){
		
		races = Collections.synchronizedList(new ArrayList<Races>());
		String sql = "";
		String[] params = new String[0];
		
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
			sql += " AND rc.racename like '%"+ getSearchDescription().replace("--", "") +"%'";
		}
		
		races = Races.retrieve(sql, params);
		Collections.reverse(races);
	}
	
	public void saveData(String val){
		if("EDUCATION".equalsIgnoreCase(val)){
			Education edu = new Education();
			if(getEducationData()!=null){
				edu = getEducationData();
			}else{
				edu.setIsActive(1);
			}
			edu.setName(getNameEducation());
			edu.save();
			clearFlds();
			loadEducations();
			Application.addMessage(1, "Success", "Information has been successfully saved");
		}else if("RACES".equalsIgnoreCase(val)){
			Races race = new Races();
			if(getRacesData()!=null){
				race = getRacesData();
			}else{
				race.setIsActive(1);
			}
			race.setName(getNameDescription());
			race.setIsIndigent(isIndigent()==true? 1 : 0);
			race.save();
			clearFlds();
			loadRaces();
			Application.addMessage(1, "Success", "Information has been successfully saved");
		}
	}
	
	public void clickItem(Object obj){
		if(obj instanceof Education){
			Education edu = (Education)obj;
			setEducationData(edu);
			setNameDescription(edu.getName());
		}else if(obj instanceof Races){
			Races race = (Races)obj;
			setRacesData(race);
			setNameDescription(race.getName());
			setIndigent(race.getIsIndigent()==1? true : false);
		}
	}
	
	public void deleteRow(Object obj){
		if(obj instanceof Education){
			Education edu = (Education)obj;
			edu.delete();
			loadEducations();
		}else if(obj instanceof Races){
			Races race = (Races)obj;
			race.delete();
			loadRaces();
		}
	}
	
	public void clearFlds(){
		setNameDescription(null);
		setSearchDescription(null);
		setEducationData(null);
		setRacesData(null);
		setIndigent(false);
		setNameEducation(null);
	}
	
	public List<Education> getEducations() {
		return educations;
	}



	public void setEducations(List<Education> educations) {
		this.educations = educations;
	}



	public List<Races> getRaces() {
		return races;
	}



	public void setRaces(List<Races> races) {
		this.races = races;
	}



	public String getNameDescription() {
		return nameDescription;
	}



	public void setNameDescription(String nameDescription) {
		this.nameDescription = nameDescription;
	}

	public String getSearchDescription() {
		return searchDescription;
	}

	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}

	public Education getEducationData() {
		return educationData;
	}

	public void setEducationData(Education educationData) {
		this.educationData = educationData;
	}

	public Races getRacesData() {
		return racesData;
	}

	public void setRacesData(Races racesData) {
		this.racesData = racesData;
	}

	public boolean isIndigent() {
		return indigent;
	}

	public void setIndigent(boolean indigent) {
		this.indigent = indigent;
	}

	public String getNameEducation() {
		return nameEducation;
	}

	public void setNameEducation(String nameEducation) {
		this.nameEducation = nameEducation;
	}
	
	
}

