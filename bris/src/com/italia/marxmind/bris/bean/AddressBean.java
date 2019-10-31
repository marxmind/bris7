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
import com.italia.marxmind.bris.controller.Barangay;
import com.italia.marxmind.bris.controller.Municipality;
import com.italia.marxmind.bris.controller.Province;
import com.italia.marxmind.bris.controller.Purok;
import com.italia.marxmind.bris.controller.Regional;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 09/14/2017
 * @version 1.0
 *
 */
@ManagedBean(name="addBean", eager=true)
@ViewScoped
public class AddressBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7655676854321L;
	
	private String regionalName;
	private String provinceName;
	private String municipalName;
	private String barangayName;
	private String purokName;
	
	private List<Regional> regionals = Collections.synchronizedList(new ArrayList<Regional>());
	private List<Province> provinces = Collections.synchronizedList(new ArrayList<Province>());
	private List<Municipality> municipals = Collections.synchronizedList(new ArrayList<Municipality>());
	private List<Barangay> barangays = Collections.synchronizedList(new ArrayList<Barangay>());
	private List<Purok> puroks = Collections.synchronizedList(new ArrayList<Purok>());
	
	private Regional regionalData;
	private Province provinceData;
	private Municipality municipalData;
	private Barangay barangayData;
	private Purok purokData;
	
	private Regional regionalSelected;
	private Province provinceSelected;
	private Municipality municipalSelected;
	private Barangay barangaySelected;
	
	private String searchProvince;
	private String searchMunicipal;
	private String searchBarangay;
	private String searchPurok;
	
	private String searchRegions;
	
	public void onTabChangeView(TabChangeEvent event) {
		
		if("Regional".equalsIgnoreCase(event.getTab().getTitle())){
			clearSelectedData();
			loadRegional();
		}else if("Province".equalsIgnoreCase(event.getTab().getTitle())){
			clearSelectedData();
			loadProvince();
		}else if("Municipality".equalsIgnoreCase(event.getTab().getTitle())){
			clearSelectedData();
			loadMunicipal();
		}else if("Barangay".equalsIgnoreCase(event.getTab().getTitle())){
			clearSelectedData();
			loadBarangay();
		}else if("Purok/Sitio".equalsIgnoreCase(event.getTab().getTitle())){
			clearSelectedData();
			loadPurok();
		}
		
	}
	
	private void clearSelectedData(){
		setRegionalSelected(null);
		setProvinceSelected(null);
		setMunicipalSelected(null);
		setBarangaySelected(null);
	}
	
	@PostConstruct
	public void init(){
		loadRegional();
	}
	
	public void loadRegional(){
		regionals = Collections.synchronizedList(new ArrayList<Regional>());
		String sql = "SELECT * FROM regional WHERE isactivereg=1 ";
		if(getSearchRegions()!=null){
			sql += " AND regname like '%"+ getSearchRegions().replace("--", "") +"%'";
		}
		regionals = Regional.retrieve(sql, new String[0]);
		Collections.reverse(regionals);
	}
	
	public void loadProvince(){
		provinces = Collections.synchronizedList(new ArrayList<Province>());
		String sql = " AND prv.provisactive=1";
		String[] params = new String[0];
		if(getSearchProvince()!=null){
			sql += " AND prv.provname like '%"+ getSearchProvince().replace("--", "") +"%'";
		}
		
		if(getRegionalSelected()!=null){
			sql +=" AND rg.regid=" + getRegionalSelected().getId();
		}
		
		provinces = Province.retrieve(sql, params);
		Collections.reverse(provinces);
	}
	
	public void loadMunicipal(){
		municipals = Collections.synchronizedList(new ArrayList<Municipality>());
		String sql = " AND mun.munisactive=1 ";
		if(getSearchMunicipal()!=null){
			sql += " AND mun.munname like '%"+ getSearchMunicipal().replace("--", "") +"%'";
		}
		
		if(getRegionalSelected()!=null){
			sql +=" AND rg.regid=" + getRegionalSelected().getId();
		}
		
		if(getProvinceSelected()!=null){
			sql +=" AND prv.provid=" + getProvinceSelected().getId();
		}
		
		municipals = Municipality.retrieve(sql, new String[0]);
		Collections.reverse(municipals);
	}
	
	public void loadBarangay(){
		barangays = Collections.synchronizedList(new ArrayList<Barangay>());
		String sql = " AND bgy.bgisactive=1 ";
		if(getSearchBarangay()!=null){
			sql += " AND bgy.bgname like '%"+ getSearchBarangay().replace("--", "") +"%'";
		}
		
		if(getRegionalSelected()!=null){
			sql +=" AND rg.regid=" + getRegionalSelected().getId();
		}
		
		if(getProvinceSelected()!=null){
			sql +=" AND prv.provid=" + getProvinceSelected().getId();
		}
		
		if(getMunicipalSelected()!=null){
			sql +=" AND mun.munid=" + getMunicipalSelected().getId();
		}
		
		barangays = Barangay.retrieve(sql, new String[0]);
		Collections.reverse(barangays);
	}
	
	public void loadPurok(){
		puroks = Collections.synchronizedList(new ArrayList<Purok>());
		String sql = " AND pur.isactivepurok=1 ";
		if(getSearchPurok()!=null){
			sql += " AND pur.purokname like '%"+ getSearchPurok().replace("--", "") +"%'";
		}
		
		if(getMunicipalSelected()!=null){
			sql +=" AND mun.munid=" + getMunicipalSelected().getId();
		}
		
		if(getBarangaySelected()!=null){
			sql +=" AND bgy.bgid=" + getBarangaySelected().getId();
		}
		
		puroks = Purok.retrieve(sql, new String[0]);
		Collections.reverse(puroks);
	}
	
	public void saveData(String type){
		System.out.println("saving in " + type);
		boolean isOk = true;
		
		if("REGIONAL".equalsIgnoreCase(type)){
			Regional reg = new Regional();
			if(getRegionalData()!=null){
				reg = getRegionalData();
			}else{
				reg.setIsActive(1);
				
				if(checkName(getRegionalName(), type)){
					isOk = false;
					Application.addMessage(3, "Error", "Name is already available");
				}
				
			}
			reg.setName(getRegionalName());
			
			if(getRegionalName()==null || getRegionalName().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide regional name");
			}
			
			
			
			if(isOk){
				reg.save();
				clearData();
				loadRegional();
				Application.addMessage(1, "Success", "The regional name " + reg.getName() + " has been successfully saved");
			}
		}else if("PROVINCE".equalsIgnoreCase(type)){
			Province prov = new Province();
			if(getProvinceData()!=null){
				prov = getProvinceData();
			}else{
				prov.setIsActive(1);
				
				if(checkName(getProvinceName(), type)){
					isOk = false;
					Application.addMessage(3, "Error", "Name is already available");
				}
			}
			prov.setName(getProvinceName());
			prov.setRegional(getRegionalSelected());
			
			if(getProvinceName()==null || getProvinceName().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide province name");
			}
			
			if(getRegionalSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide region");
			}
			
			
			if(isOk){
				prov.save();
				clearData();
				loadProvince();
				Application.addMessage(1, "Success", "The province name " + prov.getName() + " has been successfully saved");
			}
		}else if("MUNICIPALITY".equalsIgnoreCase(type)){
			Municipality mun = new Municipality();
			if(getMunicipalData()!=null){
				mun = getMunicipalData();
			}else{
				mun.setIsActive(1);
				
				if(checkName(getMunicipalName(), type)){
					isOk = false;
					Application.addMessage(3, "Error", "Name is already available");
				}
			}
			
			if(getMunicipalName()==null || getMunicipalName().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide municipal name");
			}
			
			if(getRegionalSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide region");
			}
			
			if(getProvinceSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide province");
			}
			
			if(isOk){
				mun.setName(getMunicipalName());
				mun.setProvince(getProvinceSelected());
				mun.setRegional(getRegionalSelected());
				mun.save();
				clearData();
				loadMunicipal();
				Application.addMessage(1, "Success", "The province name " + mun.getName() + " has been successfully saved");
			}
			
		}else if("BARANGAY".equalsIgnoreCase(type)){
			Barangay bar = new Barangay();
			if(getBarangayData()!=null){
				bar = getBarangayData();
			}else{
				bar.setIsActive(1);
				
				if(checkName(getBarangayName(), type)){
					isOk = false;
					Application.addMessage(3, "Error", "Name is already available");
				}
			}
			
			if(getBarangayName()==null || getBarangayName().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide barangay name");
			}
			
			if(getRegionalSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide region");
			}
			
			if(getProvinceSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide province");
			}
			
			if(getMunicipalSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide municipal");
			}
			
			if(isOk){
				bar.setName(getBarangayName());
				bar.setRegional(getRegionalSelected());
				bar.setProvince(getProvinceSelected());
				bar.setMunicipality(getMunicipalSelected());
				bar.save();
				clearData();
				loadBarangay();
				Application.addMessage(1, "Success", "The barangay name " + bar.getName() + " has been successfully saved");
			}
		}else if("PUROK".equalsIgnoreCase(type)){
			Purok pur = new Purok();
			if(getPurokData()!=null){
				pur = getPurokData();
			}else{
				pur.setIsActive(1);
				
				if(checkName(getPurokName(), type)){
					isOk = false;
					Application.addMessage(3, "Error", "Name is already available");
				}
			}
			
			if(getPurokName()==null || getPurokName().isEmpty()){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide purok/sitio name");
			}
			
			
			if(getMunicipalSelected()==null){
				isOk = false;
				Application.addMessage(3, "Error", "Please provide municipal");
			}
			
			if(isOk){
				pur.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				pur.setPurokName(getPurokName());
				pur.setBarangay(getBarangaySelected());
				pur.setMunicipality(getMunicipalSelected());
				pur.save();
				clearData();
				loadPurok();
				Application.addMessage(1, "Success", "The Purok/Sitio name " + pur.getPurokName() + " has been successfully saved");
			}
		}
	}
	
	public void clickItem(Object obj){
		if(obj instanceof Regional){
			Regional reg = (Regional)obj;
			setRegionalName(reg.getName());
			setRegionalData(reg);
		}else if(obj instanceof Province){
			Province prov = (Province)obj;
			setProvinceName(prov.getName());
			setRegionalSelected(prov.getRegional());
			setProvinceData(prov);
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			setMunicipalName(mun.getName());
			setRegionalSelected(mun.getRegional());
			setProvinceSelected(mun.getProvince());
			setMunicipalData(mun);
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			setBarangayName(bar.getName());
			setRegionalSelected(bar.getRegional());
			setProvinceSelected(bar.getProvince());
			setMunicipalSelected(bar.getMunicipality());
			setBarangayData(bar);
		}else if(obj instanceof Purok){
			Purok pur = (Purok)obj;
			setPurokName(pur.getPurokName());
			setBarangaySelected(pur.getBarangay());
			setMunicipalSelected(pur.getMunicipality());
			setPurokData(pur);
		}	
	}
	
	public void clickItemPopup(Object obj){
		if(obj instanceof Regional){
			Regional reg = (Regional)obj;
			setRegionalSelected(reg);
		}else if(obj instanceof Province){
			Province prov = (Province)obj;
			setProvinceSelected(prov);
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			setMunicipalSelected(mun);
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			setBarangaySelected(bar);
		}	
		
	}
	
	public void deleteRow(Object obj){
		if(obj instanceof Regional){
			Regional reg = (Regional)obj;
			reg.delete();
			loadRegional();
			Application.addMessage(1, "Success", "The regional name " + reg.getName() + " has been successfully deleted");
		}else if(obj instanceof Province){
			Province prov = (Province)obj;
			prov.delete();
			loadProvince();
			Application.addMessage(1, "Success", "The province name " + prov.getName() + " has been successfully deleted");
		}else if(obj instanceof Municipality){
			Municipality mun = (Municipality)obj;
			mun.delete();
			loadMunicipal();
			Application.addMessage(1, "Success", "The municipal name " + mun.getName() + " has been successfully deleted");
		}else if(obj instanceof Barangay){
			Barangay bar = (Barangay)obj;
			bar.delete();
			loadBarangay();
			Application.addMessage(1, "Success", "The barangay name " + bar.getName() + " has been successfully deleted");
		}else if(obj instanceof Purok){
			Purok pur = (Purok)obj;
			pur.delete();
			loadPurok();
			Application.addMessage(1, "Success", "The purok name " + pur.getPurokName() + " has been successfully deleted");
		}	
	}
	
	public void clearData(){
		setRegionalName(null);
		setProvinceName(null);
		setMunicipalName(null);
		setBarangayName(null);
		setPurokName(null);
		
		setRegionalData(null);
		setProvinceData(null);
		setMunicipalData(null);
		setBarangayData(null);
		setPurokData(null);
		
		setRegionalSelected(null);
		setProvinceSelected(null);
		setMunicipalSelected(null);
		setBarangaySelected(null);
		
		setSearchProvince(null);
		setSearchMunicipal(null);
		setSearchBarangay(null);
		setSearchPurok(null);
		
		setSearchRegions(null);
	}
	
	private boolean checkName(String name, String type){
		
		String sql = "";
		String[] params = new String[0];
		try{
		if("REGIONAL".equalsIgnoreCase(type)){
			params = new String[1];
			sql = "SELECT * FROM regional WHERE regname=? AND isactivereg=1";
			params[0] = name;
			List<Regional> reg = Regional.retrieve(sql, params);
			if(reg.size()>0){
				return true;
			}
		}else if("PROVINCE".equalsIgnoreCase(type)){
			params = new String[2];
			sql = " AND prv.provname=? AND rg.regid=? AND prv.provisactive=1";
			params[0] = name;
			params[1] = getRegionalSelected().getId()+"";
			List<Province> prov = Province.retrieve(sql, params);
			if(prov.size()>0){
				return true;
			}
		}else if("MUNICIPALITY".equalsIgnoreCase(type)){
			params = new String[3];
			sql = " AND mun.munname=? AND prv.provid=? AND rg.regid=? AND mun.munisactive=1";
			params[0] = name;
			params[1] = getProvinceSelected().getId()+"";
			params[2] = getRegionalSelected().getId()+"";
			List<Municipality> mun = Municipality.retrieve(sql, params);
			if(mun.size()>0){
				return true;
			}
		}else if("BARANGAY".equalsIgnoreCase(type)){
			params = new String[4];
			sql = " AND bgy.bgname=? AND mun.munid=? AND prv.provid=? AND rg.regid=? AND bgy.bgisactive=1";
			params[0] = name;
			params[1] = getMunicipalSelected().getId()+"";
			params[2] = getProvinceSelected().getId()+"";
			params[3] = getRegionalSelected().getId()+"";
			List<Barangay> bar = Barangay.retrieve(sql, params);
			if(bar.size()>0){
				return true;
			}
		}else if("PUROK".equalsIgnoreCase(type)){
			params = new String[2];
			sql = " AND pur.purokname=? AND mun.munid=? AND pur.isactivepurok=1";
			params[0] = name;
			params[1] = getMunicipalSelected().getId()+"";
			List<Purok> pur = Purok.retrieve(sql, params);
			if(pur.size()>0){
				return true;
			}
		}
		}catch(Exception e){}
		return false;
	}
	
	public String getRegionalName() {
		return regionalName;
	}

	public void setRegionalName(String regionalName) {
		this.regionalName = regionalName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getMunicipalName() {
		return municipalName;
	}

	public void setMunicipalName(String municipalName) {
		this.municipalName = municipalName;
	}

	public String getBarangayName() {
		return barangayName;
	}

	public void setBarangayName(String barangayName) {
		this.barangayName = barangayName;
	}

	public List<Regional> getRegionals() {
		return regionals;
	}

	public void setRegionals(List<Regional> regionals) {
		this.regionals = regionals;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public List<Municipality> getMunicipals() {
		return municipals;
	}

	public void setMunicipals(List<Municipality> municipals) {
		this.municipals = municipals;
	}

	public List<Barangay> getBarangays() {
		return barangays;
	}

	public void setBarangays(List<Barangay> barangays) {
		this.barangays = barangays;
	}

	public Regional getRegionalData() {
		return regionalData;
	}

	public void setRegionalData(Regional regionalData) {
		this.regionalData = regionalData;
	}

	public Province getProvinceData() {
		return provinceData;
	}

	public void setProvinceData(Province provinceData) {
		this.provinceData = provinceData;
	}

	public Municipality getMunicipalData() {
		return municipalData;
	}

	public void setMunicipalData(Municipality municipalData) {
		this.municipalData = municipalData;
	}

	public Barangay getBarangayData() {
		return barangayData;
	}

	public void setBarangayData(Barangay barangayData) {
		this.barangayData = barangayData;
	}

	public Regional getRegionalSelected() {
		return regionalSelected;
	}

	public void setRegionalSelected(Regional regionalSelected) {
		this.regionalSelected = regionalSelected;
	}

	public Province getProvinceSelected() {
		return provinceSelected;
	}

	public void setProvinceSelected(Province provinceSelected) {
		this.provinceSelected = provinceSelected;
	}

	public Municipality getMunicipalSelected() {
		return municipalSelected;
	}

	public void setMunicipalSelected(Municipality municipalSelected) {
		this.municipalSelected = municipalSelected;
	}

	public String getSearchProvince() {
		return searchProvince;
	}

	public void setSearchProvince(String searchProvince) {
		this.searchProvince = searchProvince;
	}

	public String getSearchMunicipal() {
		return searchMunicipal;
	}

	public void setSearchMunicipal(String searchMunicipal) {
		this.searchMunicipal = searchMunicipal;
	}

	public String getSearchBarangay() {
		return searchBarangay;
	}

	public void setSearchBarangay(String searchBarangay) {
		this.searchBarangay = searchBarangay;
	}

	public String getSearchRegions() {
		return searchRegions;
	}

	public void setSearchRegions(String searchRegions) {
		this.searchRegions = searchRegions;
	}

	public List<Purok> getPuroks() {
		return puroks;
	}

	public void setPuroks(List<Purok> puroks) {
		this.puroks = puroks;
	}

	public Purok getPurokData() {
		return purokData;
	}

	public void setPurokData(Purok purokData) {
		this.purokData = purokData;
	}

	public Barangay getBarangaySelected() {
		return barangaySelected;
	}

	public void setBarangaySelected(Barangay barangaySelected) {
		this.barangaySelected = barangaySelected;
	}

	public String getSearchPurok() {
		return searchPurok;
	}

	public void setSearchPurok(String searchPurok) {
		this.searchPurok = searchPurok;
	}

	public String getPurokName() {
		return purokName;
	}

	public void setPurokName(String purokName) {
		this.purokName = purokName;
	}
	
}
