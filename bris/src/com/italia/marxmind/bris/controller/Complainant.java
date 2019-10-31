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
 * @author mark italia
 * @since 03/07/2018
 * @version 1.0
 *
 */

public class Complainant {

	private long id;
	private String date;
	private int status;
	private int isActive;
	
	private Cases cases;
	
	public static List<Complainant> retrieve(String sqlAdd, String[] params){
		List<Complainant> coms = new ArrayList<>();
		
		String tableCom = "kom";
		String tableCase = "ciz";
		
		String sql = "SELECT * FROM complainant " + tableCom + ", cases " + tableCase + " WHERE " +
				tableCom + ".caseid=" + tableCase + ".caseid ";
		
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL " + ps.toString());
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Complainant com = new Complainant();
			try{com.setId(rs.getLong("comid"));}catch(NullPointerException e){}
			try{com.setDate(rs.getString("comdate"));}catch(NullPointerException e){}
			try{com.setIsActive(rs.getInt("comisactive"));}catch(NullPointerException e){}
			
			Cases caz = new Cases();
			try{caz.setId(rs.getLong("caseid"));}catch(NullPointerException e){}
			try{caz.setDate(rs.getString("casedate"));}catch(NullPointerException e){}
			try{caz.setCaseNo(rs.getString("caseno"));}catch(NullPointerException e){}
			try{caz.setNarratives(rs.getString("filcomplaints"));}catch(NullPointerException e){}
			try{caz.setStatus(rs.getInt("casestatus"));}catch(NullPointerException e){}
			try{caz.setType(rs.getInt("casetype"));}catch(NullPointerException e){}
			try{caz.setKind(rs.getInt("casekind"));}catch(NullPointerException e){}
			try{caz.setIsActive(rs.getInt("caseisactive"));}catch(NullPointerException e){}
			try{caz.setComplainants(rs.getString("complainants"));}catch(NullPointerException e){}
			try{caz.setComAddress(rs.getString("comaddress"));}catch(NullPointerException e){}
			try{caz.setRespondents(rs.getString("respondents"));}catch(NullPointerException e){}
			try{caz.setResAddress(rs.getString("resaddress"));}catch(NullPointerException e){}
			com.setCases(caz);
			
			coms.add(com);
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return coms;
	}
	
	public static Complainant save(Complainant cz){
		if(cz!=null){
			
			long id = Complainant.getInfo(cz.getId() ==0? Complainant.getLatestId()+1 : cz.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				cz = Complainant.insertData(cz, "1");
			}else if(id==2){
				LogU.add("update Data ");
				cz = Complainant.updateData(cz);
			}else if(id==3){
				LogU.add("added new Data ");
				cz = Complainant.insertData(cz, "3");
			}
			
		}
		return cz;
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
	
	public static Complainant insertData(Complainant cz, String type){
		String sql = "INSERT INTO complainant ("
				+ "comid,"
				+ "comdate,"
				+ "caseid,"
				+ "comisactive)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table complainant");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			cz.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			cz.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, cz.getDate());
		ps.setLong(cnt++, cz.getCases()==null? 0 : cz.getCases().getId());
		ps.setInt(cnt++, cz.getIsActive());
		
		LogU.add(cz.getDate());
		LogU.add(cz.getCases()==null? 0 : cz.getCases().getId());
		LogU.add(cz.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to complainant : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO complainant ("
				+ "comid,"
				+ "comdate,"
				+ "caseid,"
				+ "comisactive)" 
				+ "values(?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table complainant");
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
		
		ps.setString(cnt++, getDate());
		ps.setLong(cnt++, getCases()==null? 0 : getCases().getId());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getDate());
		LogU.add(getCases()==null? 0 : getCases().getId());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to complainant : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
	}
	
	public static Complainant updateData(Complainant cz){
		String sql = "UPDATE complainant SET "
				+ "comdate=?,"
				+ "caseid=?" 
				+ " WHERE comid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table complainant");
		
		ps.setString(cnt++, cz.getDate());
		ps.setLong(cnt++, cz.getCases()==null? 0 : cz.getCases().getId());
		ps.setLong(cnt++, cz.getId());
		
		LogU.add(cz.getDate());
		LogU.add(cz.getCases()==null? 0 : cz.getCases().getId());
		LogU.add(cz.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to complainant : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return cz;
	}
	
	public void updateData(){
		String sql = "UPDATE complainant SET "
				+ "comdate=?,"
				+ "caseid=?" 
				+ " WHERE comid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table complainant");
		
		ps.setString(cnt++, getDate());
		ps.setLong(cnt++, getCases()==null? 0 : getCases().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDate());
		LogU.add(getCases()==null? 0 : getCases().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to complainant : " + s.getMessage());
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
		sql="SELECT comid FROM complainant  ORDER BY comid DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT comid FROM complainant WHERE comid=?");
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
		String sql = "UPDATE complainant set comisactive=0 WHERE comid=?";
		
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public Cases getCases() {
		return cases;
	}

	public void setCases(Cases cases) {
		this.cases = cases;
	}
	
}
