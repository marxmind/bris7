package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 01/19/2019
 *
 */
public class CommelecNames {

	private long id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String fullName;
	private String address;
	
	private Timestamp timeStamp;
	
	public static String extractUpdateNote() {
		String sql = "SELECT timestampcom FROM commelecnames limit 1";
		String note = "No Extracted Information yet from Commelec";
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next()){
			note = rs.getString("timestampcom");
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		return note;
	}
	
	public static List<CommelecNames> retrieve(String sqlAdd, String[] params){
		List<CommelecNames> coms = new ArrayList<CommelecNames>();
		
		String sql = "SELECT * FROM commelecnames WHERE ";
				
		
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
		
		System.out.println("Commelect SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			CommelecNames na = new CommelecNames();
			try{na.setId(rs.getLong("comid"));}catch(NullPointerException e){}
			try{na.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{na.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{na.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{na.setFullName(rs.getString("fullname"));}catch(NullPointerException e){}
			try{na.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			
			
			coms.add(na);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return coms;
	}
	
	public static CommelecNames save(CommelecNames na){
		if(na!=null){
			
			long id = CommelecNames.getInfo(na.getId() ==0? CommelecNames.getLatestId()+1 : na.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				na = CommelecNames.insertData(na, "1");
			}else if(id==2){
				LogU.add("update Data ");
				na = CommelecNames.updateData(na);
			}else if(id==3){
				LogU.add("added new Data ");
				na = CommelecNames.insertData(na, "3");
			}
			
		}
		return na;
	}
	
	public void save(){
			
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static CommelecNames insertData(CommelecNames na, String type){
		String sql = "INSERT INTO commelecnames ("
				+ "comid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "fullname,"
				+ "address)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table commelecnames");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			na.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			na.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, na.getFirstName());
		ps.setString(cnt++, na.getMiddleName());
		ps.setString(cnt++, na.getLastName());
		ps.setString(cnt++, na.getFullName());
		ps.setString(cnt++, na.getAddress());
		
		LogU.add(na.getFirstName());
		LogU.add(na.getMiddleName());
		LogU.add(na.getLastName());
		LogU.add(na.getFullName());
		LogU.add(na.getAddress());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to commelecnames : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return na;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO commelecnames ("
				+ "comid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "fullname,"
				+ "address)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table commelecnames");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setString(cnt++, getFullName());
		ps.setString(cnt++, getAddress());
		
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getFullName());
		LogU.add(getAddress());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to commelecnames : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static CommelecNames updateData(CommelecNames na){
		String sql = "UPDATE commelecnames SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "fullname=?,"
				+ "address=? " 
				+ " WHERE comid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table commelecnames");
		
		
		ps.setString(cnt++, na.getFirstName());
		ps.setString(cnt++, na.getMiddleName());
		ps.setString(cnt++, na.getLastName());
		ps.setString(cnt++, na.getFullName());
		ps.setString(cnt++, na.getAddress());
		ps.setLong(cnt++, na.getId());
		
		LogU.add(na.getFirstName());
		LogU.add(na.getMiddleName());
		LogU.add(na.getLastName());
		LogU.add(na.getFullName());
		LogU.add(na.getAddress());
		LogU.add(na.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to commelecnames : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return na;
	}
	
	public void updateData(){
		String sql = "UPDATE commelecnames SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "fullname=?,"
				+ "address=? " 
				+ " WHERE comid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table commelecnames");
		
		
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setString(cnt++, getFullName());
		ps.setString(cnt++, getAddress());
		
		ps.setLong(cnt++, getId());
		
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getFullName());
		LogU.add(getAddress());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to commelecnames : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT comid FROM commelecnames  ORDER BY comid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("comid");
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
		ps = conn.prepareStatement("SELECT comid FROM commelecnames WHERE comid=?");
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
		String sql = "DELETE FROM commelecnames WHERE comid=?";
		
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

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
