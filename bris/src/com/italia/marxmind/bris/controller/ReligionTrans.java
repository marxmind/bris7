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

public class ReligionTrans {

	private long id;
	private String dateTrans;
	private int isActive;
	private int religionId;
	private int isPresentReligion;
	private Timestamp timestamp;
	
	private Customer customer;
	private UserDtls userDtls;
	
	public static List<ReligionTrans> retrieve(String sqlAdd, String[] params){
		List<ReligionTrans> trans = new ArrayList<ReligionTrans>();
		
		String tableTran = "trn";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM religiontrans "+ tableTran +", customer " + tableCus + ", userdtls " + tableUser +" WHERE " +
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
		
		//System.out.println("SQL Religions : " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			ReligionTrans tran = new ReligionTrans();
			try{tran.setId(rs.getLong("reltranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("reldatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiverel"));}catch(NullPointerException e){}
			try{tran.setReligionId(rs.getInt("relid"));}catch(NullPointerException e){}
			try{tran.setIsPresentReligion(rs.getInt("ispresentrel"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampreltran"));}catch(NullPointerException e){}
			
			
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
			
			trans.add(tran);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	public static ReligionTrans retrieve(long id){
		ReligionTrans tran = new ReligionTrans();
		
		String tableTran = "trn";
		String tableCus = "cz";
		String tableUser = "usr";
		
		
		String sql = "SELECT * FROM religiontrans "+ tableTran +", customer " + tableCus + ", userdtls " + tableUser +" WHERE " +
				tableTran + ".customerid=" + tableCus + ".customerid AND " +
				tableTran + ".userdtlsid=" + tableUser + ".userdtlsid AND " + tableTran + ".isactiverel=1 AND " + tableTran + ".reltranid=" + id;
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{tran.setId(rs.getLong("reltranid"));}catch(NullPointerException e){}
			try{tran.setDateTrans(rs.getString("reldatetrans"));}catch(NullPointerException e){}
			try{tran.setIsActive(rs.getInt("isactiverel"));}catch(NullPointerException e){}
			try{tran.setReligionId(rs.getInt("relid"));}catch(NullPointerException e){}
			try{tran.setIsPresentReligion(rs.getInt("ispresentrel"));}catch(NullPointerException e){}
			try{tran.setTimestamp(rs.getTimestamp("timestampreltran"));}catch(NullPointerException e){}
			
			
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
	
	public static ReligionTrans save(ReligionTrans trn){
		if(trn!=null){
			
			long id = ReligionTrans.getInfo(trn.getId() ==0? ReligionTrans.getLatestId()+1 : trn.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				trn = ReligionTrans.insertData(trn, "1");
			}else if(id==2){
				LogU.add("update Data ");
				trn = ReligionTrans.updateData(trn);
			}else if(id==3){
				LogU.add("added new Data ");
				trn = ReligionTrans.insertData(trn, "3");
			}
			
		}
		return trn;
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
	
	public static ReligionTrans insertData(ReligionTrans tran, String type){
		String sql = "INSERT INTO religiontrans ("
				+ "reltranid,"
				+ "reldatetrans,"
				+ "isactiverel,"
				+ "relid,"
				+ "ispresentrel,"
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
		LogU.add("inserting data into table religiontrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			tran.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			tran.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, tran.getDateTrans());
		ps.setInt(cnt++, tran.getIsActive());
		ps.setInt(cnt++, tran.getReligionId());
		ps.setInt(cnt++, tran.getIsPresentReligion());
		ps.setLong(cnt++, tran.getCustomer().getCustomerid());
		ps.setLong(cnt++, tran.getUserDtls().getUserdtlsid());
		
		LogU.add(tran.getDateTrans());
		LogU.add(tran.getIsActive());
		LogU.add(tran.getReligionId());
		LogU.add(tran.getIsPresentReligion());
		LogU.add(tran.getCustomer().getCustomerid());
		LogU.add(tran.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to religiontrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return tran;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO religiontrans ("
				+ "reltranid,"
				+ "reldatetrans,"
				+ "isactiverel,"
				+ "relid,"
				+ "ispresentrel,"
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
		LogU.add("inserting data into table religiontrans");
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
		ps.setInt(cnt++, getReligionId());
		ps.setInt(cnt++, getIsPresentReligion());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getIsActive());
		LogU.add(getReligionId());
		LogU.add(getIsPresentReligion());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to religiontrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static ReligionTrans updateData(ReligionTrans tran){
		String sql = "UPDATE religiontrans SET "
				+ "reldatetrans=?,"
				+ "relid=?,"
				+ "ispresentrel=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE reltranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table religiontrans");
		
		ps.setString(cnt++, tran.getDateTrans());
		ps.setInt(cnt++, tran.getReligionId());
		ps.setInt(cnt++, tran.getIsPresentReligion());
		ps.setLong(cnt++, tran.getCustomer().getCustomerid());
		ps.setLong(cnt++, tran.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, tran.getId());
		
		LogU.add(tran.getDateTrans());
		LogU.add(tran.getReligionId());
		LogU.add(tran.getIsPresentReligion());
		LogU.add(tran.getCustomer().getCustomerid());
		LogU.add(tran.getUserDtls().getUserdtlsid());
		LogU.add(tran.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to religiontrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return tran;
	}
	
	public void updateData(){
		String sql = "UPDATE religiontrans SET "
				+ "reldatetrans=?,"
				+ "relid=?,"
				+ "ispresentrel=?,"
				+ "customerid=?,"
				+ "userdtlsid=?" 
				+ " WHERE reltranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table religiontrans");
		
		ps.setString(cnt++, getDateTrans());
		ps.setInt(cnt++, getReligionId());
		ps.setInt(cnt++, getIsPresentReligion());
		ps.setLong(cnt++, getCustomer().getCustomerid());
		ps.setLong(cnt++, getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getReligionId());
		LogU.add(getIsPresentReligion());
		LogU.add(getCustomer().getCustomerid());
		LogU.add(getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to religiontrans : " + s.getMessage());
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
		sql="SELECT reltranid FROM religiontrans  ORDER BY reltranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("reltranid");
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
		ps = conn.prepareStatement("SELECT reltranid FROM religiontrans WHERE reltranid=?");
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
		String sql = "UPDATE religiontrans set isactiverel=0 WHERE reltranid=?";
		
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
	public int getReligionId() {
		return religionId;
	}
	public void setReligionId(int religionId) {
		this.religionId = religionId;
	}
	public int getIsPresentReligion() {
		return isPresentReligion;
	}
	public void setIsPresentReligion(int isPresentReligion) {
		this.isPresentReligion = isPresentReligion;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
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
