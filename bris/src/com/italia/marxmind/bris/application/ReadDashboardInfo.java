package com.italia.marxmind.bris.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;

/**
 * 
 * @author Mark Italia
 * @since 02/21/2019
 * @version 1.0
 *
 */

public class ReadDashboardInfo {
	
	public static final String CONFIG_FILE_NAME="dashboard-data.bris";
	
	public static void main(String[] args) {
		Map<String, Double> info = getInfo("citizen");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("zones");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("ages");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("doc-types");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("doc-months");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("id-zone");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("id-months");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("cases-pending");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("cases-months");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("cases-status");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		//budget
		System.out.println(getInfo("budget").get("Budget"));
		
		info = getInfo("mooe");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("check-months-this-year");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
		
		info = getInfo("check-months-last-year");
		for(String key : info.keySet()) {
			System.out.println(key + " " + info.get(key));
		}
	}
	
	public static Map<String, Double> getInfo(String key){
		Map<String, Double> info = Collections.synchronizedMap(new HashMap<String, Double>());
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String fileName = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		fileName += BARANGAY + "-" + MUNICIPALITY;
		fileName += Bris.SEPERATOR.getName();
		fileName += CONFIG_FILE_NAME;
		
		Properties prop = new Properties();
		if("citizen".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				info.put("Start", Double.valueOf(prop.getProperty("cit-start-record")));
				info.put("Total", Double.valueOf(prop.getProperty("cit-reg-total")));
				info.put("Male", Double.valueOf(prop.getProperty("cit-male")));
				info.put("Female", Double.valueOf(prop.getProperty("cit-female")));
				
				
			}catch(IOException e) {}
			
		}else if("start".equalsIgnoreCase(key)) {
				
				try {
					prop.load(new FileInputStream(fileName));
					
					info.put("Start", Double.valueOf(prop.getProperty("cit-start-record")));
					
					
				}catch(IOException e) {}
				
			}else if("zones".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("cit-zones").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
			}catch(IOException e) {}
		}else if("ages".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("cit-brackets").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("doc-types".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("doc-types").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("doc-months".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("doc-months").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("id-zone".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("id-month-zones").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("id-months".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("id-months").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("cases-pending".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("cases-pending").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("cases-months-last-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("cases-months-last-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("cases-months-this-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("cases-months-this-year").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}	
		}else if("cases-status".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("case-status").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("budget".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				info.put("Budget", Double.valueOf(prop.getProperty("check-budget")));
				
				
			}catch(IOException e) {}
		}else if("mooe".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("check-mooe").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("check-months-this-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("check-months-used").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("check-months-last-year".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("check-months-lastyear-used").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}else if("doc-common".equalsIgnoreCase(key)) {
			
			try {
				prop.load(new FileInputStream(fileName));
				
				String[] vals = prop.getProperty("doc-common").split(",");
				for(String a : vals) {
					info.put(a.split("=")[0], Double.valueOf(a.split("=")[1]));
				}
				
				
			}catch(IOException e) {}
		}
		
		Map<String, Double> sorted = new TreeMap<String, Double>(info);
		
		return sorted;
		
	}
	
}
