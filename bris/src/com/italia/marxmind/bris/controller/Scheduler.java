package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.CaseKind;
import com.italia.marxmind.bris.enm.CaseStatus;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @since 10/26/2018
 * @version 1.0
 *
 */
public class Scheduler {

	private long id;
	private String startDate;
	private String endDate;
	private String notes;
	private int isActive;
	
	private String memos;
	
	public static List<Scheduler> retrieve(String sqlAdd, String[] params){
		List<Scheduler> scs = new ArrayList<Scheduler>();
		
		String sql = "SELECT * FROM scheduler  WHERE isactivesc=1 ";
		
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
		//System.out.println("SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Scheduler sc = new Scheduler();
			sc.setId(rs.getLong("schedid"));
			sc.setStartDate(rs.getString("startdate"));
			sc.setEndDate(rs.getString("endate"));
			sc.setNotes(rs.getString("notes"));
			sc.setIsActive(rs.getInt("isactivesc"));
			
			scs.add(sc);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return scs;
	}
	
	public static Scheduler save(Scheduler sc){
		if(sc!=null){
			
			long id = Scheduler.getInfo(sc.getId() ==0? Scheduler.getLatestId()+1 : sc.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				sc = Scheduler.insertData(sc, "1");
			}else if(id==2){
				LogU.add("update Data ");
				sc = Scheduler.updateData(sc);
			}else if(id==3){
				LogU.add("added new Data ");
				sc = Scheduler.insertData(sc, "3");
			}
			
		}
		return sc;
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
	
	public static Scheduler insertData(Scheduler sc, String type){
		String sql = "INSERT INTO scheduler ("
				+ "schedid,"
				+ "startdate,"
				+ "endate,"
				+ "notes,"
				+ "isactivesc)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table scheduler");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			sc.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			sc.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, sc.getStartDate());
		ps.setString(cnt++, sc.getEndDate());
		ps.setString(cnt++, sc.getNotes());
		ps.setInt(cnt++, sc.getIsActive());
		
		LogU.add(sc.getStartDate());
		LogU.add(sc.getEndDate());
		LogU.add(sc.getNotes());
		LogU.add(sc.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to scheduler : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO scheduler ("
				+ "schedid,"
				+ "startdate,"
				+ "endate,"
				+ "notes,"
				+ "isactivesc)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table scheduler");
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
		
		ps.setString(cnt++, getStartDate());
		ps.setString(cnt++, getEndDate());
		ps.setString(cnt++, getNotes());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getStartDate());
		LogU.add(getEndDate());
		LogU.add(getNotes());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to scheduler : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Scheduler updateData(Scheduler sc){
		String sql = "UPDATE scheduler SET "
				+ "startdate=?,"
				+ "endate=?,"
				+ "notes=? " 
				+ " WHERE schedid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table scheduler");
		
		ps.setString(cnt++, sc.getStartDate());
		ps.setString(cnt++, sc.getEndDate());
		ps.setString(cnt++, sc.getNotes());
		ps.setLong(cnt++, sc.getId());
		
		LogU.add(sc.getStartDate());
		LogU.add(sc.getEndDate());
		LogU.add(sc.getNotes());
		LogU.add(sc.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to scheduler : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return sc;
	}
	
	public void updateData(){
		String sql = "UPDATE scheduler SET "
				+ "startdate=?,"
				+ "endate=?,"
				+ "notes=? " 
				+ " WHERE schedid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table scheduler");
		
		ps.setString(cnt++, getStartDate());
		ps.setString(cnt++, getEndDate());
		ps.setString(cnt++, getNotes());
		ps.setLong(cnt++, getId());
		
		LogU.add(getStartDate());
		LogU.add(getEndDate());
		LogU.add(getNotes());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to scheduler : " + s.getMessage());
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
		sql="SELECT schedid FROM scheduler  ORDER BY schedid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("schedid");
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
		ps = conn.prepareStatement("SELECT schedid FROM scheduler WHERE schedid=?");
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
		String sql = "UPDATE scheduler set isactivesc=0 WHERE schedid=?";
		
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getMemos() {
		return memos;
	}

	public void setMemos(String memos) {
		this.memos = memos;
	}
	
}
