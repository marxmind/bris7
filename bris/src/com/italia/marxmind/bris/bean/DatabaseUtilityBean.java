package com.italia.marxmind.bris.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Forms;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 7/1/2018
 *
 */
@ManagedBean(name="dataBean", eager=true)
@ViewScoped
public class DatabaseUtilityBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 197087564351L;
	
	private  String DOC_PATH = Bris.PRIMARY_DRIVE.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + 
			Bris.SEPERATOR.getName() + "uploaded" + Bris.SEPERATOR.getName();
	private String LOG_NAME = "inquery.bris";
	private String BAT_FILE = "marxmind.bat";
	
	private String inputString="";
	private String outString="";
	private String locationName="";
	private String epass="";
	private String uname="";
	private String database="";
	private boolean forDatabase;
	
	private List<Forms> forms;
	private StreamedContent formFile;
	private String selectedFileInformation;
	
	@PostConstruct
	public void init() {
		Login in = Login.getUserLogin();
		//UserDtls user = in.getUserDtls();
		
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()) {
			setOutString("Please input your inquiry.");
		}else {
			setOutString("You are not allowed to access this application... We are now sending email to the developer for the attempting using this module...");
		}
		deleteConfigFile();
	}
	
	
	public void deleteConfigFile() {
		File file = new File(DOC_PATH + LOG_NAME);
		file.delete();
		File bat = new File(DOC_PATH + BAT_FILE);
		bat.delete();
	}
	
	public void loadInquiry() {
		
		String sep = File.separator;
		String str = "setlocal \n";
		str += "set LogPath=C:"+sep+"bris"+sep+"uploaded"+sep +"\n";
		str += "set LogFileExt=.bris \n";
		str += "set LogFileName=inquery%LogFileExt%\n";
		str += "set MyLogFile=%MyLogFile%\n";
		str += "set MyLogFile=%LogPath%%MyLogFile%%LogFileName%\n";
		str += "cd " + getLocationName() + "\n";
		if(isForDatabase()) {
			
			str += " mysql -u"+ getUname() +" -p"+ getEpass() +" -e ";
			str +="\"use " + getDatabase() + "; " + getInputString() + ";\"";
			str +=">>\"%MyLogFile%\"";
			
			createFile(str);
			isCreatedFile();
		}else {
			
			str += getInputString();
			str +=">>\"%MyLogFile%\"";
			createFile(str);
			isCreatedFile();
		}
	}
	
	private void createFile(String str) {
		try {
		
		File file = new File(DOC_PATH + BAT_FILE);
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		pw.println(str);
		pw.flush();
        pw.close();
        
        //run the bat file to create inquery.bris file
        Runtime.getRuntime().exec(DOC_PATH +  BAT_FILE);
        
		}catch(FileNotFoundException fne) {
		}catch(IOException ioe) {}
	}
	
	public void refreshFile() {
		isCreatedFile();
	}
	
	private boolean isCreatedFile() {
		File file = new File(DOC_PATH + LOG_NAME);
		//if(isCreatedFile()==false) {//loop until file created
			if(file.isFile()) {
				String str="";
				try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = null;
		        // Read from the original file and write to the new
		        while ((line = br.readLine()) != null) {
		            
		        	str += line.replace(" ", "&nbsp;") + "<br/>";
		        	str = str.replace("³", "&nbsp;");
		        	str = str.replace("Ã", "&nbsp;");
		        	str = str.replace("Ä", "&nbsp;");
		        	str = str.replace("À", "&nbsp;");
		        	str = str.replace("\\n", "<br/>");
		        	
		        	
		        }
		        br.close();
		        setOutString(str);
				}catch(FileNotFoundException fne) {
				}catch(IOException ioe) {}
				
				return true;
			}
		//}
		
		return false;
	}
	
	public void loadFiles() {
		try {
		File file = new File(getLocationName() + File.separator);
		String[] fileList = file.list();
		forms = Collections.synchronizedList(new ArrayList<>());
		int id=1;
		for(String fileName : fileList){
			Forms form = new Forms();
			form.setId(id++);
			form.setFileName(fileName);
			String ext = fileName.split("\\.")[1];
			form.setExt(ext);
			forms.add(form);
		}
		}catch(Exception e) {}
	}
	
	public void formDownload(Forms form){
		String fileName = form.getFileName();
		System.out.println("formDownload " + fileName);
		try{
			InputStream is = new FileInputStream(getLocationName() + File.separator + fileName);
			String ext = fileName.split("\\.")[1];
			formFile = new DefaultStreamedContent(is, "application/"+ext,fileName);
			//is.close(); Cause of error when downloading file
			
			setSelectedFileInformation(getLocationName() + File.separator + form.getFileName());
		}catch(FileNotFoundException e){
		}catch(Exception eio){}
		
	}
	
	public void openFile() {
		
		File file = new File(getSelectedFileInformation());
		
			if(file.isFile()) {
				String str="";
				try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = null;
		        // Read from the original file and write to the new
		        while ((line = br.readLine()) != null) {
		            
		        	str += "<p>"+line+ "<p/>";
		        	
		        }
		        
		        br.close();
		        setOutString(str);
		        System.out.println("File open : " + getOutString());
				}catch(FileNotFoundException fne) {
				}catch(IOException ioe) {}
			}
	}
	
	public void saveFile() {
		try {
		File file = new File(getSelectedFileInformation());
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		String str = getOutString();
		
		str = str.replace("<p>", "");
		str = str.replace("</p>", "\n");
		str = str.replace("<br/>", "\n");
		str = str.replace("&nbsp;", " ");
		str = str.replace("<br>", "");
		str = str.replace("</br>", "\n");
		pw.println(str);
		pw.flush();
        pw.close();
        setOutString(null);
        Application.addMessage(1, "Success", "Successfully saved");
		}catch(Exception e) {}
	}
	
	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}

	public String getOutString() {
		return outString;
	}

	public void setOutString(String outString) {
		this.outString = outString;
	}

	public String getEpass() {
		return epass;
	}


	public void setEpass(String epass) {
		this.epass = epass;
	}


	public String getUname() {
		return uname;
	}


	public void setUname(String uname) {
		this.uname = uname;
	}


	public String getDatabase() {
		return database;
	}


	public void setDatabase(String database) {
		this.database = database;
	}


	public boolean isForDatabase() {
		return forDatabase;
	}


	public void setForDatabase(boolean forDatabase) {
		this.forDatabase = forDatabase;
	}


	public String getLocationName() {
		return locationName;
	}


	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public List<Forms> getForms() {
		return forms;
	}


	public void setForms(List<Forms> forms) {
		this.forms = forms;
	}


	public StreamedContent getFormFile() {
		return formFile;
	}


	public void setFormFile(StreamedContent formFile) {
		this.formFile = formFile;
	}


	public String getSelectedFileInformation() {
		return selectedFileInformation;
	}


	public void setSelectedFileInformation(String selectedFileInformation) {
		this.selectedFileInformation = selectedFileInformation;
	}

}
