package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 01/16/2019
 *
 */
public class OD{

	
	private long id;
	private Employee officer;
	private int month;
	private int day;
	private int year;
	private int isActive;
	
	private String officerName;
	
	public String getOfficerName() {
		return officerName;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public static List<OD> retrieve(String sqlAdd, String[] params){
		List<OD> ods = new ArrayList<OD>();
		
		String tableOD = "od";
		String tableEmp = "em";
		
		String sql = "SELECT * FROM odassignment " + tableOD + ", employee " + tableEmp + " WHERE " +
				tableOD + ".empid=" + tableEmp + ".empid ";
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
		
		//System.out.println("OD SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			OD od = new OD();
			od.setId(rs.getLong("odid"));
			od.setMonth(rs.getInt("monthod"));
			od.setDay(rs.getInt("dayod"));
			od.setYear(rs.getInt("yearod"));
			od.setIsActive(rs.getInt("isactiveod"));
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsOfficial(rs.getInt("isofficial"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			try{emp.setFullName(rs.getString("firstname") + " " +rs.getString("lastname"));}catch(NullPointerException e){}
			emp.setGenderName(emp.getGender()==1? "Male" : "Female");
			Position pos = new Position();
			try{pos.setId(rs.getInt("posid"));}catch(NullPointerException e){}
			Committee com = new Committee();
			try{com.setId(rs.getInt("committeid"));}catch(NullPointerException e){}
			
			od.setOfficer(emp);
			
			ods.add(od);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return ods;
	}
	
	/**
	 * 
	 * @param month
	 * @param day
	 * @param year
	 * @return
	 */
	public static OD retrieveAssignedOfficer(int month, int day, int year){
		OD od = new OD();
		String tableOD = "od";
		String tableEmp = "em";
		
		String sql = "SELECT * FROM odassignment " + tableOD + ", employee " + tableEmp + " WHERE " +
				tableOD + ".empid=" + tableEmp + ".empid AND od.isactiveod=1 AND od.monthod=" + month + " AND od.dayod="+ day + " AND od.yearod="+year;
		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			od.setId(rs.getLong("odid"));
			od.setMonth(rs.getInt("monthod"));
			od.setDay(rs.getInt("dayod"));
			od.setYear(rs.getInt("yearod"));
			od.setIsActive(rs.getInt("isactiveod"));
			
			Employee emp = new Employee();
			try{emp.setId(rs.getLong("empid"));}catch(NullPointerException e){}
			try{emp.setDateRegistered(rs.getString("datereg"));}catch(NullPointerException e){}
			try{emp.setDateResigned(rs.getString("dateend"));}catch(NullPointerException e){}
			try{emp.setFirstName(rs.getString("firstname"));}catch(NullPointerException e){}
			try{emp.setMiddleName(rs.getString("middlename"));}catch(NullPointerException e){}
			try{emp.setLastName(rs.getString("lastname"));}catch(NullPointerException e){}
			try{emp.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{emp.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{emp.setIsOfficial(rs.getInt("isofficial"));}catch(NullPointerException e){}
			try{emp.setIsResigned(rs.getInt("isresigned"));}catch(NullPointerException e){}
			try{emp.setContactNo(rs.getString("contactno"));}catch(NullPointerException e){}
			try{emp.setPurok(rs.getString("purok"));}catch(NullPointerException e){}
			try{emp.setIsActiveEmployee(rs.getInt("isactiveemp"));}catch(NullPointerException e){}
			try{emp.setFullName(rs.getString("firstname") + " " +rs.getString("lastname"));}catch(NullPointerException e){}
			emp.setGenderName(emp.getGender()==1? "Male" : "Female");
			Position pos = new Position();
			try{pos.setId(rs.getInt("posid"));}catch(NullPointerException e){}
			Committee com = new Committee();
			try{com.setId(rs.getInt("committeid"));}catch(NullPointerException e){}
			
			od.setOfficer(emp);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return od;
	}
	
	public static OD save(OD od){
		if(od!=null){
			
			long id = OD.getInfo(od.getId() ==0? OD.getLatestId()+1 : od.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				od = OD.insertData(od, "1");
			}else if(id==2){
				LogU.add("update Data ");
				od = OD.updateData(od);
			}else if(id==3){
				LogU.add("added new Data ");
				od = OD.insertData(od, "3");
			}
			
		}
		return od;
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
	
	public static OD insertData(OD od, String type){
		String sql = "INSERT INTO odassignment ("
				+ "odid,"
				+ "empid,"
				+ "monthod,"
				+ "dayod,"
				+ "yearod,"
				+ "isactiveod)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table odassignment");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			od.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			od.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setLong(cnt++, od.getOfficer()==null? 0 : od.getOfficer().getId());
		ps.setInt(cnt++, od.getMonth());
		ps.setInt(cnt++, od.getDay());
		ps.setInt(cnt++, od.getYear());
		ps.setInt(cnt++, od.getIsActive());
		
		LogU.add(od.getOfficer()==null? 0 : od.getOfficer().getId());
		LogU.add(od.getMonth());
		LogU.add(od.getDay());
		LogU.add(od.getYear());
		LogU.add(od.getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to odassignment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return od;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO odassignment ("
				+ "odid,"
				+ "empid,"
				+ "monthod,"
				+ "dayod,"
				+ "yearod,"
				+ "isactiveod)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table odassignment");
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
		
		ps.setLong(cnt++, getOfficer()==null? 0 : getOfficer().getId());
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getDay());
		ps.setInt(cnt++, getYear());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getOfficer()==null? 0 : getOfficer().getId());
		LogU.add(getMonth());
		LogU.add(getDay());
		LogU.add(getYear());
		LogU.add(getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to odassignment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static OD updateData(OD od){
		String sql = "UPDATE odassignment SET "
				+ "empid=?,"
				+ "monthod=?,"
				+ "dayod=?,"
				+ "yearod=? " 
				+ " WHERE odid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table odassignment");
		
		ps.setLong(cnt++, od.getOfficer()==null? 0 : od.getOfficer().getId());
		ps.setInt(cnt++, od.getMonth());
		ps.setInt(cnt++, od.getDay());
		ps.setInt(cnt++, od.getYear());
		ps.setLong(cnt++, od.getId());
		
		LogU.add(od.getOfficer()==null? 0 : od.getOfficer().getId());
		LogU.add(od.getMonth());
		LogU.add(od.getDay());
		LogU.add(od.getYear());
		LogU.add(od.getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to odassignment : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return od;
	}
	
	public void updateData(){
		String sql = "UPDATE odassignment SET "
				+ "empid=?,"
				+ "monthod=?,"
				+ "dayod=?,"
				+ "yearod=? " 
				+ " WHERE odid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table odassignment");
		
		ps.setLong(cnt++, getOfficer()==null? 0 : getOfficer().getId());
		ps.setInt(cnt++, getMonth());
		ps.setInt(cnt++, getDay());
		ps.setInt(cnt++, getYear());
		ps.setLong(cnt++, getId());
		
		LogU.add(getOfficer()==null? 0 : getOfficer().getId());
		LogU.add(getMonth());
		LogU.add(getDay());
		LogU.add(getYear());
		LogU.add(getIsActive());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to odassignment : " + s.getMessage());
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
		sql="SELECT odid FROM odassignment  ORDER BY odid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("odid");
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
		ps = conn.prepareStatement("SELECT odid FROM odassignment WHERE odid=?");
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
		String sql = "UPDATE odassignment set isactiveod=0 WHERE odid=?";
		
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
	public Employee getOfficer() {
		return officer;
	}
	public int getMonth() {
		return month;
	}
	public int getDay() {
		return day;
	}
	public int getYear() {
		return year;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setOfficer(Employee officer) {
		this.officer = officer;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
}
