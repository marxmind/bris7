package com.italia.marxmind.bris.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.primefaces.event.FileUploadEvent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.CommelecNames;
import com.italia.marxmind.bris.controller.Login;
import com.italia.marxmind.bris.controller.UserAccessLevel;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.UserAccess;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 01/19/2019
 *
 */
@ManagedBean(name="comBean", eager=true)
@ViewScoped
public class CommelecBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 777555654533321L;
	
	private  String DOC_PATH = Bris.PRIMARY_DRIVE.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + 
			Bris.SEPERATOR.getName() + "uploaded" + Bris.SEPERATOR.getName();
	
	private String searchName;
	private List<CommelecNames> names = Collections.synchronizedList(new ArrayList<CommelecNames>());
	private String extractUpdateNote;
	private boolean enabledUpload=true;
	
	public static void main(String[] args) {
		File file = new File("C:/bris/commelec.xls");
		CommelecBean.readXLSFile(file, 0);
	}
	
	public void loadNames() {
		names = Collections.synchronizedList(new ArrayList<CommelecNames>());
		String sql = " LIMIT 10";
		String[] params = new String[0];
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			
			//int size = getSearchName().length();
			//if(size>5) {
				sql = " fullname like '%"+ getSearchName().replace("--", "") +"%'";
				names = CommelecNames.retrieve(sql, params);
				sql += " LIMIT 100";
			//}
			
		}
		
	}
	
	public void uploadData(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(event)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
				 //initAttendanceFile();
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	private boolean writeDocToFile(FileUploadEvent event){
		try{
		InputStream stream = event.getFile().getInputstream();
		String fileExt = event.getFile().getFileName().split("\\.")[1];
		String filename = "commelecData-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "."+fileExt.toLowerCase();
		
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		//return loadToDatabase(fileDoc);
		readingExcelFile(filename);
		return true;
		}catch(IOException e){return false;}
		
	}
	
	private void readingExcelFile(String fileName) {
		File file = new File(DOC_PATH + fileName);
		loadFile(file,0);
	}
	
	private void loadFile(File file,int sheetNo) {
		System.out.println(file.getName());
		String ext = file.getName().split("\\.")[1];
		List<CommelecNames> names = Collections.synchronizedList(new ArrayList<CommelecNames>());
		if(DocExtension.XLS.getName().equalsIgnoreCase(ext)) {
			 names = readXLSFile(file,sheetNo);
			
		}/*else if(DocExtension.XLSX.getName().equalsIgnoreCase(ext)) {
			readXLSXFile(file,sheetNo);
		}else if(DocExtension.TXT.getName().equalsIgnoreCase(ext)) {
			readTextFile(file);
		}*/
		
		//clear data in database;
		CommelecNames.delete("DELETE FROM commelecnames", new String[0]);
		//load new data in database
		System.out.println("Checking for saving...");
		for(CommelecNames name : names) {
			name.setFullName(name.getLastName() + ", " + name.getFirstName() + " " + name.getMiddleName());
			name.save();
		}
	}
	
	
	
	private static List<CommelecNames> readXLSFile(File file,int sheetNo) {
		
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
				
				List<CommelecNames> names = Collections.synchronizedList(new ArrayList<CommelecNames>());
				Iterator rows = sheet.rowIterator();
				int startRow=1;
			    while (rows.hasNext()){
		            row=(HSSFRow) rows.next();
		            Iterator cells = row.cellIterator();
		            int countRow = 1;
		            CommelecNames att = new CommelecNames();
		            
		            if(startRow>1) {
		            	att.setId(startRow-1);
			            while (cells.hasNext()){
			            	
				                cell=(HSSFCell) cells.next();
				                String value="";
				                if(cell.getCellTypeEnum()==CellType.STRING) {
				                	value = cell.getStringCellValue();
				                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
				                	value = cell.getNumericCellValue()+"";
				                }else {
				                	//U Can Handel Boolean, Formula, Errors
				                	//System.out.println("\t");
				                }
				                
				                switch(countRow) {
				                case 1: att.setLastName(value);
				                case 2: att.setFirstName(value);
				                case 3: att.setMiddleName(value);
				                case 4: att.setAddress(value);
				                }
				                
				               countRow++;
			            }
			            names.add(att);
		            }
		            startRow++;
			    }   
	    
			   
			    fin.close();
				/*System.out.println("Checking before saving...");
			    for(CommelecNames name : names) {
			    	System.out.println(name.getLastName() + ", " + name.getFirstName() + " " + name.getMiddleName() + " " + name.getAddress());
			    }*/
			    return names;
			    } catch(Exception e) {}	
		return null;
			
	}
	
	public String getSearchName() {
		return searchName;
	}
	public List<CommelecNames> getNames() {
		return names;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public void setNames(List<CommelecNames> names) {
		this.names = names;
	}

	public String getExtractUpdateNote() {
		if(extractUpdateNote==null) {
			extractUpdateNote = CommelecNames.extractUpdateNote();
		}
		return extractUpdateNote;
	}

	public void setExtractUpdateNote(String extractUpdateNote) {
		this.extractUpdateNote = extractUpdateNote;
	}

	public boolean isEnabledUpload() {
		if(enabledUpload) {
			Login in = Login.getUserLogin();
			UserAccessLevel level = in.getAccessLevel();
			if(UserAccess.DEVELOPER.getId()==level.getLevel()) {
				enabledUpload = false;
			}
		}
		return enabledUpload;
	}

	public void setEnabledUpload(boolean enabledUpload) {
		this.enabledUpload = enabledUpload;
	}

}
enum DocExtension{
	
	XLS("xls"),
	XLSX("xlsx"),
	TXT("txt");
	
	private String name;
	
	DocExtension(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}