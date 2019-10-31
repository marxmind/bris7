package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
public class Blotters {

	private long id;
	private String dateTrans;
	private String timeTrans;
	private String incidentDate;
	private String incidentTime;
	private String incidentPlace;
	private String incidentDetails;
	private String incidentSolutions;
	private int status;
	private int incidentType;
	private int isActive;
	private UserDtls userDtls;
	private Timestamp timestamp;
	
	private List<ReportingPerson> reportingPersons = Collections.synchronizedList(new ArrayList<ReportingPerson>());
	private List<SuspectedPerson> suspectedPersons = Collections.synchronizedList(new ArrayList<SuspectedPerson>());
	private List<MinorPerson> minorPersons = Collections.synchronizedList(new ArrayList<MinorPerson>());
	private List<VictimPerson> victimPersons = Collections.synchronizedList(new ArrayList<VictimPerson>());
	private List<LuponPerson> luponPersons = Collections.synchronizedList(new ArrayList<LuponPerson>());
	
	private String incidentName;
	private String statusName;
	
	public static List<Blotters> retrieve(String sqlAdd, String[] params){
		List<Blotters> bls = new ArrayList<Blotters>();
		String tableBlot = "blot";
		String tableUser = "usr";
		String sql = "SELECT * FROM blotters " + tableBlot + ", userdtls " + tableUser + " WHERE " +
		tableBlot + ".userdtlsid=" + tableUser + ".userdtlsid ";
		
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
			
			UserDtls user = new UserDtls();
			try{user.setUserdtlsid(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			blot.setUserDtls(user);
			
			bls.add(blot);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return bls;
	}
	
	/**
	 * 
	 * @param blotId
	 * @return incident details and solutions
	 */
	public static Blotters retrieveIncidentInformation(long blotId){
		Blotters blot = new Blotters();
		String sql = "SELECT indetails,insolutions FROM blotters WHERE blotid="+blotId;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			try{blot.setIncidentDetails(rs.getString("indetails"));}catch(NullPointerException e){}
			try{blot.setIncidentSolutions(rs.getString("insolutions"));}catch(NullPointerException e){}
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return blot;
	}
	
	public static Blotters save(Blotters bar){
		if(bar!=null){
			
			long id = Blotters.getInfo(bar.getId() ==0? Blotters.getLatestId()+1 : bar.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				bar = Blotters.insertData(bar, "1");
			}else if(id==2){
				LogU.add("update Data ");
				bar = Blotters.updateData(bar);
			}else if(id==3){
				LogU.add("added new Data ");
				bar = Blotters.insertData(bar, "3");
			}
			
		}
		return bar;
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
	
	public static Blotters insertData(Blotters blot, String type){
		String sql = "INSERT INTO blotters ("
				+ "blotid,"
				+ "blotdate,"
				+ "blottime,"
				+ "incidentDate,"
				+ "incidenttime,"
				+ "incidentplace,"
				+ "indetails,"
				+ "insolutions,"
				+ "blotstatus,"
				+ "incidenttype,"
				+ "isactiveblot,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table blotters");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			blot.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			blot.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, blot.getDateTrans());
		ps.setString(cnt++, blot.getTimeTrans());
		ps.setString(cnt++, blot.getIncidentDate());
		ps.setString(cnt++, blot.getIncidentTime());
		ps.setString(cnt++, blot.getIncidentPlace());
		ps.setString(cnt++, blot.getIncidentDetails());
		ps.setString(cnt++, blot.getIncidentSolutions());
		ps.setInt(cnt++, blot.getStatus());
		ps.setInt(cnt++, blot.getIncidentType());
		ps.setInt(cnt++, blot.getIsActive());
		ps.setLong(cnt++, blot.getUserDtls()==null? 0 : blot.getUserDtls().getUserdtlsid());
		
		
		LogU.add(blot.getDateTrans());
		LogU.add(blot.getTimeTrans());
		LogU.add(blot.getIncidentDate());
		LogU.add(blot.getIncidentTime());
		LogU.add(blot.getIncidentPlace());
		LogU.add(blot.getIncidentDetails());
		LogU.add(blot.getIncidentSolutions());
		LogU.add(blot.getStatus());
		LogU.add(blot.getIncidentType());
		LogU.add(blot.getIsActive());
		LogU.add(blot.getUserDtls()==null? 0 : blot.getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to blotters : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return blot;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO blotters ("
				+ "blotid,"
				+ "blotdate,"
				+ "blottime,"
				+ "incidentDate,"
				+ "incidenttime,"
				+ "incidentplace,"
				+ "indetails,"
				+ "insolutions,"
				+ "blotstatus,"
				+ "incidenttype,"
				+ "isactiveblot,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table blotters");
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
		ps.setString(cnt++, getTimeTrans());
		ps.setString(cnt++, getIncidentDate());
		ps.setString(cnt++, getIncidentTime());
		ps.setString(cnt++, getIncidentPlace());
		ps.setString(cnt++, getIncidentDetails());
		ps.setString(cnt++, getIncidentSolutions());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIncidentType());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		
		LogU.add(getDateTrans());
		LogU.add(getTimeTrans());
		LogU.add(getIncidentDate());
		LogU.add(getIncidentTime());
		LogU.add(getIncidentPlace());
		LogU.add(getIncidentDetails());
		LogU.add(getIncidentSolutions());
		LogU.add(getStatus());
		LogU.add(getIncidentType());
		LogU.add(getIsActive());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to blotters : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static Blotters updateData(Blotters blot){
		String sql = "UPDATE blotters SET "
				+ "blotdate=?,"
				+ "blottime=?,"
				+ "incidentDate=?,"
				+ "incidenttime=?,"
				+ "incidentplace=?,"
				+ "indetails=?,"
				+ "insolutions=?,"
				+ "blotstatus=?,"
				+ "incidenttype=?,"
				+ "userdtlsid=?" 
				+ " WHERE blotid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table blotters");
		
		
		ps.setString(cnt++, blot.getDateTrans());
		ps.setString(cnt++, blot.getTimeTrans());
		ps.setString(cnt++, blot.getIncidentDate());
		ps.setString(cnt++, blot.getIncidentTime());
		ps.setString(cnt++, blot.getIncidentPlace());
		ps.setString(cnt++, blot.getIncidentDetails());
		ps.setString(cnt++, blot.getIncidentSolutions());
		ps.setInt(cnt++, blot.getStatus());
		ps.setInt(cnt++, blot.getIncidentType());
		ps.setLong(cnt++, blot.getUserDtls()==null? 0 : blot.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, blot.getId());
		
		LogU.add(blot.getDateTrans());
		LogU.add(blot.getTimeTrans());
		LogU.add(blot.getIncidentDate());
		LogU.add(blot.getIncidentTime());
		LogU.add(blot.getIncidentPlace());
		LogU.add(blot.getIncidentDetails());
		LogU.add(blot.getIncidentSolutions());
		LogU.add(blot.getStatus());
		LogU.add(blot.getIncidentType());
		LogU.add(blot.getUserDtls()==null? 0 : blot.getUserDtls().getUserdtlsid());
		LogU.add(blot.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to blotters : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return blot;
	}
	
	public void updateData(){
		String sql = "UPDATE blotters SET "
				+ "blotdate=?,"
				+ "blottime=?,"
				+ "incidentDate=?,"
				+ "incidenttime=?,"
				+ "incidentplace=?,"
				+ "indetails=?,"
				+ "insolutions=?,"
				+ "blotstatus=?,"
				+ "incidenttype=?,"
				+ "userdtlsid=?" 
				+ " WHERE blotid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table blotters");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getTimeTrans());
		ps.setString(cnt++, getIncidentDate());
		ps.setString(cnt++, getIncidentTime());
		ps.setString(cnt++, getIncidentPlace());
		ps.setString(cnt++, getIncidentDetails());
		ps.setString(cnt++, getIncidentSolutions());
		ps.setInt(cnt++, getStatus());
		ps.setInt(cnt++, getIncidentType());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getTimeTrans());
		LogU.add(getIncidentDate());
		LogU.add(getIncidentTime());
		LogU.add(getIncidentPlace());
		LogU.add(getIncidentDetails());
		LogU.add(getIncidentSolutions());
		LogU.add(getStatus());
		LogU.add(getIncidentType());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to blotters : " + s.getMessage());
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
		sql="SELECT blotid FROM blotters  ORDER BY blotid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("blotid");
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
		ps = conn.prepareStatement("SELECT blotid FROM blotters WHERE blotid=?");
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
		String sql = "UPDATE blotters set isactiveblot=0 WHERE blotid=?";
		
		
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
	public String getTimeTrans() {
		return timeTrans;
	}
	public void setTimeTrans(String timeTrans) {
		this.timeTrans = timeTrans;
	}
	public String getIncidentDate() {
		return incidentDate;
	}
	public void setIncidentDate(String incidentDate) {
		this.incidentDate = incidentDate;
	}
	public String getIncidentTime() {
		return incidentTime;
	}
	public void setIncidentTime(String incidentTime) {
		this.incidentTime = incidentTime;
	}
	public String getIncidentPlace() {
		return incidentPlace;
	}
	public void setIncidentPlace(String incidentPlace) {
		this.incidentPlace = incidentPlace;
	}
	public String getIncidentDetails() {
		return incidentDetails;
	}
	public void setIncidentDetails(String incidentDetails) {
		this.incidentDetails = incidentDetails;
	}
	public String getIncidentSolutions() {
		return incidentSolutions;
	}
	public void setIncidentSolutions(String incidentSolutions) {
		this.incidentSolutions = incidentSolutions;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIncidentType() {
		return incidentType;
	}
	public void setIncidentType(int incidentType) {
		this.incidentType = incidentType;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public List<ReportingPerson> getReportingPersons() {
		return reportingPersons;
	}

	public void setReportingPersons(List<ReportingPerson> reportingPersons) {
		this.reportingPersons = reportingPersons;
	}

	public List<SuspectedPerson> getSuspectedPersons() {
		return suspectedPersons;
	}

	public void setSuspectedPersons(List<SuspectedPerson> suspectedPersons) {
		this.suspectedPersons = suspectedPersons;
	}

	public List<MinorPerson> getMinorPersons() {
		return minorPersons;
	}

	public void setMinorPersons(List<MinorPerson> minorPersons) {
		this.minorPersons = minorPersons;
	}

	public List<VictimPerson> getVictimPersons() {
		return victimPersons;
	}

	public void setVictimPersons(List<VictimPerson> victimPersons) {
		this.victimPersons = victimPersons;
	}

	public List<LuponPerson> getLuponPersons() {
		return luponPersons;
	}

	public void setLuponPersons(List<LuponPerson> luponPersons) {
		this.luponPersons = luponPersons;
	}

	public String getIncidentName() {
		return incidentName;
	}

	public void setIncidentName(String incidentName) {
		this.incidentName = incidentName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
	
}
