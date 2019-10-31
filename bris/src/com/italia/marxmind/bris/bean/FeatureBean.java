package com.italia.marxmind.bris.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.application.Menu;
import com.italia.marxmind.bris.controller.AccessAllowed;
import com.italia.marxmind.bris.controller.Features;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.Themes;
import com.italia.marxmind.bris.controller.ThemesDecoder;
import com.italia.marxmind.bris.controller.UserAccessLevel;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.Feature;
import com.italia.marxmind.bris.enm.MenuStyle;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.reports.ReadXML;

/**
 * 
 * @author mark italia
 * @since 08/05/2017
 * @version 1.0
 *
 */
@ManagedBean(name="featuresBean")
@SessionScoped

public class FeatureBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 18780765747848478L;
	
	private boolean clearance;
	private boolean idGeneration;
	private boolean business;
	private boolean assistant;
	private boolean graph;
	private boolean organization;
	private boolean cheque;
	private boolean moemeter;
	private boolean blotters;
	private boolean brisFeatures;
	private boolean orListing;
	private boolean cedulaListing;
	private boolean pbcCreation;
	private boolean explorer;
	private boolean cedula;
	private boolean orlisting;
	private boolean reporting;
	
	private boolean profile;
	private boolean registration;
	private boolean userRegistration;
	private boolean employee;
	
	private String moduleName;
	
	private List<Features> modules = Collections.synchronizedList(new ArrayList<Features>());
	
	private List themesNames;
	private int themeId;
	private Map<Integer, Themes> themeData = Collections.synchronizedMap(new HashMap<Integer, Themes>());
	
	private List menus;
	private int menuId;
	private Map<Integer, MenuStyle> menuData = Collections.synchronizedMap(new HashMap<Integer, MenuStyle>());
	
	public Login getLogin() {
		return Login.getUserLogin();
	}
	
	@PostConstruct
	public void init(){
		
		Login in = getLogin();
		UserDtls user = in.getUserDtls();
		
		boolean isOnline = in.getIsOnline()==1? true : false;
		
		System.out.println("Check User online : " + isOnline);
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			clearance = false;
			idGeneration = false;
			business = false;
			assistant = false;
			graph = false;
			organization = false;
			cheque = false;
			moemeter = false;
			brisFeatures = false;
			blotters = false;
			profile = false;
			registration = false;
			userRegistration = false;
			employee = false;
			orListing = false;
			cedulaListing = false;
			pbcCreation = false;
			explorer = false;
			cedula = false;
			orListing = false;
			reporting = false;
			
		}else{
						
			if(isOnline) {
				userAccess(user);
			}else {
				lockFeatures();
			}
			
			brisFeatures = true;
		}
		
		modules = Collections.synchronizedList(new ArrayList<Features>());
		modules = Features.retrieve("SELECT * FROM features ", new String[0]);
		
		
	}
	
	private void lockFeatures() {
		clearance = true;
		idGeneration = true;
		business = true;
		assistant = true;
		graph = true;
		organization = true;
		cheque = true;
		moemeter = true;
		brisFeatures = true;
		blotters = true;
		profile = true;
		registration = true;
		userRegistration = true;
		employee = true;
		orListing = true;
		cedulaListing = true;
		pbcCreation = true;
		explorer = true;
		cedula = true;
		orListing = true;
		reporting = true;
	}
	
	private void userAccess(UserDtls user){
		
		clearance = true;
		idGeneration = true;
		business = true;
		assistant = true;
		graph = true;
		organization = true;
		cheque = true;
		moemeter = true;
		brisFeatures = true;
		blotters = true;
		profile = true;
		registration = true;
		userRegistration = true;
		employee = true;
		orListing = true;
		cedulaListing = true;
		pbcCreation = true;
		explorer = true;
		cedula = true;
		orListing = true;
		reporting = true;
		
		//Override access level
		for(AccessAllowed acc : AccessAllowed.retrieve(" AND ac.isactiveaccess=1 AND usr.userdtlsid="+user.getUserdtlsid(), new String[0])){
			if(Feature.CLEARANCE.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				clearance = false;
			}else if(Feature.ID_GENERATION.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				idGeneration = false;
			}else if(Feature.BUSINESS.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				business = false;
			}else if(Feature.ASSISTANT.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				assistant = false;
			}else if(Feature.GRAPH.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				graph = false;
			}else if(Feature.ORGANIZATION.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				organization = false;
			}else if(Feature.CHEQUE.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				cheque = false;
			}else if(Feature.MOE_METER.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				moemeter = false;
			}else if(Feature.BLOTTERS.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				blotters = false;
			}else if(Feature.PROFILE_DIRECTORY.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				profile = false;
			}else if(Feature.CITIZEN_REGISTRATION.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				registration = false;
			}else if(Feature.APPLICATION_USER.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				userRegistration = false;
			}else if(Feature.EMPLOYEE.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				employee = false;
			}else if(Feature.OR_LISTING.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				orListing = false;
			}else if(Feature.CEDULA_LISTING.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				cedulaListing = false;
			}else if(Feature.PBC_BANK_CREATION.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				pbcCreation = false;
			}else if(Feature.EXPLORER.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				explorer = false;
			}else if(Feature.CEDULA.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				cedula = false;
			}else if(Feature.OR_LISTING.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				orlisting = false;
			}else if(Feature.REPORTING.getName().equalsIgnoreCase(acc.getFeatures().getModuleName())){
				reporting = false;
			}
			
		}
		
		
		
	}
	
	public void changeActivation(Features fet){
		Login in = Login.getUserLogin();
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			saveData(fet, fet.isChecked());
			Application.addMessage(1, fet.getModuleName() + " has been " + (fet.isChecked()==true? "Activated." : "Deactivated."), "");
			//System.out.println("Features : " + fet.getModuleName() + " is " + (fet.isChecked()==true? "Activated" : "Deactivated"));
			
			for(AccessAllowed acc : AccessAllowed.retrieve(" AND fet.fid="+fet.getId(), new String[0])){
				if(!fet.isChecked()){
					acc.setIsActiveAccess(0);
					acc.save();
				}
				
			}
			
		}else{
			Application.addMessage(3, "Restricted to unauthorized person", "");
			modules = Collections.synchronizedList(new ArrayList<Features>());
			modules = Features.retrieve("SELECT * FROM features ", new String[0]);
		}
		
	}

	public static boolean isEnabled(Feature fets){
		//Features fet = new Features();
		
		String sql = "SELECT * FROM features WHERE modulename=? AND isActive=1";
		String[] params = new String[1];
		params[0] = fets.getName();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		System.out.println("Features SQL : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			return true;
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return false;
	}
	
	public static void save(Feature feat, boolean isEnabled){
		String sql ="";
		if(isEnabled){
			sql = "UPDATE features set isActive=1 WHERE modulename='" + feat.getName() +"'";
		}else{
			sql = "UPDATE features set isActive=0 WHERE modulename='" + feat.getName() +"'";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			conn.close();
			
		}catch(Exception e){}
		
	}
	
	public static void saveData(Features features, boolean isEnabled){
		String sql ="";
		if(isEnabled){
			sql = "UPDATE features set isActive=1 WHERE modulename='" + features.getModuleName() +"'";
		}else{
			sql = "UPDATE features set isActive=0 WHERE modulename='" + features.getModuleName() +"'";
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			conn.close();
			
		}catch(Exception e){}
		
	}
	
	public void applyTheme(){
		int size = 1;
		Bris[] tag = new Bris[size];
		String[] value = new String[size];
		int i=0;
		tag[i] = Bris.THEME_STYLE; value[i] = ThemesDecoder.themeEncode(getThemeId());// getThemeData().get(getThemeId()).getStyleName(); i++;
		
		Themes.updateThemes(tag, value);
		Application.addMessage(1, "The theme style " + ThemesDecoder.decodeEncryptedThemes(getThemeData().get(getThemeId()).getStyleName()) + " has been successfully applied. Please logout to take effect the themes look and feel.","");
	}
	
	public void applyMenu(){
		int size = 1;
		Bris[] tag = new Bris[size];
		String[] value = new String[size];
		int i=0;
		tag[i] = Bris.MENUSTYLE; value[i] = getMenuData().get(getMenuId()).getName();
		
		Menu.updateMenu(tag, value);
		Application.addMessage(1, "The menu style " + getMenuData().get(getMenuId()).getName() + " has been successfully applied. Please logout to take effect the menu style.","");
	}
	
	public boolean getClearance() {
		return clearance;
	}

	public void setClearance(boolean clearance) {
		this.clearance = clearance;
	}

	public boolean getIdGeneration() {
		return idGeneration;
	}

	public void setIdGeneration(boolean idGeneration) {
		this.idGeneration = idGeneration;
	}

	public boolean getBusiness() {
		return business;
	}

	public void setBusiness(boolean business) {
		this.business = business;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public boolean getAssistant() {
		return assistant;
	}

	public void setAssistant(boolean assistant) {
		this.assistant = assistant;
	}

	public boolean getGraph() {
		return graph;
	}

	public void setGraph(boolean graph) {
		this.graph = graph;
	}

	public boolean getOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public boolean getCheque() {
		return cheque;
	}

	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}

	public boolean getMoemeter() {
		return moemeter;
	}

	public void setMoemeter(boolean moemeter) {
		this.moemeter = moemeter;
	}

	public List<Features> getModules() {
		return modules;
	}

	public void setModules(List<Features> modules) {
		this.modules = modules;
	}

	public boolean getBrisFeatures() {
		return brisFeatures;
	}

	public void setBrisFeatures(boolean brisFeatures) {
		this.brisFeatures = brisFeatures;
	}

	public List getThemesNames() {
		
		themesNames = new ArrayList<>();
		themeData = Collections.synchronizedMap(new HashMap<Integer, Themes>()); 
		for(Themes theme : Themes.readThemesXML()){
			themesNames.add(new SelectItem(theme.getId(), ThemesDecoder.decodeEncryptedThemes(theme.getStyleName())));
			themeData.put(theme.getId(), theme);
		}
		
		return themesNames;
	}

	public void setThemesNames(List themesNames) {
		this.themesNames = themesNames;
	}

	public int getThemeId() {
		return themeId;
	}

	public void setThemeId(int themeId) {
		this.themeId = themeId;
	}

	public Map<Integer, Themes> getThemeData() {
		return themeData;
	}

	public void setThemeData(Map<Integer, Themes> themeData) {
		this.themeData = themeData;
	}

	public boolean getBlotters() {
		return blotters;
	}

	public void setBlotters(boolean blotters) {
		this.blotters = blotters;
	}

	public boolean getProfile() {
		return profile;
	}

	public void setProfile(boolean profile) {
		this.profile = profile;
	}

	public boolean getRegistration() {
		return registration;
	}

	public void setRegistration(boolean registration) {
		this.registration = registration;
	}

	public boolean getUserRegistration() {
		return userRegistration;
	}

	public void setUserRegistration(boolean userRegistration) {
		this.userRegistration = userRegistration;
	}

	public boolean getEmployee() {
		return employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

	public List getMenus() {
		menus = new ArrayList<>();
		menuData = Collections.synchronizedMap(new HashMap<Integer, MenuStyle>());
		for(MenuStyle m : MenuStyle.values()){
			menus.add(new SelectItem(m.getId(), m.getName()));
			menuData.put(m.getId(), m);
		}
		return menus;
	}

	public void setMenus(List menus) {
		this.menus = menus;
	}

	public int getMenuId() {
		return menuId;
	}

	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}

	public Map<Integer, MenuStyle> getMenuData() {
		return menuData;
	}

	public void setMenuData(Map<Integer, MenuStyle> menuData) {
		this.menuData = menuData;
	}

	public boolean getOrListing() {
		return orListing;
	}

	public void setOrListing(boolean orListing) {
		this.orListing = orListing;
	}

	public boolean getCedulaListing() {
		return cedulaListing;
	}

	public void setCedulaListing(boolean cedulaListing) {
		this.cedulaListing = cedulaListing;
	}

	public boolean getPbcCreation() {
		return pbcCreation;
	}

	public void setPbcCreation(boolean pbcCreation) {
		this.pbcCreation = pbcCreation;
	}

	public boolean getExplorer() {
		return explorer;
	}

	public void setExplorer(boolean explorer) {
		this.explorer = explorer;
	}

	public boolean getCedula() {
		return cedula;
	}

	public void setCedula(boolean cedula) {
		this.cedula = cedula;
	}

	public boolean getOrlisting() {
		return orlisting;
	}

	public void setOrlisting(boolean orlisting) {
		this.orlisting = orlisting;
	}

	public boolean getReporting() {
		return reporting;
	}

	public void setReporting(boolean reporting) {
		this.reporting = reporting;
	}
	
	
}
