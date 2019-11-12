package com.italia.marxmind.bris.bean;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.bris.application.Menu;
import com.italia.marxmind.bris.controller.Email;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.MenuStyle;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.sessions.SessionBean;

/**
 * 
 * @author mark italia
 * @since 07/07/2017
 * @version 1.0
 *
 */
//@ManagedBean(name="menuBean", eager=true)
//@ViewScoped
@Named
@javax.enterprise.context.SessionScoped
public class MenuBean implements Serializable{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 65456857965691L;
	private boolean stack;
	private boolean dock;
	private boolean defaultMenu;
	private boolean slide;
	private boolean tab;
	private boolean boot;
	private String[] labels;
	
	private int index;
	
	private boolean displayMsg;
	private String totalMsg;
	private String total;
	private String styleButton;
	private String ui;
	
	
	public UserDtls getUser() {
		return Login.getUserLogin().getUserDtls();
	}
	
	
	
	@PostConstruct
	public void init(){
		
		String style = ReadConfig.value(Bris.MENUSTYLE);
		
		if(MenuStyle.DEFAULT.getName().equalsIgnoreCase(style)){
			setDefaultMenu(true);
			setStack(false);
			setDock(false);
			setSlide(false);
			setTab(false);
			setBoot(false);
		}else if(MenuStyle.DOCK.getName().equalsIgnoreCase(style)){
			setStack(false);
			setDefaultMenu(false);
			setDock(true);
			setSlide(false);
			setTab(false);
			setBoot(false);
		}else if(MenuStyle.STACK.getName().equalsIgnoreCase(style)){
			setStack(true);
			setDock(false);
			setDefaultMenu(false);
			setSlide(false);
			setTab(false);
			setBoot(false);
		}else if(MenuStyle.SLIDE.getName().equalsIgnoreCase(style)){
			setStack(false);
			setDock(false);
			setDefaultMenu(false);
			setSlide(true);
			setTab(false);
			setBoot(false);
		}else if(MenuStyle.TAB.getName().equalsIgnoreCase(style)){
			setStack(false);
			setDock(false);
			setDefaultMenu(false);
			setSlide(false);
			setTab(true);
			setBoot(false);
		}else if(MenuStyle.BOOTSTRAP.getName().equalsIgnoreCase(style)){
			setStack(false);
			setDock(false);
			setDefaultMenu(false);
			setSlide(false);
			setTab(false);
			setBoot(true);
		}
		runReport();
		loadCountEmailNote();

		HttpSession session = SessionBean.getSession();
		String ui = session.getAttribute("ui").toString();
		System.out.println("UI value >> " + ui);
		setUi(ui);
		
	}
	
	private void runReport() {
		try {
		System.out.println("Run report....");
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		//explorer C:\bris\bris-runner\bris-report.jar
		File file = new File(pathFile +  "bris-run-report.bat");
		String bat = "java -jar "+ Bris.PRIMARY_DRIVE.getName() + File.separator + Bris.APP_FOLDER.getName() + File.separator +"bris-runner"+ File.separator +"bris-report.jar";
		 PrintWriter pw = new PrintWriter(new FileWriter(file));
		 	pw.println("@echo off");
		 	pw.println(bat);
	        pw.flush();
	        pw.close();
		
		
		Runtime.getRuntime().exec(pathFile +  "bris-run-report.bat");
		System.out.println("End run report....");
		
		/**
		 *  String path="cmd /c start d:\\sample\\sample.bat";
			Runtime rn=Runtime.getRuntime();
			Process pr=rn.exec(path);`
		 */
		/*String path = "cmd /c start "+ pathFile +  "bris-run-report.bat";
		Runtime rn=Runtime.getRuntime();
		Process pr=rn.exec(path);
		System.out.println("End run report....");*/
		
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public void loadCountEmailNote() {
		String sql = " AND msgtype=1 AND isopen=0 AND isdeleted=0 AND personcpy=?";
		String[] params = new String[1];
		params[0] = getUser().getUserdtlsid()+"";
		int count = Email.countNewEmail(sql, params);
		if(count>0) {
			displayMsg = true;
			setDisplayMsg(true);
			setTotalMsg(count+" messages.");
			setTotal(count+"");
			setStyleButton("color:red;");
		}else {
			displayMsg = false;
			setStyleButton("color:black;");
			setTotalMsg(count+" message.");
			setTotal(count+"");
		}
	}
	
	public String dashboard() {
		return "dashboard";
	}
	
	public String related(){
		return "related";
	}
	
	public String address(){
		return "address";
	}
	
	public String blotters(){
		setIndex(5);
		return "cases" + getUi();
	}
	
	public String bris(){
		setIndex(13);
		return "bris";
	}
	
	public String moemeter(){
		setIndex(10);
		return "moemeter";
	}
	
	public String cheque(){
		setIndex(4);
		return "cheque" + getUi();
	}
	
	public String organization(){
		setIndex(9);
		return "organization" + getUi();
	}
	
	public String graph(){
		setIndex(8);
		return "graph" + getUi();
	}
	
	public String assistant(){
		setIndex(7);
		return "assistant";
	}
	
	public String home(){
		setIndex(0);
		//FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		//return "/main" + getUi() +"xhtml?faces-redirect=true";
		return "main" + getUi();
	}
	
	public String cards(){
		setIndex(3);
		return "card" + getUi();
	}
	
	public String adminUser(){
		setIndex(12);
		return "adminuser";
	}
	
	public String adminTaxpayer(){
		setIndex(1);
		return "admincustomer" + getUi();
	}
	
	public String adminBusiness(){
		setIndex(6);
		return "adminBusiness" + getUi();
	}

	public String adminEmployees(){
		setIndex(11);
		return "adminEmployees" + getUi();
	}
	
	public String clearance(){
		setIndex(2);
		return "clearance" + getUi();
	}
	
	public String explorer(){
		setIndex(14);
		return "explorer";
	}
	
	public String cedula(){
		setIndex(15);
		return "cedula" + getUi();
	}
	
	public String orlisting(){
		setIndex(16);
		return "orlisting" + getUi();
	}
	
	public String reporting(){
		setIndex(17);
		return "reports";
	}
	
	public boolean isStack() {
		return stack;
	}

	public void setStack(boolean stack) {
		this.stack = stack;
	}

	public boolean isDock() {
		return dock;
	}

	public void setDock(boolean dock) {
		this.dock = dock;
	}

	public String[] getLabels() {
		
		labels = Menu.readMenuXML();
		
		return labels;
	}

	public boolean isDefaultMenu() {
		return defaultMenu;
	}

	public void setDefaultMenu(boolean defaultMenu) {
		this.defaultMenu = defaultMenu;
	}

	public boolean isSlide() {
		return slide;
	}

	public void setSlide(boolean slide) {
		this.slide = slide;
	}

	public boolean isTab() {
		return tab;
	}

	public void setTab(boolean tab) {
		this.tab = tab;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isBoot() {
		return boot;
	}

	public void setBoot(boolean boot) {
		this.boot = boot;
	}

	public boolean isDisplayMsg() {
		return displayMsg;
	}

	public void setDisplayMsg(boolean displayMsg) {
		this.displayMsg = displayMsg;
	}

	public String getTotalMsg() {
		return totalMsg;
	}

	public void setTotalMsg(String totalMsg) {
		this.totalMsg = totalMsg;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getStyleButton() {
		return styleButton;
	}

	public void setStyleButton(String styleButton) {
		this.styleButton = styleButton;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}



	public String getUi() {
		return ui;
	}



	public void setUi(String ui) {
		this.ui = ui;
	}
}
