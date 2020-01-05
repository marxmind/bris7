package com.italia.marxmind.bris.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.italia.marxmind.bris.bean.ReportGeneratorBean;
import com.italia.marxmind.bris.controller.CaseFilling;
import com.italia.marxmind.bris.controller.Cases;
import com.italia.marxmind.bris.controller.Chequedtls;
import com.italia.marxmind.bris.controller.Clearance;
import com.italia.marxmind.bris.controller.Customer;
import com.italia.marxmind.bris.controller.MOOE;
import com.italia.marxmind.bris.controller.UserDtls;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.enm.DateFormat;
import com.italia.marxmind.bris.enm.EmailType;
import com.italia.marxmind.bris.enm.Purpose;
import com.italia.marxmind.bris.reader.ReadConfig;
import com.italia.marxmind.bris.reports.Reports;
import com.italia.marxmind.bris.utils.DateUtils;


public class DailyReport {
	
	public static void runReport() {
		
		String log = "";
		if(!isUserFileExist()) {
			log += "File User is not existing....\n";
			log +="Start saving new report user....\n";
			saveUsers();
			log +="End saving new report user....\n";
		}
		
		if(isDateFileExist()) {
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
				
				//run the barangay information method
				//if date is not yet created for above information;
				log +="Start collectiong info for graph....\n";
				collecInfo();
				log +="End collectiong info for graph....\n";
			}
		}else {
			log +="File date is not existing....\n";
			log +="Start saving new report date generation....\n";
			saveDate();
			log +="End saving new report date generation....\n";
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");;
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
				
				//run the barangay information method
				//if date is not yet created for above information;
				//run the barangay information method
				//if date is not yet created for above information;
				log +="Start collectiong info for graph....\n";
				collecInfo();
				log +="End collectiong info for graph....\n";
			}
			
			
			
		}
		
		if(!isUserSummonFileExist()) {
			log += "File User is not existing....\n";
			log +="Start saving new summon user....\n";
			saveUsersSummon();
			log +="End saving new summon user....\n";
		}
		
		if(isSummonFileExist()) {
			if(readSummonDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");
			}else {
				
				log +="Start fetching data for summon generation....\n";
				collectSummonReport();
				log +="Done fetching data for summon generation....\n";
				
				log +="Start saving new date report summon....\n";
				saveSummonDate();
				log +="End saving new date report summon....\n";
			}
		}else {
			
			log +="Start saving fresh date report summon....\n";
			saveSummonDate();
			log +="End saving fresh date report summon....\n";
			
			log +="Start fetching data for summon generation....\n";
			collectSummonReport();
			log +="Done fetching data for summon generation....\n";
			
			log +="Start saving new date report summon....\n";
			saveSummonDate();
			log +="End saving new date report summon....\n";
		}
		
	}
	
	/*public static void main(String[] args) {
		
		String log = "";
		if(!isUserFileExist()) {
			log += "File User is not existing....\n";
			log +="Start saving new report user....\n";
			saveUsers();
			log +="End saving new report user....\n";
		}
		
		if(isDateFileExist()) {
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
				
				//run the barangay information method
				//if date is not yet created for above information;
				log +="Start collectiong info for graph....\n";
				collecInfo();
				log +="End collectiong info for graph....\n";
			}
		}else {
			log +="File date is not existing....\n";
			log +="Start saving new report date generation....\n";
			saveDate();
			log +="End saving new report date generation....\n";
			
			if(readDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");;
			}else {
			
				log +="Start fetching data for report generation....\n";
				collectReport();
				log +="Done fetching data for report generation....\n";
				log +="Start saving new report date generation....\n";
				saveDate();
				log +="End saving new report date generation....\n";
				saveLog(log);
				
				//run the barangay information method
				//if date is not yet created for above information;
				//run the barangay information method
				//if date is not yet created for above information;
				log +="Start collectiong info for graph....\n";
				collecInfo();
				log +="End collectiong info for graph....\n";
			}
			
			
			
		}
		
		if(!isUserSummonFileExist()) {
			log += "File User is not existing....\n";
			log +="Start saving new summon user....\n";
			saveUsersSummon();
			log +="End saving new summon user....\n";
		}
		
		if(isSummonFileExist()) {
			if(readSummonDate().equalsIgnoreCase(DateUtils.getCurrentDateYYYYMMDD())) {
				System.out.println("No report to be generate....");
			}else {
				
				log +="Start fetching data for summon generation....\n";
				collectSummonReport();
				log +="Done fetching data for summon generation....\n";
				
				log +="Start saving new date report summon....\n";
				saveSummonDate();
				log +="End saving new date report summon....\n";
			}
		}else {
			
			log +="Start saving fresh date report summon....\n";
			saveSummonDate();
			log +="End saving fresh date report summon....\n";
			
			log +="Start fetching data for summon generation....\n";
			collectSummonReport();
			log +="Done fetching data for summon generation....\n";
			
			log +="Start saving new date report summon....\n";
			saveSummonDate();
			log +="End saving new date report summon....\n";
		}
		
	}*/
	
	public static void saveLog(String log) {
		
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "log" + Bris.SEPERATOR.getName();
		
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Log File\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += log;
		File email = new File(emailPath + "bris-runner-log-"+DateUtils.getCurrentDateMMDDYYYYTIMEPlain()+".log");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static boolean isDateFileExist() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		pathFile += BARANGAY + "-" + MUNICIPALITY;
		pathFile += Bris.SEPERATOR.getName();
		pathFile += "reportDate.bris";
		
		File file = new File(pathFile);
		if(file.exists()) {
			System.out.println("File exist... " + pathFile);
			return true;
		}
		
		return false;
	}
	
	public static String readDate() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		emailPath += "reportDate.bris";
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Date");
		}catch(Exception e) {}
		
		return DateUtils.getCurrentDateYYYYMMDD();
	}
	
	public static void saveDate() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		msg += "Date="+DateUtils.getCurrentDateYYYYMMDD();
		File email = new File(emailPath + "reportDate.bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isUserFileExist() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		pathFile += BARANGAY + "-" + MUNICIPALITY;
		pathFile += Bris.SEPERATOR.getName();
		pathFile += "reportUsers.bris";
		
		File file = new File(pathFile);
		if(file.exists()) {
			System.out.println("File exist... " + pathFile);
			return true;
		}
		
		return false;
	}
	
	public static String readUsers() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		emailPath += "reportUsers.bris";
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Users");
		}catch(Exception e) {}
		
		return "5";
	}
	
	public static void saveUsers() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		String val = "";
		for(int i=1; i<=5; i++) {
			if(i>1) {
				val += ","+i;
			}else {
				val += i;
			}
		}
		msg += "Users=" + val;
		File email = new File(emailPath + "reportUsers.bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void collectReport() {
		
		String htmlHead = "";
		
		htmlHead = "<html><head><title>BRIS Reports</title></head><body><form>";
		
		htmlHead = "<p><center><h2><strong>Generated Report for "+ readDate() +"</strong></h2></center></p>";
		htmlHead += "<br/>";
		//msg += "<p><h3><strong>Please see below details for the daily report</strong></h3></p>";
		htmlHead += "<br/>";
		
		//clearance
		ReportGeneratorBean rpt = new ReportGeneratorBean();
		
		rpt.setDateFrom(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setDateTo(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setIncludeClearance(true);
		rpt.setDetailedClerance(true);
		rpt.loadReport();
		
		String htmlDoc = "<p><h2>Documents</h2></p>";
		
		htmlDoc += "<table>";
		System.out.println("Documents: " + rpt.getRpts().size());
		boolean docOk = rpt.getRpts().size()>=5? true : false;
		for(Reports r : rpt.getRpts()) {
			htmlDoc += "<tr>";
			htmlDoc += "<td>"+r.getF1()+"</td><td>" + r.getF2() + "</td><td>" + r.getF3() + "</td><td>" + r.getF4()+"</td>";
			htmlDoc += "</tr>";
			
		}
		htmlDoc += "</table>";
		
		htmlDoc += "<br/><br/><hr/>"; 
		htmlDoc += "<br/><br/>";
		
		//IDs
		rpt = new ReportGeneratorBean();
			
		rpt.setDateFrom(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setDateTo(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setIncludeIds(true);
		rpt.setDetailedIds(true);
		rpt.setIncludeIdHolderName(true);
		rpt.loadReport();
		
		String htmlId  = "<p><h2>Identification No</h2></p>"; 
		htmlId += "<table>";
		System.out.println("Ids: " + rpt.getRpts().size());
		boolean idOk = rpt.getRpts().size()>=5? true : false;
		for(Reports r : rpt.getRpts()) {
			htmlId += "<tr>";
			htmlId += "<td>"+r.getF1()+"</td><td>" + r.getF2() + "</td><td>" + r.getF3() + "</td><td>" + r.getF4()+"</td>";
			htmlId += "</tr>";
		}
		htmlId += "</table>";
		htmlId += "<br/><br/><hr/>"; 
		htmlId += "<br/><br/>"; 
		
		
		//cases
		rpt = new ReportGeneratorBean();
		
		rpt.setDateFrom(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setDateTo(DateUtils.convertDateString(readDate(), DateFormat.YYYY_MM_DD()));
		rpt.setDetailedCases(true);
		rpt.setIncludeCases(true);
		rpt.loadReport();
		String htmlCase = "<p><h2>Log Cases</h2></p>"; 
		htmlCase += "<table>";
		System.out.println("Cases: " + rpt.getRpts().size());
		boolean caseOk = rpt.getRpts().size()>=5? true : false;
		for(Reports r : rpt.getRpts()) {
			htmlCase += "<tr>";
			htmlCase += "<td>"+r.getF1()+"</td><td>" + r.getF2() + "</td><td>" + r.getF3() + "</td><td>" + r.getF4()+"</td>";
			htmlCase += "</tr>";
		}
		htmlCase += "</table>";
		htmlCase += "<br/><br/>";
		
		String htmlFooter ="</form></body></html>";
		int counting = 0;
		String html = htmlHead;
				if(docOk) {
					html += htmlDoc;
					counting=1;
				}
				if(idOk) {
					html += htmlId;
					counting=1;
				}
				if(caseOk) {
					html += htmlCase;
					counting=1;
				}
				html += htmlFooter;
		
		if(counting==1) {		
				
		int cnt=1;
		String toMailUser = "";
		boolean isCheckNote=false;
		for(String id : readUsers().split(",")) {
			
					String sql = "SELECT * FROM userdtls WHERE isactive=1 AND jobtitleid=?";
					String[] params = new String[1];
					params[0] = id;
					List<UserDtls> toUsers = UserDtls.retrieve(sql, params);
					
					if(toUsers.size()>0) {
						isCheckNote=true;
						if(toUsers.size()>1) {
							String toM = "";
							int cntM=1;
							for(UserDtls user : toUsers) {
								if(cntM>1) {
									toM += ":"+user.getUserdtlsid()+"";
								}else {
									toM = user.getUserdtlsid()+"";
								}
								cntM++;	
							}
							if(cnt>1) {
								toMailUser += ":"+toM;
							}else {
								toMailUser = toM;
							}
							
						}else {
							if(cnt>1) {
								toMailUser += ":"+toUsers.get(0).getUserdtlsid()+"";
							}else {
								toMailUser = toUsers.get(0).getUserdtlsid()+"";
							}
						}
					}	
				
			
			cnt++;
		}
		
		//System.out.println("send to " + toMailUser);
		
		isCheckNote=true;
		if(isCheckNote && !toMailUser.isEmpty()) {
			
			boolean isMore = false;
			try {
				String[] em = toMailUser.split(":");
				isMore=true;
		    }catch(Exception ex) {}
			if(isMore) {
				for(String sendTo : toMailUser.split(":")) {
					Email e = new Email();
					e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
					e.setTitle("Report for ["+ readDate() +"] Transactions");
					
					e.setType(EmailType.INBOX.getId());
					e.setIsOpen(0);
					e.setIsDeleted(0);
					e.setIsActive(1);
					
					e.setToEmail(toMailUser);
					e.setPersonCopy(Long.valueOf(sendTo));
					e.setFromEmail("0");
					
					
					String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + sendTo;
					Email.saveAttachment(fileName, html, "html");
					
					String msg = "<p><strong>Hi</strong></p>";
					msg += "<br/>";
					msg += "<p>Please see attached file for the report transactions</p>";
					msg += "<br/>";
					msg += "<p><a href=\"/bris/marxmind/attachment/"+ fileName +".html\" target=\"_blank\">Click here to see the attachment</p>";
					Email.emailSavePath(msg, fileName);
					//Email.transferAttachment(fileName + ".html");
					e.setContendId(fileName);
					e.save();
					
				}
			}else {
				Email e = new Email();
				e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
				e.setTitle("Report for ["+ readDate() +"] Transactions");
				
				e.setType(EmailType.INBOX.getId());
				e.setIsOpen(0);
				e.setIsDeleted(0);
				e.setIsActive(1);
				
				e.setToEmail(toMailUser);
				e.setPersonCopy(Long.valueOf(toMailUser));
				e.setFromEmail("0");
				
				
				String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + toMailUser;
				Email.saveAttachment(fileName, html, "html");
				
				String msg = "<p><strong>Hi</strong></p>";
				msg += "<br/>";
				msg += "<p>Please see attached file for the report transactions</p>";
				msg += "<br/>";
				msg += "<p><a href=\"/bris/marxmind/attachment/"+ fileName +".html\" target=\"_blank\">Click here to see the attachment</p>";
				Email.emailSavePath(msg, fileName);
				//Email.transferAttachment(fileName + ".html");
				e.setContendId(fileName);
				e.save();
				
			}
		}
		
		}
		
	}
	
	public static boolean isSummonFileExist() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		pathFile += BARANGAY + "-" + MUNICIPALITY;
		pathFile += Bris.SEPERATOR.getName();
		pathFile += "summonDate.bris";
		
		File file = new File(pathFile);
		if(file.exists()) {
			System.out.println("File exist... " + pathFile);
			return true;
		}
		
		return false;
	}
	
	public static String readSummonDate() {
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		emailPath += "summonDate.bris";
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Date");
		}catch(Exception e) {}
		
		return DateUtils.getCurrentDateYYYYMMDD();
		
	}
	public static void saveSummonDate() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		msg += "Date="+DateUtils.getCurrentDateYYYYMMDD();
		File email = new File(emailPath + "summonDate.bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isUserSummonFileExist() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String pathFile = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		pathFile += BARANGAY + "-" + MUNICIPALITY;
		pathFile += Bris.SEPERATOR.getName();
		pathFile += "summonUsers.bris";
		
		File file = new File(pathFile);
		if(file.exists()) {
			System.out.println("File exist... " + pathFile);
			return true;
		}
		
		return false;
	}
	
	public static String readUsersSummon() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		emailPath += "summonUsers.bris";
		
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(emailPath));
			return prop.getProperty("Users");
		}catch(Exception e) {}
		
		return "5";
	}
	
	public static void saveUsersSummon() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		String val = "";
		for(int i=1; i<=5; i++) {
			if(i>1) {
				val += ","+i;
			}else {
				val += i;
			}
		}
		msg += "Users=" + val;
		File email = new File(emailPath + "summonUsers.bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void collectSummonReport() {
		
		List<Cases> caseLookup = Collections.synchronizedList(new ArrayList<Cases>());
		
		String sql = " AND ciz.caseisactive=1 AND ciz.casestatus!=? AND ciz.casestatus!=?";
		String[] params = new String[2];
		params[0] = CaseStatus.MOVED_IN_HIGHER_COURT.getId()+"";
		params[1] = CaseStatus.SETTLED.getId()+"";
		
		List<Cases> cases = Cases.retrieve(sql, params);
		
		boolean sendEmail = false;
		if(cases!=null && cases.size()>0) {
			
			for(Cases cz : cases){
				sql = " AND ciz.caseid=? AND fil.filisactive=1 AND fil.settlementdate=? ORDER BY fil.filid DESC LIMIT 1";
				params = new String[2];
				params[0] = cz.getId()+"";
				params[1] = DateUtils.getCurrentDateYYYYMMDD();
				try{
					
					List<CaseFilling> fil = CaseFilling.retrieve(sql, params);
					
					if(fil!=null && fil.size()>0) {
						cz.setFilling(fil.get(0));
						caseLookup.add(cz);
						sendEmail = true;
					}
				}catch(Exception e){}
			}
		
			
		}
		
		if(sendEmail) {
			//sending email
			sendingSummonMail(caseLookup);
		}
		
	}
	
	private static void sendingSummonMail(List<Cases> caseLookup) {
		
		String html = "";
		
		html = "<p><strong>Hi,</strong></p>";
		html += "<p><br></p>";
		html += "<p>Please see below scheduled summon for today <strong style=\"color: rgb(230, 0, 0);\" class=\"ql-size-large\">"+ DateUtils.getCurrentDateMMMMDDYYYY() +"</strong> for your reference.</p>";
		html += "<p>	</p>";
		
		for(Cases cz : caseLookup) {
			html += "<p>	   <strong style=\"color: rgb(0, 102, 204);\" class=\"ql-size-large\">"+ cz.getFilling().getSettlementTime() +"</strong></p>";
			html += "<p>			<strong>"+ cz.getKindName() +" - "+ cz.getComplainants() +" </strong><strong style=\"color: rgb(230, 0, 0);\">VS</strong><strong> "+ cz.getRespondents() +"</strong></p>";
		}
		
		
		html += "<p>	</p>";
		html += "<p><br></p>";
		html += "<p><strong style=\"color: rgb(230, 0, 0);\">THIS IS A SYSTEM GENERATED EMAIL. PLEASE DO NOT REPLY</strong></p>";
		html += "<p><br></p>";
		html += "<p><br></p>";
		html += "<p><br></p>";
		html += "<p>Best regards,</p>";
		html += "<p><strong>BRIS TEAM</strong></p>";
		html += "<p><hr/></p>";
		html += "<p><br></p>";
		
		int cnt=1;
		String toMailUser = "";
		boolean isCheckNote=false;
		for(String id : readUsersSummon().split(",")) {
			
					String sql = "SELECT * FROM userdtls WHERE isactive=1 AND jobtitleid=?";
					String[] params = new String[1];
					params[0] = id;
					List<UserDtls> toUsers = UserDtls.retrieve(sql, params);
					
					if(toUsers.size()>0) {
						isCheckNote=true;
						if(toUsers.size()>1) {
							String toM = "";
							int cntM=1;
							for(UserDtls user : toUsers) {
								if(cntM>1) {
									toM += ":"+user.getUserdtlsid()+"";
								}else {
									toM = user.getUserdtlsid()+"";
								}
								cntM++;	
							}
							if(cnt>1) {
								toMailUser += ":"+toM;
							}else {
								toMailUser = toM;
							}
							
						}else {
							if(cnt>1) {
								toMailUser += ":"+toUsers.get(0).getUserdtlsid()+"";
							}else {
								toMailUser = toUsers.get(0).getUserdtlsid()+"";
							}
						}
					}	
				
			
			cnt++;
		}
		
		System.out.println("send to " + toMailUser);
		
		isCheckNote=true;
		if(isCheckNote && !toMailUser.isEmpty()) {
			
			boolean isMore = false;
			try {
				String[] em = toMailUser.split(":");
				isMore=true;
		    }catch(Exception ex) {}
			if(isMore) {
				for(String sendTo : toMailUser.split(":")) {
					Email e = new Email();
					e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
					e.setTitle("Report for ["+ readDate() +"] SUMMON");
					
					e.setType(EmailType.INBOX.getId());
					e.setIsOpen(0);
					e.setIsDeleted(0);
					e.setIsActive(1);
					
					e.setToEmail(toMailUser);
					e.setPersonCopy(Long.valueOf(sendTo));
					e.setFromEmail("0");
					
					
					String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + sendTo;
					Email.emailSavePath(html, fileName);
					e.setContendId(fileName);
					e.save();
					
				}
			}else {
				Email e = new Email();
				e.setSendDate(DateUtils.getCurrentDateYYYYMMDD());
				e.setTitle("Report for ["+ readDate() +"] SUMMON");
				
				e.setType(EmailType.INBOX.getId());
				e.setIsOpen(0);
				e.setIsDeleted(0);
				e.setIsActive(1);
				
				e.setToEmail(toMailUser);
				e.setPersonCopy(Long.valueOf(toMailUser));
				e.setFromEmail("0");
				
				String fileName = DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "-" + toMailUser;
				Email.emailSavePath(html, fileName);
				
				e.setContendId(fileName);
				e.save();
				
			}
		}
	}
	
	
	private static void collecInfo() {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "bris-runner" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		String msg = "Do not delete this file\n";
		msg += "Date Created: " + DateUtils.getCurrentDateMMMMDDYYYY();
		msg += "\nAuthor: MARXMIND\n";
		msg += "-------------------\n";
		msg += "\n";
		
		msg += collectCitizenInfoPerZone();
		msg += checksInfo();
		msg += mooeInfo();
		msg += casesInfo();
		msg += commonDocumentsInfo(); 
		
		File email = new File(emailPath + "dashboard-data.bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String collectCitizenInfoPerZone() {
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		
		String sql = " AND cus.cusisactive=1 AND mun.munname=? AND bar.bgname=?";
		String[] params = new String[2];
		params[0] = MUNICIPALITY;
		params[1] = BARANGAY;
		
		Map<String, Integer> citMap = new HashMap<String, Integer>();//Collections.synchronizedMap(new HashMap<String, Integer>());
		
		int total= 0;
		String firstRecordedData="";
		int maleCount = 0;
		int femaleCount = 0;
		
		int zero_ten = 0;
		int eleven_twenty = 0;
		int twenty_one_thirty = 0;
		int thirty_one_fourty = 0;
		int fourty_one_fifty = 0;
		int fifty_one_sixty = 0;
		int sixty_one_above = 0;
		
		for(Customer c : Customer.retrieve(sql, params)) {
			String purok = c.getPurok().getPurokName();
			
			if(citMap!=null && citMap.containsKey(purok)) {
				int count = citMap.get(purok) + 1;
				citMap.put(purok, count);
			}else {
				citMap.put(purok, 1);
			}
			
			//get the count of male and female
			if("1".equalsIgnoreCase(c.getGender())) {//male else female
				maleCount +=1;
			}else {
				femaleCount +=1;
			}
			
			if(total==0) {//get the total recorded citizen
				firstRecordedData =c.getDateregistered().split("-")[0];
			}
			
			//get age bracket
			if(c.getAge()>=0 && c.getAge()<=10) {
				zero_ten +=1;
			}else if(c.getAge()>=11 && c.getAge()<=20) {
				eleven_twenty +=1;
			}else if(c.getAge()>=21 && c.getAge()<=30) {
				twenty_one_thirty +=1;
			}else if(c.getAge()>=31 && c.getAge()<=40) {
				thirty_one_fourty +=1;
			}else if(c.getAge()>=41 && c.getAge()<=50) {
				fourty_one_fifty +=1;
			}else if(c.getAge()>=51 && c.getAge()<=60) {
				fifty_one_sixty +=1;
			}else {
				sixty_one_above +=1;
			}
			
			total++;//count total citizen live in barangay
		}
		
		//String data = "Registered Citizen Information\n";
		StringBuilder data = new StringBuilder();
		data.append("Registered Citizen Information\n");
		
		//data += "cit-start-record="+firstRecordedData+"\n";
		data.append("cit-start-record="+firstRecordedData+"\n");
		
		total = (total>1? total-1 : total);
		//data += "cit-reg-total="+total+"\n";
		data.append("cit-reg-total="+total+"\n");
		
		//data +="cit-male="+maleCount+"\n";
		//data +="cit-female="+femaleCount+"\n";
		data.append("cit-male="+maleCount+"\n");
		data.append("cit-female="+femaleCount+"\n");
		
		//data +="cit-zones=";
		data.append("cit-zones=");
		int countP = 1;
		for(String purok : citMap.keySet()) {
			if(countP==1) {
				//data += purok +"=" + citMap.get(purok);
				data.append(purok +"=" + citMap.get(purok));
			}else {
				//data +=","+ purok +"=" + citMap.get(purok);
				data.append(","+ purok +"=" + citMap.get(purok));
			}
			countP++;
		}
		//data +="\n";
		data.append("\n");
		
		//data +="\nAge Bracket\n";
		//data += "cit-brackets=";
		data.append("\nAge Bracket\n");
		data.append("cit-brackets=");
		
		boolean hasAgeRecorded = false;
		if(zero_ten>0) {
			//data +="0-10="+zero_ten;
			data.append("0-10="+zero_ten);
			hasAgeRecorded = true;
		}
		if(eleven_twenty>0) {
			//data += (hasAgeRecorded? "," : "" ) +"11-20="+eleven_twenty;
			data.append((hasAgeRecorded? "," : "" ) +"11-20="+eleven_twenty);
			hasAgeRecorded = true;
		}
		if(twenty_one_thirty>0) {
			//data += (hasAgeRecorded? "," : "" ) +"21-30="+twenty_one_thirty;
			data.append((hasAgeRecorded? "," : "" ) +"21-30="+twenty_one_thirty);
			hasAgeRecorded = true;
		}
		if(thirty_one_fourty>0) {
			//data += (hasAgeRecorded? "," : "" ) +"31-40="+thirty_one_fourty;
			data.append((hasAgeRecorded? "," : "" ) +"31-40="+thirty_one_fourty);
			hasAgeRecorded = true;
		}
		if(fourty_one_fifty>0) {
			//data += (hasAgeRecorded? "," : "" ) +"41-50="+fourty_one_fifty;
			data.append((hasAgeRecorded? "," : "" ) +"41-50="+fourty_one_fifty);
			hasAgeRecorded = true;
		}
		if(fifty_one_sixty>0) {
			//data += (hasAgeRecorded? "," : "" ) +"51-60="+fifty_one_sixty;
			data.append((hasAgeRecorded? "," : "" ) +"51-60="+fifty_one_sixty);
			hasAgeRecorded = true;
		}
		if(sixty_one_above>0) {
			//data += (hasAgeRecorded? "," : "" ) +"61-100="+sixty_one_above;
			data.append((hasAgeRecorded? "," : "" ) +"61-100="+sixty_one_above);
		}
		//data +="\n";
		data.append("\n");
		return data.toString();
	}
	
	
	private static String checksInfo() {
		//String data = "\nCheck Disbursement Information\n";
		StringBuilder data = new StringBuilder();
		data.append("\nCheck Disbursement Information\n");
		int year = DateUtils.getCurrentYear() - 1;
		String sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?)";
		String[] params = new String[2];
		params[0] = year + "-01-01";
		params[1] = year + "-12-31";
		
		Map<Integer, Double> checkLast = new HashMap<Integer, Double>();//Collections.synchronizedMap(new HashMap<Integer, Double>());
		Map<Integer, Double> checkCurrent = new HashMap<Integer, Double>();//Collections.synchronizedMap(new HashMap<Integer, Double>());
		boolean hasData = false;
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)) {
			int month = Integer.valueOf(chk.getDateTrans().split("-")[1]); //month
			
			if(checkLast!=null && checkLast.containsKey(month)) {
				double amount = checkLast.get(month);
				amount += chk.getAmount();
				checkLast.put(month, amount);
			}else {
				checkLast.put(month, chk.getAmount());
			}
			hasData = true;
		}
		//data += "check-months-lastyear-used=";
		data.append("check-months-lastyear-used=");
		if(!hasData) {
			//data +="1=0";
			data.append("1=0");
		}
		
		int count = 1;
		
		for(int month : checkLast.keySet()) {
			if(count==1) {
				//data += month +"=" + checkLast.get(month);
				data.append(month +"=" + checkLast.get(month));
			}else {
				//data += "," + month +"=" + checkLast.get(month);
				data.append("," + month +"=" + checkLast.get(month));
			}
			count++;
		}
		
		
		hasData = false;
		sql = " AND chk.isactivechk=1 AND (chk.datetrans>=? AND chk.datetrans<=?)";
		params = new String[2];
		params[0] = DateUtils.getCurrentYear() + "-01-01";
		params[1] = DateUtils.getCurrentYear() + "-12-31";
		
		for(Chequedtls chk : Chequedtls.retrieve(sql, params)) {
			int month = Integer.valueOf(chk.getDateTrans().split("-")[1]); //month
			
			if(checkCurrent!=null && checkCurrent.containsKey(month)) {
				double amount = checkCurrent.get(month);
				amount += chk.getAmount();
				checkCurrent.put(month, amount);
			}else {
				checkCurrent.put(month, chk.getAmount());
			}
			hasData = true;
		}
		
		//data += "\ncheck-months-used=";
		data.append("\ncheck-months-used=");
		if(!hasData) {
			//data +="1=0";
			data.append("1=0");
		}
		
		count = 1;
		
		for(int month : checkCurrent.keySet()) {
			if(count==1) {
				//data += month +"=" + checkCurrent.get(month);
				data.append(month +"=" + checkCurrent.get(month));
			}else {
				//data += "," + month +"=" + checkCurrent.get(month);
				data.append("," + month +"=" + checkCurrent.get(month));
			}
			count++;
		}
		//data +="\n";
		data.append("\n");
		return data.toString();
	}
	
	public static String mooeInfo() {
		//String data = "\nMOOE\n";
		StringBuilder data = new StringBuilder();
		data.append("\nMOOE\n");
		String sql = " AND mo.moisactive=1 AND mo.moyear=?";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYear()+"";
		int count = 1;
		//data +="check-mooe=";
		data.append("check-mooe=");
		boolean hasData = false;
		for(MOOE m : MOOE.retrieve(sql, params)) {
			
			if(count==1) {
				//data += m.getName() + "=" + m.getAmount();
				data.append(m.getName() + "=" + m.getAmount());
			}else {
				//data +=","+ m.getName() + "=" + m.getAmount();
				data.append(","+ m.getName() + "=" + m.getAmount());
			}
			hasData = true;
			count++;
		}
		
		if(!hasData) {
			//data +="No Data yet=0";
			data.append("No Data yet=0");
		}else {
			//data +="\n";
			data.append("\n");
		}
		
		return data.toString();
	}
	
	
	public static String casesInfo() {
		//String data = "\nCases Info\n";
		StringBuilder data = new StringBuilder();
		data.append("\nCases Info\n");
		int year = DateUtils.getCurrentYear() - 1;
		String sql = " AND ciz.caseisactive=1 AND (ciz.casedate>=? AND ciz.casedate<=?) AND ciz.casestatus!=?";
		String[] params = new String[3];
		params[0] = year + "-01-01";
		params[1] = year + "-12-31";
		params[2] = CaseStatus.CANCELLED.getId()+"";
		
		
		Map<Integer, Integer> casesLast = new HashMap<Integer, Integer>();//Collections.synchronizedMap(new HashMap<Integer, Integer>());
		Map<Integer, Integer> casesCurrent = new HashMap<Integer, Integer>();//Collections.synchronizedMap(new HashMap<Integer, Integer>());
		boolean hasData = false;
		for(Cases cz : Cases.retrieve(sql, params)) {
			int month = Integer.valueOf(cz.getDate().split("-")[1]); //month
			
			if(casesLast!=null && casesLast.containsKey(month)) {
				int amount = casesLast.get(month);
				amount += 1;
				casesLast.put(month, amount);
			}else {
				casesLast.put(month, 1);
			}
			hasData = true;
		}
		//data += "cases-months-last-year=";
		data.append("cases-months-last-year=");
		if(!hasData) {
			//data +="1=0";
			data.append("1=0");
		}
		
		int count = 1;
		for(int month : casesLast.keySet()) {
			if(count==1) {
				//data += month +"=" + casesLast.get(month);
				data.append(month +"=" + casesLast.get(month));
			}else {
				//data += "," + month +"=" + casesLast.get(month);
				data.append("," + month +"=" + casesLast.get(month));
			}
			count++;
		}
		
		hasData = false;
		sql = " AND ciz.caseisactive=1 AND (ciz.casedate>=? AND ciz.casedate<=?) AND ciz.casestatus!=?";
		params = new String[3];
		params[0] = DateUtils.getCurrentYear() + "-01-01";
		params[1] = DateUtils.getCurrentYear() + "-12-31";
		params[2] = CaseStatus.CANCELLED.getId()+"";
		
		for(Cases cz : Cases.retrieve(sql, params)) {
			int month = Integer.valueOf(cz.getDate().split("-")[1]); //month
			
			if(casesCurrent!=null && casesCurrent.containsKey(month)) {
				int amount = casesCurrent.get(month);
				amount += 1;
				casesCurrent.put(month, amount);
			}else {
				casesCurrent.put(month, 1);
			}
			hasData = true;
		}
		
		//data += "\ncases-months-this-year=";
		data.append("\ncases-months-this-year=");
		if(!hasData) {
			//data +="1=0";
			data.append("1=0");
		}
		
		count = 1;
		for(int month : casesCurrent.keySet()) {
			if(count==1) {
				//data += month +"=" + casesCurrent.get(month);
				data.append(month +"=" + casesCurrent.get(month));
			}else {
				//data += "," + month +"=" + casesCurrent.get(month);
				data.append("," + month +"=" + casesCurrent.get(month));
			}
			count++;
		}
		//data +="\n";
		data.append("\n");
		
		return data.toString();
	}
	
	/**
	 * 
	 * Retrieve the 10 common certificate
	 */
	private static String commonDocumentsInfo() {
		//String data="\nCommon Documents Requested\n";
		StringBuilder data = new StringBuilder();
		data.append("\nCommon Documents Requested\n");
		//data += "";
		data.append("");
		
		String sql = " AND clz.isactiveclearance=1 AND (clz.clearissueddate>=? AND clz.clearissueddate<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.getCurrentYear() + "-01-01";
		params[1] = DateUtils.getCurrentYear() + "-12-31";
		
		Map<Integer, Integer> docs = new HashMap<Integer, Integer>();//Collections.synchronizedMap(new HashMap<Integer, Integer>());
		for(Clearance c : Clearance.retrieve(sql, params)) {
			int key = c.getPurposeType();
			if(docs!=null && docs.containsKey(key)) {
				int count = docs.get(key) + 1;
				docs.put(key, count);
			}else {
				docs.put(key, 1);
			}
		}
		
		Map<Integer, String> unsort = new HashMap<Integer, String>();//Collections.synchronizedMap(new HashMap<Integer, String>());
		for(int key : docs.keySet()) {
			String value = Purpose.typeName(key) + "=" + docs.get(key);
			unsort.put(docs.get(key), value);
		}
		
		Map<Integer, String> sorted = new TreeMap<Integer, String>(unsort);
		
		int size = sorted.size();
		int big = size -1;
		//get the ten most common documents
		
		
		
		if(size>10) {
			List<String> dataList = new ArrayList<String>();
			for(String val : sorted.values()) {
				dataList.add(val);
			}
			Collections.reverse(dataList);
			int cnt = 1;
			//select only the top ten most common documents if morethan ten the selection
			//data += "doc-common=";
			data.append("doc-common=");
			for(String val : dataList) {
				if(cnt<=10) {
					if(cnt==1) {
						//data += val;
						data.append(val);
					}else {
						//data +=","+ val;
						data.append(","+ val);
					}
				}else {
					break;
				}
				cnt++;
			}
			
		}else {
			//add all data if not greater than 10
			//data += "doc-common=";
			data.append("doc-common=");
			int count = 1;
			for(int i : sorted.keySet()) {
				if(count==1) {
					//data += sorted.get(i);
					data.append(sorted.get(i));
				}else {
					//data +=","+ sorted.get(i);
					data.append(","+ sorted.get(i));
				}
				count++;		
			}
			
		}
		//data +="\n";
		data.append("\n");
		return data.toString();
	}
	
}



















