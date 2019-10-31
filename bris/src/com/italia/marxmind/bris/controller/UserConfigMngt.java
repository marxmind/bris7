package com.italia.marxmind.bris.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.UserConfig;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.sessions.SessionBean;
import com.italia.marxmind.bris.utils.DateUtils;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 09/04/2019
 *
 */
public class UserConfigMngt {
	
	private static final String USER_PATH = Bris.PRIMARY_DRIVE.getName() + File.separator + Bris.APP_FOLDER.getName() + File.separator + "users" + File.separator;
	
	public static void main(String[] args) {
		try {
			/*
			 * String path = "C:\\bris\\users\\Poblacion-LakeSebu\\"; String fileName =
			 * "mark-1.bris"; String FILEPATH = path + fileName; System.out.println(new
			 * String(readFromFile(FILEPATH, 8, 11))); writeToFile(FILEPATH, "Math", 8);
			 */
			
			updateUserConf(UserConfig.FileName, "marcos-1.bris");
			
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static String getValueField(UserConfig field) {
		try {
		HttpSession session = SessionBean.getSession();
		String barangay = ReadConfig.value(Bris.BARANGAY).replace(" ", "");
		String municipality = ReadConfig.value(Bris.MUNICIPALITY).replace(" ", "");
		String folderPath = USER_PATH +  barangay + "-" + municipality + File.separator;
		String fileName = session.getAttribute("confUser").toString();
		File file = new File(folderPath+fileName);
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		
		return prop.getProperty(field.getFieldName());
		
		}catch(IOException e) {}
		return "";
	}
	
	public static String logUserConfig(String theme, String version) {
		UserDtls user = Login.getUserLogin().getUserDtls();
		String fileUser = user.getFirstname()+user.getLastname() + "-" + user.getUserdtlsid() + ".bris";
		try {
			System.out.println("logging user configuration file");
			
		String barangay = ReadConfig.value(Bris.BARANGAY).replace(" ", "");
		String municipality = ReadConfig.value(Bris.MUNICIPALITY).replace(" ", "");
		String folderPath = USER_PATH +  barangay + "-" + municipality + File.separator;
		
		
		String FILE_LOG_TMP_NAME = "tmp"+fileUser;
		
		String finalFile = folderPath + fileUser;
        String tmpFileName = folderPath + FILE_LOG_TMP_NAME;
		
        File originalFile = new File(finalFile);
        
       
      //check log directory
        File logdirectory = new File(folderPath);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
        
        if(!originalFile.exists()){
        	originalFile.createNewFile();
        }
        
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
     // Construct the new file that will later be renamed to the original
        // filename.
        File tempFile = new File(tmpFileName);
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        String content = "Author: Mark Italia\n";
        content += "Date Created : " + DateUtils.convertDateToMonthDayYear(DateUtils.getCurrentDateYYYYMMDD()) + "\n";
    	content += "Please do not modify this file, it will interrupt user functionality.\n\n";
    	content +="fileName="+ fileUser +"\n";
    	content +="donotnotifyagaintoday=no\n";
    	content +="themeSelected="+theme+"\n";
    	content +="versionUsing="+(version.isEmpty()? "7" : version) +"\n";
    	content +="barangay="+ReadConfig.value(Bris.BARANGAY)+"\n";
       	pw.println(content);
        
        pw.flush();
        pw.close();
        br.close();
        
        //deleting original file
        if (!originalFile.delete()) {
            System.out.println("Could not delete file");
        }

        // Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(originalFile)) {
            System.out.println("Could not rename file");
        }
        
		}catch(Exception e) {e.printStackTrace();}
		
		return fileUser;
	}
	
	public static void updateUserConf(UserConfig fieldName, String value) {
		try {
			System.out.println("logging user configuration file");
			UserDtls user = Login.getUserLogin().getUserDtls();
		String barangay = ReadConfig.value(Bris.BARANGAY).replace(" ", "");
		String municipality = ReadConfig.value(Bris.MUNICIPALITY).replace(" ", "");
		String folderPath = USER_PATH +  barangay + "-" + municipality + File.separator;
		String fileUser = user.getFirstname()+user.getLastname() + "-" + user.getUserdtlsid() + ".bris";
		
		String FILE_LOG_TMP_NAME = "tmp"+fileUser;
		
		String finalFile = folderPath + fileUser;
        String tmpFileName = folderPath + FILE_LOG_TMP_NAME;
		
        File originalFile = new File(finalFile);
        
       
      //check log directory
        File logdirectory = new File(folderPath);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
        boolean isNewFile = false;
        String content = "Author: Mark Italia\n";
        if(!originalFile.exists()){
        	originalFile.createNewFile();
        	content += "Date Created : " + DateUtils.convertDateToMonthDayYear(DateUtils.getCurrentDateYYYYMMDD()) + "\n";
        	content += "Please do not modify this file, it will interrupt user functionality.\n\n";
        	content +="fileName="+ fileUser +"\n";
        	content +="donotnotifyagaintoday=no\n";
        	content +="themeSelected=nova-colored\n";
        	content +="versionUsing=avalon\n";
        	content +="barangay="+ReadConfig.value(Bris.BARANGAY)+"\n";
        	isNewFile=true;
        	System.out.println("Original file is not exist....");
        }
        
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
     // Construct the new file that will later be renamed to the original
        // filename.
        File tempFile = new File(tmpFileName);
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        
        String line = null;
        // Read from the original file and write to the new
        while ((line = br.readLine()) != null) {
        	if(line.contains(fieldName.getFieldName())) {
        		String[] lineVals = line.split("=");
        		pw.println(lineVals[0] + "=" + value);
        	
        	}else {
        		pw.println(line);
        	}
        	
        }
        
        if(isNewFile) {
        	pw.println(content);
        	System.out.println("adding new file content");
        }
        
        pw.flush();
        pw.close();
        br.close();
        
        //deleting original file
        if (!originalFile.delete()) {
            System.out.println("Could not delete file");
            return;
        }

        // Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(originalFile)) {
            System.out.println("Could not rename file");
        }
        
		}catch(Exception e) {e.printStackTrace();}
	}
	
	private static byte[] readFromFile(String filePath, int position, int size)
            throws IOException {
 
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        file.seek(position);
        byte[] bytes = new byte[size];
        file.read(bytes);
        file.close();
        return bytes;
 
    }
 
    private static void writeToFile(String filePath, String data, int position)
            throws IOException {
 
        RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        file.seek(position);
        file.write(data.getBytes());
        file.close();
 
    }
	
}
