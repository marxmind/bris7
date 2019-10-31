package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.Education;
import com.italia.marxmind.bris.controller.EducationTrans;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Races;
import com.italia.marxmind.bris.controller.RacesTrans;
import com.italia.marxmind.bris.controller.ReligionTrans;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.EducationLevel;
import com.italia.marxmind.bris.enm.Religion;
import com.italia.marxmind.bris.utils.DateUtils;

@ManagedBean(name="addInfoBean", eager=true)
@ViewScoped
public class PersonAdditionalInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 565679346431L;
	
	private int relId;
	private List religions;
	
	private int edId;
	private List educations;
	private Map<Integer, Education> eduMap = Collections.synchronizedMap(new HashMap<Integer, Education>());
	
	private int typeId;
	private List types;
	
	private int raceId;
	private List races;
	private Map<Integer, Races> raceMap = Collections.synchronizedMap(new HashMap<Integer, Races>());
	
	private ReligionTrans religionData;
	private EducationTrans educationData;
	private RacesTrans racesData;
	
	public void clearFlds(){
		setRelId(0);
		setEdId(0);
		setTypeId(0);
		setRaceId(0);
		
		setReligionData(null);
		setEducationData(null);
		setRacesData(null);
	}
	
	@ManagedProperty("#{customerBean}")
	private CustomerBean personData;
	
	public void setPersonData(CustomerBean personData){
		this.personData=personData;
	}
	
	private UserDtls getCurrentUser(){
		return Login.getUserLogin().getUserDtls();
	}
	
	public void clickAdditionalInfo(){
		if(personData.getCustomer()!=null){
			clearFlds();
			loadAddInformation();
		}else{
			System.out.println("No Data");
			clearFlds();
			setRelId(0);
			setEdId(0);
			setRaceId(0);
			getReligions();
			getEducations();
			getRaces();
		}
	}
	
	public void saveInfo(String type){
		
		Customer customer = personData.getCustomer();
		
		if("RELIGION".equalsIgnoreCase(type)){
			
			ReligionTrans rel = new ReligionTrans();
			if(getReligionData()!=null){
				rel = getReligionData();
			}else{
				rel.setIsActive(1);
			}
			rel.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			rel.setReligionId(getRelId());
			rel.setIsPresentReligion(1);
			rel.setCustomer(customer);
			rel.setUserDtls(getCurrentUser());
			rel = ReligionTrans.save(rel);
			setReligionData(rel);
			Application.addMessage(1, "Success", "Successfully saved");
			
		}else if("EDUCATION".equalsIgnoreCase(type)){
			
			EducationTrans ed = new EducationTrans();
			if(getEducationData()!=null){
				ed = getEducationData();
			}else{
				ed.setIsActive(1);
			}
			ed.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			ed.setEducation(getEduMap().get(getEdId()));
			ed.setLevelId(getTypeId());
			ed.setIsPresentEducation(1);
			ed.setCustomer(customer);
			ed.setUserDtls(getCurrentUser());
			ed = EducationTrans.save(ed);
			setEducationData(ed);
			Application.addMessage(1, "Success", "Successfully saved");
			
		}else if("RACES".equalsIgnoreCase(type)){
			
			RacesTrans race = new RacesTrans();
			if(getRacesData()!=null){
				race = getRacesData();
			}else{
				race.setIsActive(1);
			}
			race.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			race.setRaces(getRaceMap().get(getRaceId()));
			race.setCustomer(customer);
			race.setUserDtls(getCurrentUser());
			race = RacesTrans.save(race);
			setRacesData(race);
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	public void loadAddInformation(){
		
		Customer customer = personData.getCustomer();
		
		String[] params = new String[1];
		params[0] = customer.getCustomerid()+"";
		String sql = " AND trn.isactiverel=1 AND trn.ispresentrel=1 AND cz.customerid=?";
		List<ReligionTrans> rels = ReligionTrans.retrieve(sql, params);
		if(rels.size()>0){
			setRelId(rels.get(0).getReligionId());
			setReligionData(rels.get(0));
		}
		
		params = new String[1];
		sql = " AND trn.isactiveedtran=1 AND trn.ispresented=1 AND cz.customerid=?";
		params[0] = customer.getCustomerid()+"";
		List<EducationTrans> eds = EducationTrans.retrieve(sql, params);
		if(eds.size()>0){
			setEdId(eds.get(0).getEducation().getId());
			setTypeId(eds.get(0).getLevelId());
			setEducationData(eds.get(0));
		}
		
		params = new String[1];
		sql = " AND trn.isactiveracetrans=1 AND cz.customerid=?";
		params[0] = customer.getCustomerid()+"";
		List<RacesTrans> rcs = RacesTrans.retrieve(sql, params);
		if(rcs.size()>0){
			setRaceId(rcs.get(0).getRaces().getId());
			setRacesData(rcs.get(0));
		}
	}
	
	
	public int getRelId() {
		return relId;
	}
	public void setRelId(int relId) {
		this.relId = relId;
	}
	public List getReligions() {
		religions = new ArrayList();
		for(Religion rel : Religion.values()){
			religions.add(new SelectItem(rel.getId(), rel.getName()));
		}
		return religions;
	}
	public void setReligions(List religions) {
		this.religions = religions;
	}
	public int getEdId() {
		if(edId==0){
			edId = 1;
		}
		return edId;
	}
	public void setEdId(int edId) {
		this.edId = edId;
	}
	public List getEducations() {
		
		educations = new ArrayList<>();
		eduMap = Collections.synchronizedMap(new HashMap<Integer, Education>());
		for(Education edu : Education.retrieve("", new String[0])){
			educations.add(new SelectItem(edu.getId(), edu.getName()));
			eduMap.put(edu.getId(), edu);
		}
		
		return educations;
	}
	public void setEducations(List educations) {
		this.educations = educations;
	}
	public int getRaceId() {
		return raceId;
	}
	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}
	public List getRaces() {
		races = new ArrayList<>();
		raceMap = Collections.synchronizedMap(new HashMap<Integer, Races>());
		for(Races race : Races.retrieve(" ORDER BY rc.racename", new String[0])){
			races.add(new SelectItem(race.getId(), race.getName()));
			raceMap.put(race.getId(), race);
		}
		return races;
	}
	public void setRaces(List races) {
		this.races = races;
	}
	public Map<Integer, Education> getEduMap() {
		return eduMap;
	}
	public void setEduMap(Map<Integer, Education> eduMap) {
		this.eduMap = eduMap;
	}
	public Map<Integer, Races> getRaceMap() {
		return raceMap;
	}
	public void setRaceMap(Map<Integer, Races> raceMap) {
		this.raceMap = raceMap;
	}

	public ReligionTrans getReligionData() {
		return religionData;
	}

	public void setReligionData(ReligionTrans religionData) {
		this.religionData = religionData;
	}

	public EducationTrans getEducationData() {
		return educationData;
	}

	public void setEducationData(EducationTrans educationData) {
		this.educationData = educationData;
	}

	public RacesTrans getRacesData() {
		return racesData;
	}

	public void setRacesData(RacesTrans racesData) {
		this.racesData = racesData;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public List getTypes() {
		types = new ArrayList<>();
		for(EducationLevel lvl : EducationLevel.values()){
			types.add(new SelectItem(lvl.getId(), lvl.getName()));
		}
		return types;
	}

	public void setTypes(List types) {
		this.types = types;
	}
	
	
}
