package com.italia.marxmind.bris.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.marxmind.bris.application.Application;
import com.italia.marxmind.bris.controller.Forms;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.Reports;
import com.italia.marxmind.bris.security.SecureChar;
import com.italia.marxmind.bris.utils.DateUtils;

@ManagedBean(name="utilBean", eager=true)
@ViewScoped
public class BrisUtility implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 54678645535461L;
	
	private List<Reports> slqData;
	private StreamedContent sqlFile;
	
	private  String DOC_PATH = Bris.PRIMARY_DRIVE.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + 
			Bris.SEPERATOR.getName() + "uploaded" + Bris.SEPERATOR.getName();

	private String getUserName(){
		String u_name = ReadConfig.value(Bris.USER_NAME);
		   u_name = SecureChar.decode(u_name);
		   u_name = u_name.replaceAll("mark", "");
		   u_name = u_name.replaceAll("rivera", "");
		   u_name = u_name.replaceAll("italia", "");
		   return u_name;
	}
	
	private String getPassword(){
		String pword = ReadConfig.value(Bris.USER_PASS);
		   pword =  SecureChar.decode(pword);
		   pword = pword.replaceAll("mark", "");
		   pword = pword.replaceAll("rivera", "");
		   pword = pword.replaceAll("italia", "");	
		   return pword;
	}
	
	private String getDBName(){
		return ReadConfig.value(Bris.DB_NAME);
	}
	
	@PostConstruct
	public void init(){
		slqData = Collections.synchronizedList(new ArrayList<Reports>());
		File file = new File(DOC_PATH);
		String[] fileList = file.list();
		int id=1;
		try {
		for(String fileName : fileList){
			String ext = fileName.split("\\.")[1];
			
			if(!"bat".equalsIgnoreCase(ext)) {
				Reports rpt = new Reports();
				rpt.setF1(""+id);
				rpt.setF2(fileName);
				rpt.setF3(ext);
				slqData.add(rpt);
				id++;
			}
		}
		}catch(Exception e) {}
	}
	
	public void downloadData(){
		String sep = File.separator;
		String bat = "cd "+Bris.PRIMARY_DRIVE.getName()+sep+"Program Files"+sep+"MySQL"+sep+"MySQL Server 5.7"+sep+"bin" + "\n";
			   bat += "mysqldump.exe -e -u"+getUserName()+" -p"+getPassword()+" -hlocalhost "+getDBName()+" > "+DOC_PATH+sep+getDBName()+"_"+ DateUtils.getCurrentDateMMDDYYYYTIMEPlain() +".sql";
		try{	 
				File dir = new File(DOC_PATH);
				dir.mkdir();
				
				File file = new File(DOC_PATH +  "download.bat");
			   PrintWriter pw = new PrintWriter(new FileWriter(file));
			    pw.println(bat);
		        pw.flush();
		        pw.close();
		        try{Runtime.getRuntime().exec(DOC_PATH +  "download.bat");}catch(Exception e){Application.addMessage(3, "Error", "Error downloading data");}
		        Application.addMessage(1, "Success", "Data has been successfully downloaded");
		        init();
		}catch(Exception e){ Application.addMessage(3, "Error", "Error downloading data");}	        
	}
	
	private boolean loadToDatabase(File file){
		String sql = "";
		String sep = File.separator;
		try{
			File dir = new File(DOC_PATH);
			dir.mkdir();
			
			File fileUp = new File(DOC_PATH +  "uploadData.bat");
			String bat = "cd "+Bris.PRIMARY_DRIVE.getName()+sep+"Program Files"+sep+"MySQL"+sep+"MySQL Server 5.7"+sep+"bin" + "\n" +
	    		"mysql -u"+getUserName()+" -p"+getPassword()+" -e \"use "+getDBName()+"; source "+DOC_PATH.replace("\\", "/")+file.getName()+";"+"\"";
	    PrintWriter pw = new PrintWriter(new FileWriter(fileUp));
	    pw.println(bat);
        pw.flush();
        pw.close();
	    
        
        try{Runtime.getRuntime().exec(DOC_PATH +  "uploadData.bat");}catch(Exception e){return false;}
        
		return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void uploadData(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(stream)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	private boolean writeDocToFile(InputStream stream){
		try{
		String filename = "uploaded-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + ".sql";	
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(DOC_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		return loadToDatabase(fileDoc);
		}catch(IOException e){return false;}
	}

	public List<Reports> getSlqData() {
		return slqData;
	}

	public void setSlqData(List<Reports> slqData) {
		this.slqData = slqData;
	}
	
	public void fileDownload(String fileName){
		System.out.println("formDownload " + fileName);
		try{
			InputStream is = new FileInputStream(DOC_PATH + fileName);
			String ext = fileName.split("\\.")[1];
			sqlFile = new DefaultStreamedContent(is, "application/"+ext,fileName);
			//is.close();
		}catch(FileNotFoundException e){
			
		}catch(IOException eio){}
		
	}

	public StreamedContent getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(StreamedContent sqlFile) {
		this.sqlFile = sqlFile;
	}
	
	public void zipImageFile() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String IMAGE_PATH = Bris.PRIMARY_DRIVE.getName() + 
				Bris.SEPERATOR.getName() + 
				Bris.APP_FOLDER.getName() + 
				Bris.SEPERATOR.getName() + "img" + Bris.SEPERATOR.getName() +BARANGAY + "-" + MUNICIPALITY + Bris.SEPERATOR.getName();
		
		String zipFile = DOC_PATH + BARANGAY + "-" + MUNICIPALITY +"-"+ DateUtils.getCurrentDateMMDDYYYYTIMEPlain() +".zip";
		try {
		
			// create byte buffer
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
		File file = new File(IMAGE_PATH);
		String[] fileList = file.list();
		for(String fileName : fileList){
			File srcFile = new File(IMAGE_PATH + fileName);
			FileInputStream fis = new FileInputStream(srcFile);
			
			// begin writing a new ZIP entry, positions the stream to the start of the entry data
			zos.putNextEntry(new ZipEntry(srcFile.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			// close the InputStream
			fis.close();
		}
		// close the ZipOutputStream
		zos.close();
		init();
		 Application.addMessage(1, "Success", "Images has been successfully zip");
		}catch (IOException ioe) {
			Application.addMessage(3, "Error", "Error occured while zipping the images");
		}
	}
	
	public void deleteFile(Reports report) {
		File file = new File(DOC_PATH + report.getF2());
		if(file.isFile()) {
			file.delete();
			Application.addMessage(1, "Success", "Successfulle deleted file " + report.getF2() + "." + report.getF3());
			init();
		}
	}
}
