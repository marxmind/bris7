package com.italia.marxmind.bris.controller;

import java.io.FileInputStream;
import java.util.Properties;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 07/13/2018
 * @version 1.0
 *
 */
public class DocumentFormatter {

	private static final String PROPERTY_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +	
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName() + "documentFormatter.bris";
	
	public static String getTagName(String tagName){
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(PROPERTY_FILE));
			return prop.getProperty(tagName);
		}catch(Exception e){}
		return "";
	}
	
}
