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
 * @author mark italia
 * @since 10/16/2017
 * @version 1.0
 *
 */

public class Races {

	private int id;
	private String name;
	private int isActive;
	private int isIndigent;
	private Timestamp timestamp;
	private String indigentName;
	
	public static List<Races> retrieve(String sqlAdd, String[] params){
		List<Races> rcs = new ArrayList<Races>();
		
		String tableRc = "rc";
		
		String sql = "SELECT * FROM races " + tableRc  + " WHERE " + tableRc + ".isactiverace=1 ";
						
		
		sql += sqlAdd;
		
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
			
			Races rc = new Races();
			try{rc.setId(rs.getInt("raceid"));}catch(NullPointerException e){}
			try{rc.setName(rs.getString("racename"));}catch(NullPointerException e){}
			try{rc.setIsActive(rs.getInt("isactiverace"));}catch(NullPointerException e){}
			try{rc.setIsIndigent(rs.getInt("isindigent"));}catch(NullPointerException e){}
			try{rc.setTimestamp(rs.getTimestamp("timestamprace"));}catch(NullPointerException e){}
			
			rc.setIndigentName(rc.getIsIndigent()==1? "Yes" : "No");
			
			rcs.add(rc);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rcs;
	}
	
	public static Races retrieve(int id){
		Races rc = new Races();
		
		String tableRc = "rc";
		
		String sql = "SELECT * FROM races " + tableRc  + " WHERE " + tableRc + ".isactiverace=1 AND " + tableRc + ".raceid=" + id;
			
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
		
			try{rc.setId(rs.getInt("raceid"));}catch(NullPointerException e){}
			try{rc.setName(rs.getString("racename"));}catch(NullPointerException e){}
			try{rc.setIsActive(rs.getInt("isactiverace"));}catch(NullPointerException e){}
			try{rc.setIsIndigent(rs.getInt("isindigent"));}catch(NullPointerException e){}
			try{rc.setTimestamp(rs.getTimestamp("timestamprace"));}catch(NullPointerException e){}
			
			rc.setIndigentName(rc.getIsIndigent()==1? "Yes" : "No");
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rc;
	}
	
	public static Races save(Races rc){
		if(rc!=null){
			
			int id = Races.getInfo(rc.getId() ==0? Races.getLatestId()+1 : rc.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rc = Races.insertData(rc, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rc = Races.updateData(rc);
			}else if(id==3){
				LogU.add("added new Data ");
				rc = Races.insertData(rc, "3");
			}
			
		}
		return rc;
	}
	
	public void save(){
			
			int id = getInfo(getId() ==0? getLatestId()+1 : getId());
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
	
	public static Races insertData(Races rc, String type){
		String sql = "INSERT INTO races ("
				+ "raceid,"
				+ "racename,"
				+ "isactiverace,"
				+ "isindigent) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table races");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			rc.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			rc.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, rc.getName());
		ps.setInt(cnt++, rc.getIsActive());
		ps.setInt(cnt++, rc.getIsIndigent());
		
		LogU.add(rc.getName());
		LogU.add(rc.getIsActive());
		LogU.add(rc.getIsIndigent());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to races : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO races ("
				+ "raceid,"
				+ "racename,"
				+ "isactiverace,"
				+ "isindigent) " 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table races");
		if("1".equalsIgnoreCase(type)){
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setInt(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getIsIndigent());
		
		LogU.add(getName());
		LogU.add(getIsActive());
		LogU.add(getIsIndigent());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to races : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Races updateData(Races rc){
		String sql = "UPDATE races SET "
				+ "racename=?,"
				+ "isindigent=?" 
				+ " WHERE raceid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table races");
		
		
		ps.setString(cnt++, rc.getName());
		ps.setInt(cnt++, rc.getIsIndigent());
		ps.setInt(cnt++, rc.getId());
		
		LogU.add(rc.getName());
		LogU.add(rc.getIsIndigent());
		LogU.add(rc.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to races : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rc;
	}
	
	public void updateData(){
		String sql = "UPDATE races SET "
				+ "racename=?,"
				+ "isindigent=?" 
				+ " WHERE raceid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table races");
		
		
		ps.setString(cnt++, getName());
		ps.setInt(cnt++, getIsIndigent());
		ps.setInt(cnt++, getId());
		
		LogU.add(getName());
		LogU.add(getIsIndigent());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to races : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT raceid FROM races  ORDER BY raceid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("raceid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestId();	
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT raceid FROM races WHERE raceid=?");
		ps.setInt(1, id);
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
		String sql = "UPDATE races set isactiverace=0 WHERE raceid=?";
		
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
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getIsIndigent() {
		return isIndigent;
	}

	public void setIsIndigent(int isIndigent) {
		this.isIndigent = isIndigent;
	}

	public String getIndigentName() {
		return indigentName;
	}

	public void setIndigentName(String indigentName) {
		this.indigentName = indigentName;
	}
	
}
