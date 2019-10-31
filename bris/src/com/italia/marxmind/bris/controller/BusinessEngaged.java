package com.italia.marxmind.bris.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.italia.marxmind.bris.enm.Bris;

/**
 * 
 * @author mark italia
 * @since 12/02/2017
 * @version 1.0
 *
 */

public class BusinessEngaged {
	
	private int id;
	private String name;
	
	private static final String BUSINESS_FILE = Bris.PRIMARY_DRIVE.getName() + Bris.SEPERATOR.getName() + 
			Bris.APP_FOLDER.getName() + Bris.SEPERATOR.getName() +
			Bris.APP_CONFIG_FLDR.getName() + Bris.SEPERATOR.getName() +
			"BusinessEngaged.bris";
	
	public static List<BusinessEngaged> readBusinessEngagedXML(){
    	List<BusinessEngaged> lines = Collections.synchronizedList(new ArrayList<BusinessEngaged>());
    	try {
            	
            	Properties prop = new Properties();
            	prop.load(new FileInputStream(BUSINESS_FILE));
            	int size = Integer.valueOf(prop.getProperty("total"));
            	
            	for(int i=0; i<size; i++) {
            		BusinessEngaged b = new BusinessEngaged();
            		b.setId(i+1);
            		b.setName(prop.getProperty("label"+i));
            		lines.add(b);
            	}
            	
            
           } catch (Exception e) {
            e.printStackTrace();
           }
    	return lines;
    }
	
	public static BusinessEngaged businessName(int id){
    	BusinessEngaged line = new BusinessEngaged();
    	try {
    		Properties prop = new Properties();
        	prop.load(new FileInputStream(BUSINESS_FILE));
        		
        	line.setId(id);
        	line.setName(prop.getProperty("label"+id));
           } catch (Exception e) {
            e.printStackTrace();
           }
    	return line;
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
