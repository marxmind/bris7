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
 * @since 09/15/2017
 * @version 1.0
 *
 */
public class LuponPerson extends Persons{

	private long id;
	private String dateTrans;
	private int isActive;
	private Timestamp timestamp;
	private Employee luponPerson;
	private Blotters blotters;
	
	public static List<LuponPerson> retrieve(String sqlAdd, String[] params){
		List<LuponPerson> reps = new ArrayList<LuponPerson>();
		
		String tableReps = "lup";
		String tableCit = "emp";
		String tableBot = "blot";
		
		String sql = "SELECT * FROM lupons " + tableReps + ", employee " + tableCit + ", blotters " + tableBot + " WHERE " +
				tableReps + ".luponsid="+ tableCit + ".empid AND " +
				tableReps + ".blotid=" + tableBot + ".blotid ";
		
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
			
			LuponPerson per = new LuponPerson();
			try{per.setId(rs.getLong("lupid"));}catch(NullPointerException e){}
			try{per.setDateTrans(rs.getString("lupdate"));}catch(NullPointerException e){}
			try{per.setIsActive(rs.getInt("isactivelup"));}catch(NullPointerException e){}
			try{per.setTimestamp(rs.getTimestamp("timestamplup"));}catch(NullPointerException e){}
			
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
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			emp.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			emp.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			emp.setProvince(prov);
			
			per.setLuponPerson(emp);
			
			Blotters blot = new Blotters();
			try{blot.setId(rs.getLong("blotid"));}catch(NullPointerException e){}
			try{blot.setDateTrans(rs.getString("blotdate"));}catch(NullPointerException e){}
			try{blot.setTimeTrans(rs.getString("blottime"));}catch(NullPointerException e){}
			try{blot.setIncidentDate(rs.getString("incidentDate"));}catch(NullPointerException e){}
			try{blot.setIncidentTime(rs.getString("incidenttime"));}catch(NullPointerException e){}
			try{blot.setIncidentPlace(rs.getString("incidentplace"));}catch(NullPointerException e){}
			//this will cause slow in data retrieval
			//solution this will only load during retrieval per person
			//try{blot.setIncidentDetails(rs.getString("indetails"));}catch(NullPointerException e){}
			//try{blot.setIncidentSolutions(rs.getString("insolutions"));}catch(NullPointerException e){}
			try{blot.setStatus(rs.getInt("blotstatus"));}catch(NullPointerException e){}
			try{blot.setIncidentType(rs.getInt("incidenttype"));}catch(NullPointerException e){}
			try{blot.setIsActive(rs.getInt("isactiveblot"));}catch(NullPointerException e){}
			per.setBlotters(blot);
			
			reps.add(per);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return reps;
	}
	
	public static LuponPerson save(LuponPerson rep){
		if(rep!=null){
			
			long id = LuponPerson.getInfo(rep.getId() ==0? LuponPerson.getLatestId()+1 : rep.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rep = LuponPerson.insertData(rep, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rep = LuponPerson.updateData(rep);
			}else if(id==3){
				LogU.add("added new Data ");
				rep = LuponPerson.insertData(rep, "3");
			}
			
		}
		return rep;
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
	
	public static LuponPerson insertData(LuponPerson rep, String type){
		String sql = "INSERT INTO lupons ("
				+ "lupid,"
				+ "lupdate,"
				+ "isactivelup,"
				+ "luponsid,"
				+ "blotid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table lupons");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			rep.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			rep.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, rep.getDateTrans());
		ps.setInt(cnt++, rep.getIsActive());
		ps.setLong(cnt++, rep.getLuponPerson()==null? 0 : rep.getLuponPerson().getId());
		ps.setLong(cnt++, rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		
		
		LogU.add(rep.getDateTrans());
		LogU.add(rep.getIsActive());
		LogU.add(rep.getLuponPerson()==null? 0 : rep.getLuponPerson().getId());
		LogU.add(rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to lupons : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO lupons ("
				+ "lupid,"
				+ "lupdate,"
				+ "isactivelup,"
				+ "luponsid,"
				+ "blotid)" 
				+ "values(?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table lupons");
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
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getLuponPerson()==null? 0 : getLuponPerson().getId());
		ps.setLong(cnt++, getBlotters()==null? 0 : getBlotters().getId());
		
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getLuponPerson()==null? 0 : getLuponPerson().getId());
		LogU.add(getBlotters()==null? 0 : getBlotters().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to lupons : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static LuponPerson updateData(LuponPerson rep){
		String sql = "UPDATE lupons SET "
				+ "lupdate=?,"
				+ "luponsid=?,"
				+ "blotid=?" 
				+ " WHERE lupid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table lupons");
		
		
		ps.setString(cnt++, rep.getDateTrans());
		ps.setLong(cnt++, rep.getLuponPerson()==null? 0 : rep.getLuponPerson().getId());
		ps.setLong(cnt++, rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		ps.setLong(cnt++, rep.getId());
		
		LogU.add(rep.getDateTrans());
		LogU.add(rep.getLuponPerson()==null? 0 : rep.getLuponPerson().getId());
		LogU.add(rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		LogU.add(rep.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to lupons : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void updateData(){
		String sql = "UPDATE lupons SET "
				+ "lupdate=?,"
				+ "luponsid=?,"
				+ "blotid=?" 
				+ " WHERE lupid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table lupons");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setLong(cnt++, getLuponPerson()==null? 0 : getLuponPerson().getId());
		ps.setLong(cnt++, getBlotters()==null? 0 : getBlotters().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getLuponPerson()==null? 0 : getLuponPerson().getId());
		LogU.add(getBlotters()==null? 0 : getBlotters().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to lupons : " + s.getMessage());
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
		sql="SELECT lupid FROM lupons  ORDER BY lupid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("lupid");
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
		ps = conn.prepareStatement("SELECT lupid FROM lupons WHERE lupid=?");
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
		String sql = "UPDATE lupons set isactivelup=0 WHERE lupid=?";
		
		
		String[] params = new String[1];
		params[0] =getId()+"";
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
	public String getDateTrans() {
		return dateTrans;
	}
	public void setDateTrans(String dateTrans) {
		this.dateTrans = dateTrans;
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
	
	public Blotters getBlotters() {
		return blotters;
	}
	public void setBlotters(Blotters blotters) {
		this.blotters = blotters;
	}

	public Employee getLuponPerson() {
		return luponPerson;
	}

	public void setLuponPerson(Employee luponPerson) {
		this.luponPerson = luponPerson;
	}

	
	
}



