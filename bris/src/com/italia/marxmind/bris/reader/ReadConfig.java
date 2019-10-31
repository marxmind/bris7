package com.italia.marxmind.bris.reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.sessions.SessionBean;

/**
 * 
 * @author mark italia
 * @since 9/27/2016
 * Description: This class is use for reading the configuration file of the application
 *
 */
public class ReadConfig {

	private static final String APPLICATION_FILE = Bris.PRIMARY_DRIVE.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_CONFIG_FLDR.getName() + 
			Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FILE.getName();
	
	
	public static String value(Bris icoopConf){
		try{
			String id = "0";
			try {
				id = SessionBean.getSession().getAttribute("barangayid").toString();
			}catch(Exception e) {}
			
			File xmlFile = new File(APPLICATION_FILE);
			if(xmlFile.exists()){
				Properties prop = new Properties();
				prop.load(new FileInputStream(APPLICATION_FILE));
				String val = prop.getProperty(icoopConf.getName() + id);
				val = val.replace("~", File.separator);
				return val;
			}else{
				System.out.println("File is not exist...");
				return "";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		System.out.println(ReadConfig.value(Bris.DB_URL));
		
	}
}




















	
