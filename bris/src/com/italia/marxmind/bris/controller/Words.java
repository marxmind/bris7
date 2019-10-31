package com.italia.marxmind.bris.controller;

import java.io.FileInputStream;
import java.util.Properties;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 12/26/2017
 * @version 1.0
 *
 */
public class Words {

	
	/*private static final String PATH_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +	
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName();*/
	private static String wordContentSource() {
		/*String propertyFile = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() +
				Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +	
				Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName() + "documentFormatter.bris";
		
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(propertyFile));
			System.out.println("documentContentSource>>>> " + prop.getProperty("documentContentSource"));
			return prop.getProperty("documentContentSource");
		}catch(Exception e){}*/
		return DocumentFormatter.getTagName("documentContentSource");
	}
	
	public static String getTagName(String tagName){
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(wordContentSource()));
			return prop.getProperty(tagName);
		}catch(Exception e){}
		return "";
	}
	
}
