package com.italia.marxmind.bris.reports;

import java.io.FileInputStream;
import java.util.Properties;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 02/22/2017
 * @version 1.0
 *
 */
/**
 * 
 * Reading report xml configuration file
 *
 */
public class ReadXML {

	private static final String APPLICATION_FILE = Bris.PRIMARY_DRIVE.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + 
			Bris.SEPERATOR.getName() + 
			Bris.APP_CONFIG_FLDR.getName() + 
			Bris.SEPERATOR.getName() +
			Bris.REPORT_CONFIG_FILENAME.getName();
			   


	public static String value(ReportTag tag){
		try {
		Properties prop = new Properties();
    	prop.load(new FileInputStream(APPLICATION_FILE));
		return prop.getProperty(tag.getName());
		}catch(Exception e) {}
		return null;
	}
	
}
