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

public class RacesTrans {

	private long id;
	private String dateTrans;
	private int isActive;
	private int isIndigentPerson;
	private Timestamp timestamp;
	
	private Races races;
	private Customer customer;
	private UserDtls userDtls;
	
	public static List<RacesTrans> retrieve(String sqlAdd, String[] params){
		List<RacesTrans> rcs = new ArrayList<RacesTrans>();
		
		String tableTran = "trn";
		String tableRc = "rc";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM racestrans "+ tableTran +", races " + tableRc  + ", customer " + tableCus + ", userdtls " + tableUser +" WHERE " + 
				tableTran + ".raceid=" + tableRc + ".raceid AND " +
				tableTran + ".customerid=" + tableCus + ".customerid AND " +
				tableTran + ".userdtlsid=" + tableUser + ".userdtlsid";
						
		
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
		
		//System.out.println("SQL Races : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			RacesTrans tran = new RacesTrans();
			try{tran.setId(rs.getLong("racetranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("racedatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiveracetrans"));}catch(NullPointerException e){}
			try{tran.setIsIndigentPerson(rs.getInt("isindigentrace"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampracetans"));}catch(NullPointerException e){}
			
			Races rc = new Races();
			try{rc.setId(rs.getInt("raceid"));}catch(NullPointerException e){}
			try{rc.setName(rs.getString("racename"));}catch(NullPointerException e){}
			try{rc.setIsActive(rs.getInt("isactiverace"));}catch(NullPointerException e){}
			try{rc.setIsIndigent(rs.getInt("isindigent"));}catch(NullPointerException e){}
			try{rc.setTimestamp(rs.getTimestamp("timestamprace"));}catch(NullPointerException e){}
			tran.setRaces(rc);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
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
			
			tran.setCustomer(cus);
			
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
			tran.setUserDtls(user);
			
			rcs.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return rcs;
	}
	
	public static RacesTrans retrieve(long id){
		RacesTrans tran = new RacesTrans();
		
		String tableTran = "trn";
		String tableRc = "rc";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM racestrans "+ tableTran +", races " + tableRc  + ", customer " + tableCus + ", userdtls " + tableUser +" WHERE " + 
				tableTran + ".raceid=" + tableRc + ".raceid AND " +
				tableTran + ".customerid=" + tableCus + ".customerid AND " +
				tableTran + ".userdtlsid=" + tableUser + ".userdtlsid AND " + tableTran + ".isactiveracetrans=1 AND " + tableTran + ".racetranid=" + id;
						
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{tran.setId(rs.getLong("racetranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("racedatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiveracetrans"));}catch(NullPointerException e){}
			try{tran.setIsIndigentPerson(rs.getInt("isindigentrace"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampracetans"));}catch(NullPointerException e){}
			
			Races rc = new Races();
			try{rc.setId(rs.getInt("raceid"));}catch(NullPointerException e){}
			try{rc.setName(rs.getString("racename"));}catch(NullPointerException e){}
			try{rc.setIsActive(rs.getInt("isactiverace"));}catch(NullPointerException e){}
			try{rc.setIsIndigent(rs.getInt("isindigent"));}catch(NullPointerException e){}
			try{rc.setTimestamp(rs.getTimestamp("timestamprace"));}catch(NullPointerException e){}
			tran.setRaces(rc);
			
			Customer cus = new Customer();
			try{cus.setCustomerid(rs.getLong("customerid"));}catch(NullPointerException e){}
			try{cus.setFirstname(rs.getString("cusfirstname"));}catch(NullPointerException e){}
			try{cus.setMiddlename(rs.getString("cusmiddlename"));}catch(NullPointerException e){}
			try{cus.setLastname(rs.getString("cuslastname"));}catch(NullPointerException e){}
			try{cus.setFullname(rs.getString("fullname"));}catch(NullPointerException e){}
			try{cus.setGender(rs.getString("cusgender"));}catch(NullPointerException e){}
			try{cus.setAge(rs.getInt("cusage"));}catch(NullPointerException e){}
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
			
			tran.setCustomer(cus);
			
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
			tran.setUserDtls(user);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return tran;
	}
	
	public static RacesTrans save(RacesTrans rc){
		if(rc!=null){
			
			long id = RacesTrans.getInfo(rc.getId() ==0? RacesTrans.getLatestId()+1 : rc.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				rc = RacesTrans.insertData(rc, "1");
			}else if(id==2){
				LogU.add("update Data ");
				rc = RacesTrans.updateData(rc);
			}else if(id==3){
				LogU.add("added new Data ");
				rc = RacesTrans.insertData(rc, "3");
			}
			
		}
		return rc;
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
	
	public static RacesTrans insertData(RacesTrans rc, String type){
		String sql = "INSERT INTO racestrans ("
				+ "racetranid,"
				+ "racedatetrans,"
				+ "isactiveracetrans,"
				+ "isindigentrace,"
				+ "raceid,"
				+ "customerid,"
				+ "userdtlsid) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table racestrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			rc.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			rc.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, rc.getDateTrans());
		ps.setInt(cnt++, rc.getIsActive());
		ps.setInt(cnt++, rc.getIsIndigentPerson());
		ps.setInt(cnt++, rc.getRaces().getId());
		ps.setLong(cnt++, rc.getCustomer().getCustomerid());
		ps.setLong(cnt++, rc.getUserDtls().getUserdtlsid());
		
		LogU.add(rc.getDateTrans());
		LogU.add(rc.getIsActive());
		LogU.add(rc.getIsIndigentPerson());
		LogU.add(rc.getRaces().getId());
		LogU.add(rc.getCustomer().getCustomerid());
		LogU.add(rc.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to racestrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO racestrans ("
				+ "racetranid,"
				+ "racedatetrans,"
				+ "isactiveracetrans,"
				+ "isindigentrace,"
				+ "raceid,"
				+ "customerid,"
				+ "userdtlsid) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table racestrans");
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
		ps.setInt(cnt++, getIsIndigentPerson());
		ps.setInt(cnt++, getRaces().getId());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getIsIndigentPerson());
		LogU.add(getRaces().getId());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to racestrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static RacesTrans updateData(RacesTrans rc){
		String sql = "UPDATE racestrans SET "
				+ "racedatetrans=?,"
				+ "isindigentrace=?,"
				+ "raceid=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE racetranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table racestrans");
		
		
		ps.setString(cnt++, rc.getDateTrans());
		ps.setInt(cnt++, rc.getIsIndigentPerson());
		ps.setInt(cnt++, rc.getRaces().getId());
		ps.setLong(cnt++, rc.getCustomer().getCustomerid());
		ps.setLong(cnt++, rc.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, rc.getId());
		
		LogU.add(rc.getDateTrans());
		LogU.add(rc.getIsIndigentPerson());
		LogU.add(rc.getRaces().getId());
		LogU.add(rc.getCustomer().getCustomerid());
		LogU.add(rc.getUserDtls().getUserdtlsid());
		LogU.add(rc.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to racestrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return rc;
	}
	
	public void updateData(){
		String sql = "UPDATE racestrans SET "
				+ "racedatetrans=?,"
				+ "isindigentrace=?,"
				+ "raceid=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE racetranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table racestrans");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getIsIndigentPerson());
		ps.setInt(cnt++, getRaces().getId());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getIsIndigentPerson());
		LogU.add(getRaces().getId());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to racestrans : " + s.getMessage());
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
		sql="SELECT racetranid FROM racestrans  ORDER BY racetranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("racetranid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
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
		ps = conn.prepareStatement("SELECT racetranid FROM racestrans WHERE racetranid=?");
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
		String sql = "UPDATE racestrans set isactiveracetrans=0 WHERE racetranid=?";
		
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
	public int getIsIndigentPerson() {
		return isIndigentPerson;
	}
	public void setIsIndigentPerson(int isIndigentPerson) {
		this.isIndigentPerson = isIndigentPerson;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Races getRaces() {
		return races;
	}
	public void setRaces(Races races) {
		this.races = races;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	
}
