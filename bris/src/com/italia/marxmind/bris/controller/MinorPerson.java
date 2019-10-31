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
public class MinorPerson {

	private long id;
	private String dateTrans;
	private int isActive;
	private Timestamp timestamp;
	private Customer minorPerson;
	private Customer guardianPerson;
	private Blotters blotters;
	
	public static List<MinorPerson> retrieve(String sqlAdd, String[] params){
		List<MinorPerson> reps = new ArrayList<MinorPerson>();
		
		String tableReps = "mino";
		String tableCit = "ctz";
		String tableBot = "blot";
		
		String sql = "SELECT * FROM minoreds " + tableReps + ", customer " + tableCit + ", blotters " + tableBot + " WHERE " +
				tableReps + ".minoredid="+ tableCit + ".customerid AND " +
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
			
			MinorPerson per = new MinorPerson();
			try{per.setId(rs.getLong("minid"));}catch(NullPointerException e){}
			try{per.setDateTrans(rs.getString("mindate"));}catch(NullPointerException e){}
			try{per.setIsActive(rs.getInt("isactivemin"));}catch(NullPointerException e){}
			try{per.setTimestamp(rs.getTimestamp("timestampmin"));}catch(NullPointerException e){}
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
			//try{cus.setAddress(rs.getString("cusaddress"));}catch(NullPointerException e){}
			try{cus.setContactno(rs.getString("cuscontactno"));}catch(NullPointerException e){}
			try{cus.setDateregistered(rs.getString("cusdateregistered"));}catch(NullPointerException e){}
			try{cus.setCardno(rs.getString("cuscardno"));}catch(NullPointerException e){}
			try{cus.setIsactive(rs.getInt("cusisactive"));}catch(NullPointerException e){}
			try{cus.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{cus.setCivilStatus(rs.getInt("civilstatus"));}catch(NullPointerException e){}
			try{cus.setPhotoid(rs.getString("photoid"));}catch(NullPointerException e){}
			
			if("1".equalsIgnoreCase(cus.getGender())){
				cus.setGenderName("Male");
			}else{
				cus.setGenderName("Female");
			}
			
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			Purok pur = new Purok();
			try{pur.setId(rs.getLong("purid"));}catch(NullPointerException e){}
			cus.setPurok(pur);
			
			Barangay bar = new Barangay();
			try{bar.setId(rs.getInt("bgid"));}catch(NullPointerException e){}
			cus.setBarangay(bar);
			
			Municipality mun = new Municipality();
			try{mun.setId(rs.getInt("munid"));}catch(NullPointerException e){}
			cus.setMunicipality(mun);
			
			Province prov = new Province();
			try{prov.setId(rs.getInt("provid"));}catch(NullPointerException e){}
			cus.setProvince(prov);
			
			per.setMinorPerson(cus);
			
			try{Customer czx = new Customer();
			czx = Customer.customer(rs.getLong("guardianid"));
			per.setGuardianPerson(czx);}catch(NullPointerException e){}
			
			Blotters blot = new Blotters();
			try{blot.setId(rs.getLong("blotid"));}catch(NullPointerException e){}
			try{blot.setDateTrans(rs.getString("blotdate"));}catch(NullPointerException e){}
			try{blot.setTimeTrans(rs.getString("blottime"));}catch(NullPointerException e){}
			try{blot.setIncidentDate(rs.getString("incidentDate"));}catch(NullPointerException e){}
			try{blot.setIncidentTime(rs.getString("incidenttime"));}catch(NullPointerException e){}
			try{blot.setIncidentPlace(rs.getString("incidentplace"));}catch(NullPointerException e){}
			try{blot.setIncidentDetails(rs.getString("indetails"));}catch(NullPointerException e){}
			try{blot.setIncidentSolutions(rs.getString("insolutions"));}catch(NullPointerException e){}
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
	
	public static MinorPerson save(MinorPerson rep){
		if(rep!=null){
			
			long id = MinorPerson.getInfo(rep.getId() ==0? MinorPerson.getLatestId()+1 : rep.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rep = MinorPerson.insertData(rep, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rep = MinorPerson.updateData(rep);
			}else if(id==3){
				LogU.add("added new Data ");
				rep = MinorPerson.insertData(rep, "3");
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
	
	public static MinorPerson insertData(MinorPerson rep, String type){
		String sql = "INSERT INTO minoreds ("
				+ "minid,"
				+ "mindate,"
				+ "isactivemin,"
				+ "minoredid,"
				+ "guardianid,"
				+ "blotid)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table minoreds");
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
		ps.setLong(cnt++, rep.getMinorPerson()==null? 0 : rep.getMinorPerson().getCustomerid());
		ps.setLong(cnt++, rep.getGuardianPerson()==null? 0 : rep.getGuardianPerson().getCustomerid());
		ps.setLong(cnt++, rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		
		
		LogU.add(rep.getDateTrans());
		LogU.add(rep.getIsActive());
		LogU.add(rep.getMinorPerson()==null? 0 : rep.getMinorPerson().getCustomerid());
		LogU.add(rep.getGuardianPerson()==null? 0 : rep.getGuardianPerson().getCustomerid());
		LogU.add(rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to minoreds : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO minoreds ("
				+ "minid,"
				+ "mindate,"
				+ "isactivemin,"
				+ "minoredid,"
				+ "guardianid,"
				+ "blotid)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table minoreds");
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
		ps.setLong(cnt++, getMinorPerson()==null? 0 : getMinorPerson().getCustomerid());
		ps.setLong(cnt++, getGuardianPerson()==null? 0 : getGuardianPerson().getCustomerid());
		ps.setLong(cnt++, getBlotters()==null? 0 : getBlotters().getId());
		
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getMinorPerson()==null? 0 : getMinorPerson().getCustomerid());
		LogU.add(getGuardianPerson()==null? 0 : getGuardianPerson().getCustomerid());
		LogU.add(getBlotters()==null? 0 : getBlotters().getId());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to minoreds : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static MinorPerson updateData(MinorPerson rep){
		String sql = "UPDATE minoreds SET "
				+ "mindate=?,"
				+ "minoredid=?,"
				+ "guardianid=?,"
				+ "blotid=?" 
				+ " WHERE minid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table minoreds");
		
		
		ps.setString(cnt++, rep.getDateTrans());
		ps.setLong(cnt++, rep.getMinorPerson()==null? 0 : rep.getMinorPerson().getCustomerid());
		ps.setLong(cnt++, rep.getGuardianPerson()==null? 0 : rep.getGuardianPerson().getCustomerid());
		ps.setLong(cnt++, rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		ps.setLong(cnt++, rep.getId());
		
		LogU.add(rep.getDateTrans());
		LogU.add(rep.getMinorPerson()==null? 0 : rep.getMinorPerson().getCustomerid());
		LogU.add(rep.getGuardianPerson()==null? 0 : rep.getGuardianPerson().getCustomerid());
		LogU.add(rep.getBlotters()==null? 0 : rep.getBlotters().getId());
		LogU.add(rep.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to minoreds : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rep;
	}
	
	public void updateData(){
		String sql = "UPDATE minoreds SET "
				+ "mindate=?,"
				+ "minoredid=?,"
				+ "guardianid=?,"
				+ "blotid=?" 
				+ " WHERE minid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table minoreds");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setLong(cnt++, getMinorPerson()==null? 0 : getMinorPerson().getCustomerid());
		ps.setLong(cnt++, getGuardianPerson()==null? 0 : getGuardianPerson().getCustomerid());
		ps.setLong(cnt++, getBlotters()==null? 0 : getBlotters().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getMinorPerson()==null? 0 : getMinorPerson().getCustomerid());
		LogU.add(getGuardianPerson()==null? 0 : getGuardianPerson().getCustomerid());
		LogU.add(getBlotters()==null? 0 : getBlotters().getId());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to minoreds : " + s.getMessage());
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
		sql="SELECT minid FROM minoreds  ORDER BY minid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("minid");
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
		ps = conn.prepareStatement("SELECT minid FROM minoreds WHERE minid=?");
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
		String sql = "UPDATE minoreds set isactivemin=0 WHERE minid=?";
		
		
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

	public Customer getMinorPerson() {
		return minorPerson;
	}

	public void setMinorPerson(Customer minorPerson) {
		this.minorPerson = minorPerson;
	}

	public Customer getGuardianPerson() {
		return guardianPerson;
	}

	public void setGuardianPerson(Customer guardianPerson) {
		this.guardianPerson = guardianPerson;
	}
	
}


