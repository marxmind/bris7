package com.italia.marxmind.bris.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.Bris;
import com.italia.marxmind.bris.reader.ReadConfig;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/30/2018
 *
 */

public class Email {

	private long id;
	private String sendDate;
	private String readDate;
	private String title;
	private String contendId;
	private int type;
	private int isOpen;
	private int isDeleted;
	private String fromEmail;
	private String toEmail;
	private int isActive;
	private long personCopy;
	private String timestamp;
	
	private String fromNames;
	private String toNames;
	private String contentMsg;
	
	private String fromNamesCut;
	private String toNamesCut;
	private String titleCut;
	
	public static void deleteEmail(String fileName) {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "email" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath + fileName);
		
		if(file.exists()) {
			file.delete();
		}
		
		
	}
	
	public static void saveAttachment(String fileName, String msg, String ext) {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "email" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		File email = new File(emailPath + fileName + "." + ext);
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		
		}catch(Exception e) {}
	}
	
	public static void transferAttachment(String contentFile) {
		
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String path_runner = Bris.PRIMARY_DRIVE.getName() + 
				Bris.SEPERATOR.getName() + 
				Bris.APP_FOLDER.getName() + 
				Bris.SEPERATOR.getName();
			
		String emailPath = path_runner + "email" + Bris.SEPERATOR.getName() + BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		emailPath += contentFile;
		
		System.out.println("emailPath = " + emailPath);
		
		String confFile = path_runner + "bris-runner" + Bris.SEPERATOR.getName() + "attach.bris";
		Properties prop = new Properties();
		String toPathLocation = "";
		try {
			prop.load(new FileInputStream(confFile));
			
			toPathLocation = prop.getProperty("Location");
		}catch(Exception e) {}
		
		System.out.println("toPathLocation = " + toPathLocation);
            
        File file = new File(emailPath);
            try{
    			Files.copy(file.toPath(), (new File(toPathLocation + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){e.printStackTrace();}
	}
	
	//--msgtype 1-inbox 2-outbox 3-send 4-draft 5-trash
	
	public static void replyEmailSavePath(String oldMsgContent, String oldFileName, String msg, String fileName) {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "email" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
			
		BufferedReader br = new BufferedReader(new FileReader(emailPath + oldFileName + ".bris"));	
		
		File email = new File(emailPath + fileName + ".bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		
		pw.println(msg);
		//include old email
		pw.println("<br/>");
		pw.println("<hr style=\"color: black;height:2px;\"/>");
		pw.println("<br/>");
		pw.println(oldMsgContent);
		pw.println("<br/>");
		
		String line = null;
        // Read from the original file and write to the new
        while ((line = br.readLine()) != null) {
            pw.println(line);
        }
        pw.flush();
		pw.close();
		br.close();
		
		
		}catch(Exception e) {}
	}
	
	public static void emailSavePath(String msg, String fileName) {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "email" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		File file = new File(emailPath);
		
		if(!file.isDirectory()) {
			file.mkdir();
		}
		
		try {
		
		File email = new File(emailPath + fileName + ".bris");
		PrintWriter pw = new PrintWriter(new FileWriter(email));
		pw.println(msg);
		pw.flush();
		pw.close();
		
		
		}catch(Exception e) {}
	}
	
	public static String readEmail(String fileName) {
		String BARANGAY = ReadConfig.value(Bris.BARANGAY);
		String MUNICIPALITY = ReadConfig.value(Bris.MUNICIPALITY);
		String emailPath = Bris.PRIMARY_DRIVE.getName() + 
		Bris.SEPERATOR.getName() + 
		Bris.APP_FOLDER.getName() + 
		Bris.SEPERATOR.getName() + "email" + Bris.SEPERATOR.getName();
		
		emailPath += BARANGAY + "-" + MUNICIPALITY;
		emailPath += Bris.SEPERATOR.getName();
		
		String msg="";
		try {
		 File email = new File(emailPath + fileName + ".bris");
		 BufferedReader br = new BufferedReader(new FileReader(email));
		 String line = null;
	        // Read from the original file and write to the new
	        while ((line = br.readLine()) != null) {
	        	msg += line;
	        }
	     br.close();
		}catch(Exception e) {}
		return msg;
	}
	
	public static List<Email> retrieve(String sqlAdd, String[] params){
		List<Email> mails = new ArrayList<>();
		
		
		String sql = "SELECT * FROM emsg  WHERE ismsgactive=1 ";
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Email mail = new Email();
			try{mail.setId(rs.getLong("msgid"));}catch(NullPointerException e){}
			try{mail.setSendDate(rs.getString("msgsenddate"));}catch(NullPointerException e){}
			try{mail.setReadDate(rs.getString("msgreaddate"));}catch(NullPointerException e){}
			try{mail.setTitle(rs.getString("msgtitle"));}catch(NullPointerException e){}
			try{mail.setContendId(rs.getString("msgcontentid"));}catch(NullPointerException e){}
			try{mail.setType(rs.getInt("msgtype"));}catch(NullPointerException e){}
			try{mail.setIsOpen(rs.getInt("isopen"));}catch(NullPointerException e){}
			try{mail.setIsDeleted(rs.getInt("isdeleted"));}catch(NullPointerException e){}
			try{mail.setIsActive(rs.getInt("ismsgactive"));}catch(NullPointerException e){}
			try{mail.setFromEmail(rs.getString("msgfromempid"));}catch(NullPointerException e){}
			try{mail.setToEmail(rs.getString("msgtoempid"));}catch(NullPointerException e){}
			
			try{mail.setPersonCopy(rs.getLong("personcpy"));}catch(NullPointerException e){}
			
			try{ mail.setTimestamp(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a").format(rs.getTimestamp("timestampmsg")));}catch(NullPointerException e){}
			
			mails.add(mail);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return mails;
	}
	
	public static int countNewEmail(String sqlAdd, String[] params) {
		String sql = "SELECT count(*) as count FROM emsg  WHERE ismsgactive=1 ";
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getInt("count");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return 0;
	}
	
	public static Email save(Email mail){
		if(mail!=null){
			
			long id = Email.getInfo(mail.getId() ==0? Email.getLatestId()+1 : mail.getId());
			
			if(id==1){
				mail = Email.insertData(mail, "1");
			}else if(id==2){
				mail = Email.updateData(mail);
			}else if(id==3){
				mail = Email.insertData(mail, "3");
			}
			
		}
		return mail;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		if(id==1){
			insertData("1");
		}else if(id==2){
			updateData();
		}else if(id==3){
			insertData("3");
		}
		
 }
	
	public static Email insertData(Email mail, String type){
		String sql = "INSERT INTO emsg ("
				+ "msgid,"
				+ "msgsenddate,"
				+ "msgreaddate,"
				+ "msgtitle,"
				+ "msgcontentid,"
				+ "msgtype,"
				+ "isopen,"
				+ "isdeleted,"
				+ "ismsgactive,"
				+ "msgtoempid,"
				+ "msgfromempid,"
				+ "personcpy)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			mail.setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			mail.setId(id);
		}
		
		ps.setString(cnt++, mail.getSendDate());
		ps.setString(cnt++, mail.getReadDate());
		ps.setString(cnt++, mail.getTitle());
		ps.setString(cnt++, mail.getContendId());
		ps.setInt(cnt++, mail.getType());
		ps.setInt(cnt++, mail.getIsOpen());
		ps.setInt(cnt++, mail.getIsDeleted());
		ps.setInt(cnt++, mail.getIsActive());
		ps.setString(cnt++, mail.getToEmail());
		ps.setString(cnt++, mail.getFromEmail());
		ps.setLong(cnt++, mail.getPersonCopy());
		
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
		}
		return mail;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO emsg ("
				+ "msgid,"
				+ "msgsenddate,"
				+ "msgreaddate,"
				+ "msgtitle,"
				+ "msgcontentid,"
				+ "msgtype,"
				+ "isopen,"
				+ "isdeleted,"
				+ "ismsgactive,"
				+ "msgtoempid,"
				+ "msgfromempid,"
				+ "personcpy)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
		}
		
		ps.setString(cnt++, getSendDate());
		ps.setString(cnt++, getReadDate());
		ps.setString(cnt++, getTitle());
		ps.setString(cnt++, getContendId());
		ps.setInt(cnt++, getType());
		ps.setInt(cnt++, getIsOpen());
		ps.setInt(cnt++, getIsDeleted());
		ps.setInt(cnt++, getIsActive());
		ps.setString(cnt++, getToEmail());
		ps.setString(cnt++, getFromEmail());
		ps.setLong(cnt++, getPersonCopy());
		
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
		}
		
	}
	
	public static Email updateData(Email mail){
		String sql = "UPDATE emsg SET "
				+ "msgsenddate=?,"
				+ "msgreaddate=?,"
				+ "msgtitle=?,"
				+ "msgcontentid=?,"
				+ "msgtype=?,"
				+ "isopen=?,"
				+ "isdeleted=?,"
				+ "msgtoempid=?,"
				+ "msgfromempid=?,"
				+ "personcpy=?" 
				+ " WHERE msgid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		ps.setString(cnt++, mail.getSendDate());
		ps.setString(cnt++, mail.getReadDate());
		ps.setString(cnt++, mail.getTitle());
		ps.setString(cnt++, mail.getContendId());
		ps.setInt(cnt++, mail.getType());
		ps.setInt(cnt++, mail.getIsOpen());
		ps.setInt(cnt++, mail.getIsDeleted());
		ps.setString(cnt++, mail.getToEmail());
		ps.setString(cnt++, mail.getFromEmail());
		ps.setLong(cnt++, mail.getPersonCopy());
		ps.setLong(cnt++, mail.getId());
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
		}
		return mail;
	}
	
	public void updateData(){
		String sql = "UPDATE emsg SET "
				+ "msgsenddate=?,"
				+ "msgreaddate=?,"
				+ "msgtitle=?,"
				+ "msgcontentid=?,"
				+ "msgtype=?,"
				+ "isopen=?,"
				+ "isdeleted=?,"
				+ "msgtoempid=?,"
				+ "msgfromempid=?,"
				+ "personcpy=?" 
				+ " WHERE msgid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		ps.setString(cnt++, getSendDate());
		ps.setString(cnt++, getReadDate());
		ps.setString(cnt++, getTitle());
		ps.setString(cnt++, getContendId());
		ps.setInt(cnt++, getType());
		ps.setInt(cnt++, getIsOpen());
		ps.setInt(cnt++, getIsDeleted());
		ps.setString(cnt++, getToEmail());
		ps.setString(cnt++, getFromEmail());
		ps.setLong(cnt++, getPersonCopy());
		ps.setLong(cnt++, getId());
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
		}
		
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT msgid FROM emsg  ORDER BY msgid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("msgid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT msgid FROM emsg WHERE msgid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE emsg set ismsgactive=0 WHERE msgid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getReadDate() {
		return readDate;
	}
	public void setReadDate(String readDate) {
		this.readDate = readDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContendId() {
		return contendId;
	}
	public void setContendId(String contendId) {
		this.contendId = contendId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	public int getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getFromNames() {
		return fromNames;
	}

	public void setFromNames(String fromNames) {
		this.fromNames = fromNames;
	}

	public String getToNames() {
		return toNames;
	}

	public void setToNames(String toNames) {
		this.toNames = toNames;
	}

	public long getPersonCopy() {
		return personCopy;
	}

	public void setPersonCopy(long personCopy) {
		this.personCopy = personCopy;
	}

	public String getContentMsg() {
		return contentMsg;
	}

	public void setContentMsg(String contentMsg) {
		this.contentMsg = contentMsg;
	}

	public String getFromNamesCut() {
		return fromNamesCut;
	}

	public void setFromNamesCut(String fromNamesCut) {
		this.fromNamesCut = fromNamesCut;
	}

	public String getToNamesCut() {
		return toNamesCut;
	}

	public void setToNamesCut(String toNamesCut) {
		this.toNamesCut = toNamesCut;
	}

	public String getTitleCut() {
		return titleCut;
	}

	public void setTitleCut(String titleCut) {
		this.titleCut = titleCut;
	}
	
}

