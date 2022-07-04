package com.italia.marxmind.bris.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.bris.database.ConnectDB;
import com.italia.marxmind.bris.enm.ClearanceType;
import com.italia.marxmind.bris.enm.Purpose;
import com.italia.marxmind.bris.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 07/12/2017
 * @version 1.0
 *
 */

public class BCard {

	private long id;
	private String dateTrans;
	private int isActive;
	private String validDateFrom;
	private String validDateTo;
	private Customer taxpayer;
	private UserDtls userDtls;
	
	public static List<BCard> retrieve(String sqlAdd, String[] params){
		List<BCard> cards = new ArrayList<>();
		
		String tableCard = "crd";
		String tableCus = "cuz";
		String tableUser = "usr";
		
		String sql = "SELECT * FROM bidtrans " + tableCard + ", customer " + tableCus + ", userdtls " + tableUser + " WHERE " +
				tableCard + ".customerid=" + tableCus + ".customerid AND " +
				tableCard + ".userdtlsid=" + tableUser + ".userdtlsid ";
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
		
		System.out.println("BCard: " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			BCard crd = new BCard();
			crd.setId(rs.getLong("tranid"));
			crd.setDateTrans(rs.getString("datetrans"));
			crd.setValidDateFrom(rs.getString("validfrom"));
			crd.setValidDateTo(rs.getString("validto"));
			crd.setIsActive(rs.getInt("isactivebid"));
			
			
			
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
			try{cus.setBirthdate(rs.getString("borndate"));}catch(NullPointerException e){}
			try{Customer emergency = new Customer();
			emergency.setCustomerid(rs.getLong("emeperson"));
			cus.setEmergencyContactPerson(emergency);}catch(NullPointerException e){}
			try{cus.setRelationship(rs.getInt("relid"));}catch(NullPointerException e){}
			
			crd.setTaxpayer(cus);
			
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
			crd.setUserDtls(user);
			
			
			
			cards.add(crd);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return cards;
	}
	
	
	public static BCard save(BCard crd){
		if(crd!=null){
			
			long id = BCard.getInfo(crd.getId() ==0? BCard.getLatestId()+1 : crd.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				crd = BCard.insertData(crd, "1");
			}else if(id==2){
				LogU.add("update Data ");
				crd = BCard.updateData(crd);
			}else if(id==3){
				LogU.add("added new Data ");
				crd = BCard.insertData(crd, "3");
			}
			
		}
		return crd;
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
	
	public static BCard insertData(BCard crd, String type){
		String sql = "INSERT INTO bidtrans ("
				+ "tranid,"
				+ "datetrans,"
				+ "validfrom,"
				+ "validto,"
				+ "isactivebid,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bidtrans");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			crd.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			crd.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, crd.getDateTrans());
		ps.setString(cnt++, crd.getValidDateFrom());
		ps.setString(cnt++, crd.getValidDateTo());
		ps.setInt(cnt++, crd.getIsActive());
		ps.setLong(cnt++, crd.getTaxpayer()==null? 0 : crd.getTaxpayer().getCustomerid());
		ps.setLong(cnt++, crd.getUserDtls()==null? 0 : crd.getUserDtls().getUserdtlsid());
		
		LogU.add(crd.getDateTrans());
		LogU.add(crd.getValidDateFrom());
		LogU.add(crd.getValidDateTo());
		LogU.add(crd.getIsActive());
		LogU.add(crd.getTaxpayer()==null? 0 : crd.getTaxpayer().getCustomerid());
		LogU.add(crd.getUserDtls()==null? 0 : crd.getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bidtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return crd;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO bidtrans ("
				+ "tranid,"
				+ "datetrans,"
				+ "validfrom,"
				+ "validto,"
				+ "isactivebid,"
				+ "customerid,"
				+ "userdtlsid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table bidtrans");
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
		ps.setString(cnt++, getValidDateFrom());
		ps.setString(cnt++, getValidDateTo());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getTaxpayer()==null? 0 : getTaxpayer().getCustomerid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add(getDateTrans());
		LogU.add(getValidDateFrom());
		LogU.add(getValidDateTo());
		LogU.add(getIsActive());
		LogU.add(getTaxpayer()==null? 0 : getTaxpayer().getCustomerid());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to bidtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		
	}
	
	public static BCard updateData(BCard crd){
		String sql = "UPDATE bidtrans SET "
				+ "datetrans=?,"
				+ "validfrom=?,"
				+ "validto=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE tranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bidtrans");
		
		
		ps.setString(cnt++, crd.getDateTrans());
		ps.setString(cnt++, crd.getValidDateFrom());
		ps.setString(cnt++, crd.getValidDateTo());
		ps.setLong(cnt++, crd.getTaxpayer()==null? 0 : crd.getTaxpayer().getCustomerid());
		ps.setLong(cnt++, crd.getUserDtls()==null? 0 : crd.getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, crd.getId());
		
		LogU.add(crd.getDateTrans());
		LogU.add(crd.getValidDateFrom());
		LogU.add(crd.getValidDateTo());
		LogU.add(crd.getIsActive());
		LogU.add(crd.getTaxpayer()==null? 0 : crd.getTaxpayer().getCustomerid());
		LogU.add(crd.getUserDtls()==null? 0 : crd.getUserDtls().getUserdtlsid());
		LogU.add(crd.getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bidtrans : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return crd;
	}
	
	public void updateData(){
		String sql = "UPDATE bidtrans SET "
				+ "datetrans=?,"
				+ "validfrom=?,"
				+ "validto=?,"
				+ "customerid=?,"
				+ "userdtlsid=? " 
				+ " WHERE tranid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table bidtrans");
		
		
		ps.setString(cnt++, getDateTrans());
		ps.setString(cnt++, getValidDateFrom());
		ps.setString(cnt++, getValidDateTo());
		ps.setLong(cnt++, getTaxpayer()==null? 0 : getTaxpayer().getCustomerid());
		ps.setLong(cnt++, getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateTrans());
		LogU.add(getValidDateFrom());
		LogU.add(getValidDateTo());
		LogU.add(getIsActive());
		LogU.add(getTaxpayer()==null? 0 : getTaxpayer().getCustomerid());
		LogU.add(getUserDtls()==null? 0 : getUserDtls().getUserdtlsid());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.executeUpdate();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to bidtrans : " + s.getMessage());
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
		sql="SELECT tranid FROM bidtrans  ORDER BY tranid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tranid");
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
		ps = conn.prepareStatement("SELECT tranid FROM bidtrans WHERE tranid=?");
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
		String sql = "UPDATE bidtrans set isactivebid=0,userdtlsid="+ getUserDtls().getUserdtlsid() +" WHERE tranid=?";
		
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
	public Customer getTaxpayer() {
		return taxpayer;
	}
	public void setTaxpayer(Customer taxpayer) {
		this.taxpayer = taxpayer;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}


	public String getValidDateFrom() {
		return validDateFrom;
	}


	public void setValidDateFrom(String validDateFrom) {
		this.validDateFrom = validDateFrom;
	}


	public String getValidDateTo() {
		return validDateTo;
	}


	public void setValidDateTo(String validDateTo) {
		this.validDateTo = validDateTo;
	}
	
}
